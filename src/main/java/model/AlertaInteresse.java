
package model;

import java.math.BigDecimal;

public class AlertaInteresse {

    // ATRIBUTOS

    private Long idAlerta;
    private Long idEmpresa;
    private Long idMaterial;
    private BigDecimal quantidadeMinimaKg;
    private BigDecimal raioMaximoKm;
    private boolean notificarEmail;
    private boolean notificarApp;

    // GETTERS E SETTERS

    public Long getIdAlerta() { return idAlerta; }

    public void setIdAlerta(Long idAlerta) { this.idAlerta = idAlerta; }

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public Long getIdMaterial() { return idMaterial; }

    public void setIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    public BigDecimal getQuantidadeMinimaKg() { return quantidadeMinimaKg; }

    public void setQuantidadeMinimaKg(BigDecimal quantidadeMinimaKg) { this.quantidadeMinimaKg = quantidadeMinimaKg; }

    public BigDecimal getRaioMaximoKm() { return raioMaximoKm; }

    public void setRaioMaximoKm(BigDecimal raioMaximoKm) { this.raioMaximoKm = raioMaximoKm; }

    public boolean isNotificarEmail() { return notificarEmail; }

    public void setNotificarEmail(boolean notificarEmail) { this.notificarEmail = notificarEmail; }

    public boolean isNotificarApp() { return notificarApp; }

    public void setNotificarApp(boolean notificarApp) { this.notificarApp = notificarApp; }

}
