
package model;

public class PreferenciaNotificacao {

    // ATRIBUTOS

    private Long idPreferencia;
    private Long idCooperativa;
    private Long idEmpresa;
    private String tipoNotificacao;
    private boolean receberEmail;
    private boolean receberApp;

    // GETTERS E SETTERS

    public Long getIdPreferencia() { return idPreferencia; }

    public void setIdPreferencia(Long idPreferencia) { this.idPreferencia = idPreferencia; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getTipoNotificacao() { return tipoNotificacao; }

    public void setTipoNotificacao(String tipoNotificacao) { this.tipoNotificacao = tipoNotificacao; }

    public boolean isReceberEmail() { return receberEmail; }

    public void setReceberEmail(boolean receberEmail) { this.receberEmail = receberEmail; }

    public boolean isReceberApp() { return receberApp; }

    public void setReceberApp(boolean receberApp) { this.receberApp = receberApp; }

}
