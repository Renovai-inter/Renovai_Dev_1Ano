package dao;

import model.Usuario;
import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    public boolean emailExiste(String email) {
        String sql = "SELECT 1 FROM usuario WHERE LOWER(email) = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.toLowerCase());

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar e-mail.", e);
        }
    }

    // Retorna false se o e-mail já estiver cadastrado
    public boolean cadastrar(Usuario usuario) {
        String sql = "INSERT INTO usuario "
                + "(id_usuario, nome_completo, nome_usuario, senha_hash, "
                + "senha_temporaria, email, tipo_usuario) "
                + "VALUES ((SELECT COALESCE(MAX(id_usuario), 0) + 1 FROM usuario), "
                + "?, ?, ?, false, ?, ?)";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String email = usuario.getEmail().toLowerCase();

            stmt.setString(1, usuario.getNome());          // nome_completo
            stmt.setString(2, email);                      // nome_usuario
            stmt.setString(3, usuario.getSenhaHash());
            stmt.setString(4, email);                      // email
            stmt.setString(5, usuario.getTipoUsuario());   // tipo_usuario

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                return false; // e-mail ou nome de usuário já existe
            }
            throw new RuntimeException("Erro ao cadastrar usuário.", e);
        }
    }

    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT id_usuario, nome_completo, email, senha_hash, tipo_usuario "
                + "FROM usuario WHERE LOWER(email) = ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email.toLowerCase());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getLong("id_usuario"));
                    usuario.setNome(rs.getString("nome_completo"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setSenhaHash(rs.getString("senha_hash"));
                    usuario.setTipoUsuario(rs.getString("tipo_usuario"));
                    return usuario;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário.", e);
        }

        return null;
    }
}