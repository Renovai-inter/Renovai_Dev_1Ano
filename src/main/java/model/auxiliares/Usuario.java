package model.auxiliares;

import model.enums.TipoUsuario;
import util.Utilitario;

public class Usuario {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private long idUsuario; // PK
    private String nomeCompleto; // NOT NULL - VARCHAR(150)
    private String nomeUsuario; // NOT NULL - VARCHAR(50)
    private TipoUsuario tipoUsuario; // NOT NULL - CHECK
    private String senhaHash; // NOT NULL - VARCHAR(255)
    private boolean senhaTemporaria; // BOOLEAN COM DEFAULT
    private String email; // NOT NULL - VARCHAR(150)

    public Usuario() {
    }

    public Usuario(String nomeCompleto, String nomeUsuario, String email,
                   String senhaHash, boolean senhaTemporaria, TipoUsuario tipoUsuario) {
        setNomeCompleto(nomeCompleto);
        setNomeUsuario(nomeUsuario);
        setEmail(email);
        setSenhaHash(senhaHash);
        setSenhaTemporaria(senhaTemporaria);
        setTipoUsuario(tipoUsuario);
    }

    // GETTERS E SETTERS

    // Retorna o valor de nomeCompleto.
    public String getNomeCompleto() { return nomeCompleto; }

    // Define o valor de nomeCompleto.
    public void setNomeCompleto(String nomeCompleto) {
        nomeCompleto = utilitario.normalizarTextoLivre(nomeCompleto);
        if (nomeCompleto == null || nomeCompleto.isBlank()) {
            throw new IllegalArgumentException("nomeCompleto é obrigatório.");
        }
        if (nomeCompleto.length() > 150) {
            throw new IllegalArgumentException("nomeCompleto deve ter no máximo 150 caracteres.");
        }
        this.nomeCompleto = nomeCompleto;
    }

    // Retorna o valor de nomeUsuario.
    public String getNomeUsuario() { return nomeUsuario; }

    // Define o valor de nomeUsuario.
    public void setNomeUsuario(String nomeUsuario) {
        nomeUsuario = utilitario.normalizarTextoLivre(nomeUsuario);
        if (nomeUsuario == null || nomeUsuario.isBlank()) {
            throw new IllegalArgumentException("nomeUsuario é obrigatório.");
        }
        if (nomeUsuario.length() > 50) {
            throw new IllegalArgumentException("nomeUsuario deve ter no máximo 50 caracteres.");
        }
        this.nomeUsuario = nomeUsuario;
    }

    // Retorna o valor de email.
    public String getEmail() { return email; }

    // Define o valor de email.
    public void setEmail(String email) {
        email = utilitario.filtrarEmailInstitucional(email);
        if (email.length() > 150) {
            throw new IllegalArgumentException("email deve ter no máximo 150 caracteres.");
        }
        this.email = email;
    }

    // Retorna o valor de tipo de usuário
    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    // Define o valor de tipo de usuário
    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        if (tipoUsuario == null) {
            throw new IllegalArgumentException("tipoUsuario é obrigatório.");
        }
        this.tipoUsuario = tipoUsuario;
    }

    // Informa o valor de senhaTemporaria.
    public boolean isSenhaTemporaria() { return senhaTemporaria; }

    // Define o valor de senhaTemporaria.
    public void setSenhaTemporaria(boolean senhaTemporaria) { this.senhaTemporaria = senhaTemporaria; }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("senhaHash é obrigatória.");
        }
        if (senhaHash.length() > 255) {
            throw new IllegalArgumentException("senhaHash deve ter no máximo 255 caracteres.");
        }
        this.senhaHash = senhaHash;
    }
}
