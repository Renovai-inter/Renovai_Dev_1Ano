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
                <% Cooperativa c=(Cooperativa)request.getAttribute("cooperativa"); ArrayList<Cooperado> membros=(ArrayList<Cooperado>)request.getAttribute("cooperados"); if(c!=null){ %>
                <header class="coop-top">
                    <div>
                        <h1>
                        <%=h(c.getNomePublico())%>
                    </h1>
                    <p>
                    <a href="cooperativa">‹ Voltar para cooperativas</a> &nbsp; <span class="coop-pill">
                    <%=h(c.getStatus())%>
                </span>
            </p>
        </div>
        <div class="coop-actions"><a class="coop-btn claro" href="cooperativa?pagina=editar&id=<%=c.getIdCooperativa()%>">Editar</a><button class="coop-btn danger" type="button" onclick="document.getElementById('excluirModal').showModal()">Excluir</button></div>
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
    <div class="coop-stats">
        <div>Cooperados<strong>
            <%=membros==null?0:membros.size()%>
        </strong>
    </div>
    <div>Rotas<strong>
        <%=h(request.getAttribute("totalRotas"))%>
    </strong>
</div>
<div>Coletas<strong>
    <%=h(request.getAttribute("totalColetas"))%>
</strong>
</div>
<div>Negociações<strong>
    <%=h(request.getAttribute("totalNegociacoes"))%>
</strong>
</div>
</div>
<div class="coop-grid">
    <section class="coop-card">
        <h3>Identificação</h3>
        <p>
        <b>Razão social:</b> <%=h(c.getNome())%>
    </p>
    <p>
    <b>CNPJ:</b> <%=h(c.getCnpj())%>
</p>
<p>
<b>Cidade:</b> <%=h(c.getCidade())%>/<%=h(c.getEstado())%>
</p>
<p>
<b>Descrição:</b> <%=h(c.getDescricaoInstitucional())%>
</p>
</section>
<section class="coop-card">
    <h3>Contato e endereço</h3>
    <p>
    <b>E-mail:</b> <%=h(c.getEmailInstitucional())%>
</p>
<p>
<b>WhatsApp:</b> <%=h(c.getTelefoneWhatsapp())%>
</p>
<p>
<b>Endereço:</b> <%=h(c.getEndereco())%>
</p>
<p>
<b>CEP:</b> <%=h(c.getCep())%>
</p>
</section>
</div>
<section class="coop-card">
    <h3>Cooperados</h3>
    <div class="coop-overflow">
        <table class="coop-table">
            <thead>
                <tr>
                    <th>Código</th>
                    <th>Nome</th>
                    <th>Cargo</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
                <% if(membros==null||membros.isEmpty()){ %>
                <tr>
                    <td colspan="4">Nenhum cooperado cadastrado.</td>
                </tr>
                <% }else{for(Cooperado m:membros){ %>
                <tr>
                    <td>
                        <%=h(m.getCodigoCooperado())%>
                    </td>
                    <td>
                        <%=h(m.getNomeCompleto())%>
                    </td>
                    <td>
                        <%=h(m.getCargo())%>
                    </td>
                    <td>
                        <span class="coop-pill">
                        <%=h(m.getStatus())%>
                    </span>
                </td>
            </tr>
            <% }} %>
        </tbody>
    </table>
</div>
</section>
<dialog id="excluirModal" class="coop-modal">
    <h3>Excluir cooperativa</h3>
    <p>Esta ação pode remover a cooperativa e afetar seus relacionamentos. Deseja continuar?</p>
    <form method="post" action="cooperativa">
        <input type="hidden" name="acao" value="excluir">
        <input type="hidden" name="id_cooperativa" value="<%=c.getIdCooperativa()%>">
        <div class="coop-actions">
            <button class="coop-btn claro" type="button" onclick="document.getElementById('excluirModal').close()">Cancelar</button>
            <button class="coop-btn danger" type="submit">Excluir</button>
        </div>
    </form>
</dialog>
<% } %>
</main>
</div>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
