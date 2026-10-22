package dao;
import model.principais.Coleta;
import model.enums.StatusColeta;
import model.enums.TipoColeta;
import util.ConexaoBD;
import util.Utilitario;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;

public class ColetaDAO {

    // ATRIBUTOS

    private ConexaoBD conn = new ConexaoBD();
    private Utilitario utilitario = new Utilitario();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de coleta.
    public ColetaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra uma coleta com os dados informados na Model.
    public Coleta cadastrarColeta(Long idCooperativa, TipoColeta tipo, StatusColeta status,
                                 Long idRota, Long idEnderecoRota, Long idCooperadoResponsavel,
                                 String origemEntrega, String nomeLocalOrigem, LocalDate dataAgendada,
                                 LocalDateTime dataInicio, LocalDateTime dataFim, BigDecimal pesoTotalKg,
                                 String observacoes) {

        // Declaração

        Coleta coleta = new Coleta();
        coleta.getRelacionamentos().setIdCooperativa(idCooperativa);
        coleta.setTipo(tipo);
        coleta.setStatus(status);
        coleta.getRelacionamentos().setIdRota(idRota);
        coleta.getRelacionamentos().setIdEnderecoRota(idEnderecoRota);
        coleta.getRelacionamentos().setIdCooperadoResponsavel(idCooperadoResponsavel);
        coleta.setOrigemEntrega(origemEntrega);
        coleta.setNomeLocalOrigem(nomeLocalOrigem);
        coleta.setDataAgendada(dataAgendada);
        coleta.setDataInicio(dataInicio);
        coleta.setDataFim(dataFim);
        coleta.setPesoTotalKg(pesoTotalKg);
        coleta.setObservacoes(observacoes);
        if (coleta.getRelacionamentos().getIdCooperativa() == null || coleta.getTipo() == null) {
            throw new IllegalArgumentException("Cooperativa e tipo são obrigatórios.");
        }
        long ultimoId = utilitario.selecionarUltimoId("coleta");
        long idColeta;
        if (ultimoId == -1) {
            idColeta = 1;
        } else {
            idColeta = ultimoId + 1;
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO coleta (id_coleta, id_cooperativa, tipo, status, id_rota, " +
                "id_endereco_rota, id_cooperado_responsavel, origem_entrega, nome_local_origem, " +
                "data_agendada, data_inicio, data_fim, peso_total_kg, observacoes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, idColeta);
            pstmt.setLong(2, coleta.getRelacionamentos().getIdCooperativa());
            pstmt.setString(3, coleta.getTipo().name());
            if (coleta.getStatus() == null) {
                coleta.setStatus(StatusColeta.AGENDADA);
            }
            pstmt.setString(4, coleta.getStatus().name());
            pstmt.setObject(5, coleta.getRelacionamentos().getIdRota(), Types.BIGINT);
            pstmt.setObject(6, coleta.getRelacionamentos().getIdEnderecoRota(), Types.BIGINT);
            pstmt.setObject(7, coleta.getRelacionamentos().getIdCooperadoResponsavel(), Types.BIGINT);
            pstmt.setString(8, coleta.getOrigemEntrega());
            pstmt.setString(9, coleta.getNomeLocalOrigem());
            if (coleta.getDataAgendada() == null) {
                pstmt.setNull(10, Types.DATE);
            } else {
                pstmt.setDate(10, Date.valueOf(coleta.getDataAgendada()));
            }
            if (coleta.getDataInicio() == null) {
                pstmt.setNull(11, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(11, Timestamp.valueOf(coleta.getDataInicio()));
            }
            if (coleta.getDataFim() == null) {
                pstmt.setNull(12, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(12, Timestamp.valueOf(coleta.getDataFim()));
            }
            pstmt.setBigDecimal(13, coleta.getPesoTotalKg());
            pstmt.setString(14, coleta.getObservacoes());
            int linhas = pstmt.executeUpdate();
            if (linhas == 0) {
                return null;
            }
            coleta.setIdColeta(idColeta);
            return coleta;
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

        // Execução e retorno

        return cadastrarColeta(idCooperativa, TipoColeta.ENTREGA_COOPERATIVA, null,
                null, null, null, origemEntrega, nomeLocalOrigem, null, null, null,
                pesoTotalKg, observacoes);
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

        // Execução e retorno

        return consultarColetas("");
    }

    // Lista as coletas que ainda podem ser acompanhadas.
    public ArrayList<Coleta> rastrearColeta() {

        // Execução e retorno

        return consultarColetas(" WHERE status IN ('AGENDADA', 'EM_ANDAMENTO')");
    }

    // Lista as coletas encerradas para o histórico.
    public ArrayList<Coleta> historicoColetas() {

        // Execução e retorno

        return consultarColetas(" WHERE status IN ('CONCLUIDA', 'CANCELADA')");
    }

    // Lista as coletas em andamento.
    public ArrayList<Coleta> coletasEmAndamento() {

        // Execução e retorno

        return consultarColetas(" WHERE status = 'EM_ANDAMENTO'");
    }

    // Executa uma consulta de coletas e monta uma Model para cada linha.
    private ArrayList<Coleta> consultarColetas(String filtro) {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Coleta> coletas = new ArrayList<>();

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(CONSULTA_COLETAS + filtro);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Coleta coleta = new Coleta();
                coleta.setIdColeta(rs.getLong("id_coleta"));
                coleta.getRelacionamentos().setIdCooperativa(rs.getLong("id_cooperativa"));
                coleta.setTipo(TipoColeta.valueOf(rs.getString("tipo")));
                coleta.setStatus(StatusColeta.valueOf(rs.getString("status")));
                coleta.getRelacionamentos().setIdRota(idOpcional(rs, "id_rota"));
                coleta.getRelacionamentos().setIdEnderecoRota(idOpcional(rs, "id_endereco_rota"));
                coleta.getRelacionamentos().setIdCooperadoResponsavel(idOpcional(rs, "id_cooperado_responsavel"));
                coleta.setOrigemEntrega(rs.getString("origem_entrega"));
                coleta.setNomeLocalOrigem(rs.getString("nome_local_origem"));
                Date data = rs.getDate("data_agendada");
                if (data != null) {
                    coleta.setDataAgendada(data.toLocalDate());
                }
                Timestamp inicio = rs.getTimestamp("data_inicio");
                Timestamp fim = rs.getTimestamp("data_fim");
                if (inicio != null) {
                    coleta.setDataInicio(inicio.toLocalDateTime());
                }
                if (fim != null) {
                    coleta.setDataFim(fim.toLocalDateTime());
                }
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
        if (rs.wasNull()) {
            return null;
        }
        return valor;
    }
    // === METODOS UPDATE ==============================================================================================

    // Atualiza a origem de uma coleta existente.
    public Coleta cadastrarOrigem(Long idColeta, String origemEntrega, String nomeLocalOrigem) {

        // Declaração

        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setOrigemEntrega(origemEntrega);
        coleta.setNomeLocalOrigem(nomeLocalOrigem);
        if (coleta.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET origem_entrega = ?, nome_local_origem = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coleta.getOrigemEntrega());
            pstmt.setString(2, coleta.getNomeLocalOrigem());
            pstmt.setLong(3, coleta.getIdColeta());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar a origem da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza o peso total de uma coleta existente.
    public Coleta registrarPesoTotal(Long idColeta, BigDecimal pesoTotalKg) {

        // Declaração

        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setPesoTotalKg(pesoTotalKg);
        if (coleta.getIdColeta() == null || coleta.getPesoTotalKg() == null) {
            throw new IllegalArgumentException("ID e peso total são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET peso_total_kg = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, coleta.getPesoTotalKg());
            pstmt.setLong(2, coleta.getIdColeta());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar o peso da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Inicia a coleta e registra o horário de início.
    public Coleta iniciarColeta(Long idColeta, LocalDateTime dataInicio, BigDecimal pesoTotalKg) {

        // Declaração

        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setDataInicio(dataInicio);
        coleta.setPesoTotalKg(pesoTotalKg);
        if (coleta.getIdColeta() == null || coleta.getDataInicio() == null) {
            throw new IllegalArgumentException("ID e data de início são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET status = ?, peso_total_kg = ?, data_inicio = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, StatusColeta.EM_ANDAMENTO.name());
            pstmt.setBigDecimal(2, coleta.getPesoTotalKg());
            pstmt.setTimestamp(3, Timestamp.valueOf(coleta.getDataInicio()));
            pstmt.setLong(4, coleta.getIdColeta());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                coleta.setStatus(StatusColeta.EM_ANDAMENTO);
            }
            if (linhas == 0) {
                return null;
            }
            return coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao iniciar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza os dados de uma coleta identificada.
    public Coleta atualizarColeta(Long idColeta, TipoColeta tipo, StatusColeta status,
                                  String origemEntrega, String nomeLocalOrigem,
                                  LocalDate dataAgendada, LocalDateTime dataInicio,
                                  LocalDateTime dataFim, BigDecimal pesoTotalKg, String observacoes) {

        // Declaração

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
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET tipo = ?, status = ?, origem_entrega = ?, " +
                "nome_local_origem = ?, data_agendada = ?, data_inicio = ?, data_fim = ?, " +
                "peso_total_kg = ?, observacoes = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coleta.getTipo().name());
            pstmt.setString(2, coleta.getStatus().name());
            pstmt.setString(3, coleta.getOrigemEntrega());
            pstmt.setString(4, coleta.getNomeLocalOrigem());
            if (coleta.getDataAgendada() == null) {
                pstmt.setNull(5, Types.DATE);
            } else {
                pstmt.setDate(5, Date.valueOf(coleta.getDataAgendada()));
            }
            if (coleta.getDataInicio() == null) {
                pstmt.setNull(6, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(6, Timestamp.valueOf(coleta.getDataInicio()));
            }
            if (coleta.getDataFim() == null) {
                pstmt.setNull(7, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(7, Timestamp.valueOf(coleta.getDataFim()));
            }
            pstmt.setBigDecimal(8, coleta.getPesoTotalKg());
            pstmt.setString(9, coleta.getObservacoes());
            pstmt.setLong(10, coleta.getIdColeta());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza a coleta com peso e horário de término.
    public Coleta finalizarColeta(Long idColeta, LocalDateTime dataFim, BigDecimal pesoTotalKg) {

        // Declaração

        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setDataFim(dataFim);
        coleta.setPesoTotalKg(pesoTotalKg);
        if (coleta.getIdColeta() == null || coleta.getDataFim() == null) {
            throw new IllegalArgumentException("ID e data de término são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET status = ?, peso_total_kg = ?, data_fim = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, StatusColeta.CONCLUIDA.name());
            pstmt.setBigDecimal(2, coleta.getPesoTotalKg());
            pstmt.setTimestamp(3, Timestamp.valueOf(coleta.getDataFim()));
            pstmt.setLong(4, coleta.getIdColeta());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                coleta.setStatus(StatusColeta.CONCLUIDA);
            }
            if (linhas == 0) {
                return null;
            }
            return coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao finalizar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza uma entrega feita diretamente na cooperativa.
    public Coleta finalizacaoAutomatica(Long idColeta, LocalDateTime dataFim) {

        // Declaração

        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setDataFim(dataFim);

        // Execução e retorno

        if (concluirColeta(coleta) == 0) {
            return null;
        }
        return coleta;
    }

    // Encerra o acompanhamento de uma coleta finalizada.
    public Coleta encerramentoRastreamento(Long idColeta, LocalDateTime dataFim) {

        // Declaração

        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        coleta.setDataFim(dataFim);

        // Execução e retorno

        if (concluirColeta(coleta) == 0) {
            return null;
        }
        return coleta;
    }

    // Atualiza apenas o status e a data de término.
    private int concluirColeta(Coleta coleta) {

        // Declaração

        if (coleta.getIdColeta() == null || coleta.getDataFim() == null) {
            throw new IllegalArgumentException("ID e data de término são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET status = ?, data_fim = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

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

        // Declaração

        Coleta coleta = new Coleta();
        coleta.setIdColeta(idColeta);
        if (coleta.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET status = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, StatusColeta.CANCELADA.name());
            pstmt.setLong(2, coleta.getIdColeta());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                coleta.setStatus(StatusColeta.CANCELADA);
            }
            if (linhas == 0) {
                return null;
            }
            return coleta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cancelar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

}
