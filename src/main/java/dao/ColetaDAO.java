package dao;
import model.Coleta;
import model.Cooperado;
import model.enums.StatusColeta;
import model.enums.TipoColeta;
import util.Conexao;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.sql.Date;

public class ColetaDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de coleta.
    public ColetaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra uma coleta com os dados informados na Model.
    public int cadastrarColeta(Coleta coleta) {
        if (coleta.getIdCooperativa() == null || coleta.getTipo() == null) {
            throw new IllegalArgumentException("Cooperativa e tipo são obrigatórios.");
        }
        String sql = "INSERT INTO coleta (id_coleta, id_cooperativa, tipo, status, id_rota, " +
                "id_endereco_rota, id_cooperado_responsavel, origem_entrega, nome_local_origem, " +
                "data_agendada, data_inicio, data_fim, peso_total_kg, observacoes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            long idColeta = coleta.getIdColeta() == null ? proximoId(conexao) : coleta.getIdColeta();
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
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Cadastra uma entrega recebida na cooperativa.
    public int registroNaCooperativa(Coleta coleta) {
        coleta.setTipo(TipoColeta.ENTREGA_COOPERATIVA);
        return cadastrarColeta(coleta);
    }

    // Atualiza a origem de uma coleta existente.
    public int cadastrarOrigem(Coleta coleta) {
        if (coleta.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        String sql = "UPDATE coleta SET origem_entrega = ?, nome_local_origem = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coleta.getOrigemEntrega());
            pstmt.setString(2, coleta.getNomeLocalOrigem());
            pstmt.setLong(3, coleta.getIdColeta());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar a origem da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza o peso total de uma coleta existente.
    public double registrarPesoTotal(Coleta coleta) {
        if (coleta.getIdColeta() == null || coleta.getPesoTotalKg() == null) {
            throw new IllegalArgumentException("ID e peso total são obrigatórios.");
        }
        String sql = "UPDATE coleta SET peso_total_kg = ? WHERE id_coleta = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, coleta.getPesoTotalKg());
            pstmt.setLong(2, coleta.getIdColeta());
            return pstmt.executeUpdate() == 0 ? 0 : coleta.getPesoTotalKg().doubleValue();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar o peso da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // A distribuição por material precisa de um ID de material, ausente deste contrato.
    public int QuantidadeTipoMaterial(Coleta coleta) {
        throw new UnsupportedOperationException("Informe o material da coleta antes de registrar sua quantidade.");
    }

    // Consulta o próximo ID na conexão usada para inserir a coleta.
    private long proximoId(Connection conexao) throws SQLException {
        String sql = "SELECT COALESCE(MAX(id_coleta), 0) + 1 FROM coleta";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        }
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
    public int iniciarColeta(Coleta coleta) {
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
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao iniciar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza os dados de uma coleta identificada.
    public int atualizarColeta(Coleta coleta) {
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
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza a coleta com peso e horário de término.
    public int finalizarColeta(Coleta coleta) {
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
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao finalizar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza uma entrega feita diretamente na cooperativa.
    public int finalizacaoAutomatica(Coleta coleta) {
        return concluirColeta(coleta);
    }

    // Encerra o acompanhamento de uma coleta finalizada.
    public int encerramentoRastreamento(Coleta coleta) {
        return concluirColeta(coleta);
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
    public int excluirColeta(Coleta coleta) {
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
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cancelar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Consulta o último ID cadastrado em coleta.
    public long getUltimoIdColeta() {
        String sql = "SELECT MAX(id_coleta) FROM coleta";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            long ultimoId = rs.getLong(1);
            return rs.wasNull() ? -1 : ultimoId;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao consultar o último ID de coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }
}