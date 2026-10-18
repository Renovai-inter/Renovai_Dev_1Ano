# Conferência inicial entre DAOs e esquema do Renovaí

Fonte do esquema: `../script-banco.pdf` (estrutura planejada pelo grupo, não DDL conferido). Fonte do código: `src/main/java` deste projeto. Esta é uma conferência estática inicial; o banco em execução não foi consultado. Os casos abaixo precisam de correção ou confirmação na etapa de implementação. O PDF permanece a referência escolhida pelo usuário até nova orientação; não é preciso pedir o DDL original para continuar esta revisão.

## Critérios para a próxima revisão

Esta seção registra a metodologia escolhida depois da conferência inicial; não declara que o código atual já foi migrado. Para cada fluxo afetado, conferir nesta ordem:

1. **Model:** o setter filtra e valida a entrada, reutilizando métodos da `Utilitario` quando o contrato corresponder; o getter formata a saída. Registrar o valor recebido, armazenado, enviado ao banco e apresentado. Se o formato do getter for apenas para exibição, definir no roteiro como a DAO obterá o valor de gravação sem misturar os dois formatos.
2. **DAO:** os métodos CRUD que operam os dados da entidade recebem a model como parâmetro quando ela representa a operação. Conferir SQL, índices dos parâmetros, PK `BIGINT` fornecida pela DAO e valores efetivamente gravados ou lidos.
3. **Servlet:** atualizar as chamadas afetadas para montar a model por seus setters e passá-la à DAO; conferir como erros de validação e de banco chegam à resposta. Verificar a JSP apenas no ponto necessário para essa integração.

Registrar cada divergência com classe, método, ação e verificação no relatório Markdown correspondente. Alterações de código dependem da modalidade ou dos itens escolhidos pelo usuário.

| Prioridade | Campo/regra documentada | Evidência no código | Consequência provável |
| --- | --- | --- | --- |
| Alta | `material.Origem VARCHAR(10) NOT NULL`, sem `DEFAULT` | `MaterialDAO.cadastrarMaterial` insere apenas `id_material`, `nome`, `categoria` | O `INSERT` viola `NOT NULL` se o banco seguir o PDF. A grafia `Origem` requer confirmação do DDL. |
| Alta | `coleta.id_coleta BIGINT PK`, `id_cooperativa` e `tipo` obrigatórios | `ColetaDAO.cadastrarColeta` omite `id_coleta`, mas configura 8 parâmetros para 7 colunas; outros métodos inserem só origem ou peso | Erro de parâmetros ou de restrições obrigatórias. |
| Alta | `coleta.status IN ('AGENDADA','EM_ANDAMENTO','CONCLUIDA','CANCELADA')` | `ColetaDAO.finalizacaoAutomatica` e `encerramentoRastreamento` usam `StatusColeta.CONCLUIDA.name()`, mas vinculam o timestamp ao índice `3` e não vinculam o índice `2` nem o ID da coleta | O código do status está de acordo com o PDF; os parâmetros incompletos impedem o `UPDATE`. |
| Alta | `cooperativa.email_institucional`, `telefone_whatsapp`, `cep`, `endereco` são `NOT NULL` | `CooperativaDAO.cadastrarCooperativa` define `NULL` quando o filtro retorna `invalido` | Erro de restrição. Entrada inválida deveria ser barrada antes do SQL. |
| Alta | `cooperativa` tem 12 colunas no `INSERT` atual, placeholders começam em 1 | `CooperativaDAO.cadastrarCooperativa` chama `setLong(0, ...)` | Índice JDBC inválido. |
| Alta | `cooperado` referencia IDs `BIGINT` e suas colunas têm significado próprio | `CooperadoDAO.cadastrarCooperado` envia nome, usuário, senha e email aos primeiros placeholders de `cooperado`; `editarCooperado` usa índice `1` repetidamente | Tipos/colunas incompatíveis e parâmetros não vinculados. |
| Alta | `usuario.nome_usuario VARCHAR(50)`; `cooperado.id_usuario BIGINT` | `CooperadoDAO.listaCooperados` usa `getLong("nome_usuario")` e depois busca `id_usuario` sem selecioná-lo | Mapeamento de resultado incorreto. |
| Alta | `negociacao.id_negociacao`, `id_material`, `id_cooperativa`, `id_empresa` obrigatórios | `NegociacaoDAO.fazerContraproposta` faz `INSERT` em `negociacao` sem `id_negociacao` e `id_material`; o esquema tem tabela `proposta` | Falha por `NOT NULL`/PK e possível operação na tabela errada. |
| Média | `cooperado.cargo IN ('GESTOR','TESOUREIRO','MOTORISTA')` ou `NULL`; status em maiúsculas | As JSPs de cooperado atuais enviam códigos de cargo em maiúsculas; `CooperadoDAO` ainda precisa aplicar esses valores de forma consistente nas escritas | Os formulários de cargo foram alinhados ao PDF, mas a integração DAO ainda não foi validada. |
| Média | `coleta.data_agendada DATE`; `data_fim TIMESTAMP` | `ColetaDAO` constrói `java.sql.Date` com `getYear()` ou `getDayOfYear()` | Datas gravadas incorretamente ou erro de conversão. |
| Média | `coleta.id_rota`, `id_endereco_rota`, `id_cooperado_responsavel` são anuláveis | `ColetaDAO` usa `getLong()` para ler essas colunas sem `wasNull()` | `NULL` pode virar `0` no modelo. |
| Média | `cooperativa.status DEFAULT 'ATIVA'`, `coleta.status DEFAULT 'AGENDADA'`, `usuario.data_criacao DEFAULT CURRENT_TIMESTAMP` | Parte dos `INSERT`s omite campos e outros enviam valores explícitos ou `NULL` | Aplicar a regra do padrão: omitir somente quando se deseja o `DEFAULT`; documentar exceções. |
| Alta | Contratos Java entre controller e DAO | `CooperadoServlet` chama `buscarCooperadoPorId`, `listarPermissoes` e outras operações ausentes em `CooperadoDAO`; também usa outra assinatura de `cadastrarCooperado` | A compilação completa do projeto falha antes de qualquer teste de execução. A divergência já existe em `HEAD`. |
| Média | `material.categoria` aceita apenas `PAPEIS`, `PLASTICOS`, `METAIS`, `VIDROS` e `OUTROS` no `CHECK` documentado | `src/main/webapp/materiais.jsp` oferece `ELETRONICOS` no filtro e nos formulários de cadastro e edição (linhas 264, 326 e 353) | O cadastro ou a edição com essa opção tende a ser rejeitado pelo banco; a tela e o contrato de domínio precisam de decisão conjunta. |
| Alta | `negociacao.status` usa códigos maiúsculos e a conclusão exige `data_conclusao` | `NegociacaoDAO.alterarCargoCooperado` grava `aceita`; `enviarContraProposta` e `DecisaoDeOferta` filtram `em negociação`; `recusarProposta` e `CancelarNegociacao` executam `DELETE` | Atualizações podem afetar zero linhas ou violar o `CHECK`; exclusões apagam registros que os comentários dizem conservar em histórico. |

