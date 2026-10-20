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

    // Inicializa o acesso aos dados de material.
    public MaterialDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra um material com os campos exigidos pelo esquema.
    public int cadastrarMaterial(Material material) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO material " + "(id_material,"
                + "nome,categoria)" +
                "VALUES (?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
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
            throw new RuntimeException("Falha ao cadastrar material.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    // Lista os materiais cadastrados.
    public ArrayList<Material> listarMateriais() {

        // Declaração

        ArrayList<Material> materiais = new ArrayList<>();

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "SELECT " +
                "id_material," +
                "nome,categoria FROM material";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
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
            throw new RuntimeException("Falha ao listar materiais.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza os dados do material identificado pelo ID.
    public int atualizarMaterial(Material material) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE material\n" +
                "SET nome = ?, categoria = ?\n" +
                "WHERE id_material = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1,material.getNome());
            pstmt.setString(2,material.getCategoria());
            pstmt.setLong(3,material.getIdMaterial());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar material.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui o material identificado pelo ID.
    public int excluirMaterial(Material material) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM material\n" +
                "WHERE id_material = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1,material.getIdMaterial());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir material.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Consulta o último ID cadastrado na tabela de materiais.
    public long getUltimoIdMaterial () {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "SELECT id_material " +
                "FROM material " +
                "ORDER BY material.id_material DESC LIMIT 1";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()){
                return rs.getLong("id_material");
            }
            else {
                return -1;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException("Falha ao consultar o último ID de material.", e);
        } finally { conn.desconectar(conexao); } }

}
