---
name: refatorar-codigo
description: Revisar models, CRUDs das DAOs e chamadas dos servlets do Renovaí em ordem, com roteiro Markdown e execução somente do escopo escolhido pelo usuário.
---

# Refatorar código

Leia `AGENTS.md`, `PADRAO_CODIGO_BACKEND.md` e `CONFERENCIA_DAO_BANCO.md`. Delimite as models, DAOs e chamadas dos servlets afetados; se o pedido for geral, revise por lotes. Consulte `../script-banco.pdf` para as restrições documentadas. Preserve alterações preexistentes. Siga a ordem model → métodos CRUD que recebem a model → servlet que monta e envia a model. Verifique JSPs apenas quando essa integração exigir.

## Diagnóstico e relatório

Confira nomes, instanciação, seções, espaçamento, comentários e se cada método realiza o objetivo descrito acima dele. Nos métodos `CREATE`, confira a PK `BIGINT` representada por `long` ou `Long` e como a DAO usa `Utilitario.selecionarUltimoId(String tabela)` para definir e vincular explicitamente um novo ID; o método retorna o último ID, não o novo. Registre lacunas, inclusive tabela vazia, sem alterar o método utilitário nesta revisão. Na model, confira quais setters precisam filtrar e validar entradas, quais getters formatam a saída e se os construtores passam pelos setters pertinentes. A DAO deve receber a model como parâmetro nos CRUDs correspondentes, sem repetir ali os filtros; verifique o valor efetivamente enviado ao banco. Depois confira as chamadas nos servlets. Examine os métodos e regex já existentes na `Utilitario` para identificar reutilização nos setters e getters, sem presumir que estão corretos. Confira `VARCHAR(x)` nos campos sem `CHECK`, os ENUMs já existentes para `CHECK` de lista fechada e os campos textuais preparados em maiúsculas. Encaminhe a implementação ou correção dos filtros e formatações à skill `$atualizar-verificacoes-formatacoes`, para evitar duas etapas alterando o mesmo método.

Entregue um novo `NN-Refatoracao-Codigo.md` em `../Relatórios/` antes de alterar código; `NN` é o próximo número disponível e o arquivo anterior não deve ser substituído. Para cada achado, indique classe e método, comportamento atual, objetivo, ação concreta e verificação possível. Baseie a comparação de esquema no PDF indicado e não peça o DDL original para concluir essa revisão; apenas não apresente o PDF como prova do banco conectado. Termine com uma checklist **numerada, modular e executável**, separando passos de model, DAO e servlet e mostrando a dependência entre eles. Indique o estado de cada item para que o grupo possa executá-lo sem a IA. Não inclua identidade visual nem DOCX.

## Execução após escolha

Apresente o relatório e aguarde a escolha do usuário:

- **Parcial:** ajustar somente comentários de método, títulos de etapa, seções e espaçamento do escopo. O comentário registra o que o método deve fazer; divergências com o código permanecem na checklist. Não mudar lógica, SQL, assinatura, tipos ou ENUMs.
- **Completa:** executar todos os itens de código da checklist apresentada para o escopo escolhido.
- **Etapas escolhidas:** executar somente os números indicados pelo usuário.

Trabalhe em passos pequenos e atualize o estado da checklist com o que foi feito, não feito ou depende de decisão. Verifique compilação e testes que não dependam de escrita no banco quando fizer sentido. Não declare comportamento validado só porque compilou. Não acrescente arquitetura ou funcionalidades fora dos itens escolhidos.
