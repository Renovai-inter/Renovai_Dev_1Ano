---
name: atualizar-verificacoes-formatacoes
description: Revisar filtros de entrada e formatações de saída da Utilitario usadas por DAOs e models do Renovaí, com relatório Markdown e execução parcial, completa ou por itens.
---

# Atualizar verificações e formatações

Leia `AGENTS.md` e `PADRAO_CODIGO_BACKEND.md`. Revise os métodos de filtro e formatação da `Utilitario` e seus usos nas DAOs e models do escopo. `selecionarUltimoId` fica fora desta revisão. O PDF do banco mostra a estrutura planejada pelo grupo e ajuda a identificar tipos e restrições, sem provar o estado da conexão atual. Não alterar servlets ou JSPs nesta etapa.

## Diagnóstico e relatório

Para cada dado usado no escopo, mostre o formato recebido, o formato preparado para gravar e, nos métodos `READ`, o formato de apresentação. Confira se os filtros existentes realmente devolvem o valor descrito e se dados inválidos estão sendo enviados como `null` ou `"invalido"`. Esta skill detalha e corrige os filtros, as formatações e seus usos nas DAOs que a refatoração apontou ou que forem encontrados aqui. Identifique validações ausentes e pontos em que a DAO deve distinguir dado inválido de `SQLException`. Não criar nova classe de exceção.

Entregue um novo `NN-Validacao-Dados.md` em `../Relatórios/` antes de mudar código; `NN` é o próximo número disponível e o arquivo anterior não deve ser substituído. Faça uma checklist numerada com classe, método, dado, problema, ação exata e modo de verificar. Para **cada método novo** de filtro ou formatação, peça ao usuário o regex e o formato de retorno; deixe esse item pendente até receber a definição. Não invente um regex. Proponha `throw` com exceção pronta do Java adequada ao dado inválido; preserve o `try/catch (SQLException)` das DAOs para erro de banco.

## Execução após escolha

- **Parcial:** ajustar apenas comentários, seções e espaçamento conforme o padrão; manter os problemas de lógica na checklist para o grupo resolver.
- **Completa:** executar todos os itens de código definidos no relatório, desde que os regex e formatos necessários tenham sido fornecidos.
- **Etapas escolhidas:** executar somente os números indicados, deixando os demais pendentes.

Em qualquer modalidade, registrar o que foi feito e como foi verificado. Não mudar a forma de apresentar mensagens em servlet/JSP nesta etapa.
