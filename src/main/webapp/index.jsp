
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:if test="${empty dashboardCarregado}">
    <c:redirect url="/dashboard"/>
</c:if>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard | Renovaí</title>
    <link rel="stylesheet" href="${ctx}/css/renovai.css">

    <style>
        .eyebrow { margin: 0; color: var(--texto-suave); font-size: .95rem; text-transform: uppercase; }
        .topo h1 { margin-top: 2px; }

        /* ---------- Indicadores ---------- */
        .stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(210px, 1fr));
            gap: 20px;
            margin-bottom: 28px;
        }
        .stat {
            background: #fff;
            border-radius: 24px;
            padding: 20px 24px;
            box-shadow: 0 4px 14px rgba(0, 0, 0, .06);
            display: flex;
            flex-direction: column;
            gap: 2px;
        }
        .stat-icone {
            width: 44px;
            height: 44px;
            border-radius: 50%;
            display: grid;
            place-items: center;
            margin-bottom: 8px;
        }
        .stat-icone svg { width: 22px; height: 22px; }
        .stat-valor { font-size: 3rem; font-weight: 700; line-height: 1; }
        .stat-rotulo { color: var(--vinho); font-size: 1rem; }
        .stat.verde .stat-icone { background: #e3edd9; color: #3f6a3a; }
        .stat.verde .stat-valor { color: #3f6a3a; }
        .stat.vinho .stat-icone { background: #f6e6ec; color: var(--vinho); }
        .stat.vinho .stat-valor { color: var(--vinho); }

        /* ---------- Painéis ---------- */
        .painel-dash {
            background: #fff;
            border-radius: 32px;
            padding: 28px 32px;
            box-shadow: 0 4px 14px rgba(0, 0, 0, .06);
        }
        .painel-dash h2 { margin: 0; color: var(--vinho); font-size: 1.5rem; font-weight: 600; }
        .painel-dash .sub { margin: 2px 0 0; color: #2f5a35; font-size: .95rem; }
        .vazio { color: var(--texto-suave); margin: 22px 0 0; font-size: .95rem; }

        .graficos {
            display: grid;
            grid-template-columns: minmax(0, 1fr) minmax(0, 1.25fr);
            gap: 24px;
            margin-bottom: 28px;
        }

        /* Rosca */
        .donut-area { display: flex; align-items: center; gap: 28px; flex-wrap: wrap; margin-top: 22px; }
        .donut { width: 210px; flex-shrink: 0; transform: rotate(0deg); }
        .donut-total { font-size: 5px; font-weight: 600; fill: var(--texto); }
        .donut-un { font-size: 2.6px; fill: var(--texto-suave); }
        .legenda { list-style: none; margin: 0; padding: 0; display: flex; flex-direction: column; gap: 10px; }
        .legenda li { display: flex; align-items: center; gap: 10px; font-size: 1rem; flex-wrap: wrap; }
        .legenda i { width: 18px; height: 18px; border-radius: 50%; flex-shrink: 0; }
        .legenda small { color: var(--texto-suave); font-size: .8rem; }

        /* Linha */
        .linha { width: 100%; height: auto; margin-top: 12px; }
        .grade-linha { stroke: #d9d7d0; stroke-dasharray: 4 4; }
        .eixo { fill: var(--texto-suave); font-size: 11px; }
        .linha-serie { fill: none; stroke: var(--vinho); stroke-width: 2.5; }
        .ponto { fill: var(--vinho); }

        /* Atividades */
        .atividades { list-style: none; margin: 20px 0 0; padding: 0; display: flex; flex-direction: column; gap: 16px; }
        .atividades li { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
        .ativ-icone {
            width: 44px; height: 44px; border-radius: 50%;
            background: #d8e8cf; color: #3f6a3a;
            display: grid; place-items: center; flex-shrink: 0;
        }
        .ativ-hora { font-family: ui-monospace, "SFMono-Regular", Menlo, monospace; font-size: .85rem; color: #555; min-width: 58px; }

        /* Ações rápidas */
        .secao { margin-bottom: 28px; }
        .acoes-rapidas { display: flex; flex-wrap: wrap; gap: 16px; margin-top: 20px; }
        .btn-acao {
            display: inline-flex; align-items: center; gap: 10px;
            background: var(--verde-botao); color: #fff;
            text-decoration: none; font-weight: 500;
            padding: 14px 24px; border-radius: 14px;
            box-shadow: 0 3px 8px rgba(0, 0, 0, .15);
        }
        .btn-acao:hover { background: #3f7648; }
        .btn-acao:focus-visible { outline: 3px solid #f0c24b; outline-offset: 2px; }

        @media (max-width: 1000px) {
            .graficos { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>

<!-- Ícones reutilizados na página -->
<svg width="0" height="0" style="position:absolute" aria-hidden="true" focusable="false">
    <symbol id="i-users" viewBox="0 0 24 24"><path d="M16 11c1.66 0 2.99-1.34 2.99-3S17.66 5 16 5c-1.66 0-3 1.34-3 3s1.34 3 3 3zm-8 0c1.66 0 2.99-1.34 2.99-3S9.66 5 8 5C6.34 5 5 6.34 5 8s1.34 3 3 3zm0 2c-2.33 0-7 1.17-7 3.5V19h14v-2.5c0-2.33-4.67-3.5-7-3.5zm8 0c-.29 0-.62.02-.97.05 1.16.84 1.97 1.97 1.97 3.45V19h6v-2.5c0-2.33-4.67-3.5-7-3.5z"/></symbol>
    <symbol id="i-box" viewBox="0 0 24 24"><path d="M12 2 3 7v10l9 5 9-5V7l-9-5zm0 2.2L18.5 7.8 12 11.4 5.5 7.8 12 4.2zM5 9.5l6 3.3v6.9l-6-3.3V9.5zm14 0v6.9l-6 3.3v-6.9l6-3.3z"/></symbol>
    <symbol id="i-building" viewBox="0 0 24 24"><path d="M12 7V3H2v18h20V7H12zM6 19H4v-2h2v2zm0-4H4v-2h2v2zm0-4H4V9h2v2zm0-4H4V5h2v2zm4 12H8v-2h2v2zm0-4H8v-2h2v2zm0-4H8V9h2v2zm0-4H8V5h2v2zm10 12h-8v-2h2v-2h-2v-2h2v-2h-2V9h8v10zm-2-8h-2v2h2v-2zm0 4h-2v2h2v-2z"/></symbol>
    <symbol id="i-swap" viewBox="0 0 24 24"><path d="M6.99 11L3 15l3.99 4v-3H14v-2H6.99v-3zM21 9l-3.99-4v3H10v2h7.01v3L21 9z"/></symbol>
    <symbol id="i-truck" viewBox="0 0 24 24"><path d="M20 8h-3V4H3c-1.1 0-2 .9-2 2v11h2c0 1.66 1.34 3 3 3s3-1.34 3-3h6c0 1.66 1.34 3 3 3s3-1.34 3-3h2v-5l-3-4zM6 18.5c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm13.5-9l1.96 2.5H17V9.5h2.5zm-1.5 9c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5z"/></symbol>
    <symbol id="i-person-add" viewBox="0 0 24 24"><path d="M15 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm-9-2V7H4v3H1v2h3v3h2v-3h3v-2H6zm9 4c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/></symbol>
</svg>

<div class="layout">

    <aside class="sidebar">
        <div class="marca">
            <strong>Renovaí</strong>
            <span>Área Administrativa</span>
        </div>
        <nav>
            <a href="${ctx}/dashboard" class="ativo" aria-current="page">Dashboard</a>
            <a href="${ctx}/cooperativa">Cooperativas</a>
            <a href="${ctx}/cooperado">Cooperados</a>
            <a href="${ctx}/coleta">Coletas</a>
            <a href="${ctx}/material">Materiais</a>
            <a href="${ctx}/negociacao">Negociações</a>
        </nav>
        <div class="usuario">
            <c:out value="${empty nomeUsuario ? 'Nome usuário' : nomeUsuario}"/>
            <small>Administrador</small>
        </div>
    </aside>

    <main>
        <header class="topo">
            <div>
                <p class="eyebrow">Área administrativa</p>
                <h1>
                    <c:out value="${saudacao}"/><c:if test="${not empty nomeUsuario}">, <c:out value="${nomeUsuario}"/></c:if>
                </h1>
                <p>Aqui está um resumo dos registros e atividades do Renovaí.</p>
            </div>
        </header>

        <!-- ===== INDICADORES ===== -->
        <section class="stats" aria-label="Indicadores">
            <article class="stat verde">
                <span class="stat-icone"><svg fill="currentColor" aria-hidden="true"><use href="#i-users"/></svg></span>
                <strong class="stat-valor"><c:out value="${totalCooperados}" default="—"/></strong>
                <span class="stat-rotulo">Cooperados ativos</span>
            </article>
            <article class="stat vinho">
                <span class="stat-icone"><svg fill="currentColor" aria-hidden="true"><use href="#i-box"/></svg></span>
                <strong class="stat-valor"><c:out value="${totalMateriais}" default="—"/></strong>
                <span class="stat-rotulo">Materiais cadastrados</span>
            </article>
            <article class="stat verde">
                <span class="stat-icone"><svg fill="currentColor" aria-hidden="true"><use href="#i-building"/></svg></span>
                <strong class="stat-valor"><c:out value="${totalCooperativas}" default="—"/></strong>
                <span class="stat-rotulo">Cooperativas ativas</span>
            </article>
            <article class="stat vinho">
                <span class="stat-icone"><svg fill="currentColor" aria-hidden="true"><use href="#i-swap"/></svg></span>
                <strong class="stat-valor"><c:out value="${totalNegociacoes}" default="—"/></strong>
                <span class="stat-rotulo">Negociações em andamento</span>
            </article>
        </section>

        <!-- ===== GRÁFICOS ===== -->
        <div class="graficos">

            <section class="painel-dash" aria-labelledby="tDist">
                <h2 id="tDist">Distribuição dos materiais</h2>
                <p class="sub">Peso coletado por categoria, sem contar coletas canceladas.</p>

                <c:choose>
                    <c:when test="${empty distribuicao}">
                        <p class="vazio">Ainda não há materiais registrados em coletas.</p>
                    </c:when>
                    <c:otherwise>
                        <div class="donut-area">
                            <svg id="donut" class="donut" viewBox="0 0 42 42" role="img"
                                 aria-label="Gráfico de rosca com a distribuição dos materiais por categoria"></svg>
                            <ul class="legenda" id="legenda"></ul>
                        </div>
                        <ul id="dadosDistribuicao" hidden>
                            <c:forEach items="${distribuicao}" var="item">
                                <li data-categoria="<c:out value='${item.key}'/>" data-valor="${item.value.toPlainString()}"></li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="painel-dash" aria-labelledby="tNeg">
                <h2 id="tNeg">Negociações realizadas</h2>
                <p class="sub">Negociações concluídas nos últimos 6 meses.</p>

                <c:choose>
                    <c:when test="${empty serieNegociacoes}">
                        <p class="vazio">Não foi possível carregar as negociações.</p>
                    </c:when>
                    <c:otherwise>
                        <svg id="linha" class="linha" viewBox="0 0 560 280" role="img"
                             aria-label="Gráfico de linha com as negociações concluídas por mês"></svg>
                        <ul id="dadosNegociacoes" hidden>
                            <c:forEach items="${serieNegociacoes}" var="item">
                                <li data-mes="<c:out value='${item.key}'/>" data-valor="${item.value}"></li>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>

        <!-- ===== ATIVIDADES RECENTES ===== -->
        <section class="painel-dash secao" aria-labelledby="tAtiv">
            <h2 id="tAtiv">Atividades recentes</h2>

            <c:choose>
                <c:when test="${empty atividades}">
                    <p class="vazio">Nenhuma atividade recente.</p>
                </c:when>
                <c:otherwise>
                    <ul class="atividades">
                        <c:forEach items="${atividades}" var="a">
                            <li>
                                <span class="ativ-icone"><svg width="22" height="22" fill="currentColor" aria-hidden="true"><use href="#i-truck"/></svg></span>
                                <span class="ativ-hora"><c:out value="${a.horario}"/></span>
                                <span><strong><c:out value="${a.destaque}"/></strong> <c:out value="${a.texto}"/></span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:otherwise>
            </c:choose>
        </section>

        <!-- ===== AÇÕES RÁPIDAS ===== -->
        <section class="painel-dash secao" aria-labelledby="tAcoes">
            <h2 id="tAcoes">Ações rápidas</h2>
            <div class="acoes-rapidas">
                <a class="btn-acao" href="${ctx}/cooperativa?pagina=nova"><svg width="22" height="22" fill="currentColor" aria-hidden="true"><use href="#i-building"/></svg>Cadastrar cooperativa</a>
                <a class="btn-acao" href="${ctx}/coleta?acao=nova"><svg width="22" height="22" fill="currentColor" aria-hidden="true"><use href="#i-truck"/></svg>Nova coleta</a>
                <a class="btn-acao" href="${ctx}/negociacao?acao=novo"><svg width="22" height="22" fill="currentColor" aria-hidden="true"><use href="#i-swap"/></svg>Nova negociação</a>
                <a class="btn-acao" href="${ctx}/material?novo=1"><svg width="22" height="22" fill="currentColor" aria-hidden="true"><use href="#i-box"/></svg>Novo material</a>
                <a class="btn-acao" href="${ctx}/cooperado?acao=novo"><svg width="22" height="22" fill="currentColor" aria-hidden="true"><use href="#i-person-add"/></svg>Cadastrar cooperado</a>
            </div>
        </section>
    </main>
</div>

<script>
    const NS = "http://www.w3.org/2000/svg";
    const el = (nome, attrs = {}) => {
        const e = document.createElementNS(NS, nome);
        Object.entries(attrs).forEach(([k, v]) => e.setAttribute(k, v));
        return e;
    };
    const fmt = (n) => n.toLocaleString("pt-BR", { maximumFractionDigits: 2 });

    // ---------- Rosca: distribuição dos materiais ----------
    (function () {
        const lista = document.getElementById("dadosDistribuicao");
        if (!lista) return;

        const CORES = {
            "PAPEIS": "#A33962", "PLASTICOS": "#DFA1B8", "METAIS": "#85C28C",
            "VIDROS": "#519058", "ELETRONICOS": "#882B4E", "OUTROS": "#D4E699"
        };

        const dados = [...lista.children]
            .map((li) => ({ categoria: li.dataset.categoria, valor: parseFloat(li.dataset.valor) || 0 }))
            .filter((d) => d.valor > 0);
        const total = dados.reduce((soma, d) => soma + d.valor, 0);

        const svg = document.getElementById("donut");
        const legenda = document.getElementById("legenda");

        if (total <= 0) {
            legenda.textContent = "Sem pesos registrados.";
            return;
        }

        let acumulado = 0;
        dados.forEach((d) => {
            const pct = (d.valor / total) * 100;
            const cor = CORES[d.categoria] || "#b8b8b8";

            // 15,9155 = raio em que a circunferência vale 100 (o traço vira porcentagem)
            svg.appendChild(el("circle", {
                cx: 21, cy: 21, r: 15.9155, fill: "none", stroke: cor, "stroke-width": 6.5,
                "stroke-dasharray": pct + " " + (100 - pct),
                "stroke-dashoffset": 25 - acumulado
            }));
            acumulado += pct;

            const li = document.createElement("li");
            const bolinha = document.createElement("i");
            bolinha.style.background = cor;
            const nome = document.createElement("span");
            nome.textContent = d.categoria;
            const info = document.createElement("small");
            info.textContent = fmt(d.valor) + " kg (" + Math.round(pct) + "%)";
            li.append(bolinha, nome, info);
            legenda.appendChild(li);
        });

        const centro = el("text", { x: 21, y: 22, "text-anchor": "middle", class: "donut-total" });
        centro.textContent = fmt(total);
        const unidade = el("text", { x: 21, y: 26, "text-anchor": "middle", class: "donut-un" });
        unidade.textContent = "kg no total";
        svg.append(centro, unidade);
    })();

    // ---------- Linha: negociações por mês ----------
    (function () {
        const lista = document.getElementById("dadosNegociacoes");
        if (!lista) return;

        const dados = [...lista.children]
            .map((li) => ({ mes: li.dataset.mes, valor: parseInt(li.dataset.valor, 10) || 0 }));
        const svg = document.getElementById("linha");

        const W = 560, H = 280, m = { t: 16, r: 16, b: 34, l: 44 };
        const w = W - m.l - m.r, h = H - m.t - m.b;

        const max = Math.max(...dados.map((d) => d.valor), 0);
        const topo = Math.max(5, Math.ceil(max / 5) * 5);
        const y = (v) => m.t + h - (v / topo) * h;
        const x = (i) => m.l + (i + 0.5) * (w / dados.length);
        const limitar = (v) => Math.min(Math.max(v, m.t), m.t + h);

        // Grade e eixo Y
        for (let i = 0; i <= 5; i++) {
            const v = (topo / 5) * i;
            svg.appendChild(el("line", { x1: m.l, x2: W - m.r, y1: y(v), y2: y(v), class: "grade-linha" }));
            const t = el("text", { x: m.l - 8, y: y(v) + 4, "text-anchor": "end", class: "eixo" });
            t.textContent = v;
            svg.appendChild(t);
        }

        // Eixo X
        dados.forEach((d, i) => {
            const t = el("text", { x: x(i), y: H - 10, "text-anchor": "middle", class: "eixo" });
            t.textContent = d.mes;
            svg.appendChild(t);
        });

        // Curva suave passando pelos pontos
        const pts = dados.map((d, i) => ({ x: x(i), y: y(d.valor) }));
        let caminho = "M" + pts[0].x + "," + pts[0].y;
        for (let i = 0; i < pts.length - 1; i++) {
            const p0 = pts[i - 1] || pts[i], p1 = pts[i], p2 = pts[i + 1], p3 = pts[i + 2] || p2;
            const c1x = p1.x + (p2.x - p0.x) / 6, c1y = limitar(p1.y + (p2.y - p0.y) / 6);
            const c2x = p2.x - (p3.x - p1.x) / 6, c2y = limitar(p2.y - (p3.y - p1.y) / 6);
            caminho += " C" + c1x + "," + c1y + " " + c2x + "," + c2y + " " + p2.x + "," + p2.y;
        }
        svg.appendChild(el("path", { d: caminho, class: "linha-serie" }));

        // Pontos (com texto para leitor de tela e dica ao passar o mouse)
        pts.forEach((p, i) => {
            const c = el("circle", { cx: p.x, cy: p.y, r: 5, class: "ponto" });
            const dica = el("title");
            dica.textContent = dados[i].mes + ": " + dados[i].valor + (dados[i].valor === 1 ? " negociação" : " negociações");
            c.appendChild(dica);
            svg.appendChild(c);
        });
    })();
</script>
<%@ include file="/operacao-ui.jspf" %>
</body>
</html>
