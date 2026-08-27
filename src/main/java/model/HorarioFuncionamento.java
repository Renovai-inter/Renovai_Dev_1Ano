
package model;

import java.time.LocalTime;

public class HorarioFuncionamento {
    private Long idHorario;
    private Long idCooperativa;
    private String diaSemana;
    private LocalTime horarioAbertura;
    private LocalTime horarioEncerramento;
    private boolean fechado;


    public Long getIdHorario() { return idHorario; }

    public void setIdHorario(Long idHorario) { this.idHorario = idHorario; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public String getDiaSemana() { return diaSemana; }

    public void setDiaSemana(String diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHorarioAbertura() { return horarioAbertura; }

    public void setHorarioAbertura(LocalTime horarioAbertura) { this.horarioAbertura = horarioAbertura; }

    public LocalTime getHorarioEncerramento() { return horarioEncerramento; }

    public void setHorarioEncerramento(LocalTime horarioEncerramento) { this.horarioEncerramento = horarioEncerramento; }

    public boolean isFechado() { return fechado; }

    public void setFechado(boolean fechado) { this.fechado = fechado; }
}
