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
                    <a class="coop-btn" href="cooperativa?pagina=nova">▦ &nbsp; Cadastrar cooperativa</a>
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
                                <th>Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if(itens==null||itens.isEmpty()){ %>
                                <tr>
                                <td colspan="6">Nenhuma cooperativa encontrada.</td>
                                </tr>
                                <% }else{for(Cooperativa c:itens){ %>
                                <tr>
                                <td>
                                <a class="coop-name" href="cooperativa?pagina=detalhe&id=<%=c.getIdCooperativa()%>">▣ &nbsp;<%=h(c.getNomePublico())%>
                                </a>
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
                        <td>
                            <a title="Ver detalhes" href="cooperativa?pagina=detalhe&id=<%=c.getIdCooperativa()%>">✎</a>
                        </td>
                    </tr>
                    <% }} %>
                </tbody>
            </table>
        </div>
    </section>
</main>
</div>
</body>
</html>
