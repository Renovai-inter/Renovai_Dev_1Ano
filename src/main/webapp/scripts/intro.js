// Abertura da landing page: o anel do "O" se desenha, REN e VAÍ chegam, a câmera mergulha no O
// e a página aparece por dentro dele. Tudo é SVG com medidas calculadas (nada é medido no DOM).
(function () {
  const intro = document.getElementById('intro');
  if (!intro) return;

  const html = document.documentElement;
  if (html.classList.contains('sem-intro')) {
    intro.remove();
    return;
  }

  const svg = document.getElementById('introSvg');
  const palco = document.getElementById('introPalco');
  const anel = document.getElementById('introAnel');
  const esq = document.getElementById('introEsq');
  const dir = document.getElementById('introDir');
  const furo = document.getElementById('introFuro');
  const eventosParaPular = ['click', 'keydown', 'touchstart', 'wheel'];
  let terminou = false;
  let quadro;
  let temporizador;

  function terminar() {
    if (terminou) return;
    terminou = true;
    cancelAnimationFrame(quadro);
    clearTimeout(temporizador);
    eventosParaPular.forEach((ev) => window.removeEventListener(ev, terminar));
    html.classList.remove('intro-rodando');
    html.style.overflow = '';
    intro.remove();
    try { sessionStorage.setItem('introVista', '1'); } catch (e) { /* sem storage: abre de novo na próxima visita */ }
  }

  // ----- geometria (tudo em px da tela; o "O" fica no centro) -----
  const W = intro.clientWidth;
  const H = intro.clientHeight;
  const cx = W / 2;
  const cy = H / 2;
  const f = Math.min(Math.max(44, W * 0.11), 160);      // tamanho da fonte (= clamp(44px, 11vw, 160px))
  const raioExterno = 0.37 * f;                          // anel: 0,74em de diâmetro
  const espessura = 0.15 * f;
  const raioFuro = raioExterno - espessura;              // miolo do anel
  const raioCentral = raioExterno - espessura / 2;       // linha central do traço
  const perimetro = 2 * Math.PI * raioCentral;

  svg.setAttribute('viewBox', `0 0 ${W} ${H}`);
  ['introFundo', 'introMascaraFundo'].forEach((id) => {
    const r = document.getElementById(id);
    // fundo bem maior que a tela: cobre tudo mesmo se a área medida diferir um pouco da visível
    r.setAttribute('x', -W);
    r.setAttribute('y', -H);
    r.setAttribute('width', W * 3);
    r.setAttribute('height', H * 3);
  });

  anel.setAttribute('cx', cx);
  anel.setAttribute('cy', cy);
  anel.setAttribute('r', raioCentral);
  anel.setAttribute('stroke-width', espessura);
  anel.setAttribute('stroke-dasharray', perimetro);
  anel.style.setProperty('--perimetro', perimetro);

  furo.setAttribute('cx', cx);
  furo.setAttribute('cy', cy);
  furo.setAttribute('r', 0);

  // o anel assenta na linha de base do texto; REN termina e VAÍ começa junto ao anel
  const baseY = cy + raioExterno;
  const folga = 0.06 * f;
  [esq, dir].forEach((t) => {
    t.setAttribute('font-size', f);
    t.setAttribute('y', baseY);
  });
  esq.setAttribute('x', cx - raioExterno - folga);
  dir.setAttribute('x', cx + raioExterno + folga);

  const suavizar = (p) => (p < 0.5 ? 4 * p * p * p : 1 - Math.pow(-2 * p + 2, 3) / 2);

  function mergulhar() {
    if (terminou) return;

    // a página começa a animar enquanto o buraco abre
    html.classList.remove('intro-rodando');
    html.style.overflow = 'hidden';

    // escala necessária para o furo cobrir o canto mais distante da tela
    const maisLonge = Math.max(Math.hypot(cx, cy), Math.hypot(W - cx, cy), Math.hypot(cx, H - cy), Math.hypot(W - cx, H - cy));
    const escalaFinal = (maisLonge / raioFuro) * 1.1;

    const duracao = 1500;
    const inicio = performance.now();

    function passo(agora) {
      const p = Math.min((agora - inicio) / duracao, 1);
      // crescimento exponencial dá a sensação de velocidade constante no mergulho
      const escala = Math.exp(Math.log(escalaFinal) * suavizar(p));
      palco.setAttribute('transform', `translate(${cx} ${cy}) scale(${escala}) translate(${-cx} ${-cy})`);
      furo.setAttribute('r', raioFuro * escala);

      if (p < 1) quadro = requestAnimationFrame(passo);
      else terminar();
    }
    quadro = requestAnimationFrame(passo);
  }

  // qualquer toque/tecla/rolagem pula a abertura
  eventosParaPular.forEach((ev) => window.addEventListener(ev, terminar, { passive: true }));

  // deixa a palavra aparecer completa por um instante antes do mergulho
  temporizador = setTimeout(mergulhar, 2000);
})();
