package model.auxiliares;

import java.util.List;

public class RelacionamentosColeta {

    // ATRIBUTOS

    private Long idCooperativa; // FK para cooperativa
    private Long idRota; // FK para rota
    private Long idEnderecoRota; // FK para en
    private Long idCooperadoResponsavel; // FK
    private List<ColetaMaterial> materiaisColetados; // Conjunto de materiais coletados

    // GETTERS E SETTERS

    // Retorna o valor de idCooperativa.
    public Long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    // Retorna o valor de idRota.
    public Long getIdRota() { return idRota; }

    // Define o valor de idRota.
    public void setIdRota(Long idRota) { this.idRota = idRota; }

    // Retorna o valor de idEnderecoRota.
    public Long getIdEnderecoRota() { return idEnderecoRota; }

    // Define o valor de idEnderecoRota.
    public void setIdEnderecoRota(Long idEnderecoRota) { this.idEnderecoRota = idEnderecoRota; }

    // Retorna o valor de idCooperadoResponsavel.
    public Long getIdCooperadoResponsavel() { return idCooperadoResponsavel; }

    // Define o valor de idCooperadoResponsavel.
    public void setIdCooperadoResponsavel(Long idCooperadoResponsavel) { this.idCooperadoResponsavel = idCooperadoResponsavel; }

}
