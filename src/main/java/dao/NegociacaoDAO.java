package dao;

import model.Movimentacao;
import model.Negociacao;
import util.Conexao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NegociacaoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // Consulta base: a negociacao + nome/cnpj/cidade da cooperativa e da empresa + nome do material.
    // E usada na lista e na tela Ver. "ultima_atualizacao" e a data mais recente entre criacao, conclusao e movimentacoes.
    private static final String SQL_BASE =
            "SELECT n.id_negociacao, n.id_cooperativa, n.id_empresa, n.id_material, n.quantidade_kg, " +
                    "n.valor_kg_atual, n.valor_total_atual, n.status, n.data_criacao, n.data_conclusao, " +
                    "co.nome AS nome_cooperativa, co.cnpj AS cnpj_cooperativa, co.cidade AS cidade_cooperativa, co.estado AS estado_cooperativa, " +
                    "e.nome AS nome_empresa, e.cnpj AS cnpj_empresa, e.cidade AS cidade_empresa, e.estado AS estado_empresa, " +
                    "m.nome AS nome_material, " +
                    "GREATEST(n.data_criacao, n.data_conclusao, " +
                    "(SELECT MAX(p.data_proposta) FROM proposta p WHERE p.id_negociacao = n.id_negociacao)) AS ultima_atualizacao " +
                    "FROM negociacao n " +
                    "JOIN cooperativa co ON co.id_cooperativa = n.id_cooperativa " +
                    "JOIN empresa_recicladora e ON e.id_empresa = n.id_empresa " +
                    "JOIN material m ON m.id_material = n.id_material ";

    // CONSTRUTOR

    public NegociacaoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    /* Criar negociacao (tela Nova negociacao): grava a negociacao e a primeira movimentacao (a proposta inicial).
       Devolve o id da negociacao criada. */

    public long criarNegociacao(Negociacao negociacao, String autor, String observacoes) {

        String sql = "INSERT INTO negociacao(id_negociacao, id_cooperativa, id_empresa, id_material, quantidade_kg, " +
                "valor_kg_atual, valor_total_atual, status, data_criacao, data_conclusao) VALUES (?,?,?,?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        try {
            // os dois INSERTs valem juntos: se um falhar, nenhum e gravado
            conexao.setAutoCommit(false);

            // o banco nao gera o id sozinho, entao calculamos o proximo
            long idNegociacao = proximoId(conexao, "negociacao", "id_negociacao");

            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setLong(1, idNegociacao);
            pstm.setLong(2, negociacao.getIdCooperativa());
            pstm.setLong(3, negociacao.getIdEmpresa());
            pstm.setLong(4, negociacao.getIdMaterial());
            pstm.setBigDecimal(5, negociacao.getQuantidadeKg());
            pstm.setBigDecimal(6, negociacao.getValorKgAtual());
            pstm.setBigDecimal(7, calcularTotal(negociacao.getValorKgAtual(), negociacao.getQuantidadeKg()));
            pstm.setString(8, negociacao.getStatus());
            pstm.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
            if (negociacao.getDataConclusao() != null) {
                pstm.setTimestamp(10, Timestamp.valueOf(negociacao.getDataConclusao()));
            } else {
                pstm.setNull(10, Types.TIMESTAMP);
            }

            pstm.executeUpdate();

            inserirMovimentacao(conexao, idNegociacao, autor, negociacao.getValorKgAtual(),
                    negociacao.getQuantidadeKg(), observacoes);

            conexao.commit();

            return idNegociacao;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    /* Adicionar movimentacao (contraproposta): grava a movimentacao na linha do tempo e atualiza
       o valor e a quantidade atuais da negociacao. Devolve o id da movimentacao criada. */

    public long adicionarMovimentacao(long idNegociacao, String autor, BigDecimal valorKg,
                                      BigDecimal quantidadeKg, String observacoes) {

        String sql = "UPDATE negociacao SET valor_kg_atual = ?, quantidade_kg = ?, valor_total_atual = ? " +
                "WHERE id_negociacao = ?";

        Connection conexao = conn.conectar();

        try {
            conexao.setAutoCommit(false);

            long idProposta = inserirMovimentacao(conexao, idNegociacao, autor, valorKg, quantidadeKg, observacoes);

            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setBigDecimal(1, valorKg);
            pstm.setBigDecimal(2, quantidadeKg);
            pstm.setBigDecimal(3, calcularTotal(valorKg, quantidadeKg));
            pstm.setLong(4, idNegociacao);

            if (pstm.executeUpdate() == 0) {
                throw new SQLException("Negociação não encontrada.");
            }

            conexao.commit();

            return idProposta;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    /* Visualizar negociacoes (tela Lista). Se "status" vier preenchido, mostra so as desse status
       (novas propostas, em andamento, aceitas, recusadas, concluidas, canceladas). Vazio = todas. */

    public ArrayList<Negociacao> listarNegociacoes(String status) {

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        boolean filtrar = status != null && !status.trim().isEmpty();

        String sql = SQL_BASE + (filtrar ? "WHERE n.status = ? " : "") + "ORDER BY n.id_negociacao DESC";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            if (filtrar) {
                pstm.setString(1, status.trim());
            }

            ResultSet rst = pstm.executeQuery();

            while (rst.next()) {
                negociacoes.add(mapear(rst));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return negociacoes;
    }

    // Visualizar detalhes da negociacao (telas Ver e Editar). Devolve null se nao existir.

    public Negociacao buscarNegociacaoPorId(long idNegociacao) {

        Negociacao negociacao = null;

        String sql = SQL_BASE + "WHERE n.id_negociacao = ?";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            pstm.setLong(1, idNegociacao);

            ResultSet rst = pstm.executeQuery();

            if (rst.next()) {
                negociacao = mapear(rst);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return negociacao;
    }

    // Visualizar historico da negociacao (linha do tempo), da mais antiga para a mais nova.

    public ArrayList<Movimentacao> listarMovimentacoes(long idNegociacao) {

        ArrayList<Movimentacao> movimentacoes = new ArrayList<>();

        String sql = "SELECT id_proposta, id_negociacao, autor, valor_kg, quantidade_kg, observacoes, data_proposta " +
                "FROM proposta WHERE id_negociacao = ? ORDER BY data_proposta, id_proposta";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            pstm.setLong(1, idNegociacao);

            ResultSet rst = pstm.executeQuery();

            while (rst.next()) {
                Movimentacao movimentacao = new Movimentacao();

                movimentacao.setIdProposta(rst.getLong("id_proposta"));
                movimentacao.setIdNegociacao(rst.getLong("id_negociacao"));
                movimentacao.setAutor(rst.getString("autor"));
                movimentacao.setValorKg(rst.getBigDecimal("valor_kg"));
                movimentacao.setQuantidadeKg(rst.getBigDecimal("quantidade_kg"));
                movimentacao.setObservacoes(rst.getString("observacoes"));

                Timestamp data = rst.getTimestamp("data_proposta");
                if (data != null) {
                    movimentacao.setDataProposta(data.toLocalDateTime());
                }

                // a tabela nao guarda o tipo: a primeira e a Proposta e as seguintes sao Contrapropostas
                movimentacao.setTipo(movimentacoes.isEmpty() ? "Proposta" : "Contraproposta");

                movimentacoes.add(movimentacao);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return movimentacoes;
    }

    // Cooperativas, empresas e materiais (id -> nome), para montar os selects dos formularios.

    public LinkedHashMap<Long, String> listarCooperativas() {
        return listarIdNome("SELECT id_cooperativa, nome FROM cooperativa ORDER BY nome", "id_cooperativa");
    }

    public LinkedHashMap<Long, String> listarEmpresas() {
        return listarIdNome("SELECT id_empresa, nome FROM empresa_recicladora ORDER BY nome", "id_empresa");
    }

    public LinkedHashMap<Long, String> listarMateriais() {
        return listarIdNome("SELECT id_material, nome FROM material ORDER BY nome", "id_material");
    }

    /* Valores aceitos por uma coluna com regra CHECK (status, autor...), lidos do proprio banco.
       Se a regra nao for encontrada, usa os valores padrao informados. */

    public ArrayList<String> listarValoresPermitidos(String tabela, String coluna, String... padrao) {

        ArrayList<String> valores = new ArrayList<>();

        String sql = "SELECT pg_get_constraintdef(oid) FROM pg_constraint " +
                "WHERE conrelid = CAST(? AS regclass) AND contype = 'c' AND pg_get_constraintdef(oid) ILIKE ?";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            pstm.setString(1, tabela);
            pstm.setString(2, "%" + coluna + "%");

            ResultSet rst = pstm.executeQuery();

            while (rst.next() && valores.isEmpty()) {
                // pega tudo que esta entre aspas simples na definicao da regra
                Matcher m = Pattern.compile("'([^']*)'").matcher(rst.getString(1));

                while (m.find()) {
                    if (!valores.contains(m.group(1))) {
                        valores.add(m.group(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        if (valores.isEmpty()) {
            Collections.addAll(valores, padrao);
        }

        return valores;
    }

    // Quantas movimentacoes a negociacao tem (aparece no modal de excluir).

    public int contarMovimentacoes(long idNegociacao) {

        Connection conexao = conn.conectar();

        try {
            return contar(conexao, "SELECT COUNT(*) FROM proposta WHERE id_negociacao = ?", idNegociacao);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Se a negociacao ja virou venda (aparece no modal de excluir: "Venda concluida: Sim/Nao").

    public boolean possuiVenda(long idNegociacao) {

        Connection conexao = conn.conectar();

        try {
            return contar(conexao, "SELECT COUNT(*) FROM venda WHERE id_negociacao = ?", idNegociacao) > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    /* Editar negociacao (tela Editar, bloco Resumo): material, quantidade, valor e status.
       finalizada = true marca a data de conclusao (se ainda nao tinha); false limpa a data.
       Se "autor" vier preenchido (o status foi encerrado agora), registra isso na linha do tempo. */

    public int atualizarResumo(Negociacao negociacao, boolean finalizada, String autor, String observacoes) {

        String sql = "UPDATE negociacao SET id_material = ?, quantidade_kg = ?, valor_kg_atual = ?, " +
                "valor_total_atual = ?, status = ?, " +
                "data_conclusao = CASE WHEN ? THEN COALESCE(data_conclusao, CURRENT_TIMESTAMP) ELSE NULL END " +
                "WHERE id_negociacao = ?";

        Connection conexao = conn.conectar();

        try {
            conexao.setAutoCommit(false);

            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setLong(1, negociacao.getIdMaterial());
            pstm.setBigDecimal(2, negociacao.getQuantidadeKg());
            pstm.setBigDecimal(3, negociacao.getValorKgAtual());
            pstm.setBigDecimal(4, calcularTotal(negociacao.getValorKgAtual(), negociacao.getQuantidadeKg()));
            pstm.setString(5, negociacao.getStatus());
            pstm.setBoolean(6, finalizada);
            pstm.setLong(7, negociacao.getIdNegociacao());

            int linhas = pstm.executeUpdate();

            if (linhas > 0 && autor != null) {
                inserirMovimentacao(conexao, negociacao.getIdNegociacao(), autor,
                        negociacao.getValorKgAtual(), negociacao.getQuantidadeKg(), observacoes);
            }

            conexao.commit();

            return linhas;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    /* Aceitar, recusar, cancelar ou concluir: muda o status (nao apaga nada, a negociacao vai para o historico)
       e registra o encerramento na linha do tempo, com o valor e a quantidade atuais da negociacao.
       finalizada = true marca a data de conclusao. Se "autor" for null, so muda o status. */

    public int alterarStatus(long idNegociacao, String status, boolean finalizada, String autor, String observacoes) {

        String sql = "UPDATE negociacao SET status = ?, " +
                "data_conclusao = CASE WHEN ? THEN COALESCE(data_conclusao, CURRENT_TIMESTAMP) ELSE NULL END " +
                "WHERE id_negociacao = ?";

        Connection conexao = conn.conectar();

        try {
            conexao.setAutoCommit(false);

            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setString(1, status);
            pstm.setBoolean(2, finalizada);
            pstm.setLong(3, idNegociacao);

            int linhas = pstm.executeUpdate();

            if (linhas > 0 && autor != null) {
                PreparedStatement pstAtual = conexao.prepareStatement(
                        "SELECT valor_kg_atual, quantidade_kg FROM negociacao WHERE id_negociacao = ?");
                pstAtual.setLong(1, idNegociacao);

                ResultSet rst = pstAtual.executeQuery();

                if (rst.next()) {
                    inserirMovimentacao(conexao, idNegociacao, autor,
                            rst.getBigDecimal("valor_kg_atual"), rst.getBigDecimal("quantidade_kg"), observacoes);
                }
            }

            conexao.commit();

            return linhas;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    /* Excluir permanentemente: apaga as movimentacoes e a negociacao.
       Se a negociacao ja virou venda, a venda continua registrada: ela so perde o vinculo (id_negociacao fica vazio).
       Devolve: 0 = nao existe | 1 = excluida. */

    public int excluirNegociacao(long idNegociacao) {

        Connection conexao = conn.conectar();

        try {
            if (contar(conexao, "SELECT COUNT(*) FROM negociacao WHERE id_negociacao = ?", idNegociacao) == 0) {
                return 0;
            }

            conexao.setAutoCommit(false);

            PreparedStatement pst0 = conexao.prepareStatement("UPDATE venda SET id_negociacao = NULL WHERE id_negociacao = ?");
            pst0.setLong(1, idNegociacao);
            pst0.executeUpdate();

            PreparedStatement pst1 = conexao.prepareStatement("DELETE FROM proposta WHERE id_negociacao = ?");
            pst1.setLong(1, idNegociacao);
            pst1.executeUpdate();

            PreparedStatement pst2 = conexao.prepareStatement("DELETE FROM negociacao WHERE id_negociacao = ?");
            pst2.setLong(1, idNegociacao);
            pst2.executeUpdate();

            conexao.commit();

            return 1;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS AUXILIARES (privados, so esta classe usa) ===========================================================

    // Transforma uma linha do banco em um objeto Negociacao
    private Negociacao mapear(ResultSet rst) throws SQLException {

        Negociacao negociacao = new Negociacao();

        negociacao.setIdNegociacao(rst.getLong("id_negociacao"));
        negociacao.setIdCooperativa(rst.getLong("id_cooperativa"));
        negociacao.setIdEmpresa(rst.getLong("id_empresa"));
        negociacao.setIdMaterial(rst.getLong("id_material"));
        negociacao.setQuantidadeKg(rst.getBigDecimal("quantidade_kg"));
        negociacao.setValorKgAtual(rst.getBigDecimal("valor_kg_atual"));
        negociacao.setValorTotalAtual(rst.getBigDecimal("valor_total_atual"));
        negociacao.setStatus(rst.getString("status"));

        negociacao.setNomeCooperativa(rst.getString("nome_cooperativa"));
        negociacao.setCnpjCooperativa(rst.getString("cnpj_cooperativa"));
        negociacao.setCidadeCooperativa(rst.getString("cidade_cooperativa"));
        negociacao.setEstadoCooperativa(rst.getString("estado_cooperativa"));
        negociacao.setNomeEmpresa(rst.getString("nome_empresa"));
        negociacao.setCnpjEmpresa(rst.getString("cnpj_empresa"));
        negociacao.setCidadeEmpresa(rst.getString("cidade_empresa"));
        negociacao.setEstadoEmpresa(rst.getString("estado_empresa"));
        negociacao.setNomeMaterial(rst.getString("nome_material"));

        // as datas podem vir vazias (ex.: data_conclusao de uma negociacao ainda aberta)
        Timestamp criacao = rst.getTimestamp("data_criacao");
        if (criacao != null) {
            negociacao.setDataCriacao(criacao.toLocalDateTime());
        }

        Timestamp conclusao = rst.getTimestamp("data_conclusao");
        if (conclusao != null) {
            negociacao.setDataConclusao(conclusao.toLocalDateTime());
        }

        Timestamp atualizacao = rst.getTimestamp("ultima_atualizacao");
        if (atualizacao != null) {
            negociacao.setUltimaAtualizacao(atualizacao.toLocalDateTime());
        }

        return negociacao;
    }

    // Grava uma linha na tabela proposta (uma movimentacao da linha do tempo) e devolve o id dela
    private long inserirMovimentacao(Connection conexao, long idNegociacao, String autor, BigDecimal valorKg,
                                     BigDecimal quantidadeKg, String observacoes) throws SQLException {

        long idProposta = proximoId(conexao, "proposta", "id_proposta");

        PreparedStatement pstm = conexao.prepareStatement(
                "INSERT INTO proposta(id_proposta, id_negociacao, autor, valor_kg, quantidade_kg, observacoes, data_proposta) " +
                        "VALUES (?,?,?,?,?,?,?)");

        pstm.setLong(1, idProposta);
        pstm.setLong(2, idNegociacao);
        pstm.setString(3, autor);
        pstm.setBigDecimal(4, valorKg);
        pstm.setBigDecimal(5, quantidadeKg);
        if (observacoes == null || observacoes.trim().isEmpty()) {
            pstm.setNull(6, Types.VARCHAR);
        } else {
            pstm.setString(6, observacoes.trim());
        }
        pstm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

        pstm.executeUpdate();

        return idProposta;
    }

    // Proximo id de uma tabela: maior id que existe + 1 (usado porque o banco nao gera sozinho)
    private long proximoId(Connection conexao, String tabela, String coluna) throws SQLException {

        PreparedStatement pstm = conexao.prepareStatement("SELECT COALESCE(MAX(" + coluna + "), 0) + 1 FROM " + tabela);
        ResultSet rst = pstm.executeQuery();
        rst.next();

        return rst.getLong(1);
    }

    // Valor total = valor por kg x quantidade (2 casas decimais)
    private BigDecimal calcularTotal(BigDecimal valorKg, BigDecimal quantidadeKg) {
        return valorKg.multiply(quantidadeKg).setScale(2, RoundingMode.HALF_UP);
    }

    // Le uma consulta com duas colunas (id e nome) e devolve um mapa id -> nome
    private LinkedHashMap<Long, String> listarIdNome(String sql, String colunaId) {

        LinkedHashMap<Long, String> mapa = new LinkedHashMap<>();

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            while (rst.next()) {
                mapa.put(rst.getLong(colunaId), rst.getString("nome"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return mapa;
    }

    // executa um SELECT COUNT(*) com um unico parametro e devolve o numero
    private int contar(Connection conexao, String sql, long id) throws SQLException {

        PreparedStatement pstm = conexao.prepareStatement(sql);
        pstm.setLong(1, id);

        ResultSet rst = pstm.executeQuery();
        rst.next();

        return rst.getInt(1);
    }

    private void desfazer(Connection conexao) {
        try {
            conexao.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}