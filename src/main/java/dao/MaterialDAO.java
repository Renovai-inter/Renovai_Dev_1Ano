package dao;

import util.Conexao;
import model.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
    public ArrayList<Material> listarMateriais() {

        ArrayList<Material> materiais = new ArrayList<>();

        Connection conexao = conn.conectar();

        String sql = "SELECT " +
                "id_material," +
                "nome,categoria FROM material";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()){
                Material material = new Material();
                Long idMaterial = rs.getLong("id_material");
                String nomeMaterial = rs.getString("nome");
                String categoria = rs.getString("categoria");
                material.setIdMaterial(idMaterial);
                material.setNome(nomeMaterial);
                material.setCategoria(categoria);
                materiais.add(material);
            }
            return materiais;
        }catch (SQLException e) {
            return new ArrayList<>();
        } finally {
            conn.desconectar(conexao);
        }
    }


    // === METODOS UPDATE ==============================================================================================
    public int atualizarRota(Material material) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE material\n" +
                "SET nome = ?, categoria = ?\n" +
                "WHERE id_material = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1,material.getNome());
            pstmt.setString(2,material.getCategoria());
            pstmt.setLong(3,material.getIdMaterial());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            return -1;

        } finally {
            conn.desconectar(conexao);
        }
    }



    // === METODOS DELETE ==============================================================================================
    public int excluirRota(Material material) {

        Connection conexao = conn.conectar();

        String sql = "DELETE FROM material\n" +
                "WHERE id_material = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);


            pstmt.setLong(1,material.getIdMaterial());


            return pstmt.executeUpdate();

        } catch (SQLException e) {

            return -1;

        } finally {
            conn.desconectar(conexao);
        }
    }

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
