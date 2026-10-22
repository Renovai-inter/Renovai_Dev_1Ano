
package model;

import model.enums.StatusColeta;
import model.enums.TipoColeta;
import model.enums.OrigemEntrega;
import util.Utilitario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class Coleta {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private Long idColeta; // PK
    private Long idCooperativa; // FK
    private TipoColeta tipo; // NOT NULL
    private StatusColeta status; // NOT NULL com DEFAULT
    private Long idRota; // FK
    private Long idEnderecoRota; // FK
    private Long idCooperadoResponsavel; // FK
    private String origemEntrega; // VARCHAR(15)
    private String nomeLocalOrigem; // VARCHAR(150)
    private Date dataAgendada; // DATE
    private LocalDateTime dataInicio; // TIMESTAMP
    private LocalDateTime dataFim; // TIMESTAMP
    private BigDecimal pesoTotalKg; // NUMERIC(12,2)
    private String observacoes; // TEXT

    // GETTERS E SETTERS

    // Retorna o valor de idColeta.
    public Long getIdColeta() { return idColeta; }

    // Define o valor de idColeta.
    public void setIdColeta(Long idColeta) { this.idColeta = idColeta; }

    // Retorna o valor de idCooperativa.
    public Long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    // Retorna o valor de tipo.
    public TipoColeta getTipo() { return tipo; }

    // Define o valor de tipo.
    public void setTipo(TipoColeta tipo) {
        this.tipo = tipo;
    }

    // Retorna o valor de status.
    public StatusColeta getStatus() { return status; }

    // Define o valor de status.
    public void setStatus(StatusColeta status) {
        this.status = status;
    }

    // Retorna o valor de idRota.
    public Long getIdRota() { return idRota; }

    // Define o valor de idRota.
    public void setIdRota(Long idRota) { this.idRota = idRota; }

    // Retorna o valor de idEnderecoRota.
    public Long getIdEnderecoRota() { return idEnderecoRota; }

    // Define o valor de idEnderecoRota.
    public void setIdEnderecoRota(Long idEnderecoRota) { this.idEnderecoRota = idEnderecoRota; }

    // Retorna o valor de idCooperadoResponsavel.
    public Long getIdCooperadoResponsavel() { return idCooperadoResponsavel; }

    // Define o valor de idCooperadoResponsavel.
    public void setIdCooperadoResponsavel(Long idCooperadoResponsavel) { this.idCooperadoResponsavel = idCooperadoResponsavel; }

    // Retorna o valor de origemEntrega.
    public String getOrigemEntrega() { return origemEntrega; }

    // Define o valor de origemEntrega.
    public void setOrigemEntrega(String origemEntrega) {
        origemEntrega = utilitario.normalizarTextoLivre(origemEntrega);
        if (origemEntrega != null) {
            OrigemEntrega.valueOf(origemEntrega);
        }
        this.origemEntrega = origemEntrega;
    }

    // Retorna o valor de nomeLocalOrigem.
    public String getNomeLocalOrigem() { return nomeLocalOrigem; }

    // Define o valor de nomeLocalOrigem.
    public void setNomeLocalOrigem(String nomeLocalOrigem) {
        nomeLocalOrigem = utilitario.normalizarTextoLivre(nomeLocalOrigem);
        if (nomeLocalOrigem != null && nomeLocalOrigem.codePointCount(0, nomeLocalOrigem.length()) > 150) {
            throw new IllegalArgumentException("nomeLocalOrigem deve ter no máximo 150 caracteres.");
        }
        this.nomeLocalOrigem = nomeLocalOrigem;
    }

    // Retorna o valor de dataAgendada.
    public Date getDataAgendada() { return dataAgendada; }

    // Define o valor de dataAgendada.
    public void setDataAgendada(Date dataAgendada) { this.dataAgendada = dataAgendada; }

    // Retorna o valor de dataInicio.
    public LocalDateTime getDataInicio() { return dataInicio; }

    // Define o valor de dataInicio.
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    // Retorna o valor de dataFim.
    public LocalDateTime getDataFim() { return dataFim; }

    // Define o valor de dataFim.
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    // Retorna o valor de pesoTotalKg.
    public BigDecimal getPesoTotalKg() { return pesoTotalKg; }

    // Define o valor de pesoTotalKg.
    public void setPesoTotalKg(BigDecimal pesoTotalKg) {
        if (pesoTotalKg != null) {
            if (pesoTotalKg.signum() < 0) {
                throw new IllegalArgumentException("pesoTotalKg não pode ser negativo.");
            }
            utilitario.validarNumeroDecimal(pesoTotalKg.toPlainString(), 10L, 2);
        }
        this.pesoTotalKg = pesoTotalKg;
    }

    // Retorna o valor de observacoes.
    public String getObservacoes() { return observacoes; }

    // Define o valor de observacoes.
    public void setObservacoes(String observacoes) { this.observacoes = utilitario.normalizarTextoLivre(observacoes); }

}
