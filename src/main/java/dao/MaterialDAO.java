package dao;

import util.Conexao;
import util.Utilitario;
import model.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MaterialDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();
    private Utilitario utilitario = new Utilitario();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de material.
    public MaterialDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra um material com os campos exigidos pelo esquema.
    public Material cadastrarMaterial(String nome, String categoria) {

        Material material = new Material();
        material.setNome(nome);
        material.setCategoria(categoria);
        long ultimoId = utilitario.selecionarUltimoId("material");
        long novoId = ultimoId == -1 ? 1 : ultimoId + 1;

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO material " + "(id_material,"
                + "nome,categoria)" +
                "VALUES (?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, novoId);

            pstmt.setString(2, material.getNome());

            pstmt.setString(3, material.getCategoria());

            int linhas = pstmt.executeUpdate();
            if (linhas == 0) {
                return null;
            }
            material.setIdMaterial(novoId);
            return material;

        } catch (SQLException e) {
<<<<<<< HEAD
            return 0;
=======
            throw new RuntimeException("Falha ao cadastrar material.", e);
>>>>>>> origin/master
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
<<<<<<< HEAD
            e.printStackTrace();
            return new ArrayList<>();
=======
            throw new RuntimeException("Falha ao listar materiais.", e);
>>>>>>> origin/master
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza os dados do material identificado pelo ID.
    public Material atualizarMaterial(Long idMaterial, String nome, String categoria) {

        Material material = new Material();
        material.setIdMaterial(idMaterial);
        material.setNome(nome);
        material.setCategoria(categoria);

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

            return pstmt.executeUpdate() == 0 ? null : material;

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar material.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui o material identificado pelo ID.
    public Material excluirMaterial(Long idMaterial) {

        Material material = new Material();
        material.setIdMaterial(idMaterial);

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM material\n" +
                "WHERE id_material = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1,material.getIdMaterial());

            return pstmt.executeUpdate() == 0 ? null : material;

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir material.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

}
