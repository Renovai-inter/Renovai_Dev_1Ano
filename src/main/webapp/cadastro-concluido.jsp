<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Conta criada | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">
</head>
<body>
<main class="auth">

    <section class="auth-form">
        <h1>Conta criada!</h1>
        <p class="subtitulo">Tudo certo. Agora é só acessar o app do Renovaí com o e-mail e a senha que você cadastrou.</p>
        <div class="aviso sucesso" role="status">Cadastro concluído com sucesso.</div>

        <a href="https://renovai-38nc.onrender.com/" class="btn-principal"
           style="display:block;text-align:center;line-height:58px;text-decoration:none">Voltar para a Landing Page</a>
    </section>

    <aside class="mascote" aria-hidden="true">
        <img src="${ctx}/img/mascote.png" alt="" onerror="this.style.display='none'">
    </aside>
</main>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
