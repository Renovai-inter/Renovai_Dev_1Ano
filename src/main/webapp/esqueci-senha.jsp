<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Esqueci minha senha | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/auth.css">
</head>
<body>
<main class="auth">

    <section class="auth-form">
        <h1>Esqueceu a senha?</h1>
        <p class="subtitulo">Informe o e-mail da sua conta de administrador e enviaremos um link para criar uma nova senha.</p>

        <c:choose>
            <c:when test="${enviado}">
                <div class="aviso sucesso" role="status">
                    Recebemos sua solicitação. Se o e-mail pertencer a um administrador e o serviço de e-mail estiver disponível,
                    você receberá um link de redefinição válido por 30 minutos. Confira também a caixa de spam.
                </div>
                <p class="alt"><a href="${ctx}/login">Voltar para o login</a></p>
            </c:when>
            <c:otherwise>
                <form method="post" action="${ctx}/esqueci-senha" accept-charset="UTF-8">

                    <label class="campo">
                        <span class="sr-only">E-mail</span>
                        <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4-8 5-8-5V6l8 5 8-5v2z"/></svg>
                        <input type="email" name="email" placeholder="E-mail" required autocomplete="email">
                    </label>

                    <button type="submit" class="btn-principal" style="margin-top:8px">Enviar link</button>
                </form>
                <p class="alt"><a href="${ctx}/login">Voltar para o login</a></p>
            </c:otherwise>
        </c:choose>
    </section>

    <aside class="mascote" aria-hidden="true">
        <img src="${ctx}/img/mascote.png" alt="" onerror="this.style.display='none'">
    </aside>
</main>
</body>
</html>
