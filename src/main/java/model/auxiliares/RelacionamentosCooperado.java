package model.auxiliares;

import model.enums.AtividadeCooperado;
import model.enums.PermissaoCooperado;
import java.util.ArrayList;

public class RelacionamentosCooperado {

    // ATRIBUTOS

    private Long idCooperado; // Chave dos vínculos de atividade e permissão
    private String nomeCooperativa; // Nome consultado para a listagem
    private ArrayList<AtividadeCooperado> atividadesCooperado = new ArrayList<>(); // Lista de atividades para Cooperado (ENUM)
    private ArrayList<PermissaoCooperado> permissoesCooperado = new ArrayList<>(); // Lista de permissoes para Cooperado (ENUM)
    private ArrayList<CooperadoPermissao> vinculosPermissao = new ArrayList<>(); // Chaves dos vínculos gravados

    public RelacionamentosCooperado() {
    }

    public RelacionamentosCooperado(Long idCooperado, String nomeCooperativa,
                                   ArrayList<AtividadeCooperado> atividadesCooperado,
                                   ArrayList<PermissaoCooperado> permissoesCooperado,
                                   ArrayList<CooperadoPermissao> vinculosPermissao) {
        setIdCooperado(idCooperado);
        setNomeCooperativa(nomeCooperativa);
        setAtividadesCooperado(atividadesCooperado);
        setPermissoesCooperado(permissoesCooperado);
        setVinculosPermissao(vinculosPermissao);
    }

    // GETTERS E SETTERS

    // Retorna o cooperado associado aos vínculos.
    public Long getIdCooperado() { return idCooperado; }

    // Define o cooperado associado aos vínculos.
    public void setIdCooperado(Long idCooperado) { this.idCooperado = idCooperado; }

    // Retorna o nome da cooperativa quando selecionado por JOIN.
    public String getNomeCooperativa() { return nomeCooperativa; }

    // Define o nome da cooperativa obtido na consulta.
    public void setNomeCooperativa(String nomeCooperativa) { this.nomeCooperativa = nomeCooperativa; }

    public ArrayList<AtividadeCooperado> getAtividadesCooperado() {
        return atividadesCooperado;
    }

    public void setAtividadesCooperado(ArrayList<AtividadeCooperado> atividadesCooperado) {
        this.atividadesCooperado = atividadesCooperado;
    }

    public ArrayList<PermissaoCooperado> getPermissoesCooperado() {
        return permissoesCooperado;
    }

    public void setPermissoesCooperado(ArrayList<PermissaoCooperado> permissoesCooperado) {
        this.permissoesCooperado = permissoesCooperado;
    }

    // Retorna os vínculos identificados pelos IDs do catálogo.
    public ArrayList<CooperadoPermissao> getVinculosPermissao() { return vinculosPermissao; }

    // Define os vínculos identificados pelos IDs do catálogo.
    public void setVinculosPermissao(ArrayList<CooperadoPermissao> vinculosPermissao) {
        if (vinculosPermissao == null) {
            throw new IllegalArgumentException("vinculosPermissao é obrigatório.");
        }
        this.vinculosPermissao = new ArrayList<>(vinculosPermissao);
    }

}
