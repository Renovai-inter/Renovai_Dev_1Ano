<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.Cooperado, java.time.format.DateTimeFormatter" %>
<%
    Cooperado c = (Cooperado) request.getAttribute("cooperado");
    List<Long> permissoesDoCooperado = (List<Long>) request.getAttribute("permissoesDoCooperado");
    Map<Long, String> todasPermissoes = (Map<Long, String>) request.getAttribute("todasPermissoes");
    int totalColetas = (Integer) request.getAttribute("totalColetas");
    String erro = (String) request.getAttribute("erro");

    String dataCadastro = c.getDataCadastro() == null ? "-" : c.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    String[] atividades = {"Coleta externa", "Financeiro", "Pesagem", "Direção de veículo", "Prensagem", "Triagem"};
%>
<html>
<head>
    <title><%= esc(c.getNomeCompleto()) %></title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">
        <div class="topo">
            <div class="titulo-volta">
                <a href="cooperado" class="seta">&lsaquo;</a>
                <div>
                    <h1><%= esc(c.getNomeCompleto()) %></h1>
                    <p>Cooperados - <%= esc(c.getCodigoCooperado()) %></p>
                </div>
            </div>
            <div class="acoes">
                <a href="cooperado?acao=editar&id=<%= c.getIdCooperado() %>" class="botao claro">Editar</a>
                <a href="#excluir" class="botao vinho">Excluir</a>
            </div>
        </div>

        <% if (erro != null) { %>
            <div class="erro"><%= esc(erro) %></div>
        <% } %>

        <div class="duas-colunas">
            <div class="cartao">
                <h2>Identificação</h2>
                <div class="grade">
                    <div class="dado"><small>Nome completo</small><span><%= esc(c.getNomeCompleto()) %></span></div>
                    <div class="dado"><small>Código</small><span><%= esc(c.getCodigoCooperado()) %></span></div>
                    <div class="dado"><small>E-mail</small><span><%= esc(c.getEmail()) %></span></div>
                    <div class="dado"><small>Data de cadastro</small><span><%= dataCadastro %></span></div>
                    <div class="dado"><small>Status</small><span class="selo"><%= esc(c.getStatus()) %></span></div>
                </div>
            </div>

            <div class="cartao">
                <h2>Vínculo e funções</h2>
                <div class="grade">
                    <div class="dado"><small>Cooperativa</small><span><%= esc(c.getNomeCooperativa()) %></span></div>
                    <div class="dado"><small>Cargo</small><span><%= c.getCargo() == null ? "-" : esc(c.getCargo()) %></span></div>
                </div>
                <div class="dado topo-chips"><small>Atividades</small></div>
                <div class="chips so-leitura">
                    <% for (String a : atividades) { if (c.getAtividades().contains(a)) { %>
                        <label style="background:#dff0cf;border-color:#6b9065;"><%= a %></label>
                    <% } } %>
                    <% if (c.getAtividades().isEmpty()) { %><label>Nenhuma atividade</label><% } %>
                </div>
            </div>
        </div>

        <div class="cartao">
            <h2>Permissões por módulo</h2>
            <div class="permissoes">
                <% for (Map.Entry<Long, String> perm : todasPermissoes.entrySet()) { %>
                    <label class="perm"><%= esc(perm.getValue()) %>
                        <input type="checkbox" disabled <%= permissoesDoCooperado.contains(perm.getKey()) ? "checked" : "" %>>
                        <span class="chave"></span>
                    </label>
                <% } %>
            </div>
        </div>

        <div class="cartao">
            <h2>Conta de acesso</h2>
            <div class="grade">
                <div class="dado"><small>Usuário</small><span><%= esc(c.getNomeUsuario()) %></span></div>
                <div class="dado"><small>Senha</small>
                    <span class="senha-temp"><%= c.isSenhaTemporaria() ? "Senha temporária pendente de troca" : "Senha definida pelo cooperado" %></span>
                </div>
            </div>
            <p class="aviso">Por segurança, o painel nunca exibe a senha real, apenas o indicador de senha temporária.</p>
        </div>
    </div>
</div>

<%@ include file="modal-excluir.jspf" %>
</body>
</html>