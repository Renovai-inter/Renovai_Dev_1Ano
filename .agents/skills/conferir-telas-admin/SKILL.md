---
name: conferir-telas-admin
description: Verificar o comportamento do backend administrativo do Renovaí com Área ADM.pdf e JSPs existentes, sem alterar o frontend.
---

# Conferir o backend com as telas administrativas

Leia `AGENTS.md`, `PADRAO_CODIGO_BACKEND.md`, `CONFERENCIA_DAO_BANCO.md` e os relatórios anteriores relevantes. Consulte `../Área ADM.pdf` visualmente como referência das telas e `../script-banco.pdf` como referência do esquema planejado. Leia os JSPs/JSPFs existentes apenas para conhecer o contrato de entrada e saída. O desenho não comprova implementação, nem altera sozinho os valores aceitos pelo banco. Preserve mudanças preexistentes e não modifique JSP, JSPF, CSS, JavaScript ou outros arquivos do frontend.

## Diagnóstico por fluxo

1. Delimite os comportamentos administrativos pedidos. Para cada um, registre o resultado indicado no PDF e localize o JSP/JSPF existente, se houver. A ausência de tela é contexto, não uma tarefa para criá-la.
2. Leia os nomes e valores dos parâmetros, `method`, `action` e atributos de request esperados pelo JSP. Considere lista vazia, sucesso e erro quando forem observáveis na tela. Não faça revisão de layout, estilo ou navegação como trabalho a implementar.
3. Rastreie `JSP → Servlet → Model → DAO → esquema documentado` para a entrada e o caminho inverso para os dados preparados pelo backend. Aponte operações que não produzem os dados esperados pela tela e entradas que o backend não processa.
4. Separe falha de backend de divergência que depende do frontend. Quando PDF, JSP e `script-banco.pdf` discordarem, registre as fontes e a decisão de domínio necessária; não proponha mudar a interface nesta skill.
5. Classifique o comportamento do backend como atendido, parcial, ausente ou dependente de decisão. Só marque como verificado em execução se a aplicação tiver sido aberta e o resultado observado; leitura estática e compilação são verificações distintas.

## Relatório e execução

Crie `../Relatórios/NN-Conferencia-Backend-ADM.md` com o próximo número disponível, sem sobrescrever arquivos. Para cada item da checklist numerada, informe comportamento esperado, fontes consultadas, arquivos de backend envolvidos, evidência, resultado da verificação e limite da conclusão. Registre diferenças que dependem do frontend somente como observações, fora da checklist.

A checklist desta skill orienta somente a leitura e a conferência; não altera código de backend nem frontend. Se houver falha de backend, registre a ação sugerida como pendência para uma tarefa posterior, sujeita ao relatório e à autorização previstos em `AGENTS.md`. Não converta a aprovação desta conferência em autorização para corrigir código.

Ao concluir, registre o estado de cada item e o tipo de verificação realizada. Se faltarem ambiente, dados ou decisão de domínio, mantenha o resultado funcional como pendente. Não use a `Main` como prova da resposta apresentada na JSP.
