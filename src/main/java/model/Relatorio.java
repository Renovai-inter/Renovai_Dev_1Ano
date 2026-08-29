
package model;

import java.time.LocalDateTime;

public class Relatorio {

    // ATRIBUTOS

    private Long idRelatorio;
    private Long idCooperativa;
    private Long idCooperadoGerador;
    private String tipo;
    private String formatoExportacao;
    private LocalDateTime dataGeracao;

    // GETTERS E SETTERS

    public Long getIdRelatorio() { return idRelatorio; }

    public void setIdRelatorio(Long idRelatorio) { this.idRelatorio = idRelatorio; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public Long getIdCooperadoGerador() { return idCooperadoGerador; }

    public void setIdCooperadoGerador(Long idCooperadoGerador) { this.idCooperadoGerador = idCooperadoGerador; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getFormatoExportacao() { return formatoExportacao; }

    public void setFormatoExportacao(String formatoExportacao) { this.formatoExportacao = formatoExportacao; }

    public LocalDateTime getDataGeracao() { return dataGeracao; }

    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }

}
