package dao;

import model.Coleta;
import util.Conexao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class ColetaDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public ColetaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    public int registrarColeta(String tipo, String status, String origem_entrega, String nome_local_origem, String observacoes) {

        String sql = "INSERT INTO coleta(id_coleta, id_cooperativa, tipo, status, id_rota, id_endereco_rota, id_cooperado_responsavel," +
                "origem_entrega, nome_local_origem, data_agendada, data_inicio, data_fim, peso_total_kg, observacoes) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            //valores esperados pela query
            pstm.setString(1, tipo);
            pstm.setString(2, status);
            pstm.setString(3, origem_entrega);
            pstm.setString(4, nome_local_origem);
            pstm.setString(5, observacoes);
            //executando a query
            pstm.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
        return 0;
    }


    // === METODOS READ ================================================================================================
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
                Timestamp data_inicio = rs.getTimestamp("data_inicio");
                Timestamp data_fim = rs.getTimestamp("data_fim");
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
                coleta.setDataFim(data_fim);
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


    // === METODOS DELETE ==============================================================================================


}

