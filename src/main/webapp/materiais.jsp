<%--
  Created by IntelliJ IDEA.
  User: 20260060-ieg
  Date: 08/10/2026
  Time: 12:48
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="model.Material" %>
<%@ page import="java.util.List" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Materiais | Renovaí</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

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

        /* ---------- Menu lateral ---------- */
        .sidebar {
            width: 240px;
            flex-shrink: 0;
            background: var(--verde-menu);
            color: #fff;
            padding: 28px 16px;
            display: flex;
            flex-direction: column;
        }
        .sidebar .marca { padding: 0 12px 28px; }
        .sidebar .marca strong { display: block; font-size: 1.6rem; letter-spacing: -.5px; }
        .sidebar .marca span { font-size: .72rem; opacity: .85; }
        .sidebar nav { display: flex; flex-direction: column; gap: 6px; }
        .sidebar nav a {
            color: #fff;
            text-decoration: none;
            padding: 12px 14px;
            border-radius: 10px;
            font-size: .95rem;
        }
        .sidebar nav a:hover { background: rgba(255, 255, 255, .12); }
        .sidebar nav a.ativo { background: var(--verde-ativo); color: #2f4d33; font-weight: 500; }
        .sidebar .usuario {
            margin-top: auto;
            background: var(--verde-ativo);
            color: #2f4d33;
            border-radius: 14px;
            padding: 12px 14px;
            font-size: .85rem;
            font-weight: 500;
        }
        .sidebar .usuario small { display: block; font-weight: 400; font-size: .7rem; }

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

        /* Selo de categoria (cores do Figma) */
        .selo {
            display: inline-block;
            min-width: 112px;
            text-align: center;
            padding: 5px 16px;
            border-radius: 8px;
            font-size: .85rem;
            font-weight: 600;
            background: #e6e3a8;
            color: #7a7430;
        }
        .selo[data-categoria="PAPEIS"]      { background: #bfe0f5; color: #3b6e99; }
        .selo[data-categoria="PLASTICOS"]   { background: #cfe6c0; color: #5b7f45; }
        .selo[data-categoria="METAIS"]      { background: #c9d6e3; color: #3f5a73; }
        .selo[data-categoria="VIDROS"]      { background: #a6d9d3; color: #2e6e68; }
        .selo[data-categoria="ELETRONICOS"] { background: #d4c4ec; color: #6b4f9a; }
        .selo[data-categoria="OUTROS"]      { background: #e6e3a8; color: #7a7430; }

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
            .sidebar { width: 100%; padding: 16px; }
            .sidebar nav { flex-direction: row; flex-wrap: wrap; }
            .sidebar .usuario { display: none; }
            main { padding: 24px 16px; }
            thead th, tbody td, .filtros { padding-left: 16px; padding-right: 16px; }
        }
    </style>
</head>
<body>
<div class="layout">

    <!-- ===== MENU LATERAL (ajuste os hrefs conforme suas rotas) ===== -->
    <aside class="sidebar">
        <div class="marca">
            <strong>Renovaí</strong>
            <span>Área Administrativa</span>
        </div>
        <nav>
            <a href="#">Dashboard</a>
            <a href="#">Cooperativas</a>
            <a href="#">Cooperados</a>
            <a href="#">Coletas</a>
            <a href="${ctx}/material" class="ativo" aria-current="page">Materiais</a>
            <a href="#">Negociações</a>
        </nav>
        <div class="usuario">
            Nome usuário
            <small>Administrador</small>
        </div>
    </aside>

    <!-- ===== CONTEÚDO ===== -->
    <main>
        <header class="topo">
            <div>
                <h1>Materiais</h1>
                <p>Catálogo global de materiais recicláveis usados por cooperativas e negociações.</p>
            </div>
            <button type="button" class="btn btn-verde" id="btnNovo">Novo material</button>
        </header>

        <section class="painel">
            <div class="filtros">
                <input type="search" id="busca" placeholder="Buscar material..." aria-label="Buscar material">
                <select id="filtroCategoria" aria-label="Filtrar por categoria">
                    <option value="">Todas as categorias</option>
                    <option>PAPEIS</option>
                    <option>PLASTICOS</option>
                    <option>METAIS</option>
                    <option>VIDROS</option>
                    <option>ELETRONICOS</option>
                    <option>OUTROS</option>
                </select>
            </div>

            <table>
                <thead>
                <tr>
                    <th>Nome</th>
                    <th>Categoria</th>
                    <th><span class="sr-only" style="position:absolute;left:-9999px">Ações</span></th>
                </tr>
                </thead>
                <tbody id="tabelaMateriais">
                <c:forEach items="${materiais}" var="m">
                    <tr data-nome="<c:out value='${m.nome}'/>" data-categoria="<c:out value='${m.categoria}'/>">
                        <td><c:out value="${m.nome}"/></td>
                        <td><span class="selo" data-categoria="<c:out value='${m.categoria}'/>"><c:out value="${m.categoria}"/></span></td>
                        <td class="acoes">
                            <button type="button" class="icone editar btn-editar"
                                    aria-label="Editar <c:out value='${m.nome}'/>"
                                    data-id="${m.idMaterial}"
                                    data-nome="<c:out value='${m.nome}'/>"
                                    data-categoria="<c:out value='${m.categoria}'/>">
                                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M3 17.25V21h3.75L17.8 9.94l-3.75-3.75L3 17.25zm17.7-10.2a1 1 0 0 0 0-1.41l-2.34-2.34a1 1 0 0 0-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/></svg>
                            </button>
                            <button type="button" class="icone excluir btn-excluir"
                                    aria-label="Excluir <c:out value='${m.nome}'/>"
                                    data-id="${m.idMaterial}"
                                    data-nome="<c:out value='${m.nome}'/>">
                                <svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <c:if test="${empty materiais}">
                <p class="vazio">Nenhum material cadastrado ainda. Clique em “Novo material” para adicionar o primeiro.</p>
            </c:if>
            <p class="vazio" id="semResultado" hidden>Nenhum material encontrado para essa busca.</p>
        </section>
    </main>
</div>

<!-- ===== MODAL: NOVO MATERIAL ===== -->
<dialog id="modalNovo" aria-labelledby="tituloNovo">
    <button type="button" class="fechar" data-fechar aria-label="Fechar">&times;</button>
    <h2 id="tituloNovo">Novo material</h2>
    <p class="sub">Nome e categoria do material no catálogo geral.</p>
    <form method="post" action="${ctx}/material" accept-charset="UTF-8">
        <input type="hidden" name="acao" value="cadastrar">
        <label for="novoNome">Nome</label>
        <input type="text" id="novoNome" name="Nome" placeholder="Ex: PET Transparente" required maxlength="100">
        <label for="novaCategoria">Categoria</label>
        <select id="novaCategoria" name="Categoria" required>
            <option value="" disabled selected>Selecione a categoria</option>
            <option>PAPEIS</option>
            <option>PLASTICOS</option>
            <option>METAIS</option>
            <option>VIDROS</option>
            <option>ELETRONICOS</option>
            <option>OUTROS</option>
        </select>
        <div class="rodape">
            <button type="button" class="btn btn-contorno" data-fechar>Cancelar</button>
            <button type="submit" class="btn btn-verde">Salvar</button>
        </div>
    </form>
</dialog>

<!-- ===== MODAL: EDITAR MATERIAL ===== -->
<dialog id="modalEditar" aria-labelledby="tituloEditar">
    <button type="button" class="fechar" data-fechar aria-label="Fechar">&times;</button>
    <h2 id="tituloEditar">Editar material</h2>
    <p class="sub">Nome e categoria do material no catálogo geral.</p>
    <form method="post" action="${ctx}/material" accept-charset="UTF-8">
        <input type="hidden" name="acao" value="editar">
        <input type="hidden" name="idMaterial" id="editarId">
        <label for="editarNome">Nome</label>
        <input type="text" id="editarNome" name="Nome" required maxlength="100">
        <label for="editarCategoria">Categoria</label>
        <select id="editarCategoria" name="Categoria" required>
            <option value="" disabled>Selecione a categoria</option>
            <option>PAPEIS</option>
            <option>PLASTICOS</option>
            <option>METAIS</option>
            <option>VIDROS</option>
            <option>ELETRONICOS</option>
            <option>OUTROS</option>
        </select>
        <div class="rodape">
            <button type="button" class="btn btn-contorno" data-fechar>Cancelar</button>
            <button type="submit" class="btn btn-verde">Salvar</button>
        </div>
    </form>
</dialog>

<!-- ===== MODAL: EXCLUIR MATERIAL ===== -->
<dialog id="modalExcluir" aria-labelledby="tituloExcluir">
    <button type="button" class="fechar" data-fechar aria-label="Fechar">&times;</button>
    <h2 id="tituloExcluir">Excluir material</h2>
    <p class="sub">Revise antes de confirmar a exclusão de “<strong id="excluirNome"></strong>”. Essa ação não pode ser desfeita.</p>
    <form method="post" action="${ctx}/material" accept-charset="UTF-8">
        <input type="hidden" name="acao" value="excluir">
        <input type="hidden" name="idMaterial" id="excluirId">
        <div class="rodape">
            <button type="button" class="btn btn-contorno" data-fechar>Cancelar</button>
            <button type="submit" class="btn btn-vinho">Excluir</button>
        </div>
    </form>
</dialog>

<script>
    const $ = (id) => document.getElementById(id);
    const modalNovo = $("modalNovo");
    const modalEditar = $("modalEditar");
    const modalExcluir = $("modalExcluir");

    // Abrir modais
    $("btnNovo").addEventListener("click", () => modalNovo.showModal());

    document.querySelectorAll(".btn-editar").forEach((btn) => {
        btn.addEventListener("click", () => {
            $("editarId").value = btn.dataset.id;
            $("editarNome").value = btn.dataset.nome;
            $("editarCategoria").value = btn.dataset.categoria;
            modalEditar.showModal();
        });
    });

    document.querySelectorAll(".btn-excluir").forEach((btn) => {
        btn.addEventListener("click", () => {
            $("excluirId").value = btn.dataset.id;
            $("excluirNome").textContent = btn.dataset.nome;
            modalExcluir.showModal();
        });
    });

    // Fechar modais (botão X e Cancelar)
    document.querySelectorAll("[data-fechar]").forEach((el) => {
        el.addEventListener("click", () => el.closest("dialog").close());
    });

    // Busca por nome + filtro por categoria (no navegador)
    const busca = $("busca");
    const filtro = $("filtroCategoria");
    const linhas = document.querySelectorAll("#tabelaMateriais tr");

    function filtrar() {
        const termo = busca.value.trim().toLowerCase();
        const categoria = filtro.value;
        let visiveis = 0;

        linhas.forEach((tr) => {
            const okNome = tr.dataset.nome.toLowerCase().includes(termo);
            const okCat = !categoria || tr.dataset.categoria === categoria;
            tr.hidden = !(okNome && okCat);
            if (!tr.hidden) visiveis++;
        });

        $("semResultado").hidden = !(linhas.length > 0 && visiveis === 0);
    }

    busca.addEventListener("input", filtrar);
    filtro.addEventListener("change", filtrar);
</script>
</body>
</html>