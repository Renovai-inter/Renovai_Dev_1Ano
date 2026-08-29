
package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Coleta {

    // ATRIBUTOS

    private Long idColeta;
    private Long idCooperativa;
    private String tipo;
    private String status;
    private Long idRota;
    private Long idEnderecoRota;
    private Long idCooperadoResponsavel;
    private String origemEntrega;
    private String nomeLocalOrigem;
    private LocalDate dataAgendada;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private BigDecimal pesoTotalKg;
    private String observacoes;

    // GETTERS E SETTERS

    public Long getIdColeta() { return idColeta; }

    public void setIdColeta(Long idColeta) { this.idColeta = idColeta; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Long getIdRota() { return idRota; }

    public void setIdRota(Long idRota) { this.idRota = idRota; }

    public Long getIdEnderecoRota() { return idEnderecoRota; }

    public void setIdEnderecoRota(Long idEnderecoRota) { this.idEnderecoRota = idEnderecoRota; }

    public Long getIdCooperadoResponsavel() { return idCooperadoResponsavel; }

    public void setIdCooperadoResponsavel(Long idCooperadoResponsavel) { this.idCooperadoResponsavel = idCooperadoResponsavel; }

    public String getOrigemEntrega() { return origemEntrega; }

    public void setOrigemEntrega(String origemEntrega) { this.origemEntrega = origemEntrega; }

    public String getNomeLocalOrigem() { return nomeLocalOrigem; }

    public void setNomeLocalOrigem(String nomeLocalOrigem) { this.nomeLocalOrigem = nomeLocalOrigem; }

    public LocalDate getDataAgendada() { return dataAgendada; }

    public void setDataAgendada(LocalDate dataAgendada) { this.dataAgendada = dataAgendada; }

    public LocalDateTime getDataInicio() { return dataInicio; }

    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }

    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public BigDecimal getPesoTotalKg() { return pesoTotalKg; }

    public void setPesoTotalKg(BigDecimal pesoTotalKg) { this.pesoTotalKg = pesoTotalKg; }

    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

}
