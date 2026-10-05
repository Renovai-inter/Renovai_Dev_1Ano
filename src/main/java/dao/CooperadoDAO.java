package dao;
import model.Coleta;
import model.Cooperado;
import util.Conexao;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class CooperadoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public CooperadoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

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

    /*Selecionar atividades do cooperado – caso não tenha cargo definido, o gestor pode indicar uma
     ou mais atividades (coleta externa, separação de materiais, organização do galpão, prensagem, pesagem, negociações, administrativo).*/

    public int selecionarAtividades(Cooperado cooperado){

        String sql = "INSERT INTO cooperado(id_usuario, cargo, codigo_cooperado, status) VALUES(?,?,?,?)";

        Connection conexao = conn.conectar();

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setLong(1, cooperado.getIdUsuario());
            pstm.setString(2, cooperado.getCargo());
            pstm.setString(3, cooperado.getCodigoCooperado());
            pstm.setString(4, cooperado.getStatus());

            pstm.executeUpdate();

        } catch (SQLException e) {
            e.getMessage();
        }
        return 0;
    }

    /*Definir permissões de acesso no cadastro – o gestor define
     quais funcionalidades (Dashboard, Cooperados, Coletas, Negociações, Vendas, Financeiro, Relatórios, Configurações)
     estarão disponíveis para o cooperado (por padrão, todas são liberadas).
Geração automática de conta de acesso – ao cadastrar, o
sistema cria automaticamente: código do cooperado, nome de usuário e senha temporária.*/

    public int permissoesAcesso(Cooperado cooperado, String nome_completo, String nome_usuario, String senha_hash, Boolean senha_temporaria, String email, String tipo_usario, Date data_criacao){

        String sqlCop = "INSERT INTO cooperado(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo) VALUES(?,?,?,?,?,?)";
        String sqlUsu = "INSERT INTO usuario(nome_completo, nome_usuario, senha_hash, senha_temporaria, email, tipo_usuario, data_criacao) VALUES(?,?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        try{
            PreparedStatement pstm = conexao.prepareStatement(sqlCop);
            PreparedStatement pst = conexao.prepareStatement(sqlUsu);

            //cooperado
            pstm.setInt(1, Math.toIntExact(cooperado.getIdUsuario()));
            pstm.setString(2, cooperado.getCodigoCooperado());
            pstm.setString(3, cooperado.getCargo());
            pstm.setTimestamp(4, java.sql.Timestamp.valueOf(cooperado.getDataCadastro()));
            //usuario
            pst.setString(1, nome_completo);
            pst.setString(2,nome_usuario);
            pst.setString(3, senha_hash);
            pst.setBoolean(4, senha_temporaria);
            pst.setString(5, email);
            pst.setString(6, tipo_usario);
            pst.setDate(7, new java.sql.Date(data_criacao.getTime()));
        } catch (SQLException e){
            e.getMessage();
        }

        return 0;
    }




    // === METODOS READ ================================================================================================

    /*Visualizar lista de cooperados – tela principal com código, nome, status e cargo (quando houver) de todos os cooperados cadastrados.
Visualizar detalhes do cooperado – ao selecionar um cooperado, acessar a tela de edição para ver suas informações completas.*/

    public ArrayList<Cooperado> listaCooperados(){

        ArrayList<Cooperado> cooperados = new ArrayList<>();

        String sql;
        sql = "SELECT c.codigo_cooperado, u.nome_usuario, c.status, c.cargo FROM cooperado c join usuario u on c.id_usuario = u.id_usuario";

        Connection conexao = conn.conectar();

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);

            ResultSet rst = pstm.executeQuery();

            while(rst.next()){
                Cooperado cooperado = new Cooperado();

                cooperado.setCodigoCooperado(rst.getString("codigo_cooperado"));
                cooperado.setIdUsuario(rst.getLong("nome_usuario"));
                cooperado.setIdUsuario(rst.getLong("id_usuario"));
                cooperado.setStatus(rst.getString("status"));
                cooperado.setCargo(rst.getString("cargo"));

                cooperados.add(cooperado);
            }

        } catch (SQLException e) {
            e.getMessage();
        }

        return cooperados;
    }



    // === METODOS UPDATE ==============================================================================================

    // Editar cooperado – abre o formulário para edição das informações do cooperado selecionado.

    public int editarCooperado(Cooperado cooperado) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE cooperado\n" +
                "SET id_usuario = ?,\n" +
                "    id_cooperativa = ?,\n" +
                "    codigo_cooperado = ?,\n" +
                "    cargo = ?,\n" +
                "    status = ? WHERE id_cooperado = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setBigDecimal(1, BigDecimal.valueOf(cooperado.getIdUsuario()));
            pstmt.setBigDecimal(1, BigDecimal.valueOf(cooperado.getIdCooperativa()));
            pstmt.setString(1, (cooperado.getCodigoCooperado()));
            pstmt.setString(1, (cooperado.getCargo()));
            pstmt.setString(1, (cooperado.getStatus()));

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

