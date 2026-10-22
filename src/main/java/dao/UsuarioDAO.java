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
    public Usuario cadastrarUsuario(String nomeCompleto, String nomeUsuario, String email,
                                    String senhaHash, boolean senhaTemporaria, TipoUsuario tipoUsuario) {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(nomeCompleto);
        usuario.setNomeUsuario(nomeUsuario);
        usuario.setEmail(email);
        usuario.setSenhaHash(senhaHash);
        usuario.setSenhaTemporaria(senhaTemporaria);
        usuario.setTipoUsuario(tipoUsuario);
        Connection conexao = conn.conectar();
        try {
            return gravarUsuario(conexao, usuario);
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar usuário.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Grava uma conta na conexão da operação chamadora.
    Usuario gravarUsuario(Connection conexao, Usuario usuario) throws SQLException {
        long ultimoId = utilitario.selecionarUltimoId("usuario");
        long idUsuario;
        if (ultimoId == -1) {
            idUsuario = 1;
        } else {
            idUsuario = ultimoId + 1;
        }
        String sql = "INSERT INTO usuario (id_usuario, nome_completo, nome_usuario, senha_hash, " +
                "senha_temporaria, email, tipo_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, idUsuario);
            pstmt.setString(2, usuario.getNomeCompleto());
            pstmt.setString(3, usuario.getNomeUsuario());
            pstmt.setString(4, usuario.getSenhaHash());
            pstmt.setBoolean(5, usuario.isSenhaTemporaria());
            pstmt.setString(6, usuario.getEmail());
            pstmt.setString(7, usuario.getTipoUsuario().name());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            usuario.setIdUsuario(idUsuario);
            return usuario;
        }
    }

    // === METODOS READ ================================================================================================

    // Informa se o e-mail já está cadastrado.
    public boolean emailExiste(String email) {
        String emailNormalizado = utilitario.filtrarEmailInstitucional(email);
        String sql = "SELECT 1 FROM usuario WHERE LOWER(email) = ?";
        try (Connection conexao = conn.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, emailNormalizado);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao verificar e-mail.", e);
        }
    }

    // Busca a conta pelo e-mail informado.
    public Usuario buscarPorEmail(String email) {
        String emailNormalizado = utilitario.filtrarEmailInstitucional(email);
        String sql = "SELECT id_usuario, nome_completo, nome_usuario, email, senha_hash, " +
                "senha_temporaria, tipo_usuario FROM usuario WHERE LOWER(email) = ?";
        try (Connection conexao = conn.conectar();
             PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, emailNormalizado);
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
    public Usuario alterarNomeUsuario(Long idUsuario, String nomeUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("idUsuario é obrigatório.");
        }
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNomeUsuario(nomeUsuario);
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
    public Usuario alterarSenha(Long idUsuario, String senhaHash, boolean senhaTemporaria) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("idUsuario é obrigatório.");
        }
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setSenhaHash(senhaHash);
        usuario.setSenhaTemporaria(senhaTemporaria);
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
    public Usuario excluirUsuario(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("idUsuario é obrigatório.");
        }
        Connection conexao = conn.conectar();
        try {
            return excluirUsuario(conexao, idUsuario);
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
