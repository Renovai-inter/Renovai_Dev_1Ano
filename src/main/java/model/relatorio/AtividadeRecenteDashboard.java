package model.relatorio;

public class AtividadeRecenteDashboard {

    private final String horario;
    private final String destaque;
    private final String texto;

    public AtividadeRecenteDashboard(String horario, String destaque, String texto) {
        this.horario = horario;
        this.destaque = destaque;
        this.texto = texto;
    }

    public String getHorario() { return horario; }

    public String getDestaque() { return destaque; }

    public String getTexto() { return texto; }
}