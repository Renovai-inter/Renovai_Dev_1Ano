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

    public int cadastrarColeta(Coleta coleta) {

        Connection conexao = conn.conectar();

        String sql = "INSERT INTO coleta " + "(id_coleta, id_cooperativa," +
                "tipo, status, id_rota, id_endereco_rota, id_cooperado_responsavel,"
                + "origem_entrega, nome_local_origem, data_agendada, data_inicio" +
                ",data_fim, peso_total_kg, observacoes)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            long ultimoIdColeta = getUltimoIdColeta();

            if (ultimoIdColeta == -1) {
                pstmt.setLong(1,1);
            } else {
                pstmt.setLong(1,ultimoIdColeta + 1);
            }

            pstmt.setString(2, material.getNome());

            pstmt.setString(3, material.getCategoria());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }
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

            pstmt.setString(1,coleta.getTipo());
            pstmt.setString(2,coleta.getStatus());
            pstmt.setString(3,coleta.getOrigemEntrega());
            pstmt.setString(4,coleta.getNomeLocalOrigem());
            pstmt.setDate(5, new java.sql.Date(coleta.getDataAgendada().getTime()));
            pstmt.setTimestamp(6,coleta.getDataInicio());
            pstmt.setTimestamp(7,coleta.getDataFim());
            pstmt.setBigDecimal(8,coleta.getPesoTotalKg());
            pstmt.setString(9,coleta.getObservacoes());
            pstmt.setLong(10,coleta.getIdColeta());


            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }


    // === METODOS DELETE ==============================================================================================
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
    // Retorna o último índice de ID dos materiais
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

