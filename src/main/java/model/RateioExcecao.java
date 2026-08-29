
package model;

import java.math.BigDecimal;

public class RateioExcecao {

    // ATRIBUTOS

    private Long idExcecao;
    private Long idCooperativa;
    private Long idCooperado;
    private String tipo;
    private BigDecimal valor;

    // GETTERS E SETTERS

    public Long getIdExcecao() { return idExcecao; }

    public void setIdExcecao(Long idExcecao) { this.idExcecao = idExcecao; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdCooperado() { return idCooperado; }

    public void setIdCooperado(Long idCooperado) { this.idCooperado = idCooperado; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getValor() { return valor; }

    public void setValor(BigDecimal valor) { this.valor = valor; }

}
