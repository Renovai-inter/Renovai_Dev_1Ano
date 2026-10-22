package dao;

import model.Cooperado;
import util.Conexao;

import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CooperadoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public CooperadoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    /* Cadastrar cooperado + geracao automatica da conta de acesso (codigo, nome de usuario e senha temporaria).
       Grava usuario, cooperado, atividades e permissoes. Devolve o id do cooperado criado.
       permissoes: null = todas liberadas (padrao) | vazio = nenhuma. Cargo Gestor sempre recebe todas. */

    public long cadastrarCooperado(Cooperado cooperado, String nome_completo, String email,
                                   String[] atividades, String[] permissoes) {

        String sqlUsu = "INSERT INTO usuario(id_usuario, nome_completo, nome_usuario, senha_hash, senha_temporaria, email, tipo_usuario, data_criacao) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        String sqlCop = "INSERT INTO cooperado(id_cooperado, id_usuario, id_cooperativa, codigo_cooperado, cargo, status, data_cadastro) " +
                "VALUES (?,?,?,?,?,?,?)";

        Connection conexao = conn.conectar();

        try {
            conexao.setAutoCommit(false);

            // selecionarUltimoId devolve -1 quando a tabela esta vazia (ou deu erro), entao o proximo id e 1
            long ultimoUsuario = selecionarUltimoId("usuario");
            long idUsuario = (ultimoUsuario < 0) ? 1 : ultimoUsuario + 1;

            long ultimoCooperado = selecionarUltimoId("cooperado");
            long idCooperado = (ultimoCooperado < 0) ? 1 : ultimoCooperado + 1;

            String nome_usuario = gerarNomeUsuario(conexao, nome_completo);
            String senha_temporaria = "temp" + (int) (Math.random() * 10000);
            String codigo_cooperado = "COOP" + String.format("%03d", idCooperado);

            // 1. cria o usuario
            PreparedStatement pstUsu = conexao.prepareStatement(sqlUsu);

            pstUsu.setLong(1, idUsuario);
            pstUsu.setString(2, nome_completo);
            pstUsu.setString(3, nome_usuario);
            pstUsu.setString(4, senha_temporaria);
            pstUsu.setBoolean(5, true);
            pstUsu.setString(6, email);
            pstUsu.setString(7, "COOPERADO");
            pstUsu.setTimestamp(8, new Timestamp(System.currentTimeMillis()));

            pstUsu.executeUpdate();

            // 2. cria o cooperado ligado a esse usuario
            PreparedStatement pstCop = conexao.prepareStatement(sqlCop);

            pstCop.setLong(1, idCooperado);
            pstCop.setLong(2, idUsuario);
            pstCop.setLong(3, cooperado.getIdCooperativa());
            pstCop.setString(4, codigo_cooperado);
            if (cooperado.getCargo() == null || cooperado.getCargo().isEmpty()) {
                pstCop.setNull(5, Types.VARCHAR);
            } else {
                pstCop.setString(5, cooperado.getCargo());
            }
            pstCop.setString(6, cooperado.getStatus() != null ? cooperado.getStatus() : "Ativo");
            pstCop.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            pstCop.executeUpdate();

            // 3. atividades e permissoes
            if (atividades != null) {
                for (String atividade : atividades) {
                    inserirAtividade(conexao, idCooperado, atividade);
                }
            }

            boolean gestor = "Gestor".equalsIgnoreCase(cooperado.getCargo());

            if (gestor || permissoes == null) {
                liberarTodasPermissoes(conexao, idCooperado);
            } else {
                for (String idPermissao : permissoes) {
                    inserirPermissao(conexao, idCooperado, Long.parseLong(idPermissao));
                }
            }

            conexao.commit();

            return idCooperado;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Selecionar atividades do cooperado (o gestor indica uma ou mais).

    public int selecionarAtividades(long idCooperado, String[] atividades) {
        return alterarAtividadesDesempenhadas(idCooperado, atividades);
    }

    // Retorna o ultimo id de uma tabela (usado para gerar a PK do proximo registro).
    // Devolve -1 se a tabela estiver vazia ou se der erro.

    public long selecionarUltimoId(String tabela) {

        Conexao conn = new Conexao();
        Connection conexao = conn.conectar();

        String sql = "SELECT id_" + tabela +
                " FROM " + tabela +
                " ORDER BY 1 DESC LIMIT 1";

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("id_" + tabela);
            } else {
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    // Visualizar lista de cooperados: codigo, nome, e-mail, cooperativa, cargo e status.

    public ArrayList<Cooperado> listaCooperados() {

        ArrayList<Cooperado> cooperados = new ArrayList<>();

        String sql = "SELECT c.id_cooperado, c.codigo_cooperado, c.status, c.cargo, " +
                "u.nome_completo, u.email, co.nome AS nome_cooperativa " +
                "FROM cooperado c " +
                "JOIN usuario u ON c.id_usuario = u.id_usuario " +
                "LEFT JOIN cooperativa co ON c.id_cooperativa = co.id_cooperativa " +
                "ORDER BY c.codigo_cooperado";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            while (rst.next()) {
                Cooperado cooperado = new Cooperado();

                cooperado.setIdCooperado(rst.getLong("id_cooperado"));
                cooperado.setCodigoCooperado(rst.getString("codigo_cooperado"));
                cooperado.setStatus(rst.getString("status"));
                cooperado.setCargo(rst.getString("cargo"));
                cooperado.setNomeCompleto(rst.getString("nome_completo"));
                cooperado.setEmail(rst.getString("email"));
                cooperado.setNomeCooperativa(rst.getString("nome_cooperativa"));

                cooperados.add(cooperado);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return cooperados;
    }

    // Visualizar detalhes do cooperado (telas Ver e Editar). Devolve null se nao existir.

    public Cooperado buscarCooperadoPorId(long idCooperado) {

        Cooperado cooperado = null;

        String sql = "SELECT c.id_cooperado, c.id_usuario, c.id_cooperativa, c.codigo_cooperado, c.cargo, c.status, c.data_cadastro, " +
                "u.nome_completo, u.nome_usuario, u.email, u.senha_temporaria, co.nome AS nome_cooperativa " +
                "FROM cooperado c " +
                "JOIN usuario u ON c.id_usuario = u.id_usuario " +
                "LEFT JOIN cooperativa co ON c.id_cooperativa = co.id_cooperativa " +
                "WHERE c.id_cooperado = ?";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            pstm.setLong(1, idCooperado);

            ResultSet rst = pstm.executeQuery();

            if (rst.next()) {
                cooperado = new Cooperado();

                cooperado.setIdCooperado(rst.getLong("id_cooperado"));
                cooperado.setIdUsuario(rst.getLong("id_usuario"));
                cooperado.setIdCooperativa(rst.getLong("id_cooperativa"));
                cooperado.setCodigoCooperado(rst.getString("codigo_cooperado"));
                cooperado.setCargo(rst.getString("cargo"));
                cooperado.setStatus(rst.getString("status"));
                cooperado.setDataCadastro(rst.getTimestamp("data_cadastro").toLocalDateTime());
                cooperado.setNomeCompleto(rst.getString("nome_completo"));
                cooperado.setNomeUsuario(rst.getString("nome_usuario"));
                cooperado.setEmail(rst.getString("email"));
                cooperado.setSenhaTemporaria(rst.getBoolean("senha_temporaria"));
                cooperado.setNomeCooperativa(rst.getString("nome_cooperativa"));
                cooperado.setAtividades(listarAtividades(conexao, idCooperado));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return cooperado;
    }

    // Ids das permissoes (modulos) liberadas para o cooperado.

    public ArrayList<Long> listarPermissoes(long idCooperado) {

        ArrayList<Long> permissoes = new ArrayList<>();

        String sql = "SELECT id_permissao FROM cooperado_permissao WHERE id_cooperado = ?";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            pstm.setLong(1, idCooperado);

            ResultSet rst = pstm.executeQuery();

            while (rst.next()) {
                permissoes.add(rst.getLong("id_permissao"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return permissoes;
    }

    // Todas as permissoes que existem (id -> nome). Usada para montar os interruptores do formulario.

    public LinkedHashMap<Long, String> listarTodasPermissoes() {

        LinkedHashMap<Long, String> permissoes = new LinkedHashMap<>();

        String sql = "SELECT id_permissao, nome FROM permissao ORDER BY id_permissao";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            while (rst.next()) {
                permissoes.put(rst.getLong("id_permissao"), rst.getString("nome"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return permissoes;
    }

    // Todas as cooperativas (id -> nome). Usada no <select> de cooperativa.

    public LinkedHashMap<Long, String> listarCooperativas() {

        LinkedHashMap<Long, String> cooperativas = new LinkedHashMap<>();

        String sql = "SELECT id_cooperativa, nome FROM cooperativa ORDER BY nome";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            while (rst.next()) {
                cooperativas.put(rst.getLong("id_cooperativa"), rst.getString("nome"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        return cooperativas;
    }

    // Valores aceitos por uma coluna com regra CHECK (cargo, status...), lidos do proprio banco.
    // Se a regra nao for encontrada, usa os valores padrao informados.

    public ArrayList<String> listarValoresPermitidos(String nomeRegra, String... padrao) {

        ArrayList<String> valores = new ArrayList<>();

        String sql = "SELECT pg_get_constraintdef(oid) FROM pg_constraint WHERE conname = ? AND contype = 'c'";

        Connection conexao = conn.conectar();

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            pstm.setString(1, nomeRegra);

            ResultSet rst = pstm.executeQuery();

            if (rst.next()) {
                // pega tudo que esta entre aspas simples na definicao da regra
                java.util.regex.Matcher m = java.util.regex.Pattern.compile("'([^']*)'").matcher(rst.getString(1));

                while (m.find()) {
                    if (!valores.contains(m.group(1))) {
                        valores.add(m.group(1));
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }

        if (valores.isEmpty()) {
            java.util.Collections.addAll(valores, padrao);
        }

        return valores;
    }

    // Quantas coletas tem esse cooperado como responsavel (aparece no modal de excluir).

    public int contarColetasDoCooperado(long idCooperado) {

        Connection conexao = conn.conectar();

        try {
            return contar(conexao, "SELECT COUNT(*) FROM coleta WHERE id_cooperado_responsavel = ?", idCooperado);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Editar cooperado: nome e e-mail (usuario) + cooperativa, cargo e status (cooperado).
    // Se o novo cargo for Gestor, ele passa a ter acesso total.

    public int editarCooperado(Cooperado cooperado) {

        String sqlCop = "UPDATE cooperado SET id_cooperativa = ?, cargo = ?, status = COALESCE(NULLIF(?, ''), status) " +
                "WHERE id_cooperado = ?";
        String sqlUsu = "UPDATE usuario SET nome_completo = ?, email = ? " +
                "WHERE id_usuario = (SELECT id_usuario FROM cooperado WHERE id_cooperado = ?)";

        Connection conexao = conn.conectar();

        try {
            conexao.setAutoCommit(false);

            PreparedStatement pstCop = conexao.prepareStatement(sqlCop);

            pstCop.setLong(1, cooperado.getIdCooperativa());
            if (cooperado.getCargo() == null || cooperado.getCargo().isEmpty()) {
                pstCop.setNull(2, Types.VARCHAR);
            } else {
                pstCop.setString(2, cooperado.getCargo());
            }
            pstCop.setString(3, cooperado.getStatus());
            pstCop.setLong(4, cooperado.getIdCooperado());

            int linhas = pstCop.executeUpdate();

            PreparedStatement pstUsu = conexao.prepareStatement(sqlUsu);

            pstUsu.setString(1, cooperado.getNomeCompleto());
            pstUsu.setString(2, cooperado.getEmail());
            pstUsu.setLong(3, cooperado.getIdCooperado());

            pstUsu.executeUpdate();

            if ("Gestor".equalsIgnoreCase(cooperado.getCargo())) {
                liberarTodasPermissoes(conexao, cooperado.getIdCooperado());
            }

            conexao.commit();

            return linhas;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Alterar cargo do cooperado (virar Gestor libera todas as permissoes).

    public int alterarCargoCooperado(Cooperado cooperado) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE cooperado SET cargo = ? WHERE id_cooperado = ?";

        try {
            conexao.setAutoCommit(false);

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, cooperado.getCargo());
            pstmt.setLong(2, cooperado.getIdCooperado());

            int linhas = pstmt.executeUpdate();

            if ("Gestor".equalsIgnoreCase(cooperado.getCargo())) {
                liberarTodasPermissoes(conexao, cooperado.getIdCooperado());
            }

            conexao.commit();

            return linhas;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Alterar atividades desempenhadas (multipla escolha): apaga as antigas e grava as marcadas.

    public int alterarAtividadesDesempenhadas(long idCooperado, String[] atividades) {

        Connection conexao = conn.conectar();

        String sql = "DELETE FROM cooperado_atividade WHERE id_cooperado = ?";

        try {
            conexao.setAutoCommit(false);

            PreparedStatement pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, idCooperado);
            pstmt.executeUpdate();

            int total = 0;

            if (atividades != null) {
                for (String atividade : atividades) {
                    inserirAtividade(conexao, idCooperado, atividade);
                    total++;
                }
            }

            conexao.commit();

            return total;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Alterar status do cooperado (Ativo, Afastado ou Inativo).

    public int alterarStatusCooperado(Cooperado cooperado) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE cooperado SET status = ? WHERE id_cooperado = ?";

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, cooperado.getStatus());
            pstmt.setLong(2, cooperado.getIdCooperado());

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    /* Alterar permissoes de acesso. Regras: so quem tem cargo Gestor pode alterar,
       e o Gestor sempre mantem acesso total (suas permissoes nao mudam). Devolve 0 se a regra impedir. */

    public int alterarPermissoesAcesso(long idGestor, long idCooperado, String[] permissoes) {

        Connection conexao = conn.conectar();

        String sql = "DELETE FROM cooperado_permissao WHERE id_cooperado = ?";

        try {
            if (!ehGestor(conexao, idGestor) || ehGestor(conexao, idCooperado)) {
                return 0;
            }

            conexao.setAutoCommit(false);

            PreparedStatement pstmt = conexao.prepareStatement(sql);
            pstmt.setLong(1, idCooperado);
            pstmt.executeUpdate();

            for (String idPermissao : permissoes) {
                inserirPermissao(conexao, idCooperado, Long.parseLong(idPermissao));
            }

            conexao.commit();

            return permissoes.length;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Alterar nome de usuario. Devolve 0 se o nome ja estiver em uso.

    public int alterarNomeUsuario(long idUsuario, String nomeUsuario) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE usuario SET nome_usuario = ? WHERE id_usuario = ?";

        try {
            if (usuarioExiste(conexao, nomeUsuario)) {
                return 0;
            }

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, nomeUsuario);
            pstmt.setLong(2, idUsuario);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Definir nova senha (troca da senha temporaria no primeiro acesso: senhaTemporaria = false).

    public int alterarSenha(long idUsuario, String senhaHash, boolean senhaTemporaria) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE usuario SET senha_hash = ?, senha_temporaria = ? WHERE id_usuario = ?";

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, senhaHash);
            pstmt.setBoolean(2, senhaTemporaria);
            pstmt.setLong(3, idUsuario);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Botao "Redefinir senha": gera uma nova senha temporaria e devolve para o gestor repassar.

    public String redefinirSenha(long idCooperado) {

        Connection conexao = conn.conectar();

        String senha_temporaria = "temp" + (int) (Math.random() * 10000);

        String sql = "UPDATE usuario SET senha_hash = ?, senha_temporaria = ? " +
                "WHERE id_usuario = (SELECT id_usuario FROM cooperado WHERE id_cooperado = ?)";

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, senha_temporaria);
            pstmt.setBoolean(2, true);
            pstmt.setLong(3, idCooperado);

            pstmt.executeUpdate();

            return senha_temporaria;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Opcao recomendada do modal: nao apaga nada, so muda o status (o texto vem da regra do banco).

    public int inativarCooperado(long idCooperado, String statusInativo) {

        Connection conexao = conn.conectar();

        String sql = "UPDATE cooperado SET status = ? WHERE id_cooperado = ?";

        try {
            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, statusInativo);
            pstmt.setLong(2, idCooperado);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

        /* Remover cooperado de vez: apaga o historico dele (rateios e relatorios), tira o responsavel das coletas,
       apaga atividades, permissoes, o cooperado e a conta de acesso dele. Tudo na mesma transacao.
       Devolve: 0 = nao existe | 1 = excluido. */

    public int excluirCooperado(Cooperado cooperado) {

        Connection conexao = conn.conectar();

        try {
            long idCooperado = cooperado.getIdCooperado();

            // descobre o usuario antes de apagar o cooperado
            PreparedStatement pstId = conexao.prepareStatement("SELECT id_usuario FROM cooperado WHERE id_cooperado = ?");
            pstId.setLong(1, idCooperado);
            ResultSet rst = pstId.executeQuery();

            if (!rst.next()) {
                return 0;
            }
            long idUsuario = rst.getLong("id_usuario");

            conexao.setAutoCommit(false);

            // historico do cooperado (precisa sair antes do cooperado)
            PreparedStatement pstRat = conexao.prepareStatement("DELETE FROM rateio_distribuicao WHERE id_cooperado = ?");
            pstRat.setLong(1, idCooperado);
            pstRat.executeUpdate();

            PreparedStatement pstExc = conexao.prepareStatement("DELETE FROM rateio_excecao WHERE id_cooperado = ?");
            pstExc.setLong(1, idCooperado);
            pstExc.executeUpdate();

            PreparedStatement pstRel = conexao.prepareStatement("DELETE FROM relatorio WHERE id_cooperado_gerador = ?");
            pstRel.setLong(1, idCooperado);
            pstRel.executeUpdate();

            // o resto do cadastro
            PreparedStatement pst0 = conexao.prepareStatement("UPDATE coleta SET id_cooperado_responsavel = NULL WHERE id_cooperado_responsavel = ?");
            pst0.setLong(1, idCooperado);
            pst0.executeUpdate();

            PreparedStatement pst1 = conexao.prepareStatement("DELETE FROM cooperado_atividade WHERE id_cooperado = ?");
            pst1.setLong(1, idCooperado);
            pst1.executeUpdate();

            PreparedStatement pst2 = conexao.prepareStatement("DELETE FROM cooperado_permissao WHERE id_cooperado = ?");
            pst2.setLong(1, idCooperado);
            pst2.executeUpdate();

            PreparedStatement pst3 = conexao.prepareStatement("DELETE FROM cooperado WHERE id_cooperado = ?");
            pst3.setLong(1, idCooperado);
            pst3.executeUpdate();

            PreparedStatement pst4 = conexao.prepareStatement("DELETE FROM usuario WHERE id_usuario = ?");
            pst4.setLong(1, idUsuario);
            pst4.executeUpdate();

            conexao.commit();

            return 1;

        } catch (SQLException e) {
            desfazer(conexao);
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Remover acesso a um modulo (nao apaga dados do modulo). O Gestor nao pode perder acesso: devolve 0.

    public int removerAcessoModulo(long idCooperado, long idPermissao) {

        Connection conexao = conn.conectar();

        String sql = "DELETE FROM cooperado_permissao WHERE id_cooperado = ? AND id_permissao = ?";

        try {
            if (ehGestor(conexao, idCooperado)) {
                return 0;
            }

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(1, idCooperado);
            pstmt.setLong(2, idPermissao);

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS AUXILIARES (privados, so esta classe usa) ===========================================================

    private void inserirAtividade(Connection conexao, long idCooperado, String atividade) throws SQLException {

        PreparedStatement pstm = conexao.prepareStatement("INSERT INTO cooperado_atividade(id_cooperado, atividade) VALUES (?,?)");
        pstm.setLong(1, idCooperado);
        pstm.setString(2, atividade);
        pstm.executeUpdate();
    }

    private void inserirPermissao(Connection conexao, long idCooperado, long idPermissao) throws SQLException {

        PreparedStatement pstm = conexao.prepareStatement("INSERT INTO cooperado_permissao(id_cooperado, id_permissao) VALUES (?,?)");
        pstm.setLong(1, idCooperado);
        pstm.setLong(2, idPermissao);
        pstm.executeUpdate();
    }

    // apaga as permissoes do cooperado e grava todas as que existem na tabela permissao
    private void liberarTodasPermissoes(Connection conexao, long idCooperado) throws SQLException {

        PreparedStatement pst1 = conexao.prepareStatement("DELETE FROM cooperado_permissao WHERE id_cooperado = ?");
        pst1.setLong(1, idCooperado);
        pst1.executeUpdate();

        PreparedStatement pst2 = conexao.prepareStatement("INSERT INTO cooperado_permissao(id_cooperado, id_permissao) SELECT ?, id_permissao FROM permissao");
        pst2.setLong(1, idCooperado);
        pst2.executeUpdate();
    }

    private ArrayList<String> listarAtividades(Connection conexao, long idCooperado) throws SQLException {

        ArrayList<String> atividades = new ArrayList<>();

        PreparedStatement pstm = conexao.prepareStatement("SELECT atividade FROM cooperado_atividade WHERE id_cooperado = ?");
        pstm.setLong(1, idCooperado);

        ResultSet rst = pstm.executeQuery();

        while (rst.next()) {
            atividades.add(rst.getString("atividade"));
        }
        return atividades;
    }

    private boolean ehGestor(Connection conexao, long idCooperado) throws SQLException {

        PreparedStatement pstm = conexao.prepareStatement("SELECT cargo FROM cooperado WHERE id_cooperado = ?");
        pstm.setLong(1, idCooperado);

        ResultSet rst = pstm.executeQuery();

        return rst.next() && "Gestor".equalsIgnoreCase(rst.getString("cargo"));
    }

    private boolean usuarioExiste(Connection conexao, String nome_usuario) throws SQLException {

        PreparedStatement pstm = conexao.prepareStatement("SELECT COUNT(*) FROM usuario WHERE nome_usuario = ?");
        pstm.setString(1, nome_usuario);

        ResultSet rst = pstm.executeQuery();
        rst.next();

        return rst.getInt(1) > 0;
    }

    // executa um SELECT COUNT(*) com um unico parametro e devolve o numero
    private int contar(Connection conexao, String sql, long id) throws SQLException {

        PreparedStatement pstm = conexao.prepareStatement(sql);
        pstm.setLong(1, id);

        ResultSet rst = pstm.executeQuery();
        rst.next();

        return rst.getInt(1);
    }

    private void desfazer(Connection conexao) {
        try {
            conexao.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // "Joao Santos Batista" vira "joao.batista"; se ja existir, vira "joao.batista2", "joao.batista3"...
    private String gerarNomeUsuario(Connection conexao, String nome_completo) throws SQLException {

        String limpo = Normalizer.normalize(nome_completo.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("[^a-z ]", "");

        String[] partes = limpo.split(" +");

        String base = partes[0];
        if (partes.length > 1) {
            base = partes[0] + "." + partes[partes.length - 1];
        }
        if (base.length() > 40) {
            base = base.substring(0, 40);       // coluna nome_usuario tem limite de 50
        }

        String nome_usuario = base;
        int numero = 1;

        while (usuarioExiste(conexao, nome_usuario)) {
            numero++;
            nome_usuario = base + numero;
        }
        return nome_usuario;
    }

    // executa um UPDATE ou DELETE com um unico parametro e devolve quantas linhas foram afetadas
    private int executar(Connection conexao, String sql, long id) throws SQLException {

        PreparedStatement pstm = conexao.prepareStatement(sql);
        pstm.setLong(1, id);

        return pstm.executeUpdate();
    }

}