package controller;

import dao.UsuarioDAO;
import model.auxiliares.Usuario;
import model.enums.TipoUsuario;
import util.SenhaAPI;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if ("sair".equals(request.getParameter("acao"))) {
            HttpSession sessao = request.getSession(false);
            if (sessao != null) {
                sessao.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login?saiu=1");
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        if (email == null) {
            email = "";
        } else {
            email = email.trim().toLowerCase();
        }
        String senha = request.getParameter("senha");
        if (senha == null) {
            senha = "";
        }

        Usuario usuario;
        try {
            if (email.isEmpty()) {
                usuario = null;
            } else {
                Usuario filtro = new Usuario();
                filtro.setEmail(email);
                usuario = usuarioDAO.buscarPorEmail(filtro);
            }
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao consultar o usuário.", e);
        }

        // Mesma mensagem para e-mail inexistente, senha errada e conta sem permissão
        if (usuario == null || !SenhaAPI.conferir(senha, usuario.getSenhaHash())) {
            request.setAttribute("erro", "E-mail ou senha incorretos.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        if (usuario.getTipoUsuario() != TipoUsuario.ADMINISTRADOR) {
            request.setAttribute("erro", "Esta área é restrita a administradores. Use o app do Renovaí.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        HttpSession sessao = request.getSession(true);
        request.changeSessionId(); // evita fixação de sessão

        sessao.setAttribute("usuarioLogado", usuario.getIdUsuario());
        sessao.setAttribute("tipoUsuario", usuario.getTipoUsuario());
        sessao.setAttribute("nomeUsuario", primeiroNome(usuario.getNomeCompleto()));
        sessao.setMaxInactiveInterval(8 * 60 * 60);

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }

    private String primeiroNome(String nomeCompleto) {
        String[] partes = nomeCompleto.trim().split("\\s+");
        if (partes.length > 0) {
            return partes[0];
        }
        return nomeCompleto;
    }
}
