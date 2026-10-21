// Raiz do site (funciona em index.html e em pages/*.html, com qualquer contexto do WAR)
const BASE = document.querySelector('script[src$="main.js"]').src.replace(/scripts\/main\.js.*$/, '');

document.addEventListener('DOMContentLoaded', () => {
  // Botões que levam para outras páginas
  const destinos = {
    '.cta1, .plano-card-btn, .btn-sou-cooperativa, .btn-sou-recicladora': 'cadastro',
    '.conhecaRenovai': 'pages/renovai.html',
    '.sobreNos': 'pages/quem_somos.html'
  };
  Object.entries(destinos).forEach(([seletor, destino]) => {
    document.querySelectorAll(seletor).forEach((btn) => {
      btn.addEventListener('click', () => { window.location.href = BASE + destino; });
    });
  });

  // Seção "Por que escolher o Renovaí?"
  const features = [
    {
      icon: BASE + 'images/Shield lock (1).svg',
      title: 'Transparência',
      sub: 'Negociações mais claras e seguras',
      list: [
        'Preços justos e atualizados em tempo real',
        'Histórico de negociações sempre disponível',
        'Avaliações e reputação visíveis para os dois lados'
      ]
    },
    {
      icon: BASE + 'images/Connected.People.svg',
      title: 'Conexão',
      sub: 'Cooperativas e recicladoras, sem intermediários',
      list: [
        'Contato direto entre as duas pontas da cadeia',
        'Negociações combinadas dentro da plataforma',
        'Menos etapas, mais agilidade no fechamento'
      ]
    },
    {
      icon: BASE + 'images/People.svg',
      title: 'Impacto Social',
      sub: 'Uma rede que cresce junto',
      list: [
        'Cooperativas e recicladoras da sua região',
        'Trocas de experiência entre parceiros',
        'Impacto social acompanhado de perto'
      ]
    },
    {
      icon: BASE + 'images/Lightning Bolt.svg',
      title: 'Eficiência',
      sub: 'Processos mais rápidos, do início ao fim',
      list: [
        'Cadastro e negociação em poucos passos',
        'Menos burocracia na hora de fechar negócio',
        'Acompanhamento do processo em tempo real'
      ]
    }
  ];

  const iconeTrilha = document.getElementById('iconeTrilha');
  const cardIcone = document.getElementById('cardIcone');
  const cardTitulo = document.getElementById('cardTitulo');
  const cardSubtitulo = document.getElementById('cardSubtitulo');
  const cardLista = document.getElementById('cardLista');

  if (iconeTrilha) {
    iconeTrilha.addEventListener('click', (event) => {
      const segmento = event.target.closest('.icone-segmento');
      if (!segmento) return;

      const featureIndex = segmento.dataset.feature;

      iconeTrilha.querySelectorAll('.icone-segmento').forEach((el) => el.classList.remove('active'));
      iconeTrilha.querySelectorAll('.icone-conteudo').forEach((el) => el.classList.remove('active'));

      segmento.classList.add('active');
      iconeTrilha.querySelector(`.icone-conteudo[data-feature="${featureIndex}"]`)?.classList.add('active');

      const feature = features[parseInt(featureIndex, 10)];
      cardIcone.innerHTML = `<img src="${feature.icon}" alt="">`;
      cardTitulo.textContent = feature.title;
      cardSubtitulo.textContent = feature.sub;
      cardLista.innerHTML = feature.list.map((item) => `<li><span>✓</span> ${item}</li>`).join('');
    });
  }

// Conteúdo do bloco de destaque por tipo
const planosDestaqueConteudo = {
  cooperativa: {
    titulo: 'Fortaleça sua cooperativa <span class="planos-destaque-titulo1">sem custos</span>',
    descricao: 'O Renovaí é gratuito para cooperativas porque acreditamos que a tecnologia deve apoiar quem move a reciclagem. Organize sua operação, conecte-se com empresas e venda com mais facilidade.',
    badge: 'Cooperativas',
    preco: 'GRATUITO',
    lista: ['Gestão de materiais', 'Gestão de materiais', 'Gestão de materiais', 'Gestão de materiais', 'Gestão de materiais'],
    botao: 'Cadastre sua cooperativa'
  },
  recicladora: {
    // PLACEHOLDER — troque pelo conteúdo real da Recicladora quando tiver
    titulo: 'Compre direto <span class="planos-destaque-titulo1">das cooperativas</span>',
    descricao: 'Texto placeholder para a versão Recicladora. Troque por este conteúdo real quando estiver definido.',
    badge: 'Recicladoras',
    preco: 'GRATUITO',
    lista: ['Benefício x', 'Benefício x', 'Benefício x', 'Benefício x', 'Benefício x'],
    botao: 'Cadastre sua recicladora'
  }
};

function atualizarPlanos(tipo) {

  // Atualiza o card de destaque
  const dados = planosDestaqueConteudo[tipo];

  if (dados) {
    document.getElementById('destaque-titulo').innerHTML = dados.titulo;
    document.getElementById('destaque-descricao').textContent = dados.descricao;

    const card = document.getElementById('destaque-card');

    card.querySelector('.plano-card-badge').textContent = dados.badge;
    card.querySelector('.plano-card-preco-grande').textContent = dados.preco;

    card.querySelector('.plano-card-lista').innerHTML =
      dados.lista.map((item) => `<li>✓ ${item}</li>`).join('');

    card.querySelector('.plano-card-btn').textContent = dados.botao;
  }

  // Mostra somente os planos do tipo selecionado
  const cooperativa = document.getElementById('pagos-cooperativa');
  const recicladora = document.getElementById('pagos-recicladora');

  cooperativa?.classList.remove('ativo');
  recicladora?.classList.remove('ativo');

  if (tipo === 'cooperativa') {
    cooperativa?.classList.add('ativo');
  }

  if (tipo === 'recicladora') {
    recicladora?.classList.add('ativo');
  }
}

// Seletor Cooperativa / Recicladora da seção Planos
document.querySelectorAll('.planos [data-plano-tipo]').forEach((btn) => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.planos [data-plano-tipo]').forEach((b) => b.classList.remove('active'));
    btn.classList.add('active');

    document.getElementById('painel-cooperativa')?.classList.toggle('ativo', btn.dataset.planoTipo === 'cooperativa');
    document.getElementById('painel-recicladora')?.classList.toggle('ativo', btn.dataset.planoTipo === 'recicladora');
  });
});

