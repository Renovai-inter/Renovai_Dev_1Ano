package dao;

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

    public NegociacaoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

    /*Fazer contraproposta – a cooperativa cria uma nova condição (novo valor) para enviar à recicladora, podendo incluir uma observação explicando a alteração.*/

    public int fazerContraproposta(Negociacao negociacao) {

        Connection conexao = conn.conectar();

        String sql = "INSERT INTO negociacao(id_cooperativa, id_empresa, quantidade_kg, valor_kg_atual, valor_total_atual, status) VALUES(?,?,?,?,?,?)";

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

    /*Visualizar novas propostas – listagem das propostas recebidas que ainda aguardam resposta (empresa recicladora, material, quantidade, valor por kg, valor total, data de recebimento).*/

    public ArrayList<Negociacao> novasPropostas() {

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        String sql = "SELECT id_cooperativa, id_empresa, id_material, quantidade_kg, valor_kg_atual, valor_total_atual, data_conclusao FROM negociacao";

        Connection conexao = conn.conectar();

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

    /*Visualizar detalhes da proposta – abrir uma proposta para ver informações da empresa (nome, responsável, forma de contato) e informações da proposta (material, quantidade, valor por kg , valor total, data, observações).*/

    public ArrayList<Negociacao> analisarDetalhesProposta() {

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        String sql = "SELECT e.id_empresa, e.nome, e.email_institucional, e.telefone_whatsapp, n.id_material, n.quantidade_kg, n.valor_kg_atual, n.valor_total_atual" +
                " FROM negociacao n join empresa_recicladora e on n.id_negociacao = e.id_empresa";

        Connection conexao = conn.conectar();

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

    /*Visualizar negociações em andamento – propostas que já tiveram interação mas ainda não foram finalizadas (empresa, material, quantidade, última atualização, status).*/

    public ArrayList<Negociacao> negociacaoEmAndamento() {

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        String sql = "SELECT id_empresa, id_material, quantidade_kg, status FROM negociacao";

        Connection conexao = conn.conectar();

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

    /*Visualizar histórico de negociação – dentro de uma negociação em andamento, ver o histórico de interações.*/

    public ArrayList<Negociacao> historicoNegociacao() {

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        String sql = "SELECT * FROM negociacao where id_negociacao = ? order by data_criacao";

        Connection conexao = conn.conectar();

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

    /*Visualizar propostas aceitas – registro de negociações aprovadas por ambas as partes (empresa, material, quantidade, valor acordado, data da aprovação).*/

    public ArrayList<Negociacao> propostasAceitas() {

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        String sql = "SELECT id_empresa, id_material, quantidade_kg, valor_total_atual, data_conclusao FROM negociacao";

        Connection conexao = conn.conectar();

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

    /*Visualizar histórico geral – negociações finalizadas ou encerradas (vendas concluídas, propostas recusadas, negociações canceladas).*/

    public ArrayList<Negociacao> historicoGeral() {

        ArrayList<Negociacao> negociacoes = new ArrayList<>();

        String sql = "SELECT * FROM negociacao where id_negociacao = ? order by data_criacao";

        Connection conexao = conn.conectar();

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

    /*Aceitar proposta – altera o status da negociação de "nova proposta" (ou "em negociação") para "aceita".
Enviar nova contraproposta em negociação já em andamento – atualiza a negociação com uma nova condição de valor.
Aceitar ou recusar oferta dentro de uma negociação em andamento – atualiza o status da negociação existente.*/


    // === METODOS DELETE ==============================================================================================

    /*Recusar proposta – encerra a negociação (não exclui o registro, mas o move para o histórico como "recusada"), podendo incluir motivo da recusa.
Cancelar negociação – aparece no histórico como negociação cancelada (é mencionado como resultado possível, mas o documento não detalha a ação específica de cancelamento).*/


 }
