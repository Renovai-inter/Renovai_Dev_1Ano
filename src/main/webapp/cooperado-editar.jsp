<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.principais.Cooperado" %>
<%!
    // Apenas para EXIBIR: textos com acento para os valores que vem do banco (ex.: NEGOCIACOES -> Negociações).
    // O value enviado no formulario continua sendo o valor original do banco.
    private static final String[][] ROTULOS = {
            // atividades
            {"COLETA_EXTERNA", "Coleta externa"},
            {"SEPARACAO_MATERIAIS", "Separação de materiais"},
            {"ORGANIZACAO_GALPAO", "Organização do galpão"},
            {"PRENSAGEM", "Prensagem"},
            {"PESAGEM", "Pesagem"},
            {"NEGOCIACOES", "Negociações"},
            {"ADMINISTRATIVO", "Administrativo"},
            // cargos
            {"COOPERADO", "Cooperado"},
            {"GESTOR", "Gestor"},
            {"TESOUREIRO", "Tesoureiro"},
            {"MOTORISTA", "Motorista"},
            // status
            {"ATIVO", "Ativo"},
            {"AFASTADO", "Afastado"},
            {"INATIVO", "Inativo"}
    };

    private String rotulo(String valor) {
        if (valor == null) {
            return "";
        }
        for (String[] r : ROTULOS) {
            if (r[0].equalsIgnoreCase(valor.trim())) {
                return r[1];
            }
        }
        // sem traducao: "NOME_DO_VALOR" vira "Nome do valor"
        String t = valor.trim().replace('_', ' ').toLowerCase();
        return t.isEmpty() ? "" : t.substring(0, 1).toUpperCase() + t.substring(1);
    }
