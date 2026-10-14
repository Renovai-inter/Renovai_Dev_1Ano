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

    public String getNomeCompleto() { return nomeCompleto; }

    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getNomeUsuario() { return nomeUsuario; }

    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getNomeCooperativa() { return nomeCooperativa; }

    public void setNomeCooperativa(String nomeCooperativa) { this.nomeCooperativa = nomeCooperativa; }

    public boolean isSenhaTemporaria() { return senhaTemporaria; }

    public void setSenhaTemporaria(boolean senhaTemporaria) { this.senhaTemporaria = senhaTemporaria; }

    public ArrayList<String> getAtividades() { return atividades; }

    public void setAtividades(ArrayList<String> atividades) { this.atividades = atividades; }

}