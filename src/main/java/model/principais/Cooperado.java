package model.principais;

import model.auxiliares.Usuario;
import model.enums.AtividadeCooperado;
import model.enums.CargoCooperado;
import model.enums.StatusCooperado;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;

// Um cooperado possui os dados da própria conta de usuário e do vínculo com a cooperativa.
public class Cooperado extends Usuario {

    private Long idCooperado;
    private Long idCooperativa;
    private String codigoCooperado;
    private CargoCooperado cargo;
    private StatusCooperado status;
    private LocalDateTime dataCadastro;
    private String nomeCooperativa;
    private ArrayList<AtividadeCooperado> atividadesCooperado = new ArrayList<>();
    private ArrayList<Long> permissoes = new ArrayList<>();
    private transient String senhaInicial;

    public Cooperado() {
    }

    public Cooperado(Long idCooperativa, String nomeCompleto, String email,
                     CargoCooperado cargo, ArrayList<AtividadeCooperado> atividades,
                     ArrayList<Long> permissoes) {
        setIdCooperativa(idCooperativa);
        setNomeCompleto(nomeCompleto);
        setEmail(email);
        setCargo(cargo);
        setAtividadesCooperado(atividades);
        setPermissoes(permissoes);
    }

    public Long getIdCooperado() { return idCooperado; }

    public void setIdCooperado(Long idCooperado) { this.idCooperado = idCooperado; }

    public Long getIdCooperativa() { return idCooperativa; }

    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }

    public String getCodigoCooperado() { return codigoCooperado; }

    public void setCodigoCooperado(String codigoCooperado) {
        if (codigoCooperado == null || codigoCooperado.isBlank()) {
            throw new IllegalArgumentException("codigoCooperado é obrigatório.");
        }
        if (codigoCooperado.length() > 20) {
            throw new IllegalArgumentException("codigoCooperado deve ter no máximo 20 caracteres.");
        }
        this.codigoCooperado = codigoCooperado;
    }

    public String getCargo() {
        if (cargo == null) {
            return null;
        }
        String nome = cargo.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
    }

    public CargoCooperado getCargoEnum() { return cargo; }

    public void setCargo(CargoCooperado cargo) { this.cargo = cargo; }

    public String getStatus() {
        if (status == null) {
            return null;
        }
        String nome = status.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(nome.charAt(0)) + nome.substring(1);
    }

    public StatusCooperado getStatusEnum() { return status; }

    public void setStatus(StatusCooperado status) { this.status = status; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getNomeCooperativa() { return nomeCooperativa; }

    public void setNomeCooperativa(String nomeCooperativa) { this.nomeCooperativa = nomeCooperativa; }

    public ArrayList<AtividadeCooperado> getAtividadesCooperado() {
        return new ArrayList<>(atividadesCooperado);
    }

    public void setAtividadesCooperado(ArrayList<AtividadeCooperado> atividadesCooperado) {
        if (atividadesCooperado == null) {
            throw new IllegalArgumentException("atividadesCooperado é obrigatório.");
        }
        this.atividadesCooperado = new ArrayList<>(atividadesCooperado);
    }

    // Entrega os nomes das atividades para os consumidores que exibem texto.
    public ArrayList<String> getAtividades() {
        ArrayList<String> nomes = new ArrayList<>();
        for (AtividadeCooperado atividade : atividadesCooperado) {
            if (atividade == AtividadeCooperado.COLETA_EXTERNA) {
                nomes.add("Coleta externa");
            } else if (atividade == AtividadeCooperado.PESAGEM) {
                nomes.add("Pesagem");
            } else if (atividade == AtividadeCooperado.PRENSAGEM) {
                nomes.add("Prensagem");
            } else {
                nomes.add(atividade.name());
            }
        }
        return nomes;
    }

    public ArrayList<Long> getPermissoes() { return new ArrayList<>(permissoes); }

    public void setPermissoes(ArrayList<Long> permissoes) {
        if (permissoes == null) {
            throw new IllegalArgumentException("permissoes é obrigatório.");
        }
        this.permissoes = new ArrayList<>(permissoes);
    }

    // A senha gerada só acompanha o resultado do cadastro; não é persistida neste campo.
    public String getSenhaInicial() { return senhaInicial; }

    public void setSenhaInicial(String senhaInicial) { this.senhaInicial = senhaInicial; }
}
