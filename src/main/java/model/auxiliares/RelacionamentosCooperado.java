package model.auxiliares;

import model.enums.AtividadeCooperado;
import model.enums.PermissaoCooperado;
import util.Utilitario;

import java.util.ArrayList;

public class RelacionamentosCooperado {

    private final Utilitario util = new Utilitario();

    // ATRIBUTOS

    private long idCooperativa; // NOT NULL - VARCHAR(150)
    private ArrayList<AtividadeCooperado> atividadesCooperado = new ArrayList<>(); // Lista de atividades para Cooperado (ENUM)
    private ArrayList<PermissaoCooperado> permissoesCooperado = new ArrayList<>(); // Lista de permissoes para Cooperado (ENUM)

    // GETTERS E SETTERS

    // Retorna o valor de idCooperativa.
    public long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(String nomeCooperativa) { this.idCooperativa = idCooperativa; }

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

}
