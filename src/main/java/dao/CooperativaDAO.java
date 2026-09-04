package dao;

import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CooperativaDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public CooperativaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra uma nova cooperativa e todos os seus respectivos dados
    public String cadastrarCooperativa() {

        Connection conexao = conn.conectar();

        String sql = "INSERT INTO cooperativa " +
                "                (id_cooperativa, nome, cnpj," +
                "                        nome_publico, email_institucional," +
                "                        telefone_whatsapp, cep) VALUES" +
                "                (?, ?, ?, ?, ?, ?, ?)";

        String ultimo_id = "SELECT id_cooperativa FROM cooperativa ORDER BY id_cooperativa DESC LIMIT 1";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setInt(1, );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // Sobrecarga de cadastrarCooperativa: CNPJ opcional


    // Sobrecarga de cadastrarCooperativa, preenchimento de Endereco manual

    // === METODOS READ ================================================================================================

    // Static que retorna o último índice de ID das cooperativas
    private static int getUltimoId() {

        Connection conexao = conn.conectar();

        String sql = "SELECT id_cooperativa FROM cooperativa ORDER BY id_cooperativa DESC LIMIT 1";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            return rs.getInt("id_cooperativa");

        } catch (SQLException e) {

            return -1;

        }

    }

    // === METODOS UPDATE ==============================================================================================



    // === METODOS DELETE ==============================================================================================



}
