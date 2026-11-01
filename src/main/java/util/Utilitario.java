package util;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class Utilitario {

    // === CONSTRUTOR VAZIO ============================================================================================

    // Construtor vazio para implementação dos metodos em outras classes

    public Utilitario() {
    }

    // Prepara textos livres sem remover os espaços internos.
    public String normalizarTextoLivre(String texto) {
        if (texto == null) {
            return null;
        }
        return texto.strip().toUpperCase(Locale.ROOT);
    }

    // === METODOS DE FILTRO DE ENTRADA ================================================================================

    /*
    A função desses métodos de entrada é filtrar o valor de entrada para manter um valor comum para o que está no banco.
    */

    // Filtro de CNPJ - devolvendo somente os valores numéricos do formato 00.000.000/000-00
    public String filtrarCnpj(String cnpj) {
        if (cnpj == null) {
            return null;
        }
        cnpj = cnpj.strip();
        if (!cnpj.matches("^\\d{14}$")) {
            if (!cnpj.matches("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$")) {
                throw new IllegalArgumentException("CNPJ deve seguir o formato 00.000.000/0000-00.");
            }
            cnpj = cnpj.replace(".", "").replace("/", "").replace("-", "");
        }
        if (!cnpjValido(cnpj)) {
            throw new IllegalArgumentException("CNPJ inválido: confira os dígitos.");
        }
        return cnpj;
    }

    // Confere os dois dígitos verificadores do CNPJ (14 dígitos, sem máscara).
    public boolean cnpjValido(String cnpj) {
        if (cnpj == null || !cnpj.matches("^\\d{14}$") || cnpj.matches("^(\\d)\\1{13}$")) {
            return false;
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        return digitoVerificador(cnpj, pesos1) == cnpj.charAt(12) - '0'
                && digitoVerificador(cnpj, pesos2) == cnpj.charAt(13) - '0';
    }

    // Filtro de CPF - devolve os 11 dígitos, aceitando 000.000.000-00 ou só números.
    public String filtrarCpf(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("CPF é obrigatório.");
        }
        cpf = cpf.strip();
        if (!cpf.matches("^\\d{11}$")) {
            if (!cpf.matches("^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$")) {
                throw new IllegalArgumentException("CPF deve seguir o formato 000.000.000-00.");
            }
            cpf = cpf.replace(".", "").replace("-", "");
        }
        if (!cpfValido(cpf)) {
            throw new IllegalArgumentException("CPF inválido: confira os dígitos.");
        }
        return cpf;
    }

    // Confere os dois dígitos verificadores do CPF (11 dígitos, sem máscara).
    public boolean cpfValido(String cpf) {
        if (cpf == null || !cpf.matches("^\\d{11}$") || cpf.matches("^(\\d)\\1{10}$")) {
            return false;
        }
        int[] pesos1 = {10, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        return digitoVerificador(cpf, pesos1) == cpf.charAt(9) - '0'
                && digitoVerificador(cpf, pesos2) == cpf.charAt(10) - '0';
    }

    // Formata 11 dígitos de CPF como 000.000.000-00.
    public String formatarCpf(String cpf) {
        if (cpf == null) {
            return null;
        }
        cpf = cpf.strip();
        if (!cpf.matches("^\\d{11}$")) {
            throw new IllegalArgumentException("CPF armazenado deve ter 11 dígitos.");
        }
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "."
                + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    // Calcula um dígito verificador (módulo 11) com os pesos informados.
    private int digitoVerificador(String numero, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (numero.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    // Filtro de Telefone/Whatsapp - devolvendo somente os valores numéricos de um dos formatos a seguir:
    // (00) 0000-0000 ou (00) 00000-0000
    public String filtrarTelefoneWhatsapp(String telefoneWhatsapp) {
        if (telefoneWhatsapp == null) {
            throw new IllegalArgumentException("telefoneWhatsapp é obrigatório.");
        }
        telefoneWhatsapp = telefoneWhatsapp.strip();
        if (!telefoneWhatsapp.matches("^\\d{10,11}$")) {
            if (!telefoneWhatsapp.matches("^\\(\\d{2}\\) ?\\d{4,5}-\\d{4}$")) {
                throw new IllegalArgumentException("telefoneWhatsapp deve ter 10 ou 11 dígitos.");
            }
            telefoneWhatsapp = telefoneWhatsapp.replace("(", "").replace(")", "")
                    .replace(" ", "").replace("-", "");
        }
        // DDD de 11 a 99 (sem zero à esquerda); celular com 11 dígitos começa com 9
        if (!telefoneWhatsapp.matches("^[1-9][1-9](9\\d{8}|[2-5]\\d{7})$")) {
            throw new IllegalArgumentException(
                    "telefoneWhatsapp inválido: use DDD válido e, para celular, o 9 após o DDD.");
        }
        return telefoneWhatsapp;
    }

    // Filtrar email institucional seguindo o formato seu-nome@email.com
    public String filtrarEmailInstitucional(String email) {
        if (email == null) {
            throw new IllegalArgumentException("email é obrigatório.");
        }
        email = email.strip().toLowerCase(Locale.ROOT);
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("email inválido.");
        }
        return email;
    }

    // Filtra CEP para guardar somente oito dígitos.
    public String filtrarCep(String cep) {
        if (cep == null) {
            throw new IllegalArgumentException("CEP é obrigatório.");
        }
        cep = cep.strip();
        if (cep.matches("^\\d{8}$")) {
            return cep;
        }
        if (!cep.matches("^\\d{5}-\\d{3}$")) {
            throw new IllegalArgumentException("CEP deve seguir o formato 00000-000.");
        }
        return cep.replace("-", "");
    }

    // Filtrar UF seguindo um dos valores possíveis
    public String validarUf(String uf) {
        if (uf == null) {
            throw new IllegalArgumentException("UF é obrigatória.");
        }
        uf = uf.strip().toUpperCase(Locale.ROOT);

        // Lista de UFs possíveis
        String[] listaUfs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB",
                "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

        // Verificação para uf a ver dentro da lista de UF
        for (String ufVer : listaUfs) {
            if (ufVer.equals(uf)) {
                return uf;
            }
        }
        throw new IllegalArgumentException("UF inválida.");

    }

    // Validação de data seguindo o formato dd/mm/aaaa
    public String validarData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data é obrigatória.");
        }
        data = data.strip();
        try {
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);
            return LocalDate.parse(data, formato).toString();
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("data deve seguir o formato dd/mm/aaaa e existir no calendário.", e);
        }
    }

    // Filtro de informações numericas definidas como NUMERIC(X, Y)
    public BigDecimal validarNumeroDecimal(String numero, long valor, int decimal) {
        if (numero == null) {
            throw new IllegalArgumentException("número é obrigatório");
        }
        if (valor <= 0 || decimal < 0) {
            throw new IllegalArgumentException("O limite inteiro deve ser positivo e o decimal não pode ser negativo.");
        }
        numero = numero.strip().replace(",", ".");
        String formato = "^-?[0-9]{1," + valor + "}$";
        if (decimal > 0) {
            formato = "^-?[0-9]{1," + valor + "}(\\.[0-9]{1," + decimal + "})?$";
        }
        if (numero.matches(formato)) {
            return new BigDecimal(numero);
        }
        throw new IllegalArgumentException("formato de numero invalido.");
    }

    // === METODOS DE FORMATACAO DE SAIDA ==============================================================================

    /*
    A função desses metodos de saida é formatar as informações em estado bruto que estão no banco para a sua
    representação correta.
     */

    // Formatação de CNPJ - retornando os valores numéricos brutos do banco no formato 00.000.000/000-00
    public String formatarCnpj(String cnpj) {
        if (cnpj == null) {
            return null;
        }
        cnpj = cnpj.strip();
        if (!cnpj.matches("^\\d{14}$")) {
            throw new IllegalArgumentException("CNPJ armazenado deve ter 14 dígitos.");
        }
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "."
                + cnpj.substring(5, 8) + "/" + cnpj.substring(8, 12) + "-" + cnpj.substring(12);
    }

    // Formatação de Telefone/Whatsapp - retornando os valores numéricos brutos em um dos formatos
    // (00) 0000-0000 ou (00) 00000-0000
    public String formatarTelefoneWhatsapp(String telefoneWhatsapp) {
        if (telefoneWhatsapp == null) {
            return null;
        }
        telefoneWhatsapp = telefoneWhatsapp.strip();
        if (!telefoneWhatsapp.matches("^\\d{10,11}$")) {
            throw new IllegalArgumentException("telefoneWhatsapp armazenado deve ter 10 ou 11 dígitos.");
        }
        if (telefoneWhatsapp.length() == 10) {
            return "(" + telefoneWhatsapp.substring(0, 2) + ") " + telefoneWhatsapp.substring(2, 6) + "-" +
                    telefoneWhatsapp.substring(6);
        }
        return "(" + telefoneWhatsapp.substring(0, 2) + ") " + telefoneWhatsapp.substring(2, 7) + "-" +
                telefoneWhatsapp.substring(7);
    }

    // Um metodo de formatação para email não será necessário, visto que ele já entra no banco seguindo o formato

    // Formata oito dígitos de CEP como 00000-000.
    public String formatarCEP(String cep) {
        if (cep == null) {
            return null;
        }
        cep = cep.strip();
        if (!cep.matches("^\\d{8}$")) {
            throw new IllegalArgumentException("CEP armazenado deve ter oito dígitos.");
        }
        return cep.substring(0, 5) + "-" + cep.substring(5);
    }

    // Formatação de UF não será necessária pois valores já estão formatados corretamente no banco

    // Validação de data - convertendo para a visualização pretendida - MDY para dd/mm/aaaa
    public String formatarData(String data) {
        if (data == null) {
            return null;
        }
        try {
            LocalDate valor = LocalDate.parse(data.strip());
            return valor.format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("data armazenada deve seguir o formato aaaa-mm-dd.", e);
        }
    }

    // === METODOS DE USO GERAL ========================================================================================

    // Retorna o último índice de ID de qualquer tabela - usado para geração automática de PKs
    public long selecionarUltimoId(String tabela) {

        // INICIALIZA CONEXÃO COM BD

        ConexaoBD conn = new ConexaoBD();
        Connection conexao = conn.conectar();

        // SELECIONA PARA TABELA DADA

        String sql = "SELECT id_" + tabela +
                " FROM " + tabela +
                " ORDER BY 1 DESC LIMIT 1";

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong("id_" + tabela);
            } else {
                return -1;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Falha ao consultar o último ID de " + tabela + ".", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

}
