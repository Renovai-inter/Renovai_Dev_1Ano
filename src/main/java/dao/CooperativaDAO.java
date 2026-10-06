package dao;

import model.Cooperativa;
import util.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CooperativaDAO {

    // ATRIBUTOS

    private static Conexao conn = new Conexao();
    private static Validadora validadora = new Validadora();

    // CONSTRUTOR

    public CooperativaDAO() {
    }

    // === METODOS DE FILTRO PARA NUMERICO =============================================================================

    // todo: reestruturar filtragem

    // todo: formatação de datas

    // === OUTROS METODOS ==============================================================================================

    // Retorna o último índice de ID das cooperativas
    public long getUltimoIdCooperativa() {

        Connection conexao = conn.conectar();

        String sql = "SELECT id_cooperativa " +
                "FROM cooperativa " +
                "ORDER BY 1 DESC LIMIT 1";

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getLong("id_cooperativa");
            }
            else {
                return -1;
            }
        }
        catch (SQLException e) {
            return -1;
        } finally { conn.desconectar(conexao); } }

    // === METODOS CREATE ==============================================================================================

    // Metodo de cadastrar cooperativa | todo: implementar API de buscar endereço por CEP
    public int cadastrarCooperativa(Cooperativa coop) {

        // DECLARACAO

        Connection conexao = conn.conectar();
        String sql;

        // COMANDO SQL PADRÃO

        // todo: ver possível geração de link de whatsapp automático pelo numero usando API
        // Caso afirmativo, adicionar link_whatsapp no comando e statement

        sql = (
                "INSERT INTO cooperativa " +
                    "(id_cooperativa, nome, cnpj, nome_publico, descricao_institucional, " +
                    "logo_url, email_institucional, telefone_whatsapp, cep, endereco, cidade, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        try {

            // EXECUTAR PREPARED STATEMENT E PREENCHER PLACEHOLDERS

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(0, getUltimoIdCooperativa());

            pstmt.setString(1, coop.getNome().toLowerCase());

            String cnpj = validadora.filtrarCnpj(coop.getCnpj());
            if (cnpj.equals("invalido")) {
                pstmt.setNull(3, Types.VARCHAR);
            } else {
                pstmt.setString(3, cnpj);
            }

            pstmt.setString(4, coop.getNomePublico());

            if (coop.getDescricaoInstitucional().isEmpty()) {
                pstmt.setNull(5, Types.LONGVARCHAR);
            } else {
                pstmt.setString(5, coop.getDescricaoInstitucional());
            }

            if (coop.getLogoUrl().isEmpty()) {
                pstmt.setNull(6, Types.VARCHAR);
            } else {
                pstmt.setString(6, coop.getLogoUrl());
            }

            String emailInstucional = validadora.filtrarEmailInstitucional(coop.getEmailInstitucional());
            if (emailInstucional.equals("invalido")) {
                pstmt.setNull(7, Types.VARCHAR);
            } else {
                pstmt.setString(7, coop.getEmailInstitucional());
            }

            String telefoneWhatsapp = validadora.filtrarTelefoneWhatsapp(coop.getTelefoneWhatsapp());
            if (telefoneWhatsapp.equals("invalido")) {
                pstmt.setNull(8, Types.VARCHAR);
            } else {
                pstmt.setString(8, telefoneWhatsapp);
            }

            String cep = validadora.filtrarCep(coop.getCep());
            if (cep.equals("invalido")) {
                pstmt.setNull(9, Types.VARCHAR);
            } else {
                pstmt.setString(9, cep);
            }

            String endereco = validadora.filtrarEndereco(coop.getEndereco());
            if (endereco.equals("invalido")) {
                pstmt.setNull(10, Types.VARCHAR);
            } else {
                pstmt.setString(10, endereco);
            }

            pstmt.setString(11, coop.getCidade().trim().toLowerCase());

            pstmt.setString(12, validarUf(coop.getEstado()));

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }

        // todo: CADASTRO DE GESTOR EMBUTIDO

    }

    // === METODOS READ ================================================================================================

    // Exibe todas as cooperativas no banco - sem filtros ou buscas
    public ArrayList<Cooperativa> exibirCooperativas() {

        // DECLARACAO

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // COMANDO SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop;";

        try {

            // EXECUTAR PREPARED STATEMENT E RODAR O RESULT SET

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

    // Exibe as cooperativas filtradas por nome ou código
    public ArrayList<Cooperativa> buscarCooperativasPorNomeOuCodigo(String busca) {

        // DECLARACAO

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // COMANDO SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop" +
                "WHERE coop.nome LIKE '%" + busca + "%' OR coop.cnpj LIKE '%" + busca + "%';";

        try {

            // EXECUTAR PREPARED STATEMENT E RODAR O RESULT SET

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

    // Filtrar Cooperativas por Cidade
    public ArrayList<Cooperativa> filtrarCooperativasPorCidade(String cidadeBusca) {

        // DECLARACAO

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // COMANDO SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop" +
                "WHERE coop.nome LIKE '%" + cidadeBusca + "%' OR coop.cnpj LIKE '%" + cidadeBusca + "%';";

        try {

            // EXECUTAR PREPARED STATEMENT E RODAR O RESULT SET

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

    // Filtrar Cooperativas por Estado (UF)
    public ArrayList<Cooperativa> filtrarCooperativasPorEstado(String estadoBusca) {

        // DECLARACAO

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // COMANDO SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop" +
                "WHERE coop.nome LIKE '%" + estadoBusca + "%' OR coop.cnpj LIKE '%" + estadoBusca + "%';";

        try {

            // EXECUTAR PREPARED STATEMENT E RODAR O RESULT SET

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

    // Filtrar Cooperativas por Status
    public ArrayList<Cooperativa> filtrarCooperativasPorStatus(String statusBusca) {

        // DECLARACAO

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // COMANDO SQL

        String sql = "SELECT" +
                "    coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
                "    (select coalesce(count(c.*), 0) from cooperado c where c.id_cooperativa = coop.id_cooperativa) as cooperados, " +
                "    coop.data_cadastro as cadastro " +
                "FROM cooperativa coop" +
                "WHERE coop.nome LIKE '%" + statusBusca + "%' OR coop.cnpj LIKE '%" + statusBusca + "%';";

        try {

            // EXECUTAR PREPARED STATEMENT E RODAR O RESULT SET

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

    // Atualiza determinado campo do objeto Cooperativa
    public int atualizarCooperativa(Cooperativa coop, String campo) {

        // DECLARACAO

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // COMANDO SQL

        String sql = "UPDATE cooperativa " +
                "SET " + campo.toLowerCase() + " WHERE id_cooperativa = " + coop.getIdCooperativa();

        try {

            // EXECUTAR PREPARED STATEMENT E PREENCHER PLACEHOLDERS

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // === METODOS DELETE ==============================================================================================

    // Deleta cooperativa determinada rota por meio do ID
    public int excluirCooperativa(Cooperativa coop) {

        // DECLARACAO

        Connection conexao = conn.conectar();

        // COMANDO SQL

        String sql = "DELETE FROM COOPERATIVA WHERE id_cooperativa = ?";

        // EXECUTAR PREPARED STATEMENT

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(1, coop.getIdCooperativa());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            return -1;

        }

    }

}
