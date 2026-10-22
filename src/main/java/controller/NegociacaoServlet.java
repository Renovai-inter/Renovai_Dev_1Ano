package controller;

import dao.NegociacaoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Movimentacao;
import model.Negociacao;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;

@WebServlet("/negociacao")
public class NegociacaoServlet extends HttpServlet {

    // Usados so se o servlet nao conseguir ler as opcoes da regra CHECK do banco
    private static final String[] STATUS_PADRAO = {"Aguardando resposta", "Aceita", "Concluída", "Recusada", "Cancelada"};
    private static final String[] AUTOR_PADRAO = {"Cooperativa", "Recicladora"};

    private NegociacaoDAO dao = new NegociacaoDAO();

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

        } else if (acao.equals("movimentar")) {
            movimentar(request, response);

        } else if (acao.equals("aceitar") || acao.equals("recusar")
                || acao.equals("cancelar") || acao.equals("concluir")) {
            mudarStatus(request, response, acao);

        } else if (acao.equals("excluir")) {
            excluir(request, response);

        } else {
            response.sendRedirect("negociacao");
        }
    }

    // === ABRIR TELAS =================================================================================================

    // Lista. Aceita ?status=... para filtrar (todas, novas propostas, em negociacao, aceitas, recusadas, concluidas)
    private void abrirLista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String statusFiltro = request.getParameter("status");

        if (statusFiltro != null && statusFiltro.trim().isEmpty()) {
            statusFiltro = null;
        }

        request.setAttribute("lista", dao.listarNegociacoes(statusFiltro));
        request.setAttribute("statusOpcoes", statusOpcoes());
        request.setAttribute("statusFiltro", statusFiltro == null ? "" : statusFiltro);

        request.getRequestDispatcher("/negociacao-lista.jsp").forward(request, response);
    }

    private void abrirVer(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {

        if (id == null) {
            response.sendRedirect("negociacao");
            return;
        }

        Negociacao negociacao = dao.buscarNegociacaoPorId(id);

        if (negociacao == null) {
            response.sendRedirect("negociacao");
            return;
        }

        ArrayList<Movimentacao> movimentacoes = dao.listarMovimentacoes(id);
        marcarTipoFinal(movimentacoes, negociacao);

        request.setAttribute("negociacao", negociacao);
        request.setAttribute("movimentacoes", movimentacoes);
        request.setAttribute("totalMovimentacoes", movimentacoes.size());
        request.setAttribute("temVenda", dao.possuiVenda(id));

        request.getRequestDispatcher("/negociacao-ver.jsp").forward(request, response);
    }

    private void abrirNovo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("cooperativas", dao.listarCooperativas());
        request.setAttribute("empresas", dao.listarEmpresas());
        request.setAttribute("materiais", dao.listarMateriais());
        request.setAttribute("statusOpcoes", statusOpcoes());
        request.setAttribute("autores", autores());

        request.getRequestDispatcher("/negociacao-novo.jsp").forward(request, response);
    }

    private void abrirEditar(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {

        if (id == null) {
            response.sendRedirect("negociacao");
            return;
        }

        Negociacao negociacao = dao.buscarNegociacaoPorId(id);

        if (negociacao == null) {
            response.sendRedirect("negociacao");
            return;
        }

        ArrayList<Movimentacao> movimentacoes = dao.listarMovimentacoes(id);
        marcarTipoFinal(movimentacoes, negociacao);

        request.setAttribute("negociacao", negociacao);
        request.setAttribute("movimentacoes", movimentacoes);
        request.setAttribute("materiais", dao.listarMateriais());
        request.setAttribute("statusOpcoes", statusOpcoes());
        request.setAttribute("autores", autores());

        request.getRequestDispatcher("/negociacao-editar.jsp").forward(request, response);
    }

    // === GRAVAR ======================================================================================================

    // Botao "Criar negociacao" da tela Nova negociacao
    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long idCooperativa = lerLong(request, "idCooperativa");
        Long idEmpresa = lerLong(request, "idEmpresa");
        Long idMaterial = lerLong(request, "idMaterial");
        BigDecimal valor = lerDecimal(request.getParameter("valor"));
        BigDecimal quantidade = lerDecimal(request.getParameter("quantidade"));
        String autor = request.getParameter("autor");
        String status = request.getParameter("status");
        String observacoes = request.getParameter("observacoes");

        if (idCooperativa == null || idEmpresa == null || idMaterial == null || vazio(autor) || vazio(status)) {
            request.setAttribute("erro", "Escolha a cooperativa, a recicladora, o material, o autor e o status inicial.");
            abrirNovo(request, response);
            return;
        }

        if (valor == null || valor.signum() <= 0 || quantidade == null || quantidade.signum() <= 0) {
            request.setAttribute("erro", "Informe um valor e uma quantidade maiores que zero.");
            abrirNovo(request, response);
            return;
        }

        Negociacao negociacao = new Negociacao();
        negociacao.setIdCooperativa(idCooperativa);
        negociacao.setIdEmpresa(idEmpresa);
        negociacao.setIdMaterial(idMaterial);
        negociacao.setValorKgAtual(valor);
        negociacao.setQuantidadeKg(quantidade);
        negociacao.setStatus(status);
        negociacao.setDataConclusao(ehFinal(status) ? LocalDateTime.now() : null);

        try {
            long idNovo = dao.criarNegociacao(negociacao, autor, observacoes);

            response.sendRedirect("negociacao?acao=ver&id=" + idNovo);

        } catch (RuntimeException e) {
            request.setAttribute("erro", "Não foi possível criar a negociação. " + mensagemDoBanco(e));
            abrirNovo(request, response);
        }
    }

    // Botao "Salvar" da tela Editar (bloco Resumo)
    private void atualizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id == null) {
            response.sendRedirect("negociacao");
            return;
        }

        Long idMaterial = lerLong(request, "idMaterial");
        BigDecimal valor = lerDecimal(request.getParameter("valor"));
        BigDecimal quantidade = lerDecimal(request.getParameter("quantidade"));
        String status = request.getParameter("status");

        if (idMaterial == null || vazio(status)) {
            request.setAttribute("erro", "Escolha o material e o status.");
            abrirEditar(request, response, id);
            return;
        }

        if (valor == null || valor.signum() <= 0 || quantidade == null || quantidade.signum() <= 0) {
            request.setAttribute("erro", "Informe um valor e uma quantidade maiores que zero.");
            abrirEditar(request, response, id);
            return;
        }

        Negociacao atual = dao.buscarNegociacaoPorId(id);

        if (atual == null) {
            response.sendRedirect("negociacao");
            return;
        }

        Negociacao negociacao = new Negociacao();
        negociacao.setIdNegociacao(id);
        negociacao.setIdMaterial(idMaterial);
        negociacao.setValorKgAtual(valor);
        negociacao.setQuantidadeKg(quantidade);
        negociacao.setStatus(status);

        // se o status mudou para um status final (aceita, recusada...), isso tambem entra na linha do tempo
        String autor = null;
        String observacao = null;

        if (ehFinal(status) && !status.equals(atual.getStatus())) {
            autor = autorEncerramento(id, normalizar(status).contains("CANCEL") ? "cancelar" : "aceitar");
            observacao = observacaoPadrao(status);
        }

        try {
            dao.atualizarResumo(negociacao, ehFinal(status), autor, observacao);

            response.sendRedirect("negociacao?acao=ver&id=" + id);

        } catch (RuntimeException e) {
            request.setAttribute("erro", "Não foi possível salvar. " + mensagemDoBanco(e));
            abrirEditar(request, response, id);
        }
    }

    // Botao "Adicionar" do modal Adicionar movimentacao (contraproposta)
    private void movimentar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id == null) {
            response.sendRedirect("negociacao");
            return;
        }

        String autor = request.getParameter("autor");
        BigDecimal valor = lerDecimal(request.getParameter("valor"));
        BigDecimal quantidade = lerDecimal(request.getParameter("quantidade"));
        String observacoes = request.getParameter("observacoes");

        if (vazio(autor) || valor == null || valor.signum() <= 0 || quantidade == null || quantidade.signum() <= 0) {
            request.setAttribute("erro", "Escolha o autor e informe um valor e uma quantidade maiores que zero.");
            request.setAttribute("abrirModal", true);
            abrirEditar(request, response, id);
            return;
        }

        try {
            dao.adicionarMovimentacao(id, autor, valor, quantidade, observacoes);

            response.sendRedirect("negociacao?acao=editar&id=" + id);

        } catch (RuntimeException e) {
            request.setAttribute("erro", "Não foi possível adicionar a movimentação. " + mensagemDoBanco(e));
            request.setAttribute("abrirModal", true);
            abrirEditar(request, response, id);
        }
    }

    // Aceitar, recusar, cancelar ou concluir: muda o status e registra o encerramento na linha do tempo.
    // Nada e apagado: a negociacao vai para o historico.
    private void mudarStatus(HttpServletRequest request, HttpServletResponse response, String acao)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id == null) {
            response.sendRedirect("negociacao");
            return;
        }

        String chave = "CONCLU";

        if (acao.equals("aceitar")) {
            chave = "ACEIT";
        } else if (acao.equals("recusar")) {
            chave = "RECUS";
        } else if (acao.equals("cancelar")) {
            chave = "CANCEL";
        }

        String status = escolherStatus(chave);

        if (status == null) {
            request.setAttribute("erro", "O banco não tem um status correspondente a esta ação.");
            abrirVer(request, response, id);
            return;
        }

        // texto da movimentacao: o que o usuario digitou (campo "observacoes", opcional) ou um texto padrao
        String observacao = request.getParameter("observacoes");

        if (vazio(observacao)) {
            observacao = observacaoPadrao(status);
        }

        try {
            dao.alterarStatus(id, status, true, autorEncerramento(id, acao), observacao);

            response.sendRedirect("negociacao?acao=ver&id=" + id);

        } catch (RuntimeException e) {
            request.setAttribute("erro", "Não foi possível alterar o status. " + mensagemDoBanco(e));
            abrirVer(request, response, id);
        }
    }

    // === EXCLUIR (modal) =============================================================================================

    // Opcao "Excluir permanentemente": exige digitar o codigo da negociacao (campo "confirmacao").
    // Funciona direto, sem precisar cancelar antes. Se ja existir uma venda, ela continua registrada (so perde o vinculo).
    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Long id = lerId(request);

        if (id == null) {
            response.sendRedirect("negociacao");
            return;
        }

        Negociacao negociacao = dao.buscarNegociacaoPorId(id);

        if (negociacao == null) {
            response.sendRedirect("negociacao");
            return;
        }

        String confirmacao = request.getParameter("confirmacao");

        if (confirmacao == null || !confirmacao.trim().equalsIgnoreCase(negociacao.getCodigo())) {
            request.setAttribute("erro", "Para excluir, digite o código " + negociacao.getCodigo() + " no campo de confirmação.");
            abrirVer(request, response, id);
            return;
        }

        try {
            dao.excluirNegociacao(id);

        } catch (RuntimeException e) {
            request.setAttribute("erro", "Não foi possível excluir. " + mensagemDoBanco(e));
            abrirVer(request, response, id);
            return;
        }

        response.sendRedirect("negociacao");
    }

    // === AUXILIARES ==================================================================================================

    // Le o parametro "id" e transforma em numero. Se nao veio ou veio errado, devolve null.
    private Long lerId(HttpServletRequest request) {
        return lerLong(request, "id");
    }

    private Long lerLong(HttpServletRequest request, String nome) {
        try {
            return Long.parseLong(request.getParameter(nome).trim());
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    // Aceita "1200", "1200.50", "1.200,50" e "R$ 1.200,50". Devolve null se nao for um numero.
    private BigDecimal lerDecimal(String texto) {

        if (texto == null) {
            return null;
        }

        String t = texto.replace("R$", "").replace("kg", "").replace(" ", "").trim();

        if (t.isEmpty()) {
            return null;
        }

        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }

        try {
            return new BigDecimal(t).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean vazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // Tira acentos, poe em maiusculas e troca _ por espaco: "Concluída" e "CONCLUIDA" ficam iguais
    private String normalizar(String texto) {

        if (texto == null) {
            return "";
        }

        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .replace('_', ' ');
    }

    // Status que encerram a negociacao (marcam a data de conclusao)
    private boolean ehFinal(String status) {

        String s = normalizar(status);

        return s.contains("ACEIT") || s.contains("CONCLU") || s.contains("RECUS") || s.contains("CANCEL");
    }

    // Opcoes de status que o banco aceita
    private ArrayList<String> statusOpcoes() {
        return dao.listarValoresPermitidos("negociacao", "status", STATUS_PADRAO);
    }

    // Opcoes de autor que o banco aceita
    private ArrayList<String> autores() {
        return dao.listarValoresPermitidos("proposta", "autor", AUTOR_PADRAO);
    }

    // Acha, entre os status do banco, o que combina com a palavra-chave (ex.: ACEIT -> "ACEITA" ou "Aceita")
    private String escolherStatus(String chave) {

        for (String opcao : statusOpcoes()) {
            if (normalizar(opcao).contains(chave)) {
                return opcao;
            }
        }
        return null;
    }

    // Nome do tipo da ultima movimentacao quando a negociacao esta encerrada (null se ainda esta aberta)
    private String tipoFinal(String status) {

        String s = normalizar(status);

        if (s.contains("ACEIT")) {
            return "Aceita";
        } else if (s.contains("RECUS")) {
            return "Recusada";
        } else if (s.contains("CONCLU")) {
            return "Concluída";
        } else if (s.contains("CANCEL")) {
            return "Cancelada";
        }
        return null;
    }

    // Texto que entra na linha do tempo quando o encerramento e feito pelo botao (sem o usuario digitar nada)
    private String observacaoPadrao(String status) {

        String s = normalizar(status);

        if (s.contains("ACEIT")) {
            return "Proposta aceita.";
        } else if (s.contains("RECUS")) {
            return "Proposta recusada.";
        } else if (s.contains("CONCLU")) {
            return "Negociação concluída.";
        } else if (s.contains("CANCEL")) {
            return "Negociação cancelada.";
        }
        return null;
    }

    // A tabela nao guarda o tipo. A primeira movimentacao e a Proposta, as outras sao Contrapropostas
    // (a DAO ja marca isso). Se a negociacao esta encerrada, a ultima movimentacao mostra o status final.
    private void marcarTipoFinal(ArrayList<Movimentacao> movimentacoes, Negociacao negociacao) {

        String tipo = tipoFinal(negociacao.getStatus());

        if (movimentacoes.size() > 1 && tipo != null) {
            movimentacoes.get(movimentacoes.size() - 1).setTipo(tipo);
        }
    }

    /* Quem aparece como autor da movimentacao de encerramento. Como no Figma, quem responde e a outra parte:
       se a ultima proposta foi da cooperativa, a recicladora aceita (ou recusa), e vice-versa.
       Ao cancelar, quem desiste e quem fez a ultima proposta. Devolve null se nao der para descobrir os autores
       (nesse caso so o status muda). */
    private String autorEncerramento(Long id, String acao) {

        String cooperativa = escolherAutor("COOP");
        String recicladora = escolherAutor("RECIC");

        if (cooperativa == null || recicladora == null) {
            return null;
        }

        ArrayList<Movimentacao> movimentacoes = dao.listarMovimentacoes(id);

        boolean ultimaFoiDaCooperativa = movimentacoes.isEmpty()
                || movimentacoes.get(movimentacoes.size() - 1).isCooperativa();

        boolean cooperativaAge = acao.equals("cancelar") ? ultimaFoiDaCooperativa : !ultimaFoiDaCooperativa;

        return cooperativaAge ? cooperativa : recicladora;
    }

    // Acha, entre os autores que o banco aceita, o que combina com a palavra-chave (COOP ou RECIC)
    private String escolherAutor(String chave) {

        for (String opcao : autores()) {
            if (normalizar(opcao).contains(chave)) {
                return opcao;
            }
        }
        return null;
    }

    // Pega a mensagem original do erro (a do Postgres), que explica o que o banco recusou.
    private String mensagemDoBanco(Throwable e) {

        Throwable causa = e;

        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa.getMessage();
    }

}