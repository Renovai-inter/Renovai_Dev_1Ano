<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.principais.Negociacao" %>
<%@ include file="negociacao-util.jspf" %>
<%
    ArrayList<Negociacao> lista = (ArrayList<Negociacao>) request.getAttribute("lista");
    List<String> statusOpcoes = (List<String>) request.getAttribute("statusOpcoes");
    String statusFiltro = (String) request.getAttribute("statusFiltro");

    if (lista == null) lista = new ArrayList<Negociacao>();
    if (statusOpcoes == null) statusOpcoes = new ArrayList<String>();
    if (statusFiltro == null) statusFiltro = "";

    // destaca "Negociações" no menu
    request.setAttribute("menuAtivo", "negociacoes");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Negociações</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=12">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">

    <style>
        /* Ajustes finos da lista para ficar igual ao Figma (ficam aqui para funcionar mesmo com o estilo.css desatualizado) */
        .pagina-lista-neg .tabela-negociacoes { border-collapse: collapse; }
        .pagina-lista-neg .tabela-negociacoes tbody tr { border-bottom: 1px solid #e3e1da; }
        .pagina-lista-neg .tabela-negociacoes tbody tr.sem-dados { border-bottom: 0; }

        /* Material em coluna estreita, quebrando em duas linhas ("Papelão / Ondulado") */
        .pagina-lista-neg .material-nome { display: inline-block; max-width: 92px; line-height: 1.3; font-weight: 500; }

        /* icones de editar e excluir preenchidos */
        .pagina-lista-neg .acoes-linha { white-space: nowrap; text-align: right; }
        .pagina-lista-neg .acoes-linha a { display: inline-flex; align-items: center; vertical-align: middle; }
        .pagina-lista-neg .acoes-linha a.editar { margin-right: 12px; color: #8fb28e; }
        .pagina-lista-neg .acoes-linha a.excluir { color: #8b2346; }
        .pagina-lista-neg .acoes-linha a:hover { opacity: .75; }
    </style>
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo pagina-lista-neg">
        <div class="topo">
            <div>
                <h1>Negociações</h1>
                <p class="sub-pequeno">Propostas e contrapropostas entre cooperativas e empresas recicladoras.</p>
            </div>
            <a href="negociacao?acao=novo" class="botao verde botao-icone botao-sombra">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <path d="m11 17 2 2a1 1 0 1 0 3-3"/>
                    <path d="m14 14 2.5 2.5a1 1 0 1 0 3-3l-3.88-3.88a3 3 0 0 0-4.24 0l-.88.88a1 1 0 1 1-3-3l2.81-2.81a5.79 5.79 0 0 1 7.06-.87l.47.28a2 2 0 0 0 1.42.25L21 4"/>
                    <path d="m21 3 1 11h-2"/><path d="M3 3 2 14l6.5 6.5a1 1 0 1 0 3-3"/><path d="M3 4h8"/>
                </svg>
                Nova negociação
            </a>
        </div>

        <div class="cartao cartao-lista">
            <%-- O filtro por status continua funcionando pela URL (negociacao?status=...), mas nao aparece na tela, como no Figma --%>
            <div class="busca-caixa">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/>
                </svg>
                <input class="busca" type="text" id="busca" data-busca="#tabelaNegociacoes" placeholder="Buscar por código, cooperativa, empresa ou material..." autocomplete="off" aria-label="Buscar negociação">
            </div>

            <div class="tabela-rolagem">
            <table class="tabela tabela-negociacoes" id="tabelaNegociacoes">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Cooperativa</th>
                        <th>Empresa</th>
                        <th>Material</th>
                        <th>Qtd.</th>
                        <th>Valor atual</th>
                        <th>Status</th>
                        <th>Atualização</th>
                        <th></th>
                    </tr>
                </thead>

                <tbody>
                    <% if (lista.isEmpty()) { %>
                        <tr class="sem-dados">
                            <td colspan="9">Nenhuma negociação encontrada.</td>
                        </tr>
                    <% } else { for (Negociacao n : lista) { %>
                        <tr class="linha-cooperado" onclick="location.href='negociacao?acao=ver&id=<%= n.getIdNegociacao() %>'">
                            <td class="codigo"><%= esc(n.getCodigo()) %></td>
                            <td class="txt-verde"><%= esc(n.getNomeCooperativa()) %></td>
                            <td class="txt-vinho"><%= esc(n.getNomeEmpresa()) %></td>
                            <td><span class="material-nome"><%= esc(n.getNomeMaterial()) %></span></td>
                            <td><%= kg(n.getQuantidadeKg()) %></td>
                            <td><%= String.valueOf(moeda(n.getValorKgAtual())).replaceAll("[\\s\\u00A0]", "") %></td>
                            <td><span class="selo selo-<%= classeStatus(n.getStatus()) %>"><%= esc(legivel(n.getStatus())) %></span></td>
                            <td><%= data(n.getUltimaAtualizacao()) %></td>
                            <td class="acoes-linha">
                                <a href="negociacao?acao=editar&id=<%= n.getIdNegociacao() %>" class="editar" title="Editar" aria-label="Editar negociação" onclick="event.stopPropagation()">
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                                        <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04a1 1 0 0 0 0-1.41l-2.34-2.34a1 1 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                                    </svg>
                                </a>
                                <%-- a lixeira abre a tela Ver ja com o modal de exclusao aberto --%>
                                <a href="negociacao?acao=ver&id=<%= n.getIdNegociacao() %>#excluir" class="excluir" title="Excluir" aria-label="Excluir negociação" onclick="event.stopPropagation()">
                                    <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true">
                                        <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                                    </svg>
                                </a>
                            </td>
                        </tr>
                    <% } } %>
                </tbody>
            </table>
            </div>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/scripts/busca-ao-vivo.js?v=1"></script>
<%@ include file="notificacao.jspf" %>
</body>
</html>

