package dao;

import model.Coleta;
import model.Cooperado;
import model.Negociacao;
import util.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class NegociacaoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    // Inicializa o acesso aos dados de negociacao.
    public NegociacaoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    // Registra uma contraproposta vinculada à negociação.
    public int fazerContraproposta(Negociacao negociacao) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "INSERT INTO negociacao(id_cooperativa, id_empresa, quantidade_kg, valor_kg_atual, valor_total_atual, status) VALUES(?,?,?,?,?,?)";

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);

            pstm.setLong(1, negociacao.getIdCooperativa());
            pstm.setLong(2, negociacao.getIdEmpresa());
            pstm.setBigDecimal(3, negociacao.getQuantidadeKg());
            pstm.setBigDecimal(4, negociacao.getValorKgAtual());
            pstm.setBigDecimal(5, negociacao.getValorTotalAtual());
            pstm.setString(6, negociacao.getStatus());

            pstm.executeUpdate();
        } catch (SQLException sqle) {
            sqle.getMessage();
        }
        return 0;
    }

    // === METODOS READ ================================================================================================

    // Lista as propostas que aguardam resposta.
    public ArrayList<Negociacao> novasPropostas() {

        // Declaração

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT id_cooperativa, id_empresa, id_material, quantidade_kg, valor_kg_atual, valor_total_atual, data_conclusao FROM negociacao";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            Negociacao negociacao = null;
            while (rst.next()) {
                negociacao = new Negociacao();

                negociacao.setIdCooperativa(rst.getLong("id_cooperativa"));
                negociacao.setIdEmpresa(rst.getLong("id_empresa"));
                negociacao.setIdMaterial(rst.getLong("id_material"));
                negociacao.setQuantidadeKg(rst.getBigDecimal("quantidade_kg"));
                negociacao.setValorTotalAtual(rst.getBigDecimal("valor_total_atual"));
                negociacao.setDataConclusao(rst.getTimestamp("data_conclusao").toLocalDateTime());
            }

            negociacoes.add(negociacao);

        } catch (SQLException e) {
            e.getMessage();
        } finally {
            conn.desconectar(conexao);

            return negociacoes;
        }
    }

    // Consulta os detalhes de uma proposta e da empresa envolvida.
    public ArrayList<Negociacao> analisarDetalhesProposta() {

        // Declaração

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT e.id_empresa, e.nome, e.email_institucional, e.telefone_whatsapp, n.id_material, n.quantidade_kg, n.valor_kg_atual, n.valor_total_atual" +
                " FROM negociacao n join empresa_recicladora e on n.id_negociacao = e.id_empresa";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            Negociacao negociacao = null;
            while (rst.next()) {
                negociacao = new Negociacao();

                negociacao.setIdEmpresa(rst.getLong("id_empresa"));
                negociacao.setIdEmpresa(rst.getLong("nome"));
                negociacao.setIdEmpresa(rst.getLong("email_institucional"));
                negociacao.setIdEmpresa(rst.getLong("telefone_whatsapp"));
                negociacao.setIdMaterial(rst.getLong("id_material"));
                negociacao.setQuantidadeKg(rst.getBigDecimal("quantidade_kg"));
                negociacao.setValorTotalAtual(rst.getBigDecimal("valor_total_atual"));
            }

            negociacoes.add(negociacao);

        } catch (SQLException e) {
            e.getMessage();
        } finally {
            conn.desconectar(conexao);
        }
        return negociacoes;
    }

    // Lista as negociações com interação ainda não finalizadas.
    public ArrayList<Negociacao> negociacaoEmAndamento() {

        // Declaração

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT id_empresa, id_material, quantidade_kg, status FROM negociacao";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try{
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            Negociacao negociacao = null;
            while(rst.next()){
                negociacao = new Negociacao();

                negociacao.setIdEmpresa(rst.getLong("id_empresa"));
                negociacao.setIdMaterial(rst.getLong("id_material"));
                negociacao.setQuantidadeKg(rst.getBigDecimal("quantidade_kg"));
                negociacao.setStatus(rst.getString("status"));
            }

            negociacoes.add(negociacao);

        } catch (SQLException e) {
            e.getMessage();
        } finally {
            conn.desconectar(conexao);
        }
        return negociacoes;
    }

    // Consulta o histórico de uma negociação identificada.
    public ArrayList<Negociacao> historicoNegociacao() {

        // Declaração

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT * FROM negociacao where id_negociacao = ? order by data_criacao";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            Negociacao negociacao = null;
            while(rst.next()){
              negociacao = new Negociacao();

              negociacao.setIdNegociacao(rst.getLong("id_negociacao"));
            }
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            conn.desconectar(conexao);
        }
        return negociacoes;
    }

    // Lista as propostas aceitas pelas partes.
    public ArrayList<Negociacao> propostasAceitas() {

        // Declaração

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT id_empresa, id_material, quantidade_kg, valor_total_atual, data_conclusao FROM negociacao";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            Negociacao negociacao = null;
            while(rst.next()){
                negociacao = new Negociacao();

                negociacao.setIdEmpresa(rst.getLong("id_empresa"));
                negociacao.setIdMaterial(rst.getLong("id_material"));
                negociacao.setQuantidadeKg(rst.getBigDecimal("quantidade_kg"));
                negociacao.setValorTotalAtual(rst.getBigDecimal("valor_total_atual"));
                negociacao.setDataConclusao(rst.getTimestamp("data_conclusao").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            conn.desconectar(conexao);
        }
        return negociacoes;
    }

    // Lista as negociações finalizadas ou encerradas.
    public ArrayList<Negociacao> historicoGeral() {

        // Declaração

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        // Comando SQL

        String sql = "SELECT * FROM negociacao where id_negociacao = ? order by data_criacao";

        Connection conexao = conn.conectar();

        // Executar PreparedStatement

        try {
            PreparedStatement pstm = conexao.prepareStatement(sql);
            ResultSet rst = pstm.executeQuery();

            Negociacao negociacao = null;
            while(rst.next()){
                negociacao = new Negociacao();

                negociacao.setIdNegociacao(rst.getLong("id_negociacao"));
            }
        } catch (SQLException e) {
            e.getMessage();
        } finally {
            conn.desconectar(conexao);
        }
        return negociacoes;
    }

    /*Filtrar negociações – por status: todas, novas propostas, em negociação, aceitas, recusadas, concluídas.*/

    // === METODOS UPDATE ==============================================================================================

    // Aceita uma proposta e registra a conclusão da negociação.
    public int alterarCargoCooperado(Negociacao negociacao) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE negociacao\n" +
                "SET status = ? WHERE id_negociacao = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, "aceita");
            pstmt.setLong(2, negociacao.getIdNegociacao());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Atualiza a condição de valor de uma negociação em andamento.
    public int enviarContraProposta(Negociacao negociacao) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE negociacao\n" +
                "SET valor_kg_atual = ? WHERE id_negociacao = ? AND status = 'em negociação'";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setBigDecimal(1, negociacao.getValorKgAtual());
            pstmt.setLong(2, negociacao.getIdNegociacao());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Registra a decisão de aceitar ou recusar uma oferta.
    public int DecisaoDeOferta(Negociacao negociacao) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "UPDATE negociacao\n" +
                "SET status = ? WHERE id_negociacao = ? AND status = 'em negociação'";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setString(1, negociacao.getStatus());
            pstmt.setLong(2, negociacao.getIdNegociacao());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // === METODOS DELETE ==============================================================================================

    // Recusa uma proposta e conserva a negociação no histórico.
    public int recusarProposta(Negociacao negociacao) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM negociacao\n" +
                "WHERE id_negociacao = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(1, negociacao.getIdNegociacao());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

    // Cancela uma negociação e conserva seu registro no histórico.
    public int CancelarNegociacao(Negociacao negociacao) {

        // Declaração

        Connection conexao = conn.conectar();

        // Comando SQL

        String sql = "DELETE FROM negociacao\n" +
                "WHERE id_negociacao = ?";

        // Executar PreparedStatement

        try {

            PreparedStatement pstmt = conexao.prepareStatement(sql);

            pstmt.setLong(1, negociacao.getIdNegociacao());

            return pstmt.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(e);

        } finally {
            conn.desconectar(conexao);
        }
    }

 }
