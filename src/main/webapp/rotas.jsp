<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Rotas | Renovaí</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">

    <style>
        .aviso-sucesso { background: #d8e8cf; color: #2f5a35; border-radius: 12px; padding: 12px 18px; margin-bottom: 20px; font-size: .9rem; }
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
            font-family: inherit;
            font-size: .95rem;
            font-weight: 500;
            border-radius: 10px;
            padding: 10px 22px;
            cursor: pointer;
            border: 1.5px solid transparent;
        }
        .btn:focus-visible, .icone:focus-visible, select:focus-visible, input:focus-visible {
            outline: 3px solid #f0c24b;
            outline-offset: 2px;
        }
        .btn-verde { background: var(--verde-botao); color: #fff; }
        .btn-verde:hover { background: #3f7648; }
        .btn-vinho { background: var(--vinho); color: #fff; }
        .btn-vinho:hover { background: #701c40; }
        .btn-contorno { background: transparent; color: var(--verde-botao); border-color: var(--verde-botao); }
        .btn-contorno:hover { background: #eef4ec; }

        .painel {
            background: var(--card);
            border: 1px solid var(--linha);
            border-radius: 28px;
            overflow: hidden;
        }

        .filtros { display: flex; gap: 16px; padding: 24px 32px; flex-wrap: wrap; }
        .filtros input, .filtros select, dialog input, dialog select {
            font-family: inherit;
            font-size: .95rem;
            border: 1px solid var(--linha);
            border-radius: 12px;
            padding: 11px 16px;
            background: #fbfbf8;
            color: var(--texto);
        }
        .filtros input { flex: 1; min-width: 220px; max-width: 360px; }
        .filtros select { color: var(--vinho); }

        table { width: 100%; border-collapse: collapse; }
        thead th {
            background: #f1f0ec;
            color: var(--vinho);
            font-size: .72rem;
            font-weight: 600;
            text-align: left;
            padding: 12px 32px;
            text-transform: uppercase;
            border-top: 1px solid var(--linha);
            border-bottom: 1px solid var(--linha);
        }
        tbody td { padding: 14px 32px; border-bottom: 1px solid var(--linha); vertical-align: middle; }
        tbody tr:last-child td { border-bottom: 0; }
        td.acoes { width: 110px; text-align: right; white-space: nowrap; }

        .vazio { text-align: center; color: var(--texto-suave); padding: 48px 16px; }

        .icone {
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

        /* ---------- Modais ---------- */
        dialog {
            border: 0;
            border-radius: 24px;
            background: var(--fundo);
            padding: 28px 32px;
            width: min(480px, calc(100vw - 32px));
            box-shadow: 0 20px 60px rgba(0, 0, 0, .25);
        }
        dialog::backdrop { background: rgba(30, 30, 30, .45); }
        dialog h2 { margin: 0; color: var(--vinho); font-size: 1.7rem; font-weight: 600; }
        dialog .sub { margin: 2px 0 20px; font-size: .85rem; color: #444; }
        dialog label { display: block; margin: 14px 0 6px; font-size: .95rem; }
        dialog input, dialog select { width: 100%; }
        dialog .rodape { display: flex; justify-content: flex-end; gap: 12px; margin-top: 26px; }
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
            thead th, tbody td, .filtros { padding-left: 16px; padding-right: 16px; }
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
                <h1>Rotas</h1>
                <p>Rotas de coleta de cada cooperativa.</p>
            </div>
            <button type="button" class="btn btn-verde" id="btnNovo">Nova rota</button>
        </header>

        <c:if test="${not empty erroRota}"><div class="operacao-aviso erro" role="alert"><c:out value="${erroRota}"/></div></c:if>
        <c:if test="${param.sucesso == 'cadastrar'}"><div class="aviso-sucesso" role="status">Rota cadastrada com sucesso.</div></c:if>
        <c:if test="${param.sucesso == 'editar'}"><div class="aviso-sucesso" role="status">Rota atualizada com sucesso.</div></c:if>
        <c:if test="${param.sucesso == 'excluir'}"><div class="aviso-sucesso" role="status">Rota excluída com sucesso.</div></c:if>

        <section class="painel">
            <div class="filtros">
                <input type="search" id="busca" data-busca="#tabelaRotas" autocomplete="off" placeholder="Buscar rota ou cooperativa..." aria-label="Buscar rota">
            </div>

            <table>
                <thead>
                <tr>
                    <th>Rota</th>
                    <th>Cooperativa</th>
                    <th><span style="position:absolute;left:-9999px">Ações</span></th>
                </tr>
                </thead>
                <tbody id="tabelaRotas">
                <c:forEach items="${rotas}" var="r">
                    <tr>
                        <td><c:out value="${r.nomeRota}"/></td>
                        <td><c:out value="${cooperativas[r.idCooperativa]}" default="—"/></td>
                        <td class="acoes">
                            <button type="button" class="icone editar btn-editar"
                                    aria-label="Editar <c:out value='${r.nomeRota}'/>"
                                    data-id="${r.idRota}" data-coop="${r.idCooperativa}"
                                    data-nome="<c:out value='${r.nomeRota}'/>">
                                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 17.25V21h3.75L17.8 9.94l-3.75-3.75L3 17.25zm17.7-10.2a1 1 0 0 0 0-1.41l-2.34-2.34a1 1 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>
                            </button>
                            <button type="button" class="icone excluir btn-excluir"
                                    aria-label="Excluir <c:out value='${r.nomeRota}'/>"
                                    data-id="${r.idRota}" data-nome="<c:out value='${r.nomeRota}'/>">
                                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <c:if test="${empty rotas}">
                <p class="vazio">Nenhuma rota cadastrada ainda. Clique em “Nova rota” para adicionar a primeira.</p>
            </c:if>
        </section>
    </main>
</div>

<c:set var="opcoesCoop">
    <c:forEach items="${cooperativas}" var="c"><option value="${c.key}"><c:out value="${c.value}"/></option></c:forEach>
</c:set>

<!-- ===== MODAL: NOVA ROTA ===== -->
<dialog id="modalNovo" aria-labelledby="tituloNovo">
    <button type="button" class="fechar" data-fechar aria-label="Fechar">&times;</button>
    <h2 id="tituloNovo">Nova rota</h2>
    <p class="sub">Nome da rota e a cooperativa que a realiza.</p>
    <form method="post" action="${ctx}/rota" accept-charset="UTF-8">
        <input type="hidden" name="acao" value="cadastrar">
        <label for="novoNome">Nome da rota</label>
        <input type="text" id="novoNome" name="nomeRota" placeholder="Ex: Zona Sul - Segunda" required maxlength="100">
        <label for="novaCoop">Cooperativa</label>
        <select id="novaCoop" name="idCooperativa" required>
            <option value="" disabled selected>Selecione a cooperativa</option>
            ${opcoesCoop}
        </select>
        <div class="rodape">
            <button type="button" class="btn btn-contorno" data-fechar>Cancelar</button>
            <button type="submit" class="btn btn-verde">Salvar</button>
        </div>
    </form>
</dialog>

<!-- ===== MODAL: EDITAR ROTA ===== -->
<dialog id="modalEditar" aria-labelledby="tituloEditar">
    <button type="button" class="fechar" data-fechar aria-label="Fechar">&times;</button>
    <h2 id="tituloEditar">Editar rota</h2>
    <p class="sub">Altere o nome da rota ou a cooperativa responsável.</p>
    <form method="post" action="${ctx}/rota" accept-charset="UTF-8">
        <input type="hidden" name="acao" value="editar">
        <input type="hidden" name="idRota" id="editarId">
        <label for="editarNome">Nome da rota</label>
        <input type="text" id="editarNome" name="nomeRota" required maxlength="100">
        <label for="editarCoop">Cooperativa</label>
        <select id="editarCoop" name="idCooperativa" required>
            ${opcoesCoop}
        </select>
        <div class="rodape">
            <button type="button" class="btn btn-contorno" data-fechar>Cancelar</button>
            <button type="submit" class="btn btn-verde">Salvar</button>
        </div>
    </form>
</dialog>

<!-- ===== MODAL: EXCLUIR ROTA ===== -->
<dialog id="modalExcluir" aria-labelledby="tituloExcluir">
    <button type="button" class="fechar" data-fechar aria-label="Fechar">&times;</button>
    <h2 id="tituloExcluir">Excluir rota</h2>
    <p class="sub">Confirmar a exclusão de “<strong id="excluirNome"></strong>”? Essa ação não pode ser desfeita.</p>
    <form method="post" action="${ctx}/rota" accept-charset="UTF-8">
        <input type="hidden" name="acao" value="excluir">
        <input type="hidden" name="idRota" id="excluirId">
        <div class="rodape">
            <button type="button" class="btn btn-contorno" data-fechar>Cancelar</button>
            <button type="submit" class="btn btn-vinho">Excluir</button>
        </div>
    </form>
</dialog>

<script>
    const $ = (id) => document.getElementById(id);
    $("btnNovo").addEventListener("click", () => $("modalNovo").showModal());
    document.querySelectorAll(".btn-editar").forEach((btn) => {
        btn.addEventListener("click", () => {
            $("editarId").value = btn.dataset.id;
            $("editarNome").value = btn.dataset.nome;
            $("editarCoop").value = btn.dataset.coop;
            $("modalEditar").showModal();
        });
    });
    document.querySelectorAll(".btn-excluir").forEach((btn) => {
        btn.addEventListener("click", () => {
            $("excluirId").value = btn.dataset.id;
            $("excluirNome").textContent = btn.dataset.nome;
            $("modalExcluir").showModal();
        });
    });
    document.querySelectorAll("[data-fechar]").forEach((el) => {
        el.addEventListener("click", () => el.closest("dialog").close());
    });
</script>
<script src="${pageContext.request.contextPath}/scripts/busca-ao-vivo.js?v=2"></script>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
