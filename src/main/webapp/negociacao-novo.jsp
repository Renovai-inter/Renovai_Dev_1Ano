<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ include file="negociacao-util.jspf" %>
<%
    Map<Long, String> cooperativas = (Map<Long, String>) request.getAttribute("cooperativas");
    Map<Long, String> empresas = (Map<Long, String>) request.getAttribute("empresas");
    Map<Long, String> materiais = (Map<Long, String>) request.getAttribute("materiais");
    List<String> statusOpcoes = (List<String>) request.getAttribute("statusOpcoes");
    List<String> autores = (List<String>) request.getAttribute("autores");
    String erro = (String) request.getAttribute("erro");

    if (cooperativas == null) cooperativas = new LinkedHashMap<Long, String>();
    if (empresas == null) empresas = new LinkedHashMap<Long, String>();
    if (materiais == null) materiais = new LinkedHashMap<Long, String>();
    if (statusOpcoes == null) statusOpcoes = new ArrayList<String>();
    if (autores == null) autores = new ArrayList<String>();

    // Se o formulario voltou por erro, mantem o que o usuario ja tinha digitado
    boolean voltouErro = "salvar".equals(request.getParameter("acao"));

    String idCoopSel = voltouErro ? request.getParameter("idCooperativa") : "";
    String idEmpSel = voltouErro ? request.getParameter("idEmpresa") : "";
    String idMatSel = voltouErro ? request.getParameter("idMaterial") : "";
    String quantidade = voltouErro ? request.getParameter("quantidade") : "";
    String valor = voltouErro ? request.getParameter("valor") : "";
    String autorSel = voltouErro ? request.getParameter("autor") : "";
    String statusSel = voltouErro ? request.getParameter("status") : "";
    String observacoes = voltouErro ? request.getParameter("observacoes") : "";

    // destaca "Negociações" no menu
    request.setAttribute("menuAtivo", "negociacoes");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Nova negociação</title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=12"><link rel="shortcut icon" href="${pageContext.request.contextPath}/images/Nimo.svg" type="image/svg+xml">

    <style>
        /* Visual da tela Nova negociacao (fica aqui para funcionar mesmo com o estilo.css desatualizado) */
        .nova-neg .topo-form h1 { font-size: 30px; font-weight: 600; margin: 0; line-height: 1.2; }
        .nova-neg .voltar { display: inline-flex; align-items: center; gap: 4px; margin-top: 4px; font-size: 12px; color: #444; text-decoration: none; }

        .nova-neg .cartao-form { border-radius: 20px; box-shadow: 0 4px 16px rgba(0,0,0,.08); }
        .nova-neg .cartao-form > h2.linha { margin: 0 0 16px; padding-bottom: 10px; font-size: 18px; font-weight: 500; color: #8b2346; border-bottom: 1px solid #ebe9e3; }

        .nova-neg .grade-form { display: grid; grid-template-columns: 1fr 1fr; gap: 16px 40px; }
        .nova-neg .grade-form .campo-largo { grid-column: 1 / -1; }

        .nova-neg .campo { display: flex; flex-direction: column; gap: 6px; margin: 0; min-width: 0; }
        .nova-neg .campo > label { margin: 0; font-size: 14px; font-weight: 500; color: #7b1e3a; }

        /* rotulos pequenos de Cooperativa / Recicladora */
        .nova-neg .campo-parte > label { display: inline-flex; align-items: center; gap: 6px; font-size: 10px; letter-spacing: .04em; text-transform: uppercase; }
        .nova-neg .campo-parte-coop > label { color: #3f6b3e; }
        .nova-neg .campo-parte-reci > label { color: #8b2346; }

        .nova-neg .campo input[type="text"],
        .nova-neg .campo textarea {
            width: 100%; box-sizing: border-box; margin: 0;
            border: 1px solid #dfe5d6; border-radius: 8px; background: #eef3e8;
            font-family: inherit; font-size: 13px; color: #222;
        }
        .nova-neg .campo input[type="text"] { height: 38px; padding: 0 14px; }
        .nova-neg .campo textarea { min-height: 96px; padding: 12px 14px; resize: vertical; }
        .nova-neg .campo input::placeholder, .nova-neg .campo textarea::placeholder { color: #7a7a7a; }

        /* campo numerico com setinhas */
        .nova-numero { position: relative; }
        .nova-numero input[type="text"] { padding-right: 32px; }
        .nova-spin { position: absolute; right: 10px; top: 50%; transform: translateY(-50%); display: flex; flex-direction: column; gap: 3px; }
        .nova-spin button { display: flex; padding: 0; border: 0; background: transparent; color: #8b2346; cursor: pointer; line-height: 0; }
        .nova-spin svg { width: 8px; height: 6px; }

        /* lista suspensa no visual do design (o select original continua no formulario, escondido) */
        .nova-dd { position: relative; }
        .nova-dd-botao {
            width: 100%; height: 38px; box-sizing: border-box; display: flex; align-items: center; justify-content: space-between; gap: 8px;
            padding: 0 14px; border: 1px solid #dfe5d6; border-radius: 8px; background: #eef3e8;
            font-family: inherit; font-size: 13px; color: #222; text-align: left; cursor: pointer;
        }
        .nova-dd-botao.vazio .nova-dd-texto { color: #7a7a7a; }
        .nova-dd-botao svg { flex: none; width: 10px; height: 8px; color: #8b2346; }
        .nova-dd-lista {
            position: absolute; left: 0; right: 0; top: calc(100% + 4px); z-index: 30;
            margin: 0; padding: 6px; list-style: none; max-height: 240px; overflow: auto;
            background: #fff; border: 1px solid #e3e1da; border-radius: 10px; box-shadow: 0 8px 24px rgba(0,0,0,.12);
        }
        .nova-dd-lista[hidden] { display: none; }
        .nova-dd-lista li { padding: 8px 10px; border-radius: 6px; font-size: 13px; cursor: pointer; }
        .nova-dd-lista li.placeholder { color: #8a8a8a; }
        .nova-dd-lista li:hover, .nova-dd-lista li:focus { background: #eef3e8; outline: none; }
        .nova-dd-lista li[aria-selected="true"] { background: #dcebd6; color: #2f5d2e; }

        /* rodape */
        .nova-neg .rodape-form { display: flex; justify-content: flex-end; gap: 12px; margin-top: 20px; }
        .nova-neg .rodape-form .botao { display: inline-flex; align-items: center; padding: 8px 18px; border-radius: 8px; font-size: 12px; font-weight: 500; text-decoration: none; cursor: pointer; }
        .nova-neg .rodape-form .botao-contorno { background: transparent; border: 1px solid #cfcfc8; color: #666; }
        .nova-neg .rodape-form .botao-escuro { background: #5f8a5e; border: 1px solid #5f8a5e; color: #fff; }

        @media (max-width: 800px) {
            .nova-neg .grade-form { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo nova-neg">

        <div class="topo topo-form">
            <div>
                <h1>Nova negociação</h1>
                <a href="negociacao" class="voltar">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="m15 18-6-6 6-6"/>
                    </svg>
                    Voltar para negociações
                </a>
            </div>
        </div>

        <% if (erro != null) { %>
            <div class="erro"><%= esc(erro) %></div>
        <% } %>

        <form method="post" action="negociacao">
            <input type="hidden" name="acao" value="salvar">

            <div class="cartao cartao-form">
                <h2 class="linha">Partes envolvidas</h2>
                <div class="grade-form">

                    <div class="campo campo-parte campo-parte-coop">
                        <label for="idCooperativa">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                <path d="M6 22V4a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v18Z"/><path d="M6 12H4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h2"/>
                                <path d="M18 9h2a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2h-2"/><path d="M10 6h4"/><path d="M10 10h4"/><path d="M10 14h4"/><path d="M10 18h4"/>
                            </svg>
                            Cooperativa
                        </label>
                        <select id="idCooperativa" name="idCooperativa">
                            <option value="">Selecione a cooperativa</option>
                            <% for (Map.Entry<Long, String> coop : cooperativas.entrySet()) { %>
                                <option value="<%= coop.getKey() %>" <%= String.valueOf(coop.getKey()).equals(idCoopSel) ? "selected" : "" %>><%= esc(coop.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo campo-parte campo-parte-reci">
                        <label for="idEmpresa">
                            <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                <path d="M2 20a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V8l-7 5V8l-7 5V4a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"/><path d="M17 18h1"/><path d="M12 18h1"/><path d="M7 18h1"/>
                            </svg>
                            Recicladora
                        </label>
                        <select id="idEmpresa" name="idEmpresa">
                            <option value="">Selecione a recicladora</option>
                            <% for (Map.Entry<Long, String> emp : empresas.entrySet()) { %>
                                <option value="<%= emp.getKey() %>" <%= String.valueOf(emp.getKey()).equals(idEmpSel) ? "selected" : "" %>><%= esc(emp.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                </div>
            </div>

            <div class="cartao cartao-form">
                <h2 class="linha">Proposta inicial</h2>
                <div class="grade-form">

                    <div class="campo">
                        <label for="idMaterial">Material</label>
                        <select id="idMaterial" name="idMaterial">
                            <option value="">Selecione o material</option>
                            <% for (Map.Entry<Long, String> mat : materiais.entrySet()) { %>
                                <option value="<%= mat.getKey() %>" <%= String.valueOf(mat.getKey()).equals(idMatSel) ? "selected" : "" %>><%= esc(mat.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="quantidade">Quantidade</label>
                        <div class="nova-numero">
                            <input type="text" id="quantidade" name="quantidade" inputmode="decimal" title="Quantidade em kg" placeholder="0kg" value="<%= esc(quantidade) %>">
                            <div class="nova-spin" data-alvo="quantidade" data-passo="1">
                                <button type="button" data-direcao="1" tabindex="-1" aria-label="Aumentar quantidade"><svg viewBox="0 0 8 6" aria-hidden="true"><path d="M0 6l4-6 4 6z" fill="currentColor"/></svg></button>
                                <button type="button" data-direcao="-1" tabindex="-1" aria-label="Diminuir quantidade"><svg viewBox="0 0 8 6" aria-hidden="true"><path d="M0 0l4 6 4-6z" fill="currentColor"/></svg></button>
                            </div>
                        </div>
                    </div>

                    <div class="campo">
                        <label for="valor">Valor</label>
                        <div class="nova-numero">
                            <input type="text" id="valor" name="valor" inputmode="decimal" title="Valor por kg (R$)" placeholder="R$0,00" value="<%= esc(valor) %>">
                            <div class="nova-spin" data-alvo="valor" data-passo="0.1">
                                <button type="button" data-direcao="1" tabindex="-1" aria-label="Aumentar valor"><svg viewBox="0 0 8 6" aria-hidden="true"><path d="M0 6l4-6 4 6z" fill="currentColor"/></svg></button>
                                <button type="button" data-direcao="-1" tabindex="-1" aria-label="Diminuir valor"><svg viewBox="0 0 8 6" aria-hidden="true"><path d="M0 0l4 6 4-6z" fill="currentColor"/></svg></button>
                            </div>
                        </div>
                    </div>

                    <div class="campo">
                        <label for="autor">Autor</label>
                        <select id="autor" name="autor">
                            <option value="">Selecione o autor</option>
                            <% for (String a : autores) { %>
                                <option value="<%= esc(a) %>" <%= a.equals(autorSel) ? "selected" : "" %>><%= esc(legivel(a)) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="status">Status Inicial</label>
                        <select id="status" name="status">
                            <option value="">Selecione o status inicial</option>
                            <% for (String st : statusOpcoes) { %>
                                <option value="<%= esc(st) %>" <%= st.equals(statusSel) ? "selected" : "" %>><%= esc(legivel(st)) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo campo-largo">
                        <label for="observacoes">Observações</label>
                        <textarea id="observacoes" name="observacoes"><%= esc(observacoes) %></textarea>
                    </div>

                </div>
            </div>

            <div class="rodape-form">
                <a href="negociacao" class="botao botao-form botao-contorno">Cancelar</a>
                <button type="submit" class="botao botao-form botao-escuro">Criar negociação</button>

            </div>

        </form>
    </div>
</div>
<script>
    // 1) Setinhas de subir/descer em Quantidade e Valor (so visual: o campo continua texto, formato 0,00)
    (function () {
        function lerNumero(texto) {
            var t = String(texto).trim().replace(/[^\d.,-]/g, '');
            if (t.indexOf(',') !== -1) {
                t = t.replace(/\./g, '').replace(',', '.');
            }
            var n = parseFloat(t);
            return isNaN(n) ? 0 : n;
        }

        document.querySelectorAll('.nova-spin button').forEach(function (botao) {
            botao.addEventListener('click', function () {
                var spin = botao.parentNode;
                var campo = document.getElementById(spin.getAttribute('data-alvo'));
                var passo = parseFloat(spin.getAttribute('data-passo'));
                var direcao = parseInt(botao.getAttribute('data-direcao'), 10);

                var novo = lerNumero(campo.value) + passo * direcao;
                if (novo < 0) {
                    novo = 0;
                }
                campo.value = novo.toFixed(2).replace('.', ',');
            });
        });
    })();

    // 2) Listas suspensas no visual do design. Cada <select> original continua no formulario (escondido)
    //    e e ele que vai para o servlet, entao os valores enviados nao mudam.
    (function () {
        var fechadores = [];

        document.querySelectorAll('.nova-neg select').forEach(function (select) {
            var caixa = document.createElement('div');
            caixa.className = 'nova-dd';

            var botao = document.createElement('button');
            botao.type = 'button';
            botao.className = 'nova-dd-botao';
            botao.setAttribute('aria-haspopup', 'listbox');
            botao.setAttribute('aria-expanded', 'false');
            botao.innerHTML = '<span class="nova-dd-texto"></span>' +
                '<svg viewBox="0 0 10 8" aria-hidden="true"><path d="M0 0h10L5 8z" fill="currentColor"/></svg>';

            var lista = document.createElement('ul');
            lista.className = 'nova-dd-lista';
            lista.setAttribute('role', 'listbox');
            lista.hidden = true;

            var itens = [];

            function atualizarBotao() {
                var opcao = select.options[select.selectedIndex];
                botao.querySelector('.nova-dd-texto').textContent = opcao ? opcao.text : '';
                botao.classList.toggle('vazio', !opcao || opcao.value === '');
                itens.forEach(function (item, i) {
                    item.setAttribute('aria-selected', i === select.selectedIndex ? 'true' : 'false');
                });
            }

            function abrir() {
                lista.hidden = false;
                botao.setAttribute('aria-expanded', 'true');
            }

            function fechar() {
                lista.hidden = true;
                botao.setAttribute('aria-expanded', 'false');
            }

            function escolher(indice) {
                select.selectedIndex = indice;
                atualizarBotao();
                fechar();
                botao.focus();
                select.dispatchEvent(new Event('change', { bubbles: true }));
            }

            Array.prototype.forEach.call(select.options, function (opcao, i) {
                var item = document.createElement('li');
                item.setAttribute('role', 'option');
                item.tabIndex = 0;
                item.textContent = opcao.text;
                if (opcao.value === '') {
                    item.className = 'placeholder';
                }

                item.addEventListener('click', function () { escolher(i); });
                item.addEventListener('keydown', function (evento) {
                    if (evento.key === 'Enter' || evento.key === ' ') {
                        evento.preventDefault();
                        escolher(i);
                    } else if (evento.key === 'ArrowDown' && itens[i + 1]) {
                        evento.preventDefault();
                        itens[i + 1].focus();
                    } else if (evento.key === 'ArrowUp' && itens[i - 1]) {
                        evento.preventDefault();
                        itens[i - 1].focus();
                    }
                });

                itens.push(item);
                lista.appendChild(item);
            });

            botao.addEventListener('click', function () {
                if (lista.hidden) { abrir(); } else { fechar(); }
            });

            document.addEventListener('click', function (evento) {
                if (!caixa.contains(evento.target)) { fechar(); }
            });

            fechadores.push(fechar);

            // clicar no rotulo foca o botao da lista
            var rotulo = document.querySelector('label[for="' + select.id + '"]');
            if (rotulo) {
                rotulo.addEventListener('click', function (evento) {
                    evento.preventDefault();
                    botao.focus();
                });
            }

            caixa.appendChild(botao);
            caixa.appendChild(lista);
            select.parentNode.insertBefore(caixa, select.nextSibling);
            select.style.display = 'none';

            atualizarBotao();
        });

        document.addEventListener('keydown', function (evento) {
            if (evento.key === 'Escape') {
                fechadores.forEach(function (fechar) { fechar(); });
            }
        });
    })();
</script>
<%@ include file="notificacao.jspf" %>
</body>
</html>

