---
name: refatorar-codigo
description: Diagnosticar e executar a refatoração do backend Renovaí por etapas, das models aos CRUDs das DAOs e às chamadas dos servlets, com checklist Markdown e escolha explícita do escopo de execução.
---

# Refatorar código

Leia `AGENTS.md`, `PADRAO_CODIGO_BACKEND.md` e `CONFERENCIA_DAO_BANCO.md` antes da revisão. Use `../script-banco.pdf` como referência do esquema planejado, sem tratá-lo como prova do banco conectado nem pedir novamente o DDL original. Confira o código atual e os relatórios anteriores; um item marcado como concluído só permanece assim se o código ainda o sustentar. Preserve alterações preexistentes.

Trabalhe por entidade ou lote pequeno, na ordem **model → CRUD da DAO → servlet**. Examine JSPs apenas se essa integração exigir. Não amplie a revisão para identidade visual, arquitetura nova ou `Main`.

## Diagnóstico

1. **Model:** confira campos e tipos. Centralize filtros e validações de entrada nos setters e formatação de saída nos getters. Ao criar uma model, use instanciação sem parâmetros seguida dos setters; identifique construtores com parâmetros ou atribuições diretas que contornem as regras. Confira `ENUM` para `CHECK` de lista fechada e limite de `VARCHAR(x)` nos setters de campos sem `CHECK`; se o setter já valida os valores do `CHECK`, não acrescente uma segunda checagem de tamanho. Registre separadamente a entrada, o valor guardado na model, o valor persistido e o valor apresentado. Examine a reutilização possível de `Utilitario`. Encaminhe a criação ou correção de filtros, validações e formatações à skill `$atualizar-verificacoes-formatacoes`; esta skill não deve editar a mesma regra em paralelo.
2. **DAO:** confira cada CRUD que representa a entidade: recebimento da model, SQL e parâmetros, gravação do valor armazenado sem usar getter de apresentação, leitura, tratamento de recursos e exceções. Nos `READ`, confira a montagem da model por instanciação sem parâmetros e setters, inclusive na `CooperativaDAO`; liste os pontos que ainda usam construtor com parâmetros. Em `CREATE`, confira a PK `BIGINT` como `long` ou `Long` e a obtenção e vinculação explícita do próximo ID a partir de `Utilitario.selecionarUltimoId(String tabela)`. O utilitário retorna o último ID; registre o caso de tabela vazia e outros riscos encontrados, sem alterá-lo nesta etapa.
3. **Servlet:** confira a construção da model via setters, a chamada ao CRUD com a model e o tratamento distinto de erro de validação e erro de persistência. Revise a passagem para JSP somente quando necessária para exibir o resultado ou erro, sem expor detalhes de SQL.
4. **Organização:** confira nomes, instanciação, seções, espaçamento e comentários curtos que descrevam a intenção real de cada método. Registre divergências entre comentário e comportamento.

## Relatório e decisão

Antes de editar código, crie `../Relatórios/NN-Refatoracao-Codigo.md`, usando o próximo número livre e preservando os relatórios existentes. Para cada achado, informe classe e método, comportamento observado, objetivo, ação concreta e forma de verificar. Feche com uma checklist **de passo a passo**, numerada, executável e separada em **Models**, **DAOs** e **Servlets**. Cada item deve dizer o arquivo e método, o que fazer, qual item vem antes, como conferir o resultado e se está concluído, pendente ou depende de decisão. Inclua a substituição dos construtores com parâmetros encontrados nos `READ` da `CooperativaDAO` pela instanciação e pelos setters, sem presumir que essa mudança já foi feita. Aponte os itens reservados à skill de validações. Apresente o relatório e aguarde a escolha para executar:

- **Parcial:** altere somente comentários de método, títulos de etapa, seções e espaçamento aprovados. Não mude lógica, SQL, assinaturas, tipos ou `ENUM`; mantenha divergências de comportamento na checklist.
- **Completa:** execute os itens de código aprovados da checklist, respeitando a ordem das dependências e as decisões ainda pendentes.
- **Etapas escolhidas:** execute apenas os números indicados pelo usuário.

Quando esta skill fizer parte de `$revisar-back-renovai`, conclua primeiro as mudanças de model aprovadas em `$atualizar-verificacoes-formatacoes`; depois retome aqui os itens de DAO e servlet. Se a refatoração for chamada isoladamente, explicite essa dependência no relatório antes de alterar DAO ou servlet. Não peça nova autorização para um escopo já escolhido na conversa.

Execute em passos pequenos e atualize a checklist com o que foi concluído, permanece pendente ou exige decisão. Verifique compilação e testes sem escrita no banco quando aplicável; testes com escrita exigem banco isolado e confirmação explícita. Compilação sozinha não comprova o comportamento da aplicação.
