package model.auxiliares;

import util.Utilitario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PropostaNegociacao {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private Long idProposta;
    private Long idNegociacao;
    private String autor;
    private BigDecimal valorKg;
    private BigDecimal quantidadeKg;
    private String observacoes;
    private LocalDateTime dataProposta;

    // GETTERS E SETTERS

    // Retorna o ID da proposta.
    public Long getIdProposta() { return idProposta; }

    // Define o ID da proposta.
    public void setIdProposta(Long idProposta) { this.idProposta = idProposta; }

    // Retorna a negociação à qual a proposta pertence.
    public Long getIdNegociacao() { return idNegociacao; }

    // Define a negociação à qual a proposta pertence.
    public void setIdNegociacao(Long idNegociacao) { this.idNegociacao = idNegociacao; }

    // Retorna o autor da proposta.
    public String getAutor() { return autor; }

    // Aceita somente os autores documentados para proposta.
    public void setAutor(String autor) {
        autor = utilitario.normalizarTextoLivre(autor);
        if (!"COOPERATIVA".equals(autor) && !"EMPRESA".equals(autor)) {
            throw new IllegalArgumentException("autor da proposta inválido.");
        }
        this.autor = autor;
    }

    // Retorna o valor oferecido por quilograma.
    public BigDecimal getValorKg() { return valorKg; }

    // Define um valor positivo por quilograma.
    public void setValorKg(BigDecimal valorKg) {
        if (valorKg == null || valorKg.signum() <= 0) {
            throw new IllegalArgumentException("valorKg deve ser positivo.");
        }
        utilitario.validarNumeroDecimal(valorKg.toPlainString(), 8, 2);
        this.valorKg = valorKg;
    }

    // Retorna a quantidade opcional oferecida.
    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    // Define uma quantidade positiva quando informada.
    public void setQuantidadeKg(BigDecimal quantidadeKg) {
        if (quantidadeKg != null) {
            if (quantidadeKg.signum() <= 0) {
                throw new IllegalArgumentException("quantidadeKg deve ser positiva.");
            }
            utilitario.validarNumeroDecimal(quantidadeKg.toPlainString(), 10, 2);
        }
        this.quantidadeKg = quantidadeKg;
    }

    // Retorna as observações da proposta.
    public String getObservacoes() { return observacoes; }

    // Normaliza as observações informadas.
    public void setObservacoes(String observacoes) {
        this.observacoes = utilitario.normalizarTextoLivre(observacoes);
    }

    // Retorna o horário de criação, quando carregado.
    public LocalDateTime getDataProposta() { return dataProposta; }

    // Define o horário de criação, quando carregado.
    public void setDataProposta(LocalDateTime dataProposta) { this.dataProposta = dataProposta; }
}
