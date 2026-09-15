package dao;

import model.Material;
import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ColetaDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public ColetaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    public int registrarColeta(String tipo, String status, String origem_entrega, String nome_local_origem, String observacoes){

        String sql = "INSERT INTO coleta(id_coleta, id_cooperativa, tipo, status, id_rota, id_endereco_rota, id_cooperado_responsavel," +
                "origem_entrega, nome_local_origem, data_agendada, data_inicio, data_fim, peso_total_kg, observacoes) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);
            //valores esperados pela query
            pstm.setString(1, tipo);
            pstm.setString(2, status);
            pstm.setString(3, origem_entrega);
            pstm.setString(4, nome_local_origem);
            pstm.setString(5, observacoes);
            //executando a query
            pstm.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
        return 0;
    }

    }


    // === METODOS READ ================================================================================================
    public ArrayList<Material> listarColetas() {

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



    // === METODOS DELETE ==============================================================================================




