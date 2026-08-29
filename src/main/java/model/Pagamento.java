
package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Pagamento {

    // ATRIBUTOS

    private Long idPagamento;
    private Long idAssinatura;
    private LocalDate dataPagamento;
    private BigDecimal valor;
    private String status;
    private String formaPagamento;
    private String identificadorTransacao;

    // GETTERS E SETTERS

    public Long getIdPagamento() { return idPagamento; }

    public void setIdPagamento(Long idPagamento) { this.idPagamento = idPagamento; }

    public Long getIdAssinatura() { return idAssinatura; }

    public void setIdAssinatura(Long idAssinatura) { this.idAssinatura = idAssinatura; }

    public LocalDate getDataPagamento() { return dataPagamento; }

    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }

    public BigDecimal getValor() { return valor; }

    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getFormaPagamento() { return formaPagamento; }

    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getIdentificadorTransacao() { return identificadorTransacao; }

    public void setIdentificadorTransacao(String identificadorTransacao) { this.identificadorTransacao = identificadorTransacao; }

}
