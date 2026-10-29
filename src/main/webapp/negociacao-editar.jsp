<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*, model.principais.Negociacao, model.auxiliares.Proposta" %>
<%@ include file="negociacao-util.jspf" %>
<%
    Negociacao negociacao = (Negociacao) request.getAttribute("negociacao");
    ArrayList<Proposta> movimentacoes = (ArrayList<Proposta>) request.getAttribute("movimentacoes");
    Map<Long, String> materiais = (Map<Long, String>) request.getAttribute("materiais");
    List<String> statusOpcoes = (List<String>) request.getAttribute("statusOpcoes");
    String erro = (String) request.getAttribute("erro");

    if (movimentacoes == null) movimentacoes = new ArrayList<Proposta>();
    if (materiais == null) materiais = new LinkedHashMap<Long, String>();
    if (statusOpcoes == null) statusOpcoes = new ArrayList<String>();

    // destaca "Negociações" no menu
    request.setAttribute("menuAtivo", "negociacoes");
%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Editar negociação <%= esc(negociacao.getCodigo()) %></title>
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilo.css?v=12">

</head>
<body>
<div class="layout">

    <%@ include file="menu.jspf" %>

    <div class="conteudo pagina-ver-neg pagina-editar-neg">

        <form method="post" action="negociacao" id="formEditarNeg">
            <input type="hidden" name="acao" value="atualizar">
            <input type="hidden" name="id" value="<%= negociacao.getIdNegociacao() %>">

            <div class="topo topo-form">
                <div>
                    <h1>Editar Negociação <%= esc(negociacao.getCodigo()) %></h1>
                    <div class="crumbs">
                        <a href="negociacao?acao=ver&id=<%= negociacao.getIdNegociacao() %>" class="voltar">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                <path d="m15 18-6-6 6-6"/>
                            </svg>
                            Negociações
                        </a>
                        <span>&bull; Atualizada em - <%= data(negociacao.getUltimaAtualizacao()) %></span>
                        <span class="selo selo-<%= classeStatus(negociacao.getStatus()) %>"><%= esc(legivel(negociacao.getStatus())) %></span>
                    </div>
                </div>
                <div class="acoes">
                    <button type="submit" class="botao claro" style="display:inline-flex;align-items:center;gap:6px;">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#3f6b3e" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="M15.2 3a2 2 0 0 1 1.4.6l3.8 3.8a2 2 0 0 1 .6 1.4V19a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2z"/>
                            <path d="M17 21v-7a1 1 0 0 0-1-1H8a1 1 0 0 0-1 1v7"/><path d="M7 3v4a1 1 0 0 0 1 1h7"/>
                        </svg>
                        Salvar
                    </button>
                    <a href="#excluir" class="botao vinho" style="display:inline-flex;align-items:center;gap:6px;">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                            <path d="M3 6h18"/><path d="M8 6V4h8v2"/><path d="M19 6l-1 14H6L5 6"/>
                        </svg>
                        Excluir
                    </a>
                </div>
            </div>

            <% if (erro != null) { %>
                <div class="erro"><%= esc(erro) %></div>
            <% } %>

            <%@ include file="negociacao-partes.jspf" %>

            <div class="cartao">
                <h2 class="linha">Resumo</h2>
                <div class="resumo-grade">

                    <div class="campo">
                        <label for="idMaterial">Material</label>
                        <select id="idMaterial" name="idMaterial">
                            <% for (Map.Entry<Long, String> mat : materiais.entrySet()) { %>
                                <option value="<%= mat.getKey() %>" <%= mat.getKey().equals(negociacao.getIdMaterial()) ? "selected" : "" %>><%= esc(mat.getValue()) %></option>
                            <% } %>
                        </select>
                    </div>

                    <div class="campo">
                        <label for="quantidade">Quantidade</label>
                        <div class="campo-numero campo-unidade unidade-kg">
                            <input type="text" id="quantidade" name="quantidade" inputmode="decimal" title="Quantidade em kg" value="<%= esc(numero(negociacao.getQuantidadeKg())) %>">
                            <div class="spin" data-alvo="quantidade" data-passo="1">
                                <button type="button" data-direcao="1" tabindex="-1" aria-label="Aumentar quantidade">
                                    <svg viewBox="0 0 8 6" aria-hidden="true"><path d="M0 6l4-6 4 6z" fill="currentColor"/></svg>
                                </button>
                                <button type="button" data-direcao="-1" tabindex="-1" aria-label="Diminuir quantidade">
                                    <svg viewBox="0 0 8 6" aria-hidden="true"><path d="M0 0l4 6 4-6z" fill="currentColor"/></svg>
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="campo">
                        <label for="valor">Valor atual</label>
                        <div class="campo-numero campo-unidade unidade-real">
                            <input type="text" id="valor" name="valor" inputmode="decimal" title="Valor em R$ por kg" value="<%= esc(numero(negociacao.getValorKgAtual())) %>">
                            <div class="spin" data-alvo="valor" data-passo="0.1">
                                <button type="button" data-direcao="1" tabindex="-1" aria-label="Aumentar valor">
                                    <svg viewBox="0 0 8 6" aria-hidden="true"><path d="M0 6l4-6 4 6z" fill="currentColor"/></svg>
                                </button>
                                <button type="button" data-direcao="-1" tabindex="-1" aria-label="Diminuir valor">
                                    <svg viewBox="0 0 8 6" aria-hidden="true"><path d="M0 0l4 6 4-6z" fill="currentColor"/></svg>
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="campo">
                        <label for="status">Status</label>
                        <select id="status" name="status">
                            <% for (String st : statusOpcoes) { %>
                                <option value="<%= esc(st) %>" <%= st.equals(negociacao.getStatus()) ? "selected" : "" %>><%= esc(legivel(st)) %></option>
                            <% } %>
                        </select>
                    </div>

                </div>
            </div>
        </form>

        <%-- fora do formulario acima: o botao abre o modal de proposta (que tem o proprio formulario) --%>
        <div class="cartao">
            <div class="neg-cartao-topo">
                <h2 class="linha">Linha do tempo</h2>
                <a href="#proposta" class="neg-pilula">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <circle cx="12" cy="12" r="10"/><path d="M8 12h8"/><path d="M12 8v8"/>
                    </svg>
                    Nova movimentação
                </a>
            </div>
            <%@ include file="negociacao-linha-tempo.jspf" %>
        </div>

    </div>
