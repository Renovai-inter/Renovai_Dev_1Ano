
package model;

import java.time.LocalDateTime;

public class EmpresaRecicladora {
    private Long idEmpresa;
    private String nome;
    private String cnpj;
    private String segmentoAtuacao;
    private String segmentoOutro;
    private String emailInstitucional;
    private String telefoneWhatsapp;
    private String cep;
    private String endereco;
    private String cidade;
    private String estado;
    private LocalDateTime dataCadastro;


    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getCnpj() { return cnpj; }

    public void setCnpj(String cnpj) { this.cnpj = cnpj; }

    public String getSegmentoAtuacao() { return segmentoAtuacao; }

    public void setSegmentoAtuacao(String segmentoAtuacao) { this.segmentoAtuacao = segmentoAtuacao; }

    public String getSegmentoOutro() { return segmentoOutro; }

    public void setSegmentoOutro(String segmentoOutro) { this.segmentoOutro = segmentoOutro; }

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

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
}
