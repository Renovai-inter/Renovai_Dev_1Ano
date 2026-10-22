---
name: refatorar-codigo
description: Revisar o backend Renovaí, registrar pendências atuais das Models e planejar a correção das DAOs por severidade antes da integração com servlets.
---

# Refatorar código do backend Renovaí

Leia `AGENTS.md`, `PADRAO_CODIGO_BACKEND.md`, `CONFERENCIA_DAO_BANCO.md` e os relatórios anteriores. Consulte `../script-banco.pdf` como referência do esquema planejado, sem declarar que ele comprova o banco conectado. Confira sempre o código atual: um relatório anterior não comprova que um item continua concluído. Preserve alterações preexistentes.

## Protocolo de diagnóstico

1. Delimite as entidades e o estágio pedido pelo usuário. A ordem de dependência é **Model e Utilitario → DAO → Servlet**. Não comece a integração seguinte só porque a etapa anterior foi analisada.
2. Reexamine as Models do escopo: tipos, `ENUM`, nulidade, limites `VARCHAR`, filtros nos setters, formatação nos getters e chamadas à `Utilitario`. Registre apenas pendências comprovadas e diga se bloqueiam a DAO. A correção de filtros e formatadores pertence a `$atualizar-verificacoes-formatacoes`; não edite a mesma regra nesta skill.
3. Em cada DAO, compare o contrato da Model com as chamadas, o SQL, a ordem e o tipo de cada parâmetro, as colunas obrigatórias do PDF, o mapeamento de leitura, IDs, transações, recursos e exceções. Todos os métodos públicos de operação recebem valores preenchidos como parâmetros, instanciam a Model sem argumentos, preenchem pelos setters e retornam uma Model ou `ArrayList` de Models. Métodos auxiliares de JDBC e consulta de ID seguem seus tipos próprios. Não use um getter de apresentação como valor de gravação sem conferir a forma persistida.
4. Classifique achados com evidência e impacto: **P0 bloqueante** para incompatibilidade de compilação, SQL que impede a operação, escrita parcial ou acesso indevido; **P1 alta** para dado errado, perda de histórico, leitura incorreta ou violação provável de restrição; **P2 média** para recursos, tratamento de erro e organização que não estejam cobertos por P0 ou P1. Ordene por severidade e, dentro dela, por dependência de execução. Indique decisões externas separadamente; não invente regra de domínio ausente.
5. Registre os servlets e JSPs afetados como fase posterior. Inspecione a chamada necessária para identificar dependências, sem iniciar a integração se ela não foi autorizada. A `Main` fica na skill de testes.

## Protocolo de entrega e execução

Crie um novo `../Relatórios/NN-Refatoracao-Codigo.md` com o próximo número disponível, sem substituir relatórios anteriores. Inclua: escopo e limites da verificação; situação das Models; checklist numerada das DAOs em ordem de severidade; para cada item, arquivo/método, evidência, ação, dependência, forma de conferir e estado. Separe decisões pendentes dos passos executáveis. Não trate compilação como prova de funcionamento nem o PDF como DDL verificado.

Apresente o relatório antes de alterar código e execute somente a modalidade ou os itens escolhidos pelo usuário. **Parcial** altera apenas comentários, seções e espaçamento. **Completa** executa o código aprovado do escopo, respeitando dependências. **Etapas escolhidas** executa apenas os números indicados. Uma escolha para Models não autoriza DAOs; uma escolha para DAOs não autoriza servlets. Se o usuário já escolheu o escopo nesta conversa, não peça nova autorização.

Ao executar, trabalhe em lotes pequenos e atualize o estado de cada item com o resultado realmente verificado. Preserve a causa de falhas de persistência e distinga erro de banco de consulta vazia. Verifique a compilação quando fizer parte do escopo autorizado; operações de escrita ou exclusão em teste exigem banco isolado e confirmação explícita. Não execute a `Main` antiga sem inspeção, pois ela contém exclusão ao iniciar.
