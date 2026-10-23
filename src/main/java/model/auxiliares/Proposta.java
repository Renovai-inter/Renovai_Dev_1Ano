package model.auxiliares;

import model.enums.AutorNegociacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Proposta {

    // ATRIBUTOS (tabela proposta)

    private Long idProposta;
    private Long idNegociacao;
    private AutorNegociacao autor;
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

    public String getAutor() {
        if (autor == null) {
            return null;
        }
        return autor.name();
    }

    public void setAutor(String autor) {
        for (AutorNegociacao opcao : AutorNegociacao.values()) {
            if (opcao.name().equals(autor)) {
                this.autor = opcao;
                return;
            }
        }
        throw new IllegalArgumentException("autor da proposta inválido.");
    }

    public BigDecimal getValorKg() { return valorKg; }

    public void setValorKg(BigDecimal valorKg) {
        if (valorKg == null || valorKg.signum() <= 0) {
            throw new IllegalArgumentException("valorKg deve ser positivo.");
        }
        this.valorKg = valorKg;
    }

    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    public void setQuantidadeKg(BigDecimal quantidadeKg) {
        if (quantidadeKg != null && quantidadeKg.signum() <= 0) {
            throw new IllegalArgumentException("quantidadeKg deve ser positiva.");
        }
        this.quantidadeKg = quantidadeKg;
    }

    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataProposta() { return dataProposta; }

    public void setDataProposta(LocalDateTime dataProposta) { this.dataProposta = dataProposta; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    // Indica se a proposta foi feita pela cooperativa.
    public boolean isCooperativa() {
        return autor == AutorNegociacao.COOPERATIVA;
    }

}
