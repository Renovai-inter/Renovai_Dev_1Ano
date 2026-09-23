package dao;

import model.Cooperativa;
import util.Conexao;

import java.sql.*;

public class CooperativaDAO {

    // ATRIBUTOS

    private static Conexao conn = new Conexao();

    // CONSTRUTOR

    public CooperativaDAO() {
    }

    // === METODOS DE FILTRO PARA NUMERICO =============================================================================

    // Filtrar CNPJ
    private static String filtrarCnpj(String cnpj) {

        // Tratamento inicial
        cnpj.trim();

        // Retirar "." e "-"
        cnpj.replace(".", "");
        cnpj.replace("-", "");

        // Verificar formato (regex) correto
        if (cnpj.matches("^\\d{11}$")) {
            return cnpj;
        } else {
            return "invalido";
        }

    }

    // Filtrar Telefone/Whatsapp
    private static String filtrarTelefoneWhatsapp(String telefoneWhatsapp) {

        // Tratamento inicial
        telefoneWhatsapp.trim();

        // Retirar "(", ")", e "-", depois, retirar o " " presente entre o DDD e o numero
        telefoneWhatsapp.replace("(", "");
        telefoneWhatsapp.replace(")", "");
        telefoneWhatsapp.replace("-", "");
        telefoneWhatsapp.replace(" ", "");

        // Formato de telefone comum tem 10 digitos, whatsapp tem 11
        if (telefoneWhatsapp.matches("^\\d{10,11}$")) {
            return telefoneWhatsapp;
        } else {
            return "invalido";
        }

    }

    // Filtrar email institucional
    private static String filtrarEmailInstitucional(String email) {

        // Tratamento inicial
        email.trim().toLowerCase();

        // todo: Verificação de existência de email

        // Verificação usando regex. ex: joel.gracek@email.com
        if (email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            return email;
        } else {
            return "invalido";
        }

    }

    // Validar CEP
    private static String filtrarCep(String cep) {

        // Tratamento inicial
        cep.trim();

        // Validação usando formato 00000-000
        if (cep.matches("^\\d{5}-\\d{3}")) {
            return cep;
        } else {
            return "invalido";
        }

    }

    // todo: Validar UF
    private static String validarCidadeUf(String uf) {

        // Tratamento inicial
        uf.trim().toUpperCase();

        // Lista de UFs possíveis
        String[] listaUfs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB",
                "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

        // Verificação de formato: CIDADE X / UF
        for (String ufVer : listaUfs) {

            if (ufVer.equals(uf)) {
                return uf;
            }

        }
        return "invalido";

    }

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

        boolean contemCnpj = coop.getCnpj().isEmpty();
        boolean contemDescricao;
        boolean contemLogo;

        // COMANDO SQL PADRÃO

        // todo: ver possível geração de link de whatsapp automático pelo numero usando API
        // Caso afirmativo, adicionar link_whatsapp no comando e statement

        sql = (
                "INSERT INTO cooperativa " +
                    "(id_cooperativa, nome, cnpj, nome_publico, descricao_institucional, " +
                    "logo_url, email_institucional, telefone_whatsapp, cep, endereco, cidade, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        );

        // todo: EXECUTAR PREPARED STATEMENT E PREENCHER PLACEHOLDERS

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(0, getUltimoIdCooperativa());

            pstmt.setString(1, coop.getNome().toLowerCase());

            String cnpj = filtrarCnpj(coop.getCnpj());
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

            String emailInstucional = filtrarEmailInstitucional(coop.getEmailInstitucional());
            if (emailInstucional.equals("invalido")) {
                pstmt.setNull(7, Types.VARCHAR);
            } else {
                pstmt.setString(7, coop.getEmailInstitucional());
            }

            String telefoneWhatsapp = filtrarTelefoneWhatsapp(coop.getTelefoneWhatsapp());
            if (telefoneWhatsapp.equals("invalido")) {
                pstmt.setNull(8, Types.VARCHAR);
            } else {
                pstmt.setString(8, telefoneWhatsapp);
            }







        } catch (SQLException e) {
            return 0;
        } finally {
            conn.desconectar(conexao);
        }

    }

    // === METODOS READ ================================================================================================
        String sql = "Selet "


    // === METODOS UPDATE ==============================================================================================



    // === METODOS DELETE ==============================================================================================



}
