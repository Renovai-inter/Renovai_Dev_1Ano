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
    <aside class="sidebar">
        <div class="marca"><strong>Renovaí</strong><span>Área Administrativa</span></div>
        <nav>
            <a href="${ctx}/dashboard">Dashboard</a>
            <a href="${ctx}/cooperativa">Cooperativas</a>
            <a href="${ctx}/cooperado">Cooperados</a>
            <a href="${ctx}/coleta" class="ativo" aria-current="page">Coletas</a>
            <a href="${ctx}/material">Materiais</a>
            <a href="${ctx}/negociacao">Negociações</a>
        </nav>
    </aside>
    <main class="edicao">
        <header class="topo"><div><h1>Editar coleta CO-<c:out value="${coleta.idColeta}"/></h1>
            <p><a href="${ctx}/coleta?acao=detalhes&amp;id=${coleta.idColeta}">‹ Voltar aos detalhes</a></p></div></header>
        <p>Altere o agendamento e as observações. O tipo, os vínculos, os materiais e o peso permanecem como registrados.</p>
        <form method="post" action="${ctx}/coleta" accept-charset="UTF-8">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<c:out value='${coleta.idColeta}'/>">
            <section class="card">
                <label for="dataAgendada">Data agendada
                    <input type="date" id="dataAgendada" name="dataAgendada" value="<c:out value='${coleta.dataAgendada}'/>" required>
                </label>
                <c:if test="${coleta.tipo == 'ENTREGA_COOPERATIVA'}">
                    <label for="origemEntrega">Origem da entrega
                        <select id="origemEntrega" name="origemEntrega" required>
                            <option value="">Selecione a origem</option>
                            <c:forEach items="${origens}" var="origem">
                                <c:choose>
                                    <c:when test="${origem == coleta.origemEntrega}"><option value="${origem}" selected><c:out value="${origem}"/></option></c:when>
                                    <c:otherwise><option value="${origem}"><c:out value="${origem}"/></option></c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </select>
                    </label>
                    <label for="nomeLocalOrigem">Nome do local de origem
                        <input type="text" id="nomeLocalOrigem" name="nomeLocalOrigem" maxlength="150" value="<c:out value='${coleta.nomeLocalOrigem}'/>">
                    </label>
                </c:if>
                <label for="observacoes">Observações
                    <textarea id="observacoes" name="observacoes" maxlength="500"><c:out value="${coleta.observacoes}"/></textarea>
                </label>
            </section>
            <div class="acoes"><a class="btn btn-contorno" href="${ctx}/coleta?acao=detalhes&amp;id=${coleta.idColeta}">Cancelar</a>
                <button class="btn btn-verde" type="submit">Salvar alterações</button></div>
        </form>
    </main>
</div>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
