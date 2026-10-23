package model.auxiliares;

import java.util.List;
import java.util.ArrayList;

public class RelacionamentosColeta {

    // ATRIBUTOS

    private Long idCooperativa; // FK para cooperativa
    private Long idRota; // FK para rota
    private Long idEnderecoRota; // FK para en
    private Long idCooperadoResponsavel; // FK
    private List<ColetaMaterial> materiaisColetados = new ArrayList<>(); // Conjunto de materiais coletados

    public RelacionamentosColeta() {
    }

    public RelacionamentosColeta(Long idCooperativa, Long idRota,
                                Long idEnderecoRota, Long idCooperadoResponsavel,
                                List<ColetaMaterial> materiaisColetados) {
        setIdCooperativa(idCooperativa);
        setIdRota(idRota);
        setIdEnderecoRota(idEnderecoRota);
        setIdCooperadoResponsavel(idCooperadoResponsavel);
        setMateriaisColetados(materiaisColetados);
    }

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

    // Retorna os materiais associados à coleta.
    public List<ColetaMaterial> getMateriaisColetados() { return materiaisColetados; }

    // Define os materiais associados à coleta.
    public void setMateriaisColetados(List<ColetaMaterial> materiaisColetados) {
        if (materiaisColetados == null) {
            throw new IllegalArgumentException("materiaisColetados é obrigatório.");
        }
        this.materiaisColetados = new ArrayList<>(materiaisColetados);
    }

}
