---
name: implementar-testes-main
description: Preparar a Main do Renovaí como menu manual para testar métodos das DAOs sem JSP, pedindo dados conforme as models e registrando resultados em Markdown.
---

# Implementar testes pela Main

Leia `AGENTS.md` e `PADRAO_CODIGO_BACKEND.md`. Inspecione a `Main`, os métodos públicos das DAOs e os setters e getters das models antes de alterar ou executar. O menu deve montar a model por seus setters e passá-la ao CRUD conforme a assinatura aprovada; não duplicar na `Main` os filtros da model. A `Main` atual contém uma exclusão ao iniciar: retire chamadas automáticas desse tipo ao construir o menu. Não alterar servlets ou JSPs nesta skill. Resultado na `Main` não valida o comportamento nem a apresentação das telas administrativas; registre essa conferência como dependência de `$conferir-telas-admin`.

## Plano antes do código

Crie um novo `NN-Testes-Main.md` em `../Relatórios/`, usando o próximo número disponível e preservando os arquivos anteriores. Primeiro, liste as DAOs e os métodos a incluir, as perguntas que o menu fará, os dados necessários, as operações que gravam ou excluem e uma checklist numerada para construir o menu. Apresente o plano e espere o usuário escolher execução completa ou itens específicos antes de alterar a `Main`.

## Menu

- Um `while` principal mostra as DAOs numeradas; `0` encerra. Ao escolher uma DAO, outro menu mostra seus métodos; `0` volta.
- O teste pergunta ao usuário os dados necessários para o método escolhido, monta a model quando couber, executa a DAO e mostra entrada resumida, retorno e possível erro. Para métodos `CREATE`, conferir se a DAO obtém e vincula a PK `BIGINT` por meio do fluxo que usa `selecionarUltimoId(String tabela)`; não presumir geração automática pelo banco nem pedir ao usuário o ID quando a DAO deve defini-lo. Ao terminar, volta ao menu.
- Cubra os métodos existentes em passos pequenos. Não criar laços ou classes extras só para demonstrar sintaxe. Trate entrada não numérica no menu sem encerrar o programa.
- Identifique no menu os métodos que gravam ou excluem. A escolha do teste pelo usuário inicia a operação na conexão que ele configurou; nenhuma escrita ou exclusão ocorre ao iniciar a `Main`. Não imprimir credenciais.
- Em cadastros que envolvem mais de uma tabela, como cooperativa, usuário e cooperado, mostrar separadamente os retornos disponíveis. Não chamar o teste de concluído apenas porque um retorno não é nulo.

## Registro

Depois da execução autorizada, complete o **mesmo relatório** com o estado da checklist por DAO e método: dados pedidos, operação, retorno esperado, retorno observado, erro e o que ainda falta testar. Se os testes ainda não foram executados, deixe o retorno observado como pendente. Quando houver uma dependência de código ainda incorreto ou de conexão indisponível, registre-a sem inventar um resultado. Não executar operações de banco apenas para preencher o relatório; a execução dos testes segue a escolha do usuário no menu.
