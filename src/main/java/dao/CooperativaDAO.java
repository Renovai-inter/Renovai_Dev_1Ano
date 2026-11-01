package dao;

import model.enums.StatusCooperativa;
import model.principais.Cooperado;
import model.principais.Cooperativa;
import util.ConexaoBD;
import util.Utilitario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class CooperativaDAO {

    // ATRIBUTOS

    private static final String SELECT_BASE = "SELECT coop.id_cooperativa, coop.nome, coop.cnpj, coop.cidade, " +
            "coop.estado, coop.status, " +
            "(SELECT COUNT(*) FROM cooperado c WHERE c.id_cooperativa = coop.id_cooperativa) AS cooperados, " +
            "coop.data_cadastro AS cadastro FROM cooperativa coop ";

    private final ConexaoBD conn = new ConexaoBD();
    private final Utilitario utilitario = new Utilitario();
    private final CooperadoDAO cooperadoDAO = new CooperadoDAO();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de cooperativa.
    public CooperativaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Grava a cooperativa e o gestor em etapas e devolve o total de linhas afetadas.
    // Se o gestor falhar, tenta excluir a cooperativa já gravada como compensação manual.
    public int cadastrarCooperativa(Cooperativa coop, Cooperado gestor) {

        // Declaração

        if (coop == null || gestor == null || coop.getNome() == null || coop.getCnpj() == null
                || coop.getNomePublico() == null || coop.getEmailInstitucional() == null
                || coop.getTelefoneWhatsapp() == null || coop.getCep() == null
                || coop.getEndereco() == null || coop.getCidade() == null || coop.getEstado() == null) {
            throw new IllegalArgumentException("Dados obrigatórios da cooperativa e do gestor não foram informados.");
        }
        long ultimoId = utilitario.selecionarUltimoId("cooperativa");
        long novoId;
        if (ultimoId == -1) {
            novoId = 1;
        } else {
            novoId = ultimoId + 1;
        }
        int linhasCooperativa;
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO cooperativa (id_cooperativa, nome, cnpj, nome_publico, descricao_institucional, " +
                "logo_url, email_institucional, telefone_whatsapp, cep, endereco, cidade, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, novoId);
            pstmt.setString(2, coop.getNome());
            pstmt.setString(3, utilitario.filtrarCnpj(coop.getCnpj()));
            pstmt.setString(4, coop.getNomePublico());
            if (coop.getDescricaoInstitucional() == null || coop.getDescricaoInstitucional().isBlank()) {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(5, coop.getDescricaoInstitucional());
            }
            if (coop.getLogoUrl() == null || coop.getLogoUrl().isBlank()) {
                pstmt.setNull(6, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(6, coop.getLogoUrl());
            }
            pstmt.setString(7, coop.getEmailInstitucional());
            pstmt.setString(8, utilitario.filtrarTelefoneWhatsapp(coop.getTelefoneWhatsapp()));
            pstmt.setString(9, utilitario.filtrarCep(coop.getCep()));
            pstmt.setString(10, coop.getEndereco());
            pstmt.setString(11, coop.getCidade());
            pstmt.setString(12, coop.getEstado());
            linhasCooperativa = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }

        // Execução e retorno

        if (linhasCooperativa == 0) {
            return 0;
        }
        coop.setIdCooperativa(novoId);
        gestor.setIdCooperativa(novoId);
        int linhasGestor;
        try {
            linhasGestor = cooperadoDAO.cadastrarGestor(gestor);
        } catch (RuntimeException e) {
            // O gestor falhou (ex.: e-mail já cadastrado): desfaz a cooperativa para não deixá-la sem gestor.
            try {
                excluirCooperativa(coop);
            } catch (RuntimeException ignorada) {
                e.addSuppressed(ignorada);
            }
            throw e;
        }
        if (linhasGestor == 0) {
            excluirCooperativa(coop);
            return 0;
        }
        return linhasCooperativa + linhasGestor;
    }

    // === METODOS READ ================================================================================================

    // Lista todas as cooperativas ordenadas por nome.
    public ArrayList<Cooperativa> exibirCooperativas() {
        return listar("");
    }

    // Lista as cooperativas cujo nome, CNPJ ou código correspondem ao texto de busca.
    public ArrayList<Cooperativa> buscarCooperativasPorNomeOuCodigo(String busca) {
        return listar("WHERE coop.nome ILIKE ? OR coop.cnpj ILIKE ? OR CAST(coop.id_cooperativa AS TEXT) = ? ",
                "%" + busca + "%", "%" + busca + "%", busca);
    }

    // Lista as cooperativas cuja cidade contém o texto informado.
    public ArrayList<Cooperativa> filtrarCooperativasPorCidade(String cidadeBusca) {
        return listar("WHERE coop.cidade ILIKE ? ", "%" + cidadeBusca + "%");
    }

    // Lista as cooperativas da UF informada.
    public ArrayList<Cooperativa> filtrarCooperativasPorEstado(String estadoBusca) {
        return listar("WHERE UPPER(coop.estado) = UPPER(?) ", estadoBusca);
    }

    // Lista as cooperativas cujo status contém o texto informado.
    public ArrayList<Cooperativa> filtrarCooperativasPorStatus(String statusBusca) {
        return listar("WHERE CAST(coop.status AS TEXT) ILIKE ? ", "%" + statusBusca + "%");
    }

    // Busca uma cooperativa pelo ID, com dados institucionais e total de cooperados; devolve null se não existir.
    public Cooperativa buscarPorId(Cooperativa filtro) {

        // Declaração

        if (filtro == null || filtro.getIdCooperativa() == null || filtro.getIdCooperativa() <= 0) {
            throw new IllegalArgumentException("idCooperativa deve ser positivo.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "SELECT coop.id_cooperativa, coop.nome, coop.cnpj, coop.nome_publico, " +
                "coop.descricao_institucional AS descricao, coop.logo_url AS logo, " +
                "coop.email_institucional AS email, coop.telefone_whatsapp AS telefone, coop.cep, " +
                "coop.endereco, coop.cidade, coop.estado, coop.status, " +
                "(SELECT COUNT(*) FROM cooperado c WHERE c.id_cooperativa = coop.id_cooperativa) AS cooperados, " +
                "coop.data_cadastro AS cadastro FROM cooperativa coop WHERE coop.id_cooperativa = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, filtro.getIdCooperativa());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Cooperativa coop = new Cooperativa();
                coop.setIdCooperativa(rs.getLong("id_cooperativa"));
                coop.setNome(rs.getString("nome"));
                coop.setCnpj(rs.getString("cnpj"));
                coop.setNomePublico(rs.getString("nome_publico"));
                coop.setDescricaoInstitucional(rs.getString("descricao"));
                coop.setLogoUrl(rs.getString("logo"));
                coop.setEmailInstitucional(rs.getString("email"));
                coop.setTelefoneWhatsapp(rs.getString("telefone"));
                coop.setCep(rs.getString("cep"));
                coop.setEndereco(rs.getString("endereco"));
                coop.setCidade(rs.getString("cidade"));
                coop.setEstado(rs.getString("estado"));
                coop.setStatus(StatusCooperativa.valueOf(rs.getString("status")));
                coop.getRelacionamentos().setQtdCooperados(rs.getInt("cooperados"));
                Timestamp cadastro = rs.getTimestamp("cadastro");
                if (cadastro != null) {
                    coop.setDataCadastro(cadastro.toLocalDateTime());
                }
                return coop;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao buscar cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Conta os registros de rota, coleta ou negociação vinculados à cooperativa informada.
    public int contarRelacionados(String tabela, Cooperativa coop) {

        // Declaração

        if (coop == null || coop.getIdCooperativa() == null || coop.getIdCooperativa() <= 0) {
            throw new IllegalArgumentException("idCooperativa deve ser positivo.");
        }
        if (!"rota".equals(tabela) && !"coleta".equals(tabela) && !"negociacao".equals(tabela)) {
            throw new IllegalArgumentException("tabela deve ser rota, coleta ou negociacao.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "SELECT COUNT(*) FROM " + tabela + " WHERE id_cooperativa = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, coop.getIdCooperativa());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao contar registros relacionados à cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Executa a consulta base de cooperativas com o filtro informado e monta a lista ordenada por nome.
    private ArrayList<Cooperativa> listar(String filtroSql, String... parametros) {

        // Declaração

        ArrayList<Cooperativa> cooperativas = new ArrayList<>();
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = SELECT_BASE + filtroSql + "ORDER BY coop.nome";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                pstmt.setString(i + 1, parametros[i]);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    try {
                        Cooperativa coop = new Cooperativa();
                        coop.setIdCooperativa(rs.getLong("id_cooperativa"));
                        coop.setNome(rs.getString("nome"));
                        coop.setCnpj(rs.getString("cnpj"));
                        coop.setCidade(rs.getString("cidade"));
                        coop.setEstado(rs.getString("estado"));
                        coop.setStatus(StatusCooperativa.valueOf(rs.getString("status")));
                        coop.getRelacionamentos().setQtdCooperados(rs.getInt("cooperados"));
                        Timestamp cadastro = rs.getTimestamp("cadastro");
                        if (cadastro != null) {
                            coop.setDataCadastro(cadastro.toLocalDateTime());
                        }
                        cooperativas.add(coop);
                    } catch (IllegalArgumentException | NullPointerException e) {
                        System.err.println("Cooperativa com dado inválido no banco -> id=" + rs.getLong("id_cooperativa")
                                + ", nome=" + rs.getString("nome") + ", motivo=" + e.getMessage());
                    }
                }
            }
            return cooperativas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao listar cooperativas.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza os dados cadastrais da cooperativa identificada pelo ID, com colunas fixas e valores da Model.
    public int atualizarCooperativa(Cooperativa coop) {

        // Declaração

        if (coop == null || coop.getIdCooperativa() == null || coop.getIdCooperativa() <= 0
                || coop.getNome() == null || coop.getCnpj() == null || coop.getNomePublico() == null
                || coop.getEmailInstitucional() == null || coop.getTelefoneWhatsapp() == null
                || coop.getCep() == null || coop.getEndereco() == null || coop.getCidade() == null
                || coop.getEstado() == null) {
            throw new IllegalArgumentException("ID e dados obrigatórios da cooperativa não foram informados.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperativa SET nome = ?, cnpj = ?, nome_publico = ?, descricao_institucional = ?, " +
                "logo_url = ?, email_institucional = ?, telefone_whatsapp = ?, cep = ?, endereco = ?, " +
                "cidade = ?, estado = ? WHERE id_cooperativa = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coop.getNome());
            pstmt.setString(2, utilitario.filtrarCnpj(coop.getCnpj()));
            pstmt.setString(3, coop.getNomePublico());
            if (coop.getDescricaoInstitucional() == null || coop.getDescricaoInstitucional().isBlank()) {
                pstmt.setNull(4, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(4, coop.getDescricaoInstitucional());
            }
            if (coop.getLogoUrl() == null || coop.getLogoUrl().isBlank()) {
                pstmt.setNull(5, java.sql.Types.VARCHAR);
            } else {
                pstmt.setString(5, coop.getLogoUrl());
            }
            pstmt.setString(6, coop.getEmailInstitucional());
            pstmt.setString(7, utilitario.filtrarTelefoneWhatsapp(coop.getTelefoneWhatsapp()));
            pstmt.setString(8, utilitario.filtrarCep(coop.getCep()));
            pstmt.setString(9, coop.getEndereco());
            pstmt.setString(10, coop.getCidade());
            pstmt.setString(11, coop.getEstado());
            pstmt.setLong(12, coop.getIdCooperativa());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Altera o status da cooperativa (ATIVA ou INATIVA); devolve 0 se o ID não existir.
    public int alterarStatus(Cooperativa coop) {

        // Declaração

        if (coop == null || coop.getIdCooperativa() == null || coop.getIdCooperativa() <= 0
                || coop.getStatus() == null) {
            throw new IllegalArgumentException("idCooperativa e status devem ser informados.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperativa SET status = ? WHERE id_cooperativa = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coop.getStatus().name());
            pstmt.setLong(2, coop.getIdCooperativa());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao alterar o status da cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui a cooperativa identificada pelo ID; devolve 0 se nenhuma linha for afetada.
    public int excluirCooperativa(Cooperativa coop) {

        // Declaração

        if (coop == null || coop.getIdCooperativa() == null || coop.getIdCooperativa() <= 0) {
            throw new IllegalArgumentException("idCooperativa deve ser positivo.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM cooperativa WHERE id_cooperativa = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, coop.getIdCooperativa());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

}
