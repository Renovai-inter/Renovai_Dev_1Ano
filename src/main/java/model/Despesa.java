
package model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Despesa {

    // ATRIBUTOS
    private Long idDespesa;
    private Long idPeriodo;
    private String categoria;
    private BigDecimal valor;
    private LocalDate data;
    private String descricao;
    private String observacoes;

    // GETTERS E SETTERS

    public Long getIdDespesa() { return idDespesa; }

    public void setIdDespesa(Long idDespesa) { this.idDespesa = idDespesa; }

    public Long getIdPeriodo() { return idPeriodo; }

    public void setIdPeriodo(Long idPeriodo) { this.idPeriodo = idPeriodo; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public BigDecimal getValor() { return valor; }

    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDate getData() { return data; }

    public void setData(LocalDate data) { this.data = data; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

}
