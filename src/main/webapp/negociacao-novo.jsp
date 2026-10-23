<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ include file="negociacao-util.jspf" %>
<%
    Map<Long, String> cooperativas = (Map<Long, String>) request.getAttribute("cooperativas");
    Map<Long, String> empresas = (Map<Long, String>) request.getAttribute("empresas");
    Map<Long, String> materiais = (Map<Long, String>) request.getAttribute("materiais");
    List<String> statusOpcoes = (List<String>) request.getAttribute("statusOpcoes");
    List<String> autores = (List<String>) request.getAttribute("autores");
    String erro = (String) request.getAttribute("erro");

    if (cooperativas == null) cooperativas = new LinkedHashMap<Long, String>();
    if (empresas == null) empresas = new LinkedHashMap<Long, String>();
    if (materiais == null) materiais = new LinkedHashMap<Long, String>();
    if (statusOpcoes == null) statusOpcoes = new ArrayList<String>();
    if (autores == null) autores = new ArrayList<String>();

    // Se o formulario voltou por erro, mantem o que o usuario ja tinha digitado
    boolean voltouErro = "salvar".equals(request.getParameter("acao"));

    String idCoopSel = voltouErro ? request.getParameter("idCooperativa") : "";
    String idEmpSel = voltouErro ? request.getParameter("idEmpresa") : "";
    String idMatSel = voltouErro ? request.getParameter("idMaterial") : "";
    String quantidade = voltouErro ? request.getParameter("quantidade") : "";
    String valor = voltouErro ? request.getParameter("valor") : "";
    String autorSel = voltouErro ? request.getParameter("autor") : "";
    String statusSel = voltouErro ? request.getParameter("status") : "";
    String observacoes = voltouErro ? request.getParameter("observacoes") : "";

    // destaca "Negociações" no menu
    request.setAttribute("menuAtivo", "negociacoes");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nova negociação</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=4">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">

        <div class="topo topo-form">
            <div>
                <h1>Nova negociação</h1>
                <a href="negociacao" class="voltar">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="m15 18-6-6 6-6"/>
                    </svg>
                    Voltar para negociações
                </a>
            </div>
        </div>

        <% if (erro != null) { %>
            <div class="erro"><%= esc(erro) %></div>
        <% } %>

        <form method="post" action="negociacao">
            <input type="hidden" name="acao" value="salvar">

            <div class="cartao cartao-form">
                <h2 class="linha">Partes envolvidas</h2>
                <div class="grade-form">

                    <div class="campo">
                        <label for="idCooperativa">Cooperativa</label>
                        <select id="idCooperativa" name="idCooperativa">
                            <option value="">Selecione a cooperativa</option>
                            <% for (Map.Entry<Long, String> coop : cooperativas.entrySet()) { %>
                                <option value="<%= coop.getKey() %>" <%= String.valueOf(coop.getKey()).equals(idCoopSel) ? "selected" : "" %>><%= esc(coop.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="idEmpresa">Recicladora</label>
                        <select id="idEmpresa" name="idEmpresa">
                            <option value="">Selecione a recicladora</option>
                            <% for (Map.Entry<Long, String> emp : empresas.entrySet()) { %>
                                <option value="<%= emp.getKey() %>" <%= String.valueOf(emp.getKey()).equals(idEmpSel) ? "selected" : "" %>><%= esc(emp.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                </div>
            </div>

            <div class="cartao cartao-form">
                <h2 class="linha">Proposta inicial</h2>
                <div class="grade-form">

                    <div class="campo">
                        <label for="idMaterial">Material</label>
                        <select id="idMaterial" name="idMaterial">
                            <option value="">Selecione o material</option>
                            <% for (Map.Entry<Long, String> mat : materiais.entrySet()) { %>
                                <option value="<%= mat.getKey() %>" <%= String.valueOf(mat.getKey()).equals(idMatSel) ? "selected" : "" %>><%= esc(mat.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="quantidade">Quantidade (kg)</label>
                        <input type="text" id="quantidade" name="quantidade" inputmode="decimal" placeholder="0kg" value="<%= esc(quantidade) %>">
                    </div>

                    <div class="campo">
                        <label for="valor">Valor por kg (R$)</label>
                        <input type="text" id="valor" name="valor" inputmode="decimal" placeholder="R$0,00" value="<%= esc(valor) %>">
                    </div>

                    <div class="campo">
                        <label for="autor">Autor</label>
                        <select id="autor" name="autor">
                            <option value="">Selecione o autor</option>
                            <% for (String a : autores) { %>
                                <option value="<%= esc(a) %>" <%= a.equals(autorSel) ? "selected" : "" %>><%= esc(legivel(a)) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="status">Status inicial</label>
                        <select id="status" name="status">
                            <option value="">Selecione o status inicial</option>
                            <% for (String st : statusOpcoes) { %>
                                <option value="<%= esc(st) %>" <%= st.equals(statusSel) ? "selected" : "" %>><%= esc(legivel(st)) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo campo-largo">
                        <label for="observacoes">Observações</label>
                        <textarea id="observacoes" name="observacoes"><%= esc(observacoes) %></textarea>
                    </div>

                </div>
            </div>

            <div class="rodape-form">
                <a href="negociacao" class="botao botao-form botao-contorno">Cancelar</a>
                <button type="submit" class="botao botao-form botao-escuro">Criar negociação</button>
            </div>

        </form>
    </div>
</div>
</body>
</html>
