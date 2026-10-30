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
                linhas = materialDAO.cadastrarMaterial(new Material(nome, categoria));
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
                mostrarErro(request, response, "Ação inválida.");
                return;
            }

            // Preparação da resposta

            if (linhas == 0) {
                if ("cadastrar".equals(acao)) {
                    mostrarErro(request, response, "Não foi possível cadastrar este material.");
                } else {
                    mostrarErro(request, response, "Material não encontrado.");
                }
                return;
            }

            // Encaminhamento

            response.sendRedirect(request.getContextPath() + "/material?sucesso=" + acao);
        } catch (IllegalArgumentException e) {
            mostrarErro(request, response, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Erro ao processar o material.", e);
            mostrarErro(request, response, "Não foi possível concluir a operação do material.");
        }
    }

    // Lista os materiais cadastrados e abre a página de materiais.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Object erro = request.getSession().getAttribute("erroMaterial");
            if (erro != null) {
                request.setAttribute("erroMaterial", erro);
                request.getSession().removeAttribute("erroMaterial");
            }
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

    // Mantém a falha visível na tela de materiais após o redirecionamento.
    private void mostrarErro(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws IOException {
        request.getSession().setAttribute("erroMaterial", mensagem);
        response.sendRedirect(request.getContextPath() + "/material");
    }
}
