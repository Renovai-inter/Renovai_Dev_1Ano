<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.principais.Negociacao, model.auxiliares.Proposta" %>
<%@ page import="model.auxiliares.Proposta" %>
<%@ include file="negociacao-util.jspf" %>
<%
    Negociacao negociacao = (Negociacao) request.getAttribute("negociacao");
    ArrayList<Proposta> movimentacoes = (ArrayList<Proposta>) request.getAttribute("movimentacoes");
    Map<Long, String> materiais = (Map<Long, String>) request.getAttribute("materiais");
    List<String> statusOpcoes = (List<String>) request.getAttribute("statusOpcoes");
    String erro = (String) request.getAttribute("erro");

    if (movimentacoes == null) movimentacoes = new ArrayList<Proposta>();
    if (materiais == null) materiais = new LinkedHashMap<Long, String>();
    if (statusOpcoes == null) statusOpcoes = new ArrayList<String>();

    // destaca "Negociações" no menu
    request.setAttribute("menuAtivo", "negociacoes");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar negociação <%= esc(negociacao.getCodigo()) %></title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=4">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">

        <form method="post" action="negociacao">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<%= negociacao.getIdNegociacao() %>">

            <div class="topo topo-form">
                <div>
                    <h1>Editar Negociação <%= esc(negociacao.getCodigo()) %></h1>
                    <div class="crumbs">
                        <a href="negociacao?acao=ver&id=<%= negociacao.getIdNegociacao() %>" class="voltar">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                <path d="m15 18-6-6 6-6"/>
                            </svg>
                            Negociações
                        </a>
                        <span>&bull; Atualizada em <%= data(negociacao.getUltimaAtualizacao()) %> &bull;</span>
                        <span class="selo selo-<%= classeStatus(negociacao.getStatus()) %>"><%= esc(legivel(negociacao.getStatus())) %></span>
                    </div>
                </div>
                <div class="acoes">
                    <button type="submit" class="botao claro">Salvar</button>
                    <a href="#excluir" class="botao vinho">Excluir</a>
                </div>
            </div>

            <% if (erro != null) { %>
                <div class="erro"><%= esc(erro) %></div>
            <% } %>

            <%@ include file="negociacao-partes.jspf" %>

            <div class="cartao">
                <h2 class="linha">Resumo</h2>
                <div class="resumo-grade">

                    <div class="campo">
                        <label for="idMaterial">Material</label>
                        <select id="idMaterial" name="idMaterial">
                            <% for (Map.Entry<Long, String> mat : materiais.entrySet()) { %>
                                <option value="<%= mat.getKey() %>" <%= mat.getKey().equals(negociacao.getIdMaterial()) ? "selected" : "" %>><%= esc(mat.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="quantidade">Quantidade (kg)</label>
                        <input type="text" id="quantidade" name="quantidade" inputmode="decimal" value="<%= esc(numero(negociacao.getQuantidadeKg())) %>">
                    </div>

                    <div class="campo">
                        <label for="valor">Valor atual (R$ por kg)</label>
                        <input type="text" id="valor" name="valor" inputmode="decimal" value="<%= esc(numero(negociacao.getValorKgAtual())) %>">
                    </div>

                    <div class="campo">
                        <label for="status">Status</label>
                        <select id="status" name="status">
                            <% for (String st : statusOpcoes) { %>
                                <option value="<%= esc(st) %>" <%= st.equals(negociacao.getStatus()) ? "selected" : "" %>><%= esc(legivel(st)) %></option>
                            <% } %>
                        </select>
                    </div>

                </div>
            </div>
        </form>

        <%-- fora do formulario acima: o botao abre o modal de proposta (que tem o proprio formulario) --%>
        <div class="cartao">
            <h2 class="linha">Linha do tempo</h2>
            <div class="acoes acoes-direita">
                <a href="#proposta" class="botao claro">+ Nova movimentação</a>
            </div>
            <%@ include file="negociacao-linha-tempo.jspf" %>
        </div>

    </div>
</div>

<%@ include file="modal-proposta.jspf" %>
<%@ include file="modal-excluir-negociacao.jspf" %>
</body>
</html>
