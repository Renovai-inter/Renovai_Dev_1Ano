
package controller;

import dao.ColetaDAO;
import model.Coleta;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/coleta")
public class ColetaServlet extends HttpServlet {

    private final ColetaDAO coletaDAO = new ColetaDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        try {
            if ("detalhes".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                Coleta coleta = coletaDAO.buscarPorId(id);

                if (coleta == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "Coleta não encontrada.");
                    return;
                }

                request.setAttribute("coleta", coleta);
                request.setAttribute("materiais",
                        coletaDAO.listarMateriaisDaColeta(id));

                request.getRequestDispatcher("/detalhes-coleta.jsp")
                        .forward(request, response);

            } else {
                request.setAttribute("coletas", coletaDAO.listarColetas());
                request.getRequestDispatcher("/coletas.jsp")
                        .forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "ID inválido.");
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao consultar coletas.", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");

        try {
            if ("cadastrar".equals(acao)) {
                cadastrar(request);
                response.sendRedirect(request.getContextPath()
                        + "/coleta?sucesso=cadastro");

            } else if ("iniciar".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                if (!coletaDAO.atualizarStatus(id, "EM_ANDAMENTO")) {
                    response.sendError(HttpServletResponse.SC_CONFLICT,
                            "Não foi possível iniciar esta coleta.");
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/coleta?acao=detalhes&id=" + id);

            } else if ("finalizar".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                if (!coletaDAO.atualizarStatus(id, "CONCLUIDA")) {
                    response.sendError(HttpServletResponse.SC_CONFLICT,
                            "Não foi possível finalizar esta coleta.");
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/coleta?acao=detalhes&id=" + id);

            } else if ("cancelar".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                if (!coletaDAO.atualizarStatus(id, "CANCELADA")) {
                    response.sendError(HttpServletResponse.SC_CONFLICT,
                            "Não foi possível cancelar esta coleta.");
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/coleta");

            } else if ("excluir".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                String confirmacao = request.getParameter("confirmacao");

                if (!String.valueOf(id).equals(confirmacao)) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Digite o código correto da coleta.");
                    return;
                }

                if (!coletaDAO.excluirColeta(id)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,
                            "Coleta não encontrada.");
                    return;
                }

                response.sendRedirect(request.getContextPath()
                        + "/coleta?sucesso=exclusao");

            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Ação inválida.");
            }

        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Confira os campos numéricos e a data.");
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao processar a coleta.", e);
        }
    }

    private void cadastrar(HttpServletRequest request) {
        Coleta coleta = new Coleta();

        coleta.setIdCooperativa(
                Long.parseLong(request.getParameter("idCooperativa")));
        coleta.setTipo(request.getParameter("tipo"));
        coleta.setStatus("AGENDADA");
        coleta.setObservacoes(request.getParameter("observacoes"));

        String data = request.getParameter("dataAgendada");
        if (data != null && !data.isBlank()) {
            coleta.setDataAgendada(
                    java.sql.Date.valueOf(LocalDate.parse(data)));
        }

        String rota = request.getParameter("idRota");
        coleta.setIdRota(rota == null || rota.isBlank()
                ? null : Long.parseLong(rota));

        String endereco = request.getParameter("idEnderecoRota");
        coleta.setIdEnderecoRota(endereco == null || endereco.isBlank()
                ? null : Long.parseLong(endereco));

        String responsavel = request.getParameter("idCooperadoResponsavel");
        coleta.setIdCooperadoResponsavel(
                responsavel == null || responsavel.isBlank()
                        ? null : Long.parseLong(responsavel));

        if ("COLETA_EXTERNA".equals(coleta.getTipo())) {
            coleta.setOrigemEntrega(null);
            coleta.setNomeLocalOrigem(null);
        } else if ("ENTREGA_COOPERATIVA".equals(coleta.getTipo())) {
            coleta.setIdRota(null);
            coleta.setIdEnderecoRota(null);
            coleta.setOrigemEntrega(request.getParameter("origemEntrega"));
            coleta.setNomeLocalOrigem(
                    request.getParameter("nomeLocalOrigem"));
        } else {
            throw new IllegalArgumentException("Tipo de coleta inválido.");
        }

        String[] idsMateriais = request.getParameterValues("idMaterial");
        String[] quantidades = request.getParameterValues("quantidadeKg");

        Map<Long, BigDecimal> materiais = new LinkedHashMap<>();

        if (idsMateriais != null && quantidades != null) {
            if (idsMateriais.length != quantidades.length) {
                throw new IllegalArgumentException(
                        "Confira os materiais e suas quantidades.");
            }

            for (int i = 0; i < idsMateriais.length; i++) {
                if (idsMateriais[i].isBlank() || quantidades[i].isBlank()) {
                    continue;
                }

                Long idMaterial = Long.parseLong(idsMateriais[i]);
                BigDecimal quantidade = new BigDecimal(
                        quantidades[i].replace(",", "."));

                if (quantidade.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException(
                            "A quantidade não pode ser negativa.");
                }

                if (materiais.put(idMaterial, quantidade) != null) {
                    throw new IllegalArgumentException(
                            "Não repita o mesmo material no cadastro.");
                }
            }
        }

        coletaDAO.cadastrarColeta(coleta, materiais);
    }
}