package controller;

import dao.UsuarioDAO;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.auxiliares.Usuario;
import model.enums.TipoUsuario;
import util.EmailService;
import util.TokenRecuperacao;

import java.io.IOException;

@WebServlet("/esqueci-senha")
public class EsqueciSenhaServlet extends HttpServlet {

    private static final int MINUTOS_DE_VALIDADE = 30;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Mostra o formulário para informar o e-mail da conta.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/esqueci-senha.jsp").forward(request, response);
    }

    // Envia o link de redefinição se o e-mail for de um administrador e responde sempre a mesma coisa.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Leitura da requisição

        String email = request.getParameter("email");
        if (email == null) {
            email = "";
        } else {
            email = email.trim().toLowerCase();
        }

        // Processamento da ação

        if (!email.isEmpty()) {
            Usuario usuario = buscarAdministrador(email);
            if (usuario != null) {
                enviarLink(usuario, request);
            }
        }

        // Encaminhamento

        request.setAttribute("enviado", true);
        request.getRequestDispatcher("/esqueci-senha.jsp").forward(request, response);
    }

    // Retorna o administrador com esse e-mail ou null se não existir, for inválido ou não for administrador.
    private Usuario buscarAdministrador(String email) throws ServletException {
        Usuario filtro = new Usuario();
        try {
            filtro.setEmail(email);
        } catch (IllegalArgumentException e) {
            return null;
        }

        Usuario usuario;
        try {
            usuario = usuarioDAO.buscarPorEmail(filtro);
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao consultar o usuário.", e);
        }

        if (usuario == null) {
            return null;
        }
        if (usuario.getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {
            return null;
        }
        return usuario;
    }

    // Gera o token e envia o e-mail. Falhas de envio vão só para o log.
    private void enviarLink(Usuario usuario, HttpServletRequest request) {
        String token = TokenRecuperacao.gerar(
                usuario.getEmail(), usuario.getSenhaHash(), MINUTOS_DE_VALIDADE);
        String link = baseUrl(request) + "/redefinir-senha?token=" + token;

        try {
            EmailService.enviarRecuperacao(usuario.getEmail(), usuario.getNomeCompleto(), link);
        } catch (MessagingException | RuntimeException e) {
            getServletContext().log("Falha ao enviar o e-mail de redefinição de senha", e);
        }
    }

    // Endereço do site usado no link. Em produção, defina RENOVAI_BASE_URL;
    // sem ela, usa o mesmo endereço e contexto da página que pediu a redefinição.
    private String baseUrl(HttpServletRequest request) {
        String definido = System.getenv("RENOVAI_BASE_URL");
        if (definido != null && !definido.isBlank()) {
            return definido.trim().replaceAll("/+$", "");
        }
        String url = request.getRequestURL().toString();
        return url.substring(0, url.length() - request.getRequestURI().length())
                + request.getContextPath();
    }
}