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
                <header class="coop-top">
                    <div>
                        <h1>Nova cooperativa</h1>
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
                <input type="hidden" name="acao" value="cadastrar">
                <section class="coop-card">
                    <h3>Dados institucionais</h3>
                    <div class="coop-grid">
                        <label>Nome da cooperativa *<input type="text" name="nome" required>
                        </label>
                        <label>Nome público *<input type="text" name="nome_publico" required>
                        </label>
                        <label>CNPJ<input type="text" name="cnpj" >
                        </label>
                        <label>Data de fundação<input type="date" name="data_fundacao" >
                        </label>
                        <label>E-mail institucional<input type="email" name="email_institucional" >
                        </label>
                        <label>Telefone / WhatsApp<input type="text" name="telefone_whatsapp" >
                        </label>
                        <label>CEP<input type="text" name="cep" >
                        </label>
                        <label>Endereço<input type="text" name="endereco" >
                        </label>
                        <label>Cidade *<input type="text" name="cidade" required>
                        </label>
                        <label>UF<input type="text" name="estado" >
                        </label>
                        <label>Logo (URL)<input type="url" name="logo_url" >
                        </label>
                        <label class="coop-wide">Descrição institucional<textarea name="descricao_institucional" rows="3">
                        </textarea>
                    </label>
                </div>
            </section>
            <section class="coop-card">
                <h3>Gestor responsável</h3>
                <p>Informe os dados do gestor que terá acesso à cooperativa.</p>
                <div class="coop-grid">
                    <label>Nome completo *<input type="text" name="gestor_nome_completo" required>
                    </label>
                    <label>Nome de usuário<input type="text" name="gestor_nome_usuario" >
                    </label>
                    <label>E-mail *<input type="email" name="gestor_email" required>
                    </label>
                    <label>Senha temporária *<input type="password" name="gestor_senha_temporaria" required>
                    </label>
                </div>
            </section>
            <div class="coop-actions">
                <a href="cooperativa">Cancelar</a>
                <button class="coop-btn" type="submit">Criar cooperativa</button>
            </div>
        </form>
    </main>
</div>
</body>
</html>
