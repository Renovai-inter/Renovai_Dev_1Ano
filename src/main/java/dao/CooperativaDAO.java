package dao;

import model.principais.Cooperativa;
import model.principais.Cooperado;
import model.enums.StatusCooperativa;
import util.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class CooperativaDAO {

    // ATRIBUTOS

    private final Conexao conn = new Conexao();
    private final Utilitario utilitario = new Utilitario();
    private final CooperadoDAO cooperadoDAO = new CooperadoDAO();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de cooperativa.
    public CooperativaDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Cadastra uma cooperativa e seu gestor inicial.
    public Cooperativa cadastrarCooperativa(String nome, String cnpj, String nomePublico,
                                            String descricaoInstitucional, String logoUrl,
                                            String emailInstitucional, String telefoneWhatsapp,
                                            String cep, String endereco, String cidade, String estado,
                                            String nomeGestor, String usuarioGestor,
                                            String emailGestor, String senhaTemporariaGestor) {

        // Declaração

        Cooperativa coop = new Cooperativa();
        coop.setNome(nome);
        coop.setCnpj(cnpj);
        coop.setNomePublico(nomePublico);
        coop.setDescricaoInstitucional(descricaoInstitucional);
        coop.setLogoUrl(logoUrl);
        coop.setEmailInstitucional(emailInstitucional);
        coop.setTelefoneWhatsapp(telefoneWhatsapp);
        coop.setCep(cep);
        coop.setEndereco(endereco);
        coop.setCidade(cidade);
        coop.setEstado(estado);
        Cooperado gestor = new Cooperado();
        gestor.setNomeCompleto(nomeGestor);
        gestor.setNomeUsuario(usuarioGestor);
        gestor.setEmail(emailGestor);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO cooperativa (id_cooperativa, nome, cnpj, nome_publico, " +
                "descricao_institucional, logo_url, email_institucional, telefone_whatsapp, " +
                "cep, endereco, cidade, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // Executar PreparedStatement

        try {
            long ultimoId = utilitario.selecionarUltimoId("cooperativa");
            long idCooperativa;
            if (ultimoId == -1) {
                idCooperativa = 1;
            } else {
                idCooperativa = ultimoId + 1;
            }
            coop.setIdCooperativa(idCooperativa);
            try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
                pstmt.setLong(1, idCooperativa);
                pstmt.setString(2, coop.getNome());
                pstmt.setString(3, utilitario.filtrarCnpj(coop.getCnpj()));
                pstmt.setString(4, coop.getNomePublico());
                pstmt.setString(5, coop.getDescricaoInstitucional());
                pstmt.setString(6, coop.getLogoUrl());
                pstmt.setString(7, coop.getEmailInstitucional());
                pstmt.setString(8, utilitario.filtrarTelefoneWhatsapp(coop.getTelefoneWhatsapp()));
                pstmt.setString(9, utilitario.filtrarCep(coop.getCep()));
                pstmt.setString(10, coop.getEndereco());
                pstmt.setString(11, coop.getCidade());
                pstmt.setString(12, coop.getEstado());
                if (pstmt.executeUpdate() == 0) {
                    return null;
                }
            }
            boolean gestorCadastrado = cooperadoDAO.gravarGestor(conexao, coop, gestor, senhaTemporariaGestor);
            if (!gestorCadastrado) {
                return null;
            }
            coop.setCanalContatoPreferencial("WHATSAPP");
            coop.setStatus(StatusCooperativa.ATIVA);
            return coop;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao cadastrar cooperativa e gestor.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    // Preenche uma cooperativa com os dados selecionados na consulta.
    private Cooperativa lerCooperativa(ResultSet rs) throws SQLException {

        // Declaração

        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setIdCooperativa(rs.getLong("id_cooperativa"));
        cooperativa.setNome(rs.getString("nome"));
        cooperativa.setCnpj(rs.getString("cnpj"));
        cooperativa.setCidade(rs.getString("cidade"));
        cooperativa.setEstado(rs.getString("estado"));
        String status = rs.getString("status");
        if (status != null) {
            cooperativa.setStatus(StatusCooperativa.valueOf(status));
        }
        cooperativa.setQtdCooperados(rs.getInt("cooperados"));
        Timestamp cadastro = rs.getTimestamp("cadastro");
        if (cadastro != null) {
            cooperativa.setDataCadastro(cadastro.toLocalDateTime());
        }
        return cooperativa;
    }

    private static final String CONSULTA_COOPERATIVAS =
            "SELECT coop.id_cooperativa, coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
            "(SELECT COUNT(*) FROM cooperado c WHERE c.id_cooperativa = coop.id_cooperativa) AS cooperados, " +
            "coop.data_cadastro AS cadastro FROM cooperativa coop";

    // Lista as cooperativas cadastradas com dados resumidos.
    public ArrayList<Cooperativa> exibirCooperativas() {

        // Execução e retorno

        return consultarCooperativas(CONSULTA_COOPERATIVAS, null, null);
    }

    // Busca cooperativas pelo nome ou CNPJ informado.
    public ArrayList<Cooperativa> buscarCooperativasPorNomeOuCodigo(String busca) {

        // Declaração

        if (busca == null || busca.isBlank()) {
            throw new IllegalArgumentException("busca é obrigatória.");
        }
        String filtroNome = "%" + busca.strip() + "%";
        String filtroCnpj = "%" + busca.strip().replace(".", "").replace("/", "")
                .replace("-", "") + "%";

        // Execução e retorno

        return consultarCooperativas(CONSULTA_COOPERATIVAS
                + " WHERE coop.nome ILIKE ? OR coop.cnpj LIKE ?", filtroNome, filtroCnpj);
    }

    // Lista as cooperativas da cidade informada.
    public ArrayList<Cooperativa> filtrarCooperativasPorCidade(String cidadeBusca) {

        // Declaração

        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setCidade(cidadeBusca);

        // Execução e retorno

        return consultarCooperativas(CONSULTA_COOPERATIVAS
                + " WHERE coop.cidade ILIKE ?", cooperativa.getCidade(), null);
    }

    // Lista as cooperativas do estado informado.
    public ArrayList<Cooperativa> filtrarCooperativasPorEstado(String estadoBusca) {

        // Declaração

        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setEstado(estadoBusca);

        // Execução e retorno

        return consultarCooperativas(CONSULTA_COOPERATIVAS
                + " WHERE coop.estado = ?", cooperativa.getEstado(), null);
    }

    // Lista as cooperativas com o status informado.
    public ArrayList<Cooperativa> filtrarCooperativasPorStatus(String statusBusca) {

        // Declaração

        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setStatus(StatusCooperativa.valueOf(statusBusca.strip().toUpperCase()));

        // Execução e retorno

        return consultarCooperativas(CONSULTA_COOPERATIVAS
                + " WHERE coop.status = ?", cooperativa.getStatus().name(), null);
    }

    // Executa a consulta e preenche uma Model para cada linha.
    private ArrayList<Cooperativa> consultarCooperativas(String sql, String parametro1,
                                                        String parametro2) {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            if (parametro1 != null) {
                pstmt.setString(1, parametro1);
            }
            if (parametro2 != null) {
                pstmt.setString(2, parametro2);
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    cooperativas.add(lerCooperativa(rs));
                }
            }
            return cooperativas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao consultar cooperativas.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Atualiza os dados informados da cooperativa identificada.
    public Cooperativa atualizarCooperativa(Long idCooperativa, String nome, String cnpj,
                                           String nomePublico, String descricaoInstitucional,
                                           String logoUrl, String emailInstitucional,
                                           String telefoneWhatsapp, String cep, String endereco,
                                           String cidade, String estado, LocalDate dataFundacao,
                                           String canalContatoPreferencial, String linkWhatsapp,
                                           StatusCooperativa status) {

        // Declaração

        Cooperativa coop = new Cooperativa();
        coop.setIdCooperativa(idCooperativa);
        coop.setNome(nome);
        coop.setCnpj(cnpj);
        coop.setNomePublico(nomePublico);
        coop.setDescricaoInstitucional(descricaoInstitucional);
        coop.setLogoUrl(logoUrl);
        coop.setEmailInstitucional(emailInstitucional);
        coop.setTelefoneWhatsapp(telefoneWhatsapp);
        coop.setCep(cep);
        coop.setEndereco(endereco);
        coop.setCidade(cidade);
        coop.setEstado(estado);
        coop.setDataFundacao(dataFundacao);
        coop.setCanalContatoPreferencial(canalContatoPreferencial);
        coop.setLinkWhatsapp(linkWhatsapp);
        coop.setStatus(status);
        if (coop.getIdCooperativa() == null) {
            throw new IllegalArgumentException("idCooperativa é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE cooperativa SET nome = ?, cnpj = ?, nome_publico = ?, " +
                "descricao_institucional = ?, logo_url = ?, email_institucional = ?, " +
                "telefone_whatsapp = ?, cep = ?, endereco = ?, cidade = ?, estado = ?, " +
                "data_fundacao = ?, canal_contato_preferencial = ?, link_whatsapp = ?, status = ? " +
                "WHERE id_cooperativa = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, coop.getNome());
            pstmt.setString(2, utilitario.filtrarCnpj(coop.getCnpj()));
            pstmt.setString(3, coop.getNomePublico());
            pstmt.setString(4, coop.getDescricaoInstitucional());
            pstmt.setString(5, coop.getLogoUrl());
            pstmt.setString(6, coop.getEmailInstitucional());
            pstmt.setString(7, utilitario.filtrarTelefoneWhatsapp(coop.getTelefoneWhatsapp()));
            pstmt.setString(8, utilitario.filtrarCep(coop.getCep()));
            pstmt.setString(9, coop.getEndereco());
            pstmt.setString(10, coop.getCidade());
            pstmt.setString(11, coop.getEstado());
            if (coop.getDataFundacao() == null) {
                pstmt.setNull(12, Types.DATE);
            } else {
                pstmt.setDate(12, Date.valueOf(coop.getDataFundacao()));
            }
            pstmt.setString(13, coop.getCanalContatoPreferencial());
            pstmt.setString(14, coop.getLinkWhatsapp());
            pstmt.setString(15, coop.getStatus().name());
            pstmt.setLong(16, coop.getIdCooperativa());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return coop;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui a cooperativa identificada pelo ID.
    public Cooperativa excluirCooperativa(Long idCooperativa) {

        // Declaração

        Cooperativa coop = new Cooperativa();
        coop.setIdCooperativa(idCooperativa);
        if (coop.getIdCooperativa() == null) {
            throw new IllegalArgumentException("idCooperativa é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM cooperativa WHERE id_cooperativa = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, coop.getIdCooperativa());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return coop;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }
}
