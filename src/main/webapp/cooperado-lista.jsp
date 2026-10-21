<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.ArrayList, model.principais.Cooperado" %>
<%
    ArrayList<Cooperado> lista = (ArrayList<Cooperado>) request.getAttribute("lista");
%>
<html>
<head>
    <title>Cooperados</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css">
</head>
<body>
    <div class="layout">

        <div class="menu">
            <div class="logo">Renovaí</div>
            <a href="#">Dashboard</a>
            <a href="#">Cooperativas</a>
            <a href="#" class="ativo">Cooperados</a>
            <a href="#">Coletas</a>
            <a href="#">Materiais</a>
            <a href="#">Negociações</a>
            <div class="usuario">
     ess           <b>Nome usuário</b>
                <small>Administrador</small>
                <a href="cooperado-lista.jsp" class="ativo">Cooperados</a>
            </div>
        </div>

        <div class="conteudo">
            <h1><div class="topo">
                    <div>
                        <h1>Cooperados</h1>
                        <p>Todos os cooperados da plataforma</p>
                    </div>
                    <a href="cooperado-novo.jsp" class="botao verde">Cadastrar cooperado</a>
                </div></h1>
            <div class="cartao">
                <input class="busca" type="text" placeholder="Buscar por nome ou código...">

                <table>
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Usuário</th>
                            <th>Cooperativa</th>
                            <th>Cargo</th>
                            <th>Status</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr onclick="location.href='cooperado-ver.jsp'">
                            <td class="codigo">C0001</td>
                            <td>
                                <div class="usuario-celula">
                                    <span class="avatar">J</span>
                                    <div>
                                        <b>João Batista</b>
                                        <small>joao@gmail.com</small>
                                    </div>
                                </div>
                            </td>
                            <td>Cooperativa X</td>
                            <td>Cooperado</td>
                            <td><span class="selo">Ativo</span></td>
                            <td><a href="cooperado-editar.jsp" onclick="event.stopPropagation()">✎</a></td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</body>
</html>