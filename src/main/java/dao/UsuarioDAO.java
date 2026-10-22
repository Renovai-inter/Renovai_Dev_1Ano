package dao;

import model.auxiliares.Usuario;
import model.enums.TipoUsuario;
import util.ConexaoBD;
import util.Utilitario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    private final ConexaoBD conn = new ConexaoBD();
    private final Utilitario utilitario = new Utilitario();

    // === METODOS CREATE ==============================================================================================

    // Cadastra uma conta de usuário com os valores informados.
    public Usuario cadastrarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getNomeCompleto() == null
                || usuario.getNomeUsuario() == null || usuario.getEmail() == null
                || usuario.getSenhaHash() == null || usuario.getTipoUsuario() == null) {
            throw new IllegalArgumentException("Dados de cadastro do usuário são obrigatórios.");
        }
        Connection conexao = conn.conectar();
        try {
            long ultimoId = utilitario.selecionarUltimoId("usuario");
            long idUsuario;
            if (ultimoId == -1) {
                idUsuario = 1;
            } else {
                idUsuario = ultimoId + 1;
            }
            String sql = sqlInserirUsuario();
            try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                preencherCadastro(pstmt, 1, idUsuario, usuario);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    usuario.setIdUsuario(rs.getLong("id_usuario"));
                }
                return usuario;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar usuário.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Fornece o mesmo cadastro de usuário às operações compostas.
    String sqlInserirUsuario() {
        return "INSERT INTO usuario (id_usuario, nome_completo, nome_usuario, senha_hash, " +
                "senha_temporaria, email, tipo_usuario) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "RETURNING id_usuario";
    }

    // Preenche os campos do cadastro de usuário a partir do índice informado.
    void preencherCadastro(PreparedStatement pstmt, int primeiroIndice, long idUsuario,
                           Usuario usuario) throws SQLException {
        pstmt.setLong(primeiroIndice, idUsuario);
        pstmt.setString(primeiroIndice + 1, usuario.getNomeCompleto());
        pstmt.setString(primeiroIndice + 2, usuario.getNomeUsuario());
        pstmt.setString(primeiroIndice + 3, usuario.getSenhaHash());
        pstmt.setBoolean(primeiroIndice + 4, usuario.isSenhaTemporaria());
        pstmt.setString(primeiroIndice + 5, usuario.getEmail());
        pstmt.setString(primeiroIndice + 6, usuario.getTipoUsuario().name());
    }

    // === METODOS READ ================================================================================================

    // Informa se o e-mail já está cadastrado.
    public boolean emailExiste(Usuario usuario) {
        if (usuario == null || usuario.getEmail() == null) {
            throw new IllegalArgumentException("email é obrigatório.");
        }
        String sql = "SELECT 1 FROM usuario WHERE LOWER(email) = ?";
        try (Connection conexao = conn.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getEmail());
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao verificar e-mail.", e);
        }
    }

    // Busca a conta pelo e-mail informado.
    public Usuario buscarPorEmail(Usuario filtro) {
        if (filtro == null || filtro.getEmail() == null) {
            throw new IllegalArgumentException("email é obrigatório.");
        }
        String sql = "SELECT id_usuario, nome_completo, nome_usuario, email, senha_hash, " +
                "senha_temporaria, tipo_usuario FROM usuario WHERE LOWER(email) = ?";
        try (Connection conexao = conn.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, filtro.getEmail());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getLong("id_usuario"));
                    usuario.setNomeCompleto(rs.getString("nome_completo"));
                    usuario.setNomeUsuario(rs.getString("nome_usuario"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenhaHash(rs.getString("senha_hash"));
                    usuario.setSenhaTemporaria(rs.getBoolean("senha_temporaria"));
                    usuario.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
                    return usuario;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar usuário.", e);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza o nome de acesso de uma conta.
    public Usuario alterarNomeUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() <= 0 || usuario.getNomeUsuario() == null) {
            throw new IllegalArgumentException("idUsuario é obrigatório.");
        }
        String sql = "UPDATE usuario SET nome_usuario = ? WHERE id_usuario = ?";
        try (Connection conexao = conn.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getNomeUsuario());
            pstmt.setLong(2, usuario.getIdUsuario());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar nome de usuário.", e);
        }
    }

    // Atualiza o hash e o indicador de senha temporária.
    public Usuario alterarSenha(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() <= 0 || usuario.getSenhaHash() == null) {
            throw new IllegalArgumentException("idUsuario é obrigatório.");
        }
        String sql = "UPDATE usuario SET senha_hash = ?, senha_temporaria = ? WHERE id_usuario = ?";
        try (Connection conexao = conn.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, usuario.getSenhaHash());
            pstmt.setBoolean(2, usuario.isSenhaTemporaria());
            pstmt.setLong(3, usuario.getIdUsuario());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar senha.", e);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui somente a conta de usuário identificada.
    public Usuario excluirUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() <= 0) {
            throw new IllegalArgumentException("idUsuario é obrigatório.");
        }
        Connection conexao = conn.conectar();
        try {
            return excluirUsuario(conexao, usuario.getIdUsuario());
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir usuário.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Exclui a conta na conexão da operação chamadora.
    Usuario excluirUsuario(Connection conexao, Long idUsuario) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, usuario.getIdUsuario());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return usuario;
        }
    }
}
