<link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=3">
<link rel="icon" href="${ctx}/images/Nimo.svg" type="image/svg+xml">
<style>

    html body .conteudo form .rodape-form a.botao.botao-form.botao-contorno {
        background: transparent !important;
        color: #6b9065 !important;
        border: 1px solid #8fb894 !important;
        border-radius: 8px !important;
        box-shadow: none !important;
        text-decoration: none !important;
        opacity: 1 !important;
    }

    html body .conteudo form .rodape-form button.botao.botao-form.botao-escuro {
        display: inline-flex !important;
        align-items: center !important;
        justify-content: center !important;
        background: #579660 !important;
        background-color: #579660 !important;
        color: #ffffff !important;
        border: 1px solid #579660 !important;
        border-radius: 12px !important;
        box-shadow: none !important;
        filter: none !important;
        opacity: 1 !important;
        font-family: 'Poppins', Arial, sans-serif !important;
        font-size: 14px !important;
        font-weight: 500 !important;
        padding: 6px 13px !important;
        cursor: pointer !important;
    }

    html body .conteudo form .rodape-form button.botao.botao-form.botao-escuro:hover {
        background: #478451 !important;
        background-color: #478451 !important;
        border-color: #478451 !important;
        color: #ffffff !important;
    }

    /* Aviso sobre usuário e senha temporária */
    html body .conteudo form .aviso-info {
        background: #E5EEDC !important;
        background-color: #E5EEDC !important;
        color: #456B4D !important;
        border: 1px solid #D5E4CE !important;
        border-radius: 10px !important;
        padding: 14px 16px !important;
        display: flex !important;
        align-items: center !important;
        gap: 10px !important;
        width: 100% !important;
        box-sizing: border-box !important;
        font-family: 'Poppins', Arial, sans-serif !important;
        font-size: 13px !important;
        line-height: 1.5 !important;
        margin: 0 0 22px 0 !important;
        box-shadow: none !important;
    }

    /* Ícone da mensagem */
    html body .conteudo form .aviso-info svg {
        color: #456B4D !important;
        stroke: #456B4D !important;
        flex-shrink: 0 !important;
    }
