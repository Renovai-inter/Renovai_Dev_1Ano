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
        if (cnpj.matches("^\\d{14}$")) {
            return cnpj;
        }
        if (!cnpj.matches("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}$")) {
            throw new IllegalArgumentException("CNPJ deve seguir o formato 00.000.000/0000-00.");
        }
        cnpj = cnpj.replace(".", "").replace("/", "").replace("-", "");
        return cnpj;
    }

    // Filtro de Telefone/Whatsapp - devolvendo somente os valores numéricos de um dos formatos a seguir:
    // (00) 0000-0000 ou (00) 00000-0000
    public String filtrarTelefoneWhatsapp(String telefoneWhatsapp) {
        if (telefoneWhatsapp == null) {
            throw new IllegalArgumentException("telefoneWhatsapp é obrigatório.");
        }
        telefoneWhatsapp = telefoneWhatsapp.strip();
        if (telefoneWhatsapp.matches("^\\d{10,11}$")) {
            return telefoneWhatsapp;
        }
        if (!telefoneWhatsapp.matches("^\\(\\d{2}\\) ?\\d{4,5}-\\d{4}$")) {
            throw new IllegalArgumentException("telefoneWhatsapp deve ter 10 ou 11 dígitos.");
        }
        return telefoneWhatsapp.replace("(", "").replace(")", "")
                .replace(" ", "").replace("-", "");
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
