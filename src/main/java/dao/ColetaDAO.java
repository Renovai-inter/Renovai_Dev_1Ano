package dao;

import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ColetaDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public ColetaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    public int registrarColeta(String tipo, String status, String origem_entrega, String nome_local_origem, String observacoes){

        String sql = "INSERT INTO coleta(id_coleta, id_cooperativa, tipo, status, id_rota, id_endereco_rota, id_cooperado_responsavel," +
                "origem_entrega, nome_local_origem, data_agendada, data_inicio, data_fim, peso_total_kg, observacoes) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        try{
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

    }


    // === METODOS READ ================================================================================================



    // === METODOS UPDATE ==============================================================================================



    // === METODOS DELETE ==============================================================================================




