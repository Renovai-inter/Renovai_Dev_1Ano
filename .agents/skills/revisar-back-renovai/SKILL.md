---
name: revisar-back-renovai
description: Coordenar refatoração, verificações e formatações, e testes manuais pela Main no Renovaí, reunindo resultados em uma consolidação Markdown.
---

# Revisar backend Renovaí

Leia `AGENTS.md`, `PADRAO_CODIGO_BACKEND.md` e `CONFERENCIA_DAO_BANCO.md`. Confirme o escopo pedido em models, DAOs e servlets afetados, com a `Utilitario` como fonte de métodos reutilizáveis; a `Main` entra apenas na etapa de testes. Preserve a ordem model → CRUD/DAO → servlet em cada lote. Execute as skills nesta ordem:

1. `$refatorar-codigo`: diagnóstico e relatório do percurso model → DAO → servlet, com a escolha da modalidade ou dos itens pelo usuário.
2. `$atualizar-verificacoes-formatacoes`: diagnóstico e relatório dos filtros nos setters e das formatações nos getters; após a escolha do usuário, executar primeiro os itens de model autorizados.
3. Retomar os itens de código autorizados em `$refatorar-codigo`, adaptando CRUDs/DAOs e depois os servlets que os chamam. Se uma model necessária não estiver pronta, deixar o item dependente pendente em vez de inverter a ordem.
4. `$implementar-testes-main`: menu manual e registro dos resultados possíveis, após os contratos usados pelos testes estarem prontos.

Cada relatório vai para `../Relatórios/`. Apresente o checklist de uma etapa antes de aplicar alterações de código dessa etapa; a escolha da modalidade em uma etapa não autoriza automaticamente as seguintes. A ordem de análise pode começar pela refatoração, mas a execução dependente começa pela model, segue para a DAO e termina no servlet. Quando a escolha faltar, aguarde-a e continue apenas trabalho independente. Não executar a `Main` antiga com exclusão automática. Preserve mudanças preexistentes.

Ao final, entregue um novo `NN-Consolidacao-Refatoracao.md` em `../Relatórios/`, usando o próximo número disponível e preservando os arquivos anteriores. Inclua: escopo analisado, o que foi feito em cada etapa, resultados realmente verificados, pendências em checklist numerada e sugestões extras claramente opcionais. Cada pendência deve dizer o que fazer e como conferir, de modo que o grupo possa executá-la por conta própria. Não calcular porcentagem de melhoria, não criar DOCX e não exigir identidade visual. Não tratar compilação como prova de funcionamento nem o PDF do esquema como conferência do banco conectado.
