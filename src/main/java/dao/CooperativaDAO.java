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
        }
        return "invalido";

    }

    // Filtrar email institucional
    private static String filtrarEmailInstitucional(String email) {

        // Tratamento inicial
        email.trim().toLowerCase();

        // todo: Verificação de existência de email

        // Verificação usando regex. ex: joel.gracek@email.com
        if (email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            return email;
        }
        return "invalido";

    }

    // Validar CEP
    private static String filtrarCep(String cep) {

        // Tratamento inicial
        cep.trim();

        // Validação usando formato 00000-000
        if (cep.matches("^\\d{5}-\\d{3}")) {
            return cep;
        }
        return "invalido";

    }

    // Validar Endereço
    private static String filtrarEndereco(String endereco) {

        // Tratamento inicial
        String[] valoresEndereco = endereco.trim().toLowerCase().split(",");

        // Formato esperado: Rua, Número, Bairro
        if (valoresEndereco.length == 3) {

            if (!(valoresEndereco[0].trim().matches("^\\w+$")
                    && valoresEndereco[1].trim().matches("^\\d+$")
                    && valoresEndereco[2].trim().matches("^\\w+$"))) {
                return "invalido";
            }

        }
        return endereco;

    }

    // Validar UF
    private static String validarUf(String uf) {

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

            String cep = filtrarCep(coop.getCep());
            if (cep.equals("invalido")) {
                pstmt.setNull(9, Types.VARCHAR);
            } else {
                pstmt.setString(9, cep);
            }

            String endereco = filtrarEndereco(coop.getEndereco());
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

    }

    // === METODOS READ ================================================================================================



    // === METODOS UPDATE ==============================================================================================



    // === METODOS DELETE ==============================================================================================



}
