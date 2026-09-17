package dao;

import util.Conexao;

public class NegociacaoDAO {

    // ATRIBUTOS

    private Conexao conn = new Conexao();

    // CONSTRUTOR

    public NegociacaoDAO() {
    }

    // === METODOS CREATE ==============================================================================================

/*Fazer contraproposta – a cooperativa cria uma nova condição (novo valor) para enviar à recicladora, podendo incluir uma observação explicando a alteração.*/

    // === METODOS READ ================================================================================================

    /*Visualizar novas propostas – listagem das propostas recebidas que ainda aguardam resposta (empresa recicladora, material, quantidade, valor por kg, valor total, data de recebimento).
Visualizar detalhes da proposta – abrir uma proposta para ver informações da empresa (nome, responsável, forma de contato) e informações da proposta (material, quantidade, valor por kg, valor total, data, observações).
Visualizar negociações em andamento – propostas que já tiveram interação mas ainda não foram finalizadas (empresa, material, quantidade, última atualização, status).
Visualizar histórico de negociação – dentro de uma negociação em andamento, ver o histórico de interações.
Visualizar propostas aceitas – registro de negociações aprovadas por ambas as partes (empresa, material, quantidade, valor acordado, data da aprovação).
Visualizar histórico geral – negociações finalizadas ou encerradas (vendas concluídas, propostas recusadas, negociações canceladas).
Filtrar negociações – por status: todas, novas propostas, em negociação, aceitas, recusadas, concluídas.*/



    // === METODOS UPDATE ==============================================================================================

    /*Aceitar proposta – altera o status da negociação de "nova proposta" (ou "em negociação") para "aceita".
Enviar nova contraproposta em negociação já em andamento – atualiza a negociação com uma nova condição de valor.
Aceitar ou recusar oferta dentro de uma negociação em andamento – atualiza o status da negociação existente.*/



    // === METODOS DELETE ==============================================================================================

    /*Recusar proposta – encerra a negociação (não exclui o registro, mas o move para o histórico como "recusada"), podendo incluir motivo da recusa.
Cancelar negociação – aparece no histórico como negociação cancelada (é mencionado como resultado possível, mas o documento não detalha a ação específica de cancelamento).*/



}
