
package model;

import java.math.BigDecimal;

public class ColetaMaterial {

    // ATRIBUTOS

    private Long idColeta;
    private Long idMaterial;
    private BigDecimal quantidadeKg;

    // GETTERS E SETTERS

    public Long getIdColeta() { return idColeta; }

    public void setIdColeta(Long idColeta) { this.idColeta = idColeta; }

    public Long getIdMaterial() { return idMaterial; }

    public void setIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }

}
