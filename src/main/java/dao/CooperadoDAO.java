package dao;
import model.principais.Cooperado;
import model.principais.Cooperativa;
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
    public Cooperado cadastrarGestor(Cooperado gestor) {

        // Declaração

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
        Connection conexao = conn.conectar();

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
            gestor.setCodigoCooperado("CD-" + idCooperado);
            gestor.setCargo(CargoCooperado.GESTOR);
            gestor.setStatus(StatusCooperado.ATIVO);

            // Comando SQL

            String sql = "WITH usuario_novo AS (" + usuarioDAO.sqlInserirUsuario() + "), " +
                    "gestor_novo AS (INSERT INTO cooperado " +
                    "(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo) " +
                    "SELECT ?, u.id_usuario, ?, ?, ? FROM usuario_novo u " +
                    "RETURNING id_cooperado, id_usuario), " +
                    "permissoes_novas AS (INSERT INTO cooperado_permissao " +
                    "(id_cooperado, id_permissao) SELECT g.id_cooperado, p.id_permissao " +
                    "FROM gestor_novo g CROSS JOIN permissao p RETURNING id_cooperado) " +
                    "SELECT g.id_usuario, (SELECT COUNT(*) FROM permissoes_novas) " +
                    "AS total_permissoes FROM gestor_novo g";

            // Executar PreparedStatement

            try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                usuarioDAO.preencherCadastro(pstmt, 1, idUsuario, gestor);
                pstmt.setLong(8, idCooperado);
                pstmt.setLong(9, gestor.getIdCooperativa());
                pstmt.setString(10, gestor.getCodigoCooperado());
                pstmt.setString(11, gestor.getCargoEnum().name());
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    gestor.setIdUsuario(rs.getLong("id_usuario"));
                }
            }
            gestor.setIdCooperado(idCooperado);
            return gestor;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar gestor.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Cadastra a conta, o cooperado, suas atividades e permissões.
    public Cooperado cadastrarCooperado(Cooperado cooperado) {

        // Declaração

        if (cooperado == null || cooperado.getIdCooperativa() == null
                || cooperado.getNomeCompleto() == null || cooperado.getEmail() == null) {
            throw new IllegalArgumentException("Cooperativa, nome e e-mail são obrigatórios.");
        }
        ArrayList<AtividadeCooperado> atividades = cooperado.getAtividadesCooperado();
        ArrayList<Long> permissoes = cooperado.getPermissoes();
        String senhaInicial = Long.toUnsignedString(new SecureRandom().nextLong(), 36)
                + Long.toUnsignedString(new SecureRandom().nextLong(), 36);
        cooperado.setSenhaHash(SenhaAPI.gerarHash(senhaInicial));
        cooperado.setSenhaTemporaria(true);
        cooperado.setTipoUsuario(TipoUsuario.COOPERADO);
        cooperado.setStatus(StatusCooperado.ATIVO);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "WITH usuario_novo AS (" + usuarioDAO.sqlInserirUsuario() + "), " +
                "cooperado_novo AS (INSERT INTO cooperado " +
                "(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo, status) " +
                "SELECT ?, u.id_usuario, ?, ?, ?, ? FROM usuario_novo u " +
                "RETURNING id_cooperado, id_usuario), " +
                "atividades_novas AS (INSERT INTO cooperado_atividade (id_cooperado, atividade) " +
                "SELECT c.id_cooperado, a.atividade FROM cooperado_novo c " +
                "CROSS JOIN unnest(?::varchar[]) AS a(atividade) RETURNING id_cooperado), " +
                "permissoes_novas AS (INSERT INTO cooperado_permissao (id_cooperado, id_permissao) " +
                "SELECT c.id_cooperado, p.id_permissao FROM cooperado_novo c " +
                "CROSS JOIN permissao p WHERE ? OR p.id_permissao = ANY (?::bigint[]) " +
                "RETURNING id_cooperado) " +
                "SELECT c.id_usuario, (SELECT COUNT(*) FROM atividades_novas) AS total_atividades, " +
                "(SELECT COUNT(*) FROM permissoes_novas) AS total_permissoes FROM cooperado_novo c";

        // Executar PreparedStatement

        try {
            ArrayList<Long> catalogoPermissoes = listarIdsPermissoes(conexao);
            if (cooperado.getCargoEnum() == CargoCooperado.GESTOR) {
                cooperado.setPermissoes(catalogoPermissoes);
            } else if (new HashSet<>(permissoes).size() != permissoes.size()
                    || !catalogoPermissoes.containsAll(permissoes)) {
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
            try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                usuarioDAO.preencherCadastro(pstmt, 1, idUsuario, cooperado);
                pstmt.setLong(8, idCooperado);
                pstmt.setLong(9, cooperado.getIdCooperativa());
                pstmt.setString(10, cooperado.getCodigoCooperado());
                if (cooperado.getCargoEnum() == null) {
                    pstmt.setNull(11, Types.VARCHAR);
                } else {
                    pstmt.setString(11, cooperado.getCargoEnum().name());
                }
                pstmt.setString(12, cooperado.getStatusEnum().name());
                ArrayList<String> nomesAtividades = new ArrayList<>();
                for (AtividadeCooperado atividade : cooperado.getAtividadesCooperado()) {
                    nomesAtividades.add(atividade.name());
                }
                pstmt.setArray(13, conexao.createArrayOf("varchar", nomesAtividades.toArray()));
                boolean todasPermissoes = cooperado.getCargoEnum() == CargoCooperado.GESTOR;
                pstmt.setBoolean(14, todasPermissoes);
                pstmt.setArray(15, conexao.createArrayOf("bigint", permissoes.toArray()));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    cooperado.setIdUsuario(rs.getLong("id_usuario"));
                }
                cooperado.setIdCooperado(idCooperado);
                cooperado.setSenhaInicial(senhaInicial);
                return cooperado;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra as atividades informadas para um cooperado existente.
    public Cooperado selecionarAtividades(Cooperado cooperado) {

        // Declaração

        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        if (cooperado.getAtividadesCooperado().isEmpty()) {
            return null;
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        StringBuilder sql = new StringBuilder("INSERT INTO cooperado_atividade " +
                "(id_cooperado, atividade) VALUES ");
        for (int i = 0; i < cooperado.getAtividadesCooperado().size(); i++) {
            if (i > 0) {
                sql.append(", ");
            }
            sql.append("(?, ?)");
        }

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql.toString())) {
            int indice = 1;
            for (AtividadeCooperado atividade : cooperado.getAtividadesCooperado()) {
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
    public Cooperado permissoesAcesso(Cooperado cooperado) {

        // Declaração

        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        ArrayList<Long> idsPermissoes = cooperado.getPermissoes();
        ArrayList<Long> vinculos = new ArrayList<>();
        for (Long idPermissao : idsPermissoes) {
            if (idPermissao == null) {
                throw new IllegalArgumentException("idPermissao é obrigatório.");
            }
            vinculos.add(idPermissao);
        }
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
            for (Long idPermissao : vinculos) {
                pstmt.setLong(indice, cooperado.getIdCooperado());
                indice++;
                pstmt.setLong(indice, idPermissao);
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

    // Atualiza os dados de um cooperado identificado.
    public Cooperado editarCooperado(Cooperado cooperado) {

        // Declaração

        if (cooperado == null || cooperado.getIdCooperado() == null
                || cooperado.getIdCooperativa() == null || cooperado.getStatusEnum() == null
                || cooperado.getNomeCompleto() == null || cooperado.getEmail() == null) {
            throw new IllegalArgumentException("ID, cooperativa e status são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "WITH cooperado_atualizado AS (" +
                "UPDATE cooperado SET id_cooperativa = ?, cargo = ?, status = ? " +
                "WHERE id_cooperado = ? RETURNING id_cooperado, id_usuario), " +
                "usuario_atualizado AS (UPDATE usuario SET nome_completo = ?, email = ? " +
                "WHERE id_usuario IN (SELECT id_usuario FROM cooperado_atualizado) " +
                "RETURNING id_usuario), " +
                "permissoes_gestor AS (INSERT INTO cooperado_permissao (id_cooperado, id_permissao) " +
                "SELECT c.id_cooperado, p.id_permissao FROM cooperado_atualizado c " +
                "CROSS JOIN permissao p WHERE ? ON CONFLICT DO NOTHING RETURNING id_cooperado) " +
                "SELECT c.id_usuario, (SELECT COUNT(*) FROM permissoes_gestor) AS permissoes_novas " +
                "FROM cooperado_atualizado c JOIN usuario_atualizado u ON c.id_usuario = u.id_usuario";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, cooperado.getIdCooperativa());
            if (cooperado.getCargoEnum() == null) {
                pstmt.setNull(2, Types.VARCHAR);
            } else {
                pstmt.setString(2, cooperado.getCargoEnum().name());
            }
            pstmt.setString(3, cooperado.getStatusEnum().name());
            pstmt.setLong(4, cooperado.getIdCooperado());
            pstmt.setString(5, cooperado.getNomeCompleto());
            pstmt.setString(6, cooperado.getEmail());
            pstmt.setBoolean(7, cooperado.getCargoEnum() == CargoCooperado.GESTOR);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                cooperado.setIdUsuario(rs.getLong("id_usuario"));
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao editar cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o cargo de um cooperado existente.
    public Cooperado alterarCargoCooperado(Cooperado cooperado) {

        // Declaração

        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "WITH cargo_atualizado AS (" +
                "UPDATE cooperado SET cargo = ? WHERE id_cooperado = ? " +
                "RETURNING id_cooperado), permissoes_novas AS (" +
                "INSERT INTO cooperado_permissao (id_cooperado, id_permissao) " +
                "SELECT c.id_cooperado, p.id_permissao FROM cargo_atualizado c " +
                "CROSS JOIN permissao p WHERE ? ON CONFLICT DO NOTHING " +
                "RETURNING id_cooperado) SELECT c.id_cooperado, " +
                "(SELECT COUNT(*) FROM permissoes_novas) AS total_permissoes " +
                "FROM cargo_atualizado c";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            if (cooperado.getCargoEnum() == null) {
                pstmt.setNull(1, Types.VARCHAR);
            } else {
                pstmt.setString(1, cooperado.getCargoEnum().name());
            }
            pstmt.setLong(2, cooperado.getIdCooperado());
            pstmt.setBoolean(3, cooperado.getCargoEnum() == CargoCooperado.GESTOR);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar cargo do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Substitui a seleção de atividades do cooperado.
    public Cooperado alterarAtividadesDesempenhadas(Cooperado cooperado) {

        // Declaração

        if (cooperado == null || cooperado.getIdCooperado() == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sqlExcluir = "DELETE FROM cooperado_atividade WHERE id_cooperado = ?";
        String sqlInserir = "INSERT INTO cooperado_atividade (id_cooperado, atividade) VALUES (?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement excluir = conexao.prepareStatement(sqlExcluir);
             PreparedStatement inserir = conexao.prepareStatement(sqlInserir)) {
            excluir.setLong(1, cooperado.getIdCooperado());
            excluir.executeUpdate();
            for (AtividadeCooperado atividade : cooperado.getAtividadesCooperado()) {
                inserir.setLong(1, cooperado.getIdCooperado());
                inserir.setString(2, atividade.name());
                inserir.executeUpdate();
            }
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar atividade do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o status de um cooperado existente.
    public Cooperado alterarStatusCooperado(Cooperado cooperado) {

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
    public Cooperado alterarPermissoesAcesso(CooperadoPermissao anterior,
                                             CooperadoPermissao nova) {

        // Declaração

        if (anterior == null || nova == null || anterior.getIdCooperado() == null
                || anterior.getIdPermissao() == null || nova.getIdCooperado() == null
                || nova.getIdPermissao() == null
                || !anterior.getIdCooperado().equals(nova.getIdCooperado())) {
            throw new IllegalArgumentException("Vínculos de permissão são obrigatórios.");
        }
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(nova.getIdCooperado());
        ArrayList<Long> vinculos = new ArrayList<>();
        vinculos.add(nova.getIdPermissao());
        cooperado.setPermissoes(vinculos);
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
    public Cooperado alterarNomeUsuario(Cooperado cooperado) {

        // Execução e retorno

        model.auxiliares.Usuario usuario = usuarioDAO.alterarNomeUsuario(cooperado);
        if (usuario == null) {
            return null;
        }
        cooperado.setNomeUsuario(usuario.getNomeUsuario());
        return cooperado;
    }

    // Atualiza senha e indicador de senha temporária.
    public Cooperado alterarSenha(Cooperado cooperado) {

        // Execução e retorno

        model.auxiliares.Usuario usuario = usuarioDAO.alterarSenha(cooperado);
        if (usuario == null) {
            return null;
        }
        cooperado.setSenhaHash(usuario.getSenhaHash());
        cooperado.setSenhaTemporaria(usuario.isSenhaTemporaria());
        return cooperado;
    }

    // Gera uma nova senha temporária para a conta vinculada ao cooperado.
    public String redefinirSenha(Cooperado cooperado) {
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
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return senha;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao redefinir senha do cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Marca o cooperado como inativo sem apagar seu histórico.
    public Cooperado inativarCooperado(Cooperado cooperado) {
        if (cooperado == null) {
            throw new IllegalArgumentException("cooperado é obrigatório.");
        }
        cooperado.setStatus(StatusCooperado.INATIVO);
        return alterarStatusCooperado(cooperado);
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui cooperado e conta associada em um único comando SQL.
    public Cooperado excluirCooperado(Cooperado cooperado) {

        // Declaração

        if (cooperado == null || cooperado.getIdCooperado() == null) {
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
            return cooperado;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir cooperado.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Remove uma permissão específica de um cooperado.
    public Cooperado removerAcessoModulo(CooperadoPermissao vinculo) {

        // Declaração

        if (vinculo == null || vinculo.getIdCooperado() == null
                || vinculo.getIdPermissao() == null) {
            throw new IllegalArgumentException("Vínculo de permissão é obrigatório.");
        }
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(vinculo.getIdCooperado());
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM cooperado_permissao WHERE id_cooperado = ? " +
                "AND id_permissao = ? AND NOT EXISTS (SELECT 1 FROM cooperado " +
                "WHERE id_cooperado = ? AND cargo = 'GESTOR')";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, cooperado.getIdCooperado());
            pstmt.setLong(2, vinculo.getIdPermissao());
            pstmt.setLong(3, cooperado.getIdCooperado());
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
