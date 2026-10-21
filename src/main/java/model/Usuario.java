package model;

public class Usuario {

    private Long idUsuario;
    private String nome;
    private String email;
    private String senhaHash;
    private String tipoUsuario;

    public Long getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getSenhaHash() { return senhaHash; }

    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }

    public String getTipoUsuario() { return tipoUsuario; }

    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}