
package model;

public class ConfiguracaoRateio {

    // ATRIBUTOS

    private Long idCooperativa;
    private String tipo;
    private boolean participaAtivo;
    private boolean participaAfastado;
    private boolean participaInativo;

    // GETTERS E SETTERS

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isParticipaAtivo() { return participaAtivo; }

    public void setParticipaAtivo(boolean participaAtivo) { this.participaAtivo = participaAtivo; }

    public boolean isParticipaAfastado() { return participaAfastado; }

    public void setParticipaAfastado(boolean participaAfastado) { this.participaAfastado = participaAfastado; }

    public boolean isParticipaInativo() { return participaInativo; }

    public void setParticipaInativo(boolean participaInativo) { this.participaInativo = participaInativo; }

}
