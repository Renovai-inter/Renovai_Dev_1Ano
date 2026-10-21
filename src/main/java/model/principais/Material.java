
package model.principais;

import model.enums.CategoriaMaterial;
import util.Utilitario;

public class Material {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private Long idMaterial; // PK
    private String nome; // NOT NULL - VARCHAR(100)
    private CategoriaMaterial categoria; // NOT NULL - VARCHAR(20) - CHECK
    private String origem; // NOT NULL - VARCHAR(10)

    // GETTERS E SETTERS

    // Retorna o valor de idMaterial.
    public Long getIdMaterial() { return idMaterial; }

    // Define o valor de idMaterial.
    public void setIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    // Retorna o valor de nome.
    public String getNome() { return nome; }

    // Define o valor de nome.
    public void setNome(String nome) {
        nome = utilitario.normalizarTextoLivre(nome);
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome é obrigatório.");
        }
        if (nome.codePointCount(0, nome.length()) > 100) {
            throw new IllegalArgumentException("nome deve ter no máximo 100 caracteres.");
        }
        this.nome = nome;
    }

    // Retorna o valor de categoria.
    public String getCategoria() {
        if (categoria == null) {
            return null;
        }
        return categoria.name();
    }

    // Define o valor de categoria.
    public void setCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("categoria é obrigatória.");
        }
        this.categoria = CategoriaMaterial.valueOf(utilitario.normalizarTextoLivre(categoria));
    }

    public String getOrigem() {
        return origem;
    }

    // Valida e guarda a origem obrigatória do material.
    public void setOrigem(String origem) {
        origem = utilitario.normalizarTextoLivre(origem);
        if (origem == null || origem.isBlank()) {
            throw new IllegalArgumentException("origem é obrigatória.");
        }
        if (origem.codePointCount(0, origem.length()) > 10) {
            throw new IllegalArgumentException("origem deve ter no máximo 10 caracteres.");
        }
        this.origem = origem;
    }
}
