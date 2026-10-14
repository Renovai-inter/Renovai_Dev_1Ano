package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utilitario {

    // === CONSTRUTOR VAZIO ============================================================================================

    // Construtor vazio para implementação dos metodos em outras classes

    public Utilitario() {
    }

    // === METODOS DE FILTRO DE ENTRADA ================================================================================

    /*
    A função desses métodos de entrada é filtrar o valor de entrada para manter um valor comum para o que está no banco.
    */

    // Filtro de CNPJ - devolvendo somente os valores numéricos do formato 00.000.000/000-00
    public String filtrarCnpj(String cnpj) {

        // Tratamento inicial
        cnpj.replace(" ", ""); // Retirando todos os espaços em branco

        // Verificação do formato correto (00.000.000/000-00)
        if (cnpj.matches("^\\d{2}\\.\\d{3}\\.\\d{3}/\\d{3}-\\d{2}$")) {

            // Retirar ".", "-", "/" e espaços
            cnpj.replace(".", "");
            cnpj.replace("-", "");
            cnpj.replace("/", "");

            // Retorno do CNPJ em valores apenas numericos
            return cnpj;

        }
        return "invalido";

    }

    // Filtro de Telefone/Whatsapp - devolvendo somente os valores numéricos de um dos formatos a seguir:
    // (00) 0000-0000 ou (00) 00000-0000
    public String filtrarTelefoneWhatsapp(String telefoneWhatsapp) {

        // Tratamento inicial
        telefoneWhatsapp.replace(" ", ""); // Retirando todos os espaços em branco

        // Verificação de formato correto - (00) 0000-0000 ou (00) 00000-0000
        if (telefoneWhatsapp.matches("^\\(\\d{2}\\)\\d{4,5}-\\d{4}$")) {

            // Retirar "(", ")", e "-".
            telefoneWhatsapp.replace("(", "");
            telefoneWhatsapp.replace(")", "");
            telefoneWhatsapp.replace("-", "");

            return telefoneWhatsapp;

        }
        return "invalido";

    }

    // Filtrar email institucional seguindo o formato seu-nome@email.com
    public String filtrarEmailInstitucional(String email) {

        // Tratamento inicial
        email.toLowerCase().replace(" ", ""); // Retirando todos os espaços em branco

        // Verificação usando regex. ex: seu-nome@email.com
        if (email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            return email;
        }

        return "invalido";

    }

    // Filtrar CEP seguindo o formato 00000-000
    public String filtrarCep(String cep) {

        // Tratamento inicial
        cep.replace(" ", "");

        // Validação usando formato 00000-000
        if (cep.matches("^\\d{5}-\\d{3}$")) {

            // Retirando o "-"
            cep.replace("-", "");

            return cep;
        }
        return "invalido";

    }

    // Filtrar endereço seguindo o formato Rua,Numero,Bairro
    public String filtrarEndereco(String endereco) {

        // Tratamento inicial
        endereco.toUpperCase().replace(" ", "");

        // Formato esperado: Rua, Número, Bairro
        if (endereco.matches("^[\\p{L}\\d.-]+,\\d+,[\\p{L}\\d.-]+")) {
            return endereco;
        }
        return "invalido";

    }

    // Filtrar UF seguindo um dos valores possíveis
    public String validarUf(String uf) {

        // Tratamento inicial
        uf.replace(" ", "").toUpperCase();

        // Lista de UFs possíveis
        String[] listaUfs = {"AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB",
                "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"};

        // Verificação para uf a ver dentro da lista de UF
        for (String ufVer : listaUfs) {
            if (ufVer.equals(uf)) {
                return uf;
            }
        }
        return "invalido";

    }

    // Validação de data seguindo o formato dd/mm/aaaa
    public String validarData(String data) {

        // Tratamento inicial
        data.replace(" ", "");

        // Verificando formato de acordo com o padrão dd/mm/aaaa
        if (data.matches("^\\d{2}/\\d{2}/\\d{4}")) {

            // DateStyle padrão do banco: ISO, MDY | Realizar a conversão para padrão do banco
            String[] valoresData = data.split("/");
            data = valoresData[1] + "-" + valoresData[0] + "-" + valoresData[2];

            return data;

        }
        return "invalido";

    }

    // === METODOS DE FORMATACAO DE SAIDA ==============================================================================

    /*
    A função desses metodos de saida é formatar as informações em estado bruto que estão no banco para a sua
    representação correta.
     */

    // Formatação de CNPJ - retornando os valores numéricos brutos do banco no formato 00.000.000/000-00
    public String formatarCnpj(String cnpj) {

        // Tratamento inicial
        cnpj.replace(" ", ""); // Retirando todos os espaços em branco

        // Retorno no formato apropriado
        return cnpj.substring(0, 2) + "." + cnpj.substring(2, 5) + "." + cnpj.substring(9, 12) + "-" +
                cnpj.substring(12);
    }

    // Formatação de Telefone/Whatsapp - retornando os valores numéricos brutos em um dos formatos
    // (00) 0000-0000 ou (00) 00000-0000
    public String formatarTelefoneWhatsapp(String telefoneWhatsapp) {

        // Tratamento inicial
        telefoneWhatsapp.replace(" ", ""); // Retirando todos os espaços em branco

        // Retorno no formato apropriado com base nos tipos de telefone
        // Verifica o tamanho para determinar qual tipo é
        if (telefoneWhatsapp.length() == 10) {
            return "(" + telefoneWhatsapp.substring(0, 2) + ") " + telefoneWhatsapp.substring(2, 6) + "-" +
                    telefoneWhatsapp.substring(6);
        } else {
            return "(" + telefoneWhatsapp.substring(0, 2) + ") " + telefoneWhatsapp.substring(2, 7) + "-" +
                    telefoneWhatsapp.substring(7);
        }

    }

    // Um metodo de formatação para email não será necessário, visto que ele já entra no banco seguindo o formato

    // Formatacao de CEP - retornando os valores numericos brutos no formato 00000-0000
    public String formatarCEP(String cep) {

        // Tratamento inicial
        cep.replace(" ", "");

        // Retorno no formato correto
        return cep.substring(0, 6) + "-" + cep.substring(6);

    }

    // Formatação de Endereço - Seguindo o formato Rua, Número, Bairro capitalizados
    public String formatarEndereco(String endereco) {

        // Tratamento inicial
        endereco.replace(" ", "");

        // Separando cada elemento por meio da vírgula
        String[] valoresEndereco = endereco.split(",");

        // Capitalizando os valores de Rua e Bairro
        valoresEndereco[0] = valoresEndereco[0].toUpperCase().charAt(0) +
                valoresEndereco[0].toLowerCase().substring(1);
        valoresEndereco[0] = valoresEndereco[0].toUpperCase().charAt(0) +
                valoresEndereco[0].toLowerCase().substring(1);

        // Retorno no formato correto
        return valoresEndereco[0] + ", " + valoresEndereco[1] + ", " + valoresEndereco[2];

    }

    // Formatação de UF não será necessária pois valores já estão formatados corretamente no banco

    // Validação de data - convertendo para a visualização pretendida - MDY para dd/mm/aaaa
    public String formatarData(String data) {

        // Tratamento inicial
        data.replace(" ", "");

        // Convertendo para o padrão visual por meio de SPLIT
        String[] valoresData = data.split("-");
        return valoresData[1] + "/" + valoresData[0] + "/" + valoresData[2];

    }

    // === METODOS DE USO GERAL ========================================================================================

    // Retorna o último índice de ID de qualquer tabela - usado para geração automática de PKs
    public long selecionarUltimoId(String tabela) {

        // INICIALIZA CONEXÃO COM BD

        Conexao conn = new Conexao();
        Connection conexao = conn.conectar();

        // SELECIONA PARA COOPERATIVA

        String sql = "SELECT id_" + tabela +
                " FROM " + tabela +
                " ORDER BY 1 DESC LIMIT 1";

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()){
                return rs.getLong("id_"+tabela);
            }
            else {
                return -1;
            }
        }
        catch (SQLException e) {
            return -1;
        } finally { conn.desconectar(conexao); } }

}
