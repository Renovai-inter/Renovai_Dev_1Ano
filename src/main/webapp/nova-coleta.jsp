<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nova coleta | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/renovai.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">


    <style>
        .grade { display: grid; grid-template-columns: 1fr 1fr; gap: 16px 24px; }
        .grade .cheio { grid-column: 1 / -1; }
        .grade [hidden] { display: none; }
        .bloco-tipo { display: contents; }

        .mat-cab, .mat-linha {
            display: grid;
            grid-template-columns: 1fr 180px 44px;
            gap: 16px;
            align-items: center;
        }
        .mat-cab {
            background: #eef3e8;
            border-radius: 8px;
            padding: 10px 16px;
            font-size: .78rem;
            color: #555;
            text-transform: uppercase;
        }
        .mat-linha { padding: 10px 16px 0; }
        .mat-linha select, .mat-linha input {
            width: 100%;
            font-family: inherit;
            font-size: .95rem;
            border: 1px solid var(--linha);
            border-radius: 12px;
            padding: 10px 14px;
            background: #fbfbf8;
        }
        .cab-secao { display: flex; justify-content: space-between; align-items: center; gap: 12px; margin-bottom: 18px; }
        .cab-secao h2 { margin: 0; }
        .btn-pequeno { padding: 6px 14px; font-size: .8rem; border-radius: 999px; }
        .peso-informado { max-width: 320px; margin-bottom: 18px; }
        #banner { margin-top: 20px; }
        .dica { font-size: .85rem; color: var(--texto-suave); margin: 12px 0 0; }
        .rodape-form { display: flex; justify-content: flex-end; gap: 12px; }

        @media (max-width: 820px) {
            .grade { grid-template-columns: 1fr; }
            .mat-cab { display: none; }
            .mat-linha { grid-template-columns: 1fr 120px 44px; }
        }
    </style>
