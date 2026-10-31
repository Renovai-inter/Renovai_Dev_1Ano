

<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="pt_BR" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Coletas | Renovaí</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">

    <style>
        :root {
            --verde-menu: #618c66;
            --verde-ativo: #b3cca9;
            --verde-botao: #4d8a57;
            --vinho: #8a2450;
            --fundo: #f6f5f1;
            --card: #ffffff;
            --linha: #e4e2dc;
            --texto: #1c1c1c;
            --texto-suave: #6f6f6f;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: "Poppins", system-ui, sans-serif;
            background: var(--fundo);
            color: var(--texto);
        }

        .layout { display: flex; min-height: 100vh; }


        /* ---------- Conteúdo ---------- */
        main { flex: 1; padding: 36px 48px; min-width: 0; }

        .topo {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 16px;
            flex-wrap: wrap;
            margin-bottom: 28px;
        }
        .topo h1 { margin: 0; font-size: 2.2rem; font-weight: 600; }
        .topo p { margin: 4px 0 0; color: #333; font-size: .95rem; }

        .btn {
            display: inline-block;
            font-family: inherit;
            font-size: .95rem;
            font-weight: 500;
            border-radius: 10px;
            padding: 10px 22px;
            cursor: pointer;
            border: 1.5px solid transparent;
            text-decoration: none;
        }
        .btn:focus-visible, .icone:focus-visible, input:focus-visible {
            outline: 3px solid #f0c24b;
            outline-offset: 2px;
        }
        .btn-verde { background: var(--verde-botao); color: #fff; }
        .btn-verde:hover { background: #3f7648; }
        .btn-vinho { background: var(--vinho); color: #fff; }
        .btn-vinho:hover { background: #701c40; }
        .btn-vinho:disabled { background: #d3a9bb; cursor: not-allowed; }
        .btn-contorno { background: transparent; color: var(--verde-botao); border-color: var(--verde-botao); }
        .btn-contorno:hover { background: #eef4ec; }

        .aviso-sucesso {
            background: #d8e8cf;
            color: #2f5a35;
            border-radius: 12px;
            padding: 12px 18px;
            margin-bottom: 20px;
            font-size: .9rem;
        }

        .painel {
            background: var(--card);
            border: 1px solid var(--linha);
            border-radius: 28px;
            overflow: hidden;
        }

        .filtros { padding: 24px 32px; }
        .filtros input {
            width: 100%;
            max-width: 360px;
            font-family: inherit;
            font-size: .95rem;
            border: 1px solid var(--linha);
            border-radius: 12px;
            padding: 11px 16px;
            background: #fbfbf8;
        }

        .tabela-rolavel { overflow-x: auto; }
        table { width: 100%; border-collapse: collapse; min-width: 820px; }
        thead th {
            background: #f1f0ec;
            color: var(--vinho);
            font-size: .72rem;
            font-weight: 600;
            text-align: left;
            padding: 12px 20px;
            text-transform: uppercase;
            border-top: 1px solid var(--linha);
            border-bottom: 1px solid var(--linha);
        }
        tbody td {
            padding: 14px 20px;
            border-bottom: 1px solid var(--linha);
            vertical-align: middle;
            font-size: .95rem;
        }
        tbody tr:last-child td { border-bottom: 0; }
        td.codigo { color: var(--texto-suave); white-space: nowrap; }
        td.acoes { width: 100px; text-align: right; white-space: nowrap; }

        .vazio { text-align: center; color: var(--texto-suave); padding: 48px 16px; }

        .tipo {
            display: inline-block;
            border: 1px solid #cfcdc6;
            border-radius: 6px;
            padding: 3px 14px;
            font-size: .75rem;
            background: #fbfbf8;
        }

        /* Status */
        .status {
            display: inline-block;
            border-radius: 999px;
            padding: 3px 12px;
            font-size: .75rem;
            font-weight: 600;
            white-space: nowrap;
        }
        .status::before { content: "• "; }
        .status[data-status="CONCLUIDA"]    { background: #d8e8cf; color: #3f6a3a; }
        .status[data-status="EM_ANDAMENTO"] { background: #fbe9a8; color: #8a6a10; }
        .status[data-status="CANCELADA"]    { background: #e2e0dc; color: #55524c; }
        .status[data-status="AGENDADA"]     { background: #cfe5f4; color: #2f6a94; }

        .icone {
            display: inline-block;
            background: none;
            border: 0;
            padding: 6px;
            cursor: pointer;
            border-radius: 8px;
            line-height: 0;
        }
        .icone svg { width: 20px; height: 20px; }
        .icone.editar { color: #8bb08a; }
        .icone.excluir { color: var(--vinho); }
        .icone:hover { background: #f1f0ec; }

        /* ---------- Modal ---------- */
        dialog {
            border: 0;
            border-radius: 24px;
            background: var(--fundo);
            padding: 28px 32px;
            width: min(480px, calc(100vw - 32px));
            box-shadow: 0 20px 60px rgba(0, 0, 0, .25);
        }
        dialog::backdrop { background: rgba(30, 30, 30, .45); }
        dialog h2 { margin: 0; color: var(--vinho); font-size: 1.5rem; font-weight: 600; }
        dialog .sub { margin: 2px 0 18px; font-size: .85rem; color: #444; }
        dialog .titulo-secao { font-size: .75rem; color: #555; text-transform: uppercase; margin: 0 0 8px; }
        dialog .resumo {
            background: #fff;
            border: 1px solid var(--linha);
            border-radius: 12px;
            padding: 12px 16px;
            font-size: .85rem;
        }
        dialog .resumo div { display: flex; justify-content: space-between; gap: 12px; padding: 3px 0; }
        dialog .resumo span:first-child { color: var(--texto-suave); }
        dialog .alerta {
            margin-top: 16px;
            background: #fbf0d4;
            border: 1px solid #e3b84d;
            color: #6e5410;
            border-radius: 12px;
            padding: 12px 16px;
            font-size: .8rem;
        }
        dialog label { display: block; margin: 18px 0 6px; font-size: .95rem; }
        dialog input[type="text"] {
            width: 100%;
            font-family: inherit;
            font-size: .95rem;
            border: 1px solid var(--linha);
            border-radius: 12px;
            padding: 11px 16px;
            background: #fbfbf8;
        }
        dialog .rodape { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; }
        dialog .fechar {
            position: absolute;
            top: 16px;
            right: 18px;
            background: none;
            border: 0;
            font-size: 1.6rem;
            line-height: 1;
            color: var(--vinho);
            cursor: pointer;
        }

        @media (max-width: 820px) {
            .layout { flex-direction: column; }
            main { padding: 24px 16px; }
            .filtros { padding: 16px; }
        }
    </style>
</head>
<body>
<div class="layout">

    <%@ include file="/menu.jspf" %>

    <!-- ===== CONTEÚDO ===== -->
    <main>
        <header class="topo">
            <div>
                <h1>Coletas</h1>
                <p>Coletas externas e entregas realizadas nas cooperativas.</p>
            </div>
            <a href="${ctx}/coleta?acao=nova" class="btn btn-verde">Nova coleta</a>
        </header>

        <c:if test="${not empty erroColeta}"><div class="operacao-aviso erro" role="alert"><c:out value="${erroColeta}"/></div></c:if>
        <c:if test="${param.sucesso == 'cadastro'}">
            <div class="aviso-sucesso" role="status">Coleta cadastrada com sucesso.</div>
        </c:if>
        <c:if test="${param.sucesso == 'exclusao'}">
            <div class="aviso-sucesso" role="status">Coleta excluída com sucesso.</div>
        </c:if>
        <c:if test="${param.sucesso == 'cancelamento'}">
            <div class="aviso-sucesso" role="status">Coleta cancelada com sucesso.</div>
        </c:if>

        <section class="painel">
            <div class="filtros">
                <input type="search" id="busca" data-busca="#tabelaColetas" autocomplete="off" placeholder="Buscar por código, cooperativa, responsável..." aria-label="Buscar coleta">
            </div>

            <div class="tabela-rolavel">
                <table>
                    <thead>
                    <tr>
                        <th>Código</th>
                        <th>Cooperativa</th>
                        <th>Tipo</th>
                        <th>Responsável</th>
                        <th>Data</th>
                        <th>Peso total</th>
                        <th>Status</th>
                        <th><span style="position:absolute;left:-9999px">Ações</span></th>
                    </tr>
                    </thead>
                    <tbody id="tabelaColetas">
                    <c:forEach items="${coletas}" var="coleta">

                        <fmt:formatNumber value="${coleta.pesoTotalKg}" maxFractionDigits="2" var="pesoFmt"/>
                        <c:set var="pesoExibicao" value="—"/>
                        <c:if test="${not empty coleta.pesoTotalKg}"><c:set var="pesoExibicao" value="${pesoFmt} kg"/></c:if>

                        <%-- NOVO: nome da cooperativa (cai para "Cooperativa #id" se não achar) --%>
                        <c:set var="nomeCoop" value="${cooperativas[coleta.relacionamentos.idCooperativa]}"/>
                        <c:if test="${empty nomeCoop}">
                            <c:set var="nomeCoop" value="Cooperativa #${coleta.relacionamentos.idCooperativa}"/>
                        </c:if>

                        <%-- NOVO: nome do responsável (cai para "Cooperado #id", ou "—" se não houver) --%>
                        <c:set var="nomeResp" value="${empty coleta.relacionamentos.idCooperadoResponsavel ? '—' : cooperados[coleta.relacionamentos.idCooperadoResponsavel]}"/>
                        <c:if test="${empty nomeResp}">
                            <c:set var="nomeResp" value="Cooperado #${coleta.relacionamentos.idCooperadoResponsavel}"/>
                        </c:if>

                        <tr>
                            <td class="codigo">CO-<c:out value="${coleta.idColeta}"/></td>

                            <td><c:out value="${nomeCoop}"/></td>

                            <td>
                                <span class="tipo">
                                    <c:choose>
                                        <c:when test="${coleta.tipo == 'COLETA_EXTERNA'}">Externa</c:when>
                                        <c:when test="${coleta.tipo == 'ENTREGA_COOPERATIVA'}">Entrega</c:when>
                                        <c:otherwise><c:out value="${coleta.tipo}"/></c:otherwise>
                                    </c:choose>
                                </span>
                            </td>

                            <td><c:out value="${nomeResp}"/></td>

                            <td>
                                <c:choose>
                                    <c:when test="${empty coleta.dataAgendada}">—</c:when>
                                    <c:otherwise><fmt:formatNumber value="${coleta.dataAgendada.dayOfMonth}" pattern="00"/>/<fmt:formatNumber value="${coleta.dataAgendada.monthValue}" pattern="00"/>/<c:out value="${coleta.dataAgendada.year}"/></c:otherwise>
                                </c:choose>
                            </td>

                            <td><c:out value="${pesoExibicao}"/></td>

                            <td>
                                <span class="status" data-status="<c:out value='${coleta.status}'/>">
                                    <c:choose>
                                        <c:when test="${coleta.status == 'CONCLUIDA'}">Concluída</c:when>
                                        <c:when test="${coleta.status == 'EM_ANDAMENTO'}">Em andamento</c:when>
                                        <c:when test="${coleta.status == 'CANCELADA'}">Cancelada</c:when>
                                        <c:when test="${coleta.status == 'AGENDADA'}">Agendada</c:when>
                                        <c:otherwise><c:out value="${coleta.status}"/></c:otherwise>
                                    </c:choose>
                                </span>
                            </td>

                            <td class="acoes">
                                <a class="icone editar" href="${ctx}/coleta?acao=editar&id=${coleta.idColeta}"
                                   aria-label="Editar coleta CO-${coleta.idColeta}">
                                    <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 17.25V21h3.75L17.8 9.94l-3.75-3.75L3 17.25zm17.7-10.2a1 1 0 0 0 0-1.41l-2.34-2.34a1 1 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>
                                </a>
                                <button type="button" class="icone excluir btn-excluir"
                                        aria-label="Excluir coleta CO-${coleta.idColeta}"
                                        data-id="${coleta.idColeta}"
                                        data-cooperativa="<c:out value='${nomeCoop}'/>"
                                        data-peso="<c:out value='${pesoExibicao}'/>">
                                    <svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <c:if test="${empty coletas}">
                <p class="vazio">Nenhuma coleta cadastrada ainda. Clique em “Nova coleta” para registrar a primeira.</p>
            </c:if>
        </section>
    </main>
</div>

<!-- ===== MODAL: EXCLUIR COLETA ===== -->
<dialog id="modalExcluir" aria-labelledby="tituloExcluir">
    <button type="button" class="fechar" data-fechar aria-label="Fechar">&times;</button>
    <h2 id="tituloExcluir">Excluir coleta</h2>
    <p class="sub">Revise o impacto antes de confirmar a ação sobre “<span id="excluirCodigoTitulo"></span>”.</p>

    <p class="titulo-secao">Registros relacionados afetados</p>
    <div class="resumo">
        <div><span>Cooperativa</span><span id="excluirCooperativa"></span></div>
        <div><span>Peso total informado</span><span id="excluirPeso"></span></div>
    </div>

    <div class="alerta" role="note">
        Coletas não podem ser inativadas. Para desfazer uma coleta, use o status Cancelada.
        A exclusão remove o registro do histórico.
    </div>

    <form method="post" action="${ctx}/coleta" accept-charset="UTF-8" id="formExcluir">
        <input type="hidden" name="acao" value="excluir">
        <input type="hidden" name="id" id="excluirId">
        <%-- O servlet compara com o número do ID; o JS preenche ao enviar --%>
        <input type="hidden" name="confirmacao" id="excluirConfirmacao">

        <label for="excluirDigitado">Digite <strong id="excluirCodigoLabel"></strong> para confirmar:</label>
        <input type="text" id="excluirDigitado" autocomplete="off">

        <div class="rodape">
            <button type="button" class="btn btn-contorno" data-fechar>Cancelar</button>
            <button type="submit" class="btn btn-vinho" id="btnConfirmarExcluir" disabled>Excluir</button>
        </div>
    </form>
</dialog>

<script>
    const $ = (id) => document.getElementById(id);
    const modal = $("modalExcluir");
    const digitado = $("excluirDigitado");
    const btnConfirmar = $("btnConfirmarExcluir");
    let codigoEsperado = "";

    // Abrir modal de exclusão
    document.querySelectorAll(".btn-excluir").forEach((btn) => {
        btn.addEventListener("click", () => {
            const id = btn.dataset.id;
            codigoEsperado = "CO-" + id;

            $("excluirId").value = id;
            $("excluirConfirmacao").value = id;
            $("excluirCodigoTitulo").textContent = codigoEsperado;
            $("excluirCodigoLabel").textContent = codigoEsperado;
            $("excluirCooperativa").textContent = btn.dataset.cooperativa;
            $("excluirPeso").textContent = btn.dataset.peso;

            digitado.value = "";
            btnConfirmar.disabled = true;
            modal.showModal();
        });
    });

    // Só libera o botão quando o código digitado for igual ao da coleta
    digitado.addEventListener("input", () => {
        btnConfirmar.disabled =
            digitado.value.trim().toUpperCase() !== codigoEsperado.toUpperCase();
    });

    // Fechar modal
    document.querySelectorAll("[data-fechar]").forEach((el) => {
        el.addEventListener("click", () => el.closest("dialog").close());
    });
</script>
<script src="${pageContext.request.contextPath}/scripts/busca-ao-vivo.js?v=1"></script>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
