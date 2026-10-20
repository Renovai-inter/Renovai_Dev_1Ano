package dao;

import model.Negociacao;
import model.enums.StatusNegociacao;
import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class NegociacaoDAO {
    private final Conexao conn = new Conexao();

    // Inicializa o acesso aos dados de negociação.
    public NegociacaoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Registra uma proposta da cooperativa para a negociação informada.
    public int fazerContraproposta(Negociacao negociacao) {
        if (negociacao.getIdNegociacao() == null || negociacao.getValorKgAtual() == null) {
            throw new IllegalArgumentException("ID da negociação e valor por quilo são obrigatórios.");
        }
        String sqlId = "SELECT COALESCE(MAX(id_proposta), 0) + 1 FROM proposta";
        String sql = "INSERT INTO proposta (id_proposta, id_negociacao, autor, valor_kg, " +
                "quantidade_kg) VALUES (?, ?, 'COOPERATIVA', ?, ?)";
        Connection conexao = conn.conectar();
        try {
            long idProposta;
            try (PreparedStatement pstmt = conexao.prepareStatement(sqlId);
                 ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                idProposta = rs.getLong(1);
            }
            try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                pstmt.setLong(1, idProposta);
                pstmt.setLong(2, negociacao.getIdNegociacao());
                pstmt.setBigDecimal(3, negociacao.getValorKgAtual());
                pstmt.setBigDecimal(4, negociacao.getQuantidadeKg());
                return pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao registrar contraproposta da cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    // Lista propostas que aguardam resposta.
    public ArrayList<Negociacao> novasPropostas() {
        return consultarNegociacoes(" WHERE status = 'AGUARDANDO_RESPOSTA'");
    }

    // Lista os dados da negociação vinculada a uma empresa.
    public ArrayList<Negociacao> analisarDetalhesProposta() {
        return consultarNegociacoes(" WHERE id_empresa IS NOT NULL");
    }

    // Lista negociações em andamento.
    public ArrayList<Negociacao> negociacaoEmAndamento() {
        return consultarNegociacoes(" WHERE status = 'EM_NEGOCIACAO'");
    }

    // Lista negociações encerradas para consulta do histórico.
    public ArrayList<Negociacao> historicoNegociacao() {
        return consultarNegociacoes(" WHERE status IN ('ACEITA', 'RECUSADA', 'CANCELADA', 'CONCLUIDA')");
    }

    // Lista as propostas aceitas.
    public ArrayList<Negociacao> propostasAceitas() {
        return consultarNegociacoes(" WHERE status = 'ACEITA'");
    }

    // Lista todas as negociações, inclusive as finalizadas.
    public ArrayList<Negociacao> historicoGeral() {
        return consultarNegociacoes("");
    }

    // Executa uma consulta e monta uma Model para cada linha.
    private ArrayList<Negociacao> consultarNegociacoes(String filtro) {
        String sql = "SELECT id_negociacao, id_cooperativa, id_empresa, id_material, " +
                "quantidade_kg, valor_kg_atual, valor_total_atual, status, data_criacao, " +
                "data_conclusao FROM negociacao" + filtro;
        Connection conexao = conn.conectar();
        ArrayList<Negociacao> negociacoes = new ArrayList<>();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Negociacao negociacao = new Negociacao();
                negociacao.setIdNegociacao(rs.getLong("id_negociacao"));
                negociacao.setIdCooperativa(rs.getLong("id_cooperativa"));
                negociacao.setIdEmpresa(rs.getLong("id_empresa"));
                negociacao.setIdMaterial(rs.getLong("id_material"));
                negociacao.setQuantidadeKg(rs.getBigDecimal("quantidade_kg"));
                negociacao.setValorKgAtual(rs.getBigDecimal("valor_kg_atual"));
                negociacao.setValorTotalAtual(rs.getBigDecimal("valor_total_atual"));
                negociacao.setStatus(StatusNegociacao.valueOf(rs.getString("status")));
                Timestamp criacao = rs.getTimestamp("data_criacao");
                Timestamp conclusao = rs.getTimestamp("data_conclusao");
                negociacao.setDataCriacao(criacao == null ? null : criacao.toLocalDateTime());
                negociacao.setDataConclusao(conclusao == null ? null : conclusao.toLocalDateTime());
                negociacoes.add(negociacao);
            }
            return negociacoes;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao consultar negociações.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Aceita a proposta identificada e registra sua conclusão.
    public int alterarCargoCooperado(Negociacao negociacao) {
        return atualizarStatusFinal(negociacao, StatusNegociacao.ACEITA);
    }

    // Atualiza o valor de uma negociação em andamento.
    public int enviarContraProposta(Negociacao negociacao) {
        if (negociacao.getIdNegociacao() == null || negociacao.getValorKgAtual() == null) {
            throw new IllegalArgumentException("ID e valor por quilo são obrigatórios.");
        }
        String sql = "UPDATE negociacao SET valor_kg_atual = ? " +
                "WHERE id_negociacao = ? AND status = 'EM_NEGOCIACAO'";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, negociacao.getValorKgAtual());
            pstmt.setLong(2, negociacao.getIdNegociacao());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar contraproposta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra a decisão sobre uma oferta em negociação.
    public int DecisaoDeOferta(Negociacao negociacao) {
        if (negociacao.getStatus() == null) {
            throw new IllegalArgumentException("Status é obrigatório.");
        }
        if (negociacao.getStatus() == StatusNegociacao.ACEITA ||
                negociacao.getStatus() == StatusNegociacao.RECUSADA ||
                negociacao.getStatus() == StatusNegociacao.CANCELADA ||
                negociacao.getStatus() == StatusNegociacao.CONCLUIDA) {
            return atualizarStatusFinal(negociacao, negociacao.getStatus());
        }
        if (negociacao.getIdNegociacao() == null) {
            throw new IllegalArgumentException("idNegociacao é obrigatório.");
        }
        String sql = "UPDATE negociacao SET status = ? WHERE id_negociacao = ? " +
                "AND status = 'EM_NEGOCIACAO'";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, negociacao.getStatus().name());
            pstmt.setLong(2, negociacao.getIdNegociacao());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao registrar decisão da oferta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Recusa a proposta preservando seu histórico.
    public int recusarProposta(Negociacao negociacao) {
        return atualizarStatusFinal(negociacao, StatusNegociacao.RECUSADA);
    }

    // Cancela a negociação preservando seu histórico.
    public int CancelarNegociacao(Negociacao negociacao) {
        return atualizarStatusFinal(negociacao, StatusNegociacao.CANCELADA);
    }

    // Atualiza um status final e sua data obrigatória de conclusão.
    private int atualizarStatusFinal(Negociacao negociacao, StatusNegociacao status) {
        if (negociacao.getIdNegociacao() == null || negociacao.getDataConclusao() == null) {
            throw new IllegalArgumentException("ID e data de conclusão são obrigatórios.");
        }
        String sql = "UPDATE negociacao SET status = ?, data_conclusao = ? WHERE id_negociacao = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setTimestamp(2, Timestamp.valueOf(negociacao.getDataConclusao()));
            pstmt.setLong(3, negociacao.getIdNegociacao());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                negociacao.setStatus(status);
            }
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar o status da negociação.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }
}
