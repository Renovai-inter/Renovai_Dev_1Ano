
package model;

public class InformacaoPublicaConfig {
    private Long idCooperativa;
    private String campo;
    private boolean visivel;


    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public String getCampo() { return campo; }

    public void setCampo(String campo) { this.campo = campo; }

    public boolean isVisivel() { return visivel; }

    public void setVisivel(boolean visivel) { this.visivel = visivel; }
}
