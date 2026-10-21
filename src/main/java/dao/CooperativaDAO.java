package dao;

import model.Cooperativa;
import model.Cooperado;
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
        String sql = "INSERT INTO cooperativa (id_cooperativa, nome, cnpj, nome_publico, " +
                "descricao_institucional, logo_url, email_institucional, telefone_whatsapp, " +
                "cep, endereco, cidade, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            conexao.setAutoCommit(false);
            long ultimoId = utilitario.selecionarUltimoId("cooperativa");
            long idCooperativa = ultimoId == -1 ? 1 : ultimoId + 1;
            coop.setIdCooperativa(idCooperativa);
            int retornoCooperativa;
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
                retornoCooperativa = pstmt.executeUpdate();
            }
            int[] retornoGestor = cooperadoDAO.cadastrarGestor(conexao, coop, gestor,
                    senhaTemporariaGestor);
            conexao.commit();
            return retornoCooperativa == 0 || retornoGestor[0] == 0 || retornoGestor[1] == 0
                    ? null : coop;
        } catch (SQLException e) {
            try {
                conexao.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }
            throw new RuntimeException("Falha ao cadastrar cooperativa e gestor.", e);
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

    // Preenche uma cooperativa com os dados selecionados na consulta.
    private Cooperativa lerCooperativa(ResultSet rs) throws SQLException {
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
        cooperativa.setDataCadastro(cadastro == null ? null : cadastro.toLocalDateTime());
        return cooperativa;
    }

    // === METODOS READ ================================================================================================

    private static final String CONSULTA_COOPERATIVAS =
            "SELECT coop.id_cooperativa, coop.nome, coop.cnpj, coop.cidade, coop.estado, coop.status, " +
            "(SELECT COUNT(*) FROM cooperado c WHERE c.id_cooperativa = coop.id_cooperativa) AS cooperados, " +
            "coop.data_cadastro AS cadastro FROM cooperativa coop";

    // Lista as cooperativas cadastradas com dados resumidos.
    public ArrayList<Cooperativa> exibirCooperativas() {
        return consultarCooperativas("");
    }

    // Busca cooperativas pelo nome ou CNPJ informado.
    public ArrayList<Cooperativa> buscarCooperativasPorNomeOuCodigo(String busca) {
        String filtro = "%" + busca.strip() + "%";
        return consultarCooperativas(" WHERE coop.nome ILIKE ? OR coop.cnpj LIKE ?", filtro, filtro);
    }

    // Lista as cooperativas da cidade informada.
    public ArrayList<Cooperativa> filtrarCooperativasPorCidade(String cidadeBusca) {
        return consultarCooperativas(" WHERE coop.cidade ILIKE ?", cidadeBusca.strip());
    }

    // Lista as cooperativas do estado informado.
    public ArrayList<Cooperativa> filtrarCooperativasPorEstado(String estadoBusca) {
        return consultarCooperativas(" WHERE coop.estado = ?", utilitario.validarUf(estadoBusca));
    }

    // Lista as cooperativas com o status informado.
    public ArrayList<Cooperativa> filtrarCooperativasPorStatus(String statusBusca) {
        String status = StatusCooperativa.valueOf(statusBusca.strip().toUpperCase()).name();
        return consultarCooperativas(" WHERE coop.status = ?", status);
    }

    // Executa uma consulta com os filtros definidos pelo método público.
    private ArrayList<Cooperativa> consultarCooperativas(String filtro, String... parametros) {
        Connection conexao = conn.conectar();
        ArrayList<Cooperativa> cooperativas = new ArrayList<>();
        try (PreparedStatement pstmt = conexao.prepareStatement(CONSULTA_COOPERATIVAS + filtro)) {
            for (int indice = 0; indice < parametros.length; indice++) {
                pstmt.setString(indice + 1, parametros[indice]);
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

    // Atualiza um campo permitido da cooperativa identificada.
    public Cooperativa atualizarCooperativa(Long idCooperativa, String campo, String valorInformado) {
        Cooperativa coop = new Cooperativa();
        coop.setIdCooperativa(idCooperativa);
        if (coop.getIdCooperativa() == null) {
            throw new IllegalArgumentException("idCooperativa é obrigatório.");
        }
        String coluna = campo.strip().toLowerCase(java.util.Locale.ROOT);
        Object valor = switch (coluna) {
            case "nome" -> { coop.setNome(valorInformado); yield coop.getNome(); }
            case "cnpj" -> { coop.setCnpj(valorInformado); yield utilitario.filtrarCnpj(coop.getCnpj()); }
            case "nome_publico" -> { coop.setNomePublico(valorInformado); yield coop.getNomePublico(); }
            case "descricao_institucional" -> { coop.setDescricaoInstitucional(valorInformado); yield coop.getDescricaoInstitucional(); }
            case "logo_url" -> { coop.setLogoUrl(valorInformado); yield coop.getLogoUrl(); }
            case "email_institucional" -> { coop.setEmailInstitucional(valorInformado); yield coop.getEmailInstitucional(); }
            case "telefone_whatsapp" -> { coop.setTelefoneWhatsapp(valorInformado); yield utilitario.filtrarTelefoneWhatsapp(coop.getTelefoneWhatsapp()); }
            case "cep" -> { coop.setCep(valorInformado); yield utilitario.filtrarCep(coop.getCep()); }
            case "endereco" -> { coop.setEndereco(valorInformado); yield coop.getEndereco(); }
            case "cidade" -> { coop.setCidade(valorInformado); yield coop.getCidade(); }
            case "estado" -> { coop.setEstado(valorInformado); yield coop.getEstado(); }
            case "data_fundacao" -> { coop.setDataFundacao(valorInformado == null ? null : LocalDate.parse(valorInformado)); yield coop.getDataFundacao() == null ? null : Date.valueOf(coop.getDataFundacao()); }
            case "canal_contato_preferencial" -> { coop.setCanalContatoPreferencial(valorInformado); yield coop.getCanalContatoPreferencial(); }
            case "link_whatsapp" -> { coop.setLinkWhatsapp(valorInformado); yield coop.getLinkWhatsapp(); }
            case "status" -> { coop.setStatus(valorInformado == null ? null : StatusCooperativa.valueOf(valorInformado)); yield coop.getStatus() == null ? null : coop.getStatus().name(); }
            default -> throw new IllegalArgumentException("Campo da cooperativa não permitido: " + campo);
        };
        String sql = "UPDATE cooperativa SET " + coluna + " = ? WHERE id_cooperativa = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setObject(1, valor);
            pstmt.setLong(2, coop.getIdCooperativa());
            return pstmt.executeUpdate() == 0 ? null : coop;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Exclui a cooperativa identificada pelo ID.
    public Cooperativa excluirCooperativa(Long idCooperativa) {
        Cooperativa coop = new Cooperativa();
        coop.setIdCooperativa(idCooperativa);
        if (coop.getIdCooperativa() == null) {
            throw new IllegalArgumentException("idCooperativa é obrigatório.");
        }
        String sql = "DELETE FROM cooperativa WHERE id_cooperativa = ?";
        Connection conexao = conn.conectar();
        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, coop.getIdCooperativa());
            return pstmt.executeUpdate() == 0 ? null : coop;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao excluir cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }
}
