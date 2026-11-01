
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
import java.util.logging.Logger;

@WebServlet("/oauth/google/callback")
public class GoogleOAuthCallbackServlet extends HttpServlet {

    private static final Logger LOG =
            Logger.getLogger(GoogleOAuthCallbackServlet.class.getName());

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        // Impede o armazenamento da resposta em cache.
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        // Recupera a sessão criada durante o início do OAuth.
        HttpSession sessao = request.getSession(false);

        if (sessao == null) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Sessão de autorização inválida."
            );
            return;
        }

        // Confirma que o processo foi iniciado pela rota administrativa.
        if (!Boolean.TRUE.equals(
                sessao.getAttribute("google_oauth_setup"))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Configuração OAuth não autorizada."
            );
            return;
        }

        // Recupera os parâmetros enviados pelo Google.
        String codigo = request.getParameter("code");
        String stateRecebido = request.getParameter("state");
        String erroGoogle = request.getParameter("error");

        // Recupera o state gerado no início da autorização.
        String stateEsperado =
                (String) sessao.getAttribute("google_oauth_state");

        // Valida o state para impedir requisições não autorizadas.
        if (stateRecebido == null
                || stateEsperado == null
                || !MessageDigest.isEqual(
                stateRecebido.getBytes(StandardCharsets.UTF_8),
                stateEsperado.getBytes(StandardCharsets.UTF_8))) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Estado de autorização inválido."
            );
            return;
        }

        // Invalida os dados temporários para impedir reutilização.
        sessao.removeAttribute("google_oauth_state");
        sessao.removeAttribute("google_oauth_setup");

        // Verifica se o Google recusou a autorização.
        if (erroGoogle != null) {
            LOG.warning("Autorização OAuth recusada pelo Google.");

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "A autorização do Google não foi concluída."
            );
            return;
        }

        // Verifica se o código de autorização foi recebido.
        if (codigo == null || codigo.isBlank()) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Código de autorização não recebido."
            );
            return;
        }

        try {
            // Troca o código de autorização pelos tokens OAuth.
            // A resposta contém credenciais sensíveis.
            // Não exibir, registrar ou retornar seu conteúdo.
            GoogleOAuthService.trocarCodigoPorTokens(codigo);

            LOG.info("Código OAuth processado pelo backend.");

            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().println(
                    "Autorização recebida pelo Renovaí. "
                            + "A configuração do envio de e-mails "
                            + "ainda precisa ser concluída."
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            LOG.warning("Autorização OAuth interrompida.");

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "A autorização foi interrompida."
            );

        } catch (IOException e) {
            LOG.warning("Falha ao processar autorização OAuth.");

            response.sendError(
                    HttpServletResponse.SC_BAD_GATEWAY,
                    "Não foi possível concluir a autorização do Google."
            );

        } catch (RuntimeException e) {
            LOG.warning("Erro na configuração OAuth.");

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro interno na configuração OAuth."
            );
        }
    }
}