</head>
<body>
<div class="layout">

    <%@ include file="/menu.jspf" %>

    <main>
        <header class="topo">
            <div>
                <h1>Nova coleta</h1>
                <p class="voltar"><a href="${ctx}/coleta">‹ Voltar para coletas</a></p>
            </div>
        </header>

        <c:if test="${not empty erroColeta}"><div class="operacao-aviso erro" role="alert"><c:out value="${erroColeta}"/></div></c:if>
        <form method="post" action="${ctx}/coleta" accept-charset="UTF-8" id="formColeta">
            <input type="hidden" name="acao" value="cadastrar">

            <!-- ===== DADOS GERAIS ===== -->
            <section class="card">
                <h2>Dados gerais</h2>

                <div class="grade">
                    <div class="campo">
                        <label for="idCooperativa">Cooperativa</label>
                        <c:choose>
                            <c:when test="${not empty cooperativas}">
                                <select id="idCooperativa" name="idCooperativa" data-posted="<c:out value='${param.idCooperativa}'/>" required>
                                    <option value="" disabled selected>Selecione a cooperativa</option>
                                    <c:forEach items="${cooperativas}" var="op">
                                        <option value="${op.key}"><c:out value="${op.value}"/></option>
                                    </c:forEach>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <input type="number" min="1" id="idCooperativa" name="idCooperativa"
                                       placeholder="ID da cooperativa" data-posted="<c:out value='${param.idCooperativa}'/>" required>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="campo">
                        <label for="tipo">Tipo de coleta</label>
                        <select id="tipo" name="tipo" data-posted="<c:out value='${param.tipo}'/>" required>
                            <option value="" disabled selected>Selecione o tipo de coleta</option>
                            <option value="COLETA_EXTERNA">Coleta externa</option>
                            <option value="ENTREGA_COOPERATIVA">Entrega na cooperativa</option>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="dataAgendada">Data</label>
                        <input type="date" id="dataAgendada" name="dataAgendada" data-posted="<c:out value='${param.dataAgendada}'/>" required>
                    </div>

                    <!-- Campos que só existem na coleta externa -->
                    <div class="bloco-tipo" id="blocoExterna" hidden>
                        <div class="campo">
                            <label for="idRota">Rota</label>
                            <c:choose>
                                <c:when test="${not empty rotas}">
                                    <select id="idRota" name="idRota" data-posted="<c:out value='${param.idRota}'/>" required>
                                        <option value="" disabled selected>Selecione a rota</option>
                                        <c:forEach items="${rotas}" var="op">
                                            <option value="${op.key}"><c:out value="${op.value}"/></option>
                                        </c:forEach>
                                    </select>
                                </c:when>
                                <c:otherwise>
                                    <input type="number" min="1" id="idRota" name="idRota"
                                           placeholder="ID da rota" data-posted="<c:out value='${param.idRota}'/>" required>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="campo">
                            <label for="idCooperadoResponsavel">Cooperado responsável</label>
                            <c:choose>
                                <c:when test="${not empty cooperados}">
                                    <select id="idCooperadoResponsavel" name="idCooperadoResponsavel" data-posted="<c:out value='${param.idCooperadoResponsavel}'/>" required>
                                        <option value="" disabled selected>Selecione o cooperado</option>
                                        <c:forEach items="${cooperados}" var="op">
                                            <option value="${op.key}"><c:out value="${op.value}"/></option>
                                        </c:forEach>
                                    </select>
                                </c:when>
                                <c:otherwise>
                                    <input type="number" min="1" id="idCooperadoResponsavel"
                                           name="idCooperadoResponsavel" placeholder="ID do cooperado" data-posted="<c:out value='${param.idCooperadoResponsavel}'/>" required>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>

                    <!-- Campos que só existem na entrega na cooperativa -->
                    <div class="bloco-tipo" id="blocoEntrega" hidden>
                        <div class="campo">
                            <label for="nomeLocalOrigem">Nome do local de origem</label>
                            <input type="text" id="nomeLocalOrigem" name="nomeLocalOrigem" data-posted="<c:out value='${param.nomeLocalOrigem}'/>" maxlength="150">
                        </div>
                    </div>

                    <div class="campo cheio">
                        <label for="observacoes">Observações</label>
                        <textarea id="observacoes" name="observacoes" maxlength="500"><c:out value="${dadosEnviados ? param.observacoes : ''}"/></textarea>
                    </div>
                </div>
            </section>

            <!-- ===== MATERIAIS ===== -->
            <section class="card">
                <div class="cab-secao">
                    <h2>Materiais da coleta</h2>
                    <button type="button" class="btn btn-contorno btn-pequeno" id="btnNovoMaterial">+ Novo material</button>
                </div>

                <div class="campo peso-informado">
                    <label for="pesoInformado">Peso total informado (kg)</label>
                    <input type="number" id="pesoInformado" name="pesoTotalKg" min="0" step="0.01" placeholder="0">
                </div>

                <div class="mat-cab" aria-hidden="true">
                    <span>Material</span><span>Peso (kg)</span><span></span>
                </div>
                <div id="linhasMateriais"></div>
                <div id="materiaisEnviados" hidden><c:if test="${dadosEnviados}"><c:forEach items="${paramValues.idMaterial}" var="materialEnviado" varStatus="indice"><span data-material="<c:out value='${materialEnviado}'/>" data-quantidade="<c:out value='${paramValues.quantidadeKg[indice.index]}'/>"></span></c:forEach></c:if></div>

                <c:if test="${empty materiaisCatalogo}">
                    <p class="dica">Nenhum material no catálogo. <a href="${ctx}/material">Cadastre materiais</a> antes de registrar a coleta.</p>
                </c:if>

                <div class="banner" id="banner" role="status" aria-live="polite"></div>
            </section>

            <div class="rodape-form">
                <a href="${ctx}/coleta" class="btn btn-contorno">Cancelar</a>
                <button type="submit" class="btn btn-verde">Registrar coleta</button>
            </div>
        </form>
    </main>
</div>

