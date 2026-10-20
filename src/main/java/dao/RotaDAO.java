package dao;

import model.Rota;
import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RotaDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de rota.
    public RotaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra uma rota vinculada à cooperativa.
    public int cadastrarRota(Rota rota) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO rota " +
                "(id_rota," +
                "id_cooperativa,nome_rota)" +
                "VALUES (?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            long ultimoIdRota = getUltimoIdRota();

            // Verifica se o ultimoId foi encontrado (-1) se não
            if (ultimoIdRota == -1) {
                pstmt.setLong(1,1);
            } else {
                pstmt.setLong(1, ultimoIdRota + 1);
            }

            Long IdCooperativa = rota.getIdCooperativa();

            // Verifica se o ultimoIdRota foi encontrado (-1) se não

            if (IdCooperativa != null && IdCooperativa>0){
                pstmt.setLong(2, IdCooperativa);
            }else {
                throw new IllegalArgumentException("idCooperativa deve ser positivo.");
            }

            pstmt.setString(3, rota.getNomeRota());

            return pstmt.executeUpdate();
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

        String sql = "SELECT " +
                "id_rota," +
                "id_cooperativa,nome_rota FROM rota";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()){
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
        }catch (SQLException e) {
            throw new RuntimeException("Falha ao listar rotas.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza os dados da rota identificada pelo ID.
    public int atualizarRota(Rota rota) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE rota\n" +
                "SET id_cooperativa = ?, nome_rota = ?\n" +
                "WHERE id_rota = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1,rota.getIdCooperativa());
            pstmt.setString(2,rota.getNomeRota());
            pstmt.setLong(3,rota.getIdRota());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar rota.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui a rota identificada pelo ID.
    public int excluirRota(Rota rota) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM rota\n" +
                "WHERE id_rota = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1,rota.getIdRota());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir rota.", e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Consulta o último ID cadastrado na tabela de rotas.
        public long getUltimoIdRota () {

        // Declaração

            Connection conexao = conn.conectar();

        // Comando SQL

            String sql =
                    "SELECT id_rota " +
                            "FROM rota " +
                            "ORDER BY rota.id_rota DESC LIMIT 1";

        // Executar PreparedStatement

            try (PreparedStatement pstmt = conexao.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                if  (rs.next()){
                    return rs.getLong("id_rota");
                }
                else {
                    return -1;
                }

            } catch (SQLException e) {
                throw new RuntimeException("Falha ao consultar o último ID de rota.", e);

            } finally {
                conn.desconectar(conexao);
            }
        }
    }
