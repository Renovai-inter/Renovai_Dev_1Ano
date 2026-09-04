package dao;

import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CooperativaDAO {

    // ATRIBUTOS

    private static Conexao conn = new Conexao();

    // CONSTRUTOR

    public CooperativaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastrar Cooperativa 1. CNPJ definido. Endereço automático
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
        } finally {
            conn.desconectar(conexao);
        }

    }

    // Cadastrar Cooperativa 2. CNPJ indefinido. Endereço automático
    public int cadastrarCooperativa(String nome, String emailInstitucional, String telefoneWhatsapp,
                                    String cep) {

        Connection conexao = conn.conectar();

        String sql =
                "INSERT INTO cooperativa " +
                        "(id_cooperativa, nome, " +
                        "nome_publico, email_institucional, telefone_whatsapp, " +
                        "cep, endereco, cidade, estado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            pstmt.setString(3, nome);
            pstmt.setString(4, emailInstitucional);
            pstmt.setString(5, telefoneWhatsapp);
            pstmt.setString(6, cep);
            pstmt.setString(7, "ENDERECO");
            pstmt.setString(8, "CIDADE");
            pstmt.setString(9, "ESTADO");

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // Cadastrar Cooperativa 3. CNPJ definido. Endereço manual
    public int cadastrarCooperativa(String nome, String cnpj, String emailInstitucional, String telefoneWhatsapp,
                                    String cep, String endereco, String cidade, String estado) {

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
            pstmt.setString(8, endereco);
            pstmt.setString(9, cidade);
            pstmt.setString(10, estado);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // Cadastrar Cooperativa 4. CNPJ indefinido. Endereço manual
    public int cadastrarCooperativa(String nome, String emailInstitucional, String telefoneWhatsapp,
                                    String cep, String endereco, String cidade, String estado) {

        Connection conexao = conn.conectar();

        String sql =
                "INSERT INTO cooperativa " +
                        "(id_cooperativa, nome, " +
                        "nome_publico, email_institucional, telefone_whatsapp, " +
                        "cep, endereco, cidade, estado) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            pstmt.setString(3, nome);
            pstmt.setString(4, emailInstitucional);
            pstmt.setString(5, telefoneWhatsapp);
            pstmt.setString(6, cep);
            pstmt.setString(7, endereco);
            pstmt.setString(8, cidade);
            pstmt.setString(9, estado);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // === METODOS READ ================================================================================================

    // Static que retorna o último índice de ID das cooperativas
    public int getUltimoId() {

        Connection conexao = conn.conectar();

        String sql =
                "SELECT id_cooperativa " +
                "FROM cooperativa " +
                        "ORDER BY id_cooperativa DESC LIMIT 1";

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
