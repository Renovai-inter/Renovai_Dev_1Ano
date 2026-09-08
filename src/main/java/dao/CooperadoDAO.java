package dao;
import util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class CooperadoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public CooperadoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    public int cadastrarCooperado(String nome_completo, String nome_usuario, String senha_hash,
                                  int senha_temporaria, String email, String tipo_usuario, Date data_criacao){

        String sql = "INSERT INTO cooperado(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo, status, " +
                "data_cadastro) VALUES (?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);
            //valores esperados pela query
            pstm.setString(1, nome_completo);
            pstm.setString(2, nome_usuario);
            pstm.setString(3, senha_hash);
            pstm.setInt(4, senha_temporaria);
            pstm.setString(5, email);
            pstm.setString(6, tipo_usuario);
            pstm.setDate(7, new java.sql.Date(data_criacao.getTime()));
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



    // === METODOS UPDATE ==============================================================================================



    // === METODOS DELETE ==============================================================================================



}
