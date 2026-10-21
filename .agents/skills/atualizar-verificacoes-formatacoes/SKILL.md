---
name: atualizar-verificacoes-formatacoes
description: Revisar filtros nos setters e formatações nos getters das models do Renovaí, reutilizando a Utilitario e entregando um roteiro Markdown antes de executar mudanças.
---

# Atualizar verificações e formatações

Leia `AGENTS.md`, `PADRAO_CODIGO_BACKEND.md` e `CONFERENCIA_DAO_BANCO.md`. Comece pelas models do escopo: revise filtros e validações nos setters e formatações de saída nos getters. Reutilize métodos existentes da `Utilitario` quando corresponderem ao dado, sem copiar regex. `selecionarUltimoId` fica fora desta revisão. Depois identifique DAOs e servlets afetados pelas mudanças de contrato. Os métodos públicos das DAOs recebem valores preenchidos, montam Models pelos setters e retornam Model ou `ArrayList` de Models. O PDF do banco é a referência de estrutura escolhida pelo grupo; não peça DDL original para esta revisão.

## Diagnóstico e relatório

Para cada atributo usado no escopo, mostre o formato recebido pelo setter, o valor armazenado na model, o valor necessário para o banco e o formato devolvido pelo getter para apresentação. Confira se os métodos existentes da `Utilitario` realmente devolvem o valor descrito e se entradas inválidas viram `null` ou `"invalido"`. Esta skill detalha e corrige os filtros e formatações apontados pela refatoração ou encontrados aqui, centralizando a regra na model. Identifique as chamadas de DAO e servlet que precisam mudar para usar a model. Não permitir que um getter formatado para exibição seja usado sem conferência como valor de gravação. Diferencie erro lançado pelo setter de `SQLException`; não crie classe nova de exceção.

Entregue um novo `NN-Validacao-Dados.md` em `../Relatórios/` antes de mudar código; `NN` é o próximo número disponível e o arquivo anterior não deve ser substituído. Faça uma checklist numerada com model, setter ou getter, dado, problema, ação exata, chamadas afetadas e modo de verificar, em ordem model → DAO → servlet. Para **cada método novo** de filtro ou formatação, peça ao usuário o regex e o formato de retorno; deixe esse item pendente até receber a definição. Não invente um regex. Proponha `throw` com exceção pronta do Java adequada ao dado inválido; preserve o `try/catch (SQLException)` das DAOs para erro de banco.

## Execução após escolha

- **Parcial:** ajustar apenas comentários, seções e espaçamento conforme o padrão; manter os problemas de lógica na checklist para o grupo resolver.
- **Completa:** executar todos os itens de código definidos no relatório, desde que os regex e formatos necessários tenham sido fornecidos.
- **Etapas escolhidas:** executar somente os números indicados, deixando os demais pendentes.

Em qualquer modalidade, registre o que foi feito e como foi verificado. Na execução de código aprovada, atualize as chamadas dos servlets afetadas para passar os valores à DAO e lidar com a validação dos setters; leia a JSP apenas para conferir o contrato de resposta necessário. Para a área administrativa, anote se o backend prepara valor formatado, validação e mensagem compatíveis com `../Área ADM.pdf` e encaminhe a conferência detalhada a `$conferir-telas-admin`. Não altere o frontend.
