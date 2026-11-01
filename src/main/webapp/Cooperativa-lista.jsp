<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.format.DateTimeFormatter,java.util.ArrayList,model.principais.Cooperativa" %>
<%!
private String h(Object value) {
    if (value == null) return "";
    return value.toString()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;");
}
private static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
%>
<!doctype html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width,initial-scale=1">
        <title>Cooperativas | Renovai</title>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cooperativas.css">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">
    </head>
    <body>
        <div class="layout">
            <%@ include file="/menu.jspf" %>
            <main class="conteudo coop-conteudo">
                <% ArrayList<Cooperativa> itens = (ArrayList<Cooperativa>) request.getAttribute("cooperativas"); %>
                <header class="coop-top">
                    <div>
                        <h1>Cooperativas</h1>
                        <p>Cooperativas de reciclagem cadastradas na plataforma</p>
                    </div>
                    <a class="coop-btn" href="cooperativa?pagina=nova">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M6 22V4a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v18Z"/><path d="M6 12H4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h2"/><path d="M18 9h2a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2h-2"/><path d="M10 6h4"/><path d="M10 10h4"/><path d="M10 14h4"/><path d="M10 18h4"/></svg>
                        Nova cooperativa
                    </a>
                </header>
                <% if (request.getAttribute("erro") != null) { %>
                <div class="coop-alert erro" role="alert"><%= h(request.getAttribute("erro")) %></div>
                <% } %>
                <% if (request.getAttribute("mensagem") != null) { %>
                <div class="coop-alert" role="status"><%= h(request.getAttribute("mensagem")) %></div>
                <% } %>
                <section class="coop-card coop-card-lista">
                    <form method="get" action="cooperativa" class="coop-search">
                        <input name="busca" data-busca="#tabelaCooperativas" autocomplete="off" value="<%= h(request.getParameter("busca")) %>" placeholder="Buscar por nome, CNPJ ou cidade..." aria-label="Buscar cooperativa">
                        <button class="coop-btn" type="submit">Buscar</button>
                    </form>
                    <div class="coop-overflow">
                        <table class="coop-table" id="tabelaCooperativas" data-responsiva>
                            <thead>
                                <tr>
                                    <th>Nome</th>
                                    <th>Cidade / UF</th>
                                    <th>Status</th>
                                    <th>Cooperados</th>
                                    <th>Cadastro</th>
                                    <th><span class="coop-sr">Ações</span></th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (itens == null || itens.isEmpty()) { %>
                                <tr>
                                    <td colspan="6" class="coop-vazio">Nenhuma cooperativa encontrada.</td>
                                </tr>
                                <% } else { for (Cooperativa c : itens) {
                                       String status = String.valueOf(c.getStatus());
                                %>
                                <tr class="coop-linha-link" onclick="location.href='cooperativa?pagina=detalhe&id=<%= c.getIdCooperativa() %>'">
                                    <td>
                                        <a class="coop-name" href="cooperativa?pagina=detalhe&id=<%= c.getIdCooperativa() %>">
                                            <span class="coop-avatar" aria-hidden="true">
                                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 22V4a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v18Z"/><path d="M6 12H4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h2"/><path d="M18 9h2a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2h-2"/><path d="M10 6h4"/><path d="M10 10h4"/><path d="M10 14h4"/><path d="M10 18h4"/></svg>
                                            </span>
                                            <span><%= h(c.getNome()) %><small><%= h(c.getCnpj()) %></small></span>
                                        </a>
                                    </td>
                                    <td><%= h(c.getCidade()) %>/<%= h(c.getEstado()) %></td>
                                    <td><span class="coop-pill <%= "INATIVA".equals(status) ? "inativa" : "" %>"><%= h(status) %></span></td>
                                    <td><%= c.getRelacionamentos().getQtdCooperados() %></td>
                                    <td><%= c.getDataCadastro() == null ? "—" : c.getDataCadastro().format(DATA_BR) %></td>
                                    <td class="coop-acoes">
                                        <a class="coop-icone-btn editar" title="Editar cooperativa" aria-label="Editar <%= h(c.getNome()) %>" href="cooperativa?pagina=editar&id=<%= c.getIdCooperativa() %>" onclick="event.stopPropagation()">
                                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>
                                        </a>
                                        <% if ("INATIVA".equals(status)) { %>
                                        <form method="post" action="cooperativa" style="display:inline" onclick="event.stopPropagation()">
                                            <input type="hidden" name="acao" value="reativar">
                                            <input type="hidden" name="id_cooperativa" value="<%= c.getIdCooperativa() %>">
                                            <button type="submit" class="coop-icone-btn reativar" title="Reativar cooperativa" aria-label="Reativar <%= h(c.getNome()) %>">
                                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 12a9 9 0 1 0 3-6.7L3 8"/><path d="M3 3v5h5"/></svg>
                                            </button>
                                        </form>
                                        <% } %>
                                        <button type="button" class="coop-icone-btn excluir" title="Excluir cooperativa" aria-label="Excluir <%= h(c.getNome()) %>"
                                                data-id="<%= c.getIdCooperativa() %>" data-nome="<%= h(c.getNome()) %>" data-status="<%= h(status) %>"
                                                onclick="event.stopPropagation(); abrirExcluirCoop(this)">
                                            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
                                        </button>
                                    </td>
                                </tr>
                                <% } } %>
                            </tbody>
                        </table>
                    </div>
                </section>
            </main>
        </div>
        <%@ include file="/cooperativa-modal-excluir.jspf" %>
        <script src="${pageContext.request.contextPath}/scripts/busca-ao-vivo.js?v=2"></script>
        <script src="${pageContext.request.contextPath}/scripts/tabela-responsiva.js?v=1"></script>
        <%@ include file="/operacao-ui.jspf" %>
    </body>
</html>
