/*
 * Tabelas responsivas: em telas estreitas (até 900px) cada linha vira um cartão,
 * com o nome da coluna ao lado do valor. Evita a rolagem horizontal das listas.
 * Uso: incluir o script na página; ele atua nas tabelas com data-responsiva
 * ou, quando nenhuma tem o atributo, em todas as tabelas com <thead>.
 */
(function () {
    'use strict';

    var CSS = '@media (max-width: 900px) {'
        + 'table.tr-cartoes { min-width: 0 !important; width: 100% !important; display: block; }'
        + 'table.tr-cartoes thead { position: absolute; left: -9999px; }'
        + 'table.tr-cartoes tbody { display: block; }'
        + 'table.tr-cartoes tr { display: block; margin: 0 0 12px; padding: 10px 14px; border: 1px solid #e3e3e3; border-radius: 12px; background: #fff; }'
        + 'table.tr-cartoes td { display: flex; justify-content: space-between; align-items: center; gap: 12px;'
        + ' width: auto !important; padding: 6px 0 !important; border: 0 !important; text-align: right !important; white-space: normal !important; }'
        + 'table.tr-cartoes td::before { content: attr(data-label); flex: 0 0 auto; font-weight: 600; color: #6f6f6f; text-align: left; }'
        + 'table.tr-cartoes td[data-label=""]::before { content: none; }'
        + 'table.tr-cartoes td[data-label=""] { justify-content: flex-end; }'
        + 'table.tr-cartoes td[colspan] { display: block; text-align: center !important; }'
        + 'table.tr-cartoes td[colspan]::before { content: none; }'
        + '}';

    function textoDe(celula) {
        return (celula.textContent || '').replace(/\s+/g, ' ').trim();
    }

    function preparar(tabela) {
        var cabecalho = tabela.tHead && tabela.tHead.rows[0];
        if (!cabecalho) return;
        var rotulos = Array.prototype.map.call(cabecalho.cells, function (c) {
            var t = textoDe(c);
            return t === 'Ações' ? '' : t;
        });
        Array.prototype.forEach.call(tabela.tBodies, function (corpo) {
            Array.prototype.forEach.call(corpo.rows, function (linha) {
                Array.prototype.forEach.call(linha.cells, function (celula, i) {
                    if (!celula.hasAttribute('data-label')) {
                        celula.setAttribute('data-label', rotulos[i] || '');
                    }
                });
            });
        });
        tabela.classList.add('tr-cartoes');
    }

    function iniciar() {
        var estilo = document.createElement('style');
        estilo.textContent = CSS;
        document.head.appendChild(estilo);
        var marcadas = document.querySelectorAll('table[data-responsiva]');
        var tabelas = marcadas.length ? marcadas : document.querySelectorAll('table');
        Array.prototype.forEach.call(tabelas, preparar);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', iniciar);
    } else {
        iniciar();
    }
})();
