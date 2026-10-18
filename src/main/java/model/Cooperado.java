package model;

import model.enums.CargoCooperado;
import model.enums.StatusCooperado;
import model.enums.TipoUsuario;
import util.Utilitario;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Cooperado {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS (tabela cooperado)

    private Long idCooperado; // PK
    private Long idUsuario; // FK
    private Long idCooperativa; // FK
    private String codigoCooperado; // NOT NULL - VARCHAR(20)
    private CargoCooperado cargo; // NOT NULL - CHECK
    private StatusCooperado status; // NOT NULL - CHECK COM DEFAULT
    private LocalDateTime dataCadastro; // NOT NULL COM DEFAULT

    // ATRIBUTOS (vem de outras tabelas, preenchidos pelos JOINs da DAO)

    private String nomeCompleto; // NOT NULL - VARCHAR(150)
    private String nomeUsuario; // NOT NULL - VARCHAR(50)
    private TipoUsuario tipoUsuario; // NOT NULL - CHECK
    private String email; // NOT NULL - VARCHAR(150)
    private String nomeCooperativa; // NOT NULL - VARCHAR(150)
    private boolean senhaTemporaria; // BOOLEAN COM DEFAULT
    private ArrayList<String> atividades = new ArrayList<>();

    // GETTERS E SETTERS

    // Retorna o valor de idCooperado.
    public Long getIdCooperado() { return idCooperado; }

    // Define o valor de idCooperado.
    public void setIdCooperado(Long idCooperado) { this.idCooperado = idCooperado; }

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
        if (codigoCooperado != null && codigoCooperado.codePointCount(0, codigoCooperado.length()) > 20) {
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

    // Retorna o valor de nomeCompleto.
    public String getNomeCompleto() { return nomeCompleto; }

    // Define o valor de nomeCompleto.
    public void setNomeCompleto(String nomeCompleto) {
        nomeCompleto = utilitario.normalizarTextoLivre(nomeCompleto);
        if (nomeCompleto != null && nomeCompleto.codePointCount(0, nomeCompleto.length()) > 150) {
            throw new IllegalArgumentException("nomeCompleto deve ter no máximo 150 caracteres.");
        }
        this.nomeCompleto = nomeCompleto;
    }

    // Retorna o valor de nomeUsuario.
    public String getNomeUsuario() { return nomeUsuario; }

    // Define o valor de nomeUsuario.
    public void setNomeUsuario(String nomeUsuario) {
        nomeUsuario = utilitario.normalizarTextoLivre(nomeUsuario);
        if (nomeUsuario != null && nomeUsuario.codePointCount(0, nomeUsuario.length()) > 50) {
            throw new IllegalArgumentException("nomeUsuario deve ter no máximo 50 caracteres.");
        }
        this.nomeUsuario = nomeUsuario;
    }

    // Retorna o valor de email.
    public String getEmail() { return email; }

    // Define o valor de email.
    public void setEmail(String email) {
        this.email = utilitario.filtrarEmailInstitucional(email);
    }

    // Retorna o valor de nomeCooperativa.
    public String getNomeCooperativa() { return nomeCooperativa; }

    // Define o valor de nomeCooperativa.
    public void setNomeCooperativa(String nomeCooperativa) {
        nomeCooperativa = utilitario.normalizarTextoLivre(nomeCooperativa);
        if (nomeCooperativa != null && nomeCooperativa.codePointCount(0, nomeCooperativa.length()) > 150) {
            throw new IllegalArgumentException("nomeCooperativa deve ter no máximo 150 caracteres.");
        }
        this.nomeCooperativa = nomeCooperativa;
    }

    // Informa o valor de senhaTemporaria.
    public boolean isSenhaTemporaria() { return senhaTemporaria; }

    // Define o valor de senhaTemporaria.
    public void setSenhaTemporaria(boolean senhaTemporaria) { this.senhaTemporaria = senhaTemporaria; }

    // Retorna o valor de atividades.
    public ArrayList<String> getAtividades() { return atividades; }

    // Define o valor de atividades.
    public void setAtividades(ArrayList<String> atividades) { this.atividades = atividades; }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
