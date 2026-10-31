/* Busca ao vivo das listagens da área administrativa.
 *
 * A cada letra digitada, as linhas da tabela que não combinam somem na hora.
 *
 *   <input data-busca="#idDaTabela">                       procura em toda a linha
 *   <input data-busca="#idDaTabela" data-busca-filtros="#categoria">
 *   <select id="categoria" data-filtra="categoria">         só mostra linhas cujo data-categoria é igual
 *
 * - Ignora maiúsculas e acentos ("joao" acha "João").
 * - Várias palavras: todas precisam aparecer na linha ("maria santo" acha "Maria ... Santo André").
 * - Números: "11222333" acha o CNPJ "11.222.333/0001-81".
 * - Esc limpa o campo. Se o campo estiver num formulário, Enter não recarrega a página.
 */
(function () {
    'use strict';

    function normalizar(texto) {
        return (texto || '').toLowerCase().normalize('NFD').replace(/[̀-ͯ]/g, '');
    }

    function soDigitos(texto) {
        return texto.replace(/\D/g, '');
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
        var textos = linhas.map(function (linha) {
            var texto = normalizar(linha.textContent);
            return { texto: texto, digitos: soDigitos(texto) };
        });

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

        // Aviso para leitores de tela
        var status = document.createElement('span');
        status.setAttribute('role', 'status');
        status.setAttribute('aria-live', 'polite');
        status.style.cssText = 'position:absolute;left:-9999px';
        campo.insertAdjacentElement('afterend', status);

        function aplicar() {
            var bruto = campo.value.trim();
            var termos = normalizar(bruto).split(/\s+/).filter(Boolean);
            var visiveis = 0;

            linhas.forEach(function (linha, i) {
                var combina = termos.every(function (termo) {
                    if (textos[i].texto.indexOf(termo) !== -1) return true;
                    // "11222333" também acha "11.222.333/0001-81"
                    var digitos = soDigitos(termo);
                    return digitos.length >= 3 && /^[\d\s.\-\/()]+$/.test(termo)
                        && textos[i].digitos.indexOf(digitos) !== -1;
                });
                filtros.forEach(function (filtro) {
                    var atributo = filtro.dataset.filtra;
                    if (combina && filtro.value && atributo && linha.dataset[atributo] !== filtro.value) {
                        combina = false;
                    }
                });
                linha.style.display = combina ? '' : 'none';
                if (combina) visiveis++;
            });

            if (visiveis === 0) {
                celula.textContent = bruto
                    ? 'Nenhum resultado para “' + bruto + '”.'
                    : 'Nenhum resultado para os filtros escolhidos.';
            }
            vazio.style.display = visiveis === 0 ? '' : 'none';
            status.textContent = visiveis === 0
                ? 'Nenhum resultado'
                : visiveis + (visiveis === 1 ? ' resultado' : ' resultados');
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
