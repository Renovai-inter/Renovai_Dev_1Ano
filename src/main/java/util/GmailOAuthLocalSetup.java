package util;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/** Run locally only, with a Google OAuth Desktop client. Never deploy or expose as a servlet. */
public final class GmailOAuthLocalSetup {
    private static final SecureRandom RANDOM = new SecureRandom();
    private GmailOAuthLocalSetup() {}

    private static String random() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static Map<String, String> query(String raw) {
        Map<String, String> result = new HashMap<>();
        if (raw == null) return result;
        for (String part : raw.split("&")) {
            String[] kv = part.split("=", 2);
            result.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                    kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "");
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String clientId = GmailApiService.required("GMAIL_CLIENT_ID");
        String clientSecret = GmailApiService.required("GMAIL_CLIENT_SECRET");
        String state = random();
        String verifier = random();
        String challenge = Base64.getUrlEncoder().withoutPadding().encodeToString(
                MessageDigest.getInstance("SHA-256").digest(verifier.getBytes(StandardCharsets.US_ASCII)));
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> code = new AtomicReference<>();
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
        int port = server.getAddress().getPort();
        String redirect = "http://127.0.0.1:" + port + "/callback";
        server.createContext("/callback", exchange -> {
            Map<String, String> params = query(exchange.getRequestURI().getRawQuery());
            boolean valid = "GET".equals(exchange.getRequestMethod()) && state.equals(params.get("state"));
            if (valid && params.containsKey("code")) code.set(params.get("code"));
            byte[] message = (valid && code.get() != null
                    ? "Autorização recebida. Volte ao terminal local."
                    : "Autorização inválida ou recusada.").getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(valid && code.get() != null ? 200 : 400, message.length);
            try (var out = exchange.getResponseBody()) { out.write(message); }
            if (valid && code.get() != null) latch.countDown();
        });
        server.start();
        try {
            String authorization = "https://accounts.google.com/o/oauth2/v2/auth?client_id="
                    + GmailApiService.form(clientId)
                    + "&redirect_uri=" + GmailApiService.form(redirect)
                    + "&response_type=code&scope="
                    + GmailApiService.form("https://www.googleapis.com/auth/gmail.send")
                    + "&access_type=offline&prompt=consent&state=" + GmailApiService.form(state)
                    + "&code_challenge=" + GmailApiService.form(challenge)
                    + "&code_challenge_method=S256";
            System.out.println("Abra no navegador, entre na conta Gmail REMETENTE e autorize:\n" + authorization);
            if (!latch.await(180, TimeUnit.SECONDS)) throw new IOException("Autorização expirou após 3 minutos.");
            String body = "code=" + GmailApiService.form(code.get())
                    + "&client_id=" + GmailApiService.form(clientId)
                    + "&client_secret=" + GmailApiService.form(clientSecret)
                    + "&redirect_uri=" + GmailApiService.form(redirect)
                    + "&code_verifier=" + GmailApiService.form(verifier)
                    + "&grant_type=authorization_code";
            String json = GmailApiService.postForm("https://oauth2.googleapis.com/token", body);
            System.out.println("\nGMAIL_REFRESH_TOKEN (COPIE APENAS PARA O RENDER, NÃO COMPARTILHE):\n"
                    + GmailApiService.jsonString(json, "refresh_token"));
        } finally {
            server.stop(0);
        }
    }
}