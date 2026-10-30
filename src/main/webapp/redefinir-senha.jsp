<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Redefinir senha | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">

</head>
<body>
<main class="auth">

    <section class="auth-form">

        <c:choose>
            <c:when test="${tokenInvalido}">
                <h1>Link inválido</h1>
                <p class="subtitulo">Este link expirou ou já foi usado. Peça um novo para continuar.</p>
                <a href="${ctx}/esqueci-senha" class="btn-principal"
                   style="display:inline-block;text-align:center;text-decoration:none;margin-top:8px">
                    Pedir novo link
                </a>
            </c:when>
            <c:otherwise>
                <h1>Nova senha</h1>
                <p class="subtitulo">Escolha uma nova senha para a sua conta de administrador.</p>

                <c:if test="${not empty erro}">
                    <div class="aviso erro" role="alert"><c:out value="${erro}"/></div>
                </c:if>

                <form method="post" action="${ctx}/redefinir-senha" accept-charset="UTF-8">
                    <input type="hidden" name="token" value="<c:out value='${token}'/>">

                    <label class="campo">
                        <span class="sr-only">Nova senha</span>
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zM9 8V6c0-1.66 1.34-3 3-3s3 1.34 3 3v2H9z"/></svg>
                        <input type="password" name="senha" placeholder="Nova senha (mín. 8 caracteres)"
                               required minlength="8" maxlength="100" autocomplete="new-password">
                    </label>

                    <label class="campo">
                        <span class="sr-only">Confirmar senha</span>
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zM9 8V6c0-1.66 1.34-3 3-3s3 1.34 3 3v2H9z"/></svg>
                        <input type="password" name="confirmarSenha" placeholder="Confirmar nova senha"
                               required minlength="8" maxlength="100" autocomplete="new-password">
                    </label>

                    <button type="submit" class="btn-principal" style="margin-top:8px">Salvar nova senha</button>
                </form>
            </c:otherwise>
        </c:choose>
    </section>

    <aside class="mascote" aria-hidden="true">
        <img src="${ctx}/img/mascote.png" alt="" onerror="this.style.display='none'">
    </aside>
</main>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
