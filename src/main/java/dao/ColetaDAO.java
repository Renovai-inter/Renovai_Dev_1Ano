
package dao;

import model.Coleta;
<<<<<<< HEAD
import util.Conexao;

=======
import model.enums.StatusColeta;
import model.enums.TipoColeta;
import util.Conexao;
import util.Utilitario;
>>>>>>> origin/master
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
=======
>>>>>>> origin/master

public class ColetaDAO {

    public Long getUltimoIdColeta() {
        String sql = "SELECT COALESCE(MAX(id_coleta), 0) FROM coleta";

<<<<<<< HEAD
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
=======
    private Conexao conn = new Conexao();
    private Utilitario utilitario = new Utilitario();
>>>>>>> origin/master

            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar ID da coleta.", e);
        }

<<<<<<< HEAD
        return 0L;
=======
    // Inicializa o acesso aos dados de coleta.
    public ColetaDAO() {
>>>>>>> origin/master
    }

    public boolean cadastrarColeta(
            Coleta coleta,
            Map<Long, BigDecimal> materiais) {

<<<<<<< HEAD
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
                    stmt.setString(3, coleta.getTipo());
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
        coleta.setTipo(rs.getString("tipo"));
        coleta.setStatus(rs.getString("status"));

        coleta.setIdRota(getLongOuNull(rs, "id_rota"));
        coleta.setIdEnderecoRota(getLongOuNull(rs, "id_endereco_rota"));
        coleta.setIdCooperadoResponsavel(
                getLongOuNull(rs, "id_cooperado_responsavel"));

        coleta.setOrigemEntrega(rs.getString("origem_entrega"));
        coleta.setNomeLocalOrigem(rs.getString("nome_local_origem"));
        coleta.setDataAgendada(rs.getDate("data_agendada"));
        coleta.setDataInicio(rs.getTimestamp("data_inicio"));

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
=======
    // Cadastra uma coleta com os dados informados na Model.
    public Coleta cadastrarColeta(Long idCooperativa, TipoColeta tipo, StatusColeta status,
                                 Long idRota, Long idEnderecoRota, Long idCooperadoResponsavel,
                                 String origemEntrega, String nomeLocalOrigem, java.util.Date dataAgendada,
                                 LocalDateTime dataInicio, LocalDateTime dataFim, BigDecimal pesoTotalKg,
                                 String observacoes) {
        Coleta coleta = new Coleta();
        coleta.setIdCooperativa(idCooperativa);
        coleta.setTipo(tipo);
        coleta.setStatus(status);
        coleta.setIdRota(idRota);
        coleta.setIdEnderecoRota(idEnderecoRota);
        coleta.setIdCooperadoResponsavel(idCooperadoResponsavel);
        coleta.setOrigemEntrega(origemEntrega);
        coleta.setNomeLocalOrigem(nomeLocalOrigem);
        coleta.setDataAgendada(dataAgendada);
        coleta.setDataInicio(dataInicio);
        coleta.setDataFim(dataFim);
        coleta.setPesoTotalKg(pesoTotalKg);
        coleta.setObservacoes(observacoes);
        if (coleta.getIdCooperativa() == null || coleta.getTipo() == null) {
            throw new IllegalArgumentException("Cooperativa e tipo são obrigatórios.");
        }
        long ultimoId = utilitario.selecionarUltimoId("coleta");
        long idColeta = ultimoId == -1 ? 1 : ultimoId + 1;
        String sql = "INSERT INTO coleta (id_coleta, id_cooperativa, tipo, status, id_rota, " +
                "id_endereco_rota, id_cooperado_responsavel, origem_entrega, nome_local_origem, " +
                "data_agendada, data_inicio, data_fim, peso_total_kg, observacoes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, idColeta);
            pstmt.setLong(2, coleta.getIdCooperativa());
            pstmt.setString(3, coleta.getTipo().name());
            pstmt.setString(4, coleta.getStatus() == null ? StatusColeta.AGENDADA.name() : coleta.getStatus().name());
            pstmt.setObject(5, coleta.getIdRota(), Types.BIGINT);
            pstmt.setObject(6, coleta.getIdEnderecoRota(), Types.BIGINT);
            pstmt.setObject(7, coleta.getIdCooperadoResponsavel(), Types.BIGINT);
            pstmt.setString(8, coleta.getOrigemEntrega());
            pstmt.setString(9, coleta.getNomeLocalOrigem());
            pstmt.setDate(10, coleta.getDataAgendada() == null ? null
                    : new java.sql.Date(coleta.getDataAgendada().getTime()));
            pstmt.setTimestamp(11, coleta.getDataInicio() == null ? null
                    : Timestamp.valueOf(coleta.getDataInicio()));
            pstmt.setTimestamp(12, coleta.getDataFim() == null ? null
                    : Timestamp.valueOf(coleta.getDataFim()));
            pstmt.setBigDecimal(13, coleta.getPesoTotalKg());
            pstmt.setString(14, coleta.getObservacoes());
            int linhas = pstmt.executeUpdate();
            coleta.setIdColeta(idColeta);
            if (coleta.getStatus() == null) {
                coleta.setStatus(StatusColeta.AGENDADA);
            }
            return linhas == 0 ? null : coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Cadastra uma entrega recebida na cooperativa.
    public Coleta registroNaCooperativa(Long idCooperativa, String origemEntrega,
                                       String nomeLocalOrigem, BigDecimal pesoTotalKg,
                                       String observacoes) {
        Coleta coleta = new Coleta();
        coleta.setIdCooperativa(idCooperativa);
        coleta.setTipo(TipoColeta.ENTREGA_COOPERATIVA);
        coleta.setOrigemEntrega(origemEntrega);
        coleta.setNomeLocalOrigem(nomeLocalOrigem);
        coleta.setPesoTotalKg(pesoTotalKg);
        coleta.setObservacoes(observacoes);
        return cadastrarColeta(coleta.getIdCooperativa(), coleta.getTipo(), coleta.getStatus(),
                null, null, null, coleta.getOrigemEntrega(), coleta.getNomeLocalOrigem(),
                null, null, null, coleta.getPesoTotalKg(), coleta.getObservacoes());
    }

    // Atualiza a origem de uma coleta existente.
    public Coleta cadastrarOrigem(Long idColeta, String origemEntrega, String nomeLocalOrigem) {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setOrigemEntrega(origemEntrega);
        coleta.setNomeLocalOrigem(nomeLocalOrigem);
        if (coleta.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        String sql = "UPDATE coleta SET origem_entrega = ?, nome_local_origem = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coleta.getOrigemEntrega());
            pstmt.setString(2, coleta.getNomeLocalOrigem());
            pstmt.setLong(3, coleta.getIdColeta());
            return pstmt.executeUpdate() == 0 ? null : coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar a origem da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza o peso total de uma coleta existente.
    public Coleta registrarPesoTotal(Long idColeta, BigDecimal pesoTotalKg) {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setPesoTotalKg(pesoTotalKg);
        if (coleta.getIdColeta() == null || coleta.getPesoTotalKg() == null) {
            throw new IllegalArgumentException("ID e peso total são obrigatórios.");
        }
        String sql = "UPDATE coleta SET peso_total_kg = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, coleta.getPesoTotalKg());
            pstmt.setLong(2, coleta.getIdColeta());
            return pstmt.executeUpdate() == 0 ? null : coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar o peso da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Os parâmetros ficam preparados para o vínculo quando a Model representar os materiais da coleta.
    public Coleta quantidadeTipoMaterial(Long idColeta, Long idMaterial, BigDecimal quantidadeKg) {
        throw new UnsupportedOperationException("Pendente: representar idMaterial e quantidadeKg na Model da coleta.");
    }

    // === METODOS READ ================================================================================================

    private static final String CONSULTA_COLETAS =
            "SELECT id_coleta, id_cooperativa, tipo, status, id_rota, id_endereco_rota, " +
            "id_cooperado_responsavel, origem_entrega, nome_local_origem, data_agendada, " +
            "data_inicio, data_fim, peso_total_kg, observacoes FROM coleta";

    // Lista as coletas cadastradas.
    public ArrayList<Coleta> listarColetas() {
        return consultarColetas("");
    }

    // Lista as coletas que ainda podem ser acompanhadas.
    public ArrayList<Coleta> rastrearColeta() {
        return consultarColetas(" WHERE status IN ('AGENDADA', 'EM_ANDAMENTO')");
    }

    // Lista as coletas encerradas para o histórico.
    public ArrayList<Coleta> historicoColetas() {
        return consultarColetas(" WHERE status IN ('CONCLUIDA', 'CANCELADA')");
    }

    // Lista as coletas em andamento.
    public ArrayList<Coleta> coletasEmAndamento() {
        return consultarColetas(" WHERE status = 'EM_ANDAMENTO'");
    }

    // Executa uma consulta de coletas e monta uma Model para cada linha.
    private ArrayList<Coleta> consultarColetas(String filtro) {
        Connection conexao = conn.conectar();
        ArrayList<Coleta> coletas = new ArrayList<>();
        try (PreparedStatement pstmt = conexao.prepareStatement(CONSULTA_COLETAS + filtro);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Coleta coleta = new Coleta();
                coleta.setIdColeta(rs.getLong("id_coleta"));
                coleta.setIdCooperativa(rs.getLong("id_cooperativa"));
                coleta.setTipo(TipoColeta.valueOf(rs.getString("tipo")));
                coleta.setStatus(StatusColeta.valueOf(rs.getString("status")));
                coleta.setIdRota(idOpcional(rs, "id_rota"));
                coleta.setIdEnderecoRota(idOpcional(rs, "id_endereco_rota"));
                coleta.setIdCooperadoResponsavel(idOpcional(rs, "id_cooperado_responsavel"));
                coleta.setOrigemEntrega(rs.getString("origem_entrega"));
                coleta.setNomeLocalOrigem(rs.getString("nome_local_origem"));
                coleta.setDataAgendada(rs.getDate("data_agendada"));
                Timestamp inicio = rs.getTimestamp("data_inicio");
                Timestamp fim = rs.getTimestamp("data_fim");
                coleta.setDataInicio(inicio == null ? null : inicio.toLocalDateTime());
                coleta.setDataFim(fim == null ? null : fim.toLocalDateTime());
                coleta.setPesoTotalKg(rs.getBigDecimal("peso_total_kg"));
                coleta.setObservacoes(rs.getString("observacoes"));
                coletas.add(coleta);
            }
            return coletas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao consultar coletas.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Preserva a nulidade de uma chave opcional.
    private Long idOpcional(ResultSet rs, String coluna) throws SQLException {
        long valor = rs.getLong(coluna);
        return rs.wasNull() ? null : valor;
    }
    // === METODOS UPDATE ==============================================================================================

    // Inicia a coleta e registra o horário de início.
    public Coleta iniciarColeta(Long idColeta, LocalDateTime dataInicio, BigDecimal pesoTotalKg) {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setDataInicio(dataInicio);
        coleta.setPesoTotalKg(pesoTotalKg);
        if (coleta.getIdColeta() == null || coleta.getDataInicio() == null) {
            throw new IllegalArgumentException("ID e data de início são obrigatórios.");
        }
        String sql = "UPDATE coleta SET status = ?, peso_total_kg = ?, data_inicio = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, StatusColeta.EM_ANDAMENTO.name());
            pstmt.setBigDecimal(2, coleta.getPesoTotalKg());
            pstmt.setTimestamp(3, Timestamp.valueOf(coleta.getDataInicio()));
            pstmt.setLong(4, coleta.getIdColeta());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                coleta.setStatus(StatusColeta.EM_ANDAMENTO);
            }
            return linhas == 0 ? null : coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao iniciar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza os dados de uma coleta identificada.
    public Coleta atualizarColeta(Long idColeta, TipoColeta tipo, StatusColeta status,
                                  String origemEntrega, String nomeLocalOrigem,
                                  java.util.Date dataAgendada, LocalDateTime dataInicio,
                                  LocalDateTime dataFim, BigDecimal pesoTotalKg, String observacoes) {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setTipo(tipo);
        coleta.setStatus(status);
        coleta.setOrigemEntrega(origemEntrega);
        coleta.setNomeLocalOrigem(nomeLocalOrigem);
        coleta.setDataAgendada(dataAgendada);
        coleta.setDataInicio(dataInicio);
        coleta.setDataFim(dataFim);
        coleta.setPesoTotalKg(pesoTotalKg);
        coleta.setObservacoes(observacoes);
        if (coleta.getIdColeta() == null || coleta.getTipo() == null || coleta.getStatus() == null) {
            throw new IllegalArgumentException("ID, tipo e status são obrigatórios.");
        }
        String sql = "UPDATE coleta SET tipo = ?, status = ?, origem_entrega = ?, " +
                "nome_local_origem = ?, data_agendada = ?, data_inicio = ?, data_fim = ?, " +
                "peso_total_kg = ?, observacoes = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coleta.getTipo().name());
            pstmt.setString(2, coleta.getStatus().name());
            pstmt.setString(3, coleta.getOrigemEntrega());
            pstmt.setString(4, coleta.getNomeLocalOrigem());
            pstmt.setDate(5, coleta.getDataAgendada() == null ? null
                    : new java.sql.Date(coleta.getDataAgendada().getTime()));
            pstmt.setTimestamp(6, coleta.getDataInicio() == null ? null
                    : Timestamp.valueOf(coleta.getDataInicio()));
            pstmt.setTimestamp(7, coleta.getDataFim() == null ? null
                    : Timestamp.valueOf(coleta.getDataFim()));
            pstmt.setBigDecimal(8, coleta.getPesoTotalKg());
            pstmt.setString(9, coleta.getObservacoes());
            pstmt.setLong(10, coleta.getIdColeta());
            return pstmt.executeUpdate() == 0 ? null : coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza a coleta com peso e horário de término.
    public Coleta finalizarColeta(Long idColeta, LocalDateTime dataFim, BigDecimal pesoTotalKg) {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setDataFim(dataFim);
        coleta.setPesoTotalKg(pesoTotalKg);
        if (coleta.getIdColeta() == null || coleta.getDataFim() == null) {
            throw new IllegalArgumentException("ID e data de término são obrigatórios.");
        }
        String sql = "UPDATE coleta SET status = ?, peso_total_kg = ?, data_fim = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, StatusColeta.CONCLUIDA.name());
            pstmt.setBigDecimal(2, coleta.getPesoTotalKg());
            pstmt.setTimestamp(3, Timestamp.valueOf(coleta.getDataFim()));
            pstmt.setLong(4, coleta.getIdColeta());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                coleta.setStatus(StatusColeta.CONCLUIDA);
            }
            return linhas == 0 ? null : coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao finalizar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza uma entrega feita diretamente na cooperativa.
    public Coleta finalizacaoAutomatica(Long idColeta, LocalDateTime dataFim) {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setDataFim(dataFim);
        return concluirColeta(coleta) == 0 ? null : coleta;
    }

    // Encerra o acompanhamento de uma coleta finalizada.
    public Coleta encerramentoRastreamento(Long idColeta, LocalDateTime dataFim) {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setDataFim(dataFim);
        return concluirColeta(coleta) == 0 ? null : coleta;
    }

    // Atualiza apenas o status e a data de término.
    private int concluirColeta(Coleta coleta) {
        if (coleta.getIdColeta() == null || coleta.getDataFim() == null) {
            throw new IllegalArgumentException("ID e data de término são obrigatórios.");
        }
        String sql = "UPDATE coleta SET status = ?, data_fim = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, StatusColeta.CONCLUIDA.name());
            pstmt.setTimestamp(2, Timestamp.valueOf(coleta.getDataFim()));
            pstmt.setLong(3, coleta.getIdColeta());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                coleta.setStatus(StatusColeta.CONCLUIDA);
            }
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao concluir coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Cancela uma coleta preservando o registro para o histórico.
    public Coleta excluirColeta(Long idColeta) {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        if (coleta.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        String sql = "UPDATE coleta SET status = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, StatusColeta.CANCELADA.name());
            pstmt.setLong(2, coleta.getIdColeta());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                coleta.setStatus(StatusColeta.CANCELADA);
            }
            return linhas == 0 ? null : coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cancelar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

}
>>>>>>> origin/master
