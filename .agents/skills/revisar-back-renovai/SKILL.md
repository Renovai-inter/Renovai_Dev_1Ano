---
name: revisar-back-renovai
description: Coordenar refatoração, verificações e formatações, e testes manuais pela Main no Renovaí, reunindo resultados em uma consolidação Markdown.
---

# Revisar backend Renovaí

Leia `AGENTS.md` e `PADRAO_CODIGO_BACKEND.md`. Confirme o escopo pedido dentro de DAOs, models e `Utilitario`; a `Main` entra apenas na etapa de testes. Execute as skills nesta ordem:

1. `$refatorar-codigo`: diagnóstico, relatório e execução parcial, completa ou por itens escolhidos.
2. `$atualizar-verificacoes-formatacoes`: diagnóstico, relatório e execução escolhida para filtros e formatações.
3. `$implementar-testes-main`: menu manual e registro dos resultados possíveis.

Cada relatório vai para `../Relatórios/`. Apresente o checklist de uma etapa antes de aplicar alterações de código dessa etapa; a escolha da modalidade em uma etapa não autoriza automaticamente as seguintes. Quando a escolha faltar, aguarde-a e continue apenas trabalho independente. Não executar a `Main` antiga com exclusão automática. Preserve mudanças preexistentes.

Ao final, entregue um novo `NN-Consolidacao-Refatoracao.md` em `../Relatórios/`, usando o próximo número disponível e preservando os arquivos anteriores. Inclua: escopo analisado, o que foi feito em cada etapa, resultados realmente verificados, pendências em checklist numerada e sugestões extras claramente opcionais. Cada pendência deve dizer o que fazer e como conferir, de modo que o grupo possa executá-la por conta própria. Não calcular porcentagem de melhoria, não criar DOCX e não exigir identidade visual. Não tratar compilação como prova de funcionamento nem o PDF do esquema como conferência do banco conectado.
