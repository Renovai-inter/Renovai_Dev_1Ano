package controller;

import dao.MaterialDAO;
import model.Material;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/material")
public class MaterialServlet extends HttpServlet {

    private MaterialDAO materialDAO = new MaterialDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DOPOST FOI CHAMADO");

        String acao = request.getParameter("acao");

        String nome = request.getParameter("Nome");
        String categoria = request.getParameter("Categoria");

        System.out.println("Nome: " + nome);
        System.out.println("Categoria: " + categoria);

        if ("editar".equals(acao)) {

            Long idMaterial = Long.parseLong(request.getParameter("idMaterial"));

            Material material = new Material();
            material.setIdMaterial(idMaterial);
            material.setNome(nome);
            material.setCategoria(categoria);

            materialDAO.atualizarMaterial(material);

        } else {

            Material material = new Material();
            material.setNome(nome);
            material.setCategoria(categoria);

            int resultado = materialDAO.cadastrarMaterial(material);

            System.out.println("RESULTADO DO CADASTRO: " + resultado);
        }

        response.sendRedirect(request.getContextPath() + "/material");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("DOGET FOI CHAMADO");

        List<Material> materiais = materialDAO.listarMateriais();

        request.setAttribute("materiais", materiais);

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}