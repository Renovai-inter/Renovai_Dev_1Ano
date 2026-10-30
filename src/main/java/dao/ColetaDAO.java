package dao;
import model.principais.Coleta;
import model.auxiliares.ColetaMaterial;
import model.enums.StatusColeta;
import model.enums.TipoColeta;
import util.ConexaoBD;
import util.Utilitario;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

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
    public int cadastrarColeta(Coleta coleta) {

        // Declaração

        if (coleta == null) {
            throw new IllegalArgumentException("coleta é obrigatória.");
        }
        coleta.validarTipoEVinculos();
        if (coleta.getRelacionamentos().getIdCooperativa() == null || coleta.getTipo() == null) {
            throw new IllegalArgumentException("Cooperativa e tipo são obrigatórios.");
        }
        List<ColetaMaterial> materiais = coleta.getRelacionamentos().getMateriaisColetados();
        for (ColetaMaterial item : materiais) {
            if (item == null || item.getIdMaterial() == null || item.getQuantidadeKg() == null) {
                throw new IllegalArgumentException("Material e quantidade são obrigatórios.");
            }
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
                "id_endereco_rota, id_cooperado_responsavel, nome_local_origem, " +
                "data_agendada, data_inicio, data_fim, peso_total_kg, observacoes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlMaterial = "INSERT INTO coleta_material (id_coleta, id_material, quantidade_kg) " +
                "VALUES (?, ?, ?)";

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
            pstmt.setString(8, coleta.getNomeLocalOrigem());
            if (coleta.getDataAgendada() == null) {
                pstmt.setNull(9, Types.DATE);
            } else {
                pstmt.setDate(9, Date.valueOf(coleta.getDataAgendada()));
            }
            if (coleta.getDataInicio() == null) {
                pstmt.setNull(10, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(10, Timestamp.valueOf(coleta.getDataInicio()));
            }
            if (coleta.getDataFim() == null) {
                pstmt.setNull(11, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(11, Timestamp.valueOf(coleta.getDataFim()));
            }
            pstmt.setBigDecimal(12, coleta.getPesoTotalKg());
            pstmt.setString(13, coleta.getObservacoes());
            int linhas = pstmt.executeUpdate();
            if (linhas == 0) {
                return 0;
            }
            coleta.setIdColeta(idColeta);
            try (PreparedStatement pstmtMaterial = conexao.prepareStatement(sqlMaterial)) {
                for (ColetaMaterial item : materiais) {
                    pstmtMaterial.setLong(1, idColeta);
                    pstmtMaterial.setLong(2, item.getIdMaterial());
                    pstmtMaterial.setBigDecimal(3, item.getQuantidadeKg());
                    int linhasMaterial = pstmtMaterial.executeUpdate();
                    if (linhasMaterial == 0) {
                        throw new IllegalStateException("Material da coleta não foi registrado.");
                    }
                    item.setIdColeta(idColeta);
                    linhas += linhasMaterial;
                }
            }
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Cadastra uma entrega recebida na cooperativa.
    public int registroNaCooperativa(Coleta coleta) {

        // Execução e retorno

        if (coleta == null) {
            throw new IllegalArgumentException("coleta é obrigatória.");
        }
        coleta.setTipo(TipoColeta.ENTREGA_COOPERATIVA);
        return cadastrarColeta(coleta);
    }

    // Registra a quantidade de um material vinculado à coleta.
    public int quantidadeTipoMaterial(ColetaMaterial item) {

        // Declaração

        if (item == null || item.getIdColeta() == null || item.getIdMaterial() == null
                || item.getQuantidadeKg() == null) {
            throw new IllegalArgumentException("Dados do material da coleta são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO coleta_material (id_coleta, id_material, quantidade_kg) " +
                "VALUES (?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, item.getIdColeta());
            pstmt.setLong(2, item.getIdMaterial());
            pstmt.setBigDecimal(3, item.getQuantidadeKg());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao registrar material da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    private static final String CONSULTA_COLETAS =
            "SELECT id_coleta, id_cooperativa, tipo, status, id_rota, id_endereco_rota, " +
            "id_cooperado_responsavel, nome_local_origem, data_agendada, " +
            "data_inicio, data_fim, peso_total_kg, observacoes FROM coleta";

    // Lista as coletas cadastradas.
    public ArrayList<Coleta> listarColetas() {

        // Execução e retorno

        return consultarColetas("");
    }

    // Busca a coleta identificada pelo objeto recebido.
    public Coleta buscarPorId(Coleta filtro) {
        if (filtro == null || filtro.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        Connection conexao = conn.conectar();
        String sql = CONSULTA_COLETAS + " WHERE id_coleta = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, filtro.getIdColeta());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return lerColeta(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Lista a quantidade de cada material associada à coleta.
    public Map<Long, BigDecimal> listarMateriaisDaColeta(Coleta filtro) {
        if (filtro == null || filtro.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        Map<Long, BigDecimal> materiais = new LinkedHashMap<>();
        Connection conexao = conn.conectar();
        String sql = "SELECT id_material, quantidade_kg FROM coleta_material WHERE id_coleta = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, filtro.getIdColeta());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    materiais.put(rs.getLong("id_material"), rs.getBigDecimal("quantidade_kg"));
                }
            }
            return materiais;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar materiais da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Carrega as cooperativas disponíveis para o formulário.
    public Map<Long, String> listarCooperativas() {
        return listarOpcoes("SELECT id_cooperativa, nome_publico FROM cooperativa ORDER BY nome_publico");
    }

    // Carrega os cooperados disponíveis para o formulário.
    public Map<Long, String> listarCooperados() {
        return listarOpcoes("SELECT id_cooperado, nome_completo FROM cooperado " +
                "JOIN usuario USING (id_usuario) ORDER BY nome_completo");
    }

    // Carrega as rotas disponíveis para o formulário.
    public Map<Long, String> listarRotas() {
        return listarOpcoes("SELECT id_rota, nome_rota FROM rota ORDER BY nome_rota");
    }

    // Executa uma consulta fixa de duas colunas para opções de seleção.
    private Map<Long, String> listarOpcoes(String sql) {
        Map<Long, String> opcoes = new LinkedHashMap<>();
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                opcoes.put(rs.getLong(1), rs.getString(2));
            }
            return opcoes;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar opções da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
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
                coletas.add(lerColeta(rs));
            }
            return coletas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao consultar coletas.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Preenche uma coleta a partir da linha consultada.
    private Coleta lerColeta(ResultSet rs) throws SQLException {
        Coleta coleta = new Coleta();
        coleta.setIdColeta(rs.getLong("id_coleta"));
        coleta.getRelacionamentos().setIdCooperativa(rs.getLong("id_cooperativa"));
        coleta.setTipo(TipoColeta.valueOf(rs.getString("tipo")));
        coleta.setStatus(StatusColeta.valueOf(rs.getString("status")));
        coleta.getRelacionamentos().setIdRota(idOpcional(rs, "id_rota"));
        coleta.getRelacionamentos().setIdEnderecoRota(idOpcional(rs, "id_endereco_rota"));
        coleta.getRelacionamentos().setIdCooperadoResponsavel(idOpcional(rs, "id_cooperado_responsavel"));
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
        return coleta;
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

    // Atualiza somente o status da coleta identificada.
    public int atualizarStatus(Coleta coleta) {
        if (coleta == null || coleta.getIdColeta() == null || coleta.getStatus() == null) {
            throw new IllegalArgumentException("ID e status são obrigatórios.");
        }
        Connection conexao = conn.conectar();
        String sql = "UPDATE coleta SET status = ? WHERE id_coleta = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coleta.getStatus().name());
            pstmt.setLong(2, coleta.getIdColeta());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar status da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza o nome do local de origem de uma coleta existente.
    public int cadastrarOrigem(Coleta coleta) {

        // Declaração

        if (coleta == null || coleta.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET nome_local_origem = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coleta.getNomeLocalOrigem());
            pstmt.setLong(2, coleta.getIdColeta());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar a origem da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza o peso total de uma coleta existente.
    public int registrarPesoTotal(Coleta coleta) {

        // Declaração

        if (coleta == null || coleta.getIdColeta() == null || coleta.getPesoTotalKg() == null) {
            throw new IllegalArgumentException("ID e peso total são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET peso_total_kg = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, coleta.getPesoTotalKg());
            pstmt.setLong(2, coleta.getIdColeta());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar o peso da coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Inicia a coleta e registra o horário de início.
    public int iniciarColeta(Coleta coleta) {

        // Declaração

        if (coleta == null || coleta.getIdColeta() == null || coleta.getDataInicio() == null) {
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
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao iniciar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza os dados de uma coleta identificada.
    public int atualizarColeta(Coleta coleta) {

        // Declaração

        if (coleta == null) {
            throw new IllegalArgumentException("coleta é obrigatória.");
        }
        coleta.validarTipoEVinculos();
        if (coleta.getIdColeta() == null || coleta.getTipo() == null || coleta.getStatus() == null
                || coleta.getRelacionamentos().getIdCooperativa() == null) {
            throw new IllegalArgumentException("ID, cooperativa, tipo e status são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta SET id_cooperativa = ?, id_rota = ?, id_endereco_rota = ?, " +
                "id_cooperado_responsavel = ?, tipo = ?, status = ?, " +
                "nome_local_origem = ?, data_agendada = ?, data_inicio = ?, data_fim = ?, " +
                "peso_total_kg = ?, observacoes = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, coleta.getRelacionamentos().getIdCooperativa());
            pstmt.setObject(2, coleta.getRelacionamentos().getIdRota(), Types.BIGINT);
            pstmt.setObject(3, coleta.getRelacionamentos().getIdEnderecoRota(), Types.BIGINT);
            pstmt.setObject(4, coleta.getRelacionamentos().getIdCooperadoResponsavel(), Types.BIGINT);
            pstmt.setString(5, coleta.getTipo().name());
            pstmt.setString(6, coleta.getStatus().name());
            pstmt.setString(7, coleta.getNomeLocalOrigem());
            if (coleta.getDataAgendada() == null) {
                pstmt.setNull(8, Types.DATE);
            } else {
                pstmt.setDate(8, Date.valueOf(coleta.getDataAgendada()));
            }
            if (coleta.getDataInicio() == null) {
                pstmt.setNull(9, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(9, Timestamp.valueOf(coleta.getDataInicio()));
            }
            if (coleta.getDataFim() == null) {
                pstmt.setNull(10, Types.TIMESTAMP);
            } else {
                pstmt.setTimestamp(10, Timestamp.valueOf(coleta.getDataFim()));
            }
            pstmt.setBigDecimal(11, coleta.getPesoTotalKg());
            pstmt.setString(12, coleta.getObservacoes());
            pstmt.setLong(13, coleta.getIdColeta());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Sincroniza os materiais informados após a atualização da coleta.
    public int atualizarMateriaisDaColeta(Coleta coleta) {
        if (coleta == null || coleta.getIdColeta() == null) {
            throw new IllegalArgumentException("ID da coleta é obrigatório.");
        }
        Map<Long, BigDecimal> novos = new LinkedHashMap<>();
        for (ColetaMaterial item : coleta.getRelacionamentos().getMateriaisColetados()) {
            if (item == null || item.getIdMaterial() == null || item.getQuantidadeKg() == null
                    || novos.putIfAbsent(item.getIdMaterial(), item.getQuantidadeKg()) != null) {
                throw new IllegalArgumentException("Confira os materiais e suas quantidades sem repetir material.");
            }
        }
        Map<Long, BigDecimal> antigos = listarMateriaisDaColeta(coleta);
        Connection conexao = conn.conectar();
        int linhas = 0;
        try (PreparedStatement atualizar = conexao.prepareStatement(
                "UPDATE coleta_material SET quantidade_kg = ? WHERE id_coleta = ? AND id_material = ?");
             PreparedStatement inserir = conexao.prepareStatement(
                     "INSERT INTO coleta_material (id_coleta, id_material, quantidade_kg) VALUES (?, ?, ?)");
             PreparedStatement excluir = conexao.prepareStatement(
                     "DELETE FROM coleta_material WHERE id_coleta = ? AND id_material = ?")) {
            for (Map.Entry<Long, BigDecimal> item : novos.entrySet()) {
                atualizar.setBigDecimal(1, item.getValue());
                atualizar.setLong(2, coleta.getIdColeta());
                atualizar.setLong(3, item.getKey());
                int atualizadas = atualizar.executeUpdate();
                if (atualizadas == 0) {
                    inserir.setLong(1, coleta.getIdColeta());
                    inserir.setLong(2, item.getKey());
                    inserir.setBigDecimal(3, item.getValue());
                    linhas += inserir.executeUpdate();
                } else {
                    linhas += atualizadas;
                }
            }
            for (Long idAntigo : antigos.keySet()) {
                if (!novos.containsKey(idAntigo)) {
                    excluir.setLong(1, coleta.getIdColeta());
                    excluir.setLong(2, idAntigo);
                    linhas += excluir.executeUpdate();
                }
            }
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar materiais da coleta; gravações anteriores podem ter sido mantidas.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza a coleta com peso e horário de término.
    public int finalizarColeta(Coleta coleta) {

        // Declaração

        if (coleta == null || coleta.getIdColeta() == null || coleta.getDataFim() == null) {
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

        // Declaração

        if (coleta == null || coleta.getIdColeta() == null || coleta.getDataFim() == null) {
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
    public int excluirColeta(Coleta coleta) {

        // Declaração

        if (coleta == null || coleta.getIdColeta() == null) {
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
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cancelar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Apaga definitivamente a coleta identificada quando o banco permite.
    public int apagarColeta(Coleta coleta) {
        if (coleta == null || coleta.getIdColeta() == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        Connection conexao = conn.conectar();
        String sql = "DELETE FROM coleta WHERE id_coleta = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, coleta.getIdColeta());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao apagar coleta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

}
