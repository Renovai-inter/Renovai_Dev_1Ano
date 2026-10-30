<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList,model.principais.Cooperativa,model.principais.Cooperado" %>
<%!
private String h(Object value) {
if (value == null) return "";
return value.toString()
.replace("&", "&amp;")
.replace("<", "&lt;")
.replace(">", "&gt;")
.replace("\"", "&quot;");
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
                <% ArrayList<Cooperativa> itens=(ArrayList<Cooperativa>)request.getAttribute("cooperativas"); %>
                <header class="coop-top">
                    <div>
                        <h1>Cooperativas</h1>
                        <p>Cooperativas de reciclagem cadastradas na plataforma</p>
                    </div>
                    <a class="coop-btn" href="cooperativa?pagina=nova">Nova cooperativa</a>
                </header>
                <% if(request.getAttribute("erro")!=null){ %>
                <div class="coop-alert erro">
                    <%=h(request.getAttribute("erro"))%>
                </div>
                <% } %>
                <% if(request.getAttribute("mensagem")!=null){ %>
                <div class="coop-alert">
                    <%=h(request.getAttribute("mensagem"))%>
                </div>
                <% } %>
                <section class="coop-card">
                    <form method="get" action="cooperativa" class="coop-search">
                        <input name="busca" value="<%=h(request.getParameter("busca"))%>" placeholder="⌕  Buscar por nome ou código...">
                        <button class="coop-btn" type="submit">Buscar</button>
                    </form>
                    <div class="coop-overflow">
                        <table class="coop-table">
                            <thead>
                                <tr>
                                <th>Nome</th>
                                <th>Cidade / UF</th>
                                <th>Status</th>
                                <th>Cooperados</th>
                                <th>Cadastro</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if(itens==null||itens.isEmpty()){ %>
                                <tr>
                                <td colspan="5">Nenhuma cooperativa encontrada.</td>
                                </tr>
                                <% }else{for(Cooperativa c:itens){ %>
                                <tr>
                                <td>
                                <div class="coop-identificacao">
                                    <a class="coop-name" href="cooperativa?pagina=detalhe&id=<%=c.getIdCooperativa()%>">
                                        <svg class="coop-icone" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 21h18"/><path d="M5 21V7l8-4v18"/><path d="M19 21V11l-6-4"/><path d="M9 9v.01M9 12v.01M9 15v.01M9 18v.01"/></svg>
                                        <%=h(c.getNomePublico())%>
                                    </a>
                                    <a class="editar" title="Editar cooperativa" aria-label="Editar cooperativa" href="cooperativa?pagina=editar&id=<%=c.getIdCooperativa()%>">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/></svg>
                                    </a>
                                </div>
                                <small>
                                <%=h(c.getNome())%>
                                </small>
                            </td>
                            <td>
                                <%=h(c.getCidade())%>/<%=h(c.getEstado())%>
                            </td>
                            <td>
                                <span class="coop-pill">
                                <%=h(c.getStatus())%>
                            </span>
                        </td>
                        <td>
                            <%=c.getRelacionamentos().getQtdCooperados()%>
                        </td>
                        <td>
                            <%=c.getDataCadastro()==null?"—":h(c.getDataCadastro().toLocalDate())%>
                        </td>
                    </tr>
                    <% }} %>
                </tbody>
            </table>
        </div>
    </section>
</main>
</div>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
