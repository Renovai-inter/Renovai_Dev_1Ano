package controller;

import dao.CooperadoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Cooperado;

import java.io.IOException;
import java.sql.SQLException;
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

    // === ABRIR TELAS =================================================================================================

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
        request.setAttribute("atividadesOpcoes", dao.listarValoresPermitidos("ck_coopatv_atividade",
                "Coleta externa", "Financeiro", "Pesagem", "Direção de veículo", "Prensagem", "Triagem"));

        request.getRequestDispatcher("/cooperado-ver.jsp").forward(request, response);
    }

    private void abrirNovo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("cooperativas", dao.listarCooperativas());
        request.setAttribute("todasPermissoes", dao.listarTodasPermissoes());
        request.setAttribute("cargos", dao.listarValoresPermitidos("ck_cooperado_cargo"));
        request.setAttribute("atividadesOpcoes", dao.listarValoresPermitidos("ck_coopatv_atividade",
                "Coleta externa", "Financeiro", "Pesagem", "Direção de veículo", "Prensagem", "Triagem"));

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
        request.setAttribute("cargos", dao.listarValoresPermitidos("ck_cooperado_cargo"));
        request.setAttribute("statusOpcoes", dao.listarValoresPermitidos("ck_cooperado_status", "Ativo", "Afastado", "Inativo"));
        request.setAttribute("atividadesOpcoes", dao.listarValoresPermitidos("ck_coopatv_atividade",
                "Coleta externa", "Financeiro", "Pesagem", "Direção de veículo", "Prensagem", "Triagem"));

        request.getRequestDispatcher("/cooperado-editar.jsp").forward(request, response);
    }

    // === GRAVAR ======================================================================================================

    // Botao "Cadastrar cooperado" da tela Novo
    // Botao "Cadastrar cooperado" da tela Novo
    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nome = request.getParameter("nomeCompleto");
        String email = request.getParameter("email");
        String idCooperativa = request.getParameter("idCooperativa");

        if (nome == null || nome.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || idCooperativa == null || idCooperativa.isEmpty()) {

            request.setAttribute("erro", "Preencha o nome completo, o e-mail e escolha a cooperativa.");
            abrirNovo(request, response);
            return;
        }

        email = email.trim();

        if (!emailValido(email)) {
            request.setAttribute("erro", MSG_EMAIL_INVALIDO);
            abrirNovo(request, response);
            return;
        }

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperativa(Long.parseLong(idCooperativa));
        cooperado.setCargo(request.getParameter("cargo"));
        cooperado.setStatus(escolherStatus("Ativo"));

        String[] atividades = request.getParameterValues("atividades");
        String[] permissoes = request.getParameterValues("permissoes");

        // nenhuma caixinha marcada = nenhuma permissao (o Gestor recebe todas na DAO)
        if (permissoes == null) {
            permissoes = new String[0];
        }

        try {
            long idNovo = dao.cadastrarCooperado(cooperado, nome.trim(), email, atividades, permissoes);

            response.sendRedirect("cooperado?acao=ver&id=" + idNovo);

        } catch (RuntimeException e) {
            request.setAttribute("erro", "Não foi possível cadastrar. " + mensagemDoBanco(e));
            abrirNovo(request, response);
        }
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

        email = email.trim();

        if (!emailValido(email)) {
            request.setAttribute("erro", MSG_EMAIL_INVALIDO);
            abrirEditar(request, response, id);
            return;
        }

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(id);
        cooperado.setNomeCompleto(nome.trim());
        cooperado.setEmail(email);
        cooperado.setIdCooperativa(Long.parseLong(idCooperativa));
        cooperado.setCargo(request.getParameter("cargo"));
        cooperado.setStatus(request.getParameter("status"));

        try {
            dao.editarCooperado(cooperado);
            dao.alterarAtividadesDesempenhadas(id, request.getParameterValues("atividades"));

            response.sendRedirect("cooperado?acao=ver&id=" + id);

        } catch (RuntimeException e) {
            request.setAttribute("erro", "Não foi possível salvar. " + mensagemDoBanco(e));
            abrirEditar(request, response, id);
        }
    }

    // Botão "Redefinir senha" da tela Editar
    private void redefinirSenha(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }

        try {
            String senhaNova = dao.redefinirSenha(id);

            // Define o atributo antes de abrir a tela
            request.setAttribute("senhaNova", senhaNova);

        } catch (RuntimeException e) {
            request.setAttribute("erro",
                    "Não foi possível redefinir a senha. " + mensagemDoBanco(e));
        }

        // Abre a tela de edição com os atributos já definidos
        abrirEditar(request, response, id);
    }

    // === EXCLUIR (modal) =============================================================================================

    // Opcao "Alterar status para inativo"
    private void inativar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id != null) {
            try {
                dao.inativarCooperado(id, escolherStatus("Inativo"));

            } catch (RuntimeException e) {
                request.setAttribute("erro", "Não foi possível inativar. " + mensagemDoBanco(e));
                abrirVer(request, response, id);
                return;
            }
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

        try {
            int resultado = dao.excluirCooperado(cooperado);

            // -1 = o banco nao deixa apagar quem tem rateio ou relatorio
            if (resultado == -1) {
                request.setAttribute("erro", "Este cooperado tem rateios ou relatórios e não pode ser excluído. Use a opção Alterar status para inativo.");
                abrirVer(request, response, id);
                return;
            }

        } catch (RuntimeException e) {
            request.setAttribute("erro", "Não foi possível excluir. " + mensagemDoBanco(e));
            abrirVer(request, response, id);
            return;
        }

        response.sendRedirect("cooperado");
    }

    // === AUXILIARES ==================================================================================================

    // Le o parametro "id" e transforma em numero. Se nao veio ou veio errado, devolve null.
    private Long lerId(HttpServletRequest request) {
        try {
            return Long.parseLong(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Procura no banco como o status esta escrito (Ativo, ATIVO...) e usa exatamente esse texto.
    private String escolherStatus(String desejado) {

        ArrayList<String> opcoes = dao.listarValoresPermitidos("ck_cooperado_status", "Ativo", "Afastado", "Inativo");

        for (String s : opcoes) {
            if (s.equalsIgnoreCase(desejado)) {
                return s;
            }
        }
        return desejado;
    }

    // Aceita nome@dominio.com. Recusa pontos seguidos (a..b@x.com), ponto no começo/fim e espaços.
    private static final String REGEX_EMAIL =
            "^[A-Za-z0-9]+([._%+-][A-Za-z0-9]+)*@[A-Za-z0-9]+([.-][A-Za-z0-9]+)*\\.[A-Za-z]{2,}$";

    private static final String MSG_EMAIL_INVALIDO =
            "O formato do e-mail não é válido. Use algo como nome@exemplo.com";

    private boolean emailValido(String email) {
        return email != null && email.matches(REGEX_EMAIL);
    }

    // Pega a mensagem original do erro e troca pelas mensagens amigáveis quando conhece o motivo.
    // Traduz o erro do banco para uma mensagem amigável.
    private String mensagemDoBanco(Throwable e) {

        Throwable causa = e;

        while (causa.getCause() != null) {
            causa = causa.getCause();
        }

        String msg = causa.getMessage() == null ? "" : causa.getMessage();

        if (msg.contains("uq_usuario_email")) {
            return "Este e-mail já está cadastrado. Use outro e-mail.";
        }

        if (msg.contains("ck_usuario_email_formato")) {
            return "O formato do e-mail não é válido. Use algo como nome@exemplo.com";
        }

        // Qualquer outro erro: só a primeira linha (esconde o "Detalhe", que mostra a senha temporária)
        return msg.split("\\R")[0];
    }

}