%>
<%
    Cooperado c = (Cooperado) request.getAttribute("cooperado");

    // Pagina aberta direto, sem passar pelo servlet: volta para a lista
    if (c == null) {
        response.sendRedirect("cooperado");
        return;
    }

    Map<Long, String> cooperativas = (Map<Long, String>) request.getAttribute("cooperativas");
    List<String> cargos = (List<String>) request.getAttribute("cargos");
    List<String> statusOpcoes = (List<String>) request.getAttribute("statusOpcoes");
    Integer totalColetas = (Integer) request.getAttribute("totalColetas");
    String erro = (String) request.getAttribute("erro");
    String senhaNova = (String) request.getAttribute("senhaNova");

    // Protecao: se vier vazio, usa valores padrao em vez de dar erro
    if (cooperativas == null) {
        cooperativas = new LinkedHashMap<Long, String>();
    }

    if (cargos == null) {
        cargos = new ArrayList<String>();
    } else {
        // Cria uma lista modificável para permitir adicionar novos cargos
        cargos = new ArrayList<String>(cargos);
    }

    // Adiciona COOPERADO no início, caso ainda não exista
    if (!cargos.contains("COOPERADO")) {
        cargos.add(0, "COOPERADO");
    }

    if (statusOpcoes == null) {
        statusOpcoes = Arrays.asList("Ativo", "Afastado", "Inativo");
    }

    if (totalColetas == null) {
        totalColetas = 0;
    }

    List<String> atividadesOpcoes = (List<String>) request.getAttribute("atividadesOpcoes");
    if (atividadesOpcoes == null) atividadesOpcoes = new ArrayList<String>();
    String[] atividades = atividadesOpcoes.toArray(new String[0]);
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar cooperado</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=5">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/v2.css?v=1">
</head>
<body class="pagina-v2">
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">

        <form method="post" action="cooperado">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<%= c.getIdCooperado() %>">

            <div class="topo topo-form">
                <div class="titulo-volta">
                    <a href="cooperado?acao=ver&id=<%= c.getIdCooperado() %>" class="seta">&lsaquo;</a>
                    <div>
                        <h1>Editar cooperado <%= esc(nomeBonito(c.getNomeCompleto())) %></h1>
                        <p>Cooperados - <%= esc(c.getCodigoCooperado()) %></p>
                    </div>
                </div>
                <div class="acoes">
                    <button type="submit" class="botao botao-topo verde">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"/>
                            <polyline points="17 21 17 13 7 13 7 21"/><polyline points="7 3 7 8 15 8"/>
                        </svg>
                        Salvar
                    </button>
                    <a href="#excluir" class="botao botao-topo vinho">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="M3 6h18"/><path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"/><path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"/>
                            <line x1="10" x2="10" y1="11" y2="17"/><line x1="14" x2="14" y1="11" y2="17"/>
                        </svg>
                        Excluir
                    </a>
                </div>
            </div>

            <% if (erro != null) { %>
                <div class="erro"><%= esc(erro) %></div>
            <% } %>
            <% if ("cadastro".equals(request.getParameter("sucesso"))) { %><div class="operacao-aviso" role="status">Cooperado cadastrado com sucesso.</div><% } %>

            <% if (senhaNova != null) { %>
                <div class="sucesso">Senha redefinida. Nova senha temporária: <b><%= esc(senhaNova) %></b>.
                    Anote agora, ela não será exibida novamente.</div>
            <% } %>

            <div class="cartao cartao-form">
                <h2>Identificação</h2>
                <div class="grade">
                    <div class="campo">
                        <label for="nomeCompleto">Nome Completo</label>
                        <input type="text" id="nomeCompleto" name="nomeCompleto" value="<%= esc(c.getNomeCompleto()) %>">
                    </div>
                    <div class="campo">
                        <label for="email">E-mail</label>
                        <input type="text" id="email" name="email" value="<%= esc(c.getEmail()) %>">
                    </div>
                    <div class="campo">
                        <label for="status">Status</label>
                        <select id="status" name="status">
                            <% for (String s : statusOpcoes) { %>
                                <option value="<%= esc(s) %>" <%= c.getStatusEnum() != null && s.equals(c.getStatusEnum().name()) ? "selected" : "" %>><%= esc(rotulo(s)) %></option>
                            <% } %>
                        </select>
                    </div>
                </div>
            </div>

            <div class="cartao cartao-form">
                <h2>Vínculo e funções</h2>
                <div class="grade">
                    <div class="campo">
                        <label for="idCooperativa">Cooperativa</label>
                        <select id="idCooperativa" name="idCooperativa">
                            <option value="">Selecione a cooperativa</option>
                            <% for (Map.Entry<Long, String> coop : cooperativas.entrySet()) { %>
                                <option value="<%= coop.getKey() %>" <%= coop.getKey().equals(c.getIdCooperativa()) ? "selected" : "" %>><%= esc(coop.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="campo">
                        <label for="cargo">Cargo</label>
                        <select id="cargo" name="cargo">
                            <option value="" <%= c.getCargoEnum() == null ? "selected" : "" %>>Selecione o cargo</option>
                            <% for (String cg : cargos) { %>
                                <option value="<%= esc(cg) %>" <%= c.getCargoEnum() != null && cg.equals(c.getCargoEnum().name()) ? "selected" : "" %>><%= esc(rotulo(cg)) %></option>
                            <% } %>
                        </select>
                    </div>
                </div>

                <div class="campo topo-chips"><label>Atividades (múltipla escolha)</label></div>
                <div class="chips">
                    <% for (int i = 0; i < atividades.length; i++) { %>
                        <input type="checkbox" id="a<%= i %>" name="atividades" value="<%= esc(atividades[i]) %>" <%= c.getAtividades().contains(atividades[i]) ? "checked" : "" %>>
                        <label for="a<%= i %>"><%= esc(rotulo(atividades[i])) %></label>
                    <% } %>
                </div>
            </div>

            <div class="cartao cartao-form">
                <h2>Conta de acesso</h2>
                <div class="grade grade-conta">
                    <div class="dado">
                        <small>Usuário</small>
                        <span><%= esc(c.getNomeUsuario()) %></span>
                    </div>
                    <div class="dado">
                        <small>Senha</small>
                        <div class="linha-senha">
                            <% if (c.isSenhaTemporaria() && senhaNova == null) { %>
                                <span class="senha-temp">
                                    Senha temporária pendente de troca
                                </span>
                            <% } %>
                            <button type="submit" form="formSenha" class="botao botao-topo claro">
                                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                    <path d="m21 2-2 2m-7.61 7.61a5.5 5.5 0 1 1-7.778 7.778 5.5 5.5 0 0 1 7.777-7.777zm0 0L15.5 7.5m0 0 3 3L22 7l-3-3m-3.5 3.5L19 4"/>
                                </svg>
                                Redefinir senha
                            </button>
                        </div>
                    </div>
                </div>
                <p class="aviso">Por segurança, o painel nunca exibe a senha real - apenas o indicador de senha temporária.</p>
            </div>

        </form>

        <%-- Formulario separado: um formulario nao pode ficar dentro de outro --%>
        <form id="formSenha" method="post" action="cooperado">
            <input type="hidden" name="acao" value="redefinirSenha">
            <input type="hidden" name="id" value="<%= c.getIdCooperado() %>">
        </form>

    </div>
</div>

<%@ include file="modal-excluir.jspf" %>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>


