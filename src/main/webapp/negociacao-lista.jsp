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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=4">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">
        <div class="topo">
            <div>
                <h1>Negociações</h1>
                <p>Propostas e contrapropostas entre cooperativas e empresas recicladoras</p>
            </div>
            <a href="negociacao?acao=novo" class="botao verde botao-icone">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <path d="m11 17 2 2a1 1 0 1 0 3-3"/>
                    <path d="m14 14 2.5 2.5a1 1 0 1 0 3-3l-3.88-3.88a3 3 0 0 0-4.24 0l-.88.88a1 1 0 1 1-3-3l2.81-2.81a5.79 5.79 0 0 1 7.06-.87l.47.28a2 2 0 0 0 1.42.25L21 4"/>
                    <path d="m21 3 1 11h-2"/><path d="M3 3 2 14l6.5 6.5a1 1 0 1 0 3-3"/><path d="M3 4h8"/>
                </svg>
                Nova negociação
            </a>
        </div>

        <div class="cartao cartao-lista">
            <div class="barra-lista">
                <div class="busca-caixa">
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/>
                    </svg>
                    <input class="busca" type="text" id="busca" placeholder="Buscar por código..." autocomplete="off">
                </div>

                <%-- filtro por status: o servlet le o parametro "status" --%>
                <form method="get" action="negociacao">
                    <select name="status" onchange="this.form.submit()">
                        <option value="">Todos os status</option>
                        <% for (String op : statusOpcoes) { %>
                            <option value="<%= esc(op) %>" <%= op.equals(statusFiltro) ? "selected" : "" %>><%= esc(legivel(op)) %></option>
                        <% } %>
                    </select>
                </form>
            </div>

            <div class="tabela-rolagem">
            <table class="tabela tabela-auto">
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
                            <td><%= esc(n.getNomeMaterial()) %></td>
                            <td><%= kg(n.getQuantidadeKg()) %></td>
                            <td><%= moeda(n.getValorKgAtual()) %>/kg</td>
                            <td><span class="selo selo-<%= classeStatus(n.getStatus()) %>"><%= esc(legivel(n.getStatus())) %></span></td>
                            <td><%= data(n.getUltimaAtualizacao()) %></td>
                            <td class="acoes-linha">
                                <a href="negociacao?acao=editar&id=<%= n.getIdNegociacao() %>" class="editar" title="Editar" aria-label="Editar negociação" onclick="event.stopPropagation()">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                        <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/>
                                    </svg>
                                </a>
                                <%-- a lixeira abre a tela Ver ja com o modal de exclusao aberto --%>
                                <a href="negociacao?acao=ver&id=<%= n.getIdNegociacao() %>#excluir" class="excluir" title="Excluir" aria-label="Excluir negociação" onclick="event.stopPropagation()">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                        <path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                                        <line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/>
                                    </svg>
                                </a>
                            </td>
                        </tr>
                    <% } } %>
                    <tr id="semResultado" class="sem-dados" style="display:none">
                        <td colspan="9">Nenhuma negociação encontrada.</td>
                    </tr>
                </tbody>
            </table>
            </div>
        </div>
    </div>

</div>
<script>
    // Tira acentos e deixa minusculo, para a busca nao depender de maiusculas/acentos
    function normalizar(texto) {
        return texto.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    }

    var campoBusca = document.getElementById('busca');
    var linhas = document.querySelectorAll('.linha-cooperado');
    var semResultado = document.getElementById('semResultado');

    // Roda a cada letra ou numero digitado
    campoBusca.addEventListener('input', function () {

        var digitado = normalizar(this.value.trim());

        // escapa caracteres especiais para nao quebrar a expressao regular
        var padrao = new RegExp(digitado.replace(/[.*+?^$()|\[\]\\{}]/g, '\\$&'));

        var achou = 0;

        linhas.forEach(function (linha) {
            // procura no codigo (coluna 1)
            var texto = normalizar(linha.cells[0].textContent);

            if (padrao.test(texto)) {
                linha.style.display = '';
                achou++;
            } else {
                linha.style.display = 'none';
            }
        });

        semResultado.style.display = (achou === 0 && linhas.length > 0) ? '' : 'none';
    });
</script>
</body>
</html>
