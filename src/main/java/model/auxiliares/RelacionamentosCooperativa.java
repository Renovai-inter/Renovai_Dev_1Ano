package model.auxiliares;

public class RelacionamentosCooperativa {

    // ATRIBUTOS

    private int qtdCooperados; // Quantidade de cooperados definida através de JOIN e COUNT
    private int qtdRotas; // Quantidade de rotas definida através de JOIN e COUNT
    private int qtdColetas; // Quantidade de coletas definida através de JOIN e COUNT
    private int qtdNegociacoes; // Quantidade de negociações definida através de JOIN e COUNT

    // GETTERS E SETTERS

    // Retorna o valor de qtdCooperados.
    public int getQtdCooperados() {
        return qtdCooperados;
    }

    // Define o valor de qtdCooperados.
    public void setQtdCooperados(int qtdCooperados) {
        this.qtdCooperados = qtdCooperados;
    }

    // Retorna o valor de qtdRotas.
    public int getQtdRotas() {
        return qtdRotas;
    }

    // Define o valor de qtdRotas.
    public void setQtdRotas(int qtdRotas) {
        this.qtdRotas = qtdRotas;
    }

    // Retorna o valor de qtdColetas.
    public int getQtdColetas() {
        return qtdColetas;
    }

    // Define o valor de qtdColetas.
    public void setQtdColetas(int qtdColetas) {
        this.qtdColetas = qtdColetas;
    }

    // Retorna o valor de qtdNegociacoes.
    public int getQtdNegociacoes() {
        return qtdNegociacoes;
    }

    // Define o valor de qtdNegociacoes.
    public void setQtdNegociacoes(int qtdNegociacoes) {
        this.qtdNegociacoes = qtdNegociacoes;
    }

}
