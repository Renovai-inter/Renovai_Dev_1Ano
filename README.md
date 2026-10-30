# Renovaí

O **Renovaí** é um projeto acadêmico voltado à organização da coleta e da negociação de materiais recicláveis.
Ele reúne páginas públicas e uma área administrativa para acompanhar cooperativas, cooperados, materiais, coletas e negociações.
A proposta é tornar mais claros os registros, os responsáveis e o andamento de cada operação.

Este repositório contém uma aplicação web Java desenvolvida pelo grupo no primeiro ano do projeto interdisciplinar.
O código está em evolução: a presença de uma tela ou rota não significa que seu fluxo já tenha sido validado de ponta a ponta.
As regras de dados foram confrontadas com o esquema planejado em `../script-banco.pdf`; o PDF não comprova a estrutura da instância PostgreSQL em uso.

## Funcionalidades

- **Acesso público:** página inicial, apresentação do projeto, cadastro e autenticação de usuários.
- **Conta:** solicitação de recuperação de senha por e-mail e definição de uma nova senha por token.
- **Dashboard:** visão administrativa com indicadores, atividades recentes e atalhos para os módulos.
- **Cooperativas:** listagem, busca, cadastro, consulta de detalhes, atualização e exclusão.
- **Cooperados:** cadastro, listagem, consulta, edição, permissões e ações de inativação ou exclusão.
- **Materiais:** listagem e operações de cadastro, edição e exclusão pela área administrativa.
- **Coletas:** cadastro, listagem, detalhes e ações para iniciar, finalizar, cancelar ou excluir; há fluxo de edição em desenvolvimento.
- **Negociações:** listagem, propostas, contrapropostas, movimentações, consulta do histórico e ações sobre o estado da negociação.

As operações administrativas dependem da integração entre tela, Servlet, Model, DAO e banco de dados.
Algumas combinações de campos, mensagens e destinos ainda exigem conferência funcional; veja **Estado atual**.

## Tecnologias

Java 17 no `pom.xml`, Maven, aplicação WAR, Jakarta Servlets/JSP/JSTL, JDBC e PostgreSQL.
O `Dockerfile` usa imagens com Java 21 e Tomcat 11 para construir e servir o WAR.

## Organização do código

```text
src/main/java/model/       Entidades, vínculos, enums e validações
src/main/java/dao/         Consultas e gravações JDBC
src/main/java/controller/  Servlets e filtros de autenticação
src/main/java/util/        Conexão, formatação, e-mail e token
src/main/webapp/           Páginas JSP, fragmentos, CSS, scripts e imagens
```

Os Servlets recebem as requisições, preparam as Models e chamam as DAOs; as JSPs apresentam os resultados.
`Main.java` é um teste isolado de conexão com o banco e não substitui o servidor web.
`AGENTS.md` e `PADRAO_CODIGO_BACKEND.md` registram as convenções adotadas na revisão do backend.

## Processo de refatoração

A refatoração foi conduzida em etapas, com diagnóstico e registro das decisões antes das mudanças de maior alcance.
O grupo escolheu trabalhar por camada e preservar rotas, parâmetros, mensagens e comportamento sempre que o contrato permitisse.

1. **Mapeamento:** leitura das Models, DAOs, Servlets, JSPs e da referência de esquema; divergências foram anotadas em relatórios.
2. **Models:** revisão de setters, construtores, limites de texto, números decimais, datas, enums e vínculos entre entidades.
3. **DAOs:** alinhamento de parâmetros às Models e de retornos de escrita ao número de linhas afetadas; revisão do SQL e de suas chamadas.
4. **Servlets:** adaptação das ações aos contratos das DAOs, mantendo os fluxos existentes e conferindo os dados enviados às JSPs.
5. **Integração:** conferência de imports, assinaturas, encaminhamentos, links e mensagens; correções pontuais foram feitas quando havia incompatibilidade demonstrada.

Houve rodadas **parciais**, limitadas a comentários e organização, e rodadas **completas** para itens aprovados pelo grupo.
As gravações em várias tabelas usam comandos separados; se uma etapa posterior falhar, dados anteriores podem permanecer gravados.
Esse comportamento foi mantido por decisão do grupo e não deve ser interpretado como uma operação atômica.
Relatórios na pasta irmã `../Relatórios/` documentam o percurso, especialmente `17-Consolidacao-Refatoracao.md`,
`38-Refatoracao-Contratos-DAO-SQL-Simples.md`, `49-Consolidacao-Chamadas-Assinaturas.md`,
`51-Memoria-Status-Refatoracao-Backend.md` e `56-Execucao-Parcial-Integracao-e-Incongruencias-V2.md`.

## Como usamos IA

O grupo utilizou o **Codex** como apoio à leitura do repositório, à análise das dependências entre camadas e à redação de checklists e relatórios.
Após a definição do escopo pelo grupo, a IA também auxiliou em alterações de código e em verificações localizadas.
As decisões sobre regras de negócio, prioridades, limites das mudanças e aceitação de riscos permaneceram com as pessoas do projeto.
Os relatórios distinguem propostas, mudanças executadas, resultados observados e pendências.
Resultados de compilação e inspeção estática são apresentados como evidência do respectivo escopo, não como prova de funcionamento no navegador ou no banco.
Este README também foi redigido com auxílio de IA, a partir do código e dos registros do projeto, para revisão pelo grupo.

## Configuração

Configure no processo que executa a aplicação `DB_URL`, `DB_USER` e `DB_PASSWORD` para a conexão JDBC.
Use uma URL PostgreSQL no formato `jdbc:postgresql://<host>:<porta>/<banco>`; `jdbc:postgres://` não é aceito pelo driver.
Para recuperação de senha, configure `SMTP_HOST`, `SMTP_PORT`, `SMTP_USER`, `SMTP_PASSWORD`, `SMTP_FROM`,
`RENOVAI_BASE_URL` e `RENOVAI_TOKEN_SECRET` conforme as necessidades do ambiente.
O arquivo `.env.example` lista as variáveis, mas é explicativo: a aplicação lê variáveis de ambiente e não carrega `.env` por conta própria.
Mantenha valores reais fora do Git; o `.gitignore` exclui arquivos `.env`.

## Execução

Com Java e Maven disponíveis, execute `mvn package` na raiz do repositório para gerar o WAR em `target/`.
Publique o WAR em um Tomcat compatível com as APIs Jakarta declaradas no `pom.xml` e configure as variáveis no processo do servidor.
Como alternativa, execute `docker build -t renovai .` e `docker run --rm -p 8080:8080 --env-file .env renovai`.
Nesse caso, prepare um `.env` com linhas `NOME=valor`; a imagem publica o WAR como aplicação raiz em `http://localhost:8080/`.
No IntelliJ, configure as mesmas variáveis na configuração de execução do Tomcat; o contexto da URL pode diferir do Docker.

## Verificação

`mvn package` verifica a compilação e o empacotamento do projeto; o repositório não contém uma suíte em `src/test` nesta revisão.
Para validar os fluxos, ainda é necessário executar JSPs e requisições no Tomcat, com PostgreSQL e SMTP configurados conforme o caso.
Os relatórios registram quais verificações foram feitas em cada etapa e quais ficaram pendentes.

## Estado atual

A integração e a apresentação das áreas administrativas continuam em evolução.
Campos obrigatórios, ações de edição, mensagens de resultado e alguns destinos de tela precisam de conferência em execução.
Consulte os relatórios mais recentes antes de tratar uma funcionalidade listada acima como concluída.