//Alterar cargo do cooperado.

    public int alterarCargoCooperado(Cooperado cooperado) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE cooperado\n" +
                "SET cargo = ? WHERE id_cooperado = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, (cooperado.getCargo()));

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

//Alterar atividades desempenhadas (múltipla escolha).

    public int alterarAtividadesDesempenhadas(int idCooperado, String atividade) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE cooperado_atividade\n" +
                "SET atividade = ? WHERE id_cooperado = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, atividade);
            pstmt.setInt(2, idCooperado);


            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }


//Alterar status do cooperado (Ativo, Afastado ou Inativo).

    public int alterarStatusCooperado(Cooperado cooperado) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE cooperado\n" +
                "SET status = ? WHERE id_cooperado = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, (cooperado.getStatus()));

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

//Alterar permissões de acesso ao sistema (somente cooperados com cargo de Gestor podem fazer essa alteração para outros usuários; o gestor sempre mantém acesso total, que não pode ser removido).

    public int alterarPermissoesAcesso(int idCooperado, int idPermissao) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE cooperado_permissao\n" +
                "SET id_permissao = ? WHERE id_cooperado = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setInt(1, idCooperado);
            pstmt.setInt(2, idPermissao);

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

//Alterar nome de usuário – pode ser feito posteriormente pelo próprio cooperado, nas configurações da conta.

    public int alterarNomeUsuario(int idUsuario, String nomeUsuario) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE usuario\n" +
                "SET nome_usuario = ? WHERE id_usuario = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, nomeUsuario);
            pstmt.setInt(2, idUsuario);

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

//Definir nova senha no primeiro acesso – o cooperado deve trocar a senha temporária ao acessar o sistema pela primeira vez.

    public int alterarSenha(int idUsuario, String senhaHash, boolean senhaTemporaria) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE usuario\n" +
                "SET senha_hash = ?,\n" +
                "    senha_temporaria = ? WHERE id_usuario = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, senhaHash);
            pstmt.setBoolean(2, false);
            pstmt.setInt(3, idUsuario);

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    /*Remover cooperado – remove o cooperado da cooperativa, com uma tela de confirmação antes para evitar exclusões acidentais. Recomendado apenas quando o cooperado realmente deixa de fazer parte da cooperativa (para afastamentos temporários, o indicado é usar o status "Afastado" em vez de remover).*/

     public int excluirCooperado(Cooperado cooperado) {

        Connection conexao = conn.conectar();

        String sql = "DELETE FROM cooperado\n" +
                "WHERE id_cooperado = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(1, cooperado.getIdCooperado());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

//Remover acesso a um módulo específico (permissão) – não exclui os dados relacionados à funcionalidade, apenas impede o acesso a ela (é uma "remoção" parcial, não uma exclusão de dados).*/

    public int removerAcessoModulo(int idCooperado, int idPermissao) {

        Connection conexao = conn.conectar();

        String sql = "DELETE FROM cooperado_permissao\n" +
                "WHERE id_cooperado = ? AND id_permissao = ?";

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);


            pstmt.setLong(1,idCooperado);
            pstmt.setLong(1,idPermissao);


            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

}
