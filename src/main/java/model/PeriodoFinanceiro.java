
package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PeriodoFinanceiro {

    // ATRIBUTOS

    private Long idPeriodo;
    private Long idCooperativa;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String status;
    private BigDecimal receitaTotal;
    private BigDecimal despesaTotal;
    private BigDecimal sobra;
    private BigDecimal valorRateioTotal;
    private Integer qtdCooperadosParticipantes;
    private LocalDateTime dataFechamento;

    // GETTERS E SETTERS

    public Long getIdPeriodo() { return idPeriodo; }

    public void setIdPeriodo(Long idPeriodo) { this.idPeriodo = idPeriodo; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public LocalDate getDataInicio() { return dataInicio; }

    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }

    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public BigDecimal getReceitaTotal() { return receitaTotal; }

    public void setReceitaTotal(BigDecimal receitaTotal) { this.receitaTotal = receitaTotal; }

    public BigDecimal getDespesaTotal() { return despesaTotal; }

    public void setDespesaTotal(BigDecimal despesaTotal) { this.despesaTotal = despesaTotal; }

    public BigDecimal getSobra() { return sobra; }

    public void setSobra(BigDecimal sobra) { this.sobra = sobra; }

    public BigDecimal getValorRateioTotal() { return valorRateioTotal; }

    public void setValorRateioTotal(BigDecimal valorRateioTotal) { this.valorRateioTotal = valorRateioTotal; }

    public Integer getQtdCooperadosParticipantes() { return qtdCooperadosParticipantes; }

    public void setQtdCooperadosParticipantes(Integer qtdCooperadosParticipantes) { this.qtdCooperadosParticipantes = qtdCooperadosParticipantes; }

    public LocalDateTime getDataFechamento() { return dataFechamento; }

    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }

}
