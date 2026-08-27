
package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Venda {
    private Long idVenda;
    private Long idCooperativa;
    private Long idComprador;
    private Long idNegociacao;
    private Long idMaterial;
    private BigDecimal quantidadeKg;
    private BigDecimal valorKg;
    private BigDecimal valorTotal;
    private LocalDate dataVenda;
    private String origem;
    private String documentoUrl;
    private String numeroDocumento;
    private String observacoes;
    private String status;


    public Long getIdVenda() { return idVenda; }

    public void setIdVenda(Long idVenda) { this.idVenda = idVenda; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdComprador() { return idComprador; }

    public void setIdComprador(Long idComprador) { this.idComprador = idComprador; }

    public Long getIdNegociacao() { return idNegociacao; }

    public void setIdNegociacao(Long idNegociacao) { this.idNegociacao = idNegociacao; }

    public Long getIdMaterial() { return idMaterial; }

    public void setIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }

    public BigDecimal getValorKg() { return valorKg; }

    public void setValorKg(BigDecimal valorKg) { this.valorKg = valorKg; }

    public BigDecimal getValorTotal() { return valorTotal; }

    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public LocalDate getDataVenda() { return dataVenda; }

    public void setDataVenda(LocalDate dataVenda) { this.dataVenda = dataVenda; }

    public String getOrigem() { return origem; }

    public void setOrigem(String origem) { this.origem = origem; }

    public String getDocumentoUrl() { return documentoUrl; }

    public void setDocumentoUrl(String documentoUrl) { this.documentoUrl = documentoUrl; }

    public String getNumeroDocumento() { return numeroDocumento; }

    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
