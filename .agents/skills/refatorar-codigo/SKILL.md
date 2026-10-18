---
name: refatorar-codigo
description: Revisar a padronização de DAOs e models do Renovaí, entregar roteiro Markdown em passos pequenos e executar apenas a modalidade ou os itens escolhidos pelo usuário.
---

# Refatorar código

Leia `AGENTS.md` e `PADRAO_CODIGO_BACKEND.md`. Delimite as DAOs e models pedidas; se o pedido for geral, revise as classes existentes por lotes. Consulte `../script-banco.pdf` para as restrições documentadas. Preserve alterações preexistentes. Servlets e JSPs ficam fora das mudanças desta skill; registre dependências encontradas para conversa futura.

## Diagnóstico e relatório

Confira nomes, instanciação, seções, espaçamento, comentários e se cada método realiza o objetivo descrito acima dele. Revise também quais entradas ainda precisam de validação, quais campos textuais devem ser preparados em maiúsculas e quais `CHECK` do PDF definem uma lista fechada. Antes de propor um ENUM novo, confira os ENUMs Java que já existem; planeje seu uso nas models e DAOs correspondentes. Outros tipos de `CHECK` pedem validação, não ENUM. Liste os problemas de filtros e formatações no roteiro geral, mas encaminhe sua correção à skill `$atualizar-verificacoes-formatacoes`, para evitar duas etapas alterando o mesmo método.

Entregue um novo `NN-Refatoracao-Codigo.md` em `../Relatórios/` antes de alterar código; `NN` é o próximo número disponível e o arquivo anterior não deve ser substituído. Para cada achado, indique classe e método, comportamento atual, objetivo, ação concreta e verificação possível. Separe diferenças do código em relação ao PDF das regras confirmadas no banco. Termine com uma checklist **numerada, modular e executável**: um passo pequeno por item, em ordem útil, para o grupo poder fazer o trabalho sem a IA. Indique dependências entre itens e o estado de cada um. Não inclua identidade visual nem DOCX.

## Execução após escolha

Apresente o relatório e aguarde a escolha do usuário:

- **Parcial:** ajustar somente comentários de método, títulos de etapa, seções e espaçamento do escopo. O comentário registra o que o método deve fazer; divergências com o código permanecem na checklist. Não mudar lógica, SQL, assinatura, tipos ou ENUMs.
- **Completa:** executar todos os itens de código da checklist apresentada para o escopo escolhido.
- **Etapas escolhidas:** executar somente os números indicados pelo usuário.

Trabalhe em passos pequenos e atualize o estado da checklist com o que foi feito, não feito ou depende de decisão. Verifique compilação e testes que não dependam de escrita no banco quando fizer sentido. Não declare comportamento validado só porque compilou. Não acrescente arquitetura ou funcionalidades fora dos itens escolhidos.
