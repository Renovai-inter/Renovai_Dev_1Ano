# Padrão de código do backend Renovaí

Este documento orienta a revisão didática das classes `dao`, `model` e `util` do projeto. As skills devem mostrar o que precisa ser feito em passos pequenos, para que o grupo possa escolher entre executar as mudanças ou fazê-las por conta própria. Servlets e JSPs serão discutidos em outra etapa. Não introduzir frameworks, camadas ou classes de exceção apenas para padronizar o código.

## Referência do banco

`../script-banco.pdf` representa a estrutura planejada pelo grupo: tabelas, colunas, tipos e restrições como `NOT NULL` e `CHECK`. Ele orienta a revisão, mas não comprova o estado do banco conectado. Ao encontrar diferença entre código e PDF, registrar a diferença no relatório para decisão do grupo. Não inventar valores ou regras ausentes do documento.

## Nomes e instanciação

- Classes e tipos ENUM usam `PascalCase`; métodos, atributos, parâmetros e variáveis usam `camelCase`; constantes e valores de ENUM usam `UPPER_SNAKE_CASE`. Nomes de tabelas e colunas nas consultas SQL seguem `snake_case`.
- Usar nomes em português que indiquem a função do elemento. Manter `DAO` no nome das classes de acesso a dados e preservar os pacotes existentes.
- Ao revisar uma classe, mostrar como `Conexao`, `Utilitario` e outras dependências são instanciadas. Padronizar com o estilo já usado pelo grupo, sem introduzir injeção de dependências.
- Antes de renomear algo usado por outras classes, identificar as chamadas afetadas. Alterações em servlet e JSP ficam fora do escopo atual e devem ser registradas como dependência para etapa futura.

## Organização, espaçamento e comentários

- Nas DAOs, usar as seções `CREATE`, `READ`, `UPDATE` e `DELETE` para localizar os métodos. Nas models, separar atributos e métodos quando isso ajudar a leitura.
- Acima de cada método, escrever um comentário simples de uma ou duas linhas dizendo o que ele **deve fazer**. Não usar Javadoc. Se o código não cumprir o objetivo, preservar a intenção no comentário e registrar a divergência no relatório.
- Dentro dos métodos, usar apenas os títulos de etapa que se aplicarem: `// Declaração`, `// Comando SQL`, `// Executar PreparedStatement` e `// Execução e retorno`. Deixar uma linha vazia antes do título e outra entre o título e o bloco. Não separar os comandos dentro do mesmo bloco com linhas vazias.
- No bloco de `PreparedStatement`, manter preparação, preenchimento dos parâmetros e execução no `try`; o `catch (SQLException)` trata falhas do banco. Se o método usar a rotina atual de `Conexao` para fechar a conexão, preservar esse formato. Não exigir `try-with-resources` nesta revisão.
- Comentários de etapa ajudam a localizar o código; comentários de método mostram a intenção. Na execução parcial, ajustar comentários e organização visual sem mudar a lógica.

## Dados, validação e formatação

- Revisar quais campos ainda precisam de validação e quais restrições `CHECK` do PDF correspondem a valores fechados. Para listas fechadas usadas pelas DAOs e models, conferir os ENUMs já existentes antes de propor ou criar outro. Usar o ENUM nas models e DAOs do escopo aprovado; outros tipos de `CHECK`, como limites numéricos, são casos de validação, não de ENUM.
- Nos métodos da `Utilitario`, filtrar dados de entrada para gravação e formatar dados lidos para apresentação. Para cada filtro ou formatação **novo**, pedir ao usuário o regex e o formato de retorno antes de implementar. Conferir os filtros existentes; não presumir que funcionam só porque já estão escritos. `selecionarUltimoId` é uma exceção existente e fica fora desta revisão.
- Para valores que realmente podem conter texto, preparar a gravação em maiúsculas com `toUpperCase()`. Não aplicar a conversão automaticamente a qualquer `String`: CNPJ, por exemplo, continua sendo uma `String` formada somente por dígitos, após remover a pontuação e validar o resultado. Nos métodos `READ`, usar a formatação de saída pertinente aos dados retornados.
- Dado inválido deve gerar `throw` de uma exceção adequada já existente no Java, com mensagem compreensível, em vez de retornar `"invalido"` ou `null`. `IllegalArgumentException` pode indicar formato ou valor inválido; a conversão para inteiro pode gerar `NumberFormatException`. Essas exceções não exigem `throws` na assinatura. Uma exceção verificada, como `SQLException`, exige `catch` ou `throws`; nas DAOs, manter `catch (SQLException)`.
- Não confundir erro de validação com falha de banco. Não transformar `SQLException` em um retorno que pareça sucesso ou ausência normal de dados. Não criar classe de exceção própria para este fluxo. A apresentação da mensagem em servlet/JSP será definida em etapa futura.

## Relatórios e execução das skills

- Cada análise entrega um arquivo Markdown em `../Relatórios/`, fora do repositório. Use o próximo número disponível no nome (`01-...md`, `02-...md`) e não sobrescreva relatórios anteriores. Não criar DOCX nem exigir identidade visual. O relatório indica classe, método, divergência, ação concreta e como verificar, com checklist numerada em passos pequenos e independentes.
- Depois do relatório de refatoração ou verificações, o usuário escolhe: **parcial** (somente comentários, seções e espaçamento), **completa** (Codex executa todos os itens de código da checklist apresentada para o escopo) ou **etapas escolhidas** (somente os números indicados). Na skill da `Main`, a escolha é entre construir o menu completo apresentado ou apenas os itens indicados. Preparar o relatório não autoriza alterar código.
- A skill de testes pela `Main` prepara um menu manual por DAO e método. Operações de escrita ou exclusão só ocorrem após a escolha do teste no menu, usando a conexão configurada pelo usuário; nada desse tipo deve executar ao iniciar.
- A revisão geral gera uma Consolidação de Refatoração em Markdown: o que foi feito, pendências em checklist e sugestões opcionais. Não usar porcentagem de melhoria.
