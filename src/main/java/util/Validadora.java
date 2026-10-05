package util;

public class Validadora {

    // === CONSTRUTOR VAZIO ============================================================================================

    // Construtor vazio para implementação dos metodos em outras classes

    public Validadora() {
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

        } else {

            return "invalido";

        }

    }

    // Filtro de Telefone/Whatsapp - devolvendo somente os valores numéticos de um dos formatos a seguir:
    // (00) 0000-0000 ou (00) 00000-0000
    private static String filtrarTelefoneWhatsapp(String telefoneWhatsapp) {

        // Tratamento inicial
        telefoneWhatsapp.replace(" ", ""); // Retirando todos os espaços em branco

        // Verificação de formato correto - (00) 0000-0000 ou (00) 00000-0000
        if (telefoneWhatsapp.matches("^\\(\\d{2}\\)\\d{4,5}-\\d{4}$")) {

            // Retirar "(", ")", e "-".
            telefoneWhatsapp.replace("(", "");
            telefoneWhatsapp.replace(")", "");
            telefoneWhatsapp.replace("-", "");

            return telefoneWhatsapp;

        } else {

            return "invalido";

        }

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

}
