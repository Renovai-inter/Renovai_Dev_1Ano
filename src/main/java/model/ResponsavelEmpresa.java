
package model;

public class ResponsavelEmpresa {
    private Long idResponsavel;
    private Long idUsuario;
    private Long idEmpresa;
    private String cargo;


    public Long getIdResponsavel() { return idResponsavel; }

    public void setIdResponsavel(Long idResponsavel) { this.idResponsavel = idResponsavel; }

    public Long getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getCargo() { return cargo; }

    public void setCargo(String cargo) { this.cargo = cargo; }
}
