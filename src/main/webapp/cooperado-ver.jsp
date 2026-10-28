<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.principais.Cooperado, java.time.format.DateTimeFormatter" %>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= esc(c.getNomeCompleto()) %></title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=8">
</head>
<%!
    // Apenas para EXIBIR: textos com acento para os valores que vem do banco (ex.: NEGOCIACOES -> Negociações).
    private static final String[][] ROTULOS = {
            // atividades
            {"COLETA_EXTERNA", "Coleta externa"},
            {"SEPARACAO_MATERIAIS", "Separação de materiais"},
            {"ORGANIZACAO_GALPAO", "Organização do galpão"},
            {"PRENSAGEM", "Prensagem"},
            {"PESAGEM", "Pesagem"},
            {"NEGOCIACOES", "Negociações"},
            {"ADMINISTRATIVO", "Administrativo"},
            // permissoes por modulo
            {"DASHBOARD", "Dashboard"},
            {"COOPERADOS", "Cooperados"},
            {"COLETAS", "Coletas"},
            {"VENDAS", "Vendas"},
            {"FINANCEIRO", "Financeiro"},
            {"RELATORIOS", "Relatórios"},
            {"CONFIGURACOES", "Configurações"},
            // cargos
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

    List<Long> permissoesDoCooperado = (List<Long>) request.getAttribute("permissoesDoCooperado");
    Map<Long, String> todasPermissoes = (Map<Long, String>) request.getAttribute("todasPermissoes");
    Integer totalColetas = (Integer) request.getAttribute("totalColetas");
    String erro = (String) request.getAttribute("erro");

    if (permissoesDoCooperado == null) permissoesDoCooperado = new ArrayList<Long>();
    if (todasPermissoes == null) todasPermissoes = new LinkedHashMap<Long, String>();
    if (totalColetas == null) totalColetas = 0;

    String dataCadastro = c.getDataCadastro() == null ? "-" : c.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    List<String> atividadesOpcoes = (List<String>) request.getAttribute("atividadesOpcoes");
    if (atividadesOpcoes == null) atividadesOpcoes = new ArrayList<String>();
    String[] atividades = atividadesOpcoes.toArray(new String[0]);
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><%= esc(c.getNomeCompleto()) %></title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=8">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">
        <div class="topo topo-form">
            <div class="titulo-volta">
                <a href="cooperado" class="seta">&lsaquo;</a>
                <div>
                    <h1><%= esc(c.getNomeCompleto()) %></h1>
                    <p>Cooperados - <%= esc(c.getCodigoCooperado()) %></p>
                </div>
            </div>
            <div class="acoes">
                <a href="cooperado?acao=editar&id=<%= c.getIdCooperado() %>" class="botao botao-topo claro">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M17 3a2.85 2.83 0 1 1 4 4L7.5 20.5 2 22l1.5-5.5Z"/><path d="m15 5 4 4"/>
                    </svg>
                    Editar
                </a>
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

        <div class="duas-colunas">
            <div class="cartao cartao-form">
                <h2>Identificação</h2>
                <div class="grade grade-ident">
                    <div class="dado"><small>Nome completo</small><span><%= esc(c.getNomeCompleto()) %></span></div>
                    <div class="dado"><small>Código</small><span><%= esc(c.getCodigoCooperado()) %></span></div>
                    <div class="dado"><small>E-mail</small><span><%= esc(c.getEmail()) %></span></div>
                    <div class="dado"><small>Data de cadastro</small><span><%= dataCadastro %></span></div>
                    <div class="dado"><small>Status</small><span class="selo selo-<%= esc(rotulo(c.getStatus()).toLowerCase()) %>"><%= esc(rotulo(c.getStatus())) %></span></div>
                </div>
            </div>

            <div class="cartao cartao-form">
                <h2>Vínculo e funções</h2>
                <div class="grade grade-ident">
                    <div class="dado"><small>Cooperativa</small><span><%= c.getNomeCooperativa() == null ? "-" : esc(c.getNomeCooperativa()) %></span></div>
                    <div class="dado"><small>Cargo</small><span><%= (c.getCargo() == null || c.getCargo().isEmpty()) ? "Cooperado" : esc(rotulo(c.getCargo())) %></span></div>
                </div>
                <div class="dado topo-chips"><small>Atividades (múltipla escolha)</small></div>
                <div class="chips so-leitura">
                    <% for (String a : atividades) { if (c.getAtividades().contains(a)) { %>
                        <label><%= esc(rotulo(a)) %></label>
                    <% } } %>
                    <% if (c.getAtividades().isEmpty()) { %>
                        <label>Nenhuma atividade</label>
                    <% } %>
                </div>
            </div>
        </div>

        <div class="cartao cartao-form">
            <h2>Permissões por módulo</h2>
            <div class="permissoes">
                <% for (Map.Entry<Long, String> perm : todasPermissoes.entrySet()) {
                    String modulo = perm.getValue();

                    if ("DASHBOARD".equalsIgnoreCase(modulo)
                            || "CONFIGURACOES".equalsIgnoreCase(modulo)
                            || "Dashboard".equalsIgnoreCase(modulo)
                            || "Configurações".equalsIgnoreCase(modulo)) {
                        continue;
                    }
                %>
                    <label class="perm perm-leitura"><%= esc(rotulo(modulo)) %>
                        <input type="checkbox" disabled <%= permissoesDoCooperado.contains(perm.getKey()) ? "checked" : "" %>>
                        <span class="chave"></span>
                    </label>
                <% } %>
            </div>
        </div>

        <div class="cartao cartao-form">
            <h2>Conta de acesso</h2>
            <div class="grade grade-conta">
                <div class="dado"><small>Usuário</small><span><%= esc(c.getNomeUsuario()) %></span></div>
                <div class="dado"><small>Senha</small>
                    <% if (c.isSenhaTemporaria()
                            && request.getAttribute("senhaNova") == null) { %>
                        <span class="senha-temp">
                            Senha temporária pendente de troca
                        </span>
                    <% } %>
                </div>
            </div>
            <p class="aviso">Por segurança, o painel nunca exibe a senha real - apenas o indicador de senha temporária.</p>
        </div>
    </div>
</div>

<%@ include file="modal-excluir.jspf" %>
</body>
</html>
