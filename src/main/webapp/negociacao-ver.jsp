<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.principais.Negociacao, model.auxiliares.Proposta" %>
<%@ include file="negociacao-util.jspf" %>
<%
    Negociacao negociacao = (Negociacao) request.getAttribute("negociacao");
    ArrayList<Proposta> movimentacoes = (ArrayList<Proposta>) request.getAttribute("movimentacoes");
    String erro = (String) request.getAttribute("erro");

    if (movimentacoes == null) movimentacoes = new ArrayList<Proposta>();

    // destaca "Negociações" no menu
    request.setAttribute("menuAtivo", "negociacoes");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Negociação <%= esc(negociacao.getCodigo()) %></title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=6">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">

</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo pagina-ver-neg">

        <div class="topo topo-form">
            <div>
                <h1>Negociação <%= esc(negociacao.getCodigo()) %></h1>
                <div class="crumbs">
                    <a href="negociacao" class="voltar">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="m15 18-6-6 6-6"/>
                        </svg>
                        Negociações
                    </a>
                    <span>&bull; Atualizada em - <%= data(negociacao.getUltimaAtualizacao()) %></span>
                    <span class="selo selo-<%= classeStatus(negociacao.getStatus()) %>"><%= esc(legivel(negociacao.getStatus())) %></span>
                </div>
            </div>
            <div class="acoes">
                <a href="negociacao?acao=editar&id=<%= negociacao.getIdNegociacao() %>" class="botao claro" style="display:inline-flex;align-items:center;gap:6px;">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4Z"/>
                    </svg>
                    Editar
                </a>
                <a href="#excluir" class="botao vinho" style="display:inline-flex;align-items:center;gap:6px;">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/>
                    </svg>
                    Excluir
                </a>
            </div>
        </div>

        <% if (erro != null) { %>
            <div class="erro"><%= esc(erro) %></div>
        <% } %>

        <%-- Cartões Cooperativa / Recicladora (estrutura em negociacao-partes.jspf) --%>
        <%@ include file="negociacao-partes.jspf" %>

        <div class="cartao">
            <h2 class="linha">Resumo</h2>
            <div class="resumo-grade">
                <div class="dado"><small>Material</small><span><%= esc(negociacao.getNomeMaterial()) %></span></div>
                <div class="dado"><small>Quantidade</small><span><%= kg(negociacao.getQuantidadeKg()) %></span></div>
                <div class="dado"><small>Valor atual</small><span><%= moeda(negociacao.getValorKgAtual()) %></span></div>
                <div class="dado"><small>Status</small><span class="selo selo-<%= classeStatus(negociacao.getStatus()) %>"><%= esc(legivel(negociacao.getStatus())) %></span></div>
            </div>
        </div>

        <% if (request.getParameter("teste") != null) { %>
        <%-- TEMPORARIO (para testar o back): botoes das acoes aceitar, recusar e concluir do servlet.
             Eles nao existem no Figma; apague este bloco quando o fluxo estiver testado. --%>
        <div class="cartao">
            <h2 class="linha">Ações da negociação <small>(temporário, para testes)</small></h2>
            <div class="acoes">
                <form method="post" action="negociacao">
                    <input type="hidden" name="acao" value="aceitar">
                    <input type="hidden" name="id" value="<%= negociacao.getIdNegociacao() %>">
                    <button type="submit" class="botao verde">Aceitar</button>
                </form>
                <form method="post" action="negociacao">
                    <input type="hidden" name="acao" value="recusar">
                    <input type="hidden" name="id" value="<%= negociacao.getIdNegociacao() %>">
                    <button type="submit" class="botao vinho">Recusar</button>
                </form>
                <form method="post" action="negociacao">
                    <input type="hidden" name="acao" value="concluir">
                    <input type="hidden" name="id" value="<%= negociacao.getIdNegociacao() %>">
                    <button type="submit" class="botao claro">Concluir</button>
                </form>
            </div>
        </div>

        <% } %>

        <div class="cartao">
            <h2 class="linha">Linha do tempo</h2>
            <%-- Linha vertical, bolinhas e cartões (estrutura em negociacao-linha-tempo.jspf) --%>
            <%@ include file="negociacao-linha-tempo.jspf" %>
        </div>

    </div>
</div>

<%@ include file="modal-excluir-negociacao.jspf" %>
<%@ include file="notificacao.jspf" %>
</body>
</html>
