
package model;

import java.time.LocalDate;

public class Assinatura {
    private Long idAssinatura;
    private Long idEmpresa;
    private Long idPlano;
    private String status;
    private LocalDate dataInicio;
    private LocalDate dataVencimento;


    public Long getIdAssinatura() { return idAssinatura; }

    public void setIdAssinatura(Long idAssinatura) { this.idAssinatura = idAssinatura; }

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public Long getIdPlano() { return idPlano; }

    public void setIdPlano(Long idPlano) { this.idPlano = idPlano; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDate getDataInicio() { return dataInicio; }

    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataVencimento() { return dataVencimento; }

    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
}
