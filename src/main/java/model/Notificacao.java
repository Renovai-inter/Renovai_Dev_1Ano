
package model;

import java.time.LocalDateTime;

public class Notificacao {

    // ATRIBUTOS

    private Long idNotificacao;
    private String destinatarioTipo;
    private Long idCooperativa;
    private Long idEmpresa;
    private String categoria;
    private String titulo;
    private String descricao;
    private LocalDateTime dataHora;
    private String status;
    private String referenciaTipo;
    private Long referenciaId;

    // GETTERS E SETTERS

    public Long getIdNotificacao() { return idNotificacao; }

    public void setIdNotificacao(Long idNotificacao) { this.idNotificacao = idNotificacao; }

    public String getDestinatarioTipo() { return destinatarioTipo; }

    public void setDestinatarioTipo(String destinatarioTipo) { this.destinatarioTipo = destinatarioTipo; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(Long idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getCategoria() { return categoria; }

    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }

    public void setDescricao(String descricao) { this.descricao = descricao; }

    public LocalDateTime getDataHora() { return dataHora; }

    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getReferenciaTipo() { return referenciaTipo; }

    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }

    public Long getReferenciaId() { return referenciaId; }

    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

}
