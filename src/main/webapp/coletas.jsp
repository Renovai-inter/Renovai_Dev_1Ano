
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Coletas | Renovaí</title>
</head>
<body>
    <h1>Coletas</h1>

    <p>
        <a href="${pageContext.request.contextPath}/nova-coleta.jsp">
            Cadastrar coleta
        </a>
    </p>

    <table border="1" cellpadding="8">
        <thead>
            <tr>
                <th>Código</th>
                <th>Cooperativa</th>
                <th>Tipo</th>
                <th>Data agendada</th>
                <th>Peso total (kg)</th>
                <th>Status</th>
                <th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="coleta" items="${coletas}">
                <tr>
                    <td><c:out value="${coleta.idColeta}"/></td>
                    <td><c:out value="${coleta.idCooperativa}"/></td>
                    <td><c:out value="${coleta.tipo}"/></td>
                    <td><c:out value="${coleta.dataAgendada}"/></td>
                    <td><c:out value="${coleta.pesoTotalKg}"/></td>
                    <td><c:out value="${coleta.status}"/></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/coleta?acao=detalhes&id=${coleta.idColeta}">
                            Detalhes
                        </a>

                        <c:if test="${coleta.status == 'AGENDADA'}">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/coleta">
                                <input type="hidden" name="acao" value="iniciar">
                                <input type="hidden" name="id"
                                       value="${coleta.idColeta}">
                                <button type="submit">Iniciar</button>
                            </form>

                            <form method="post"
                                  action="${pageContext.request.contextPath}/coleta">
                                <input type="hidden" name="acao" value="cancelar">
                                <input type="hidden" name="id"
                                       value="${coleta.idColeta}">
                                <button type="submit">Cancelar</button>
                            </form>
                        </c:if>

                        <c:if test="${coleta.status == 'EM_ANDAMENTO'}">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/coleta">
                                <input type="hidden" name="acao" value="finalizar">
                                <input type="hidden" name="id"
                                       value="${coleta.idColeta}">
                                <button type="submit">Finalizar</button>
                            </form>
                        </c:if>

                        <form method="post"
                              action="${pageContext.request.contextPath}/coleta"
                              onsubmit="return confirm('Deseja excluir esta coleta? Digite o código para confirmar.')">
                            <input type="hidden" name="acao" value="excluir">
                            <input type="hidden" name="id"
                                   value="${coleta.idColeta}">
                            <input type="number" name="confirmacao"
                                   placeholder="Código da coleta" required>
                            <button type="submit">Excluir</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty coletas}">
                <tr>
                    <td colspan="7">Nenhuma coleta cadastrada.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</body>
</html>