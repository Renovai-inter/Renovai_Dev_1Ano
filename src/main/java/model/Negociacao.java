
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Negociacao {
    private Long idNegociacao;
    private Long idCooperativa;
    private Long idEmpresa;
    private Long idMaterial;
    private BigDecimal quantidadeKg;
    private BigDecimal valorKgAtual;
    private BigDecimal valorTotalAtual;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;


    public Long getIdNegociacao() { return idNegociacao; }

    public void setIdNegociacao(Long idNegociacao) { this.idNegociacao = idNegociacao; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public Long getIdMaterial() { return idMaterial; }

    public void setIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }

    public BigDecimal getValorKgAtual() { return valorKgAtual; }

    public void setValorKgAtual(BigDecimal valorKgAtual) { this.valorKgAtual = valorKgAtual; }

    public BigDecimal getValorTotalAtual() { return valorTotalAtual; }

    public void setValorTotalAtual(BigDecimal valorTotalAtual) { this.valorTotalAtual = valorTotalAtual; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }

    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
}
