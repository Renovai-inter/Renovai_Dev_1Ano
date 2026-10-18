
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Negociacao {

    // ATRIBUTOS

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

    // GETTERS E SETTERS

    // Retorna o valor de idNegociacao.
    public Long getIdNegociacao() { return idNegociacao; }

    // Define o valor de idNegociacao.
    public void setIdNegociacao(Long idNegociacao) { this.idNegociacao = idNegociacao; }

    // Retorna o valor de idCooperativa.
    public Long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    // Retorna o valor de idEmpresa.
    public Long getIdEmpresa() { return idEmpresa; }

    // Define o valor de idEmpresa.
    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    // Retorna o valor de idMaterial.
    public Long getIdMaterial() { return idMaterial; }

    // Define o valor de idMaterial.
    public void setIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    // Retorna o valor de quantidadeKg.
    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    // Define o valor de quantidadeKg.
    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }

    // Retorna o valor de valorKgAtual.
    public BigDecimal getValorKgAtual() { return valorKgAtual; }

    // Define o valor de valorKgAtual.
    public void setValorKgAtual(BigDecimal valorKgAtual) { this.valorKgAtual = valorKgAtual; }

    // Retorna o valor de valorTotalAtual.
    public BigDecimal getValorTotalAtual() { return valorTotalAtual; }

    // Define o valor de valorTotalAtual.
    public void setValorTotalAtual(BigDecimal valorTotalAtual) { this.valorTotalAtual = valorTotalAtual; }

    // Retorna o valor de status.
    public String getStatus() { return status; }

    // Define o valor de status.
    public void setStatus(String status) {
        if (status != null && status.codePointCount(0, status.length()) > 20) {
            throw new IllegalArgumentException("status deve ter no máximo 20 caracteres.");
        }
        this.status = status;
    }

    // Retorna o valor de dataCriacao.
    public LocalDateTime getDataCriacao() { return dataCriacao; }

    // Define o valor de dataCriacao.
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    // Retorna o valor de dataConclusao.
    public LocalDateTime getDataConclusao() { return dataConclusao; }

    // Define o valor de dataConclusao.
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

}
