package dao;

import util.ConexaoBD;
import util.Utilitario;
import model.principais.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MaterialDAO {

    // ATRIBUTOS

    private ConexaoBD conn = new ConexaoBD();
    private Utilitario utilitario = new Utilitario();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de material.
    public MaterialDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra um material com os campos exigidos pelo esquema.
    public int cadastrarMaterial(Material material) {

        // Declaração

        if (material == null || material.getNome() == null
                || material.getCategoria() == null || material.getOrigem() == null) {
            throw new IllegalArgumentException("Nome, categoria e origem são obrigatórios.");
        }
        long ultimoId = utilitario.selecionarUltimoId("material");
        long novoId;
        if (ultimoId == -1) {
            novoId = 1;
        } else {
            novoId = ultimoId + 1;
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO material (id_material, nome, categoria, \"Origem\") " +
                "VALUES (?, ?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, novoId);
            pstmt.setString(2, material.getNome());
            pstmt.setString(3, material.getCategoria());
            pstmt.setString(4, material.getOrigem());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                material.setIdMaterial(novoId);
            }
            return linhas;
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

        String sql = "SELECT id_material, nome, categoria, \"Origem\" FROM material";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Material material = new Material();
                Long idMaterial = rs.getLong("id_material");
                String nomeMaterial = rs.getString("nome");
                String categoria = rs.getString("categoria");
                material.setIdMaterial(idMaterial);
                material.setNome(nomeMaterial);
                material.setCategoria(categoria);
                material.setOrigem(rs.getString("Origem"));
                materiais.add(material);
            }
            return materiais;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar materiais.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza os dados do material identificado pelo ID.
    public int atualizarMaterial(Material material) {

        // Declaração

        if (material == null || material.getNome() == null || material.getCategoria() == null) {
            throw new IllegalArgumentException("Nome e categoria são obrigatórios.");
        }
        if (material.getIdMaterial() == null || material.getIdMaterial() <= 0) {
            throw new IllegalArgumentException("idMaterial deve ser positivo.");
        }

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE material SET nome = ?, categoria = ? WHERE id_material = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, material.getNome());
            pstmt.setString(2, material.getCategoria());
            pstmt.setLong(3, material.getIdMaterial());
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

        if (material == null || material.getIdMaterial() == null || material.getIdMaterial() <= 0) {
            throw new IllegalArgumentException("idMaterial deve ser positivo.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM material WHERE id_material = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, material.getIdMaterial());
            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir material.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

}