</div>


<%@ include file="modal-proposta.jspf" %>
<%@ include file="modal-excluir-negociacao.jspf" %>
<%@ include file="negociacao-notificacoes.jsp" %>

<script>
        // 1) Envia o formulario sem sair da tela. Se o servlet aceitar a alteracao, abre a pagina de destino,
        //    onde a notificacao desliza. Se der erro, mostra o aviso de erro aqui mesmo.
        (function () {
            var form = document.getElementById('formEditarNeg');
            if (!form) { return; }

            // avisa o arquivo de notificacoes que este formulario cuida do proprio envio
            form.setAttribute('data-neg-ajax', '');

            var botao = form.querySelector('button[type="submit"]');
            var enviando = false;
            var CHAVE = 'negNotificacao';
            var LIMITE = 10000000000; // 10^10: o maior valor que colunas numeric(12,2) aceitam

            if (!window.negMostrarErroTexto) {
                console.warn('[editar] o arquivo de notificacoes NAO esta incluido nesta pagina (inclua o mesmo arquivo usado nas outras telas)');
            }

            // ---- avisos ------------------------------------------------------------------------

            function erroBonito(titulo, mensagem) {
                if (window.negMostrarErro) { window.negMostrarErro(titulo, mensagem); }
                else { alert(titulo + '\n' + mensagem); }
            }

            // Marca "negociacao atualizada" para a proxima pagina mostrar o aviso
            // (guarda no navegador; o ?aviso= na URL, abaixo, e o reforco que nao depende disso).
            function marcarNotificacao(tipo) {
                if (window.negMarcar) { window.negMarcar(tipo); return; }
                var valor = JSON.stringify({ tipo: tipo, ts: Date.now() });
                try { sessionStorage.setItem(CHAVE, valor); } catch (e) { /* ok */ }
                try { localStorage.setItem(CHAVE, valor); } catch (e) { /* ok */ }
                try { document.cookie = CHAVE + '=' + encodeURIComponent(valor) + '; path=/; max-age=600; SameSite=Lax'; } catch (e) { /* ok */ }
            }

            // acrescenta ?aviso=tipo (ou &aviso=tipo) na URL de destino, respeitando o #
            function comAviso(url, tipo) {
                var partes = String(url).split('#');
                var base = partes[0];
                base += (base.indexOf('?') === -1 ? '?' : '&') + 'aviso=' + encodeURIComponent(tipo);
                return partes.length > 1 ? base + '#' + partes.slice(1).join('#') : base;
            }

            function irPara(url) {
                marcarNotificacao('atualizada');
                window.location.href = comAviso(url, 'atualizada');
            }

            // ---- "carregando" --------------------------------------------------------------------

            // feedback imediato: spinner no botao, cursor de espera e barra fina no topo
            function ligarCarregando() {
                if (botao) { botao.disabled = true; botao.classList.add('carregando'); }
                document.documentElement.classList.add('neg-enviando');
                if (!document.querySelector('.neg-progresso')) {
                    var barra = document.createElement('div');
                    barra.className = 'neg-progresso';
                    document.body.appendChild(barra);
                }
            }

            function desligarCarregando() {
                if (botao) { botao.disabled = false; botao.classList.remove('carregando'); }
                document.documentElement.classList.remove('neg-enviando');
                var barra = document.querySelector('.neg-progresso');
                if (barra) { barra.remove(); }
            }

            // ---- validacao antes de enviar (evita ir ao servidor com dado que o banco recusaria) ----

            // "1.234,50" -> 1234.5 ; devolve NaN se nao for um numero valido
            function numeroBR(texto) {
                var t = String(texto).replace(/R\$|\s/g, '');
                if (t === '') { return NaN; }
                if (t.indexOf(',') !== -1) {
                    if (!/^\d{1,3}(\.\d{3})*,\d+$|^\d+,\d+$/.test(t)) { return NaN; }
                    t = t.replace(/\./g, '').replace(',', '.');
                } else if (!/^\d+(\.\d+)?$/.test(t)) {
                    return NaN;
                }
                return parseFloat(t);
            }

            // devolve null se esta tudo certo, ou [titulo, mensagem, idDoCampo]
            function validar() {
                var qtd = numeroBR(document.getElementById('quantidade').value);
                var val = numeroBR(document.getElementById('valor').value);

                if (isNaN(qtd) || qtd < 0) {
                    return ['Quantidade inválida!', 'Informe a quantidade em kg usando apenas números, por exemplo 1500,00.', 'quantidade'];
                }
                if (isNaN(val) || val < 0) {
                    return ['Valor inválido!', 'Informe o valor atual por kg usando apenas números, por exemplo 12,50.', 'valor'];
                }
                if (qtd >= LIMITE) {
                    return ['Quantidade muito grande!', 'A quantidade informada passa do limite permitido. Confira o número e tente novamente.', 'quantidade'];
                }
                if (val >= LIMITE) {
                    return ['Valor muito grande!', 'O valor informado passa do limite permitido. Confira o número e tente novamente.', 'valor'];
                }
                if (qtd * val >= LIMITE) {
                    return ['Valor total muito grande!', 'A quantidade multiplicada pelo valor passa do limite permitido. Reduza um dos dois e tente novamente.', 'quantidade'];
                }
                return null;
            }

            // ---- envio ---------------------------------------------------------------------------

            form.addEventListener('submit', function (evento) {
                evento.preventDefault();
                if (enviando) { return; }

                var problema = validar();
                if (problema) {
                    erroBonito(problema[0], problema[1]);
                    var campo = document.getElementById(problema[2]);
                    if (campo) { campo.focus(); }
                    return;
                }

                enviando = true;
                ligarCarregando();

                fetch(form.action, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8' },
                    body: new URLSearchParams(new FormData(form)).toString(),
                    credentials: 'same-origin'
                }).then(function (resposta) {
                    console.info('[editar] resposta do servidor: status ' + resposta.status +
                        ', redirecionou = ' + resposta.redirected + ', url = ' + resposta.url);

                    // caso normal: o servlet redireciona depois de salvar
                    if (resposta.redirected) {
                        irPara(resposta.url);
                        return;
                    }

                    return resposta.text().then(function (html) {
                        var doc = new DOMParser().parseFromString(html, 'text/html');
                        var caixaErro = doc.querySelector('.erro');
                        var textoErro = caixaErro ? caixaErro.textContent.trim() : '';

                        // salvou, mas o servlet respondeu a pagina direto (sem redirecionar)
                        if (resposta.ok && textoErro === '') {
                            console.info('[editar] salvo sem redirecionamento; indo para a tela da negociacao');
                            irPara('negociacao?acao=ver&id=' +
                                encodeURIComponent(form.querySelector('input[name="id"]').value));
                            return;
                        }

                        // erro: mostra o aviso bonito aqui mesmo, sem recarregar
                        // (o que foi digitado continua nos campos e nada e marcado como sucesso)
                        console.info('[editar] erro do servidor:', textoErro || ('HTTP ' + resposta.status));
                        enviando = false;
                        desligarCarregando();
                        if (window.negMostrarErroTexto) {
                            window.negMostrarErroTexto(textoErro, 'atualizada');
                        } else {
                            erroBonito('Não foi possível atualizar!', 'Não foi possível salvar as alterações. Confira as informações e tente novamente.');
                        }
                    });
                }).catch(function (erro) {
                    console.info('[editar] falha no envio via fetch', erro);
                    enviando = false;
                    desligarCarregando();

                    // sem internet: avisa com a notificacao em vez de cair na tela de erro do navegador
                    if (navigator.onLine === false) {
                        erroBonito('Sem conexão!', 'Parece que você está sem internet. Verifique sua conexão e tente novamente.');
                        return;
                    }
                    // outra falha de rede: tenta o envio normal do formulario
                    form.submit();
                });
            });
        })();


    // 2) Setinhas de subir/descer em Quantidade e Valor atual (so visual: o campo continua texto, formato 0,00)
    (function () {
        function lerNumero(texto) {
            var t = String(texto).trim().replace(/[^\d.,-]/g, '');
            if (t.indexOf(',') !== -1) {
                t = t.replace(/\./g, '').replace(',', '.');
            }
            var n = parseFloat(t);
            return isNaN(n) ? 0 : n;
        }

        function formatar(n) {
            return n.toFixed(2).replace('.', ',');
        }

        document.querySelectorAll('.spin button').forEach(function (botao) {
            botao.addEventListener('click', function () {
                var spin = botao.parentNode;
                var campo = document.getElementById(spin.getAttribute('data-alvo'));
                var passo = parseFloat(spin.getAttribute('data-passo'));
                var direcao = parseInt(botao.getAttribute('data-direcao'), 10);

                var novo = lerNumero(campo.value) + passo * direcao;
                if (novo < 0) {
                    novo = 0;
                }
                campo.value = formatar(novo);
            });
        });
    })();

    // 3) Lista suspensa do Status no visual do design. O <select> original continua no formulario
    //    (escondido) e e ele que vai para o servlet, entao o valor enviado nao muda.
    (function () {
        var select = document.getElementById('status');
        if (!select) {
            return;
        }

        var caixa = document.createElement('div');
        caixa.className = 'dropdown-status';

        var botao = document.createElement('button');
        botao.type = 'button';
        botao.className = 'dropdown-status-botao';
        botao.setAttribute('aria-haspopup', 'listbox');
        botao.setAttribute('aria-expanded', 'false');
        botao.innerHTML = '<span class="dropdown-status-texto"></span>' +
            '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m6 9 6 6 6-6"/></svg>';

        var lista = document.createElement('ul');
        lista.className = 'dropdown-status-lista';
        lista.setAttribute('role', 'listbox');
        lista.hidden = true;

        var itens = [];

        function atualizarBotao() {
            var opcao = select.options[select.selectedIndex];
            botao.querySelector('.dropdown-status-texto').textContent = opcao ? opcao.text : '';
            itens.forEach(function (item, i) {
                item.setAttribute('aria-selected', i === select.selectedIndex ? 'true' : 'false');
            });
        }

        function abrir() {
            lista.hidden = false;
            caixa.classList.add('aberto');
            botao.setAttribute('aria-expanded', 'true');
        }

        function fechar() {
            lista.hidden = true;
            caixa.classList.remove('aberto');
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

            item.addEventListener('click', function () {
                escolher(i);
            });

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
            if (lista.hidden) {
                abrir();
            } else {
                fechar();
            }
        });

        document.addEventListener('click', function (evento) {
            if (!caixa.contains(evento.target)) {
                fechar();
            }
        });

        document.addEventListener('keydown', function (evento) {
            if (evento.key === 'Escape') {
                fechar();
            }
        });

        caixa.appendChild(botao);
        caixa.appendChild(lista);
        select.parentNode.insertBefore(caixa, select.nextSibling);
        select.style.display = 'none';

        atualizarBotao();
    })();
</script>
</body>
</html>

