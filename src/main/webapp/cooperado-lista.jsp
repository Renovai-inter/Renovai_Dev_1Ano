<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="icon" href="${ctx}/images/Nimo.svg" type="image/svg+xml">
<%@ page import="java.util.ArrayList, model.principais.Cooperado" %>
<%!
    // "GESTOR" vira "Gestor", "ATIVO" vira "Ativo" (o Figma mostra so a primeira letra maiuscula)
    private String capitalizar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "";
        }
        String t = texto.trim().toLowerCase();
        return t.substring(0, 1).toUpperCase() + t.substring(1);
    }
%>
<%
    ArrayList<Cooperado> lista = (ArrayList<Cooperado>) request.getAttribute("lista");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cooperados</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=2">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">
        <% if ("inativacao".equals(request.getParameter("sucesso"))) { %><div class="aviso-sucesso" role="status">Cooperado inativado com sucesso.</div><% } %>
        <% if ("exclusao".equals(request.getParameter("sucesso"))) { %><div class="aviso-sucesso" role="status">Cooperado excluído com sucesso.</div><% } %>
        <% if ("naoEncontrado".equals(request.getParameter("erro"))) { %><div class="aviso erro" role="alert">Cooperado não encontrado.</div><% } %>
        <% if ("acaoInvalida".equals(request.getParameter("erro"))) { %><div class="aviso erro" role="alert">Ação inválida.</div><% } %>
        <% if ("operacao".equals(request.getParameter("erro"))) { %><div class="aviso erro" role="alert">Não foi possível concluir a operação do cooperado.</div><% } %>
        <div class="topo">
            <div>
                <h1>Cooperados</h1>
                <p>Todos os cooperados da plataforma</p>
            </div>
            <a href="cooperado?acao=novo" class="botao verde botao-icone">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/>
                    <line x1="19" x2="19" y1="8" y2="14"/><line x1="22" x2="16" y1="11" y2="11"/>
                </svg>
                Cadastrar cooperado
            </a>
        </div>

        <div class="cartao cartao-lista">
            <div class="busca-caixa">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/>
                </svg>
                <input class="busca" type="text" id="busca" placeholder="Buscar por nome ou código..." autocomplete="off">
            </div>

            <div class="tabela-rolagem">
            <table class="tabela">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Usuário</th>
                        <th>Cooperativa</th>
                        <th>Cargo</th>
                        <th>Status</th>
                        <th></th>
                    </tr>
                </thead>

                <tbody>
                    <% if (lista == null || lista.isEmpty()) { %>
                        <tr class="sem-dados">
                            <td colspan="6">Nenhum cooperado cadastrado.</td>
                        </tr>
                    <% } else { for (Cooperado c : lista) {
                           String nome = c.getNomeCompleto() == null ? "" : c.getNomeCompleto();
                           // mantido caso queira voltar a usar a inicial no avatar (o Figma usa o icone de pessoa)
                           String inicial = nome.isEmpty() ? "?" : nome.substring(0, 1).toUpperCase();
                           // cargo vazio = cooperado comum (o banco guarda NULL)
                           String cargo = (c.getCargo() == null || c.getCargo().trim().isEmpty()) ? "Cooperado" : capitalizar(c.getCargo());
                           String status = capitalizar(c.getStatus());
                    %>
                        <tr class="linha-cooperado" onclick="location.href='cooperado?acao=ver&id=<%= c.getIdCooperado() %>'">
                            <td class="codigo"><%= esc(c.getCodigoCooperado()) %></td>
                            <td>
                                <div class="usuario-celula">
                                    <span class="avatar" title="<%= esc(nome) %>">
                                        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                            <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>
                                        </svg>
                                    </span>
                                    <div>
                                        <b><%= esc(nome) %></b>
                                        <small><%= esc(c.getEmail()) %></small>
                                    </div>
                                </div>
                            </td>
                            <td><%= c.getNomeCooperativa() == null ? "-" : esc(c.getNomeCooperativa()) %></td>
                            <td><%= esc(cargo) %></td>
                            <td><span class="selo selo-<%= esc(status.toLowerCase()) %>"><%= esc(status) %></span></td>
                            <td>
                                <a href="cooperado?acao=editar&id=<%= c.getIdCooperado() %>" class="editar" title="Editar" aria-label="Editar cooperado" onclick="event.stopPropagation()">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                        <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/>
                                    </svg>
                                </a>
                            </td>
                        </tr>
                    <% } } %>
                    <tr id="semResultado" class="sem-dados" style="display:none">
                        <td colspan="6">Nenhum cooperado encontrado.</td>
                    </tr>
                </tbody>
            </table>
            </div>
        </div>
    </div>

</div>
<script>
    // Tira acentos e deixa minusculo, para "joao" achar "João"
    function normalizar(texto) {
        return texto.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    }

    var campoBusca = document.getElementById('busca');
    var linhas = document.querySelectorAll('.linha-cooperado');
    var semResultado = document.getElementById('semResultado');

    // Roda a cada letra ou numero digitado
    campoBusca.addEventListener('input', function () {

        var digitado = normalizar(this.value.trim());

        // escapa caracteres especiais ( ) [ ] . * + ? para nao quebrar a expressao regular
        var padrao = new RegExp(digitado.replace(/[.*+?^$()|\[\]\\{}]/g, '\\$&'));

        var achou = 0;

        linhas.forEach(function (linha) {
            // procura no codigo (coluna 1) e no nome/e-mail (coluna 2)
            var texto = normalizar(linha.cells[0].textContent + ' ' + linha.cells[1].textContent);

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
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
