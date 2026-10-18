package dao;
import model.Coleta;
import model.Cooperado;
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

    // Cadastra uma coleta externa agendada com seus dados obrigatórios.
    public int cadastrarColeta(Coleta coleta) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO coleta " + "(id_cooperativa," +
                "tipo, id_rota, id_endereco_rota, id_cooperado_responsavel,"
                + "data_agendada, observacoes)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            long ultimoIdColeta = getUltimoIdColeta();

            if (ultimoIdColeta == -1) {
                pstmt.setLong(1, 1);
            } else {
                pstmt.setLong(1, ultimoIdColeta + 1);
            }

            pstmt.setLong(2, coleta.getIdCooperativa());

            pstmt.setString(3, coleta.getTipo());

            pstmt.setLong(4, coleta.getIdRota());

            pstmt.setLong(5, coleta.getIdEnderecoRota());

            pstmt.setLong(6, coleta.getIdCooperadoResponsavel());

            pstmt.setDate(7, new java.sql.Date(coleta.getDataAgendada().getYear()));

            pstmt.setString(8, coleta.getObservacoes());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra uma entrega recebida diretamente na cooperativa.
    public int registroNaCooperativa(Coleta coleta) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO coleta(id_coleta, origem_entrega, nome_local_origem, data_fim, observacoes) VALUES (?,?,?,?,?,?)";

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            long ultimoIdColeta = getUltimoIdColeta();

            if (ultimoIdColeta == -1) {
                pstm.setLong(1, 1);
            } else {
                pstm.setLong(1, ultimoIdColeta + 1);
            }

            pstm.setLong(2, coleta.getIdColeta());
            pstm.setString(3, coleta.getOrigemEntrega());
            pstm.setString(4, coleta.getNomeLocalOrigem());
            pstm.setDate(5, new java.sql.Date(coleta.getDataFim().getDayOfYear()));
            pstm.setString(6, coleta.getObservacoes());

            return pstm.executeUpdate();
        } catch (SQLException sqle) {
            sqle.getMessage();
        }
        return 0;
    }

    // Registra a origem informada para uma entrega.
    public int cadastrarOrigem(Coleta coleta){

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO coleta(nome_local_origem, origem_entrega) VALUES(?,?)";

        // Executar PreparedStatement

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setString(1, coleta.getNomeLocalOrigem());
            pstm.setString(2, coleta.getOrigemEntrega());

            pstm.executeUpdate();

        } catch (SQLException e) {
            e.getMessage();
        }
        return 0;
    }

    // Registra o peso total de uma coleta existente.
    public double registrarPesoTotal(Coleta coleta){

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO coleta(peso_total_kg) VALUES (?)";

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setBigDecimal(1, coleta.getPesoTotalKg());

            pstm.executeUpdate();

        } catch (SQLException sqle){
            sqle.getMessage();
        }

        return 0;
    }

    // Registra a quantidade recebida por tipo de material.
    public int QuantidadeTipoMaterial(Coleta coleta){

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO coleta(tipo, peso_total_kg) VALUES(?,?)";

        // Executar PreparedStatement

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setString(1, coleta.getTipo());
            pstm.setBigDecimal(2, coleta.getPesoTotalKg());

            pstm.executeUpdate();

        } catch (SQLException sqle) {
            sqle.getMessage();
        }
        return 0;
    }

    // === METODOS READ ================================================================================================

    // Lista as coletas cadastradas com seus dados principais.
    public ArrayList<Coleta> listarColetas() {

        // Declaração

        ArrayList<Coleta> coletas = new ArrayList<>();

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "SELECT " +
                "id_coleta, id_cooperativa," +
                "tipo,status,id_rota," + "id_endereco_rota,id_cooperado_responsavel," +
                "origem_entrega,nome_local_origem,data_agendada,data_inicio,data_fim" +
                ",peso_total_kg,observacoes " +
                "FROM coleta";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Coleta coleta = new Coleta();
                Long id_coleta = rs.getLong("id_coleta");
                Long id_cooperativa = rs.getLong("id_cooperativa");
                String tipo = rs.getString("tipo");
                String status = rs.getString("status");
                Long id_rota = rs.getLong("id_rota");
                Long id_endereco_rota = rs.getLong("id_endereco_rota");
                Long id_cooperado_responsavel = rs.getLong("id_cooperado_responsavel");
                String origem = rs.getString("origem_entrega");
                String nome_local_origem = rs.getString("nome_local_origem");
                Date data_agendada = rs.getDate("data_agendada");
                Date data_inicio = rs.getDate("data_inicio");
                BigDecimal peso_total_kg = rs.getBigDecimal("peso_total_kg");
                String observacoes = rs.getString("observacoes");

                coleta.setIdColeta(id_coleta);
                coleta.setIdCooperativa(id_cooperativa);
                coleta.setTipo(tipo);
                coleta.setStatus(status);
                coleta.setIdRota(id_rota);
                coleta.setIdEnderecoRota(id_endereco_rota);
                coleta.setIdCooperadoResponsavel(id_cooperado_responsavel);
                coleta.setOrigemEntrega(origem);
                coleta.setNomeLocalOrigem(nome_local_origem);
                coleta.setDataAgendada(data_agendada);
                coleta.setDataInicio(data_inicio);
                coleta.setPesoTotalKg(peso_total_kg);
                coleta.setObservacoes(observacoes);

                coletas.add(coleta);
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
        return coletas;
    }

    // Consulta os dados disponíveis para acompanhar uma coleta externa.
    public ArrayList<Coleta> rastrearColeta(){

        // Declaração

        ArrayList<Coleta> coletas = new ArrayList<>();

        // Comando SQL

        String sql;
        sql = "SELECT id_coleta, tipo, status, id_rota, id_endereco_rota, id_cooperado_responsavel, origem_entrega, nome_local_origem, peso_total_kg, data_fim FROM coleta where id_coleta = ? ";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            ResultSet rst = pstm.executeQuery();

            Coleta coleta = null;
            while (rst.next()) {
                coleta = new Coleta();

                coleta.setIdColeta(rst.getLong("id_coleta"));
                coleta.setTipo(rst.getString("tipo"));
                coleta.setStatus(rst.getString("status"));
                coleta.setIdRota(rst.getLong("id_rota"));
                coleta.setIdEnderecoRota((rst.getLong("id_endereco_rota")));
                coleta.setIdCooperadoResponsavel((rst.getLong("id_cooperado_responsavel")));
                coleta.setOrigemEntrega((rst.getString("origem_entrega")));
                coleta.setNomeLocalOrigem((rst.getString("nome_local_origem")));
                coleta.setPesoTotalKg((rst.getBigDecimal("peso_total_kg")));
                coleta.setDataFim((rst.getTimestamp("data_fim")).toLocalDateTime());
            }

            coletas.add(coleta);

        } catch (SQLException e) {
            e.getMessage();
        }

        return coletas;
    }

    // Lista as coletas registradas para consulta do histórico.
    public ArrayList<Coleta> historicoColetas(){

        // Declaração

        ArrayList<Coleta> coletas = new ArrayList<>();

        // Comando SQL

        String sql;
        sql = "SELECT id_coleta, tipo, status, id_rota, id_endereco_rota, id_cooperado_responsavel, origem_entrega, nome_local_origem, peso_total_kg, data_fim FROM coleta where id_coleta = ? ";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            ResultSet rst = pstm.executeQuery();

            Coleta coleta = null;
            while (rst.next()) {
                coleta = new Coleta();

                coleta.setIdColeta(rst.getLong("id_coleta"));
                coleta.setTipo(rst.getString("tipo"));
                coleta.setStatus(rst.getString("status"));
                coleta.setIdRota(rst.getLong("id_rota"));
                coleta.setIdEnderecoRota((rst.getLong("id_endereco_rota")));
                coleta.setIdCooperadoResponsavel((rst.getLong("id_cooperado_responsavel")));
                coleta.setOrigemEntrega((rst.getString("origem_entrega")));
                coleta.setNomeLocalOrigem((rst.getString("nome_local_origem")));
                coleta.setPesoTotalKg((rst.getBigDecimal("peso_total_kg")));
                coleta.setDataFim((rst.getTimestamp("data_fim")).toLocalDateTime());
            }

            coletas.add(coleta);

        } catch (SQLException e) {
            e.getMessage();
        } finally {
            conn.desconectar(conexao);
        }

        return coletas;
    }

    // Lista apenas as coletas que estão em andamento.
    public ArrayList<Coleta> coletasEmAndamento(){

        // Declaração

        ArrayList<Coleta> coletas = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT id_coleta, tipo, nome_local_origem, id_cooperado_responsavel, data_inicio, status FROM coleta";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            Coleta coleta = null;
            while(rst.next()){
                coleta = new Coleta();

                coleta.setIdColeta(rst.getLong("id_coleta"));
                coleta.setTipo(rst.getString("tipo"));
                coleta.setNomeLocalOrigem(rst.getString("nome_local_origem"));
                coleta.setIdCooperadoResponsavel(rst.getLong("id_cooperado_responsavel"));
                coleta.setDataInicio(rst.getTimestamp("data_inicio"));
                coleta.setStatus(rst.getString("status"));
            }
            coletas.add(coleta);

        } catch (SQLException e) {
            e.getMessage();
        } finally {
            conn.desconectar(conexao);
        }
        return coletas;
    }

    /*Conferir peso total vs. soma dos materiais – campo de verificação que compara o peso total informado com a soma dos pesos por material.*/

    // === METODOS UPDATE ==============================================================================================

    // Inicia uma coleta agendada e registra o horário de início.
    public int iniciarColeta(Coleta coleta) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta\n" +
                "SET status = ?, peso_total_kg = ?, data_inicio = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, coleta.getStatus());
            pstmt.setBigDecimal(2, coleta.getPesoTotalKg());
            pstmt.setDate(3, Date.valueOf(String.valueOf(coleta.getDataInicio())));

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza os dados de uma coleta identificada pelo ID.
    public int atualizarColeta(Coleta coleta) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta\n" +
                "SET tipo = ?, status = ?," +
                "\n origem_entrega = ?,nome_local_origem = ?," +
                "data_agendada = ?, data_inicio = ?, data_fim = ?," +
                "peso_total_kg = ?, observacoes = ? " +
                "WHERE id_coleta = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza uma coleta externa com peso e horário de término.
    public int finalizarColeta(Coleta coleta) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta\n" +
                "SET status = ?, peso_total_kg = ?, data_fim = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, coleta.getStatus());
            pstmt.setBigDecimal(2, coleta.getPesoTotalKg());
            pstmt.setTimestamp(3, Timestamp.valueOf(coleta.getDataFim()));

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Finaliza uma entrega registrada na cooperativa.
    public int finalizacaoAutomatica(Coleta coleta) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta\n" +
                "SET status = ?, data_fim = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, "Concluida");
            pstmt.setTimestamp(3, Timestamp.valueOf(coleta.getDataFim()));

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Encerra o acompanhamento após a finalização da coleta.
    public int encerramentoRastreamento(Coleta coleta) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE coleta\n" +
                "SET status = ?, data_fim = ? WHERE id_coleta = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, "Concluida");
            pstmt.setTimestamp(3, Timestamp.valueOf(coleta.getDataFim()));

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Cancela uma coleta preservando seu registro para o histórico.
    public int excluirColeta(Coleta coleta) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM coleta\n" +
                "WHERE id_coleta = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(1,coleta.getIdColeta());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Consulta o último ID cadastrado na tabela de coletas.
    public long getUltimoIdColeta () {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "SELECT id_coleta " +
                "FROM coleta " +
                "ORDER BY coleta.id_coleta DESC LIMIT 1";

        // Executar PreparedStatement

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getLong("id_coleta");
            }
            else {
                return -1;
            }
        }
        catch (SQLException e) {
            return -1;
        } finally { conn.desconectar(conexao); } }

}