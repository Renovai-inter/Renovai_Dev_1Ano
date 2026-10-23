
package model.principais;

import model.auxiliares.RelacionamentosColeta;
import model.enums.StatusColeta;
import model.enums.TipoColeta;
import model.enums.OrigemEntrega;
import util.Utilitario;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class Coleta {

    private final Utilitario utilitario = new Utilitario();

    // ATRIBUTOS

    private Long idColeta; // PK
    private final RelacionamentosColeta relacionamentos = new RelacionamentosColeta();
    private TipoColeta tipo; // NOT NULL
    private StatusColeta status; // NOT NULL com DEFAULT
    private String origemEntrega; // VARCHAR(15)
    private String nomeLocalOrigem; // VARCHAR(150)
    private LocalDate dataAgendada; // DATE
    private LocalDateTime dataInicio; // TIMESTAMP
    private LocalDateTime dataFim; // TIMESTAMP
    private BigDecimal pesoTotalKg; // NUMERIC(12,2)
    private String observacoes; // TEXT

    public Coleta() {
    }

    public Coleta(Long idCooperativa, TipoColeta tipo, StatusColeta status,
                  Long idRota, Long idEnderecoRota, Long idCooperadoResponsavel,
                  String origemEntrega, String nomeLocalOrigem, LocalDate dataAgendada,
                  LocalDateTime dataInicio, LocalDateTime dataFim, BigDecimal pesoTotalKg,
                  String observacoes) {
        getRelacionamentos().setIdCooperativa(idCooperativa);
        setTipo(tipo);
        setStatus(status);
        getRelacionamentos().setIdRota(idRota);
        getRelacionamentos().setIdEnderecoRota(idEnderecoRota);
        getRelacionamentos().setIdCooperadoResponsavel(idCooperadoResponsavel);
        setOrigemEntrega(origemEntrega);
        setNomeLocalOrigem(nomeLocalOrigem);
        setDataAgendada(dataAgendada);
        setDataInicio(dataInicio);
        setDataFim(dataFim);
        setPesoTotalKg(pesoTotalKg);
        setObservacoes(observacoes);
        validarTipoEVinculos();
    }

    // GETTERS E SETTERS

    // Retorna o valor de idColeta.
    public Long getIdColeta() { return idColeta; }

    // Define o valor de idColeta.
    public void setIdColeta(Long idColeta) { this.idColeta = idColeta; }

    // Reúne as chaves e os materiais associados à coleta.
    public RelacionamentosColeta getRelacionamentos() { return relacionamentos; }

    // Retorna o valor de tipo.
    public TipoColeta getTipo() { return tipo; }

    // Define o valor de tipo.
    public void setTipo(TipoColeta tipo) {
        this.tipo = tipo;
    }

    // Retorna o valor de status.
    public StatusColeta getStatus() { return status; }

    // Define o valor de status.
    public void setStatus(StatusColeta status) {
        this.status = status;
    }

    // Retorna o valor de origemEntrega.
    public String getOrigemEntrega() { return origemEntrega; }

    // Define o valor de origemEntrega.
    public void setOrigemEntrega(String origemEntrega) {
        origemEntrega = utilitario.normalizarTextoLivre(origemEntrega);
        if (origemEntrega != null) {
            OrigemEntrega.valueOf(origemEntrega);
        }
        this.origemEntrega = origemEntrega;
    }

    // Confere a combinação entre tipo, rota e origem prevista para a coleta.
    public void validarTipoEVinculos() {
        if (tipo == null) {
            throw new IllegalArgumentException("tipo é obrigatório.");
        }
        if (tipo == TipoColeta.ENTREGA_COOPERATIVA) {
            if (relacionamentos.getIdRota() != null || relacionamentos.getIdEnderecoRota() != null) {
                throw new IllegalArgumentException("Entrega na cooperativa não usa rota nem endereço da rota.");
            }
        } else {
            if (origemEntrega != null) {
                throw new IllegalArgumentException("Coleta externa não usa origem de entrega.");
            }
        }
    }

    // Retorna o valor de nomeLocalOrigem.
    public String getNomeLocalOrigem() { return nomeLocalOrigem; }

    // Define o valor de nomeLocalOrigem.
    public void setNomeLocalOrigem(String nomeLocalOrigem) {
        nomeLocalOrigem = utilitario.normalizarTextoLivre(nomeLocalOrigem);
        if (nomeLocalOrigem != null && nomeLocalOrigem.length() > 150) {
            throw new IllegalArgumentException("nomeLocalOrigem deve ter no máximo 150 caracteres.");
        }
        this.nomeLocalOrigem = nomeLocalOrigem;
    }

    // Retorna o valor de dataAgendada.
    public LocalDate getDataAgendada() { return dataAgendada; }

    // Define o valor de dataAgendada.
    public void setDataAgendada(LocalDate dataAgendada) { this.dataAgendada = dataAgendada; }

    // Retorna o valor de dataInicio.
    public LocalDateTime getDataInicio() { return dataInicio; }

    // Define o valor de dataInicio.
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    // Retorna o valor de dataFim.
    public LocalDateTime getDataFim() { return dataFim; }

    // Define o valor de dataFim.
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    // Retorna o valor de pesoTotalKg.
    public BigDecimal getPesoTotalKg() { return pesoTotalKg; }

    // Define o valor de pesoTotalKg.
    public void setPesoTotalKg(BigDecimal pesoTotalKg) {
        if (pesoTotalKg != null) {
            if (pesoTotalKg.signum() < 0) {
                throw new IllegalArgumentException("pesoTotalKg não pode ser negativo.");
            }
            utilitario.validarNumeroDecimal(pesoTotalKg.toPlainString(), 10L, 2);
        }
        this.pesoTotalKg = pesoTotalKg;
    }

    // Retorna o valor de observacoes.
    public String getObservacoes() { return observacoes; }

    // Define o valor de observacoes.
    public void setObservacoes(String observacoes) { this.observacoes = utilitario.normalizarTextoLivre(observacoes); }

}
