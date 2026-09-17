package dao;
import util.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class CooperadoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public CooperadoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    /*CREATE (Criar)
Adicionar cooperado – abre o formulário para cadastro de um novo cooperado, informando nome completo (obrigatório) e cargo (opcional: Gestor, Tesoureiro, Motorista).
Selecionar atividades do cooperado – caso não tenha cargo definido, o gestor pode indicar uma ou mais atividades (coleta externa, separação de materiais, organização do galpão, prensagem, pesagem, negociações, administrativo).
Definir permissões de acesso no cadastro – o gestor define quais funcionalidades (Dashboard, Cooperados, Coletas, Negociações, Vendas, Financeiro, Relatórios, Configurações) estarão disponíveis para o cooperado (por padrão, todas são liberadas).
Geração automática de conta de acesso – ao cadastrar, o sistema cria automaticamente: código do cooperado, nome de usuário e senha temporária.*/

    public int cadastrarCooperado(String nome_completo, String nome_usuario, String senha_hash,
                                  int senha_temporaria, String email, String tipo_usuario, Date data_criacao){

        String sql = "INSERT INTO cooperado(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo, status, " +
                "data_cadastro) VALUES (?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);
            //valores esperados pela query
            pstm.setString(1, nome_completo);
            pstm.setString(2, nome_usuario);
            pstm.setString(3, senha_hash);
            pstm.setInt(4, senha_temporaria);
            pstm.setString(5, email);
            pstm.setString(6, tipo_usuario);
            pstm.setDate(7, new java.sql.Date(data_criacao.getTime()));
            //executando a query
            pstm.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
        return 0;
    }


    // === METODOS READ ================================================================================================

    /*Visualizar lista de cooperados – tela principal com código, nome, status e cargo (quando houver) de todos os cooperados cadastrados.
Visualizar detalhes do cooperado – ao selecionar um cooperado, acessar a tela de edição para ver suas informações completas.*/



    // === METODOS UPDATE ==============================================================================================

    /*Editar cooperado – abre o formulário para edição das informações do cooperado selecionado.
Alterar cargo do cooperado.
Alterar atividades desempenhadas (múltipla escolha).
Alterar status do cooperado (Ativo, Afastado ou Inativo).
Alterar permissões de acesso ao sistema (somente cooperados com cargo de Gestor podem fazer essa alteração para outros usuários; o gestor sempre mantém acesso total, que não pode ser removido).
Alterar nome de usuário – pode ser feito posteriormente pelo próprio cooperado, nas configurações da conta.
Definir nova senha no primeiro acesso – o cooperado deve trocar a senha temporária ao acessar o sistema pela primeira vez.*/



    // === METODOS DELETE ==============================================================================================

    /*Remover cooperado – remove o cooperado da cooperativa, com uma tela de confirmação antes para evitar exclusões acidentais. Recomendado apenas quando o cooperado realmente deixa de fazer parte da cooperativa (para afastamentos temporários, o indicado é usar o status "Afastado" em vez de remover).
Remover acesso a um módulo específico (permissão) – não exclui os dados relacionados à funcionalidade, apenas impede o acesso a ela (é uma "remoção" parcial, não uma exclusão de dados).*/



}
