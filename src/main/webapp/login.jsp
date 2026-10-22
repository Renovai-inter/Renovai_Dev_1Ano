<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Entrar | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/auth.css">
</head>
<body>
<main class="auth">

    <section class="auth-form">
        <h1>Bem vindo de volta!</h1>
        <p class="subtitulo">Entre para acompanhar os registros e atividades do Renovaí.</p>

        <c:if test="${param.cadastro == 'ok'}">
            <div class="aviso sucesso" role="status">Conta criada com sucesso. Entre para continuar.</div>
        </c:if>
        <c:if test="${param.saiu == '1'}">
            <div class="aviso sucesso" role="status">Você saiu da sua conta.</div>
        </c:if>
        <c:if test="${not empty erro}">
            <div class="aviso erro" role="alert"><c:out value="${erro}"/></div>
        </c:if>

        <form method="post" action="${ctx}/login" accept-charset="UTF-8">

            <label class="campo">
                <span class="sr-only">E-mail</span>
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4-8 5-8-5V6l8 5 8-5v2z"/></svg>
                <input type="email" name="email" placeholder="E-mail" required autocomplete="email"
                       value="<c:out value='${email}'/>">
            </label>

            <label class="campo">
                <span class="sr-only">Senha</span>
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zM9 8V6c0-1.66 1.34-3 3-3s3 1.34 3 3v2H9z"/></svg>
                <input type="password" name="senha" placeholder="Senha" required autocomplete="current-password">
            </label>

            <button type="submit" class="btn-principal" style="margin-top:8px">Entrar</button>
        </form>

    </section>

    <aside class="mascote" aria-hidden="true">
        <img src="${ctx}/img/mascote.png" alt="" onerror="this.style.display='none'">
    </aside>
</main>
</body>
</html>