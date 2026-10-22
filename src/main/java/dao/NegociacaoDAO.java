package dao;

import model.principais.Negociacao;
import model.principais.PropostaNegociacao;
import model.enums.StatusNegociacao;
import util.ConexaoBD;
import util.Utilitario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class NegociacaoDAO {
    private final ConexaoBD conn = new ConexaoBD();
    private final Utilitario utilitario = new Utilitario();

    // Inicializa o acesso aos dados de negociação.
    public NegociacaoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Registra uma proposta da cooperativa para a negociação informada.
    public PropostaNegociacao fazerContraproposta(Long idNegociacao, BigDecimal valorKg,
                                                  BigDecimal quantidadeKg, String observacoes) {

        // Declaração

        PropostaNegociacao proposta = new PropostaNegociacao();
        proposta.setIdNegociacao(idNegociacao);
        proposta.setAutor("COOPERATIVA");
        proposta.setValorKg(valorKg);
        proposta.setQuantidadeKg(quantidadeKg);
        proposta.setObservacoes(observacoes);
        if (proposta.getIdNegociacao() == null) {
            throw new IllegalArgumentException("ID da negociação e valor por quilo são obrigatórios.");
        }
        long ultimoId = utilitario.selecionarUltimoId("proposta");
        long idProposta;
        if (ultimoId == -1) {
            idProposta = 1;
        } else {
            idProposta = ultimoId + 1;
        }
        proposta.setIdProposta(idProposta);
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO proposta (id_proposta, id_negociacao, autor, valor_kg, " +
                "quantidade_kg, observacoes) VALUES (?, ?, ?, ?, ?, ?)";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setLong(1, proposta.getIdProposta());
            pstmt.setLong(2, proposta.getIdNegociacao());
            pstmt.setString(3, proposta.getAutor());
            pstmt.setBigDecimal(4, proposta.getValorKg());
            pstmt.setBigDecimal(5, proposta.getQuantidadeKg());
            pstmt.setString(6, proposta.getObservacoes());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return proposta;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao registrar contraproposta da cooperativa.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS READ ================================================================================================

    // Lista propostas que aguardam resposta.
    public ArrayList<Negociacao> novasPropostas() {

        // Execução e retorno

        return consultarNegociacoes(" WHERE status = 'AGUARDANDO_RESPOSTA'");
    }

    // Lista os dados da negociação vinculada a uma empresa.
    public ArrayList<Negociacao> analisarDetalhesProposta() {

        // Execução e retorno

        return consultarNegociacoes(" WHERE id_empresa IS NOT NULL");
    }

    // Lista negociações em andamento.
    public ArrayList<Negociacao> negociacaoEmAndamento() {

        // Execução e retorno

        return consultarNegociacoes(" WHERE status = 'EM_NEGOCIACAO'");
    }

    // Lista negociações encerradas para consulta do histórico.
    public ArrayList<Negociacao> historicoNegociacao() {

        // Execução e retorno

        return consultarNegociacoes(" WHERE status IN ('ACEITA', 'RECUSADA', 'CANCELADA', 'CONCLUIDA')");
    }

    // Lista as propostas aceitas.
    public ArrayList<Negociacao> propostasAceitas() {

        // Execução e retorno

        return consultarNegociacoes(" WHERE status = 'ACEITA'");
    }

    // Lista todas as negociações, inclusive as finalizadas.
    public ArrayList<Negociacao> historicoGeral() {

        // Execução e retorno

        return consultarNegociacoes("");
    }

    // Executa uma consulta e monta uma Model para cada linha.
    private ArrayList<Negociacao> consultarNegociacoes(String filtro) {

        // Declaração

        Connection conexao = conn.conectar();
        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT id_negociacao, id_cooperativa, id_empresa, id_material, " +
                "quantidade_kg, valor_kg_atual, valor_total_atual, status, data_criacao, " +
                "data_conclusao FROM negociacao" + filtro;

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Negociacao negociacao = new Negociacao();
                negociacao.setIdNegociacao(rs.getLong("id_negociacao"));
                negociacao.setIdCooperativa(rs.getLong("id_cooperativa"));
                negociacao.setIdEmpresa(rs.getLong("id_empresa"));
                negociacao.setIdMaterial(rs.getLong("id_material"));
                negociacao.setQuantidadeKg(rs.getBigDecimal("quantidade_kg"));
                negociacao.setValorKgAtual(rs.getBigDecimal("valor_kg_atual"));
                negociacao.setValorTotalAtual(rs.getBigDecimal("valor_total_atual"));
                negociacao.setStatus(StatusNegociacao.valueOf(rs.getString("status")));
                Timestamp criacao = rs.getTimestamp("data_criacao");
                Timestamp conclusao = rs.getTimestamp("data_conclusao");
                if (criacao != null) {
                    negociacao.setDataCriacao(criacao.toLocalDateTime());
                }
                if (conclusao != null) {
                    negociacao.setDataConclusao(conclusao.toLocalDateTime());
                }
                negociacoes.add(negociacao);
            }
            return negociacoes;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao consultar negociações.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS UPDATE ==============================================================================================

    // Aceita a proposta identificada e registra sua conclusão.
    public Negociacao aceitarProposta(Long idNegociacao, LocalDateTime dataConclusao) {

        // Declaração

        Negociacao negociacao = new Negociacao();
        negociacao.setIdNegociacao(idNegociacao);
        negociacao.setDataConclusao(dataConclusao);

        // Execução e retorno

        if (atualizarStatusFinal(negociacao, StatusNegociacao.ACEITA) == 0) {
            return null;
        }
        return negociacao;
    }

    // Atualiza o valor de uma negociação em andamento.
    public Negociacao enviarContraProposta(Long idNegociacao, BigDecimal valorKgAtual) {

        // Declaração

        Negociacao negociacao = new Negociacao();
        negociacao.setIdNegociacao(idNegociacao);
        negociacao.setValorKgAtual(valorKgAtual);
        if (negociacao.getIdNegociacao() == null || negociacao.getValorKgAtual() == null) {
            throw new IllegalArgumentException("ID e valor por quilo são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE negociacao SET valor_kg_atual = ? " +
                "WHERE id_negociacao = ? AND status = 'EM_NEGOCIACAO'";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, negociacao.getValorKgAtual());
            pstmt.setLong(2, negociacao.getIdNegociacao());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return negociacao;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar contraproposta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra a decisão sobre uma oferta em negociação.
    public Negociacao decisaoDeOferta(Long idNegociacao, StatusNegociacao status,
                                     LocalDateTime dataConclusao) {

        // Declaração

        Negociacao negociacao = new Negociacao();
        negociacao.setIdNegociacao(idNegociacao);
        negociacao.setStatus(status);
        negociacao.setDataConclusao(dataConclusao);
        if (negociacao.getStatus() == null) {
            throw new IllegalArgumentException("Status é obrigatório.");
        }
        if (negociacao.getStatus() == StatusNegociacao.ACEITA ||
                negociacao.getStatus() == StatusNegociacao.RECUSADA ||
                negociacao.getStatus() == StatusNegociacao.CANCELADA ||
                negociacao.getStatus() == StatusNegociacao.CONCLUIDA) {
            if (atualizarStatusFinal(negociacao, negociacao.getStatus()) == 0) {
                return null;
            }
            return negociacao;
        }
        if (negociacao.getIdNegociacao() == null) {
            throw new IllegalArgumentException("idNegociacao é obrigatório.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE negociacao SET status = ? WHERE id_negociacao = ? " +
                "AND status = 'EM_NEGOCIACAO'";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, negociacao.getStatus().name());
            pstmt.setLong(2, negociacao.getIdNegociacao());
            if (pstmt.executeUpdate() == 0) {
                return null;
            }
            return negociacao;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao registrar decisão da oferta.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }

    // Recusa a proposta preservando seu histórico.
    public Negociacao recusarProposta(Long idNegociacao, LocalDateTime dataConclusao) {

        // Declaração

        Negociacao negociacao = new Negociacao();
        negociacao.setIdNegociacao(idNegociacao);
        negociacao.setDataConclusao(dataConclusao);

        // Execução e retorno

        if (atualizarStatusFinal(negociacao, StatusNegociacao.RECUSADA) == 0) {
            return null;
        }
        return negociacao;
    }

    // Cancela a negociação preservando seu histórico.
    public Negociacao cancelarNegociacao(Long idNegociacao, LocalDateTime dataConclusao) {

        // Declaração

        Negociacao negociacao = new Negociacao();
        negociacao.setIdNegociacao(idNegociacao);
        negociacao.setDataConclusao(dataConclusao);

        // Execução e retorno

        if (atualizarStatusFinal(negociacao, StatusNegociacao.CANCELADA) == 0) {
            return null;
        }
        return negociacao;
    }

    // Atualiza um status final e sua data obrigatória de conclusão.
    private int atualizarStatusFinal(Negociacao negociacao, StatusNegociacao status) {

        // Declaração

        if (negociacao.getIdNegociacao() == null || negociacao.getDataConclusao() == null) {
            throw new IllegalArgumentException("ID e data de conclusão são obrigatórios.");
        }
        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE negociacao SET status = ?, data_conclusao = ? WHERE id_negociacao = ?";

        // Executar PreparedStatement

        try (PreparedStatement pstmt = conexao.prepareStatement(sql)) {
            pstmt.setString(1, status.name());
            pstmt.setTimestamp(2, Timestamp.valueOf(negociacao.getDataConclusao()));
            pstmt.setLong(3, negociacao.getIdNegociacao());
            int linhas = pstmt.executeUpdate();
            if (linhas > 0) {
                negociacao.setStatus(status);
            }
            return linhas;
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao atualizar o status da negociação.", e);
        } finally {
            conn.desconectar(conexao);
        }
    }
}
