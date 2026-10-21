package controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebFilter("/*")
public class AutenticacaoFilter implements Filter {

    // Caminhos que não exigem login
    private static final List<String> PUBLICOS =
            List.of("/login", "/cadastro", "/css/", "/img/", "/js/");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String caminho = request.getRequestURI().substring(request.getContextPath().length());
        boolean publico = PUBLICOS.stream().anyMatch(caminho::startsWith);

        HttpSession sessao = request.getSession(false);
        boolean administrador = sessao != null
                && sessao.getAttribute("usuarioLogado") != null
                && "ADMINISTRADOR".equals(sessao.getAttribute("tipoUsuario"));

        if (publico || administrador) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}