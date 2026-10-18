package dao;

import model.Cooperativa;
import model.Cooperado;
import util.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CooperativaDAO {

    // ATRIBUTOS

    private final Conexao conn = new Conexao();
    private final Utilitario utilitario = new Utilitario();
    private final CooperadoDAO cooperadoDAO = new CooperadoDAO();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de cooperativa.
    public CooperativaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra uma cooperativa e seu gestor inicial.
    public int[] cadastrarCooperativa(Cooperativa coop, Cooperado gestor, String senhaTemporariaGestor) {

        // Declaração

        Connection conexao = conn.conectar();
        String sql;

        // Comando SQL

        sql = (
                "INSERT INTO cooperativa " +
                    "(id_cooperativa, nome, cnpj, nome_publico, descricao_institucional, " +
                    "logo_url, email_institucional, telefone_whatsapp, cep, endereco, cidade, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        try {

            // Executar PreparedStatement

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(0, utilitario.selecionarUltimoId("cooperativa"));

            pstmt.setString(1, coop.getNome().toUpperCase().trim());

            String cnpj = utilitario.filtrarCnpj(coop.getCnpj());
            if (cnpj.equals("invalido")) {
                pstmt.setNull(3, Types.VARCHAR);
            } else {
                pstmt.setString(3, cnpj);
            }

            pstmt.setString(4, coop.getNomePublico().toUpperCase().trim());

            if (coop.getDescricaoInstitucional().isEmpty()) {
                pstmt.setNull(5, Types.LONGVARCHAR);
            } else {
                pstmt.setString(5, coop.getDescricaoInstitucional());
            }

            if (coop.getLogoUrl().isEmpty()) {
                pstmt.setNull(6, Types.VARCHAR);
            } else {
                pstmt.setString(6, coop.getLogoUrl().trim());
            }

            String emailInstucional = utilitario.filtrarEmailInstitucional(coop.getEmailInstitucional());
            if (emailInstucional.equals("invalido")) {
                pstmt.setNull(7, Types.VARCHAR);
            } else {
                pstmt.setString(7, coop.getEmailInstitucional());
            }

            String telefoneWhatsapp = utilitario.filtrarTelefoneWhatsapp(coop.getTelefoneWhatsapp());
            if (telefoneWhatsapp.equals("invalido")) {
                pstmt.setNull(8, Types.VARCHAR);
            } else {
                pstmt.setString(8, telefoneWhatsapp);
            }

            String cep = utilitario.filtrarCep(coop.getCep());
            if (cep.equals("invalido")) {
                pstmt.setNull(9, Types.VARCHAR);
            } else {
                pstmt.setString(9, cep);
            }

            String endereco = utilitario.filtrarEndereco(coop.getEndereco());
            if (endereco.equals("invalido")) {
                pstmt.setNull(10, Types.VARCHAR);
            } else {
                pstmt.setString(10, endereco);
            }

            pstmt.setString(11, coop.getCidade().toLowerCase().trim());

            pstmt.setString(12, utilitario.validarUf(coop.getEstado()));

            // Execução e retorno

            int[] retornoGestor = cooperadoDAO.cadastrarGestor(coop, gestor.getNomeCompleto(), gestor.getNomeUsuario(), gestor.getEmail(), senhaTemporariaGestor);
            int retornoCooperativa = pstmt.executeUpdate();

            return new int[]{retornoCooperativa, retornoGestor[0], retornoGestor[1]};

        } catch (SQLException sqle) {
            return new int[]{0, 0, 0};
        } finally {
            conn.desconectar(conexao);
        }

    }

    // === METODOS READ ================================================================================================

    // Lista as cooperativas cadastradas com dados resumidos.
    public ArrayList<Cooperativa> exibirCooperativas() {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop;";

        try {

            // Executar PreparedStatement

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            // todo: formatatar saídas: CNPJ
            while (rs.next()) {

                cooperativas.add(new Cooperativa(
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("status"),
                        rs.getInt("cooperados"),
                        LocalDateTime.parse(rs.getString( "cadastro"))
                ));

            }

            return cooperativas;

        } catch (SQLException e) {
            return null;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // Busca cooperativas pelo nome ou código informado.
    public ArrayList<Cooperativa> buscarCooperativasPorNomeOuCodigo(String busca) {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop" +
                "WHERE coop.nome LIKE '%" + busca + "%' OR coop.cnpj LIKE '%" + busca + "%';";

        try {

            // Executar PreparedStatement

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            // todo: formatatar saídas: CNPJ
            while (rs.next()) {

                cooperativas.add(new Cooperativa(
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("status"),
                        rs.getInt("cooperados"),
                        LocalDateTime.parse(rs.getString( "cadastro"))
                ));

            }

            return cooperativas;

        } catch (SQLException e) {
            return null;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // Lista as cooperativas da cidade informada.
    public ArrayList<Cooperativa> filtrarCooperativasPorCidade(String cidadeBusca) {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop" +
                "WHERE coop.nome LIKE '%" + cidadeBusca + "%' OR coop.cnpj LIKE '%" + cidadeBusca + "%';";

        try {

            // Executar PreparedStatement

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            // todo: formatatar saídas: CNPJ
            while (rs.next()) {

                cooperativas.add(new Cooperativa(
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("status"),
                        rs.getInt("cooperados"),
                        LocalDateTime.parse(rs.getString( "cadastro"))
                ));

            }

            return cooperativas;

        } catch (SQLException e) {
            return null;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // Lista as cooperativas do estado informado.
    public ArrayList<Cooperativa> filtrarCooperativasPorEstado(String estadoBusca) {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop" +
                "WHERE coop.nome LIKE '%" + estadoBusca + "%' OR coop.cnpj LIKE '%" + estadoBusca + "%';";

        try {

            // Executar PreparedStatement

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            // todo: formatatar saídas: CNPJ
            while (rs.next()) {

                cooperativas.add(new Cooperativa(
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("status"),
                        rs.getInt("cooperados"),
                        LocalDateTime.parse(rs.getString( "cadastro"))
                ));

            }

            return cooperativas;

        } catch (SQLException e) {
            return null;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // Lista as cooperativas com o status informado.
    public ArrayList<Cooperativa> filtrarCooperativasPorStatus(String statusBusca) {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop" +
                "WHERE coop.nome LIKE '%" + statusBusca + "%' OR coop.cnpj LIKE '%" + statusBusca + "%';";

        try {

            // Executar PreparedStatement

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            // todo: formatatar saídas: CNPJ
            while (rs.next()) {

                cooperativas.add(new Cooperativa(
                        rs.getString("nome"),
                        rs.getString("cnpj"),
                        rs.getString("cidade"),
                        rs.getString("estado"),
                        rs.getString("status"),
                        rs.getInt("cooperados"),
                        LocalDateTime.parse(rs.getString( "cadastro"))
                ));

            }

            return cooperativas;

        } catch (SQLException e) {
            return null;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // todo: exibir uma única cooperativa e todos os seus dados

    // === METODOS UPDATE ==============================================================================================

    // Atualiza um campo permitido da cooperativa identificada.
    public int atualizarCooperativa(Cooperativa coop, String campo) {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // Comando SQL

        String sql = "UPDATE cooperativa " +
                "SET " + campo.toLowerCase() + " WHERE id_cooperativa = " + coop.getIdCooperativa();

        try {

            // Executar PreparedStatement

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // === METODOS DELETE ==============================================================================================

    // Exclui a cooperativa identificada pelo ID.
    public int excluirCooperativa(Cooperativa coop) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM COOPERATIVA WHERE id_cooperativa = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(1, coop.getIdCooperativa());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            return -1;

        }

    }

}
