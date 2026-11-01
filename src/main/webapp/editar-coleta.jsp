<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar coleta CO-<c:out value="${coleta.idColeta}"/> | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/renovai.css">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">
    <style>
        .edicao { max-width: 850px; }
        .edicao .card { display: grid; gap: 18px; }
        .edicao label { display: grid; gap: 7px; font-weight: 600; }
        .edicao input, .edicao select, .edicao textarea { box-sizing: border-box; width: 100%; padding: 11px 13px; border: 1px solid #d7d9d2; border-radius: 10px; font: inherit; }
        .edicao textarea { min-height: 120px; resize: vertical; }
        .edicao .acoes { display: flex; justify-content: flex-end; gap: 12px; flex-wrap: wrap; }
    </style>
</head>
<body>
<div class="layout">
    <%@ include file="/menu.jspf" %>
    <main class="edicao">
        <header class="topo"><div><h1>Editar coleta CO-<c:out value="${coleta.idColeta}"/></h1>
            <p><a href="${ctx}/coleta?acao=detalhes&amp;id=${coleta.idColeta}">‹ Voltar aos detalhes</a></p></div></header>
        <p>Tipo e status permanecem como registrados. Altere os vínculos, materiais e agendamento abaixo.</p>
        <c:if test="${not empty erroColeta}"><div class="operacao-aviso erro" role="alert"><c:out value="${erroColeta}"/></div></c:if>
        <form method="post" action="${ctx}/coleta" accept-charset="UTF-8">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<c:out value='${coleta.idColeta}'/>">
            <section class="card">
                <label for="idCooperativa">Cooperativa
                    <select id="idCooperativa" name="idCooperativa" data-posted="<c:out value='${dadosEnviados ? param.idCooperativa : coleta.relacionamentos.idCooperativa}'/>" required>
                        <option value="">Selecione a cooperativa</option>
                        <c:forEach items="${cooperativas}" var="op"><option value="${op.key}"><c:out value="${op.value}"/></option></c:forEach>
                    </select>
                </label>
                <c:if test="${coleta.tipo == 'COLETA_EXTERNA'}">
                    <label for="idRota">Rota
                        <select id="idRota" name="idRota" data-posted="<c:out value='${dadosEnviados ? param.idRota : coleta.relacionamentos.idRota}'/>">
                            <option value="">Sem rota</option>
                            <c:forEach items="${rotas}" var="op"><option value="${op.key}"><c:out value="${op.value}"/></option></c:forEach>
                        </select>
                    </label>
                </c:if>
                <label for="idCooperadoResponsavel">Cooperado responsável
                    <select id="idCooperadoResponsavel" name="idCooperadoResponsavel" data-posted="<c:out value='${dadosEnviados ? param.idCooperadoResponsavel : coleta.relacionamentos.idCooperadoResponsavel}'/>">
                        <option value="">Sem responsável</option>
                        <c:forEach items="${cooperados}" var="op"><option value="${op.key}"><c:out value="${op.value}"/></option></c:forEach>
                    </select>
                </label>
                <label for="dataAgendada">Data agendada
                    <input type="date" id="dataAgendada" name="dataAgendada" value="<c:out value='${dadosEnviados ? param.dataAgendada : coleta.dataAgendada}'/>" required>
                </label>
                <c:if test="${coleta.tipo == 'ENTREGA_COOPERATIVA'}">
                    <label for="nomeLocalOrigem">Nome do local de origem
                        <input type="text" id="nomeLocalOrigem" name="nomeLocalOrigem" placeholder="Ex: Galpão da cooperativa" maxlength="150" value="<c:out value='${dadosEnviados ? param.nomeLocalOrigem : coleta.nomeLocalOrigem}'/>">
                    </label>
                </c:if>
                <label for="observacoes">Observações
                    <textarea id="observacoes" name="observacoes" maxlength="500" placeholder="Informações adicionais sobre a coleta"><c:out value="${dadosEnviados ? param.observacoes : coleta.observacoes}"/></textarea>
                </label>
            </section>
            <section class="card">
                <h2>Materiais</h2>
                <div id="linhasMateriais"></div>
                <button class="btn btn-contorno" type="button" id="adicionarMaterial">+ Adicionar material</button>
                <div id="materiaisIniciais" hidden>
                    <c:choose>
                        <c:when test="${dadosEnviados}"><c:forEach items="${paramValues.idMaterial}" var="idMaterial" varStatus="indice"><span data-id="<c:out value='${idMaterial}'/>" data-quantidade="<c:out value='${paramValues.quantidadeKg[indice.index]}'/>"></span></c:forEach></c:when>
                        <c:otherwise><c:forEach items="${materiaisAtuais}" var="item"><span data-id="${item.key}" data-quantidade="${item.value}"></span></c:forEach></c:otherwise>
                    </c:choose>
                </div>
            </section>
            <div class="acoes"><a class="btn btn-contorno" href="${ctx}/coleta?acao=detalhes&amp;id=${coleta.idColeta}">Cancelar</a>
                <button class="btn btn-verde" type="submit">Salvar alterações</button></div>
        </form>
        <template id="modeloMaterial"><div class="linha-material" style="display:flex;gap:10px;flex-wrap:wrap;margin-bottom:10px">
            <select name="idMaterial" aria-label="Material" required style="flex:2;min-width:180px"><option value="">Selecione o material</option>
                <c:forEach items="${materiaisCatalogo}" var="material"><option value="${material.idMaterial}"><c:out value="${material.nome}"/></option></c:forEach>
            </select>
            <input type="number" name="quantidadeKg" min="0" step="0.01" placeholder="0" aria-label="Quantidade em kg" required style="flex:1;min-width:120px">
            <button type="button" class="btn btn-contorno" data-remover>Remover</button>
        </div></template>
        <script>
            document.querySelectorAll('[data-posted]').forEach(campo => { campo.value = campo.dataset.posted; });
            const linhas = document.getElementById('linhasMateriais');
            const modelo = document.getElementById('modeloMaterial');
            function adicionarMaterial(id, quantidade) {
                linhas.appendChild(modelo.content.cloneNode(true));
                const linha = linhas.lastElementChild;
                if (id) linha.querySelector('select').value = id;
                if (quantidade) linha.querySelector('input').value = quantidade;
            }
            document.querySelectorAll('#materiaisIniciais span').forEach(item => adicionarMaterial(item.dataset.id, item.dataset.quantidade));
            document.getElementById('adicionarMaterial').addEventListener('click', () => adicionarMaterial('', ''));
            linhas.addEventListener('click', evento => { if (evento.target.matches('[data-remover]')) evento.target.closest('.linha-material').remove(); });
        </script>
        <c:if test="${not empty erroColeta}"><script>document.querySelector('form input:invalid, form select:invalid, form textarea:invalid, form input:not([type=hidden])').focus();</script></c:if>
    </main>
</div>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
