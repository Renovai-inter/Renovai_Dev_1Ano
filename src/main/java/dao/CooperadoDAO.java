package dao;
import model.principais.Cooperado;
import model.principais.Cooperativa;
import model.auxiliares.Usuario;
import model.auxiliares.CooperadoPermissao;
import model.enums.AtividadeCooperado;
import model.enums.CargoCooperado;
import model.enums.StatusCooperado;
import model.enums.TipoUsuario;
import util.ConexaoBD;
import util.Utilitario;

import java.sql.*;
import java.util.*;

public class CooperadoDAO {

    // ATRIBUTOS

    private ConexaoBD conn = new ConexaoBD();
    private Utilitario utilitario = new Utilitario();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de cooperado.
    public CooperadoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra um usuário gestor vinculado a uma cooperativa.
    public Cooperado cadastrarGestor(Long idCooperativa, String nomeCompleto,
                                    String nomeUsuario, String email, String senhaTemporaria) {

        // Declaração

        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setIdCooperativa(idCooperativa);
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(nomeCompleto);
        if (nomeUsuario == null || nomeUsuario.isBlank()) {
            usuario.setNomeUsuario(nomeCompleto.replace(" ", ""));
        } else {
            usuario.setNomeUsuario(nomeUsuario);
        }
        usuario.setEmail(email);
        usuario.setSenhaHash(senhaTemporaria);
        usuario.setSenhaTemporaria(true);
        usuario.setTipoUsuario(TipoUsuario.GESTOR_COOPERATIVA);
        Cooperado gestor = new Cooperado();
        gestor.setIdCooperativa(idCooperativa);
        gestor.setUsuario(usuario);
        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try {
            boolean cadastrado = gravarGestor(conexao, cooperativa, usuario, gestor);
            if (!cadastrado) {
                return null;
            }
            return gestor;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar gestor.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Grava o gestor usando a conexão recebida da operação chamadora.
    boolean gravarGestor(Connection conexao, Cooperativa cooperativa, Usuario usuario,
                        Cooperado gestor) throws SQLException {

        // Declaração

        long ultimoIdCooperado = utilitario.selecionarUltimoId("cooperado");
        long idCooperado;
        if (ultimoIdCooperado == -1) {
            idCooperado = 1;
        } else {
            idCooperado = ultimoIdCooperado + 1;
        }
        long ultimoIdUsuario = utilitario.selecionarUltimoId("usuario");
        long idUsuario;
        if (ultimoIdUsuario == -1) {
            idUsuario = 1;
        } else {
            idUsuario = ultimoIdUsuario + 1;
        }
        gestor.setCodigoCooperado("CD-" + idCooperado);
        gestor.setCargo(CargoCooperado.GESTOR);
        gestor.setStatus(StatusCooperado.ATIVO);

        // Comando SQL

        String sql = "WITH usuario_novo AS (" + usuarioDAO.sqlInserirUsuario() + ") " +
                "INSERT INTO cooperado (id_cooperado, id_usuario, id_cooperativa, " +
                "codigo_cooperado, cargo) " +
                "SELECT ?, u.id_usuario, ?, ?, ? FROM usuario_novo u " +
                "RETURNING id_usuario";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            usuarioDAO.preencherCadastro(pstmt, 1, idUsuario, usuario);
            pstmt.setLong(8, idCooperado);
            pstmt.setLong(9, cooperativa.getIdCooperativa());
            pstmt.setString(10, gestor.getCodigoCooperado());
            pstmt.setString(11, gestor.getCargo().name());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }
                usuario.setIdUsuario(rs.getLong("id_usuario"));
            }
        }
        gestor.setIdUsuario(usuario.getIdUsuario());
        gestor.setIdCooperado(idCooperado);
        gestor.setIdCooperativa(cooperativa.getIdCooperativa());
        gestor.setUsuario(usuario);
        return true;
    }

    // Cadastra usuário e cooperado em um único comando SQL.
    public Cooperado cadastrarCooperado(Long idCooperativa, String nomeCompleto,
                                       String nomeUsuario, String email, String senhaHash,
                                       boolean senhaTemporaria, CargoCooperado cargo,
                                       StatusCooperado status) {

        // Declaração

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(nomeCompleto);
        usuario.setNomeUsuario(nomeUsuario);
        usuario.setEmail(email);
        usuario.setSenhaHash(senhaHash);
        usuario.setSenhaTemporaria(senhaTemporaria);
        usuario.setTipoUsuario(TipoUsuario.COOPERADO);
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperativa(idCooperativa);
        cooperado.setUsuario(usuario);
        cooperado.setCargo(cargo);
        cooperado.setStatus(status);
        if (cooperado.getIdCooperativa() == null || usuario.getSenhaHash() == null) {
            throw new IllegalArgumentException("Dados obrigatórios do cooperado e usuário estão ausentes.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "WITH usuario_novo AS (" + usuarioDAO.sqlInserirUsuario() + ") " +
                "INSERT INTO cooperado (id_cooperado, id_usuario, id_cooperativa, " +
                "codigo_cooperado, cargo, status) " +
                "SELECT ?, u.id_usuario, ?, ?, ?, ? FROM usuario_novo u " +
                "RETURNING id_usuario";

        // Executar PreparedStatement

        try {
            long ultimoIdCooperado = utilitario.selecionarUltimoId("cooperado");
            long idCooperado;
            if (ultimoIdCooperado == -1) {
                idCooperado = 1;
            } else {
                idCooperado = ultimoIdCooperado + 1;
            }
            long ultimoIdUsuario = utilitario.selecionarUltimoId("usuario");
            long idUsuario;
            if (ultimoIdUsuario == -1) {
                idUsuario = 1;
            } else {
                idUsuario = ultimoIdUsuario + 1;
            }
            cooperado.setCodigoCooperado("CD-" + idCooperado);
            if (cooperado.getStatus() == null) {
                cooperado.setStatus(StatusCooperado.ATIVO);
            }
            try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                usuarioDAO.preencherCadastro(pstmt, 1, idUsuario, usuario);
                pstmt.setLong(8, idCooperado);
                pstmt.setLong(9, cooperado.getIdCooperativa());
                pstmt.setString(10, cooperado.getCodigoCooperado());
                if (cooperado.getCargo() == null) {
                    pstmt.setNull(11, Types.VARCHAR);
                } else {
                    pstmt.setString(11, cooperado.getCargo().name());
                }
                pstmt.setString(12, cooperado.getStatus().name());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    usuario.setIdUsuario(rs.getLong("id_usuario"));
                }
                cooperado.setIdUsuario(usuario.getIdUsuario());
                cooperado.setIdCooperado(idCooperado);
                cooperado.setUsuario(usuario);
                return cooperado;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra as atividades informadas para um cooperado existente.
    public Cooperado selecionarAtividades(Long idCooperado, ArrayList<String> atividades) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        cooperado.getRelacionamentos().setIdCooperado(idCooperado);
        ArrayList<AtividadeCooperado> atividadesValidadas = new ArrayList<>();
        if (atividades != null) {
            for (String atividade : atividades) {
                atividadesValidadas.add(AtividadeCooperado.valueOf(atividade));
            }
        }
        cooperado.getRelacionamentos().setAtividadesCooperado(atividadesValidadas);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        if (cooperado.getRelacionamentos().getAtividadesCooperado().isEmpty()) {
            return null;
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        StringBuilder sql = new StringBuilder("INSERT INTO cooperado_atividade " +
                "(id_cooperado, atividade) VALUES ");
        for (int i = 0; i < atividadesValidadas.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(?, ?)");
        }

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql.toString())) {
            int indice = 1;
            for (AtividadeCooperado atividade : cooperado.getRelacionamentos().getAtividadesCooperado()) {
                pstmt.setLong(indice, cooperado.getIdCooperado());
                indice++;
                pstmt.setString(indice, atividade.name());
                indice++;
            }
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao registrar atividades do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra as permissões selecionadas para um cooperado.
    public Cooperado permissoesAcesso(Long idCooperado, ArrayList<Long> idsPermissoes) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        if (idsPermissoes == null) {
            throw new IllegalArgumentException("idsPermissoes é obrigatório.");
        }
        ArrayList<CooperadoPermissao> vinculos = new ArrayList<>();
        for (Long idPermissao : idsPermissoes) {
            CooperadoPermissao vinculo = new CooperadoPermissao();
            vinculo.setIdCooperado(idCooperado);
            vinculo.setIdPermissao(idPermissao);
            vinculos.add(vinculo);
        }
        cooperado.getRelacionamentos().setIdCooperado(idCooperado);
        cooperado.getRelacionamentos().setVinculosPermissao(vinculos);
        if (vinculos.isEmpty()) {
            return null;
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        StringBuilder sql = new StringBuilder("INSERT INTO cooperado_permissao " +
                "(id_cooperado, id_permissao) VALUES ");
        for (int i = 0; i < vinculos.size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(?, ?)");
        }

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql.toString())) {
            int indice = 1;
            for (CooperadoPermissao vinculo : vinculos) {
                pstmt.setLong(indice, vinculo.getIdCooperado());
                indice++;
                pstmt.setLong(indice, vinculo.getIdPermissao());
                indice++;
            }
            int linhas = pstmt.executeUpdate();
            if (linhas == 0) {
                return null;
            }
            if (linhas != vinculos.size()) {
                throw new IllegalStateException("Nem todas as permissões foram registradas.");
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao registrar permissões do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    // Lista cooperados com identificação, usuário, cooperativa, status e cargo.
    public ArrayList<Cooperado> listaCooperados() {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Cooperado> cooperados = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT c.id_cooperado, c.id_usuario, c.id_cooperativa, " +
                "c.codigo_cooperado, c.status, c.cargo, u.nome_usuario, " +
                "u.nome_completo, u.email, coop.nome_publico AS nome_cooperativa " +
                "FROM cooperado c JOIN usuario u ON c.id_usuario = u.id_usuario " +
                "JOIN cooperativa coop ON c.id_cooperativa = coop.id_cooperativa";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cooperado cooperado = new Cooperado();
                cooperado.setIdCooperado(rs.getLong("id_cooperado"));
                cooperado.setIdUsuario(rs.getLong("id_usuario"));
                cooperado.setIdCooperativa(rs.getLong("id_cooperativa"));
                cooperado.setCodigoCooperado(rs.getString("codigo_cooperado"));
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(cooperado.getIdUsuario());
                usuario.setNomeUsuario(rs.getString("nome_usuario"));
                usuario.setNomeCompleto(rs.getString("nome_completo"));
                usuario.setEmail(rs.getString("email"));
                cooperado.setUsuario(usuario);
                cooperado.getRelacionamentos().setIdCooperado(cooperado.getIdCooperado());
                cooperado.getRelacionamentos().setNomeCooperativa(rs.getString("nome_cooperativa"));
                cooperado.setStatus(StatusCooperado.valueOf(rs.getString("status")));
                String cargo = rs.getString("cargo");
                if (cargo != null) {
                    cooperado.setCargo(CargoCooperado.valueOf(cargo));
                }
                cooperados.add(cooperado);
            }
            return cooperados;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar cooperados.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza os dados de um cooperado identificado.
    public Cooperado editarCooperado(Long idCooperado, Long idUsuario,
                                     Long idCooperativa, String codigoCooperado,
                                     CargoCooperado cargo, StatusCooperado status) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        cooperado.setIdUsuario(idUsuario);
        cooperado.setIdCooperativa(idCooperativa);
        cooperado.setCodigoCooperado(codigoCooperado);
        cooperado.setCargo(cargo);
        cooperado.setStatus(status);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado SET id_usuario = ?, id_cooperativa = ?, " +
                "codigo_cooperado = ?, cargo = ?, status = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setObject(1, cooperado.getIdUsuario(), Types.BIGINT);
            pstmt.setObject(2, cooperado.getIdCooperativa(), Types.BIGINT);
            pstmt.setString(3, cooperado.getCodigoCooperado());
            if (cooperado.getCargo() == null) {
                pstmt.setNull(4, Types.VARCHAR);
            } else {
                pstmt.setString(4, cooperado.getCargo().name());
            }
            if (cooperado.getStatus() == null) {
                pstmt.setString(5, StatusCooperado.ATIVO.name());
                cooperado.setStatus(StatusCooperado.ATIVO);
            } else {
                pstmt.setString(5, cooperado.getStatus().name());
            }
            pstmt.setLong(6, cooperado.getIdCooperado());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao editar cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o cargo de um cooperado existente.
    public Cooperado alterarCargoCooperado(Long idCooperado, CargoCooperado cargo) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        cooperado.setCargo(cargo);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado SET cargo = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            if (cooperado.getCargo() == null) {
                pstmt.setNull(1, Types.VARCHAR);
            } else {
                pstmt.setString(1, cooperado.getCargo().name());
            }
            pstmt.setLong(2, cooperado.getIdCooperado());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar cargo do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera uma atividade do cooperado identificado.
    public Cooperado alterarAtividadesDesempenhadas(Long idCooperado, String atividadeAnterior,
                                                    String atividadeNova) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        cooperado.getRelacionamentos().setIdCooperado(idCooperado);
        ArrayList<AtividadeCooperado> atividades = new ArrayList<>();
        atividades.add(AtividadeCooperado.valueOf(atividadeNova));
        cooperado.getRelacionamentos().setAtividadesCooperado(atividades);
        AtividadeCooperado anterior = AtividadeCooperado.valueOf(atividadeAnterior);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado_atividade SET atividade = ? " +
                "WHERE id_cooperado = ? AND atividade = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cooperado.getRelacionamentos().getAtividadesCooperado().get(0).name());
            pstmt.setLong(2, cooperado.getIdCooperado());
            pstmt.setString(3, anterior.name());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar atividade do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o status de um cooperado existente.
    public Cooperado alterarStatusCooperado(Long idCooperado, StatusCooperado status) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        cooperado.setStatus(status);
        if (cooperado.getIdCooperado() == null || cooperado.getStatus() == null) {
            throw new IllegalArgumentException("ID e status são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado SET status = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cooperado.getStatus().name());
            pstmt.setLong(2, cooperado.getIdCooperado());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar status do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera a permissão vinculada ao cooperado identificado.
    public Cooperado alterarPermissoesAcesso(Long idCooperado, Long idPermissaoAnterior,
                                             Long idPermissaoNova) {

        // Declaração

        CooperadoPermissao anterior = new CooperadoPermissao();
        anterior.setIdCooperado(idCooperado);
        anterior.setIdPermissao(idPermissaoAnterior);
        CooperadoPermissao novo = new CooperadoPermissao();
        novo.setIdCooperado(idCooperado);
        novo.setIdPermissao(idPermissaoNova);
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        ArrayList<CooperadoPermissao> vinculos = new ArrayList<>();
        vinculos.add(novo);
        cooperado.getRelacionamentos().setIdCooperado(idCooperado);
        cooperado.getRelacionamentos().setVinculosPermissao(vinculos);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado_permissao SET id_permissao = ? " +
                "WHERE id_cooperado = ? AND id_permissao = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, novo.getIdPermissao());
            pstmt.setLong(2, novo.getIdCooperado());
            pstmt.setLong(3, anterior.getIdPermissao());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar permissão do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o nome de usuário de uma conta existente.
    public Cooperado alterarNomeUsuario(Long idUsuario, String nomeUsuario) {

        // Execução e retorno

        Usuario usuario = usuarioDAO.alterarNomeUsuario(idUsuario, nomeUsuario);
        if (usuario == null) {
            return null;
        }
        Cooperado cooperado = new Cooperado();
        cooperado.setIdUsuario(idUsuario);
        cooperado.setUsuario(usuario);
        return cooperado;
    }

    // Atualiza senha e indicador de senha temporária.
    public Cooperado alterarSenha(Long idUsuario, String senhaHash, boolean senhaTemporaria) {

        // Execução e retorno

        Usuario usuario = usuarioDAO.alterarSenha(idUsuario, senhaHash, senhaTemporaria);
        if (usuario == null) {
            return null;
        }
        Cooperado cooperado = new Cooperado();
        cooperado.setIdUsuario(idUsuario);
        cooperado.setUsuario(usuario);
        return cooperado;
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui cooperado e conta associada em um único comando SQL.
    public Cooperado excluirCooperado(Long idCooperado) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "WITH alvo AS (" +
                "SELECT c.id_cooperado, c.id_usuario FROM cooperado c " +
                "JOIN usuario u ON u.id_usuario = c.id_usuario WHERE c.id_cooperado = ?), " +
                "cooperado_apagado AS (" +
                "DELETE FROM cooperado WHERE id_cooperado IN " +
                "(SELECT id_cooperado FROM alvo) RETURNING id_usuario), " +
                "usuario_apagado AS (" +
                "DELETE FROM usuario WHERE id_usuario IN " +
                "(SELECT id_usuario FROM cooperado_apagado) RETURNING id_usuario) " +
                "SELECT id_usuario FROM usuario_apagado";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, cooperado.getIdCooperado());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                cooperado.setIdUsuario(rs.getLong("id_usuario"));
            }
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(cooperado.getIdUsuario());
            cooperado.setUsuario(usuario);
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Remove uma permissão específica de um cooperado.
    public Cooperado removerAcessoModulo(Long idCooperado, Long idPermissao) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM cooperado_permissao WHERE id_cooperado = ? AND id_permissao = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, cooperado.getIdCooperado());
            pstmt.setLong(2, idPermissao);
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao remover acesso do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }
}
