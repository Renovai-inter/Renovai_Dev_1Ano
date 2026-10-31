package model.principais;

import model.enums.StatusNegociacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Negociacao {

    // ATRIBUTOS (tabela negociacao)

    private Long idNegociacao;
    private Long idCooperativa;
    private Long idEmpresa;
    private Long idMaterial;
    private BigDecimal quantidadeKg;
    private BigDecimal valorKgAtual;
    private BigDecimal valorTotalAtual;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;

    // ATRIBUTOS (vem de outras tabelas, preenchidos pelos JOINs da DAO)

    private String nomeCooperativa;
    private String cnpjCooperativa;
    private String cidadeCooperativa;
    private String estadoCooperativa;
    private String nomeEmpresa;
    private String cnpjEmpresa;
    private String cidadeEmpresa;
    private String estadoEmpresa;
    private String nomeMaterial;
    private LocalDateTime ultimaAtualizacao;

    // GETTERS E SETTERS (tabela negociacao)

    public Long getIdNegociacao() { return idNegociacao; }

    public void setIdNegociacao(Long idNegociacao) { this.idNegociacao = idNegociacao; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public Long getIdMaterial() { return idMaterial; }

    public void setIdMaterial(Long idMaterial) { this.idMaterial = idMaterial; }

    public BigDecimal getQuantidadeKg() { return quantidadeKg; }

    public void setQuantidadeKg(BigDecimal quantidadeKg) {
        if (quantidadeKg != null && quantidadeKg.signum() <= 0) {
            throw new IllegalArgumentException("quantidadeKg deve ser positiva.");
        }
        this.quantidadeKg = quantidadeKg;
    }

    public BigDecimal getValorKgAtual() { return valorKgAtual; }

    public void setValorKgAtual(BigDecimal valorKgAtual) {
        if (valorKgAtual == null || valorKgAtual.signum() <= 0) {
            throw new IllegalArgumentException("valorKgAtual deve ser positivo.");
        }
        this.valorKgAtual = valorKgAtual;
    }

    public BigDecimal getValorTotalAtual() { return valorTotalAtual; }

    public void setValorTotalAtual(BigDecimal valorTotalAtual) { this.valorTotalAtual = valorTotalAtual; }

    public String getStatus() { return status; }

    public void setStatus(String status) {
        boolean permitido = false;
        for (StatusNegociacao opcao : StatusNegociacao.values()) {
            if (opcao.name().equals(status)) {
                permitido = true;
                break;
            }
        }
        if (!permitido) {
            throw new IllegalArgumentException("status da negociação inválido.");
        }
        this.status = status;
    }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataConclusao() { return dataConclusao; }

    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }

    // GETTERS E SETTERS (dados dos JOINs)

    public String getNomeCooperativa() { return nomeCooperativa; }

    public void setNomeCooperativa(String nomeCooperativa) { this.nomeCooperativa = nomeCooperativa; }

    public String getCnpjCooperativa() { return cnpjCooperativa; }

    public void setCnpjCooperativa(String cnpjCooperativa) { this.cnpjCooperativa = cnpjCooperativa; }

    public String getCidadeCooperativa() { return cidadeCooperativa; }

    public void setCidadeCooperativa(String cidadeCooperativa) { this.cidadeCooperativa = cidadeCooperativa; }

    public String getEstadoCooperativa() { return estadoCooperativa; }

    public void setEstadoCooperativa(String estadoCooperativa) { this.estadoCooperativa = estadoCooperativa; }

    public String getNomeEmpresa() { return nomeEmpresa; }

    public void setNomeEmpresa(String nomeEmpresa) { this.nomeEmpresa = nomeEmpresa; }

    public String getCnpjEmpresa() { return cnpjEmpresa; }

    public void setCnpjEmpresa(String cnpjEmpresa) { this.cnpjEmpresa = cnpjEmpresa; }

    public String getCidadeEmpresa() { return cidadeEmpresa; }

    public void setCidadeEmpresa(String cidadeEmpresa) { this.cidadeEmpresa = cidadeEmpresa; }

    public String getEstadoEmpresa() { return estadoEmpresa; }

    public void setEstadoEmpresa(String estadoEmpresa) { this.estadoEmpresa = estadoEmpresa; }

    public String getNomeMaterial() { return nomeMaterial; }

    public void setNomeMaterial(String nomeMaterial) { this.nomeMaterial = nomeMaterial; }

    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }

    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }

    // METODOS AUXILIARES (calculados, nao existem no banco)

    // Codigo mostrado nas telas: id 143 vira NE-0143
    public String getCodigo() {
        if (idNegociacao == null) {
            return "";
        }
        return "NE-" + String.format("%04d", idNegociacao);
    }

    // Status pronto para exibir: EM_NEGOCIACAO vira "Em negociacao"; textos ja normais ficam como estao
    public String getStatusLegivel() {
        if (status == null) {
            return "";
        }
        if ("EM_NEGOCIACAO".equals(status)) {
            return "Em negociação";
        }
        String texto = status.replace('_', ' ').trim();
        if (!texto.isEmpty() && texto.equals(texto.toUpperCase())) {
            texto = texto.substring(0, 1) + texto.substring(1).toLowerCase();
        }
        return texto;
    }

}
