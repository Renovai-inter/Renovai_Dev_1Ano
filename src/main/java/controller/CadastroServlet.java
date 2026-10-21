package controller;

import dao.UsuarioDAO;
import model.relatorio.Usuario;
import util.SenhaAPI;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/cadastro")
public class CadastroServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("1".equals(request.getParameter("concluido"))) {
            request.getRequestDispatcher("/cadastro-concluido.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String nome = valor(request.getParameter("nome"));
        String email = valor(request.getParameter("email")).toLowerCase();
        String senha = request.getParameter("senha") == null ? "" : request.getParameter("senha");
        String confirmacao = request.getParameter("confirmarSenha") == null
                ? "" : request.getParameter("confirmarSenha");
        boolean termos = request.getParameter("termos") != null;

        String erro = validar(nome, email, senha, confirmacao, termos);

        try {
            if (erro == null && usuarioDAO.emailExiste(email)) {
                erro = "Este e-mail já está cadastrado.";
            }

            if (erro == null) {
                Usuario usuario = new Usuario();
                usuario.setNome(nome);
                usuario.setEmail(email);
                usuario.setSenhaHash(SenhaAPI.gerarHash(senha));
                usuario.setTipoUsuario("COOPERADO"); // fixo: o cadastro público nunca cria administrador

                if (!usuarioDAO.cadastrar(usuario)) {
                    erro = "Este e-mail já está cadastrado.";
                }
            }
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao processar o cadastro.", e);
        }

        if (erro != null) {
            request.setAttribute("erro", erro);
            request.setAttribute("nome", nome);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/cadastro.jsp").forward(request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/cadastro?concluido=1");
    }

    private String validar(String nome, String email, String senha,
                           String confirmacao, boolean termos) {

        if (nome.length() < 3 || nome.length() > 150) {
            return "Informe seu nome completo.";
        }
        if (email.length() > 150 || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return "Informe um e-mail válido.";
        }
        if (senha.length() < 8 || senha.length() > 100) {
            return "A senha deve ter de 8 a 100 caracteres.";
        }
        if (!senha.equals(confirmacao)) {
            return "As senhas não conferem.";
        }
        if (!termos) {
            return "Aceite os Termos de Uso e a Política de Privacidade para continuar.";
        }
        return null;
    }

    private String valor(String texto) {
        return texto == null ? "" : texto.trim();
    }
}