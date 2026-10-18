package dao;
import model.Coleta;
import model.Cooperado;
import model.Cooperativa;
import util.Conexao;
import util.Utilitario;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;

public class CooperadoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();
    private Utilitario utilitario = new Utilitario();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de cooperado.
    public CooperadoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra o cooperado e os dados de usuário necessários.
    public int cadastrarCooperado(String nome_completo, String nome_usuario, String senha_hash,

        // Declaração

                                  int senha_temporaria, String email, String tipo_usuario, Date data_criacao){

        // Comando SQL

        String sql = "INSERT INTO cooperado(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo, status, " +
                "data_cadastro) VALUES (?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

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

    // Cadastra um usuário gestor vinculado a uma cooperativa.
    public int[] cadastrarGestor(Cooperativa cooperativa, String nomeCompleto, String nomeUsuario, String email, String senhaTemporaria) {

        // Declaração

        Connection conexao = conn.conectar();

        long idUsuario = utilitario.selecionarUltimoId("usuario");
        long idCooperado = utilitario.selecionarUltimoId("cooperado");

        // Comando SQL

        String sqlUsuario = "INSERT INTO usuario (id_usuario, nome_completo, nome_usuario, senha_hash, email, tipo_usuario) " +
                "VALUES (?, ?, ?, ?, ?, 'GESTOR_COOPERATIVA')";

        String sqlCooperado = "INSERT INTO cooperado (id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo) " +
                "VALUES (?, ?, ?, ?, 'GESTOR')";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmtUsuario = conexao.prepareStatement(sqlUsuario);
            pstmtUsuario.setLong(1, idUsuario);
            pstmtUsuario.setString(2, nomeCompleto.toUpperCase().strip());
            if (nomeUsuario.isEmpty()) {
                pstmtUsuario.setString(3, nomeCompleto.toUpperCase().replace(" ", ""));
            } else {
                pstmtUsuario.setString(3, nomeUsuario.toUpperCase().strip());
            }
            pstmtUsuario.setString(4, senhaTemporaria);
            pstmtUsuario.setString(5, utilitario.filtrarEmailInstitucional(email));

            PreparedStatement pstmtCooperado = conexao.prepareStatement(sqlCooperado);
            pstmtCooperado.setLong(1, idCooperado);
            pstmtCooperado.setLong(2, idUsuario);
            pstmtCooperado.setLong(3, cooperativa.getIdCooperativa());
            pstmtCooperado.setString(4, "CD-"+idCooperado);

            return new int[]{pstmtUsuario.executeUpdate(), pstmtCooperado.executeUpdate()};

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

    }

    // Registra as atividades atribuídas a um cooperado.
    public int selecionarAtividades(Cooperado cooperado){

        // Declaração

        // Comando SQL

        String sql = "INSERT INTO cooperado(id_usuario, cargo, codigo_cooperado, status) VALUES(?,?,?,?)";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

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

    // Registra a conta e as permissões de acesso do cooperado.
    public int permissoesAcesso(Cooperado cooperado, String nome_completo, String nome_usuario, String senha_hash, Boolean senha_temporaria, String email, String tipo_usario, Date data_criacao){

        // Declaração

        // Comando SQL

        String sqlCop = "INSERT INTO cooperado(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo) VALUES(?,?,?,?,?,?)";
        String sqlUsu = "INSERT INTO usuario(nome_completo, nome_usuario, senha_hash, senha_temporaria, email, tipo_usuario, data_criacao) VALUES(?,?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

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

    // Lista os cooperados com identificação, status e cargo.
    public ArrayList<Cooperado> listaCooperados(){

        // Declaração

        ArrayList<Cooperado> cooperados = new ArrayList<>();

        // Comando SQL

        String sql;
        sql = "SELECT c.codigo_cooperado, u.nome_usuario, c.status, c.cargo FROM cooperado c join usuario u on c.id_usuario = u.id_usuario";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

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

    // Atualiza os dados do cooperado identificado pelo ID.
    public int editarCooperado(Cooperado cooperado) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado\n" +
                "SET id_usuario = ?,\n" +
                "    id_cooperativa = ?,\n" +
                "    codigo_cooperado = ?,\n" +
                "    cargo = ?,\n" +
                "    status = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

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

    // Altera o cargo de um cooperado existente.
    public int alterarCargoCooperado(Cooperado cooperado) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado\n" +
                "SET cargo = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

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

    // Altera uma atividade atribuída ao cooperado.
    public int alterarAtividadesDesempenhadas(int idCooperado, String atividade) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado_atividade\n" +
                "SET atividade = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

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

    // Altera o status de um cooperado existente.
    public int alterarStatusCooperado(Cooperado cooperado) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado\n" +
                "SET status = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

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

    // Altera uma permissão de acesso atribuída ao cooperado.
    public int alterarPermissoesAcesso(int idCooperado, int idPermissao) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado_permissao\n" +
                "SET id_permissao = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

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

    // Altera o nome de usuário de uma conta existente.
    public int alterarNomeUsuario(int idUsuario, String nomeUsuario) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE usuario\n" +
                "SET nome_usuario = ? WHERE id_usuario = ?";

        // Executar PreparedStatement

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

    // Atualiza a senha e o indicador de senha temporária.
    public int alterarSenha(int idUsuario, String senhaHash, boolean senhaTemporaria) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE usuario\n" +
                "SET senha_hash = ?,\n" +
                "    senha_temporaria = ? WHERE id_usuario = ?";

        // Executar PreparedStatement

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

    // Exclui o vínculo do cooperado identificado pelo ID.
     public int excluirCooperado(Cooperado cooperado) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM cooperado\n" +
                "WHERE id_cooperado = ?";

        // Executar PreparedStatement

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

    // Remove uma permissão específica de um cooperado.
    public int removerAcessoModulo(int idCooperado, int idPermissao) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM cooperado_permissao\n" +
                "WHERE id_cooperado = ? AND id_permissao = ?";

        // Executar PreparedStatement

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
