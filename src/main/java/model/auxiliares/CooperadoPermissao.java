package model.auxiliares;

public class CooperadoPermissao {

    private Long idCooperado;
    private Long idPermissao;

    // Retorna o cooperado do vínculo.
    public Long getIdCooperado() { return idCooperado; }

    // Define o cooperado do vínculo.
    public void setIdCooperado(Long idCooperado) {
        if (idCooperado == null) {
            throw new IllegalArgumentException("idCooperado é obrigatório.");
        }
        this.idCooperado = idCooperado;
    }

    // Retorna a permissão do vínculo.
    public Long getIdPermissao() { return idPermissao; }

    // Define a permissão do vínculo.
    public void setIdPermissao(Long idPermissao) {
        if (idPermissao == null) {
            throw new IllegalArgumentException("idPermissao é obrigatório.");
        }
        this.idPermissao = idPermissao;
    }
}
