/* Botão "olho" para mostrar/ocultar senha.
   Olho aberto = senha visível; olho com traço = senha oculta. */
(function () {
    var ABERTO = '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 5C7 5 2.7 8.1 1 12.5 2.7 16.9 7 20 12 20s9.3-3.1 11-7.5C21.3 8.1 17 5 12 5zm0 12.5a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm0-8a3 3 0 1 0 0 6 3 3 0 0 0 0-6z"/></svg>';
    var FECHADO = '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 5C7 5 2.7 8.1 1 12.5 2.7 16.9 7 20 12 20s9.3-3.1 11-7.5C21.3 8.1 17 5 12 5zm0 12.5a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm0-8a3 3 0 1 0 0 6 3 3 0 0 0 0-6z"/><rect x="11" y="2" width="2" height="20" rx="1" transform="rotate(0 12 12)"/></svg>';

    var css = document.createElement('style');
    css.textContent =
        '.olho-senha{background:none;border:0;padding:4px;margin:0;cursor:pointer;display:flex;align-items:center;flex-shrink:0}' +
        '.olho-senha svg{width:24px;height:24px;fill:#5f8a6b}' +
        '.olho-senha:focus-visible{outline:2px solid #3d7a4e;border-radius:6px}';
    document.head.appendChild(css);

    function atualizar(botao, campo) {
        var visivel = campo.type === 'text';
        botao.innerHTML = visivel ? ABERTO : FECHADO;
        botao.setAttribute('aria-label', visivel ? 'Ocultar senha' : 'Mostrar senha');
        botao.setAttribute('aria-pressed', visivel ? 'true' : 'false');
    }

    document.querySelectorAll('input[type="password"]').forEach(function (campo) {
        var botao = document.createElement('button');
        botao.type = 'button';
        botao.className = 'olho-senha';
        botao.addEventListener('click', function (e) {
            e.preventDefault();
            campo.type = campo.type === 'password' ? 'text' : 'password';
            atualizar(botao, campo);
            campo.focus();
        });
        atualizar(botao, campo);
        campo.insertAdjacentElement('afterend', botao);
    });
})();
