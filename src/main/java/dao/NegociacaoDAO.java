package dao;

import model.auxiliares.Proposta;
import model.principais.Negociacao;
import util.ConexaoBD;
import util.Utilitario;

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

    private ConexaoBD conn = new ConexaoBD();
    private Utilitario utilitario = new Utilitario();

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

    // Cria a negociacao e a primeira proposta; devolve o total de linhas gravadas.

    public int criarNegociacao(Negociacao negociacao, Proposta proposta) {

        String sql = "INSERT INTO negociacao(id_negociacao, id_cooperativa, id_empresa, id_material, quantidade_kg, " +
                "valor_kg_atual, valor_total_atual, status, data_criacao, data_conclusao) VALUES (?,?,?,?,?,?,?,?,?,?)";

        long idNegociacao = proximoId("negociacao");
        Connection conexao = conn.conectar();

        try {
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

            int linhas = pstm.executeUpdate();
            if (linhas == 0) {
                return 0;
            }
            negociacao.setIdNegociacao(idNegociacao);
            proposta.setIdNegociacao(idNegociacao);
            int propostas = inserirMovimentacao(conexao, proposta);
            if (propostas == 0) {
                throw new SQLException("Proposta inicial não gravada.");
            }
            linhas += propostas;
            return linhas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Adiciona uma proposta e atualiza o resumo da negociacao.

    public int adicionarMovimentacao(Proposta proposta) {

        String sql = "UPDATE negociacao SET valor_kg_atual = ?, quantidade_kg = ?, valor_total_atual = ? " +
                "WHERE id_negociacao = ?";

        Connection conexao = conn.conectar();

        try {
            int linhas = inserirMovimentacao(conexao, proposta);
            if (linhas == 0) {
                throw new SQLException("Movimentação não gravada.");
            }

            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setBigDecimal(1, proposta.getValorKg());
            pstm.setBigDecimal(2, proposta.getQuantidadeKg());
            pstm.setBigDecimal(3, calcularTotal(proposta.getValorKg(), proposta.getQuantidadeKg()));
            pstm.setLong(4, proposta.getIdNegociacao());

            int atualizadas = pstm.executeUpdate();
            if (atualizadas == 0) {
                throw new SQLException("Negociação não encontrada.");
            }

            return linhas + atualizadas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    // Lista as negociacoes; filtra pelo status quando ele e informado.

    public ArrayList<Negociacao> listarNegociacoes(String status) {

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        boolean filtrar = status != null && !status.trim().isEmpty();

        String sql = SQL_BASE;
        if (filtrar) {
            sql += "WHERE n.status = ? ";
        }
        sql += "ORDER BY n.id_negociacao DESC";

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

    public Negociacao buscarNegociacaoPorId(Negociacao filtro) {

        Negociacao negociacao = null;

        String sql = SQL_BASE + "WHERE n.id_negociacao = ?";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            pstm.setLong(1, filtro.getIdNegociacao());

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

    public ArrayList<Proposta> listarMovimentacoes(Negociacao negociacao) {

        ArrayList<Proposta> movimentacoes = new ArrayList<>();

        String sql = "SELECT id_proposta, id_negociacao, autor, valor_kg, quantidade_kg, observacoes, data_proposta " +
                "FROM proposta WHERE id_negociacao = ? ORDER BY data_proposta, id_proposta";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            pstm.setLong(1, negociacao.getIdNegociacao());

            ResultSet rst = pstm.executeQuery();

            while (rst.next()) {
                Proposta proposta = new Proposta();

                proposta.setIdProposta(rst.getLong("id_proposta"));
                proposta.setIdNegociacao(rst.getLong("id_negociacao"));
                proposta.setAutor(rst.getString("autor"));
                proposta.setValorKg(rst.getBigDecimal("valor_kg"));
                proposta.setQuantidadeKg(rst.getBigDecimal("quantidade_kg"));
                proposta.setObservacoes(rst.getString("observacoes"));

                Timestamp data = rst.getTimestamp("data_proposta");
                if (data != null) {
                    proposta.setDataProposta(data.toLocalDateTime());
                }

                // a tabela nao guarda o tipo: a primeira e a Proposta e as seguintes sao Contrapropostas
                if (movimentacoes.isEmpty()) {
                    proposta.setTipo("Proposta");
                } else {
                    proposta.setTipo("Contraproposta");
                }

                movimentacoes.add(proposta);
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

    public int contarMovimentacoes(Negociacao negociacao) {

        Connection conexao = conn.conectar();

        try {
            return contar(conexao, "SELECT COUNT(*) FROM proposta WHERE id_negociacao = ?", negociacao.getIdNegociacao());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Se a negociacao ja virou venda (aparece no modal de excluir: "Venda concluida: Sim/Nao").

    public boolean possuiVenda(Negociacao negociacao) {

        Connection conexao = conn.conectar();

        try {
            return contar(conexao, "SELECT COUNT(*) FROM venda WHERE id_negociacao = ?", negociacao.getIdNegociacao()) > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza o resumo e registra uma proposta de encerramento quando informada.

    public int atualizarResumo(Negociacao negociacao, Proposta proposta) {

        String sql = "UPDATE negociacao SET id_material = ?, quantidade_kg = ?, valor_kg_atual = ?, " +
                "valor_total_atual = ?, status = ?, " +
                "data_conclusao = CASE WHEN ? THEN COALESCE(data_conclusao, CURRENT_TIMESTAMP) ELSE NULL END " +
                "WHERE id_negociacao = ?";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setLong(1, negociacao.getIdMaterial());
            pstm.setBigDecimal(2, negociacao.getQuantidadeKg());
            pstm.setBigDecimal(3, negociacao.getValorKgAtual());
            pstm.setBigDecimal(4, calcularTotal(negociacao.getValorKgAtual(), negociacao.getQuantidadeKg()));
            pstm.setString(5, negociacao.getStatus());
            pstm.setBoolean(6, negociacao.getDataConclusao() != null);
            pstm.setLong(7, negociacao.getIdNegociacao());

            int linhas = pstm.executeUpdate();

            if (linhas > 0 && proposta != null) {
                int propostas = inserirMovimentacao(conexao, proposta);
                if (propostas == 0) {
                    throw new SQLException("Movimentação não gravada.");
                }
                linhas += propostas;
            }

            return linhas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o status e registra uma proposta de encerramento quando informada.

    public int alterarStatus(Negociacao negociacao, Proposta proposta) {

        String sql = "UPDATE negociacao SET status = ?, " +
                "data_conclusao = CASE WHEN ? THEN COALESCE(data_conclusao, CURRENT_TIMESTAMP) ELSE NULL END " +
                "WHERE id_negociacao = ?";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setString(1, negociacao.getStatus());
            pstm.setBoolean(2, negociacao.getDataConclusao() != null);
            pstm.setLong(3, negociacao.getIdNegociacao());

            int linhas = pstm.executeUpdate();

            if (linhas > 0 && proposta != null) {
                int propostas = inserirMovimentacao(conexao, proposta);
                if (propostas == 0) {
                    throw new SQLException("Movimentação não gravada.");
                }
                linhas += propostas;
            }

            return linhas;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui as propostas e a negociacao, desvinculando as vendas existentes.

    public int excluirNegociacao(Negociacao negociacao) {

        Connection conexao = conn.conectar();

        try {
            if (contar(conexao, "SELECT COUNT(*) FROM negociacao WHERE id_negociacao = ?", negociacao.getIdNegociacao()) == 0) {
                return 0;
            }

            PreparedStatement pst0 = conexao.prepareStatement("UPDATE venda SET id_negociacao = NULL WHERE id_negociacao = ?");
            pst0.setLong(1, negociacao.getIdNegociacao());
            int linhas = pst0.executeUpdate();

            PreparedStatement pst1 = conexao.prepareStatement("DELETE FROM proposta WHERE id_negociacao = ?");
            pst1.setLong(1, negociacao.getIdNegociacao());
            linhas += pst1.executeUpdate();

            PreparedStatement pst2 = conexao.prepareStatement("DELETE FROM negociacao WHERE id_negociacao = ?");
            pst2.setLong(1, negociacao.getIdNegociacao());
            linhas += pst2.executeUpdate();
            return linhas;

        } catch (SQLException e) {
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

    // Grava uma linha na tabela proposta e preenche o ID gerado no objeto.
    private int inserirMovimentacao(Connection conexao, Proposta proposta) throws SQLException {

        long idProposta = proximoId("proposta");

        PreparedStatement pstm = conexao.prepareStatement(
                "INSERT INTO proposta(id_proposta, id_negociacao, autor, valor_kg, quantidade_kg, observacoes, data_proposta) " +
                        "VALUES (?,?,?,?,?,?,?)");

        pstm.setLong(1, idProposta);
        pstm.setLong(2, proposta.getIdNegociacao());
        pstm.setString(3, proposta.getAutor());
        pstm.setBigDecimal(4, proposta.getValorKg());
        pstm.setBigDecimal(5, proposta.getQuantidadeKg());
        if (proposta.getObservacoes() == null || proposta.getObservacoes().trim().isEmpty()) {
            pstm.setNull(6, Types.VARCHAR);
        } else {
            pstm.setString(6, proposta.getObservacoes().trim());
        }
        pstm.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

        int linhas = pstm.executeUpdate();
        if (linhas > 0) {
            proposta.setIdProposta(idProposta);
        }
        return linhas;
    }

    // Usa a consulta de ultimo ID compartilhada pelo projeto.
    private long proximoId(String tabela) {
        long ultimoId = utilitario.selecionarUltimoId(tabela);
        if (ultimoId == -1) {
            return 1;
        }
        return ultimoId + 1;
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

}
