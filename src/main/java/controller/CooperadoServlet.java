package controller;

import dao.CooperadoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.principais.Cooperado;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/cooperado")
public class CooperadoServlet extends HttpServlet {

    private CooperadoDAO dao = new CooperadoDAO();

    // === GET: abrir telas ============================================================================================

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        if (acao == null) {
            acao = "listar";
        }

        if (acao.equals("ver")) {
            abrirVer(request, response, lerId(request));

        } else if (acao.equals("novo")) {
            abrirNovo(request, response);

        } else if (acao.equals("editar")) {
            abrirEditar(request, response, lerId(request));

        } else {
            abrirLista(request, response);
        }
    }

    // === POST: gravar, alterar e excluir =============================================================================

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String acao = request.getParameter("acao");

        if (acao == null) {
            acao = "";
        }

        if (acao.equals("salvar")) {
            salvar(request, response);

        } else if (acao.equals("atualizar")) {
            atualizar(request, response);

        } else if (acao.equals("redefinirSenha")) {
            redefinirSenha(request, response);

        } else if (acao.equals("inativar")) {
            inativar(request, response);

        } else if (acao.equals("excluir")) {
            excluir(request, response);

        } else {
            response.sendRedirect("cooperado");
        }
    }

    // === ABRIR TELAS (cada uma busca os dados no DAO e entrega para o JSP) ===========================================

    private void abrirLista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ArrayList<Cooperado> lista = dao.listaCooperados();

        request.setAttribute("lista", lista);
        request.getRequestDispatcher("/cooperado-lista.jsp").forward(request, response);
    }

    private void abrirVer(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {

        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }

        Cooperado cooperado = dao.buscarCooperadoPorId(id);

        if (cooperado == null) {
            response.sendRedirect("cooperado");
            return;
        }

        request.setAttribute("cooperado", cooperado);
        request.setAttribute("permissoesDoCooperado", dao.listarPermissoes(id));
        request.setAttribute("todasPermissoes", dao.listarTodasPermissoes());
        request.setAttribute("totalColetas", dao.contarColetasDoCooperado(id));

        request.getRequestDispatcher("/cooperado-ver.jsp").forward(request, response);
    }

    private void abrirNovo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("cooperativas", dao.listarCooperativas());
        request.setAttribute("todasPermissoes", dao.listarTodasPermissoes());

        request.getRequestDispatcher("/cooperado-novo.jsp").forward(request, response);
    }

    private void abrirEditar(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {

        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }

        Cooperado cooperado = dao.buscarCooperadoPorId(id);

        if (cooperado == null) {
            response.sendRedirect("cooperado");
            return;
        }

        request.setAttribute("cooperado", cooperado);
        request.setAttribute("cooperativas", dao.listarCooperativas());
        request.setAttribute("totalColetas", dao.contarColetasDoCooperado(id));

        request.getRequestDispatcher("/cooperado-editar.jsp").forward(request, response);
    }

    // === GRAVAR ======================================================================================================

    // Botao "Cadastrar cooperado" da tela Novo
    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nomeCompleto");
        String email = request.getParameter("email");
        String idCooperativa = request.getParameter("idCooperativa");

        // validacao: sem isso o banco recusa e aparece erro 500
        if (nome == null || nome.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || idCooperativa == null || idCooperativa.isEmpty()) {

            request.setAttribute("erro", "Preencha o nome completo, o e-mail e escolha a cooperativa.");
            abrirNovo(request, response);
            return;
        }

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperativa(Long.parseLong(idCooperativa));
        cooperado.setCargo(request.getParameter("cargo"));

        String[] atividades = request.getParameterValues("atividades");
        String[] permissoes = request.getParameterValues("permissoes");

        // nenhuma caixinha marcada = nenhuma permissao (o Gestor recebe todas na DAO)
        if (permissoes == null) {
            permissoes = new String[0];
        }

        long idNovo = dao.cadastrarCooperado(cooperado, nome.trim(), email.trim(), atividades, permissoes);

        response.sendRedirect("cooperado?acao=ver&id=" + idNovo);
    }

    // Botao "Salvar" da tela Editar
    private void atualizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }

        String nome = request.getParameter("nomeCompleto");
        String email = request.getParameter("email");
        String idCooperativa = request.getParameter("idCooperativa");

        if (nome == null || nome.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || idCooperativa == null || idCooperativa.isEmpty()) {

            request.setAttribute("erro", "Preencha o nome completo, o e-mail e escolha a cooperativa.");
            abrirEditar(request, response, id);
            return;
        }

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(id);
        cooperado.setNomeCompleto(nome.trim());
        cooperado.setEmail(email.trim());
        cooperado.setIdCooperativa(Long.parseLong(idCooperativa));
        cooperado.setCargo(request.getParameter("cargo"));
        cooperado.setStatus(request.getParameter("status"));

        dao.editarCooperado(cooperado);
        dao.alterarAtividadesDesempenhadas(id, request.getParameterValues("atividades"));

        response.sendRedirect("cooperado?acao=ver&id=" + id);
    }

    // Botao "Redefinir senha" da tela Editar
    private void redefinirSenha(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }

        String senhaNova = dao.redefinirSenha(id);

        // a tela Editar mostra a senha nova uma unica vez
        request.setAttribute("senhaNova", senhaNova);
        abrirEditar(request, response, id);
    }

    // === EXCLUIR (modal) =============================================================================================

    // Opcao "Alterar status para inativo"
    private void inativar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id != null) {
            dao.inativarCooperado(id);
        }

        response.sendRedirect("cooperado");
    }

    // Opcao "Excluir permanentemente"
    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(id);

        int resultado = dao.excluirCooperado(cooperado);

        // -1 = o banco nao deixa apagar quem tem rateio ou relatorio
        if (resultado == -1) {
            request.setAttribute("erro", "Este cooperado tem rateios ou relatórios e não pode ser excluído. Use a opção Alterar status para inativo.");
            abrirVer(request, response, id);
            return;
        }

        response.sendRedirect("cooperado");
    }

    // === AUXILIAR ====================================================================================================

    // Le o parametro "id" e transforma em numero. Se nao veio ou veio errado, devolve null.
    private Long lerId(HttpServletRequest request) {
        try {
            return Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

}