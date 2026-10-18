
package model;

public class Rota {

    // ATRIBUTOS

    private Long idRota; // PK
    private Long idCooperativa; // FK
    private String nomeRota; // NOT NULL - VARCHAR(100)

    // GETTERS E SETTERS

    // Retorna o valor de idRota.
    public Long getIdRota() { return idRota; }

    // Define o valor de idRota.
    public void setIdRota(Long idRota) { this.idRota = idRota; }

    // Retorna o valor de idCooperativa.
    public Long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    // Retorna o valor de nomeRota.
    public String getNomeRota() { return nomeRota; }

    // Define o valor de nomeRota.
    public void setNomeRota(String nomeRota) {
        if (nomeRota != null && nomeRota.codePointCount(0, nomeRota.length()) > 100) {
            throw new IllegalArgumentException("nomeRota deve ter no máximo 100 caracteres.");
        }
        this.nomeRota = nomeRota;
    }

}
