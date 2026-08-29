
package model;

import java.time.LocalDateTime;

public class Cooperado {

    // ATRIBUTOS

    private Long idCooperado;
    private Long idUsuario;
    private Long idCooperativa;
    private String codigoCooperado;
    private String cargo;
    private String status;
    private LocalDateTime dataCadastro;

    // GETTERS E SETTERS

    public Long getIdCooperado() { return idCooperado; }

    public void setIdCooperado(Long idCooperado) { this.idCooperado = idCooperado; }

    public Long getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public String getCodigoCooperado() { return codigoCooperado; }

    public void setCodigoCooperado(String codigoCooperado) { this.codigoCooperado = codigoCooperado; }

    public String getCargo() { return cargo; }

    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

}
