/* Busca ao vivo das listagens da área administrativa.
 *
 * A cada letra digitada, as linhas da tabela que não combinam somem na hora.
 *
 *   <input data-busca="#idDaTabela">                       procura em toda a linha
 *   <input data-busca="#idDaTabela" data-busca-filtros="#categoria">
 *   <select id="categoria" data-filtra="categoria">         só mostra linhas cujo data-categoria é igual
 *
 * - Ignora maiúsculas e acentos ("joao" acha "João").
 * - Procura o trecho digitado em qualquer parte do texto, desde a primeira letra ("el" acha "Joel").
 * - O status (Ativo, Inativo, Concluída...) só entra na busca com 3 letras ou mais.
 * - Várias palavras: todas precisam aparecer na linha ("maria santo" acha "Maria ... Santo André").
 * - Números: "11222333" acha o CNPJ "11.222.333/0001-81".
 * - Mostra "Mostrando X de Y" enquanto há busca. Esc limpa o campo.
 * - Se o campo estiver num formulário, Enter não recarrega a página.
 */
(function () {
    'use strict';

    // Elementos que são só etiqueta de status: não entram na busca com 1 ou 2 letras
    var SELETOR_STATUS = '.selo, .coop-pill, .status, [data-busca-status]';

    function normalizar(texto) {
        return (texto || '').toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');
    }

    function soDigitos(texto) {
        return texto.replace(/\D/g, '');
    }

    // Junta o texto de todos os elementos separando-os por espaço
    // (assim "Maria" + "maria@x.com" coladas no HTML não viram "Mariamaria@x.com")
    function textoDe(no) {
        var partes = [];
        var andador = document.createTreeWalker(no, NodeFilter.SHOW_TEXT, null);
        while (andador.nextNode()) partes.push(andador.currentNode.nodeValue);
        return partes.join(' ');
    }

    // Texto da linha separado em "principal" e "status"
    function lerLinha(linha) {
        var clone = linha.cloneNode(true);
        var status = '';
        Array.prototype.forEach.call(clone.querySelectorAll(SELETOR_STATUS), function (el) {
            status += ' ' + textoDe(el);
            el.parentNode.removeChild(el);
        });
        var principal = normalizar(textoDe(clone));
        return {
            principal: principal,
            palavras: principal.split(/[^a-z0-9]+/).filter(Boolean),
            status: normalizar(status),
            digitos: soDigitos(principal)
        };
    }

    function iniciar(campo) {
        var alvo = document.querySelector(campo.dataset.busca);
        if (!alvo) return;
        var corpo = alvo.tBodies ? alvo.tBodies[0] : alvo;
        if (!corpo) return;

        // Só as linhas de dados (avisos como "Nenhum registro" ficam de fora)
        var linhas = Array.prototype.filter.call(corpo.rows, function (linha) {
            return !linha.classList.contains('sem-dados') && !linha.classList.contains('busca-vazio');
        });
        if (linhas.length === 0) return;

        var filtros = campo.dataset.buscaFiltros
            ? Array.prototype.slice.call(document.querySelectorAll(campo.dataset.buscaFiltros))
            : [];

        // O texto de cada linha é calculado uma vez só
        var textos = linhas.map(lerLinha);

        // Linha de aviso quando nada combina
        var tabela = alvo.closest('table') || alvo;
        var colunas = (tabela.tHead && tabela.tHead.rows[0]) ? tabela.tHead.rows[0].cells.length : 1;
        var vazio = document.createElement('tr');
        vazio.className = 'busca-vazio';
        var celula = document.createElement('td');
        celula.colSpan = colunas;
        celula.style.cssText = 'text-align:center;padding:40px 16px;color:#6f6f6f;white-space:normal;background:transparent;border:0';
        vazio.appendChild(celula);
        vazio.style.display = 'none';
        corpo.appendChild(vazio);

        // "Mostrando X de Y": deixa claro que a busca está funcionando
        var contagem = document.createElement('div');
        contagem.className = 'busca-contagem';
        contagem.setAttribute('role', 'status');
        contagem.setAttribute('aria-live', 'polite');
        contagem.style.cssText = 'display:none;padding:0 32px 12px;font-size:.85rem;color:#6f6f6f';
        var recipiente = campo.closest('.busca-caixa, .filtros, form') || campo.parentElement;
        recipiente.insertAdjacentElement('afterend', contagem);

        function combina(termo, i) {
            var t = textos[i];
            if (t.principal.indexOf(termo) !== -1) return true;
            if (termo.length >= 3 && t.status.indexOf(termo) !== -1) return true;
            // "11222333" também acha "11.222.333/0001-81"
            var digitos = soDigitos(termo);
            return digitos.length >= 3 && /^[\d\s.\-\/()]+$/.test(termo) && t.digitos.indexOf(digitos) !== -1;
        }

        function aplicar() {
            var bruto = campo.value.trim();
            var termos = normalizar(bruto).split(/\s+/).filter(function (t) { return /[a-z0-9]/.test(t); });
            var filtrando = termos.length > 0;
            var visiveis = 0;

            linhas.forEach(function (linha, i) {
                var ok = termos.every(function (termo) { return combina(termo, i); });
                filtros.forEach(function (filtro) {
                    var atributo = filtro.dataset.filtra;
                    if (filtro.value && atributo) {
                        filtrando = true;
                        if (linha.dataset[atributo] !== filtro.value) ok = false;
                    }
                });
                linha.style.display = ok ? '' : 'none';
                if (ok) visiveis++;
            });

            if (visiveis === 0) {
                celula.textContent = bruto
                    ? 'Nenhum resultado para “' + bruto + '”.'
                    : 'Nenhum resultado para os filtros escolhidos.';
            }
            vazio.style.display = visiveis === 0 ? '' : 'none';

            contagem.style.display = filtrando ? '' : 'none';
            contagem.textContent = visiveis === 0
                ? 'Nenhum resultado'
                : 'Mostrando ' + visiveis + ' de ' + linhas.length;
        }

        // Qualquer jeito de mudar o texto atualiza a lista na hora: digitar, colar, recortar,
        // apagar com o "x" do campo de busca ou usar o corretor/teclado do celular.
        ['input', 'keyup', 'paste', 'cut', 'search', 'change'].forEach(function (evento) {
            campo.addEventListener(evento, function () {
                // colar/recortar disparam antes do texto mudar: espera o navegador atualizar o campo
                if (evento === 'paste' || evento === 'cut') {
                    setTimeout(aplicar, 0);
                } else {
                    aplicar();
                }
            });
        });
        filtros.forEach(function (filtro) { filtro.addEventListener('change', aplicar); });
        campo.addEventListener('keydown', function (evento) {
            if (evento.key === 'Escape' && campo.value) {
                campo.value = '';
                aplicar();
            }
        });

        // Campo dentro de um formulário (busca antiga no servidor): a página não recarrega mais
        if (campo.form) {
            campo.form.addEventListener('submit', function (evento) { evento.preventDefault(); });
            Array.prototype.forEach.call(campo.form.querySelectorAll('button[type="submit"]'), function (botao) {
                botao.style.display = 'none';
            });
        }

        // Voltar pela seta do navegador pode devolver o campo já preenchido
        aplicar();
    }

    function iniciarTodos() {
        Array.prototype.forEach.call(document.querySelectorAll('input[data-busca]'), iniciar);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', iniciarTodos);
    } else {
        iniciarTodos();
    }
})();
