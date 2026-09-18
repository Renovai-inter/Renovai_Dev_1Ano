package dao;

import model.Coleta;
import util.Conexao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.sql.Date;

public class ColetaDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public ColetaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    /*Nova coleta (Coleta externa) – cadastro de uma nova coleta, selecionando destino (rota cadastrada ou endereço específico), data, cooperado responsável (opcional) e observações (opcional). Ao salvar, entra com status "Agendada".
*/

    public int cadastrarColeta(Coleta coleta) {

        Connection conexao = conn.conectar();

        String sql = "INSERT INTO coleta " + "(id_cooperativa," +
                "tipo, id_rota, id_endereco_rota, id_cooperado_responsavel,"
                + "data_agendada, observacoes)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

    /*Nova coleta (Entrega na cooperativa) – registro de uma entrega feita diretamente na cooperativa, informando origem
     (empresa, condomínio, escola, pessoa física, ecoponto, outro), nome do local/pessoa, data (preenchida automaticamente, editável) e
     observações.*/

    public int registroNaCooperativa(Coleta coleta) {
        Connection conexao = conn.conectar();

        String sql = "INSERT INTO coleta(id_coleta, origem_entrega, nome_local_origem, data_fim, observacoes) VALUES (?,?,?,?,?,?)";

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

    /* Cadastrar local/pessoa na hora – durante o registro de uma entrega, se o local ainda não existir nos endereços, o gestor pode cadastrá-lo ali mesmo.*/

    public int cadastrarOrigem(Coleta coleta){
        Connection conexao = conn.conectar();

        String sql = "INSERT INTO coleta(nome_local_origem, origem_entrega) VALUES(?,?)";

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setString(1, coleta.getNomeLocalOrigem());
            pstm.setString(2, coleta.getOrigemEntrega());


        } catch (SQLException e) {
            e.getMessage();
        }
        return 0;
    }

        /*Registrar peso total coletado/recebido – informar o peso total (obrigatório) ao finalizar uma coleta externa ou registrar uma entrega.*/

    public double registrarPesoTotal(Coleta coleta){
        Connection conexao = conn.conectar();

        String sql = "INSERT INTO coleta(peso_total_kg) VALUES (?)";

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setBigDecimal(1, coleta.getPesoTotalKg());

        } catch (SQLException sqle){
            sqle.getMessage();
        }

        return 0;
    }

    /*Registrar quantidade por tipo de material – opcionalmente, distribuir o peso total entre os materiais (papel, papelão, PET, alumínio, vidro etc.).*/

    public int QuantidadeTipoMaterial(Coleta coleta){
        Connection conexao = conn.conectar();

        String sql = "INSERT INTO coleta(tipo, peso_total_kg) VALUES(?,?)";

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setString(1, coleta.getTipo());
            pstm.setBigDecimal(2, coleta.getPesoTotalKg());

        } catch (SQLException sqle) {
            sqle.getMessage();
        }
        return 0;
    }


    // === METODOS READ ================================================================================================

    /*Visualizar coletas em andamento – lista na parte superior da tela com código, tipo, local, cooperado responsável, horário de início e status.
Rastrear coleta – visualizar em tempo real a localização da equipe, rota planejada, percurso já realizado, pontos visitados e restantes, e endereço da próxima parada (disponível apenas para coletas externas em andamento).
Visualizar histórico de coletas – lista de todas as coletas registradas (código, tipo, responsável, data, status), da mais recente para a mais antiga.
Conferir peso total vs. soma dos materiais – campo de verificação que compara o peso total informado com a soma dos pesos por material.*/

    public ArrayList<Coleta> listarColetas() {

        ArrayList<Coleta> coletas = new ArrayList<>();

        Connection conexao = conn.conectar();

        String sql = "SELECT " +
                "id_coleta, id_cooperativa," +
                "tipo,status,id_rota," + "id_endereco_rota,id_cooperado_responsavel," +
                "origem_entrega,nome_local_origem,data_agendada,data_inicio,data_fim" +
                ",peso_total_kg,observacoes " +
                "FROM coleta";

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


    // === METODOS UPDATE ==============================================================================================

    /*Iniciar coleta – altera o status de "Agendada" para "Em andamento", passando a aparecer na seção de coletas em andamento.
Finalizar coleta (Coleta externa) – altera o status de "Em andamento" para "Concluída", após o registro do peso total e, opcionalmente, da distribuição por material.
Finalização automática (Entrega na cooperativa) – ao salvar, o status é definido automaticamente como "Concluída" (não passa por "Agendada" nem "Em andamento").
Encerramento automático do rastreamento – ao finalizar a coleta, o compartilhamento de localização é interrompido automaticamente.*/

    public int atualizarColeta(Coleta coleta) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE coleta\n" +
                "SET tipo = ?, status = ?," +
                "\n origem_entrega = ?,nome_local_origem = ?," +
                "data_agendada = ?, data_inicio = ?, data_fim = ?," +
                "peso_total_kg = ?, observacoes = ? " +
                "WHERE id_coleta = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);


            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }


    // === METODOS DELETE ==============================================================================================

    /*Cancelar coleta – o documento cita o status "Cancelada" (coleta cancelada antes da conclusão), mas não detalha a ação/fluxo específico de cancelamento.*/
    public int excluirColeta(Coleta coleta) {

        Connection conexao = conn.conectar();

        String sql = "DELETE FROM coleta\n" +
                "WHERE id_coleta = ?";

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
    // Retorna o último índice de ID das coletas
    public long getUltimoIdColeta () {

        Connection conexao = conn.conectar();

        String sql = "SELECT id_coleta " +
                "FROM coleta " +
                "ORDER BY coleta.id_coleta DESC LIMIT 1";

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

