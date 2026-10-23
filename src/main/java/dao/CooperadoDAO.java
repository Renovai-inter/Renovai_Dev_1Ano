package dao;
import model.principais.Cooperado;
import model.auxiliares.CooperadoPermissao;
import model.enums.AtividadeCooperado;
import model.enums.CargoCooperado;
import model.enums.StatusCooperado;
import model.enums.TipoUsuario;
import util.ConexaoBD;
import util.SenhaAPI;
import util.Utilitario;

import java.sql.*;
import java.text.Normalizer;
import java.security.SecureRandom;
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
    public int cadastrarGestor(Cooperado gestor) {
        if (gestor == null || gestor.getIdCooperativa() == null
                || gestor.getNomeCompleto() == null || gestor.getEmail() == null
                || gestor.getSenhaHash() == null) {
            throw new IllegalArgumentException("Dados do gestor são obrigatórios.");
        }
        if (gestor.getNomeUsuario() == null || gestor.getNomeUsuario().isBlank()) {
            gestor.setNomeUsuario(gestor.getNomeCompleto().replace(" ", ""));
        }
        gestor.setSenhaTemporaria(true);
        gestor.setTipoUsuario(TipoUsuario.GESTOR_COOPERATIVA);
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
        Connection conexao = conn.conectar();
        String sqlCooperado = "INSERT INTO cooperado " +
                "(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement inserirUsuario = conexao.prepareStatement(usuarioDAO.sqlInserirUsuario());
             PreparedStatement inserirCooperado = conexao.prepareStatement(sqlCooperado)) {
            usuarioDAO.preencherCadastro(inserirUsuario, 1, idUsuario, gestor);
            int linhas = inserirUsuario.executeUpdate();
            if (linhas == 0) {
                return 0;
            }
            gestor.setIdUsuario(idUsuario);
            inserirCooperado.setLong(1, idCooperado);
            inserirCooperado.setLong(2, idUsuario);
            inserirCooperado.setLong(3, gestor.getIdCooperativa());
            inserirCooperado.setString(4, gestor.getCodigoCooperado());
            inserirCooperado.setString(5, gestor.getCargoEnum().name());
            int linhasCooperado = inserirCooperado.executeUpdate();
            if (linhasCooperado == 0) {
                throw new IllegalStateException("Gestor não foi registrado após a conta.");
            }
            linhas += linhasCooperado;
            gestor.setIdCooperado(idCooperado);
            gestor.setPermissoes(listarIdsPermissoes(conexao));
            linhas += permissoesAcesso(gestor);
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar gestor.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Cadastra a conta, o cooperado, suas atividades e permissões.
    public int cadastrarCooperado(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperativa() == null
                || cooperado.getNomeCompleto() == null || cooperado.getEmail() == null) {
            throw new IllegalArgumentException("Cooperativa, nome e e-mail são obrigatórios.");
        }
        for (AtividadeCooperado atividade : cooperado.getAtividadesCooperado()) {
            if (atividade == null) {
                throw new IllegalArgumentException("Atividade do cooperado é obrigatória.");
            }
        }
        ArrayList<Long> permissoes = cooperado.getPermissoes();
        String senhaInicial = Long.toUnsignedString(new SecureRandom().nextLong(), 36)
                + Long.toUnsignedString(new SecureRandom().nextLong(), 36);
        cooperado.setSenhaHash(SenhaAPI.gerarHash(senhaInicial));
        cooperado.setSenhaTemporaria(true);
        cooperado.setTipoUsuario(TipoUsuario.COOPERADO);
        cooperado.setStatus(StatusCooperado.ATIVO);
        Connection conexao = conn.conectar();
        String sqlCooperado = "INSERT INTO cooperado " +
                "(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement inserirUsuario = conexao.prepareStatement(usuarioDAO.sqlInserirUsuario());
             PreparedStatement inserirCooperado = conexao.prepareStatement(sqlCooperado)) {
            ArrayList<Long> catalogo = listarIdsPermissoes(conexao);
            if (cooperado.getCargoEnum() == CargoCooperado.GESTOR) {
                cooperado.setPermissoes(catalogo);
            } else if (new HashSet<>(permissoes).size() != permissoes.size()
                    || !catalogo.containsAll(permissoes)) {
                throw new IllegalArgumentException("Há permissões duplicadas ou inexistentes.");
            }
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
            cooperado.setNomeUsuario(gerarNomeUsuario(conexao, cooperado.getNomeCompleto(), idUsuario));
            cooperado.setCodigoCooperado("CD-" + idCooperado);
            usuarioDAO.preencherCadastro(inserirUsuario, 1, idUsuario, cooperado);
            int linhas = inserirUsuario.executeUpdate();
            if (linhas == 0) {
                return 0;
            }
            cooperado.setIdUsuario(idUsuario);
            inserirCooperado.setLong(1, idCooperado);
            inserirCooperado.setLong(2, idUsuario);
            inserirCooperado.setLong(3, cooperado.getIdCooperativa());
            inserirCooperado.setString(4, cooperado.getCodigoCooperado());
            if (cooperado.getCargoEnum() == null) {
                inserirCooperado.setNull(5, Types.VARCHAR);
            } else {
                inserirCooperado.setString(5, cooperado.getCargoEnum().name());
            }
            inserirCooperado.setString(6, cooperado.getStatusEnum().name());
            int linhasCooperado = inserirCooperado.executeUpdate();
            if (linhasCooperado == 0) {
                throw new IllegalStateException("Cooperado não foi registrado após a conta.");
            }
            linhas += linhasCooperado;
            cooperado.setIdCooperado(idCooperado);
            linhas += selecionarAtividades(cooperado).size();
            linhas += permissoesAcesso(cooperado);
            cooperado.setSenhaInicial(senhaInicial);
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra as atividades informadas para um cooperado existente.
    public ArrayList<AtividadeCooperado> selecionarAtividades(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        ArrayList<AtividadeCooperado> atividades = cooperado.getAtividadesCooperado();
        if (atividades.isEmpty()) {
            return atividades;
        }
        for (AtividadeCooperado atividade : atividades) {
            if (atividade == null) {
                throw new IllegalArgumentException("Atividade do cooperado é obrigatória.");
            }
        }
        Connection conexao = conn.conectar();
        String sql = "INSERT INTO cooperado_atividade (id_cooperado, atividade) VALUES (?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            for (AtividadeCooperado atividade : atividades) {
                pstmt.setLong(1, cooperado.getIdCooperado());
                pstmt.setString(2, atividade.name());
                if (pstmt.executeUpdate() == 0) {
                    throw new IllegalStateException("Atividade do cooperado não foi registrada.");
                }
            }
            return atividades;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao registrar atividades do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra as permissões selecionadas para um cooperado.
    public int permissoesAcesso(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        ArrayList<Long> permissoes = cooperado.getPermissoes();
        if (permissoes.isEmpty()) {
            return 0;
        }
        for (Long idPermissao : permissoes) {
            if (idPermissao == null) {
                throw new IllegalArgumentException("idPermissao é obrigatório.");
            }
        }
        Connection conexao = conn.conectar();
        String sql = "INSERT INTO cooperado_permissao (id_cooperado, id_permissao) VALUES (?, ?)";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            int linhas = 0;
            for (Long idPermissao : permissoes) {
                pstmt.setLong(1, cooperado.getIdCooperado());
                pstmt.setLong(2, idPermissao);
                int alteradas = pstmt.executeUpdate();
                if (alteradas == 0) {
                    throw new IllegalStateException("Permissão do cooperado não foi registrada.");
                }
                linhas += alteradas;
            }
            return linhas;
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

    // Busca o cooperado e os vínculos necessários às telas de detalhe e edição.
    public Cooperado buscarCooperadoPorId(Cooperado filtro) {
        if (filtro == null || filtro.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        String sql = "SELECT c.id_cooperado, c.id_usuario, c.id_cooperativa, " +
                "c.codigo_cooperado, c.cargo, c.status, c.data_cadastro, " +
                "u.nome_completo, u.nome_usuario, u.email, u.senha_temporaria, " +
                "coop.nome_publico AS nome_cooperativa " +
                "FROM cooperado c JOIN usuario u ON c.id_usuario = u.id_usuario " +
                "JOIN cooperativa coop ON c.id_cooperativa = coop.id_cooperativa " +
                "WHERE c.id_cooperado = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, filtro.getIdCooperado());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Cooperado cooperado = new Cooperado();
                cooperado.setIdCooperado(rs.getLong("id_cooperado"));
                cooperado.setIdUsuario(rs.getLong("id_usuario"));
                cooperado.setIdCooperativa(rs.getLong("id_cooperativa"));
                cooperado.setCodigoCooperado(rs.getString("codigo_cooperado"));
                cooperado.setNomeCompleto(rs.getString("nome_completo"));
                cooperado.setNomeUsuario(rs.getString("nome_usuario"));
                cooperado.setEmail(rs.getString("email"));
                cooperado.setSenhaTemporaria(rs.getBoolean("senha_temporaria"));
                cooperado.setNomeCooperativa(rs.getString("nome_cooperativa"));
                cooperado.setStatus(StatusCooperado.valueOf(rs.getString("status")));
                String cargo = rs.getString("cargo");
                if (cargo != null) {
                    cooperado.setCargo(CargoCooperado.valueOf(cargo));
                }
                Timestamp data = rs.getTimestamp("data_cadastro");
                if (data != null) {
                    cooperado.setDataCadastro(data.toLocalDateTime());
                }
                cooperado.setAtividadesCooperado(listarAtividades(conexao, filtro.getIdCooperado()));
                cooperado.setPermissoes(listarPermissoes(conexao, filtro.getIdCooperado()));
                return cooperado;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Retorna os IDs das permissões vinculadas ao cooperado.
    public ArrayList<Long> listarPermissoes(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();
        try {
            return listarPermissoes(conexao, cooperado.getIdCooperado());
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar permissões do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    private ArrayList<Long> listarPermissoes(Connection conexao, Long idCooperado)
            throws SQLException {
        ArrayList<Long> permissoes = new ArrayList<>();
        String sql = "SELECT id_permissao FROM cooperado_permissao WHERE id_cooperado = ? " +
                "ORDER BY id_permissao";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, idCooperado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    permissoes.add(rs.getLong("id_permissao"));
                }
            }
        }
        return permissoes;
    }

    private ArrayList<Long> listarIdsPermissoes(Connection conexao) throws SQLException {
        ArrayList<Long> permissoes = new ArrayList<>();
        String sql = "SELECT id_permissao FROM permissao ORDER BY id_permissao";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                permissoes.add(rs.getLong("id_permissao"));
            }
        }
        return permissoes;
    }

    // Retorna o catálogo de permissões para a tela.
    public LinkedHashMap<Long, String> listarTodasPermissoes() {
        LinkedHashMap<Long, String> permissoes = new LinkedHashMap<>();
        String sql = "SELECT id_permissao, nome FROM permissao ORDER BY id_permissao";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                permissoes.put(rs.getLong("id_permissao"), rs.getString("nome"));
            }
            return permissoes;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar permissões.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Retorna as cooperativas disponíveis para seleção.
    public LinkedHashMap<Long, String> listarCooperativas() {
        LinkedHashMap<Long, String> cooperativas = new LinkedHashMap<>();
        String sql = "SELECT id_cooperativa, nome_publico FROM cooperativa ORDER BY nome_publico";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                cooperativas.put(rs.getLong("id_cooperativa"), rs.getString("nome_publico"));
            }
            return cooperativas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar cooperativas.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Conta as coletas que ainda referenciam o cooperado.
    public int contarColetasDoCooperado(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        String sql = "SELECT COUNT(*) FROM coleta WHERE id_cooperado_responsavel = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, cooperado.getIdCooperado());
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao contar coletas do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    private ArrayList<AtividadeCooperado> listarAtividades(Connection conexao, Long idCooperado)
            throws SQLException {
        ArrayList<AtividadeCooperado> atividades = new ArrayList<>();
        String sql = "SELECT atividade FROM cooperado_atividade WHERE id_cooperado = ? " +
                "ORDER BY atividade";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, idCooperado);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    atividades.add(AtividadeCooperado.valueOf(rs.getString("atividade")));
                }
            }
        }
        return atividades;
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza dados, permissões de gestor e atividades do cooperado em um comando.
    public int editarCooperado(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperado() == null
                || cooperado.getIdCooperativa() == null || cooperado.getStatusEnum() == null
                || cooperado.getNomeCompleto() == null || cooperado.getEmail() == null) {
            throw new IllegalArgumentException("ID, cooperativa e status são obrigatórios.");
        }
        for (AtividadeCooperado atividade : cooperado.getAtividadesCooperado()) {
            if (atividade == null) {
                throw new IllegalArgumentException("Atividade do cooperado é obrigatória.");
            }
        }
        Connection conexao = conn.conectar();
        String sqlUsuario = "SELECT id_usuario FROM cooperado WHERE id_cooperado = ?";
        String sqlCooperado = "UPDATE cooperado SET id_cooperativa = ?, cargo = ?, status = ? " +
                "WHERE id_cooperado = ?";
        String sqlAlterarUsuario = "UPDATE usuario SET nome_completo = ?, email = ? " +
                "WHERE id_usuario = ?";
        String sqlExcluirAtividades = "DELETE FROM cooperado_atividade WHERE id_cooperado = ?";
        try (PreparedStatement buscar = conexao.prepareStatement(sqlUsuario);
             PreparedStatement alterar = conexao.prepareStatement(sqlCooperado);
             PreparedStatement alterarUsuario = conexao.prepareStatement(sqlAlterarUsuario);
             PreparedStatement excluirAtividades = conexao.prepareStatement(sqlExcluirAtividades)) {
            buscar.setLong(1, cooperado.getIdCooperado());
            try (ResultSet rs = buscar.executeQuery()) {
                if (!rs.next()) {
                    return 0;
                }
                cooperado.setIdUsuario(rs.getLong("id_usuario"));
            }
            alterar.setLong(1, cooperado.getIdCooperativa());
            if (cooperado.getCargoEnum() == null) {
                alterar.setNull(2, Types.VARCHAR);
            } else {
                alterar.setString(2, cooperado.getCargoEnum().name());
            }
            alterar.setString(3, cooperado.getStatusEnum().name());
            alterar.setLong(4, cooperado.getIdCooperado());
            int linhas = alterar.executeUpdate();
            if (linhas == 0) {
                return 0;
            }
            alterarUsuario.setString(1, cooperado.getNomeCompleto());
            alterarUsuario.setString(2, cooperado.getEmail());
            alterarUsuario.setLong(3, cooperado.getIdUsuario());
            int linhasUsuario = alterarUsuario.executeUpdate();
            if (linhasUsuario == 0) {
                throw new IllegalStateException("Usuário do cooperado não foi atualizado.");
            }
            linhas += linhasUsuario;
            if (cooperado.getCargoEnum() == CargoCooperado.GESTOR) {
                ArrayList<Long> catalogo = listarIdsPermissoes(conexao);
                ArrayList<Long> atuais = listarPermissoes(conexao, cooperado.getIdCooperado());
                catalogo.removeAll(atuais);
                Cooperado vinculos = new Cooperado();
                vinculos.setIdCooperado(cooperado.getIdCooperado());
                vinculos.setPermissoes(catalogo);
                linhas += permissoesAcesso(vinculos);
            }
            excluirAtividades.setLong(1, cooperado.getIdCooperado());
            linhas += excluirAtividades.executeUpdate();
            linhas += selecionarAtividades(cooperado).size();
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao editar cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o cargo de um cooperado existente.
    public int alterarCargoCooperado(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();
        String sql = "UPDATE cooperado SET cargo = ? WHERE id_cooperado = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            if (cooperado.getCargoEnum() == null) {
                pstmt.setNull(1, Types.VARCHAR);
            } else {
                pstmt.setString(1, cooperado.getCargoEnum().name());
            }
            pstmt.setLong(2, cooperado.getIdCooperado());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0 && cooperado.getCargoEnum() == CargoCooperado.GESTOR) {
                ArrayList<Long> catalogo = listarIdsPermissoes(conexao);
                ArrayList<Long> atuais = listarPermissoes(conexao, cooperado.getIdCooperado());
                catalogo.removeAll(atuais);
                Cooperado vinculos = new Cooperado();
                vinculos.setIdCooperado(cooperado.getIdCooperado());
                vinculos.setPermissoes(catalogo);
                linhas += permissoesAcesso(vinculos);
            }
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar cargo do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o status de um cooperado existente.
    public int alterarStatusCooperado(Cooperado cooperado) {

        // Declaração

        if (cooperado == null || cooperado.getIdCooperado() == null
                || cooperado.getStatusEnum() == null) {
            throw new IllegalArgumentException("ID e status são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado SET status = ? WHERE id_cooperado = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, cooperado.getStatusEnum().name());
            pstmt.setLong(2, cooperado.getIdCooperado());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar status do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera a permissão vinculada ao cooperado identificado.
    public int alterarPermissoesAcesso(CooperadoPermissao anterior,
                                             CooperadoPermissao nova) {

        // Declaração

        if (anterior == null || nova == null || anterior.getIdCooperado() == null
                || anterior.getIdPermissao() == null || nova.getIdCooperado() == null
                || nova.getIdPermissao() == null
                || !anterior.getIdCooperado().equals(nova.getIdCooperado())) {
            throw new IllegalArgumentException("Vínculos de permissão são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperado_permissao SET id_permissao = ? " +
                "WHERE id_cooperado = ? AND id_permissao = ? " +
                "AND NOT EXISTS (SELECT 1 FROM cooperado WHERE id_cooperado = ? " +
                "AND cargo = 'GESTOR')";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, nova.getIdPermissao());
            pstmt.setLong(2, nova.getIdCooperado());
            pstmt.setLong(3, anterior.getIdPermissao());
            pstmt.setLong(4, nova.getIdCooperado());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar permissão do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o nome de usuário de uma conta existente.
    public int alterarNomeUsuario(Cooperado cooperado) {
        return usuarioDAO.alterarNomeUsuario(cooperado);
    }

    // Atualiza senha e indicador de senha temporária.
    public int alterarSenha(Cooperado cooperado) {
        return usuarioDAO.alterarSenha(cooperado);
    }

    // Gera uma nova senha temporária para a conta vinculada ao cooperado.
    public int redefinirSenha(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        String senha = Long.toUnsignedString(new SecureRandom().nextLong(), 36)
                + Long.toUnsignedString(new SecureRandom().nextLong(), 36);
        String hash = SenhaAPI.gerarHash(senha);
        String sql = "UPDATE usuario SET senha_hash = ?, senha_temporaria = TRUE " +
                "WHERE id_usuario = (SELECT id_usuario FROM cooperado WHERE id_cooperado = ?)";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, hash);
            pstmt.setLong(2, cooperado.getIdCooperado());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                cooperado.setSenhaHash(hash);
                cooperado.setSenhaTemporaria(true);
                cooperado.setSenhaInicial(senha);
            }
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao redefinir senha do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Marca o cooperado como inativo sem apagar seu histórico.
    public int inativarCooperado(Cooperado cooperado) {
        if (cooperado == null) {
            throw new IllegalArgumentException("cooperado é obrigatório.");
        }
        cooperado.setStatus(StatusCooperado.INATIVO);
        return alterarStatusCooperado(cooperado);
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui cooperado e conta associada em comandos separados.
    public int excluirCooperado(Cooperado cooperado) {
        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();
        String sqlBuscar = "SELECT id_usuario FROM cooperado WHERE id_cooperado = ?";
        String sqlExcluir = "DELETE FROM cooperado WHERE id_cooperado = ?";
        try (PreparedStatement buscar = conexao.prepareStatement(sqlBuscar);
             PreparedStatement excluir = conexao.prepareStatement(sqlExcluir)) {
            buscar.setLong(1, cooperado.getIdCooperado());
            try (ResultSet rs = buscar.executeQuery()) {
                if (!rs.next()) {
                    return 0;
                }
                cooperado.setIdUsuario(rs.getLong("id_usuario"));
            }
            excluir.setLong(1, cooperado.getIdCooperado());
            int linhas = excluir.executeUpdate();
            if (linhas == 0) {
                return 0;
            }
            int linhasUsuario = usuarioDAO.excluirUsuario(conexao, cooperado.getIdUsuario());
            if (linhasUsuario == 0) {
                throw new IllegalStateException("Conta do cooperado não foi excluída.");
            }
            return linhas + linhasUsuario;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Remove uma permissão específica de um cooperado.
    public int removerAcessoModulo(CooperadoPermissao vinculo) {

        // Declaração

        if (vinculo == null || vinculo.getIdCooperado() == null
                || vinculo.getIdPermissao() == null) {
            throw new IllegalArgumentException("Vínculo de permissão é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM cooperado_permissao WHERE id_cooperado = ? " +
                "AND id_permissao = ? AND NOT EXISTS (SELECT 1 FROM cooperado " +
                "WHERE id_cooperado = ? AND cargo = 'GESTOR')";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, vinculo.getIdCooperado());
            pstmt.setLong(2, vinculo.getIdPermissao());
            pstmt.setLong(3, vinculo.getIdCooperado());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao remover acesso do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Gera um nome de acesso a partir do nome, consultando os já existentes.
    private String gerarNomeUsuario(Connection conexao, String nomeCompleto, long idUsuario)
            throws SQLException {
        String limpo = Normalizer.normalize(nomeCompleto.trim().toLowerCase(Locale.ROOT),
                Normalizer.Form.NFD).replaceAll("[^a-z ]", "");
        String[] partes = limpo.trim().split(" +");
        String base;
        if (partes.length == 0 || partes[0].isBlank()) {
            base = "cooperado" + idUsuario;
        } else if (partes.length > 1) {
            base = partes[0] + "." + partes[partes.length - 1];
        } else {
            base = partes[0];
        }
        if (base.length() > 40) {
            base = base.substring(0, 40);
        }
        String candidato = base;
        int numero = 1;
        String sql = "SELECT 1 FROM usuario WHERE nome_usuario = ?";
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            while (true) {
                pstmt.setString(1, candidato.toUpperCase(Locale.ROOT));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return candidato;
                    }
                }
                numero++;
                candidato = base + numero;
            }
        }
    }
}
