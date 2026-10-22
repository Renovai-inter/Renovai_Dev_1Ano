package model.principais;

import model.auxiliares.RelacionamentosCooperado;
import model.auxiliares.Usuario;
import model.enums.CargoCooperado;
import model.enums.StatusCooperado;
import util.Utilitario;

import java.time.LocalDateTime;

public class Cooperado {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private Long idCooperado; // PK
    private Usuario usuario;
    private final RelacionamentosCooperado relacionamentos = new RelacionamentosCooperado();
    private Long idUsuario; // FK
    private Long idCooperativa; // FK
    private String codigoCooperado; // NOT NULL - VARCHAR(20)
    private CargoCooperado cargo; // campo opcional com CHECK
    private StatusCooperado status; // NOT NULL - CHECK COM DEFAULT
    private LocalDateTime dataCadastro; // NOT NULL COM DEFAULT

    // GETTERS E SETTERS

    // Retorna o valor de idCooperado.
    public Long getIdCooperado() { return idCooperado; }

    // Define o valor de idCooperado.
    public void setIdCooperado(Long idCooperado) { this.idCooperado = idCooperado; }

    // Retorna a conta associada ao cooperado, quando carregada.
    public Usuario getUsuario() { return usuario; }

    // Associa a conta já preenchida pelos seus próprios setters.
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // Reúne as atividades e permissões associadas ao cooperado.
    public RelacionamentosCooperado getRelacionamentos() { return relacionamentos; }

    // Retorna o valor de idUsuario.
    public Long getIdUsuario() { return idUsuario; }

    // Define o valor de idUsuario.
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    // Retorna o valor de idCooperativa.
    public Long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    // Retorna o valor de codigoCooperado.
    public String getCodigoCooperado() { return codigoCooperado; }

    // Define o valor de codigoCooperado.
    public void setCodigoCooperado(String codigoCooperado) {
        if (codigoCooperado == null || codigoCooperado.isBlank()) {
            throw new IllegalArgumentException("codigoCooperado é obrigatório.");
        }
        if (codigoCooperado.length() > 20) {
            throw new IllegalArgumentException("codigoCooperado deve ter no máximo 20 caracteres.");
        }
        this.codigoCooperado = codigoCooperado;
    }

    // Retorna o valor de cargo.
    public CargoCooperado getCargo() { return cargo; }

    // Define o valor de cargo.
    public void setCargo(CargoCooperado cargo) {
        this.cargo = cargo;
    }

    // Retorna o valor de status.
    public StatusCooperado getStatus() { return status; }

    // Define o valor de status.
    public void setStatus(StatusCooperado status) {
        this.status = status;
    }

    // Retorna o valor de dataCadastro.
    public LocalDateTime getDataCadastro() { return dataCadastro; }

    // Define o valor de dataCadastro.
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

}
