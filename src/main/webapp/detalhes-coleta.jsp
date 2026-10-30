<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="pt_BR" />
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<fmt:formatNumber value="${coleta.pesoTotalKg}" maxFractionDigits="2" var="pesoFmt"/>
<fmt:formatNumber value="${somaMateriais}" maxFractionDigits="2" var="somaFmt"/>
<c:set var="externa" value="${coleta.tipo == 'COLETA_EXTERNA'}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Coleta CO-${coleta.idColeta} | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/renovai.css">
    <link rel="icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">

    <style>
        .duas-colunas { display: grid; grid-template-columns: 1.2fr 1fr; gap: 24px; }
        .duas-colunas .card { margin-bottom: 24px; }

        .dados { display: grid; grid-template-columns: 1fr 1fr; gap: 16px 24px; margin: 0; }
        .dados dt { font-size: .75rem; color: var(--texto-suave); }
        .dados dd { margin: 2px 0 0; font-size: .95rem; }
        .dados .cheio { grid-column: 1 / -1; }
        .dados .suave { color: var(--texto-suave); }

        .acoes-topo { display: flex; gap: 10px; flex-wrap: wrap; align-items: center; }
        .acoes-topo form { margin: 0; }

        table { width: 100%; border-collapse: collapse; }
        thead th {
            background: #eef3e8;
            color: #555;
            font-size: .78rem;
            font-weight: 500;
            text-transform: uppercase;
            text-align: left;
            padding: 10px 16px;
        }
        thead th:last-child, tbody td:last-child { text-align: right; }
        tbody td { padding: 12px 16px; border-bottom: 1px solid var(--linha); }
        tbody tr:last-child td { border-bottom: 0; }
        .vazio { color: var(--texto-suave); margin: 8px 0 0; }
        .valor-grande { font-size: 1.1rem; font-weight: 500; }

        @media (max-width: 960px) {
            .duas-colunas { grid-template-columns: 1fr; }
            .dados { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
<div class="layout">

    <aside class="sidebar">
        <div class="marca">
            <strong>Renovaí</strong>
            <span>Área Administrativa</span>
        </div>
        <nav>
            <a href="${ctx}/dashboard">Dashboard</a>
            <a href="${ctx}/cooperativa">Cooperativas</a>
            <a href="${ctx}/cooperado">Cooperados</a>
            <a href="${ctx}/coleta" class="ativo" aria-current="page">Coletas</a>
            <a href="${ctx}/material">Materiais</a>
            <a href="${ctx}/negociacao">Negociações</a>
        </nav>
        <div class="usuario">
            Nome usuário
            <small>Administrador</small>
        </div>
    </aside>

    <main>
        <header class="topo">
            <div>
                <h1>Coleta CO-<c:out value="${coleta.idColeta}"/></h1>
                <p class="voltar">
                    <a href="${ctx}/coleta">‹ Coletas</a>
                    <span>•</span>
                    <span><c:out value="${nomeCooperativa}"/></span>
                    <span class="status" data-status="<c:out value='${coleta.status}'/>">
                        <c:choose>
                            <c:when test="${coleta.status == 'CONCLUIDA'}">Concluída</c:when>
                            <c:when test="${coleta.status == 'EM_ANDAMENTO'}">Em andamento</c:when>
                            <c:when test="${coleta.status == 'CANCELADA'}">Cancelada</c:when>
                            <c:when test="${coleta.status == 'AGENDADA'}">Agendada</c:when>
                            <c:otherwise><c:out value="${coleta.status}"/></c:otherwise>
                        </c:choose>
                    </span>
                    <span class="tipo">${externa ? 'Coleta externa' : 'Entrega na cooperativa'}</span>
                </p>
            </div>

            <div class="acoes-topo">
                <a class="btn btn-contorno" href="${ctx}/coleta?acao=editar&id=${coleta.idColeta}">Editar</a>
                <c:if test="${coleta.status == 'AGENDADA'}">
                    <form method="post" action="${ctx}/coleta" accept-charset="UTF-8">
                        <input type="hidden" name="acao" value="iniciar">
                        <input type="hidden" name="id" value="${coleta.idColeta}">
                        <button type="submit" class="btn btn-verde">Iniciar coleta</button>
                    </form>
                </c:if>

                <c:if test="${coleta.status == 'EM_ANDAMENTO'}">
                    <form method="post" action="${ctx}/coleta" accept-charset="UTF-8">
                        <input type="hidden" name="acao" value="finalizar">
                        <input type="hidden" name="id" value="${coleta.idColeta}">
                        <button type="submit" class="btn btn-verde">Finalizar coleta</button>
                    </form>
                </c:if>

                <c:if test="${coleta.status == 'AGENDADA' || coleta.status == 'EM_ANDAMENTO'}">
                    <form method="post" action="${ctx}/coleta" accept-charset="UTF-8"
                          onsubmit="return confirm('Cancelar esta coleta?')">
                        <input type="hidden" name="acao" value="cancelar">
                        <input type="hidden" name="id" value="${coleta.idColeta}">
                        <button type="submit" class="btn btn-contorno">Cancelar coleta</button>
                    </form>
                </c:if>

                <button type="button" class="btn btn-vinho" id="btnExcluir">Excluir</button>
            </div>
        </header>

        <c:if test="${param.sucesso == 'atualizada'}"><div class="aviso-sucesso" role="status">Coleta atualizada com sucesso.</div></c:if>
        <c:if test="${param.sucesso == 'iniciada'}">
            <div class="aviso-sucesso" role="status">Coleta iniciada com sucesso.</div>
        </c:if>
        <c:if test="${param.sucesso == 'finalizada'}">
            <div class="aviso-sucesso" role="status">Coleta finalizada com sucesso.</div>
        </c:if>

        <div class="duas-colunas">
            <!-- ===== DADOS DA COLETA ===== -->
            <section class="card">
                <h2>${externa ? 'Dados da coleta externa' : 'Dados da entrega na cooperativa'}</h2>

                <dl class="dados">
                    <c:choose>
                        <c:when test="${externa}">
                            <div>
                                <dt>Rota</dt>
                                <dd><c:out value="${empty nomeRota ? '—' : nomeRota}"/></dd>
                            </div>
                            <div>
                                <dt>Endereço</dt>
                                <dd>
                                    <c:choose>
                                        <c:when test="${empty coleta.relacionamentos.idEnderecoRota}"><span class="suave">(definido pela rota)</span></c:when>
                                        <c:otherwise>Endereço #<c:out value="${coleta.relacionamentos.idEnderecoRota}"/></c:otherwise>
                                    </c:choose>
                                </dd>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div>
                                <dt>Local de origem</dt>
                                <dd><c:out value="${empty coleta.nomeLocalOrigem ? '—' : coleta.nomeLocalOrigem}"/></dd>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <div>
                        <dt>Cooperado responsável</dt>
                        <dd><c:out value="${empty nomeCooperado ? '—' : nomeCooperado}"/></dd>
                    </div>
                    <div>
                        <dt>Data da coleta</dt>
                        <dd>
                            <c:choose>
                                <c:when test="${empty coleta.dataAgendada}">—</c:when>
                                <c:otherwise><fmt:formatNumber value="${coleta.dataAgendada.dayOfMonth}" pattern="00"/>/<fmt:formatNumber value="${coleta.dataAgendada.monthValue}" pattern="00"/>/<c:out value="${coleta.dataAgendada.year}"/></c:otherwise>
                            </c:choose>
                        </dd>
                    </div>
                    <div class="cheio">
                        <dt>Observações</dt>
                        <dd><c:out value="${empty coleta.observacoes ? '—' : coleta.observacoes}"/></dd>
                    </div>
                </dl>
            </section>

            <!-- ===== CONFERÊNCIA DE PESO ===== -->
            <section class="card">
                <h2>Conferência de peso</h2>

                <dl class="dados">
                    <div>
                        <dt>Peso total informado</dt>
                        <dd class="valor-grande"><c:out value="${pesoFmt}" default="0"/> kg</dd>
                    </div>
                    <div>
                        <dt>Soma dos materiais</dt>
                        <dd class="valor-grande"><c:out value="${somaFmt}" default="0"/> kg</dd>
                    </div>
                </dl>

                <c:choose>
                    <c:when test="${pesoConfere}">
                        <div class="banner ok" role="status" style="margin-top:18px">
                            ✓ Peso total informado <c:out value="${pesoFmt}" default="0"/> kg.
                            Soma dos materiais <c:out value="${somaFmt}" default="0"/> kg (confere).
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="banner alerta" role="status" style="margin-top:18px">
                            ⚠ Peso total informado <c:out value="${pesoFmt}" default="0"/> kg.
                            Soma dos materiais <c:out value="${somaFmt}" default="0"/> kg (confira).
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>

        <!-- ===== MATERIAIS ===== -->
        <section class="card">
            <h2>Materiais por módulo</h2>

            <c:choose>
                <c:when test="${empty materiais}">
                    <p class="vazio">Nenhum material registrado nesta coleta.</p>
                </c:when>
                <c:otherwise>
                    <table>
                        <thead>
                        <tr><th>Material</th><th>Peso (kg)</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${materiais}" var="item">
                            <tr>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty nomesMateriais[item.key]}"><c:out value="${nomesMateriais[item.key]}"/></c:when>
                                        <c:otherwise>Material #<c:out value="${item.key}"/></c:otherwise>
                                    </c:choose>
                                </td>
                                <td><fmt:formatNumber value="${item.value}" maxFractionDigits="2"/></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</div>

<!-- ===== MODAL: EXCLUIR COLETA ===== -->
<dialog id="modalExcluir" aria-labelledby="tituloExcluir">
    <button type="button" class="fechar" data-fechar aria-label="Fechar">&times;</button>
    <h2 id="tituloExcluir">Excluir coleta</h2>
    <p class="sub">Revise o impacto antes de confirmar a ação sobre “CO-<c:out value="${coleta.idColeta}"/>”.</p>

    <p class="titulo-secao">Registros relacionados afetados</p>
    <div class="resumo">
        <div><span>Cooperativa</span><span><c:out value="${nomeCooperativa}"/></span></div>
        <div><span>Materiais registrados</span><span>${materiais.size()}</span></div>
        <div><span>Peso total informado</span><span><c:out value="${pesoFmt}" default="0"/> kg</span></div>
    </div>

    <div class="alerta" role="note">
        Coletas não podem ser inativadas. Para desfazer uma coleta, use o status Cancelada.
        A exclusão remove o registro do histórico.
    </div>

    <form method="post" action="${ctx}/coleta" accept-charset="UTF-8">
        <input type="hidden" name="acao" value="excluir">
        <input type="hidden" name="id" value="${coleta.idColeta}">
        <input type="hidden" name="confirmacao" value="${coleta.idColeta}">

        <label for="excluirDigitado">Digite <strong>CO-<c:out value="${coleta.idColeta}"/></strong> para confirmar:</label>
        <input type="text" id="excluirDigitado" autocomplete="off">

        <div class="rodape">
            <button type="button" class="btn btn-contorno" data-fechar>Cancelar</button>
            <button type="submit" class="btn btn-vinho" id="btnConfirmarExcluir" disabled>Excluir</button>
        </div>
    </form>
</dialog>

<script>
    const modal = document.getElementById("modalExcluir");
    const digitado = document.getElementById("excluirDigitado");
    const confirmar = document.getElementById("btnConfirmarExcluir");
    const esperado = "CO-${coleta.idColeta}";

    document.getElementById("btnExcluir").addEventListener("click", () => {
        digitado.value = "";
        confirmar.disabled = true;
        modal.showModal();
    });

    digitado.addEventListener("input", () => {
        confirmar.disabled = digitado.value.trim().toUpperCase() !== esperado.toUpperCase();
    });

    document.querySelectorAll("[data-fechar]").forEach((el) => {
        el.addEventListener("click", () => modal.close());
    });
</script>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
