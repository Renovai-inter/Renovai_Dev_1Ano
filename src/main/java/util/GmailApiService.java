package util;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class GmailApiService {
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)).build();
    private GmailApiService() {}

    static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank())
            throw new IllegalStateException("Variável ausente: " + key);
        return value.trim();
    }

    static String form(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    // Extract only ASCII OAuth token fields, never log response bodies.
    static String jsonString(String json, String key) throws IOException {
        Pattern pattern = Pattern.compile("\\\"" + Pattern.quote(key) + "\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) throw new IOException("Campo OAuth ausente: " + key);
        return matcher.group(1);
    }

    static String postForm(String url, String body) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(25))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200)
            throw new IOException("OAuth HTTP " + response.statusCode() + " (verifique credenciais, consentimento e token)");
        return response.body();
    }

    static String accessToken() throws IOException, InterruptedException {
        String body = "client_id=" + form(required("GMAIL_CLIENT_ID"))
                + "&client_secret=" + form(required("GMAIL_CLIENT_SECRET"))
                + "&refresh_token=" + form(required("GMAIL_REFRESH_TOKEN"))
                + "&grant_type=refresh_token";
        return jsonString(postForm("https://oauth2.googleapis.com/token", body), "access_token");
    }

    public static void send(String recipient, String name, String resetLink) throws MessagingException {
        try {
            String sender = required("GMAIL_FROM");
            MimeMessage mime = new MimeMessage(Session.getInstance(new Properties()));
            mime.setFrom(new InternetAddress(sender));
            mime.setRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(recipient));
            mime.setSubject("Redefinição de senha | Renovaí", "UTF-8");
            mime.setText("Olá, " + name + "!\n\nRecebemos um pedido para redefinir sua senha do Renovaí.\n"
                    + "O link é válido por 30 minutos:\n" + resetLink
                    + "\n\nSe você não solicitou a alteração, ignore esta mensagem.\n", "UTF-8");
            mime.saveChanges();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            mime.writeTo(output);
            String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(output.toByteArray());
            String token = accessToken();
            HttpRequest request = HttpRequest.newBuilder(URI.create("https://gmail.googleapis.com/gmail/v1/users/me/messages/send"))
                    .timeout(Duration.ofSeconds(25))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"raw\":\"" + raw + "\"}"))
                    .build();
            HttpResponse<String> response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200)
                throw new IOException("Gmail API HTTP " + response.statusCode() + " (verifique Gmail, escopo e remetente)");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MessagingException("Envio interrompido", e);
        } catch (IOException | IllegalStateException e) {
            throw new MessagingException("Falha ao enviar via Gmail API: " + e.getMessage(), e);
        }
    }
}
