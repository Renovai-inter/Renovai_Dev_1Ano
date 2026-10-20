
package dao;

import model.Coleta;
import util.Conexao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ColetaDAO {

    public Long getUltimoIdColeta() {
        String sql = "SELECT COALESCE(MAX(id_coleta), 0) FROM coleta";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar ID da coleta.", e);
        }

        return 0L;
    }

    public boolean cadastrarColeta(
            Coleta coleta,
            Map<Long, BigDecimal> materiais) {

        String sqlColeta = """
            INSERT INTO coleta (
                id_coleta, id_cooperativa, tipo, status, id_rota,
                id_endereco_rota, id_cooperado_responsavel,
                origem_entrega, nome_local_origem, data_agendada,
                observacoes, peso_total_kg
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        String sqlMaterial = """
            INSERT INTO coleta_material (id_coleta, id_material, quantidade_kg)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = Conexao.conectar()) {
            conn.setAutoCommit(false);

            try {
                long idColeta;

                String sqlId = "SELECT COALESCE(MAX(id_coleta), 0) + 1 FROM coleta";
                try (PreparedStatement stmtId = conn.prepareStatement(sqlId);
                     ResultSet rs = stmtId.executeQuery()) {
                    rs.next();
                    idColeta = rs.getLong(1);
                }

                BigDecimal pesoTotal = BigDecimal.ZERO;
                if (materiais != null) {
                    for (BigDecimal quantidade : materiais.values()) {
                        if (quantidade != null) {
                            pesoTotal = pesoTotal.add(quantidade);
                        }
                    }
                }

                try (PreparedStatement stmt = conn.prepareStatement(sqlColeta)) {
                    stmt.setLong(1, idColeta);
                    stmt.setLong(2, coleta.getIdCooperativa());
                    stmt.setString(3, coleta.getTipo().name());
                    stmt.setString(4, "AGENDADA");
                    setLongOuNull(stmt, 5, coleta.getIdRota());
                    setLongOuNull(stmt, 6, coleta.getIdEnderecoRota());
                    setLongOuNull(stmt, 7, coleta.getIdCooperadoResponsavel());
                    stmt.setString(8, coleta.getOrigemEntrega());
                    stmt.setString(9, coleta.getNomeLocalOrigem());

                    if (coleta.getDataAgendada() != null) {
                        stmt.setDate(10, new java.sql.Date(
                                coleta.getDataAgendada().getTime()));
                    } else {
                        stmt.setNull(10, Types.DATE);
                    }

                    stmt.setString(11, coleta.getObservacoes());

                    if (materiais != null && !materiais.isEmpty()) {
                        stmt.setBigDecimal(12, pesoTotal);
                    } else {
                        stmt.setBigDecimal(12, BigDecimal.ZERO);
                    }

                    stmt.executeUpdate();
                }

                if (materiais != null && !materiais.isEmpty()) {
                    try (PreparedStatement stmtMaterial =
                                 conn.prepareStatement(sqlMaterial)) {
                        for (Map.Entry<Long, BigDecimal> item : materiais.entrySet()) {
                            if (item.getKey() == null ||
                                    item.getValue() == null ||
                                    item.getValue().compareTo(BigDecimal.ZERO) < 0) {
                                throw new SQLException("Material ou quantidade inválida.");
                            }

                            stmtMaterial.setLong(1, idColeta);
                            stmtMaterial.setLong(2, item.getKey());
                            stmtMaterial.setBigDecimal(3, item.getValue());
                            stmtMaterial.addBatch();
                        }

                        stmtMaterial.executeBatch();
                    }
                }

                conn.commit();
                coleta.setIdColeta(idColeta);
                return true;

            } catch (Exception e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao cadastrar coleta e materiais.", e);
        }
    }

    public List<Coleta> listarColetas() {
        List<Coleta> lista = new ArrayList<>();

        String sql = """
            SELECT id_coleta, id_cooperativa, tipo, status, id_rota,
                   id_endereco_rota, id_cooperado_responsavel,
                   origem_entrega, nome_local_origem, data_agendada,
                   data_inicio, data_fim, peso_total_kg, observacoes
            FROM coleta
            ORDER BY id_coleta DESC
            """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearColeta(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar coletas.", e);
        }

        return lista;
    }

    public Coleta buscarPorId(Long idColeta) {
        String sql = "SELECT * FROM coleta WHERE id_coleta = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idColeta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearColeta(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar coleta.", e);
        }

        return null;
    }

    public Map<Long, BigDecimal> listarMateriaisDaColeta(Long idColeta) {
        Map<Long, BigDecimal> materiais = new LinkedHashMap<>();

        String sql = """
            SELECT id_material, quantidade_kg
            FROM coleta_material
            WHERE id_coleta = ?
            ORDER BY id_material
            """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idColeta);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    materiais.put(
                            rs.getLong("id_material"),
                            rs.getBigDecimal("quantidade_kg")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar materiais da coleta.", e);
        }

        return materiais;
    }

    public boolean atualizarStatus(Long idColeta, String status) {
        String sql;

        if ("EM_ANDAMENTO".equals(status)) {
            sql = """
                UPDATE coleta
                SET status = ?, data_inicio = CURRENT_TIMESTAMP
                WHERE id_coleta = ? AND status = 'AGENDADA'
                """;
        } else if ("CONCLUIDA".equals(status)) {
            sql = """
                UPDATE coleta
                SET status = ?, data_fim = CURRENT_TIMESTAMP
                WHERE id_coleta = ? AND status = 'EM_ANDAMENTO'
                """;
        } else if ("CANCELADA".equals(status)) {
            sql = """
                UPDATE coleta
                SET status = ?
                WHERE id_coleta = ? AND status IN ('AGENDADA', 'EM_ANDAMENTO')
                """;
        } else {
            throw new IllegalArgumentException("Status inválido.");
        }

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setLong(2, idColeta);
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status da coleta.", e);
        }
    }

    public boolean excluirColeta(Long idColeta) {
        String sql = "DELETE FROM coleta WHERE id_coleta = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idColeta);
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir coleta.", e);
        }
    }

    private Coleta mapearColeta(ResultSet rs) throws SQLException {
        Coleta coleta = new Coleta();

        coleta.setIdColeta(rs.getLong("id_coleta"));
        coleta.setIdCooperativa(rs.getLong("id_cooperativa"));
        coleta.setTipo(model.enums.TipoColeta.valueOf(rs.getString("tipo")));
        coleta.setStatus(model.enums.StatusColeta.valueOf(rs.getString("status")));

        coleta.setIdRota(getLongOuNull(rs, "id_rota"));
        coleta.setIdEnderecoRota(getLongOuNull(rs, "id_endereco_rota"));
        coleta.setIdCooperadoResponsavel(
                getLongOuNull(rs, "id_cooperado_responsavel"));

        coleta.setOrigemEntrega(rs.getString("origem_entrega"));
        coleta.setNomeLocalOrigem(rs.getString("nome_local_origem"));
        coleta.setDataAgendada(rs.getDate("data_agendada"));
        Timestamp inicio = rs.getTimestamp("data_inicio");

        if (inicio != null) {
            coleta.setDataInicio(inicio.toLocalDateTime());
        }

        Timestamp fim = rs.getTimestamp("data_fim");
        if (fim != null) {
            coleta.setDataFim(fim.toLocalDateTime());
        }

        coleta.setPesoTotalKg(rs.getBigDecimal("peso_total_kg"));
        coleta.setObservacoes(rs.getString("observacoes"));

        return coleta;
    }

    private void setLongOuNull(
            PreparedStatement stmt, int indice, Long valor) throws SQLException {
        if (valor == null) {
            stmt.setNull(indice, Types.BIGINT);
        } else {
            stmt.setLong(indice, valor);
        }
    }

    private Long getLongOuNull(ResultSet rs, String coluna) throws SQLException {
        long valor = rs.getLong(coluna);
        return rs.wasNull() ? null : valor;
    }

    public Map<Long, String> listarCooperativas() {
        return listarOpcoes("SELECT id_cooperativa, nome FROM cooperativa ORDER BY nome");
    }

    public Map<Long, String> listarCooperados() {
        return listarOpcoes("SELECT id_cooperado, nome FROM cooperado ORDER BY nome");
    }

    public Map<Long, String> listarRotas() {
        return listarOpcoes("SELECT id_rota, nome FROM rota ORDER BY nome");
    }

    private Map<Long, String> listarOpcoes(String sql) {
        Map<Long, String> opcoes = new LinkedHashMap<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                opcoes.put(rs.getLong(1), rs.getString(2));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar opções.", e);
        }

        return opcoes;
    }
}