// ===== Carrossel infinito (com suporte a blocos escondidos) =====
const carrosselState = {}; // guarda { larguraConjunto, inicializado } por id

function inicializarCarrosselInfinito(containerId) {
  if (carrosselState[containerId]?.inicializado) return;

  const container = document.getElementById(containerId);
  if (!container) return;

  const cardsOriginais = Array.from(container.children);
  if (cardsOriginais.length === 0) return;

  const gap = 24; // precisa bater com .carrossel-cards no CSS

  const clonesAntes = cardsOriginais.map((card) => card.cloneNode(true));
  const clonesDepois = cardsOriginais.map((card) => card.cloneNode(true));

  clonesAntes.reverse().forEach((clone) => container.insertBefore(clone, container.firstChild));
  clonesDepois.forEach((clone) => container.appendChild(clone));

  const larguraConjunto = cardsOriginais.reduce((acc, card) => acc + card.offsetWidth + gap, 0);

  container.scrollLeft = larguraConjunto;

  let ajustando = false;
  container.addEventListener('scroll', () => {
    if (ajustando) return;
    const estado = carrosselState[containerId];
    if (container.scrollLeft <= 0) {
      ajustando = true;
      container.style.scrollBehavior = 'auto';
      container.scrollLeft += estado.larguraConjunto;
      container.style.scrollBehavior = 'smooth';
      ajustando = false;
    } else if (container.scrollLeft >= estado.larguraConjunto * 2) {
      ajustando = true;
      container.style.scrollBehavior = 'auto';
      container.scrollLeft -= estado.larguraConjunto;
      container.style.scrollBehavior = 'smooth';
      ajustando = false;
    }
  });

  carrosselState[containerId] = { larguraConjunto, inicializado: true };
}

// Setas do carrossel
document.querySelectorAll('.carrossel-seta').forEach((seta) => {
  seta.addEventListener('click', () => {
    const trilho = document.getElementById(seta.dataset.carrossel);
    if (!trilho) return;
    const larguraCard = trilho.querySelector('.funcionalidade-card')?.offsetWidth || 280;
    const distancia = larguraCard + 24;
    trilho.scrollBy({ left: seta.classList.contains('seta-direita') ? distancia : -distancia, behavior: 'smooth' });
  });
});

// ===== Tabs Funcionalidades (mostrar/esconder + inicializa carrossel ao exibir) =====
document.querySelectorAll('.tab-cooperativa, .tab-recicladora').forEach((btn) => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.tab-cooperativa, .tab-recicladora').forEach((b) => b.classList.remove('active'));
    btn.classList.add('active');

    document.querySelectorAll('.bloco-funcionalidade').forEach((bloco) => bloco.classList.remove('ativo'));
    const alvo = document.getElementById(btn.dataset.target);
    if (alvo) {
      alvo.classList.add('ativo');
      const carrosselInterno = alvo.querySelector('.carrossel-cards');
      if (carrosselInterno) inicializarCarrosselInfinito(carrosselInterno.id);
    }
  });
});

// Mostra o bloco de Funcionalidades ativo por padrão (Cooperativa) e já inicializa o carrossel dele
document.getElementById('bloco-cooperativa')?.classList.add('ativo');
inicializarCarrosselInfinito('carrossel-coop');

  // Acordeão do FAQ
  document.querySelectorAll('.faq-item').forEach((item) => {
    const pergunta = item.querySelector('.faq-pergunta');
    const resposta = item.querySelector('.faq-resposta');

    pergunta.addEventListener('click', () => {
      const estaAberto = item.classList.contains('active');

      document.querySelectorAll('.faq-item').forEach((outro) => {
        outro.classList.remove('active');
        outro.querySelector('.faq-resposta').style.maxHeight = null;
      });

      if (!estaAberto) {
        item.classList.add('active');
        resposta.style.maxHeight = resposta.scrollHeight + 'px';
      }
    });
  });
});
