
package model;

import java.time.LocalDateTime;

public class Usuario {
    private Long idUsuario;
    private String nomeCompleto;
    private String nomeUsuario;
    private String senhaHash;
    private boolean senhaTemporaria;
    private String email;
    private String tipoUsuario;
    private LocalDateTime dataCriacao;


    public Long getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNomeCompleto() { return nomeCompleto; }

    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getNomeUsuario() { return nomeUsuario; }

    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getSenhaHash() { return senhaHash; }

    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public boolean isSenhaTemporaria() { return senhaTemporaria; }

    public void setSenhaTemporaria(boolean senhaTemporaria) { this.senhaTemporaria = senhaTemporaria; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getTipoUsuario() { return tipoUsuario; }

    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
