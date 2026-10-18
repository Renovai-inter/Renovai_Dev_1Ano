
package model;

import jdk.jshell.Snippet;
import model.enums.StatusNegociacao;
import util.Utilitario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Negociacao {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private Long idNegociacao; // PK
    private Long idCooperativa; // FK
    private Long idEmpresa; // FK
    private Long idMaterial; // FK
    private BigDecimal quantidadeKg; // NUMERIC(12, 2)
    private BigDecimal valorKgAtual; // NOT NULL - NUMERIC(10, 2)
    private BigDecimal valorTotalAtual; // NUMERIC(14, 2)
    private StatusNegociacao status; // NOT NULL COM CHECK E DEFAULT
    private LocalDateTime dataCriacao; // NOT NULL - TIMESTAMP COM DEFAULT
    private LocalDateTime dataConclusao; // TIMESTAMP

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
    public void setQuantidadeKg(BigDecimal quantidadeKg) {
        if (quantidadeKg != null) {
            if (quantidadeKg.signum() <= 0) {
                throw new IllegalArgumentException("quantidadeKg deve ser positiva.");
            }
            utilitario.validarNumeroDecimal(quantidadeKg.toPlainString(), 12, 2);
        }
        this.quantidadeKg = quantidadeKg;
    }

    // Retorna o valor de valorKgAtual.
    public BigDecimal getValorKgAtual() { return valorKgAtual; }

    // Define o valor de valorKgAtual.
    public void setValorKgAtual(BigDecimal valorKgAtual) {
        if (valorKgAtual == null || valorKgAtual.signum() <= 0) {
            throw new IllegalArgumentException("valorKgAtual deve ser positivo.");
        }
        utilitario.validarNumeroDecimal(valorKgAtual.toPlainString(), 10, 2);
        this.valorKgAtual = valorKgAtual;
    }

    // Retorna o valor de valorTotalAtual.
    public BigDecimal getValorTotalAtual() { return valorTotalAtual; }

    // Define o valor de valorTotalAtual.
    public void setValorTotalAtual(BigDecimal valorTotalAtual) {
        if (valorTotalAtual != null) {
            utilitario.validarNumeroDecimal(valorTotalAtual.toPlainString(), 14, 2);
        }
        this.valorTotalAtual = valorTotalAtual;
    }

    // Retorna o valor de status.
    public StatusNegociacao getStatus() { return status; }

    // Define o valor de status.
    public void setStatus(StatusNegociacao status) {
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
