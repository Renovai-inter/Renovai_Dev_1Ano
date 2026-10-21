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

        // Declaração

        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setIdCooperativa(idCooperativa);
        Cooperado gestor = new Cooperado();
        gestor.setNomeCompleto(nomeCompleto);
        if (nomeUsuario == null || nomeUsuario.isBlank()) {
            gestor.setNomeUsuario(nomeCompleto.replace(" ", ""));
        } else {
            gestor.setNomeUsuario(nomeUsuario);
        }
        gestor.setEmail(email);
        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try {
            boolean cadastrado = gravarGestor(conexao, cooperativa, gestor, senhaTemporaria);
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
    boolean gravarGestor(Connection conexao, Cooperativa cooperativa, Cooperado gestor,
                           String senhaTemporaria) throws SQLException {

        // Declaração

        long ultimoIdUsuario = utilitario.selecionarUltimoId("usuario");
        long ultimoIdCooperado = utilitario.selecionarUltimoId("cooperado");
        long idUsuario;
        if (ultimoIdUsuario == -1) {
            idUsuario = 1;
        } else {
            idUsuario = ultimoIdUsuario + 1;
        }
        long idCooperado;
        if (ultimoIdCooperado == -1) {
            idCooperado = 1;
        } else {
            idCooperado = ultimoIdCooperado + 1;
        }
        int usuarios;
        int cooperados;

        // Comando SQL

        String sqlUsuario = "INSERT INTO usuario (id_usuario, nome_completo, nome_usuario, " +
                "senha_hash, email, tipo_usuario) VALUES (?, ?, ?, ?, ?, 'GESTOR_COOPERATIVA')";
        String sqlCooperado = "INSERT INTO cooperado (id_cooperado, id_usuario, id_cooperativa, " +
                "codigo_cooperado, cargo) VALUES (?, ?, ?, ?, 'GESTOR')";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sqlUsuario)) {
            pstmt.setLong(1, idUsuario);
            pstmt.setString(2, gestor.getNomeCompleto());
            pstmt.setString(3, gestor.getNomeUsuario());
            pstmt.setString(4, senhaTemporaria);
            pstmt.setString(5, gestor.getEmail());
            usuarios = pstmt.executeUpdate();
        }
        if (usuarios == 0) {
            return false;
        }
        try (PreparedStatement pstmt = conexao.prepareStatement(sqlCooperado)) {
            pstmt.setLong(1, idCooperado);
            pstmt.setLong(2, idUsuario);
            pstmt.setLong(3, cooperativa.getIdCooperativa());
            pstmt.setString(4, "CD-" + idCooperado);
            cooperados = pstmt.executeUpdate();
        }
        if (cooperados == 0) {
            return false;
        }
        gestor.setIdUsuario(idUsuario);
        gestor.setIdCooperado(idCooperado);
        gestor.setIdCooperativa(cooperativa.getIdCooperativa());
        gestor.setCodigoCooperado("CD-" + idCooperado);
        gestor.setCargo(CargoCooperado.GESTOR);
        gestor.setStatus(StatusCooperado.ATIVO);
        gestor.setTipoUsuario(TipoUsuario.GESTOR_COOPERATIVA);
        return true;
    }

    // Cadastra usuário e cooperado em duas gravações sucessivas.
    public Cooperado cadastrarCooperado(Long idCooperativa, String nomeCompleto,
                                       String nomeUsuario, String email, String senhaHash,
                                       boolean senhaTemporaria, CargoCooperado cargo,
                                       StatusCooperado status) {

        // Declaração

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
        Connection conexao = conn.conectar();

        // Comando SQL

        String sqlUsuario = "INSERT INTO usuario (id_usuario, nome_completo, nome_usuario, senha_hash, " +
                "senha_temporaria, email, tipo_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlCooperado = "INSERT INTO cooperado (id_cooperado, id_usuario, id_cooperativa, " +
                "codigo_cooperado, cargo, status) VALUES (?, ?, ?, ?, ?, ?)";

        // Executar PreparedStatement

        try {
            long ultimoIdUsuario = utilitario.selecionarUltimoId("usuario");
            long ultimoIdCooperado = utilitario.selecionarUltimoId("cooperado");
            long idUsuario;
            if (ultimoIdUsuario == -1) {
                idUsuario = 1;
            } else {
                idUsuario = ultimoIdUsuario + 1;
            }
            long idCooperado;
            if (ultimoIdCooperado == -1) {
                idCooperado = 1;
            } else {
                idCooperado = ultimoIdCooperado + 1;
            }
            try (PreparedStatement pstmt = conexao.prepareStatement(sqlUsuario)) {
                pstmt.setLong(1, idUsuario);
                pstmt.setString(2, cooperado.getNomeCompleto());
                pstmt.setString(3, cooperado.getNomeUsuario());
                pstmt.setString(4, senhaHash);
                pstmt.setBoolean(5, cooperado.isSenhaTemporaria());
                pstmt.setString(6, cooperado.getEmail());
                pstmt.setString(7, cooperado.getTipoUsuario().name());
                if (pstmt.executeUpdate() == 0) {
                    return null;
                }
            }
            try (PreparedStatement pstmt = conexao.prepareStatement(sqlCooperado)) {
                pstmt.setLong(1, idCooperado);
                pstmt.setLong(2, idUsuario);
                pstmt.setLong(3, cooperado.getIdCooperativa());
                if (cooperado.getCodigoCooperado() == null) {
                    cooperado.setCodigoCooperado("CD-" + idCooperado);
                }
                pstmt.setString(4, cooperado.getCodigoCooperado());
                if (cooperado.getCargo() == null) {
                    pstmt.setNull(5, Types.VARCHAR);
                } else {
                    pstmt.setString(5, cooperado.getCargo().name());
                }
                if (cooperado.getStatus() == null) {
                    cooperado.setStatus(StatusCooperado.ATIVO);
                }
                pstmt.setString(6, cooperado.getStatus().name());
                int linhas = pstmt.executeUpdate();
                if (linhas == 0) {
                    return null;
                }
                cooperado.setIdUsuario(idUsuario);
                cooperado.setIdCooperado(idCooperado);
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
        cooperado.setAtividades(atividades);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        if (cooperado.getAtividades() == null || cooperado.getAtividades().isEmpty()) {
            return null;
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO cooperado_atividade (id_cooperado, atividade) VALUES (?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                for (String atividade : cooperado.getAtividades()) {
                    pstmt.setLong(1, cooperado.getIdCooperado());
                    pstmt.setString(2, atividade);
                    if (pstmt.executeUpdate() == 0) {
                        return null;
                    }
                }
            return cooperado;
        } catch (SQLException e) {
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
                cooperado.setNomeUsuario(rs.getString("nome_usuario"));
                cooperado.setNomeCompleto(rs.getString("nome_completo"));
                cooperado.setEmail(rs.getString("email"));
                cooperado.setNomeCooperativa(rs.getString("nome_cooperativa"));
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
        ArrayList<String> atividades = new ArrayList<>();
        atividades.add(atividadeNova);
        cooperado.setAtividades(atividades);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado_atividade SET atividade = ? " +
                "WHERE id_cooperado = ? AND atividade = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cooperado.getAtividades().get(0));
            pstmt.setLong(2, cooperado.getIdCooperado());
            pstmt.setString(3, atividadeAnterior);
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
        throw new UnsupportedOperationException("Pendente: representar a permissão anterior e a nova na Model do cooperado.");
    }

    // Altera o nome de usuário de uma conta existente.
    public Cooperado alterarNomeUsuario(Long idUsuario, String nomeUsuario) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdUsuario(idUsuario);
        cooperado.setNomeUsuario(nomeUsuario);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE usuario SET nome_usuario = ? WHERE id_usuario = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cooperado.getNomeUsuario());
            pstmt.setLong(2, cooperado.getIdUsuario());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar nome de usuário.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza senha e indicador de senha temporária.
    public Cooperado alterarSenha(Long idUsuario, String senhaHash, boolean senhaTemporaria) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdUsuario(idUsuario);
        cooperado.setSenhaTemporaria(senhaTemporaria);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE usuario SET senha_hash = ?, senha_temporaria = ? WHERE id_usuario = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, senhaHash);
            pstmt.setBoolean(2, cooperado.isSenhaTemporaria());
            pstmt.setLong(3, cooperado.getIdUsuario());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar senha.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui o vínculo do cooperado identificado.
    public Cooperado excluirCooperado(Long idCooperado) {

        // Declaração

        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(idCooperado);
        if (cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM cooperado WHERE id_cooperado = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, cooperado.getIdCooperado());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
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
