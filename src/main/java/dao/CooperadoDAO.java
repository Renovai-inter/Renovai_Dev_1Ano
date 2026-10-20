package dao;
import model.Cooperado;
import model.Cooperativa;
import model.enums.CargoCooperado;
import model.enums.StatusCooperado;
import model.enums.TipoUsuario;
import util.Conexao;
import util.Utilitario;

import java.sql.*;
import java.util.*;

public class CooperadoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();
    private Utilitario utilitario = new Utilitario();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de cooperado.
    public CooperadoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra um usuário gestor vinculado a uma cooperativa.
    public Cooperado cadastrarGestor(Long idCooperativa, String nomeCompleto,
                                    String nomeUsuario, String email, String senhaTemporaria) {
        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setIdCooperativa(idCooperativa);
        Cooperado gestor = new Cooperado();
        gestor.setNomeCompleto(nomeCompleto);
        gestor.setNomeUsuario(nomeUsuario == null || nomeUsuario.isBlank()
                ? nomeCompleto.replace(" ", "") : nomeUsuario);
        gestor.setEmail(email);
        Connection conexao = conn.conectar();
        try {
            conexao.setAutoCommit(false);
            int[] resultado = cadastrarGestor(conexao, cooperativa, gestor, senhaTemporaria);
            conexao.commit();
            return resultado[0] == 0 || resultado[1] == 0 ? null : gestor;
        } catch (SQLException e) {
            try {
                conexao.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }
            throw new RuntimeException("Falha ao cadastrar gestor.", e);
        } catch (RuntimeException e) {
            try {
                conexao.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }
            throw e;
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Cadastra o gestor usando a mesma conexão da cooperativa.
    int[] cadastrarGestor(Connection conexao, Cooperativa cooperativa, Cooperado gestor,
                         String senhaTemporaria) throws SQLException {
        long ultimoIdUsuario = utilitario.selecionarUltimoId("usuario");
        long ultimoIdCooperado = utilitario.selecionarUltimoId("cooperado");
        long idUsuario = ultimoIdUsuario == -1 ? 1 : ultimoIdUsuario + 1;
        long idCooperado = ultimoIdCooperado == -1 ? 1 : ultimoIdCooperado + 1;
        String sqlUsuario = "INSERT INTO usuario (id_usuario, nome_completo, nome_usuario, " +
                "senha_hash, email, tipo_usuario) VALUES (?, ?, ?, ?, ?, 'GESTOR_COOPERATIVA')";
        String sqlCooperado = "INSERT INTO cooperado (id_cooperado, id_usuario, id_cooperativa, " +
                "codigo_cooperado, cargo) VALUES (?, ?, ?, ?, 'GESTOR')";
        int usuarios;
        try (PreparedStatement pstmt = conexao.prepareStatement(sqlUsuario)) {
            pstmt.setLong(1, idUsuario);
            pstmt.setString(2, gestor.getNomeCompleto());
            pstmt.setString(3, gestor.getNomeUsuario());
            pstmt.setString(4, senhaTemporaria);
            pstmt.setString(5, gestor.getEmail());
            usuarios = pstmt.executeUpdate();
        }
        int cooperados;
        try (PreparedStatement pstmt = conexao.prepareStatement(sqlCooperado)) {
            pstmt.setLong(1, idCooperado);
            pstmt.setLong(2, idUsuario);
            pstmt.setLong(3, cooperativa.getIdCooperativa());
            pstmt.setString(4, "CD-" + idCooperado);
            cooperados = pstmt.executeUpdate();
        }
        gestor.setIdUsuario(idUsuario);
        gestor.setIdCooperado(idCooperado);
        gestor.setIdCooperativa(cooperativa.getIdCooperativa());
        gestor.setCodigoCooperado("CD-" + idCooperado);
        gestor.setCargo(CargoCooperado.GESTOR);
        gestor.setStatus(StatusCooperado.ATIVO);
        gestor.setTipoUsuario(TipoUsuario.GESTOR_COOPERATIVA);
        return new int[]{usuarios, cooperados};
    }

    // Cadastra usuário e cooperado na mesma transação.
    public Cooperado cadastrarCooperado(Long idCooperativa, String nomeCompleto,
                                       String nomeUsuario, String email, String senhaHash,
                                       boolean senhaTemporaria, CargoCooperado cargo,
                                       StatusCooperado status) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperativa(idCooperativa);
        cooperado.setNomeCompleto(nomeCompleto);
        cooperado.setNomeUsuario(nomeUsuario);
        cooperado.setEmail(email);
        cooperado.setSenhaTemporaria(senhaTemporaria);
        cooperado.setTipoUsuario(TipoUsuario.COOPERADO);
        cooperado.setCargo(cargo);
        cooperado.setStatus(status);
        if (cooperado.getIdCooperativa() == null || cooperado.getNomeCompleto() == null ||
                cooperado.getNomeUsuario() == null || cooperado.getEmail() == null || senhaHash == null) {
            throw new IllegalArgumentException("Dados obrigatórios do cooperado e usuário estão ausentes.");
        }
        String sqlUsuario = "INSERT INTO usuario (id_usuario, nome_completo, nome_usuario, senha_hash, " +
                "senha_temporaria, email, tipo_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCooperado = "INSERT INTO cooperado (id_cooperado, id_usuario, id_cooperativa, " +
                "codigo_cooperado, cargo, status) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conexao = conn.conectar();
        try {
            conexao.setAutoCommit(false);
            long ultimoIdUsuario = utilitario.selecionarUltimoId("usuario");
            long ultimoIdCooperado = utilitario.selecionarUltimoId("cooperado");
            long idUsuario = ultimoIdUsuario == -1 ? 1 : ultimoIdUsuario + 1;
            long idCooperado = ultimoIdCooperado == -1 ? 1 : ultimoIdCooperado + 1;
            try (PreparedStatement pstmt = conexao.prepareStatement(sqlUsuario)) {
                pstmt.setLong(1, idUsuario);
                pstmt.setString(2, cooperado.getNomeCompleto());
                pstmt.setString(3, cooperado.getNomeUsuario());
                pstmt.setString(4, senhaHash);
                pstmt.setBoolean(5, cooperado.isSenhaTemporaria());
                pstmt.setString(6, cooperado.getEmail());
                pstmt.setString(7, cooperado.getTipoUsuario() == null
                        ? TipoUsuario.COOPERADO.name() : cooperado.getTipoUsuario().name());
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conexao.prepareStatement(sqlCooperado)) {
                pstmt.setLong(1, idCooperado);
                pstmt.setLong(2, idUsuario);
                pstmt.setLong(3, cooperado.getIdCooperativa());
                pstmt.setString(4, cooperado.getCodigoCooperado() == null
                        ? "CD-" + idCooperado : cooperado.getCodigoCooperado());
                pstmt.setString(5, cooperado.getCargo() == null ? null : cooperado.getCargo().name());
                pstmt.setString(6, cooperado.getStatus() == null
                        ? StatusCooperado.ATIVO.name() : cooperado.getStatus().name());
                int linhas = pstmt.executeUpdate();
                cooperado.setIdUsuario(idUsuario);
                cooperado.setIdCooperado(idCooperado);
                if (cooperado.getCodigoCooperado() == null) {
                    cooperado.setCodigoCooperado("CD-" + idCooperado);
                }
                if (cooperado.getStatus() == null) {
                    cooperado.setStatus(StatusCooperado.ATIVO);
                }
                conexao.commit();
                return linhas == 0 ? null : cooperado;
            }
        } catch (SQLException e) {
            try {
                conexao.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }
            throw new RuntimeException("Falha ao cadastrar cooperado.", e);
        } catch (RuntimeException e) {
            try {
                conexao.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }
            throw e;
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra as atividades informadas para um cooperado existente.
    public Cooperado selecionarAtividades(Long idCooperado, ArrayList<String> atividades) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        cooperado.setAtividades(atividades);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        if (cooperado.getAtividades() == null || cooperado.getAtividades().isEmpty()) {
            return cooperado;
        }
        String sql = "INSERT INTO cooperado_atividade (id_cooperado, atividade) VALUES (?, ?)";
        Connection conexao = conn.conectar();
        try {
            conexao.setAutoCommit(false);
            int total = 0;
            try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                for (String atividade : cooperado.getAtividades()) {
                    pstmt.setLong(1, cooperado.getIdCooperado());
                    pstmt.setString(2, atividade);
                    total += pstmt.executeUpdate();
                }
            }
            conexao.commit();
            return total == 0 ? null : cooperado;
        } catch (SQLException e) {
            try {
                conexao.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }
            throw new RuntimeException("Falha ao registrar atividades do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Os parâmetros ficam preparados até a Model representar os vínculos de permissão.
    public Cooperado permissoesAcesso(Long idCooperado, ArrayList<Long> idsPermissoes) {
        throw new UnsupportedOperationException("Pendente: representar permissões na Model do cooperado.");
    }

    // === METODOS READ ================================================================================================

    // Lista cooperados com identificação, usuário, status e cargo.
    public ArrayList<Cooperado> listaCooperados() {
        String sql = "SELECT c.id_cooperado, c.id_usuario, c.id_cooperativa, " +
                "c.codigo_cooperado, c.status, c.cargo, u.nome_usuario " +
                "FROM cooperado c JOIN usuario u ON c.id_usuario = u.id_usuario";
        Connection conexao = conn.conectar();
        ArrayList<Cooperado> cooperados = new ArrayList<>();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Cooperado cooperado = new Cooperado();
                cooperado.setIdCooperado(rs.getLong("id_cooperado"));
                cooperado.setIdUsuario(rs.getLong("id_usuario"));
                cooperado.setIdCooperativa(rs.getLong("id_cooperativa"));
                cooperado.setCodigoCooperado(rs.getString("codigo_cooperado"));
                cooperado.setNomeUsuario(rs.getString("nome_usuario"));
                cooperado.setStatus(StatusCooperado.valueOf(rs.getString("status")));
                String cargo = rs.getString("cargo");
                cooperado.setCargo(cargo == null ? null : CargoCooperado.valueOf(cargo));
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
        String sql = "UPDATE cooperado SET id_usuario = ?, id_cooperativa = ?, " +
                "codigo_cooperado = ?, cargo = ?, status = ? WHERE id_cooperado = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setObject(1, cooperado.getIdUsuario(), Types.BIGINT);
            pstmt.setObject(2, cooperado.getIdCooperativa(), Types.BIGINT);
            pstmt.setString(3, cooperado.getCodigoCooperado());
            pstmt.setString(4, cooperado.getCargo() == null ? null : cooperado.getCargo().name());
            pstmt.setString(5, cooperado.getStatus() == null ? null : cooperado.getStatus().name());
            pstmt.setLong(6, cooperado.getIdCooperado());
            return pstmt.executeUpdate() == 0 ? null : cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao editar cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o cargo de um cooperado existente.
    public Cooperado alterarCargoCooperado(Long idCooperado, CargoCooperado cargo) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        cooperado.setCargo(cargo);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        String sql = "UPDATE cooperado SET cargo = ? WHERE id_cooperado = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cooperado.getCargo() == null ? null : cooperado.getCargo().name());
            pstmt.setLong(2, cooperado.getIdCooperado());
            return pstmt.executeUpdate() == 0 ? null : cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar cargo do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera uma atividade do cooperado identificado.
    public Cooperado alterarAtividadesDesempenhadas(Long idCooperado, String atividadeAnterior,
                                                    String atividadeNova) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        ArrayList<String> atividades = new ArrayList<>();
        atividades.add(atividadeNova);
        cooperado.setAtividades(atividades);
        String sql = "UPDATE cooperado_atividade SET atividade = ? " +
                "WHERE id_cooperado = ? AND atividade = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, atividadeNova);
            pstmt.setLong(2, idCooperado);
            pstmt.setString(3, atividadeAnterior);
            return pstmt.executeUpdate() == 0 ? null : cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar atividade do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o status de um cooperado existente.
    public Cooperado alterarStatusCooperado(Long idCooperado, StatusCooperado status) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        cooperado.setStatus(status);
        if (cooperado.getIdCooperado() == null || cooperado.getStatus() == null) {
            throw new IllegalArgumentException("ID e status são obrigatórios.");
        }
        String sql = "UPDATE cooperado SET status = ? WHERE id_cooperado = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cooperado.getStatus().name());
            pstmt.setLong(2, cooperado.getIdCooperado());
            return pstmt.executeUpdate() == 0 ? null : cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar status do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera a permissão vinculada ao cooperado identificado.
    public Cooperado alterarPermissoesAcesso(Long idCooperado, Long idPermissaoAnterior,
                                             Long idPermissaoNova) {
        throw new UnsupportedOperationException("Pendente: representar a permissão anterior e a nova na Model do cooperado.");
    }

    // Altera o nome de usuário de uma conta existente.
    public Cooperado alterarNomeUsuario(Long idUsuario, String nomeUsuario) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdUsuario(idUsuario);
        cooperado.setNomeUsuario(nomeUsuario);
        String sql = "UPDATE usuario SET nome_usuario = ? WHERE id_usuario = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, nomeUsuario);
            pstmt.setLong(2, idUsuario);
            return pstmt.executeUpdate() == 0 ? null : cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar nome de usuário.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza senha e indicador de senha temporária.
    public Cooperado alterarSenha(Long idUsuario, String senhaHash, boolean senhaTemporaria) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdUsuario(idUsuario);
        cooperado.setSenhaTemporaria(senhaTemporaria);
        String sql = "UPDATE usuario SET senha_hash = ?, senha_temporaria = ? WHERE id_usuario = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, senhaHash);
            pstmt.setBoolean(2, senhaTemporaria);
            pstmt.setLong(3, idUsuario);
            return pstmt.executeUpdate() == 0 ? null : cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar senha.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui o vínculo do cooperado identificado.
    public Cooperado excluirCooperado(Long idCooperado) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        String sql = "DELETE FROM cooperado WHERE id_cooperado = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, cooperado.getIdCooperado());
            return pstmt.executeUpdate() == 0 ? null : cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Remove uma permissão específica de um cooperado.
    public Cooperado removerAcessoModulo(Long idCooperado, Long idPermissao) {
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        String sql = "DELETE FROM cooperado_permissao WHERE id_cooperado = ? AND id_permissao = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, idCooperado);
            pstmt.setLong(2, idPermissao);
            return pstmt.executeUpdate() == 0 ? null : cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao remover acesso do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }
}
