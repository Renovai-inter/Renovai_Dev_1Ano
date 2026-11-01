
package util;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public final class GoogleOAuthService {

    private static final String REDIRECT_URI =
            "https://renovai-backend-athb.onrender.com/oauth/google/callback";

    private static final HttpClient HTTP = HttpClient.newHttpClient();

    private GoogleOAuthService() {}

    public static String gerarUrlAutorizacao(String state) {
        String clientId = configuracao("GMAIL_CLIENT_ID");

        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + codificar(clientId)
                + "&redirect_uri=" + codificar(REDIRECT_URI)
                + "&response_type=code"
                + "&scope=" + codificar(
                "https://www.googleapis.com/auth/gmail.send")
                + "&access_type=offline"
                + "&prompt=consent"
                + "&state=" + codificar(state);
    }

    public static String trocarCodigoPorTokens(String code)
            throws IOException, InterruptedException {

        String corpo = "code=" + codificar(code)
                + "&client_id=" + codificar(configuracao("GMAIL_CLIENT_ID"))
                + "&client_secret=" + codificar(configuracao("GMAIL_CLIENT_SECRET"))
                + "&redirect_uri=" + codificar(REDIRECT_URI)
                + "&grant_type=authorization_code";

        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth2.googleapis.com/token"))
                .header("Content-Type",
                        "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build();

        HttpResponse<String> resposta = HTTP.send(
                requisicao,
                HttpResponse.BodyHandlers.ofString()
        );

        if (resposta.statusCode() != 200) {
            throw new IOException(
                    "Falha na autorização OAuth. HTTP "
                            + resposta.statusCode()
            );
        }

        return resposta.body();
    }

    private static String configuracao(String nome) {
        String valor = System.getenv(nome);

        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Variável não configurada: " + nome
            );
        }

        return valor;
    }

    private static String codificar(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}
