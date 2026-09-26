
package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cooperativa {

    // ATRIBUTOS

    private Long idCooperativa;
    private String nome;
    private String cnpj;
    private String nomePublico;
    private String descricaoInstitucional;
    private String logoUrl;
    private String emailInstitucional;
    private String telefoneWhatsapp;
    private String cep;
    private String endereco;
    private String cidade;
    private String estado;
    private LocalDate dataFundacao;
    private String canalContatoPreferencial;
    private String linkWhatsapp;
    private String status;
    private LocalDateTime dataCadastro;

    private int qtdCooperados;

    // CONSTRUTORES

    // Todos os atributos todo: substituir por setters nos construtores
    public Cooperativa(Long idCooperativa, String nome, String cnpj, String nomePublico, String descricaoInstitucional, String logoUrl, String emailInstitucional, String telefoneWhatsapp, String cep, String endereco, String cidade, String estado, LocalDate dataFundacao, String canalContatoPreferencial, String linkWhatsapp, String status, LocalDateTime dataCadastro) {
        this.idCooperativa = idCooperativa; // pk - obrigatorio
        this.nome = nome; // obrigatorio | CREATE

        if (this.cnpj.isEmpty()) {
            this.cnpj = "";
        } else {
            this.cnpj = cnpj; // opcional | CREATE
        }

        if (this.nomePublico.isEmpty()) {
            this.nomePublico = nome;
        } else {
            this.nomePublico = nomePublico; // obrigatorio
        }

        this.descricaoInstitucional = descricaoInstitucional; // opcional | CREATE
        this.logoUrl = logoUrl; // opcional | CREATE
        this.emailInstitucional = emailInstitucional; // obrigatorio | CREATE
        this.telefoneWhatsapp = telefoneWhatsapp; // obrigatorio | CREATE
        this.cep = cep; // obrigatorio
        this.endereco = endereco; // obrigatorio | CREATE
        this.cidade = cidade; // obrigatorio | CREATE
        this.estado = estado; // obrigatorio | CREATE
        this.dataFundacao = dataFundacao; // opcional
        this.canalContatoPreferencial = canalContatoPreferencial; // obrigatorio (c/default)
        this.linkWhatsapp = linkWhatsapp; // opcional
        this.status = status; // obrigatorio (c/default)
        this.dataCadastro = dataCadastro; // obrigatorio (c/default)
    }

    // Exibição do READ todo: substituir por setters nos construtores
    public Cooperativa(String nome, String cnpj, String cidade, String estado, String status, int qtdCooperados, LocalDateTime dataCadastro) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.cidade = cidade;
        this.estado = estado;
        this.status = status;
        this.qtdCooperados = qtdCooperados;
        this.dataCadastro = dataCadastro;
    }

    public int getQtdCooperados() {
        return qtdCooperados;
    }

    public void setQtdCooperados(int qtdCooperados) {
        this.qtdCooperados = qtdCooperados;
    }

    // GETTERS E SETTERS

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }

    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getNomePublico() { return nomePublico; }

    public void setNomePublico(String nomePublico) { this.nomePublico = nomePublico; }

    public String getDescricaoInstitucional() { return descricaoInstitucional; }

    public void setDescricaoInstitucional(String descricaoInstitucional) { this.descricaoInstitucional = descricaoInstitucional; }

    public String getLogoUrl() { return logoUrl; }

    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getEmailInstitucional() { return emailInstitucional; }

    public void setEmailInstitucional(String emailInstitucional) { this.emailInstitucional = emailInstitucional; }

    public String getTelefoneWhatsapp() { return telefoneWhatsapp; }

    public void setTelefoneWhatsapp(String telefoneWhatsapp) { this.telefoneWhatsapp = telefoneWhatsapp; }

    public String getCep() { return cep; }

    public void setCep(String cep) { this.cep = cep; }

    public String getEndereco() { return endereco; }

    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCidade() { return cidade; }

    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getDataFundacao() { return dataFundacao; }

    public void setDataFundacao(LocalDate dataFundacao) { this.dataFundacao = dataFundacao; }

    public String getCanalContatoPreferencial() { return canalContatoPreferencial; }

    public void setCanalContatoPreferencial(String canalContatoPreferencial) { this.canalContatoPreferencial = canalContatoPreferencial; }

    public String getLinkWhatsapp() { return linkWhatsapp; }

    public void setLinkWhatsapp(String linkWhatsapp) { this.linkWhatsapp = linkWhatsapp; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

}
