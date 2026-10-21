---
name: conferir-telas-admin
description: Conferir os fluxos administrativos do Renovaí entre Área ADM.pdf, JSPs, servlets, DAOs, models e esquema documentado, com relatório antes de corrigir a interface.
---

# Conferir telas administrativas

Leia `AGENTS.md`, `PADRAO_CODIGO_BACKEND.md`, `CONFERENCIA_DAO_BANCO.md` e os relatórios anteriores relevantes. Consulte `../Área ADM.pdf` visualmente como referência das telas e `../script-banco.pdf` como referência do esquema planejado. O desenho não comprova implementação, nem altera sozinho os valores aceitos pelo banco. Preserve mudanças preexistentes.

## Diagnóstico por fluxo

1. Delimite as telas e ações pedidas. Para cada uma, registre a evidência visual do PDF e localize o JSP/JSPF existente ou marque a tela como ausente. Não invente tela, rota ou regra para preencher lacunas.
2. Compare estrutura, campos, opções, filtros, listas, botões, navegação e estados exibidos. Confira nomes e valores dos parâmetros, `method`, `action`, links, atributos de request e tratamento de lista vazia, sucesso e erro.
3. Rastreie `JSP → Servlet → Model → DAO → esquema documentado` para a entrada e o caminho inverso para o resultado visível. Aponte operações de backend que não chegam à tela e controles da tela sem operação correspondente.
4. Separe divergências de apresentação, integração e regra de negócio. Quando PDF, JSP e `script-banco.pdf` discordarem, registre as três versões e a decisão necessária. Não faça a interface enviar valor que contrarie `CHECK` ou enum documentado sem decisão do grupo.
5. Classifique cada fluxo como implementado, parcial, ausente ou dependente de decisão. Só marque como verificado em execução se a aplicação tiver sido aberta e o resultado observado; leitura estática e compilação são verificações distintas.

## Relatório e execução

Crie `../Relatórios/NN-Telas-Administrativas.md` com o próximo número disponível, sem sobrescrever arquivos. Para cada item da checklist numerada, informe tela e ação, arquivos envolvidos, evidência, ação concreta, dependências e forma de conferir. Separe decisões pendentes dos itens executáveis.

Apresente a checklist antes de mudar Java, JSP, CSS ou JavaScript. Execute apenas os itens escolhidos pelo usuário ou a checklist completa se ele já a tiver aprovado para este escopo. Uma aprovação de backend não aprova mudanças de tela. Preserve a ordem de implementação `Model → DAO → Servlet → JSP` quando houver dependências. Não trate **parcial** como autorização para alteração visual ou funcional: nessa etapa, use checklist completa ou números específicos.

Após executar, atualize o relatório com o estado de cada item e o tipo de verificação realizada. Se faltarem ambiente, dados ou decisão de domínio, mantenha o resultado funcional como pendente. Não use a `Main` como prova do resultado na JSP.