## Domínios ENUM no escopo atual

| Tabela.campo | Valores documentados no `CHECK` |
| --- | --- |
| `material.categoria` | `PAPEIS`, `PLASTICOS`, `METAIS`, `VIDROS`, `OUTROS` |
| `coleta.tipo` | `COLETA_EXTERNA`, `ENTREGA_COOPERATIVA` |
| `coleta.status` | `AGENDADA`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA` |
| `coleta.origem_entrega` | `EMPRESA`, `CONDOMINIO`, `ESCOLA`, `PESSOA_FISICA`, `ECOPONTO`, `OUTRO` |
| `cooperado.cargo` | `GESTOR`, `TESOUREIRO`, `MOTORISTA` (campo anulável) |
| `cooperado.status` | `ATIVO`, `AFASTADO`, `INATIVO` |
| `cooperativa.status` | `ATIVA`, `INATIVA` |
| `negociacao.status` | `AGUARDANDO_RESPOSTA`, `EM_NEGOCIACAO`, `ACEITA`, `RECUSADA`, `CANCELADA`, `CONCLUIDA` |
| `usuario.tipo_usuario` | `GESTOR_COOPERATIVA`, `COOPERADO`, `RESPONSAVEL_EMPRESA` (DAO de gestor usa `GESTOR_COOPERATIVA`) |

## Implementação inicial de ENUMs

Foram criados os nove ENUMs da tabela acima em `src/main/java/model/enums/`. Os modelos de `Material`, `Coleta`, `Cooperado`, `Cooperativa` e `Negociacao` armazenam esses domínios como ENUM, mantendo getters `String` para os consumidores atuais e adicionando getters tipados. Os setters `String` rejeitam valores fora do `CHECK` documentado por `IllegalArgumentException`. Os formulários de cooperado passaram a enviar códigos válidos do banco; `Cooperado` deixou de ser opção de cargo, representando ausência de cargo pelo valor vazio.

Esta implementação cobre apenas domínios finitos. Ela não corrige os problemas de placeholders, colunas obrigatórias, transações ou erros SQL registrados acima.

## Pendências de confirmação

- Obter o DDL efetivamente executado ou acesso somente leitura ao catálogo PostgreSQL para confirmar o PDF, inclusive a coluna `material.Origem`, geração dos IDs e possíveis diferenças de `DEFAULT`.
- Decidir de onde virá o valor obrigatório `material.Origem`, que ainda não está na tela/modelo atual; não inventar uma constante ou um ENUM sem regra de domínio.
- Confirmar com os responsáveis pelo produto se a retirada da opção `Cooperado` do seletor de cargo corresponde ao fluxo esperado; no contrato atual do PDF, cooperado sem cargo é `NULL`.
