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
    public int cadastrarCooperativa(String nome, String cnpj, String emailInstitucional, String telefoneWhatsapp,
                                       String cep) {

        Connection conexao = conn.conectar();

        String sql =
                "INSERT INTO cooperativa " +
                    "(id_cooperativa, nome, cnpj, " +
                    "nome_publico, email_institucional, telefone_whatsapp, " +
                    "cep, endereco, cidade, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            int ultimoId = getUltimoId();

            // Verifica se o ultimoId foi encontrado (-1) se não
            if (ultimoId == -1) {
                return 0;
            } else {
                pstmt.setInt(1, getUltimoId()+1);
            }
            pstmt.setString(2, nome);
            pstmt.setString(3, cnpj);
            pstmt.setString(4, nome);
            pstmt.setString(5, emailInstitucional);
            pstmt.setString(6, telefoneWhatsapp);
            pstmt.setString(7, cep);
            pstmt.setString(8, "ENDERECO");
            pstmt.setString(9, "CIDADE");
            pstmt.setString(10, "ESTADO");

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
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
