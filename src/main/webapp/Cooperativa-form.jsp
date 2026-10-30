<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/coop-helpers.jspf" %>

<!doctype html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">

    <title>Cooperativas | Renovaí</title>

    <link
        href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap"
        rel="stylesheet">

    <link
        rel="stylesheet"
        href="${pageContext.request.contextPath}/css/estilo.css">

    <link
        rel="stylesheet"
        href="${pageContext.request.contextPath}/css/cooperativas.css">
</head>

<body class="coop-page">
<div class="layout">

    <% request.setAttribute("menuAtivo", "cooperativas"); %>
    <%@ include file="/menu.jspf" %>

    <main class="conteudo coop-conteudo">

        <header class="coop-top">
            <div>
                <h1>Nova cooperativa</h1>

                <p>
                    <a href="cooperativa">
                        ‹ Voltar para cooperativas
                    </a>
                </p>
            </div>
        </header>

        <% if (request.getAttribute("erro") != null) { %>
        <div class="coop-alert erro" role="alert">
            <%=h(request.getAttribute("erro"))%>
        </div>
        <% } %>

        <% if (request.getAttribute("mensagem") != null) { %>
        <div class="coop-alert" role="status">
            <%=h(request.getAttribute("mensagem"))%>
        </div>
        <% } %>

        <form action="cooperativa" method="post">

            <input type="hidden" name="acao" value="cadastrar">

            <section class="coop-card">
                <h2>Dados institucionais</h2>

                <div class="coop-grid">

                    <label class="coop-wide">
                        Nome da cooperativa *
                        <input
                            type="text"
                            name="nome"
                            placeholder="Insira a razão social da cooperativa"
                            required>
                    </label>

                    <label class="coop-wide">
                        Nome público *
                        <input
                            type="text"
                            name="nome_publico"
                            placeholder="Nome apresentado na plataforma"
                            required>
                    </label>

                    <label>
                        CNPJ *
                        <input
                            type="text"
                            name="cnpj"
                            placeholder="00.000.000/0000-00"
                            required>
                    </label>

                    <label>
                        Telefone / WhatsApp *
                        <input
                            type="tel"
                            name="telefone_whatsapp"
                            placeholder="(00) 00000-0000"
                            required>
                    </label>

                    <label>
                        E-mail de contato *
                        <input
                            type="email"
                            name="email_institucional"
                            placeholder="contato@cooperativa.com"
                            required>
                    </label>

                    <label>
                        CEP *
                        <input
                            type="text"
                            name="cep"
                            placeholder="00000-000"
                            required>
                    </label>

                    <label>
                        Endereço *
                        <input
                            type="text"
                            name="endereco"
                            placeholder="Rua, número, bairro"
                            required>
                    </label>

                    <label>
                        Cidade *
                        <input
                            type="text"
                            name="cidade"
                            placeholder="Nome da cidade"
                            required>
                    </label>

                    <label>
                        UF *
                        <select name="estado" required>
                            <option value="">UF</option>
                            <option value="AC">AC</option>
                            <option value="AL">AL</option>
                            <option value="AP">AP</option>
                            <option value="AM">AM</option>
                            <option value="BA">BA</option>
                            <option value="CE">CE</option>
                            <option value="DF">DF</option>
                            <option value="ES">ES</option>
                            <option value="GO">GO</option>
                            <option value="MA">MA</option>
                            <option value="MT">MT</option>
                            <option value="MS">MS</option>
                            <option value="MG">MG</option>
                            <option value="PA">PA</option>
                            <option value="PB">PB</option>
                            <option value="PR">PR</option>
                            <option value="PE">PE</option>
                            <option value="PI">PI</option>
                            <option value="RJ">RJ</option>
                            <option value="RN">RN</option>
                            <option value="RS">RS</option>
                            <option value="RO">RO</option>
                            <option value="RR">RR</option>
                            <option value="SC">SC</option>
                            <option value="SP">SP</option>
                            <option value="SE">SE</option>
                            <option value="TO">TO</option>
                        </select>
                    </label>

                    <label class="coop-wide">
                        Descrição
                        <textarea
                            name="descricao_institucional"
                            rows="3"
                            placeholder="Áreas de atuação, materiais principais..."></textarea>
                    </label>

                    <label class="coop-wide">
                        Logo (URL)
                        <input
                            type="url"
                            name="logo_url"
                            placeholder="https://exemplo.com/logo.png">

                        <small>
                            Informe o endereço de uma imagem.
                            O cadastro atual recebe a logo por URL.
                        </small>
                    </label>
                </div>
            </section>

            <section class="coop-card">
                <h2>Gestor responsável</h2>

                <p class="coop-notice">
                    ⓘ Ao salvar, o sistema cadastra a cooperativa e o
                    gestor com a senha temporária informada.
                </p>

                <div class="coop-grid">

                    <label>
                        Nome completo *
                        <input
                            name="gestor_nome_completo"
                            placeholder="Insira o nome completo do gestor"
                            autocomplete="name"
                            required>
                    </label>

                    <label>
                        Usuário
                        <input
                            name="gestor_nome_usuario"
                            placeholder="Crie um nome de usuário"
                            autocomplete="off">
                    </label>

                    <label>
                        E-mail *
                        <input
                            type="email"
                            name="gestor_email"
                            placeholder="gestor@cooperativa.com"
                            required>
                    </label>

                    <label>
                        Senha temporária *
                        <input
                            type="password"
                            name="gestor_senha_temporaria"
                            placeholder="Digite uma senha temporária para o gestor"
                            autocomplete="new-password"
                            required>
                    </label>
                </div>
            </section>

            <div class="coop-actions">
                <a class="coop-btn secondary" href="cooperativa">
                    Cancelar
                </a>

                <button class="coop-btn" type="submit">
                    Criar cooperativa
                </button>
            </div>
        </form>
    </main>
</div>
</body>
</html>