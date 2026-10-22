package controller;

import dao.ColetaDAO;
import dao.DashboardDAO;
import model.relatorio.AtividadeRecenteDashboard;
import model.principais.Coleta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final String[] MESES = {
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun",
            "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_DIA_MES = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final ColetaDAO coletaDAO = new ColetaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LocalDateTime agora = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));

        // Nome de quem está logado: guarde "nomeUsuario" na sessão no login
        HttpSession sessao = request.getSession(false);
        Object nome = sessao != null ? sessao.getAttribute("nomeUsuario") : null;

        request.setAttribute("dashboardCarregado", true);
        request.setAttribute("saudacao", saudacao(agora.getHour()));
        request.setAttribute("nomeUsuario", nome);

        // null = não foi possível carregar (a tela mostra "—")
        request.setAttribute("totalCooperados",
                carregar(dashboardDAO::contarCooperadosAtivos, "cooperados ativos"));
        request.setAttribute("totalMateriais",
                carregar(dashboardDAO::contarMateriais, "materiais cadastrados"));
        request.setAttribute("totalCooperativas",
                carregar(dashboardDAO::contarCooperativasAtivas, "cooperativas ativas"));
        request.setAttribute("totalNegociacoes",
                carregar(dashboardDAO::contarNegociacoesEmAndamento, "negociações em andamento"));

        request.setAttribute("distribuicao",
                carregar(dashboardDAO::distribuicaoMateriais, "distribuição dos materiais"));

        Map<YearMonth, Integer> serie = carregar(
                () -> dashboardDAO.negociacoesConcluidasPorMes(6), "negociações por mês");
        Map<String, Integer> serieRotulada = new LinkedHashMap<>();
        if (serie != null) {
            serie.forEach((mes, qtd) -> serieRotulada.put(MESES[mes.getMonthValue() - 1], qtd));
        }
        request.setAttribute("serieNegociacoes", serieRotulada);

        request.setAttribute("atividades", atividadesRecentes(agora.toLocalDate()));

        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }

    private String saudacao(int hora) {
        if (hora < 12) return "Bom dia";
        if (hora < 18) return "Boa tarde";
        return "Boa noite";
    }

    private <T> T carregar(Supplier<T> fonte, String descricao) {
        try {
            return fonte.get();
        } catch (RuntimeException e) {
            getServletContext().log("Dashboard: não foi possível carregar " + descricao, e);
            return null;
        }
    }

    // As 5 últimas coletas cadastradas, descritas pelo status atual
    private List<AtividadeRecenteDashboard> atividadesRecentes(LocalDate hoje) {
        List<AtividadeRecenteDashboard> atividades = new ArrayList<>();

        List<Coleta> coletas = carregar(coletaDAO::listarColetas, "atividades recentes");
        if (coletas == null) return atividades;

        for (int i = 0; i < Math.min(5, coletas.size()); i++) {
            Coleta c = coletas.get(i);
            String destaque = "Coleta CO-" + c.getIdColeta();
            String status = c.getStatus() == null ? "" : c.getStatus().name();

            LocalDateTime quando = null;
            boolean temHora = true;
            String texto;

            switch (status) {
                case "CONCLUIDA":
                    texto = "concluída";
                    quando = c.getDataFim();
                    break;
                case "EM_ANDAMENTO":
                    texto = "iniciada";
                    quando = c.getDataInicio();
                    break;
                case "CANCELADA":
                    texto = "cancelada";
                    temHora = false;
                    quando = c.getDataAgendada() == null ? null : c.getDataAgendada().atStartOfDay();
                    break;
                default:
                    texto = "agendada";
                    temHora = false;
                    if (c.getDataAgendada() != null) {
                        quando = c.getDataAgendada().atStartOfDay();
                        texto += " para " + FORMATO_DATA.format(quando);
                    }
            }

            atividades.add(new AtividadeRecenteDashboard(horario(quando, temHora, hoje), destaque, texto));
        }

        return atividades;
    }

    private String horario(LocalDateTime quando, boolean temHora, LocalDate hoje) {
        if (quando == null) return "—";
        if (temHora && quando.toLocalDate().equals(hoje)) return FORMATO_HORA.format(quando);
        return FORMATO_DIA_MES.format(quando);
    }
}
