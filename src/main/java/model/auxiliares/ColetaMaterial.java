package model.auxiliares;

import util.Utilitario;

import java.math.BigDecimal;

public class ColetaMaterial {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private long idColeta; // PK para relacionamento com Coleta
    private long idMaterial; // PK para relacionamento com Material
    private BigDecimal pesoTotalInformadoKg; //  NUMERIC(12,2) [not null]

    // GETTERS E SETTERS

    public long getIdColeta() {
        return idColeta;
    }

    public void setIdColeta(long idColeta) {
        this.idColeta = idColeta;
    }

    public long getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(long idMaterial) {
        this.idMaterial = idMaterial;
    }

    public BigDecimal getPesoTotalInformadoKg() {
        return pesoTotalInformadoKg;
    }

    public void setPesoTotalInformadoKg(BigDecimal pesoTotalInformadoKg) {
        if (pesoTotalInformadoKg == null) {
            throw new IllegalArgumentException("pesoTotalInformadoKg é obrigatório.");
        }
        if (pesoTotalInformadoKg.signum() < 0) {
            throw new IllegalArgumentException("pesoTotalInformadoKg não pode ser negativo.");
        }
        utilitario.validarNumeroDecimal(pesoTotalInformadoKg.toPlainString(), 10L, 2);
        this.pesoTotalInformadoKg = pesoTotalInformadoKg;
    }

}
