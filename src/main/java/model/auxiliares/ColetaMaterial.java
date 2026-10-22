package model.auxiliares;

import util.Utilitario;

import java.math.BigDecimal;

public class ColetaMaterial {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private Long idColeta; // PK para relacionamento com Coleta
    private Long idMaterial; // PK para relacionamento com Material
    private BigDecimal quantidadeKg; // NUMERIC(12,2) [not null]

    // GETTERS E SETTERS

    public Long getIdColeta() {
        return idColeta;
    }

    public void setIdColeta(Long idColeta) {
        if (idColeta == null) {
            throw new IllegalArgumentException("idColeta é obrigatório.");
        }
        this.idColeta = idColeta;
    }

    public Long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Long idMaterial) {
        if (idMaterial == null) {
            throw new IllegalArgumentException("idMaterial é obrigatório.");
        }
        this.idMaterial = idMaterial;
    }

    public BigDecimal getQuantidadeKg() {
        return quantidadeKg;
    }

    public void setQuantidadeKg(BigDecimal quantidadeKg) {
        if (quantidadeKg == null) {
            throw new IllegalArgumentException("quantidadeKg é obrigatória.");
        }
        if (quantidadeKg.signum() < 0) {
            throw new IllegalArgumentException("quantidadeKg não pode ser negativa.");
        }
        utilitario.validarNumeroDecimal(quantidadeKg.toPlainString(), 10L, 2);
        this.quantidadeKg = quantidadeKg;
    }

}