<!-- Modelo de uma linha de material -->
<template id="modeloLinha">
    <div class="mat-linha">
        <select name="idMaterial" required aria-label="Material">
            <option value="" selected disabled>Selecione o material</option>
            <c:forEach items="${materiaisCatalogo}" var="mat">
                <option value="${mat.idMaterial}"><c:out value="${mat.nome}"/></option>
            </c:forEach>
        </select>
        <input type="number" name="quantidadeKg" min="0" step="0.01" placeholder="0" required aria-label="Peso em kg">
        <button type="button" class="icone excluir remover" aria-label="Remover material">
            <svg viewBox="0 0 24 24" fill="currentColor"><path d="M6 19a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/></svg>
        </button>
    </div>
</template>

<script>
    const $ = (id) => document.getElementById(id);

    // ----- Campos que mudam conforme o tipo -----
    const tipo = $("tipo");
    const blocos = {
        COLETA_EXTERNA: $("blocoExterna"),
        ENTREGA_COOPERATIVA: $("blocoEntrega")
    };

    function alternarTipo() {
        Object.entries(blocos).forEach(([valor, bloco]) => {
            const ativo = tipo.value === valor;
            bloco.hidden = !ativo;
            // campos desabilitados não são enviados nem validados
            bloco.querySelectorAll("input, select").forEach((el) => { el.disabled = !ativo; });
        });
    }
    tipo.addEventListener("change", alternarTipo);
    alternarTipo();

    // ----- Linhas de materiais -----
    const linhas = $("linhasMateriais");
    const modelo = $("modeloLinha");
    const banner = $("banner");
    const arredondar = (n) => Math.round(n * 100) / 100;
    const formatar = (n) => n.toLocaleString("pt-BR", { maximumFractionDigits: 2 });

    function atualizar() {
        // Impede escolher o mesmo material em duas linhas
        const selects = [...linhas.querySelectorAll("select")];
        const escolhidos = selects.map((s) => s.value).filter(Boolean);
        selects.forEach((s) => {
            [...s.options].forEach((o) => {
                if (o.value === "") return;
                o.disabled = o.value !== s.value && escolhidos.includes(o.value);
            });
        });

        // Conferência de peso
        const soma = arredondar(
            [...linhas.querySelectorAll("input[name=quantidadeKg]")]
                .reduce((total, i) => total + (parseFloat(i.value) || 0), 0)
        );
        const informadoTxt = $("pesoInformado").value;

        if (informadoTxt === "") {
            banner.className = "banner";
            banner.textContent = "Soma dos materiais: " + formatar(soma) + " kg";
        } else {
            const informado = arredondar(parseFloat(informadoTxt) || 0);
            const confere = informado === soma;
            banner.className = "banner " + (confere ? "ok" : "alerta");
            banner.textContent = (confere ? "✓ " : "⚠ ")
                + "Peso total informado " + formatar(informado)
                + " kg – soma dos materiais " + formatar(soma) + " kg"
                + (confere ? "" : " (confira)");
        }
    }

    function adicionarLinha() {
        linhas.appendChild(modelo.content.cloneNode(true));
        atualizar();
    }

    linhas.addEventListener("click", (e) => {
        const botao = e.target.closest(".remover");
        if (botao) {
            botao.closest(".mat-linha").remove();
            atualizar();
        }
    });
    linhas.addEventListener("input", atualizar);
    linhas.addEventListener("change", atualizar);
    $("pesoInformado").addEventListener("input", atualizar);
    $("btnNovoMaterial").addEventListener("click", adicionarLinha);

    document.querySelectorAll('[data-posted]').forEach((campo) => { if (campo.dataset.posted) campo.value = campo.dataset.posted; });
    alternarTipo();
    const enviados = [...document.querySelectorAll('#materiaisEnviados span')];
    if (enviados.length === 0) adicionarLinha();
    enviados.forEach((item) => {
        adicionarLinha();
        const linha = linhas.lastElementChild;
        linha.querySelector('select').value = item.dataset.material;
        linha.querySelector('input[name=quantidadeKg]').value = item.dataset.quantidade;
    });
    atualizar();
    <c:if test="${not empty erroColeta}">document.querySelector('form input:invalid, form select:invalid, form textarea:invalid, form input:not([type=hidden])').focus();</c:if>
</script>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
