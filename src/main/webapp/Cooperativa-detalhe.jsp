<%@ page pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/coop-helpers.jspf" %>

<%
    String aba = request.getParameter("aba");

    if (!Arrays.asList(
            "cooperados",
            "coletas",
            "negociacoes",
            "materiais"
    ).contains(aba)) {
        aba = "cooperados";
    }

    boolean modalAberto =
            "1".equals(request.getParameter("excluir"));
%>

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

<div class="layout"
     <% if (modalAberto && request.getAttribute("cooperativa") != null) { %>
     inert
     <% } %>>

    <% request.setAttribute("menuAtivo", "cooperativas"); %>
    <%@ include file="/menu.jspf" %>

    <main class="conteudo coop-conteudo">

        <%
            Cooperativa c =
                    (Cooperativa) request.getAttribute("cooperativa");

            List<Cooperado> membros =
                    (List<Cooperado>) request.getAttribute("cooperados");

            List<Coleta> coletas =
                    (List<Coleta>) request.getAttribute("coletas");

            List<Negociacao> negociacoes =
                    (List<Negociacao>) request.getAttribute("negociacoes");

            if (membros == null) {
                membros = Collections.emptyList();
            }

            if (coletas == null) {
                coletas = Collections.emptyList();
            }

            if (negociacoes == null) {
                negociacoes = Collections.emptyList();
            }

            if (c != null) {
        %>

        <header class="coop-top">
            <div class="coop-detail-title">

                <a class="coop-back"
                   href="cooperativa"
                   aria-label="Voltar para cooperativas">‹</a>

                <div>
                    <h1><%=h(c.getNomePublico())%></h1>

                    <p>
                        Cooperativa
                        <%=h(c.getCidade())%>/<%=h(c.getEstado())%>

                        <span class="<%=selo(c.getStatus())%>">
                            <%=h(rotulo(c.getStatus()))%>
                        </span>
                    </p>
                </div>
            </div>

            <div class="coop-row-actions">

                <button
                    class="coop-btn secondary"
                    type="submit"
                    form="coop-edit">
                    Salvar
                </button>

                <a class="coop-btn danger"
                   href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>&amp;aba=<%=aba%>&amp;excluir=1">

                    <svg
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        stroke-width="2"
                        aria-hidden="true">
                        <path d="M3 6h18M9 6V3h6v3M6 6l1 15h10l1-15M10 10v7m4-7v7"/>
                    </svg>

                    Excluir
                </a>
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

        <div class="coop-stats">

            <div>
                <span>
                    <svg width="15" height="15"
                         viewBox="0 0 24 24"
                         fill="none"
                         stroke="currentColor"
                         stroke-width="2"
                         stroke-linecap="round"
                         stroke-linejoin="round"
                         aria-hidden="true">
                        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                        <circle cx="9" cy="7" r="4"/>
                        <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                    </svg>
                    Cooperados
                </span>

                <strong><%=membros.size()%></strong>
            </div>

            <div>
                <span>
                    <svg width="15" height="15"
                         viewBox="0 0 24 24"
                         fill="none"
                         stroke="currentColor"
                         stroke-width="2"
                         stroke-linecap="round"
                         stroke-linejoin="round"
                         aria-hidden="true">
                        <path d="M14 18V6a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v11a1 1 0 0 0 1 1h2"/>
                        <path d="M15 18H9"/>
                        <path d="M19 18h2a1 1 0 0 0 1-1v-3.65a1 1 0 0 0-.22-.624l-3.48-4.35A1 1 0 0 0 17.52 8H14"/>
                        <circle cx="17" cy="18" r="2"/>
                        <circle cx="7" cy="18" r="2"/>
                    </svg>
                    Rotas
                </span>

                <strong>
                    <%=h(request.getAttribute("totalRotas"))%>
                </strong>
            </div>

            <div>
                <span>
                    <svg width="15" height="15"
                         viewBox="0 0 24 24"
                         fill="none"
                         stroke="currentColor"
                         stroke-width="2"
                         stroke-linecap="round"
                         stroke-linejoin="round"
                         aria-hidden="true">
                        <path d="M14 18V6a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v11a1 1 0 0 0 1 1h2"/>
                        <path d="M15 18H9"/>
                        <path d="M19 18h2a1 1 0 0 0 1-1v-3.65a1 1 0 0 0-.22-.624l-3.48-4.35A1 1 0 0 0 17.52 8H14"/>
                        <circle cx="17" cy="18" r="2"/>
                        <circle cx="7" cy="18" r="2"/>
                    </svg>
                    Coletas
                </span>

                <strong>
                    <%=h(request.getAttribute("totalColetas"))%>
                </strong>
            </div>

            <div>
                <span>
                    <svg width="15" height="15"
                         viewBox="0 0 24 24"
                         fill="none"
                         stroke="currentColor"
                         stroke-width="2"
                         stroke-linecap="round"
                         stroke-linejoin="round"
                         aria-hidden="true">
                        <path d="m11 17 2 2a1 1 0 1 0 3-3"/>
                        <path d="m14 14 2.5 2.5a1 1 0 1 0 3-3l-3.88-3.88a3 3 0 0 0-4.24 0l-.88.88a1 1 0 1 1-3-3l2.81-2.81a5.79 5.79 0 0 1 7.06-.87l.47.28a2 2 0 0 0 1.42.25L21 4"/>
                        <path d="m21 3 1 11h-2"/>
                        <path d="M3 3 2 14l6.5 6.5a1 1 0 1 0 3-3"/>
                        <path d="M3 4h8"/>
                    </svg>
                    Negociações
                </span>

                <strong>
                    <%=h(request.getAttribute("totalNegociacoes"))%>
                </strong>
            </div>
        </div>

        <form id="coop-edit" method="post" action="cooperativa">

            <input type="hidden" name="acao" value="atualizar">

            <input type="hidden"
                   name="id_cooperativa"
                   value="<%=c.getIdCooperativa()%>">

            <div class="coop-detail-grid">

                <section class="coop-card">
                    <h2>Identificação</h2>

                    <div class="coop-grid coop-edit-grid">

                        <label>
                            Razão social
                            <input name="nome"
                                   value="<%=h(c.getNome())%>"
                                   required>
                        </label>

                        <label>
                            CNPJ
                            <input name="cnpj"
                                   value="<%=h(c.getCnpj())%>">
                        </label>

                        <label>
                            Nome público
                            <input name="nome_publico"
                                   value="<%=h(c.getNomePublico())%>"
                                   required>
                        </label>

                        <div class="coop-data">
                            <span>Data de cadastro</span>

                            <p>
                                <% if (c.getDataCadastro() != null) { %>
                                <%=c.getDataCadastro().format(
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                )%>
                                <% } else { %>
                                —
                                <% } %>
                            </p>
                        </div>

                        <label>
                            Status
                            <select name="status">
                                <option value="ATIVA"
                                    <% if (c.getStatus() == StatusCooperativa.ATIVA) { %>
                                    selected
                                    <% } %>>
                                    Ativo
                                </option>

                                <option value="INATIVA"
                                    <% if (c.getStatus() == StatusCooperativa.INATIVA) { %>
                                    selected
                                    <% } %>>
                                    Inativo
                                </option>
                            </select>
                        </label>

                        <label class="coop-wide">
                            Descrição
                            <textarea
                                name="descricao_institucional"
                                rows="3"><%=h(c.getDescricaoInstitucional())%></textarea>
                        </label>
                    </div>
                </section>

                <section class="coop-card">
                    <h2>Contato e endereço</h2>

                    <div class="coop-grid coop-edit-grid">

                        <label>
                            E-mail
                            <input type="email"
                                   name="email_institucional"
                                   value="<%=h(c.getEmailInstitucional())%>">
                        </label>

                        <label>
                            Telefone
                            <input type="tel"
                                   name="telefone_whatsapp"
                                   value="<%=h(c.getTelefoneWhatsapp())%>">
                        </label>

                        <label class="coop-wide">
                            Endereço
                            <input name="endereco"
                                   value="<%=h(c.getEndereco())%>">
                        </label>

                        <label>
                            Cidade
                            <input name="cidade"
                                   value="<%=h(c.getCidade())%>"
                                   required>
                        </label>

                        <label>
                            UF
                            <select name="estado">
                                <option value="">UF</option>

                                <%
                                    for (String uf : new String[]{
                                            "AC", "AL", "AP", "AM", "BA",
                                            "CE", "DF", "ES", "GO", "MA",
                                            "MT", "MS", "MG", "PA", "PB",
                                            "PR", "PE", "PI", "RJ", "RN",
                                            "RS", "RO", "RR", "SC", "SP",
                                            "SE", "TO"
                                    }) {
                                %>
                                <option value="<%=uf%>"
                                    <% if (uf.equals(c.getEstado())) { %>
                                    selected
                                    <% } %>>
                                    <%=uf%>
                                </option>
                                <% } %>
                            </select>
                        </label>

                        <label>
                            CEP
                            <input name="cep"
                                   value="<%=h(c.getCep())%>">
                        </label>

                        <label>
                            Logo (URL)
                            <input type="url"
                                   name="logo_url"
                                   value="<%=h(c.getLogoUrl())%>"
                                   placeholder="https://...">
                        </label>
                    </div>
                </section>
            </div>
        </form>

        <section id="registros" class="coop-card coop-list-card">

            <nav class="coop-tabs"
                 aria-label="Registros da cooperativa">

                <a id="tab-cooperados"
                   href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>&amp;aba=cooperados#registros"
                   <% if ("cooperados".equals(aba)) { %>
                   aria-current="page"
                   <% } %>>
                    Cooperados
                </a>

                <a id="tab-coletas"
                   href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>&amp;aba=coletas#registros"
                   <% if ("coletas".equals(aba)) { %>
                   aria-current="page"
                   <% } %>>
                    Coletas
                </a>

                <a id="tab-negociacoes"
                   href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>&amp;aba=negociacoes#registros"
                   <% if ("negociacoes".equals(aba)) { %>
                   aria-current="page"
                   <% } %>>
                    Negociações
                </a>

                <a id="tab-materiais"
                   href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>&amp;aba=materiais#registros"
                   <% if ("materiais".equals(aba)) { %>
                   aria-current="page"
                   <% } %>>
                    Materiais
                </a>
            </nav>

            <div id="panel-cooperados"
                 class="coop-overflow"
                 role="region"
                 aria-labelledby="tab-cooperados"
                 tabindex="0"
                 <% if (!"cooperados".equals(aba)) { %>
                 hidden
                 <% } %>>

                <table class="coop-table">
                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Usuário</th>
                        <th>Cargo</th>
                        <th>Status</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% if (membros.isEmpty()) { %>
                    <tr>
                        <td colspan="4" class="coop-empty">
                            Nenhum cooperado cadastrado.
                        </td>
                    </tr>
                    <%
                        } else {
                            for (Cooperado m : membros) {
                    %>
                    <tr>
                        <td class="coop-code">
                            <%=h(m.getCodigoCooperado())%>
                        </td>

                        <td>
                            <div class="coop-identity">

                                <span class="coop-avatar member"
                                      aria-hidden="true">
                                    <svg viewBox="0 0 24 24"
                                         fill="currentColor">
                                        <circle cx="12" cy="8" r="4"/>
                                        <path d="M4 21v-3a8 8 0 0 1 16 0v3Z"/>
                                    </svg>
                                </span>

                                <div>
                                    <span>
                                        <%=h(m.getNomeCompleto())%>
                                    </span>

                                    <small>
                                        <%=h(m.getEmail())%>
                                    </small>
                                </div>
                            </div>
                        </td>

                        <td>
                            <%
                                if (m.getCargo() == null
                                        || m.getCargo().isBlank()) {
                            %>
                            Cooperado
                            <% } else { %>
                            <%=h(rotulo(m.getCargo()))%>
                            <% } %>
                        </td>

                        <td>
                            <span class="<%=selo(m.getStatus())%>">
                                <%=h(rotulo(m.getStatus()))%>
                            </span>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>

            <div id="panel-coletas"
                 class="coop-overflow"
                 role="region"
                 aria-labelledby="tab-coletas"
                 tabindex="0"
                 <% if (!"coletas".equals(aba)) { %>
                 hidden
                 <% } %>>

                <table class="coop-table">
                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Tipo</th>
                        <th>Data</th>
                        <th>Peso</th>
                        <th>Status</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% if (coletas.isEmpty()) { %>
                    <tr>
                        <td colspan="5" class="coop-empty">
                            Nenhuma coleta registrada.
                        </td>
                    </tr>
                    <%
                        } else {
                            for (Coleta item : coletas) {
                    %>
                    <tr>
                        <td class="coop-code">
                            <%=h(item.getIdColeta())%>
                        </td>

                        <td>
                            <%=h(rotulo(item.getTipo()))%>
                        </td>

                        <td>
                            <% if (item.getDataAgendada() != null) { %>
                            <%=item.getDataAgendada().format(
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            )%>
                            <% } else { %>
                            —
                            <% } %>
                        </td>

                        <td>
                            <% if (item.getPesoTotalKg() != null) { %>
                            <%=h(item.getPesoTotalKg())%> kg
                            <% } else { %>
                            —
                            <% } %>
                        </td>

                        <td>
                            <span class="<%=selo(item.getStatus())%>">
                                <%=h(rotulo(item.getStatus()))%>
                            </span>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>

            <div id="panel-negociacoes"
                 class="coop-overflow"
                 role="region"
                 aria-labelledby="tab-negociacoes"
                 tabindex="0"
                 <% if (!"negociacoes".equals(aba)) { %>
                 hidden
                 <% } %>>

                <table class="coop-table">
                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Recicladora</th>
                        <th>Material</th>
                        <th>Peso</th>
                        <th>Status</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% if (negociacoes.isEmpty()) { %>
                    <tr>
                        <td colspan="5" class="coop-empty">
                            Nenhuma negociação registrada.
                        </td>
                    </tr>
                    <%
                        } else {
                            for (Negociacao n : negociacoes) {
                    %>
                    <tr>
                        <td class="coop-code">
                            <%=h(n.getCodigo())%>
                        </td>

                        <td><%=h(n.getNomeEmpresa())%></td>
                        <td><%=h(n.getNomeMaterial())%></td>
                        <td><%=h(n.getQuantidadeKg())%> kg</td>

                        <td>
                            <span class="<%=selo(n.getStatus())%>">
                                <%=h(n.getStatusLegivel())%>
                            </span>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                    </tbody>
                </table>
            </div>

            <div id="panel-materiais"
                 class="coop-overflow"
                 role="region"
                 aria-labelledby="tab-materiais"
                 tabindex="0"
                 <% if (!"materiais".equals(aba)) { %>
                 hidden
                 <% } %>>

                <p class="coop-table-note">
                    Materiais vinculados às negociações da cooperativa.
                </p>

                <table class="coop-table">
                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Recicladora</th>
                        <th>Material</th>
                        <th>Peso</th>
                        <th>Status</th>
                    </tr>
                    </thead>

                    <tbody>
                    <% if (negociacoes.isEmpty()) { %>
                    <tr>
                        <td colspan="5" class="coop-empty">
                            Nenhum material vinculado às negociações.
                        </td>
                    </tr>
                    <%
                        } else {
                            for (Negociacao n : negociacoes) {
                    %>
                    <tr>
                        <td class="coop-code">
                            <%=h(n.getIdMaterial())%>
                        </td>

                        <td><%=h(n.getNomeEmpresa())%></td>
                        <td><%=h(n.getNomeMaterial())%></td>
                        <td><%=h(n.getQuantidadeKg())%> kg</td>

                        <td>
                            <span class="<%=selo(n.getStatus())%>">
                                <%=h(n.getStatusLegivel())%>
                            </span>
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

        <% } else { %>

        <div class="coop-alert erro" role="alert">
            Cooperativa não encontrada.
        </div>

        <a href="cooperativa">
            Voltar para cooperativas
        </a>

        <% } %>

    </main>
</div>

<% if (c != null && modalAberto) { %>

<div class="coop-modal-overlay">

    <section id="excluirModal"
             class="coop-modal"
             role="dialog"
             aria-modal="true"
             aria-labelledby="delete-title"
             aria-describedby="delete-description">

        <a class="coop-modal-close"
           href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>&amp;aba=<%=aba%>#registros"
           aria-label="Fechar confirmação">
            ×
        </a>

        <h2 id="delete-title">Excluir cooperativa</h2>

        <p id="delete-description">
            Revise o impacto antes de confirmar a ação sobre
            “<%=h(c.getNomePublico())%>”.
        </p>

        <h3>Registros relacionados afetados</h3>

        <dl class="coop-impact">

            <div>
                <dt>Cooperados vinculados</dt>
                <dd><%=membros.size()%></dd>
            </div>

            <div>
                <dt>Rotas cadastradas</dt>
                <dd>
                    <%=h(request.getAttribute("totalRotas"))%>
                </dd>
            </div>

            <div>
                <dt>Coletas registradas</dt>
                <dd>
                    <%=h(request.getAttribute("totalColetas"))%>
                </dd>
            </div>

            <div>
                <dt>Negociações</dt>
                <dd>
                    <%=h(request.getAttribute("totalNegociacoes"))%>
                </dd>
            </div>

        </dl>

        <form method="post"
              action="cooperativa"
              id="coop-delete">

            <input type="hidden"
                   name="id_cooperativa"
                   value="<%=c.getIdCooperativa()%>">

            <input type="hidden"
                   name="status"
                   value="INATIVA">

            <label class="coop-delete-option">

                <input type="radio"
                       name="acao"
                       value="atualizar"
                       checked>

                <span>
                    <strong>Inativar (reversível)</strong>
                    <small>
                        Preserva os dados e altera o status para inativo.
                    </small>
                </span>

            </label>

            <label class="coop-delete-option permanent">

                <input type="radio"
                       name="acao"
                       value="excluir">

                <span>
                    <strong>Excluir permanentemente</strong>
                    <small>
                        Ação irreversível. Vínculos existentes
                        podem impedir a exclusão.
                    </small>
                </span>

            </label>

            <div class="coop-actions">

                <a class="coop-btn secondary"
                   href="cooperativa?pagina=detalhe&amp;id=<%=c.getIdCooperativa()%>&amp;aba=<%=aba%>#registros"
                   autofocus>
                    Cancelar
                </a>

                <button class="coop-btn" type="submit">
                    Confirmar ação
                </button>

            </div>
        </form>
    </section>
</div>

<% } %>

</body>
</html>