package dao;

import model.Cooperativa;
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

    // === METODOS DE VALIDACAO E REGEX ================================================================================

    // Filtrar o CNPJ para retornar apenas valores numéricos
    private static String validarCnpj(String cnpj) {

        // Tratamento inicial
        cnpj.trim();

        // Retirar "." e "-"
        cnpj.replace(".", "");
        cnpj.replace("-", "");

        // Verificar formato (regex) correto
        if (cnpj.matches("^\\d{11}$")) {
            return cnpj;
        } else {
            return "cnpj invalido";
        }

    }

    // === OUTROS METODOS ==============================================================================================

    // Retorna o último índice de ID das cooperativas
    public long getUltimoIdMaterial () {

        Connection conexao = conn.conectar();

        String sql = "SELECT id_cooperativa " +
                "FROM cooperativa " +
                "ORDER BY 1 DESC LIMIT 1";

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getLong("id_material");
            }
            else {
                return -1;
            }
        }
        catch (SQLException e) {
            return -1;
        } finally { conn.desconectar(conexao); } }

    // === METODOS CREATE ==============================================================================================

    // Metodo de cadastrar cooperativa | todo: implementar API de buscar endereço por CEP
    public int cadastrarCooperativa(Cooperativa cooperativa) {

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

    // === METODOS READ ================================================================================================



    // === METODOS UPDATE ==============================================================================================



    // === METODOS DELETE ==============================================================================================



}
