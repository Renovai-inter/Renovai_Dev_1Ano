
package model;

import java.math.BigDecimal;

public class Plano {
    private Long idPlano;
    private String nome;
    private BigDecimal valor;
    private String periodicidade;
    private String descricaoRecursos;


    public Long getIdPlano() { return idPlano; }

    public void setIdPlano(Long idPlano) { this.idPlano = idPlano; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getValor() { return valor; }

    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getPeriodicidade() { return periodicidade; }

    public void setPeriodicidade(String periodicidade) { this.periodicidade = periodicidade; }

    public String getDescricaoRecursos() { return descricaoRecursos; }

    public void setDescricaoRecursos(String descricaoRecursos) { this.descricaoRecursos = descricaoRecursos; }
}
