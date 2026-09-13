package dao;

import util.Conexao;
import model.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MaterialDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public MaterialDAO() {
    }

    // === METODOS CREATE ==============================================================================================
    public int cadastrarMaterial(Material material) {

        Connection conexao = conn.conectar();

        String sql = "INSERT INTO material " + "(id_material,"
                + "nome,categoria)" +
                "VALUES (?, ?, ?)";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            long ultimoId = getUltimoIdMaterial();

            if (ultimoId == -1) {
                pstmt.setLong(1,1);
            } else {
                pstmt.setLong(1,ultimoId + 1);
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



    // === METODOS UPDATE ==============================================================================================



    // === METODOS DELETE ==============================================================================================

    // Retorna o último índice de ID dos materiais
    public long getUltimoIdMaterial () {

        Connection conexao = conn.conectar();

        String sql = "SELECT id_material " +
                "FROM material " +
                "ORDER BY material.id_material DESC LIMIT 1";

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

}
