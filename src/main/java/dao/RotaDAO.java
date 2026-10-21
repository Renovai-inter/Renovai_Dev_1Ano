package dao;

import model.Rota;
import util.Conexao;
import util.Utilitario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RotaDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();
    private Utilitario utilitario = new Utilitario();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de rota.
    public RotaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra uma rota vinculada à cooperativa.
    public Rota cadastrarRota(Long idCooperativa, String nomeRota) {

        // Declaração

        Rota rota = new Rota();
        rota.setIdCooperativa(idCooperativa);
        rota.setNomeRota(nomeRota);
        if (rota.getIdCooperativa() == null || rota.getIdCooperativa() <= 0) {
            throw new IllegalArgumentException("idCooperativa deve ser positivo.");
        }
        long ultimoIdRota = utilitario.selecionarUltimoId("rota");
        long novoIdRota = ultimoIdRota == -1 ? 1 : ultimoIdRota + 1;
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO rota (id_rota, id_cooperativa, nome_rota) " +
                "VALUES (?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, novoIdRota);
            pstmt.setLong(2, rota.getIdCooperativa());
            pstmt.setString(3, rota.getNomeRota());
            int linhas = pstmt.executeUpdate();
            if (linhas == 0) {
                return null;
            }
            rota.setIdRota(novoIdRota);
            return rota;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar rota.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    // Lista as rotas cadastradas.
    public ArrayList<Rota> listarRotas() {

        // Declaração

        ArrayList<Rota> rotas = new ArrayList<>();
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "SELECT id_rota, id_cooperativa, nome_rota FROM rota";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Rota rota = new Rota();
                Long idRota = rs.getLong("id_rota");
                Long idCoop = rs.getLong("id_cooperativa");
                String nomeRota = rs.getString("nome_rota");
                rota.setIdRota(idRota);
                rota.setIdCooperativa(idCoop);
                rota.setNomeRota(nomeRota);
                rotas.add(rota);
            }
            return rotas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar rotas.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza os dados da rota identificada pelo ID.
    public Rota atualizarRota(Long idRota, Long idCooperativa, String nomeRota) {

        // Declaração

        Rota rota = new Rota();
        rota.setIdRota(idRota);
        rota.setIdCooperativa(idCooperativa);
        rota.setNomeRota(nomeRota);

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE rota SET id_cooperativa = ?, nome_rota = ? WHERE id_rota = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, rota.getIdCooperativa());
            pstmt.setString(2, rota.getNomeRota());
            pstmt.setLong(3, rota.getIdRota());
            return pstmt.executeUpdate() == 0 ? null : rota;

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar rota.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui a rota identificada pelo ID.
    public Rota excluirRota(Long idRota) {

        // Declaração

        Rota rota = new Rota();
        rota.setIdRota(idRota);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM rota WHERE id_rota = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, rota.getIdRota());
            return pstmt.executeUpdate() == 0 ? null : rota;

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir rota.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    }
