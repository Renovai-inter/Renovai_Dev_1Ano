package dao;

import util.Conexao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

public class DashboardDAO {

    // === CONTADORES (ajuste tabelas, colunas e valores de status) ===
    public int contarCooperadosAtivos() {
        return contar("SELECT COUNT(*) FROM cooperado WHERE status = 'ATIVO'");
    }

    public int contarMateriais() {
        return contar("SELECT COUNT(*) FROM material");
    }

    public int contarCooperativasAtivas() {
        return contar("SELECT COUNT(*) FROM cooperativa WHERE status = 'ATIVA'");
    }

    public int contarNegociacoesEmAndamento() {
        return contar("SELECT COUNT(*) FROM negociacao WHERE status = 'EM_ANDAMENTO'");
    }

    // === DISTRIBUIÇÃO DOS MATERIAIS (kg por categoria, coletas não canceladas) ===
    public Map<String, BigDecimal> distribuicaoMateriais() {
        String sql = """
            SELECT m.categoria, SUM(cm.quantidade_kg) AS total
            FROM coleta_material cm
            JOIN material m ON m.id_material = cm.id_material
            JOIN coleta c ON c.id_coleta = cm.id_coleta
            WHERE c.status <> 'CANCELADA'
            GROUP BY m.categoria
            ORDER BY total DESC
            """;

        Map<String, BigDecimal> distribuicao = new LinkedHashMap<>();

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                if (total != null) {
                    distribuicao.put(rs.getString("categoria"), total);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar a distribuição dos materiais.", e);
        }

        return distribuicao;
    }

    // === NEGOCIAÇÕES CONCLUÍDAS NOS ÚLTIMOS N MESES ===
    public Map<YearMonth, Integer> negociacoesConcluidasPorMes(int meses) {
        YearMonth atual = YearMonth.now(ZoneId.of("America/Sao_Paulo"));
        YearMonth inicio = atual.minusMonths(meses - 1L);

        Map<YearMonth, Integer> serie = new LinkedHashMap<>();
        for (int i = 0; i < meses; i++) {
            serie.put(inicio.plusMonths(i), 0);
        }

        String sql = "SELECT data_conclusao FROM negociacao "
                + "WHERE status = 'CONCLUIDA' AND data_conclusao >= ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(inicio.atDay(1)));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date data = rs.getDate(1);
                    if (data != null) {
                        serie.computeIfPresent(
                                YearMonth.from(data.toLocalDate()), (mes, qtd) -> qtd + 1);
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar as negociações por mês.", e);
        }

        return serie;
    }

    private int contar(String sql) {
        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar registros do dashboard.", e);
        }
    }
}