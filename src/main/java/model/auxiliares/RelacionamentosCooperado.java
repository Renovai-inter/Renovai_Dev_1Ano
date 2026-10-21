package model.auxiliares;

import util.Utilitario;

import java.util.ArrayList;

public class RelacionamentosCooperado {

    private final Utilitario util = new Utilitario();

    // ATRIBUTOS

    private long idCooperativa; // NOT NULL - VARCHAR(150)
    private ArrayList<String> atividades = new ArrayList<>();

    // GETTERS E SETTERS

    // Retorna o valor de idCooperativa.
    public long getIdCooperativa() { return idCooperativa; }

    // Define o valor de idCooperativa.
    public void setIdCooperativa(String nomeCooperativa) { this.idCooperativa = idCooperativa; }

    // Retorna o valor de atividades.
    public ArrayList<String> getAtividades() { return atividades; }

    // Define o valor de atividades.
    public void setAtividades(ArrayList<String> atividades) { this.atividades = atividades; }

}
