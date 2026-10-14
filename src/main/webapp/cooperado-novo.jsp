<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%
    Map<Long, String> cooperativas = (Map<Long, String>) request.getAttribute("cooperativas");
    Map<Long, String> todasPermissoes = (Map<Long, String>) request.getAttribute("todasPermissoes");
    String erro = (String) request.getAttribute("erro");

    // Se o formulario voltou por erro, mantem o que o usuario ja tinha digitado
    boolean voltouErro = "salvar".equals(request.getParameter("acao"));
    String nome = voltouErro ? request.getParameter("nomeCompleto") : "";
    String email = voltouErro ? request.getParameter("email") : "";
    String idCoopSel = voltouErro ? request.getParameter("idCooperativa") : "";
    String cargoSel = voltouErro ? request.getParameter("cargo") : "";

    List<String> atividadesMarcadas = new ArrayList<String>();
    List<String> permissoesMarcadas = new ArrayList<String>();
    if (voltouErro) {
        if (request.getParameterValues("atividades") != null) atividadesMarcadas = Arrays.asList(request.getParameterValues("atividades"));
        if (request.getParameterValues("permissoes") != null) permissoesMarcadas = Arrays.asList(request.getParameterValues("permissoes"));
    }

    String[] atividades = {"Coleta externa", "Financeiro", "Pesagem", "Direção de veículo", "Prensagem", "Triagem"};
%>
<html>
<head>
    <title>Novo cooperado</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">
        <div class="topo">
            <div>
                <h1>Novo cooperado</h1>
                <a href="cooperado" class="voltar">&lt; Voltar para cooperados</a>
            </div>
        </div>

        <% if (erro != null) { %>
            <div class="erro"><%= esc(erro) %></div>
        <% } %>

        <form method="post" action="cooperado">
            <input type="hidden" name="acao" value="salvar">

            <div class="cartao">
                <h2 class="linha">Dados do Cooperado</h2>
                <div class="grade-form">

                    <div class="campo">
                        <label for="idCooperativa">Cooperativa</label>
                        <select id="idCooperativa" name="idCooperativa">
                            <option value="">Selecione a cooperativa</option>
                            <% if (cooperativas != null) { for (Map.Entry<Long, String> coop : cooperativas.entrySet()) { %>
                                <option value="<%= coop.getKey() %>" <%= String.valueOf(coop.getKey()).equals(idCoopSel) ? "selected" : "" %>><%= esc(coop.getValue()) %></option>
                            <% } } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="nomeCompleto">Nome Completo</label>
                        <input type="text" id="nomeCompleto" name="nomeCompleto" placeholder="Insira o nome completo do cooperado" value="<%= esc(nome) %>">
                    </div>

                    <div class="campo">
                        <label for="cargo">Cargo (opcional)</label>
                        <select id="cargo" name="cargo">
                            <option value="">Selecione o cargo</option>
                            <option value="Cooperado" <%= "Cooperado".equals(cargoSel) ? "selected" : "" %>>Cooperado</option>
                            <option value="Gestor" <%= "Gestor".equals(cargoSel) ? "selected" : "" %>>Gestor</option>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="email">E-mail</label>
                        <input type="text" id="email" name="email" placeholder="Insira o e-mail do cooperado" value="<%= esc(email) %>">
                    </div>

                </div>
            </div>

            <div class="cartao">
                <h2 class="linha">Atividades <small>(múltipla escolha)</small></h2>
                <div class="chips">
                    <% for (int i = 0; i < atividades.length; i++) { %>
                        <input type="checkbox" id="a<%= i %>" name="atividades" value="<%= atividades[i] %>" <%= atividadesMarcadas.contains(atividades[i]) ? "checked" : "" %>>
                        <label for="a<%= i %>"><%= atividades[i] %></label>
                    <% } %>
                </div>
            </div>

            <div class="cartao">
                <h2 class="linha">Permissões por módulo</h2>
                <div class="permissoes">
                    <% if (todasPermissoes != null) { for (Map.Entry<Long, String> perm : todasPermissoes.entrySet()) { %>
                        <label class="perm"><%= esc(perm.getValue()) %>
                            <input type="checkbox" name="permissoes" value="<%= perm.getKey() %>"
                                <%= (!voltouErro || permissoesMarcadas.contains(String.valueOf(perm.getKey()))) ? "checked" : "" %>>
                            <span class="chave"></span>
                        </label>
                    <% } } %>
                </div>
            </div>

            <div class="rodape-form">
                <a href="cooperado" class="botao claro">Cancelar</a>
                <button type="submit" class="botao verde">Cadastrar cooperado</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>