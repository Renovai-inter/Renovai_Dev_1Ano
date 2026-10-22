package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Movimentacao {

    // ATRIBUTOS (tabela proposta)

    private Long idProposta;
    private Long idNegociacao;
    private String autor;
    private BigDecimal valorKg;
    private BigDecimal quantidadeKg;
    private String observacoes;
    private LocalDateTime dataProposta;

    // ATRIBUTO calculado (a tabela nao tem essa coluna): Proposta, Contraproposta ou Aceita

    private String tipo;

    // GETTERS E SETTERS

    public Long getIdProposta() { return idProposta; }

    public void setIdProposta(Long idProposta) { this.idProposta = idProposta; }

    public Long getIdNegociacao() { return idNegociacao; }

    public void setIdNegociacao(Long idNegociacao) { this.idNegociacao = idNegociacao; }

    public String getAutor() { return autor; }

    public void setAutor(String autor) { this.autor = autor; }

    public BigDecimal getValorKg() { return valorKg; }

    public void setValorKg(BigDecimal valorKg) { this.valorKg = valorKg; }

    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }

    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataProposta() { return dataProposta; }

    public void setDataProposta(LocalDateTime dataProposta) { this.dataProposta = dataProposta; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    // true se quem fez a movimentacao foi a cooperativa (o resto e a recicladora)
    public boolean isCooperativa() {
        return autor != null && autor.toUpperCase().startsWith("COOP");
    }

}
