<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- 1. Importe a classe do DAO e o pacote do seu modelo/entidade -->
<%@ page import="com.exemplo.dao.CooperativaDAO" %>
<%@ page import="com.exemplo.model.Cooperativa" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
<title>Lista de Usuários</title>
</head>
<body>
<h1>Usuários cadastrados</h1>

    <ul>
<%
        try {
            // 2. Instancia o DAO diretamente na página JSP
            CooperativaDAO dao = new CooperativaDAO();
            // 3. Executa a função do DAO
            List<Cooperativa> lista = dao.getUltimoId();

            // 4. Itera sobre os resultados exibindo na tela
            for (Cooperativa u : lista) {
    %>
<li><%= u.getNome() %> - <%= u.getEmail() %></li>
<%
            }
        } catch (Exception e) {
            out.println("Erro ao carregar dados: " + e.getMessage());
        }
    %>
</ul>
</body>
</html>