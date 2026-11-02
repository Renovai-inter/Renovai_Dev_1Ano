<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.math.BigDecimal,java.time.format.DateTimeFormatter,java.util.ArrayList,model.principais.Coleta,model.principais.Cooperativa,model.principais.Cooperado,model.principais.Negociacao" %>
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

// "ENTREGA_COOPERATIVA" -> "Entrega cooperativa"
private String legivel(Object valor) {
    if (valor == null) return "—";
    String t = valor.toString().replace('_', ' ').trim().toLowerCase();
    return t.isEmpty() ? "—" : t.substring(0, 1).toUpperCase() + t.substring(1);
}
private String kg(BigDecimal valor) {
    return valor == null ? "—" : valor.stripTrailingZeros().toPlainString() + " kg";
}
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
                <%
                    Cooperativa c = (Cooperativa) request.getAttribute("cooperativa");
                    ArrayList<Cooperado> membros = (ArrayList<Cooperado>) request.getAttribute("cooperados");
                    ArrayList<Coleta> coletas = (ArrayList<Coleta>) request.getAttribute("coletas");
                    ArrayList<Negociacao> negociacoes = (ArrayList<Negociacao>) request.getAttribute("negociacoes");
                    if (c != null) {
                        String status = String.valueOf(c.getStatus());
                        boolean inativa = "INATIVA".equals(status);
                        String ctx = request.getContextPath();
                %>
                <header class="coop-top">
                    <div>
                        <h1><%= h(c.getNomePublico()) %></h1>
                        <p>
                            <a href="cooperativa">‹ Voltar para cooperativas</a>
                            <span class="coop-pill <%= inativa ? "inativa" : "" %>"><%= h(status) %></span>
                        </p>
                    </div>
                    <div class="coop-actions">
                        <a class="coop-btn claro" href="cooperativa?pagina=editar&id=<%= c.getIdCooperativa() %>">Editar</a>
                        <% if (inativa) { %>
                        <form method="post" action="cooperativa">
                            <input type="hidden" name="acao" value="reativar">
                            <input type="hidden" name="id_cooperativa" value="<%= c.getIdCooperativa() %>">
                            <button class="coop-btn claro" type="submit">Reativar</button>
                        </form>
                        <% } %>
                        <button class="coop-btn danger" type="button" data-id="<%= c.getIdCooperativa() %>"
                                data-nome="<%= h(c.getNome()) %>" data-status="<%= h(status) %>"
                                onclick="abrirExcluirCoop(this)">Excluir</button>
                    </div>
                </header>
                <% if (request.getAttribute("erro") != null) { %>
                <div class="coop-alert erro" role="alert"><%= h(request.getAttribute("erro")) %></div>
                <% } %>
                <% if (request.getAttribute("mensagem") != null) { %>
                <div class="coop-alert" role="status"><%= h(request.getAttribute("mensagem")) %></div>
                <% } %>

                <div class="coop-stats">
                    <div>Cooperados<strong><%= membros == null ? 0 : membros.size() %></strong></div>
                    <div>Rotas<strong><%= h(request.getAttribute("totalRotas")) %></strong></div>
                    <div>Coletas<strong><%= h(request.getAttribute("totalColetas")) %></strong></div>
                    <div>Negociações<strong><%= h(request.getAttribute("totalNegociacoes")) %></strong></div>
                </div>

                <div class="coop-grid">
                    <section class="coop-card">
                        <h3>Identificação</h3>
                        <p><b>Razão social:</b> <%= h(c.getNome()) %></p>
                        <p><b>CNPJ:</b> <%= h(c.getCnpj()) %></p>
                        <p><b>Cidade:</b> <%= h(c.getCidade()) %>/<%= h(c.getEstado()) %></p>
                        <p><b>Cadastro:</b> <%= c.getDataCadastro() == null ? "—" : c.getDataCadastro().format(DATA_BR) %></p>
                        <p><b>Descrição:</b> <%= h(c.getDescricaoInstitucional()) %></p>
                    </section>
                    <section class="coop-card">
                        <h3>Contato e endereço</h3>
                        <p><b>E-mail:</b> <%= h(c.getEmailInstitucional()) %></p>
                        <p><b>WhatsApp:</b> <%= h(c.getTelefoneWhatsapp()) %></p>
                        <p><b>Endereço:</b> <%= h(c.getEndereco()) %></p>
                        <p><b>CEP:</b> <%= h(c.getCep()) %></p>
                    </section>
                </div>

                <section class="coop-card">
                    <div class="coop-abas" role="tablist">
                        <button type="button" class="ativa" role="tab" aria-selected="true" data-aba="cooperados">Cooperados</button>
                        <button type="button" role="tab" aria-selected="false" data-aba="coletas">Coletas</button>
                        <button type="button" role="tab" aria-selected="false" data-aba="negociacoes">Negociações</button>
                    </div>

                    <div class="coop-overflow coop-painel" id="aba-cooperados" role="tabpanel">
                        <table class="coop-table">
                            <thead><tr><th>Código</th><th>Nome</th><th>Cargo</th><th>Status</th></tr></thead>
                            <tbody>
                                <% if (membros == null || membros.isEmpty()) { %>
                                <tr><td colspan="4" class="coop-vazio">Nenhum cooperado cadastrado.</td></tr>
                                <% } else { for (Cooperado m : membros) { %>
                                <tr class="coop-linha-link" data-href="<%= ctx %>/cooperado?acao=ver&id=<%= m.getIdCooperado() %>">
                                    <td><%= h(m.getCodigoCooperado()) %></td>
                                    <td><%= h(m.getNomeCompleto()) %></td>
                                    <td><%= h(legivel(m.getCargo() == null || m.getCargo().isEmpty() ? "Cooperado" : m.getCargo())) %></td>
                                    <td><span class="coop-pill"><%= h(m.getStatus()) %></span></td>
                                </tr>
                                <% } } %>
                            </tbody>
                        </table>
                    </div>

                    <div class="coop-overflow coop-painel" id="aba-coletas" role="tabpanel" hidden>
                        <table class="coop-table">
                            <thead><tr><th>Código</th><th>Tipo</th><th>Data</th><th>Peso total</th><th>Status</th></tr></thead>
                            <tbody>
                                <% if (coletas == null || coletas.isEmpty()) { %>
                                <tr><td colspan="5" class="coop-vazio">Nenhuma coleta registrada.</td></tr>
                                <% } else { for (Coleta col : coletas) { %>
                                <tr class="coop-linha-link" data-href="<%= ctx %>/coleta?acao=detalhes&id=<%= col.getIdColeta() %>">
                                    <td>CO-<%= col.getIdColeta() %></td>
                                    <td><%= h(legivel(col.getTipo())) %></td>
                                    <td><%= col.getDataAgendada() == null ? "—" : col.getDataAgendada().format(DATA_BR) %></td>
                                    <td><%= kg(col.getPesoTotalKg()) %></td>
                                    <td><span class="coop-pill"><%= h(legivel(col.getStatus())) %></span></td>
                                </tr>
                                <% } } %>
                            </tbody>
                        </table>
                    </div>

                    <div class="coop-overflow coop-painel" id="aba-negociacoes" role="tabpanel" hidden>
                        <table class="coop-table">
                            <thead><tr><th>Código</th><th>Empresa</th><th>Material</th><th>Quantidade</th><th>Status</th></tr></thead>
                            <tbody>
                                <% if (negociacoes == null || negociacoes.isEmpty()) { %>
                                <tr><td colspan="5" class="coop-vazio">Nenhuma negociação registrada.</td></tr>
                                <% } else { for (Negociacao n : negociacoes) { %>
                                <tr class="coop-linha-link" data-href="<%= ctx %>/negociacao?acao=ver&id=<%= n.getIdNegociacao() %>">
                                    <td>NE-<%= String.format("%04d", n.getIdNegociacao()) %></td>
                                    <td><%= h(n.getNomeEmpresa()) %></td>
                                    <td><%= h(n.getNomeMaterial()) %></td>
                                    <td><%= kg(n.getQuantidadeKg()) %></td>
                                    <td><span class="coop-pill"><%= h(legivel(n.getStatus())) %></span></td>
                                </tr>
                                <% } } %>
                            </tbody>
                        </table>
                    </div>
                </section>
                <% } %>
            </main>
        </div>
        <% if (c != null) { %><%@ include file="/cooperativa-modal-excluir.jspf" %><% } %>
        <script>
            // Abas: mostra só o painel escolhido
            document.querySelectorAll('.coop-abas button').forEach(function (aba) {
                aba.addEventListener('click', function () {
                    document.querySelectorAll('.coop-abas button').forEach(function (b) {
                        b.classList.toggle('ativa', b === aba);
                        b.setAttribute('aria-selected', b === aba ? 'true' : 'false');
                    });
                    document.querySelectorAll('.coop-painel').forEach(function (p) {
                        p.hidden = p.id !== 'aba-' + aba.dataset.aba;
                    });
                });
            });
            // Linhas das tabelas levam para o registro
            document.querySelectorAll('.coop-linha-link').forEach(function (linha) {
                linha.addEventListener('click', function () { location.href = linha.dataset.href; });
            });
        </script>
        <%@ include file="/operacao-ui.jspf" %>
    </body>
</html>
