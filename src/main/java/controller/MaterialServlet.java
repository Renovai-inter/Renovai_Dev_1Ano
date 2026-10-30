package controller;

import dao.MaterialDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.principais.Material;

import java.io.IOException;
import java.util.List;

@WebServlet("/material")
public class MaterialServlet extends HttpServlet {

    private final MaterialDAO materialDAO = new MaterialDAO();

    // Processa o cadastro, a edição ou a exclusão de um material.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Leitura da requisição

        String acao = request.getParameter("acao");
        String nome = request.getParameter("Nome");
        String categoria = request.getParameter("Categoria");

        try {
            // Processamento da ação

            int linhas;
            if ("cadastrar".equals(acao)) {
                String origem = request.getParameter("origem");
                if (origem == null || origem.isBlank()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Informe a origem do material.");
                    return;
                }
                linhas = materialDAO.cadastrarMaterial(new Material(nome, categoria, origem));
            } else if ("editar".equals(acao)) {
                Long idMaterial = Long.parseLong(request.getParameter("idMaterial"));
                Material alteracao = new Material();
                alteracao.setIdMaterial(idMaterial);
                alteracao.setNome(nome);
                alteracao.setCategoria(categoria);
                linhas = materialDAO.atualizarMaterial(alteracao);
            } else if ("excluir".equals(acao)) {
                Long idMaterial = Long.parseLong(request.getParameter("idMaterial"));
                Material exclusao = new Material();
                exclusao.setIdMaterial(idMaterial);
                linhas = materialDAO.excluirMaterial(exclusao);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida.");
                return;
            }

            // Preparação da resposta

            if (linhas == 0) {
                if ("cadastrar".equals(acao)) {
                    response.sendError(HttpServletResponse.SC_CONFLICT,
                            "Não foi possível cadastrar este material.");
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "Material não encontrado.");
                }
                return;
            }

            // Encaminhamento

            response.sendRedirect(request.getContextPath() + "/material?sucesso=" + acao);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao processar o material.", e);
        }
    }

    // Lista os materiais cadastrados e abre a página de materiais.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Processamento da ação

            List<Material> materiais = materialDAO.listarMateriais();

            // Preparação da resposta

            request.setAttribute("materiais", materiais);

            // Encaminhamento

            request.getRequestDispatcher("/materiais.jsp").forward(request, response);
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao consultar materiais.", e);
        }
    }
}
