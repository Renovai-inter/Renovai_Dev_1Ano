<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>



<!DOCTYPE html>
<html>
<head>
    <title>Materiais</title>
</head>

<body>

<h1>Materiais cadastrados</h1>

<ul>


<%
    List<Material> materiais =
            (List<Material>) request.getAttribute("materiais");

    if (materiais != null) {

        for (Material material : materiais) {
%>

            <li>

                <form action="${pageContext.request.contextPath}/material" method="post">

                    <input type="hidden" name="acao" value="editar">

                    <input type="hidden"
                           name="idMaterial"
                           value="<%= material.getIdMaterial() %>">

                    <input type="text"
                           name="Nome"
                           value="<%= material.getNome() %>">

                    <input type="text"
                           name="Categoria"
                           value="<%= material.getCategoria() %>">

                    <button type="submit">Editar</button>

                </form>

                <form action="${pageContext.request.contextPath}/material" method="post">

                    <input type="hidden" name="acao" value="excluir">

                    <input type="hidden" name="idMaterial" value="<%= material.getIdMaterial() %>">

                    <button type="submit">Excluir</button>

                </form>
            </li>

<%
        }
    }
%>

</ul>

<h2>Cadastrar material</h2>

<form action="${pageContext.request.contextPath}/material" method="post">

    <label>Nome:</label>
    <input type="text" name="Nome">

    <label>Categoria:</label>
    <input type="text" name="Categoria">

    <button type="submit">Cadastrar</button>

</form>


</body>
</html>