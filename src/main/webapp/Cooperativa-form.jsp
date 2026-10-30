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
private String campo(jakarta.servlet.http.HttpServletRequest request, String nome, Object atual) {
    String enviado = request.getParameter(nome);
    if (enviado != null) return h(enviado);
    return h(atual);
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
                <% Cooperativa cooperativa = (Cooperativa) request.getAttribute("cooperativa"); boolean edicao = cooperativa != null; %>
                <header class="coop-top">
                    <div>
                        <h1><%= edicao ? "Editar cooperativa" : "Nova cooperativa" %></h1>
                        <p>
                        <a href="cooperativa">‹ Voltar para cooperativas</a>
                    </p>
                </div>
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
            <form action="cooperativa" method="post">
                <input type="hidden" name="acao" value="<%= edicao ? "atualizar" : "cadastrar" %>">
                <% if (edicao) { %><input type="hidden" name="id_cooperativa" value="<%= cooperativa.getIdCooperativa() %>"><% } %>
                <section class="coop-card">
                    <h3>Dados institucionais</h3>
                    <div class="coop-grid">
                        <label>Nome da cooperativa *<input type="text" name="nome" value="<%= campo(request, "nome", edicao ? cooperativa.getNome() : null) %>" required maxlength="150">
                        </label>
                        <label>Nome público *<input type="text" name="nome_publico" value="<%= campo(request, "nome_publico", edicao ? cooperativa.getNomePublico() : null) %>" required maxlength="150">
                        </label>
                        <label>CNPJ *<input type="text" name="cnpj" value="<%= campo(request, "cnpj", edicao ? cooperativa.getCnpj() : null) %>" required>
                        </label>
                        <label>E-mail institucional *<input type="email" name="email_institucional" value="<%= campo(request, "email_institucional", edicao ? cooperativa.getEmailInstitucional() : null) %>" required maxlength="150">
                        </label>
                        <label>Telefone / WhatsApp *<input type="text" name="telefone_whatsapp" value="<%= campo(request, "telefone_whatsapp", edicao ? cooperativa.getTelefoneWhatsapp() : null) %>" required>
                        </label>
                        <label>CEP *<input type="text" name="cep" value="<%= campo(request, "cep", edicao ? cooperativa.getCep() : null) %>" required>
                        </label>
                        <label>Endereço *<input type="text" name="endereco" value="<%= campo(request, "endereco", edicao ? cooperativa.getEndereco() : null) %>" required maxlength="255">
                        </label>
                        <label>Cidade *<input type="text" name="cidade" value="<%= campo(request, "cidade", edicao ? cooperativa.getCidade() : null) %>" required maxlength="100">
                        </label>
                        <label>UF *<input type="text" name="estado" value="<%= campo(request, "estado", edicao ? cooperativa.getEstado() : null) %>" required maxlength="2">
                        </label>
                        <label>Logo (URL)<input type="url" name="logo_url" value="<%= campo(request, "logo_url", edicao ? cooperativa.getLogoUrl() : null) %>" maxlength="500">
                        </label>
                        <label class="coop-wide">Descrição institucional<textarea name="descricao_institucional" rows="3"><%= campo(request, "descricao_institucional", edicao ? cooperativa.getDescricaoInstitucional() : null) %></textarea>
                    </label>
                </div>
            </section>
            <% if (!edicao) { %><section class="coop-card">
                <h3>Gestor responsável</h3>
                <p>Informe os dados do gestor que terá acesso à cooperativa.</p>
                <div class="coop-grid">
                    <label>Nome completo *<input type="text" name="gestor_nome_completo" value="<%= campo(request, "gestor_nome_completo", null) %>" required>
                    </label>
                    <label>Nome de usuário<input type="text" name="gestor_nome_usuario" value="<%= campo(request, "gestor_nome_usuario", null) %>">
                    </label>
                    <label>E-mail *<input type="email" name="gestor_email" value="<%= campo(request, "gestor_email", null) %>" required>
                    </label>
                    <label>Senha temporária *<input type="password" name="gestor_senha_temporaria" required>
                    </label>
                </div>
            </section>
            <% } %>
            <div class="coop-actions">
                <a href="cooperativa">Cancelar</a>
                <button class="coop-btn" type="submit"><%= edicao ? "Salvar alterações" : "Criar cooperativa" %></button>
            </div>
        </form>
        <% if (request.getAttribute("erro") != null) { %><script>
            const primeiroInvalido = document.querySelector('form input:invalid, form textarea:invalid');
            if (primeiroInvalido) primeiroInvalido.focus();
        </script><% } %>
    </main>
</div>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
