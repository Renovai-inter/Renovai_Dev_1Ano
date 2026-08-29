
package model;

import java.time.LocalDateTime;

public class Favorito {

    // ATRIBUTOS

    private Long idEmpresa;
    private Long idCooperativa;
    private LocalDateTime dataAdicao;

    // GETTERS E SETTERS

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public LocalDateTime getDataAdicao() { return dataAdicao; }

    public void setDataAdicao(LocalDateTime dataAdicao) { this.dataAdicao = dataAdicao; }

}
