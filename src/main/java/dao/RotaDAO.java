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

    public RotaDAO() {
    }

    // === METODOS CREATE ==============================================================================================
    public int cadastrarRota(Rota rota) {

        Connection conexao = conn.conectar();

        String sql = "INSERT INTO rota " +
                "(id_rota," +
                "id_cooperativa,nome_rota)" +
                "VALUES (?, ?, ?)";
        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

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
                return 0;
            }

            pstmt.setString(3, rota.getNomeRota());

            return pstmt.executeUpdate();
        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }
    }


    // === METODOS READ ================================================================================================
    public ArrayList<Rota> listarRotas() {

        ArrayList<Rota> rotas = new ArrayList<>();

        Connection conexao = conn.conectar();

        String sql = "SELECT " +
                "id_rota," +
                "id_cooperativa,nome_rota FROM rota";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

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
            return new ArrayList<>();
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================
    public int atualizarRota(Rota rota) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE rota\n" +
                "SET id_cooperativa = ?, nome_rota = ?\n" +
                "WHERE id_rota = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(1,rota.getIdCooperativa());
            pstmt.setString(2,rota.getNomeRota());
            pstmt.setLong(3,rota.getIdRota());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            return -1;

        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================
    public int excluirRota(Rota rota) {

        Connection conexao = conn.conectar();

        String sql = "DELETE FROM rota\n" +
                "WHERE id_rota = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);


            pstmt.setLong(1,rota.getIdRota());


            return pstmt.executeUpdate();

        } catch (SQLException e) {

            return -1;

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Retorna o último índice de ID das rotas
        public long getUltimoIdRota () {

            Connection conexao = conn.conectar();

            String sql =
                    "SELECT id_rota " +
                            "FROM rota " +
                            "ORDER BY rota.id_rota DESC LIMIT 1";

            try {

                PreparedStatement pstmt = conexao.prepareStatement(sql);

                ResultSet rs = pstmt.executeQuery();

                if  (rs.next()){
                    return rs.getLong("id_rota");
                }
                else {
                    return -1;
                }


            } catch (SQLException e) {

                return -1;

            } finally {
                conn.desconectar(conexao);
            }
        }
    }

