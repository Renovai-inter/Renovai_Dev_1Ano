
package model;

public class Comprador {

    // ATRIBUTOS

    private Long idComprador;
    private Long idCooperativa;
    private Long idEmpresaRecicladora;
    private String nomeEmpresa;
    private String nomeResponsavel;
    private String whatsapp;
    private String telefone;
    private String email;
    private String cidade;
    private String observacoes;

    // GETTERS E SETTERS

    public Long getIdComprador() { return idComprador; }

    public void setIdComprador(Long idComprador) { this.idComprador = idComprador; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdEmpresaRecicladora() { return idEmpresaRecicladora; }

    public void setIdEmpresaRecicladora(Long idEmpresaRecicladora) { this.idEmpresaRecicladora = idEmpresaRecicladora; }

    public String getNomeEmpresa() { return nomeEmpresa; }

    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }

    public String getNomeResponsavel() { return nomeResponsavel; }

    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }

    public String getWhatsapp() { return whatsapp; }

    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getCidade() { return cidade; }

    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

}
