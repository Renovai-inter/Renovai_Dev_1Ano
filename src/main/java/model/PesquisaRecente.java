
package model;

import java.time.LocalDateTime;

public class PesquisaRecente {

    // ATRIBUTOS

    private Long idPesquisa;
    private Long idEmpresa;
    private String termoPesquisa;
    private LocalDateTime dataPesquisa;

    // GETTERS E SETTERS

    public Long getIdPesquisa() { return idPesquisa; }

    public void setIdPesquisa(Long idPesquisa) { this.idPesquisa = idPesquisa; }

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getTermoPesquisa() { return termoPesquisa; }

    public void setTermoPesquisa(String termoPesquisa) { this.termoPesquisa = termoPesquisa; }

    public LocalDateTime getDataPesquisa() { return dataPesquisa; }

    public void setDataPesquisa(LocalDateTime dataPesquisa) { this.dataPesquisa = dataPesquisa; }

}
