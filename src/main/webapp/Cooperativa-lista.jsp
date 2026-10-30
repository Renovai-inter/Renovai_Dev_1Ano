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
                <h1>Cooperativas</h1>
                <p>Cooperativas de reciclagem cadastradas na plataforma</p>
            </div>

            <a class="coop-btn" href="cooperativa?pagina=nova">
                <svg
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="1.7"
                    aria-hidden="true">
                    <path d="M4 21V3h12v18M16 9h4v12M2 21h20M7 7h2m2 0h2M7 11h2m2 0h2M7 15h2m2 0h2M9 21v-3h3v3"/>
                </svg>
                Cadastrar cooperativa
            </a>
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

        <section
            class="coop-card coop-list-card"
            aria-label="Cooperativas cadastradas">

            <form
                method="get"
                action="cooperativa"
                class="coop-search"
                role="search">

                <label>
                    <span class="sr-only">
                        Buscar por nome, CNPJ ou código
                    </span>

                    <input
                        type="search"
                        name="busca"
                        value="<%=h(request.getParameter("busca"))%>"
                        placeholder="Buscar por nome, CNPJ ou código...">
                </label>

                <button class="coop-btn secondary" type="submit">
                    Buscar
                </button>
            </form>

            <div class="coop-overflow">
                <table class="coop-table">
                    <thead>
                    <tr>
                        <th>Nome</th>
                        <th>Cidade/UF</th>
                        <th>Status</th>
                        <th>Cooperados</th>
                        <th>Cadastro</th>
                        <th>
                            <span class="sr-only">Ações</span>
                        </th>
                    </tr>
                    </thead>

                    <tbody>
                    <%
                        List<Cooperativa> itens =
                                (List<Cooperativa>)
                                        request.getAttribute("cooperativas");

                        if (itens == null || itens.isEmpty()) {
                    %>
                    <tr>
                        <td colspan="6" class="coop-empty">
                            Nenhuma cooperativa encontrada.
                        </td>
                    </tr>
                    <%
                        } else {
                            for (Cooperativa c : itens) {
                    %>
                    <tr>
                        <td>
                            <div class="coop-identity">

                                <span class="coop-avatar">
                                    <svg
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        stroke-width="1.7"
                                        aria-hidden="true">
                                        <path d="M4 21V3h12v18M16 9h4v12M2 21h20M7 7h2m2 0h2M7 11h2m2 0h2M7 15h2m2 0h2M9 21v-3h3v3"/>
                                    </svg>
                                </span>

                                <div>
                                    <a
                                        class="coop-name"
                                        href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>">
                                        <%=h(c.getNomePublico())%>
                                    </a>

                                    <small>
                                        <%=h(c.getCnpj())%>
                                    </small>
                                </div>
                            </div>
                        </td>

                        <td>
                            <%=h(c.getCidade())%>/<%=h(c.getEstado())%>
                        </td>

                        <td>
                            <span class="<%=selo(c.getStatus())%>">
                                <%=h(rotulo(c.getStatus()))%>
                            </span>
                        </td>

                        <td>
                            <%=c.getRelacionamentos().getQtdCooperados()%>
                        </td>

                        <td>
                            <% if (c.getDataCadastro() != null) { %>
                            <%=c.getDataCadastro().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )%>
                            <% } else { %>
                            —
                            <% } %>
                        </td>

                        <td>
                            <div class="coop-row-actions">

                                <a
                                    class="coop-icon"
                                    href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>"
                                    aria-label="Editar <%=h(c.getNomePublico())%>">

                                    <svg
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        stroke-width="2"
                                        aria-hidden="true">
                                        <path d="m15 5 4 4M4 20l4-1L20 7l-4-4L4 15Z"/>
                                    </svg>
                                </a>

                                <a
                                    class="coop-icon danger"
                                    href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>&amp;excluir=1"
                                    aria-label="Excluir <%=h(c.getNomePublico())%>">

                                    <svg
                                        viewBox="0 0 24 24"
                                        fill="none"
                                        stroke="currentColor"
                                        stroke-width="2"
                                        aria-hidden="true">
                                        <path d="M3 6h18M9 6V3h6v3M6 6l1 15h10l1-15M10 10v7m4-7v7"/>
                                    </svg>
                                </a>
                            </div>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>
        </section>
    </main>
</div>
</body>
</html>