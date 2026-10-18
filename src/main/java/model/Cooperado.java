package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Cooperado {

    // ATRIBUTOS (tabela cooperado)

    private Long idCooperado;
    private Long idUsuario;
    private Long idCooperativa;
    private String codigoCooperado;
    private String cargo;
    private String status;
    private LocalDateTime dataCadastro;

    // ATRIBUTOS (vem de outras tabelas, preenchidos pelos JOINs da DAO)

    private String nomeCompleto;
    private String nomeUsuario;
    private String email;
    private String nomeCooperativa;
    private boolean senhaTemporaria;
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
    public String getCargo() { return cargo; }

    // Define o valor de cargo.
    public void setCargo(String cargo) {
        if (cargo != null && cargo.codePointCount(0, cargo.length()) > 15) {
            throw new IllegalArgumentException("cargo deve ter no máximo 15 caracteres.");
        }
        this.cargo = cargo;
    }

    // Retorna o valor de status.
    public String getStatus() { return status; }

    // Define o valor de status.
    public void setStatus(String status) {
        if (status != null && status.codePointCount(0, status.length()) > 10) {
            throw new IllegalArgumentException("status deve ter no máximo 10 caracteres.");
        }
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
        if (nomeCompleto != null && nomeCompleto.codePointCount(0, nomeCompleto.length()) > 150) {
            throw new IllegalArgumentException("nomeCompleto deve ter no máximo 150 caracteres.");
        }
        this.nomeCompleto = nomeCompleto;
    }

    // Retorna o valor de nomeUsuario.
    public String getNomeUsuario() { return nomeUsuario; }

    // Define o valor de nomeUsuario.
    public void setNomeUsuario(String nomeUsuario) {
        if (nomeUsuario != null && nomeUsuario.codePointCount(0, nomeUsuario.length()) > 50) {
            throw new IllegalArgumentException("nomeUsuario deve ter no máximo 50 caracteres.");
        }
        this.nomeUsuario = nomeUsuario;
    }

    // Retorna o valor de email.
    public String getEmail() { return email; }

    // Define o valor de email.
    public void setEmail(String email) {
        if (email != null && email.codePointCount(0, email.length()) > 150) {
            throw new IllegalArgumentException("email deve ter no máximo 150 caracteres.");
        }
        this.email = email;
    }

    // Retorna o valor de nomeCooperativa.
    public String getNomeCooperativa() { return nomeCooperativa; }

    // Define o valor de nomeCooperativa.
    public void setNomeCooperativa(String nomeCooperativa) {
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

}