</style>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
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
            // permissoes por modulo
            {"DASHBOARD", "Dashboard"},
            {"COOPERADOS", "Cooperados"},
            {"COLETAS", "Coletas"},
            {"VENDAS", "Vendas"},
            {"FINANCEIRO", "Financeiro"},
            {"RELATORIOS", "Relatórios"},
            {"CONFIGURACOES", "Configurações"},
            // cargos
            {"COOPERADO", "Cooperado"},
            {"GESTOR", "Gestor"},
            {"TESOUREIRO", "Tesoureiro"},
            {"MOTORISTA", "Motorista"}
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
    Map<Long, String> cooperativas = (Map<Long, String>) request.getAttribute("cooperativas");
    Map<Long, String> todasPermissoes = (Map<Long, String>) request.getAttribute("todasPermissoes");
    List<String> cargos = (List<String>) request.getAttribute("cargos");
    String erro = (String) request.getAttribute("erro");

    if (cooperativas == null) cooperativas = new LinkedHashMap<Long, String>();
    if (todasPermissoes == null) todasPermissoes = new LinkedHashMap<Long, String>();
    if (cargos == null) {
        cargos = new ArrayList<String>();
    }

    // Adiciona Cooperado caso ainda não esteja na lista
    if (!cargos.contains("COOPERADO")) {
        cargos.add(0, "COOPERADO");
    }

    // Se o formulario voltou por erro, mantem o que o usuario ja tinha digitado
    boolean voltouErro = "salvar".equals(request.getParameter("acao"));

    String nome = voltouErro ? request.getParameter("nomeCompleto") : "";
    String email = voltouErro ? request.getParameter("email") : "";
    String idCoopSel = voltouErro ? request.getParameter("idCooperativa") : "";
    String cargoSel = voltouErro ? request.getParameter("cargo") : "";

    List<String> atividadesMarcadas = new ArrayList<String>();
    List<String> permissoesMarcadas = new ArrayList<String>();

    if (voltouErro) {
        String[] ativ = request.getParameterValues("atividades");
        String[] perms = request.getParameterValues("permissoes");

        if (ativ != null) {
            atividadesMarcadas = Arrays.asList(ativ);
        }
        if (perms != null) {
            permissoesMarcadas = Arrays.asList(perms);
        }
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
    <title>Novo cooperado</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=3">
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo">

        <div class="topo topo-form">
            <div>
                <h1>Novo cooperado</h1>
                <a href="cooperado" class="voltar">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="m15 18-6-6 6-6"/>
                    </svg>
                    Voltar para cooperados
                </a>
            </div>
        </div>

        <% if (erro != null) { %>
            <div class="erro"><%= esc(erro) %></div>
        <% } %>

        <form method="post" action="cooperado">
            <input type="hidden" name="acao" value="salvar">

            <div class="cartao cartao-form">
                <h2 class="linha">Dados do Cooperado</h2>
                <div class="grade-form">

                    <div class="campo">
                        <label for="idCooperativa">Cooperativa</label>
                        <select id="idCooperativa" name="idCooperativa">
                            <option value="">Selecione a cooperativa</option>
                            <% for (Map.Entry<Long, String> coop : cooperativas.entrySet()) { %>
                                <option value="<%= coop.getKey() %>" <%= String.valueOf(coop.getKey()).equals(idCoopSel) ? "selected" : "" %>><%= esc(coop.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="nomeCompleto">Nome Completo</label>
                        <input type="text" id="nomeCompleto" name="nomeCompleto" placeholder="Insira o nome completo do cooperado" value="<%= esc(nome) %>">
                    </div>

                    <div class="campo">
                        <label for="cargo">Cargo (opcional)</label>
                        <select id="cargo" name="cargo">
                            <option value="">Selecione o cargo</option>
                            <% for (String cg : cargos) { %>
                                <option value="<%= esc(cg) %>" <%= cg.equals(cargoSel) ? "selected" : "" %>><%= esc(rotulo(cg)) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="email">E-mail</label>
                        <input type="text" id="email" name="email" placeholder="Insira o e-mail do cooperado" value="<%= esc(email) %>">
                    </div>

                </div>
            </div>

            <div class="cartao cartao-form">
                <h2 class="linha">Atividades <small>(múltipla escolha)</small></h2>
                <div class="chips">
                    <% for (int i = 0; i < atividades.length; i++) { %>
                        <input type="checkbox" id="a<%= i %>" name="atividades" value="<%= esc(atividades[i]) %>" <%= atividadesMarcadas.contains(atividades[i]) ? "checked" : "" %>>
                        <label for="a<%= i %>"><%= esc(rotulo(atividades[i])) %></label>
                    <% } %>
                </div>
            </div>


<div class="cartao cartao-form">
    <h2 class="linha">Permissões por módulo</h2>
    <div class="permissoes">
        <% for (Map.Entry<Long, String> perm : todasPermissoes.entrySet()) {
               String modulo = perm.getValue();

               if ("DASHBOARD".equalsIgnoreCase(modulo)
                       || "CONFIGURACOES".equalsIgnoreCase(modulo)
                       || "Dashboard".equalsIgnoreCase(modulo)
                       || "Configurações".equalsIgnoreCase(modulo)) {
                   continue;
               }

               boolean marcada = !voltouErro || permissoesMarcadas.contains(String.valueOf(perm.getKey()));
        %>
            <label class="perm"><%= esc(rotulo(modulo)) %>
                <input type="checkbox" name="permissoes" value="<%= perm.getKey() %>" <%= marcada ? "checked" : "" %>>
                <span class="chave"></span>
            </label>
        <% } %>
    </div>
</div>


            <div class="aviso-info">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                    <circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/>
                </svg>
                O sistema gera automaticamente o nome de usuário e uma senha temporária no primeiro acesso.
            </div>

            <div class="rodape-form">
                <a href="cooperado" class="botao botao-form botao-contorno">Cancelar</a>
                <button type="submit" class="botao botao-form botao-escuro">Criar cooperado</button>
            </div>

        </form>
    </div>
</div>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
