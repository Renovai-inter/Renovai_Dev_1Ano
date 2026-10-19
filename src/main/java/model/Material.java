
package model;

import model.enums.CategoriaMaterial;
import util.Utilitario;

public class Material {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private Long idMaterial; // PK
    private String nome; // NOT NULL - VARCHAR(100)
    private CategoriaMaterial categoria; // NOT NULL - VARCHAR(20) - CHECK

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
        if (nome != null && nome.codePointCount(0, nome.length()) > 100) {
            throw new IllegalArgumentException("nome deve ter no máximo 100 caracteres.");
        }
        this.nome = nome;
    }

    // Retorna o valor de categoria.
    public String getCategoria() { return categoria == null ? null : categoria.name(); }

    // Define o valor de categoria.
    public void setCategoria(String categoria) {
        this.categoria = categoria == null ? null : CategoriaMaterial.valueOf(utilitario.normalizarTextoLivre(categoria));
    }

}
