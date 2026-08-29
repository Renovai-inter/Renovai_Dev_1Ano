
package model;

import java.math.BigDecimal;

public class RateioDistribuicao {

    // ATRIBUTOS

    private Long idPeriodo;
    private Long idCooperado;
    private BigDecimal valorRecebido;

    // GETTERS E SETTERS

    public Long getIdPeriodo() { return idPeriodo; }

    public void setIdPeriodo(Long idPeriodo) { this.idPeriodo = idPeriodo; }

    public Long getIdCooperado() { return idCooperado; }

    public void setIdCooperado(Long idCooperado) { this.idCooperado = idCooperado; }

    public BigDecimal getValorRecebido() { return valorRecebido; }

    public void setValorRecebido(BigDecimal valorRecebido) { this.valorRecebido = valorRecebido; }

}
