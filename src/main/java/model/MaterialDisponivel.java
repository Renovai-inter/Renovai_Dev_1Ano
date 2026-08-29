
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MaterialDisponivel {

    // ATRIBUTOS

    private Long idDisponibilidade;
    private Long idCooperativa;
    private Long idMaterial;
    private BigDecimal quantidadeKg;
    private String observacoes;
    private LocalDateTime dataAtualizacao;

    // GETTERS E SETTERS

    public Long getIdDisponibilidade() { return idDisponibilidade; }

    public void setIdDisponibilidade(Long idDisponibilidade) { this.idDisponibilidade = idDisponibilidade; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdMaterial() { return idMaterial; }

    public void setIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }

    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }

}
