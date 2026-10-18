
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
    private int qtdRotas;
    private int qtdColetas;
    private int qtdNegociacoes;

    // CONSTRUTORES

    // Constrói a cooperativa com seus dados cadastrais completos.
    public Cooperativa(Long idCooperativa, String nome, String cnpj, String nomePublico, String descricaoInstitucional, String logoUrl, String emailInstitucional, String telefoneWhatsapp, String cep, String endereco, String cidade, String estado, LocalDate dataFundacao, String canalContatoPreferencial, String linkWhatsapp, String status, LocalDateTime dataCadastro) {
        this.idCooperativa = idCooperativa; // pk - obrigatorio
        setNome(nome); // obrigatorio | CREATE

        if (cnpj == null || cnpj.isEmpty()) {
            setCnpj("");
        } else {
            setCnpj(cnpj); // opcional | CREATE
        }

        if (nomePublico == null || nomePublico.isEmpty()) {
            setNomePublico(nome);
        } else {
            setNomePublico(nomePublico); // obrigatorio
        }

        this.descricaoInstitucional = descricaoInstitucional; // opcional | CREATE
        setLogoUrl(logoUrl); // opcional | CREATE
        setEmailInstitucional(emailInstitucional); // obrigatorio | CREATE
        setTelefoneWhatsapp(telefoneWhatsapp); // obrigatorio | CREATE
        setCep(cep); // obrigatorio
        setEndereco(endereco); // obrigatorio | CREATE
        setCidade(cidade); // obrigatorio | CREATE
        this.estado = estado; // obrigatorio | CREATE
        this.dataFundacao = dataFundacao; // opcional
        setCanalContatoPreferencial(canalContatoPreferencial); // obrigatorio (c/default)
        setLinkWhatsapp(linkWhatsapp); // opcional
        setStatus(status); // obrigatorio (c/default)
        this.dataCadastro = dataCadastro; // obrigatorio (c/default)
    }

    // Constrói um resumo da cooperativa para a listagem.
    public Cooperativa(String nome, String cnpj, String cidade, String estado, String status, int qtdCooperados, LocalDateTime dataCadastro) {
        setNome(nome);

        if (cnpj == null || cnpj.isEmpty()) {
            setCnpj("");
        } else {
            setCnpj(cnpj);
        }

        setCidade(cidade);
        this.estado = estado;
        setStatus(status);
        this.qtdCooperados = qtdCooperados;
        this.dataCadastro = dataCadastro;
    }

    // Constrói os dados completos e as contagens da cooperativa.
    public Cooperativa(Long idCooperativa, String nome, String cnpj, String nomePublico, String descricaoInstitucional, String logoUrl, String emailInstitucional, String telefoneWhatsapp, String cep, String endereco, String cidade, String estado, LocalDate dataFundacao, String canalContatoPreferencial, String linkWhatsapp, String status, LocalDateTime dataCadastro, int qtdCooperados, int qtdRotas, int qtdColetas, int qtdNegociacoes) {
        setNome(nome);

        if (cnpj == null || cnpj.isEmpty()) {
            setCnpj("");
        } else {
            setCnpj(cnpj);
        }

        if (nomePublico == null || nomePublico.isEmpty()) {
            setNomePublico(nome);
        } else {
            setNomePublico(nomePublico);
        }

        this.descricaoInstitucional = descricaoInstitucional;
        setLogoUrl(logoUrl);
        setEmailInstitucional(emailInstitucional);
        setTelefoneWhatsapp(telefoneWhatsapp);
        setCep(cep);
        setEndereco(endereco);
        setCidade(cidade);
        this.estado = estado;
        this.dataFundacao = dataFundacao;
        setCanalContatoPreferencial(canalContatoPreferencial);
        setLinkWhatsapp(linkWhatsapp);
        setStatus(status);
        this.dataCadastro = dataCadastro;

        this.qtdCooperados = qtdCooperados;
        this.qtdRotas = qtdRotas;
        this.qtdColetas = qtdColetas;
        this.qtdNegociacoes = qtdNegociacoes;
    }

    // Retorna o valor de qtdCooperados.
    public int getQtdCooperados() {
        return qtdCooperados;
    }

    // Define o valor de qtdCooperados.
    public void setQtdCooperados(int qtdCooperados) {
        this.qtdCooperados = qtdCooperados;
    }

    // GETTERS E SETTERS

    // Retorna o valor de idCooperativa.
    public Long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    // Retorna o valor de nome.
    public String getNome() { return nome; }

    // Define o valor de nome.
    public void setNome(String nome) {
        if (nome != null && nome.codePointCount(0, nome.length()) > 150) {
            throw new IllegalArgumentException("nome deve ter no máximo 150 caracteres.");
        }
        this.nome = nome;
    }

    // Retorna o valor de cnpj.
    public String getCnpj() { return cnpj; }

    // Define o valor de cnpj.
    public void setCnpj(String cnpj) {
        if (cnpj != null && cnpj.codePointCount(0, cnpj.length()) > 18) {
            throw new IllegalArgumentException("cnpj deve ter no máximo 18 caracteres.");
        }
        this.cnpj = cnpj;
    }

    // Retorna o valor de nomePublico.
    public String getNomePublico() { return nomePublico; }

    // Define o valor de nomePublico.
    public void setNomePublico(String nomePublico) {
        if (nomePublico != null && nomePublico.codePointCount(0, nomePublico.length()) > 150) {
            throw new IllegalArgumentException("nomePublico deve ter no máximo 150 caracteres.");
        }
        this.nomePublico = nomePublico;
    }

    // Retorna o valor de descricaoInstitucional.
    public String getDescricaoInstitucional() { return descricaoInstitucional; }

    // Define o valor de descricaoInstitucional.
    public void setDescricaoInstitucional(String descricaoInstitucional) { this.descricaoInstitucional = descricaoInstitucional; }

    // Retorna o valor de logoUrl.
    public String getLogoUrl() { return logoUrl; }

    // Define o valor de logoUrl.
    public void setLogoUrl(String logoUrl) {
        if (logoUrl != null && logoUrl.codePointCount(0, logoUrl.length()) > 500) {
            throw new IllegalArgumentException("logoUrl deve ter no máximo 500 caracteres.");
        }
        this.logoUrl = logoUrl;
    }

    // Retorna o valor de emailInstitucional.
    public String getEmailInstitucional() { return emailInstitucional; }

    // Define o valor de emailInstitucional.
    public void setEmailInstitucional(String emailInstitucional) {
        if (emailInstitucional != null && emailInstitucional.codePointCount(0, emailInstitucional.length()) > 150) {
            throw new IllegalArgumentException("emailInstitucional deve ter no máximo 150 caracteres.");
        }
        this.emailInstitucional = emailInstitucional;
    }

    // Retorna o valor de telefoneWhatsapp.
    public String getTelefoneWhatsapp() { return telefoneWhatsapp; }

    // Define o valor de telefoneWhatsapp.
    public void setTelefoneWhatsapp(String telefoneWhatsapp) {
        if (telefoneWhatsapp != null && telefoneWhatsapp.codePointCount(0, telefoneWhatsapp.length()) > 20) {
            throw new IllegalArgumentException("telefoneWhatsapp deve ter no máximo 20 caracteres.");
        }
        this.telefoneWhatsapp = telefoneWhatsapp;
    }

    // Retorna o valor de cep.
    public String getCep() { return cep; }

    // Define o valor de cep.
    public void setCep(String cep) {
        if (cep != null && cep.codePointCount(0, cep.length()) > 9) {
            throw new IllegalArgumentException("cep deve ter no máximo 9 caracteres.");
        }
        this.cep = cep;
    }

    // Retorna o valor de endereco.
    public String getEndereco() { return endereco; }

    // Define o valor de endereco.
    public void setEndereco(String endereco) {
        if (endereco != null && endereco.codePointCount(0, endereco.length()) > 255) {
            throw new IllegalArgumentException("endereco deve ter no máximo 255 caracteres.");
        }
        this.endereco = endereco;
    }

    // Retorna o valor de cidade.
    public String getCidade() { return cidade; }

    // Define o valor de cidade.
    public void setCidade(String cidade) {
        if (cidade != null && cidade.codePointCount(0, cidade.length()) > 100) {
            throw new IllegalArgumentException("cidade deve ter no máximo 100 caracteres.");
        }
        this.cidade = cidade;
    }

    // Retorna o valor de estado.
    public String getEstado() { return estado; }

    // Define o valor de estado.
    public void setEstado(String estado) { this.estado = estado; }

    // Retorna o valor de dataFundacao.
    public LocalDate getDataFundacao() { return dataFundacao; }

    // Define o valor de dataFundacao.
    public void setDataFundacao(LocalDate dataFundacao) { this.dataFundacao = dataFundacao; }

    // Retorna o valor de canalContatoPreferencial.
    public String getCanalContatoPreferencial() { return canalContatoPreferencial; }

    // Define o valor de canalContatoPreferencial.
    public void setCanalContatoPreferencial(String canalContatoPreferencial) {
        if (canalContatoPreferencial != null && canalContatoPreferencial.codePointCount(0, canalContatoPreferencial.length()) > 10) {
            throw new IllegalArgumentException("canalContatoPreferencial deve ter no máximo 10 caracteres.");
        }
        this.canalContatoPreferencial = canalContatoPreferencial;
    }

    // Retorna o valor de linkWhatsapp.
    public String getLinkWhatsapp() { return linkWhatsapp; }

    // Define o valor de linkWhatsapp.
    public void setLinkWhatsapp(String linkWhatsapp) {
        if (linkWhatsapp != null && linkWhatsapp.codePointCount(0, linkWhatsapp.length()) > 255) {
            throw new IllegalArgumentException("linkWhatsapp deve ter no máximo 255 caracteres.");
        }
        this.linkWhatsapp = linkWhatsapp;
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

    // Retorna o valor de qtdRotas.
    public int getQtdRotas() {
        return qtdRotas;
    }

    // Define o valor de qtdRotas.
    public void setQtdRotas(int qtdRotas) {
        this.qtdRotas = qtdRotas;
    }

    // Retorna o valor de qtdColetas.
    public int getQtdColetas() {
        return qtdColetas;
    }

    // Define o valor de qtdColetas.
    public void setQtdColetas(int qtdColetas) {
        this.qtdColetas = qtdColetas;
    }

    // Retorna o valor de qtdNegociacoes.
    public int getQtdNegociacoes() {
        return qtdNegociacoes;
    }

    // Define o valor de qtdNegociacoes.
    public void setQtdNegociacoes(int qtdNegociacoes) {
        this.qtdNegociacoes = qtdNegociacoes;
    }
}
