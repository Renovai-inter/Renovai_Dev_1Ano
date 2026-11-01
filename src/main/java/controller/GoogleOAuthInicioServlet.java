
package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.GoogleOAuthService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@WebServlet("/oauth/google/iniciar")
public class GoogleOAuthInicioServlet extends HttpServlet {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.setHeader("Cache-Control", "no-store");

        String chaveConfigurada = System.getenv("GMAIL_SETUP_KEY");
        String chaveRecebida = request.getParameter("chave");

        if (chaveConfigurada == null || chaveConfigurada.isBlank()
                || chaveRecebida == null
                || !MessageDigest.isEqual(
                chaveConfigurada.getBytes(StandardCharsets.UTF_8),
                chaveRecebida.getBytes(StandardCharsets.UTF_8))) {

            response.sendError(403, "Configuração não autorizada.");
            return;
        }

        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            response.sendError(403, "Sessão inválida.");
            return;
        }

        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);

        String state = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

        sessao.setAttribute("google_oauth_state", state);
        sessao.setAttribute("google_oauth_setup", Boolean.TRUE);

        response.sendRedirect(
                GoogleOAuthService.gerarUrlAutorizacao(state)
        );
    }
}
