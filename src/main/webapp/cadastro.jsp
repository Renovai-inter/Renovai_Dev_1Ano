<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Criar conta | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/auth.css">
    <link rel="icon" href="${ctx}/images/Nimo.svg" type="image/svg+xml">
</head>
<body>
<main class="auth">

    <a href="${ctx}/" class="voltar" aria-label="Voltar para a página inicial">
            <svg width="22" height="22" viewBox="0 0 24 24" aria-hidden="true"><path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/></svg>
            <span>Voltar</span>
    </a>

    <section class="auth-form">
        <h1>Crie sua conta!</h1>
        <p class="subtitulo">Junte-se a nós e faça parte da mudança por um planeta mais sustentável!</p>

        <c:if test="${not empty erro}">
            <div class="aviso erro" role="alert"><c:out value="${erro}"/></div>
        </c:if>

        <form method="post" action="${ctx}/cadastro" accept-charset="UTF-8" id="formCadastro">

            <label class="campo">
                <span class="sr-only">Nome completo</span>
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></svg>
                <input type="text" name="nome" placeholder="Nome completo" required minlength="3" maxlength="150"
                       autocomplete="name" value="<c:out value='${nome}'/>">
            </label>

            <label class="campo">
                <span class="sr-only">E-mail</span>
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M20 4H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4-8 5-8-5V6l8 5 8-5v2z"/></svg>
                <input type="email" name="email" placeholder="E-mail" required maxlength="150"
                       autocomplete="email" value="<c:out value='${email}'/>">
            </label>

            <label class="campo">
                <span class="sr-only">Senha</span>
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zM9 8V6c0-1.66 1.34-3 3-3s3 1.34 3 3v2H9z"/></svg>
                <input type="password" name="senha" id="senha" placeholder="Senha (mínimo de 8 caracteres)"
                       required minlength="8" maxlength="100" autocomplete="new-password">
            </label>

            <label class="campo">
                <span class="sr-only">Confirmar senha</span>
                <svg viewBox="0 0 24 24" aria-hidden="true"><path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zM9 8V6c0-1.66 1.34-3 3-3s3 1.34 3 3v2H9z"/></svg>
                <input type="password" name="confirmarSenha" id="confirmarSenha" placeholder="Confirmar senha"
                       required maxlength="100" autocomplete="new-password">
            </label>

            <label class="termos">
                <input type="checkbox" name="termos" required>
                <span>Eu concordo com os <a href="#">Termos de Uso</a> e a <a href="#">Política de Privacidade</a></span>
            </label>

            <button type="submit" class="btn-principal">Cadastrar</button>
        </form>

        <div class="ou">ou</div>
        <p class="alt">Já tem uma conta? Acesse pelo app do Renovaí.</p>
    </section>

    <aside class="mascote" aria-hidden="true">
        <img src="${ctx}/img/mascote.png" alt="" onerror="this.style.display='none'">
    </aside>
</main>

<script>
    const senha = document.getElementById("senha");
    const confirmar = document.getElementById("confirmarSenha");

    function conferir() {
        confirmar.setCustomValidity(
            confirmar.value !== senha.value ? "As senhas não conferem." : ""
        );
    }
    senha.addEventListener("input", conferir);
    confirmar.addEventListener("input", conferir);
</script>
</body>
</html>
