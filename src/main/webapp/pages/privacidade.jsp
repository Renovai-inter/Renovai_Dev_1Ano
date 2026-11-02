<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Política de Privacidade — Renovaí | Renovaí</title>
    <link rel="shortcut icon" href="${ctx}/images/Nimo.svg" type="image/svg+xml">
    <style>
        body{margin:0;background:#f4f7f5;color:#1f2a24;font:16px/1.65 system-ui,-apple-system,"Segoe UI",Roboto,sans-serif}
        main{max-width:820px;margin:0 auto;padding:32px 16px 56px}
        .cartao{background:#fff;border-radius:12px;padding:28px 24px;box-shadow:0 1px 4px rgba(0,0,0,.08)}
        h1{margin:0 0 4px;font-size:1.7rem}
        h2{margin:28px 0 8px;font-size:1.15rem;color:#1b6b3a}
        .nota{background:#eef6f0;border-left:4px solid #1b6b3a;padding:10px 14px;border-radius:6px;font-size:.92rem}
        .tabela{overflow-x:auto}
        table{border-collapse:collapse;width:100%;font-size:.92rem}
        th,td{border:1px solid #d5ded8;padding:8px 10px;text-align:left;vertical-align:top}
        th{background:#eef6f0}
        a{color:#1b6b3a}
        nav{margin-bottom:16px;display:flex;gap:16px;flex-wrap:wrap;font-size:.95rem}
    </style>
</head>
<body>
<main>
    <nav>
        <a href="javascript:history.length>1?history.back():location.href='${ctx}/cadastro'">&larr; Voltar</a>
        <a href="${ctx}/pages/termos.jsp">Termos de Uso</a>
        <a href="${ctx}/login">Entrar</a>
    </nav>
    <div class="cartao">
        <h1>Política de Privacidade — Renovaí</h1>
<p><strong>Última atualização:</strong> 01/11/2026</p>
<p>O Renovaí é um projeto acadêmico do Instituto J&amp;F (Projeto Interdisciplinar) que organiza a coleta e a negociação de</p>
<p>materiais recicláveis entre cooperativas e empresas. Esta política explica quais dados pessoais o sistema trata, para quê,</p>
<p>por quanto tempo e quais são os seus direitos, em linha com a Lei Geral de Proteção de Dados (LGPD, Lei 13.709/2018).</p>
<p class="nota">Este documento foi redigido pelo grupo para fins didáticos e não substitui orientação jurídica.</p>
<h2>1. Quem é o responsável</h2>
<p>O grupo de desenvolvimento do Renovaí é o controlador dos dados tratados no sistema.</p>
<p>Contato para assuntos de privacidade: <strong>renovai.institutojef@gmail.com</strong>.</p>
<h2>2. Quais dados são tratados</h2>
<div class="tabela"><table><thead><tr><th>Grupo</th><th>Dados</th><th>Origem</th></tr></thead><tbody>
<tr><td>Contas de acesso</td><td>nome completo, nome de usuário, e-mail, tipo de usuário, senha (guardada somente como hash)</td><td>Cadastro de administrador ou criação pelo administrador</td></tr>
<tr><td>Cooperados</td><td>nome, e-mail, cargo, status, atividades, permissões, <strong>CPF (opcional)</strong></td><td>Cadastro feito pelo administrador</td></tr>
<tr><td>Cooperativas</td><td>razão social e nome público, CNPJ, e-mail e telefone/WhatsApp institucionais, endereço, CEP, cidade, estado, logotipo</td><td>Cadastro feito pelo administrador</td></tr>
<tr><td>Operação</td><td>coletas, rotas, materiais, pesos, negociações, propostas, valores e histórico de movimentações</td><td>Registros feitos no sistema</td></tr>
<tr><td>Técnicos</td><td>cookie de sessão, endereço IP e registros de acesso do servidor</td><td>Gerados automaticamente</td></tr>
</tbody></table></div>
<p>O Renovaí <strong>não</strong> trata dados sensíveis (art. 5º, II, da LGPD) e não é destinado a menores de 18 anos.</p>
<h2>3. Para que os dados são usados e a base legal</h2>
<ul>
<li><strong>Autenticar e identificar</strong> administradores e cooperados (execução do serviço e legítimo interesse).</li>
<li><strong>Registrar e acompanhar</strong> coletas, rotas e negociações entre cooperativas e empresas (execução do serviço).</li>
<li><strong>Recuperar senha</strong> por e-mail, com link temporário (execução do serviço).</li>
<li><strong>Segurança e correção de falhas</strong>, por meio de registros técnicos (legítimo interesse).</li>
<li><strong>CPF:</strong> coletado somente se informado, para identificar o cooperado. É opcional e pode ser removido a qualquer momento.</li>
</ul>
<p>Os dados não são usados para publicidade nem vendidos.</p>
<h2>4. Com quem os dados são compartilhados</h2>
<p>Somente com prestadores necessários ao funcionamento do sistema:</p>
<ul>
<li><strong>Hospedagem da aplicação e do banco de dados:</strong> Render, que opera a infraestrutura em nuvem.</li>
<li><strong>Envio de e-mails</strong> de recuperação de senha: provedor de e-mail configurado pelo grupo (SMTP ou API).</li>
</ul>
<p>Não há compartilhamento com terceiros para fins comerciais. Dados de operação (por exemplo, uma negociação) ficam</p>
<p>visíveis aos administradores do sistema.</p>
<h2>5. Por quanto tempo guardamos</h2>
<ul>
<li>Dados de contas e de operação ficam guardados enquanto o projeto estiver ativo ou enquanto forem necessários para o histórico de coletas e negociações.</li>
<li>Ao excluir um registro pelo sistema, ele deixa de aparecer; cópias de segurança do provedor podem mantê-lo por um tempo limitado.</li>
<li>O link de recuperação de senha vale por 30 minutos e só pode ser usado uma vez.</li>
<li>A sessão de login expira após 8 horas de inatividade.</li>
</ul>
<h2>6. Como protegemos os dados</h2>
<ul>
<li>Senhas são guardadas com hash, nunca em texto aberto.</li>
<li>Chaves, senhas de banco e de e-mail ficam em variáveis de ambiente, fora do código-fonte.</li>
<li>O acesso à área administrativa exige login de administrador.</li>
<li>Mesmo com esses cuidados, nenhum sistema é totalmente imune a falhas. Em caso de incidente que possa causar risco relevante, o grupo comunicará os afetados e a ANPD, como a lei exige.</li>
</ul>
<h2>7. Seus direitos (art. 18 da LGPD)</h2>
<p>Você pode pedir: confirmação de que tratamos seus dados; acesso; correção de dados incompletos ou errados; anonimização,</p>
<p>bloqueio ou eliminação de dados desnecessários; portabilidade; informação sobre compartilhamento; e revogação de consentimento.</p>
<p>Para exercer esses direitos, escreva para <strong>[preencher e-mail do grupo]</strong>. Responderemos em prazo razoável.</p>
<h2>8. Cookies</h2>
<p>O sistema usa apenas o <strong>cookie de sessão</strong>, necessário para manter o login. Não usamos cookies de publicidade ou de rastreamento.</p>
<h2>9. Mudanças nesta política</h2>
<p>Podemos atualizar este texto. A data de "última atualização" no topo mostra a versão em vigor.</p>
    </div>
</main>
</body>
</html>
