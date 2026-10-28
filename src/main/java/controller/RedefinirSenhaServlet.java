package controller;

import dao.UsuarioDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.auxiliares.Usuario;
import model.enums.TipoUsuario;
import util.SenhaAPI;
import util.TokenRecuperacao;

import java.io.IOException;

@WebServlet("/redefinir-senha")
public class RedefinirSenhaServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Mostra o formulário de nova senha se o link ainda valer.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        Usuario usuario = usuarioDoToken(token);

        if (usuario == null) {
            mostrar(request, response, null, true, null);
        } else {
            mostrar(request, response, token, false, null);
        }
    }

    // Valida a nova senha e grava no lugar da antiga se o link ainda valer.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Leitura da requisição

        String token = request.getParameter("token");
        String senha = request.getParameter("senha");
        if (senha == null) {
            senha = "";
        }
        String confirmacao = request.getParameter("confirmarSenha");
        if (confirmacao == null) {
            confirmacao = "";
        }

        // Validação dos dados

        Usuario usuario = usuarioDoToken(token);
        if (usuario == null) {
            mostrar(request, response, null, true, null);
            return;
        }
        if (senha.length() < 8 || senha.length() > 100) {
            mostrar(request, response, token, false, "A senha deve ter de 8 a 100 caracteres.");
            return;
        }
        if (!senha.equals(confirmacao)) {
            mostrar(request, response, token, false, "As senhas não conferem.");
            return;
        }

        // Processamento da ação

        usuario.setSenhaHash(SenhaAPI.gerarHash(senha));
        usuario.setSenhaTemporaria(false);

        int linhas;
        try {
            linhas = usuarioDAO.alterarSenha(usuario);
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao redefinir a senha.", e);
        }

        // Encaminhamento

        if (linhas == 0) {
            mostrar(request, response, null, true, null);
            return;
        }
        response.sendRedirect(request.getContextPath() + "/login?senha=ok");
    }

    // Retorna o administrador dono do token ou null se o link não vale mais.
    private Usuario usuarioDoToken(String token) throws ServletException {
        String email = TokenRecuperacao.emailDoToken(token);
        if (email == null) {
            return null;
        }

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
        if (!TokenRecuperacao.conferir(token, usuario.getSenhaHash())) {
            return null;
        }
        return usuario;
    }

    // Prepara os atributos e encaminha para o JSP.
    private void mostrar(HttpServletRequest request, HttpServletResponse response,
                         String token, boolean tokenInvalido, String erro)
            throws ServletException, IOException {

        request.setAttribute("token", token);
        request.setAttribute("tokenInvalido", tokenInvalido);
        request.setAttribute("erro", erro);
        request.getRequestDispatcher("/redefinir-senha.jsp").forward(request, response);
    }
}