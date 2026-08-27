
package model;

public class EnderecoRota {
    private Long idEndereco;
    private Long idRota;
    private String nomeLocal;
    private String tipoLocal;
    private String enderecoCompleto;
    private String nomeResponsavel;
    private String telefoneContato;
    private String observacoes;
    private Integer ordem;


    public Long getIdEndereco() { return idEndereco; }

    public void setIdEndereco(Long idEndereco) { this.idEndereco = idEndereco; }

    public Long getIdRota() { return idRota; }

    public void setIdRota(Long idRota) { this.idRota = idRota; }

    public String getNomeLocal() { return nomeLocal; }

    public void setNomeLocal(String nomeLocal) { this.nomeLocal = nomeLocal; }

    public String getTipoLocal() { return tipoLocal; }

    public void setTipoLocal(String tipoLocal) { this.tipoLocal = tipoLocal; }

    public String getEnderecoCompleto() { return enderecoCompleto; }

    public void setEnderecoCompleto(String enderecoCompleto) { this.enderecoCompleto = enderecoCompleto; }

    public String getNomeResponsavel() { return nomeResponsavel; }

    public void setNomeResponsavel(String nomeResponsavel) { this.nomeResponsavel = nomeResponsavel; }

    public String getTelefoneContato() { return telefoneContato; }

    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }

    public String getObservacoes() { return observacoes; }

    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Integer getOrdem() { return ordem; }

    public void setOrdem(Integer ordem) { this.ordem = ordem; }
}
