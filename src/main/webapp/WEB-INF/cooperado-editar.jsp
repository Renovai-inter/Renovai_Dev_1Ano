<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.principais.Cooperado" %>
<%
    Cooperado c = (Cooperado) request.getAttribute("cooperado");
    Map<Long, String> cooperativas = (Map<Long, String>) request.getAttribute("cooperativas");
    int totalColetas = (Integer) request.getAttribute("totalColetas");
    String erro = (String) request.getAttribute("erro");
    String senhaNova = (String) request.getAttribute("senhaNova");

    String[] atividades = {"Coleta externa", "Financeiro", "Pesagem", "Direção de veículo", "Prensagem", "Triagem"};
    String[] statusOpcoes = {"Ativo", "Afastado", "Inativo"};
%>
<html>
<head>
    <title>Editar cooperado</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">
        <form method="post" action="cooperado">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<%= c.getIdCooperado() %>">

            <div class="topo">
                <div class="titulo-volta">
                    <a href="cooperado?acao=ver&id=<%= c.getIdCooperado() %>" class="seta">&lsaquo;</a>
                    <div>
                        <h1>Editar cooperado <%= esc(c.getNomeCompleto()) %></h1>
                        <p>Cooperados - <%= esc(c.getCodigoCooperado()) %></p>
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

            <% if (senhaNova != null) { %>
                <div class="sucesso">Senha redefinida. Nova senha temporária: <b><%= esc(senhaNova) %></b>.
                    Anote agora, ela não será exibida novamente.</div>
            <% } %>

            <div class="cartao">
                <h2>Identificação</h2>
                <div class="grade">
                    <div class="campo">
                        <label for="nomeCompleto">Nome Completo</label>
                        <input type="text" id="nomeCompleto" name="nomeCompleto" value="<%= esc(c.getNomeCompleto()) %>">
                    </div>
                    <div class="campo">
                        <label for="email">E-mail</label>
                        <input type="text" id="email" name="email" value="<%= esc(c.getEmail()) %>">
                    </div>
                    <div class="campo">
                        <label for="status">Status</label>
                        <select id="status" name="status">
                            <% for (String s : statusOpcoes) { %>
                                <option value="<%= s %>" <%= s.equals(c.getStatus()) ? "selected" : "" %>><%= s %></option>
                            <% } %>
                        </select>
                    </div>
                </div>
            </div>

            <div class="cartao">
                <h2>Vínculo e funções</h2>
                <div class="grade">
                    <div class="campo">
                        <label for="idCooperativa">Cooperativa</label>
                        <select id="idCooperativa" name="idCooperativa">
                            <option value="">Selecione a cooperativa</option>
                            <% for (Map.Entry<Long, String> coop : cooperativas.entrySet()) { %>
                                <option value="<%= coop.getKey() %>" <%= coop.getKey().equals(c.getIdCooperativa()) ? "selected" : "" %>><%= esc(coop.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="campo">
                        <label for="cargo">Cargo</label>
                        <select id="cargo" name="cargo">
                            <option value="">Selecione o cargo</option>
                            <option value="Cooperado" <%= "Cooperado".equals(c.getCargo()) ? "selected" : "" %>>Cooperado</option>
                            <option value="Gestor" <%= "Gestor".equals(c.getCargo()) ? "selected" : "" %>>Gestor</option>
                        </select>
                    </div>
                </div>

                <div class="campo topo-chips"><label>Atividades (múltipla escolha)</label></div>
                <div class="chips">
                    <% for (int i = 0; i < atividades.length; i++) { %>
                        <input type="checkbox" id="a<%= i %>" name="atividades" value="<%= atividades[i] %>" <%= c.getAtividades().contains(atividades[i]) ? "checked" : "" %>>
                        <label for="a<%= i %>"><%= atividades[i] %></label>
                    <% } %>
                </div>
            </div>

            <div class="cartao">
                <h2>Conta de acesso</h2>
                <div class="grade">
                    <div class="dado"><small>Usuário</small><span><%= esc(c.getNomeUsuario()) %></span></div>
                    <div class="dado"><small>Senha</small>
                        <div class="linha-senha">
                            <span class="senha-temp"><%= c.isSenhaTemporaria() ? "SenhaAPI temporária pendente de troca" : "SenhaAPI definida pelo cooperado" %></span>
                            <button type="submit" form="formSenha" class="botao claro">Redefinir senha</button>
                        </div>
                    </div>
                </div>
                <p class="aviso">Por segurança, o painel nunca exibe a senha real, apenas o indicador de senha temporária.</p>
            </div>
        </form>

        <%-- Formulario separado: um formulario nao pode ficar dentro de outro --%>
        <form id="formSenha" method="post" action="cooperado">
            <input type="hidden" name="acao" value="redefinirSenha">
            <input type="hidden" name="id" value="<%= c.getIdCooperado() %>">
        </form>
    </div>
</div>

<%@ include file="modal-excluir.jspf" %>
</body>
</html>