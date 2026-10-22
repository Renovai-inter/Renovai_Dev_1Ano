---
name: refatorar-codigo
description: Revisar o backend Renovaí por camada, registrar pendências de Models e DAOs e planejar a integração dos Servlets preservando seus fluxos existentes.
---

# Refatorar código do backend Renovaí

Leia `AGENTS.md`, `PADRAO_CODIGO_BACKEND.md`, `CONFERENCIA_DAO_BANCO.md` e os relatórios anteriores. Consulte `../script-banco.pdf` como referência do esquema planejado, sem declarar que ele comprova o banco conectado. Confira sempre o código atual: um relatório anterior não comprova que um item continua concluído. Preserve alterações preexistentes.

## Protocolo de diagnóstico

1. Delimite as entidades e o estágio pedido pelo usuário. A ordem de dependência é **Model e Utilitario → DAO → Servlet**. Não comece a integração seguinte só porque a etapa anterior foi analisada.
2. Reexamine as Models do escopo: tipos, `ENUM`, nulidade, limites `VARCHAR`, filtros nos setters, formatação nos getters e chamadas à `Utilitario`. Registre apenas pendências comprovadas e diga se bloqueiam a DAO. A correção de filtros e formatadores pertence a `$atualizar-verificacoes-formatacoes`; não edite a mesma regra nesta skill.
3. Em cada DAO, compare o contrato da Model com as chamadas, o SQL, a ordem e o tipo de cada parâmetro, as colunas obrigatórias do PDF, o mapeamento de leitura, IDs, sequências de gravação, recursos e exceções. Os métodos públicos de operação recebem o objeto da Model correspondente, preenchido pelo chamador por construtor que usa os setters ou pelos próprios setters, e retornam uma Model ou `ArrayList` de Models. Leituras de `ResultSet` podem instanciar a Model sem argumentos. Filtros livres e períodos de relatório, além de auxiliares JDBC, seguem seus tipos próprios. Não use um getter de apresentação como valor de gravação sem conferir a forma persistida. Siga as preferências do padrão: `if/else` simples, sem `switch/case` nos `UPDATE` de cooperativa, sem comandos de transação ou `try/catch` aninhado, e com o mínimo de sobrecargas e duplicações.
4. Classifique achados com evidência e impacto: **P0 bloqueante** para incompatibilidade de compilação, SQL que impede a operação, escrita parcial ou acesso indevido; **P1 alta** para dado errado, perda de histórico, leitura incorreta ou violação provável de restrição; **P2 média** para recursos, tratamento de erro e organização que não estejam cobertos por P0 ou P1. Ordene por severidade e, dentro dela, por dependência de execução. Indique decisões externas separadamente; não invente regra de domínio ausente.
5. Registre os servlets afetados e leia os JSPs apenas para identificar parâmetros recebidos e dados esperados. Para fluxos administrativos, consulte `../Área ADM.pdf` e identifique o comportamento de backend necessário, sem declarar a resposta da tela pronta por causa de uma DAO. A conferência detalhada do backend com essas fontes pertence a `$conferir-telas-admin`; não proponha alteração do frontend. A `Main` fica na skill de testes.

## Etapa dos Servlets

Revise a camada de Servlets como um lote próprio, depois de conferir os contratos de Model e DAO dos quais ela depende. Organize o diagnóstico por Servlet e, dentro dele, por cada ação já existente. Inclua `AutenticacaoFilter` quando o fluxo de login ou sessão exigir ajuste; examine `CorsFilter` apenas diante de incompatibilidade concreta. Se faltar um dado da Model ou um método da DAO, registre a dependência em checklist separada para a camada anterior e retome a ação do Servlet depois da decisão correspondente.

Priorize a menor correção necessária para compatibilizar chamadas, tipos e formatos. Preserve, sempre que compatíveis com os contratos atuais, rotas, parâmetros, atributos de requisição, mensagens, dados da sessão, `forward`, redirecionamentos e a resposta atual de cada ação. Não reorganize um método só para impor um formato uniforme. Confira o que cada ação recebe, qual operação chama e o que entrega à JSP, sem editar o frontend.

Mantenha `doGet` e `doPost` fáceis de localizar e escreva acima de cada método um comentário curto que descreva seu objetivo. Dentro dos métodos, use apenas os títulos de etapa aplicáveis: `// Leitura da requisição`, `// Validação dos dados`, `// Processamento da ação`, `// Preparação da resposta` e `// Encaminhamento`. Siga o espaçamento definido em `PADRAO_CODIGO_BACKEND.md` para os títulos, sem aplicar aos Servlets os títulos de SQL das DAOs.

Padronize exceções com prioridade para falhas vindas da DAO: encapsule-as em `ServletException` com mensagem geral e causa preservada, sem expor detalhes de SQL à página. Trate erros de entrada e validação no fluxo de resposta já usado pela ação, salvo incompatibilidade comprovada. Não crie classes de exceção novas apenas para esta integração.

Para cada ação, confira estaticamente o contrato com a JSP e a DAO e compile o projeto inteiro com Java 17. Quando houver ambiente de teste configurado, verifique requisição e resposta em execução; caso contrário, registre essa verificação como pendente. Compilação e leitura de código não comprovam o funcionamento em execução.

## Protocolo de entrega e execução

Crie um novo `../Relatórios/NN-Refatoracao-Codigo.md` com o próximo número disponível, sem substituir relatórios anteriores. Inclua: escopo e limites da verificação; situação das Models; checklist numerada das DAOs em ordem de severidade; para cada item, arquivo/método, evidência, ação, dependência, forma de conferir e estado. Separe decisões pendentes dos passos executáveis. Não trate compilação como prova de funcionamento nem o PDF como DDL verificado.

Quando o escopo for a camada de Servlets, organize a checklist numerada por Servlet e ação existente. Para cada item, indique incompatibilidade comprovada, menor correção necessária, dependências de Model ou DAO e verificação possível. Preserve a autorização separada de cada camada.

Apresente o relatório antes de alterar código e execute somente a modalidade ou os itens escolhidos pelo usuário. **Parcial** altera apenas comentários, seções e espaçamento. **Completa** executa o código aprovado do escopo, respeitando dependências. **Etapas escolhidas** executa apenas os números indicados. Uma escolha para Models não autoriza DAOs; uma escolha para DAOs não autoriza servlets. Se o usuário já escolheu o escopo nesta conversa, não peça nova autorização.

Ao executar, trabalhe em lotes pequenos e atualize o estado de cada item com o resultado realmente verificado. Preserve a causa de falhas de persistência e distinga erro de banco de consulta vazia. Verifique a compilação quando fizer parte do escopo autorizado; operações de escrita ou exclusão em teste exigem banco isolado e confirmação explícita. Não execute a `Main` antiga sem inspeção, pois ela contém exclusão ao iniciar.
