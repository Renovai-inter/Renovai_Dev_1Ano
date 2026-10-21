
package model.principais;

import model.enums.StatusCooperativa;
import util.Utilitario;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Cooperativa {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS (tabela cooperativa)

    private Long idCooperativa; // PK
    private String nome; // NOT NULL - VARCHAR(150)
    private String cnpj; // NOT NULL - ENTRADA VALIDADA COM METODO
    private String nomePublico; // NOT NULL - VARCHAR(150)
    private String descricaoInstitucional; // TEXT
    private String logoUrl; // VARCHAR(500)
    private String emailInstitucional; // NOT NULL - ENTRADA VALIDADA COM METODO
    private String telefoneWhatsapp; // NOT NULL - ENTRADA VALIDADA COM METODO
    private String cep; // NOT NULL - ENTRADA VALIDADA COM METODO
    private String endereco; // NOT NULL - ENTRADA VALIDADA COM METODO
    private String cidade; // NOT NULL - VARCHAR(100)
    private String estado; // NOT NULL - ENTRADA VALIDADA COM METODO
    private LocalDate dataFundacao; // DATE
    private String canalContatoPreferencial; // NOT NULL - VARCHAR(10) COM DEFAULT
    private String linkWhatsapp; // VARCHAR(255)
    private StatusCooperativa status; // NOT NULL - CHECK COM DEFAULT
    private LocalDateTime dataCadastro; // NOT NULL - TIMESTAMP COM DEFAULT

    // GETTERS E SETTERS

    // Retorna o valor de idCooperativa.
    public Long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    // Retorna o valor de nome.
    public String getNome() { return nome; }

    // Define o valor de nome.
    public void setNome(String nome) {
        nome = utilitario.normalizarTextoLivre(nome);
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome é obrigatório.");
        }
        if (nome.codePointCount(0, nome.length()) > 150) {
            throw new IllegalArgumentException("nome deve ter no máximo 150 caracteres.");
        }
        this.nome = nome;
    }

    // Retorna o valor de cnpj.
    public String getCnpj() { return utilitario.formatarCnpj(cnpj); }

    // Define o valor de cnpj.
    public void setCnpj(String cnpj) {
        cnpj = utilitario.filtrarCnpj(cnpj);
        if (cnpj != null && cnpj.codePointCount(0, cnpj.length()) > 18) {
            throw new IllegalArgumentException("cnpj deve ter no máximo 18 caracteres.");
        }
        this.cnpj = cnpj;
    }

    // Retorna o valor de nomePublico.
    public String getNomePublico() { return nomePublico; }

    // Define o valor de nomePublico.
    public void setNomePublico(String nomePublico) {
        nomePublico = utilitario.normalizarTextoLivre(nomePublico);
        if (nomePublico == null || nomePublico.isBlank()) {
            throw new IllegalArgumentException("nomePublico é obrigatório.");
        }
        if (nomePublico.codePointCount(0, nomePublico.length()) > 150) {
            throw new IllegalArgumentException("nomePublico deve ter no máximo 150 caracteres.");
        }
        this.nomePublico = nomePublico;
    }

    // Retorna o valor de descricaoInstitucional.
    public String getDescricaoInstitucional() { return descricaoInstitucional; }

    // Define o valor de descricaoInstitucional.
    public void setDescricaoInstitucional(String descricaoInstitucional) {
        this.descricaoInstitucional = utilitario.normalizarTextoLivre(descricaoInstitucional);
    }

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
        emailInstitucional = utilitario.filtrarEmailInstitucional(emailInstitucional);
        if (emailInstitucional != null && emailInstitucional.codePointCount(0, emailInstitucional.length()) > 150) {
            throw new IllegalArgumentException("emailInstitucional deve ter no máximo 150 caracteres.");
        }
        this.emailInstitucional = emailInstitucional;
    }

    // Retorna o valor de telefoneWhatsapp.
    public String getTelefoneWhatsapp() { return utilitario.formatarTelefoneWhatsapp(telefoneWhatsapp); }

    // Define o valor de telefoneWhatsapp.
    public void setTelefoneWhatsapp(String telefoneWhatsapp) {
        telefoneWhatsapp = utilitario.filtrarTelefoneWhatsapp(telefoneWhatsapp);
        if (telefoneWhatsapp != null && telefoneWhatsapp.codePointCount(0, telefoneWhatsapp.length()) > 20) {
            throw new IllegalArgumentException("telefoneWhatsapp deve ter no máximo 20 caracteres.");
        }
        this.telefoneWhatsapp = telefoneWhatsapp;
    }

    // Retorna o valor de cep.
    public String getCep() { return utilitario.formatarCEP(cep); }

    // Define o valor de cep.
    public void setCep(String cep) {
        cep = utilitario.filtrarCep(cep);
        if (cep != null && cep.codePointCount(0, cep.length()) > 9) {
            throw new IllegalArgumentException("cep deve ter no máximo 9 caracteres.");
        }
        this.cep = cep;
    }

    // Retorna o valor de endereco.
    public String getEndereco() { return endereco; }

    // Define o valor de endereco.
    public void setEndereco(String endereco) {
        endereco = utilitario.normalizarTextoLivre(endereco);
        if (endereco == null || endereco.isBlank()) {
            throw new IllegalArgumentException("endereco é obrigatório.");
        }
        if (endereco.codePointCount(0, endereco.length()) > 255) {
            throw new IllegalArgumentException("endereco deve ter no máximo 255 caracteres.");
        }
        this.endereco = endereco;
    }

    // Retorna o valor de cidade.
    public String getCidade() { return cidade; }

    // Define o valor de cidade.
    public void setCidade(String cidade) {
        cidade = utilitario.normalizarTextoLivre(cidade);
        if (cidade == null || cidade.isBlank()) {
            throw new IllegalArgumentException("cidade é obrigatória.");
        }
        if (cidade.codePointCount(0, cidade.length()) > 100) {
            throw new IllegalArgumentException("cidade deve ter no máximo 100 caracteres.");
        }
        this.cidade = cidade;
    }

    // Retorna o valor de estado.
    public String getEstado() { return estado; }

    // Define o valor de estado.
    public void setEstado(String estado) { this.estado = utilitario.validarUf(estado); }

    // Retorna o valor de dataFundacao.
    public LocalDate getDataFundacao() { return dataFundacao; }

    // Define o valor de dataFundacao.
    public void setDataFundacao(LocalDate dataFundacao) { this.dataFundacao = dataFundacao; }

    // Retorna o valor de canalContatoPreferencial.
    public String getCanalContatoPreferencial() { return canalContatoPreferencial; }

    // Define o valor de canalContatoPreferencial.
    public void setCanalContatoPreferencial(String canalContatoPreferencial) {
        canalContatoPreferencial = utilitario.normalizarTextoLivre(canalContatoPreferencial);
        if (canalContatoPreferencial == null) {
            throw new IllegalArgumentException("canalContatoPreferencial é obrigatório.");
        }
        if (!canalContatoPreferencial.equals("WHATSAPP")
                && !canalContatoPreferencial.equals("TELEFONE")
                && !canalContatoPreferencial.equals("EMAIL")) {
            throw new IllegalArgumentException("canalContatoPreferencial inválido.");
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
    public StatusCooperativa getStatus() { return status; }

    // Define o valor de status.
    public void setStatus(StatusCooperativa status) {
        if (status == null) {
            throw new IllegalArgumentException("status é obrigatório.");
        }
        this.status = status;
    }

    // Retorna o valor de dataCadastro.
    public LocalDateTime getDataCadastro() { return dataCadastro; }

    // Define o valor de dataCadastro.
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

}
