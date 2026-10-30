
package controller;

import dao.ColetaDAO;
import model.principais.Coleta;
import model.auxiliares.ColetaMaterial;
import dao.MaterialDAO;
import model.principais.Material;
import model.enums.StatusColeta;
import model.enums.TipoColeta;
import java.util.Objects;
import java.util.function.Supplier;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/coleta")
public class ColetaServlet extends HttpServlet {

    private final ColetaDAO coletaDAO = new ColetaDAO();
    private final MaterialDAO materialDAO = new MaterialDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        Object erro = request.getSession().getAttribute("erroColeta");
        if (erro != null) {
            request.setAttribute("erroColeta", erro);
            request.getSession().removeAttribute("erroColeta");
        }
        String acao = request.getParameter("acao");

        try {
            if ("editar".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                Coleta filtro = new Coleta();
                filtro.setIdColeta(id);
                Coleta coleta = coletaDAO.buscarPorId(filtro);
                if (coleta == null) {
                    mostrarErro(request, response, "Coleta não encontrada.");
                    return;
                }
                request.setAttribute("coleta", coleta);
                request.setAttribute("cooperativas", opcoesOuVazio(coletaDAO::listarCooperativas, "cooperativas"));
                request.setAttribute("cooperados", opcoesOuVazio(coletaDAO::listarCooperados, "cooperados"));
                request.setAttribute("rotas", opcoesOuVazio(coletaDAO::listarRotas, "rotas"));
                request.setAttribute("materiaisCatalogo", materialDAO.listarMateriais());
                request.setAttribute("materiaisAtuais", coletaDAO.listarMateriaisDaColeta(coleta));
                request.getRequestDispatcher("/editar-coleta.jsp").forward(request, response);
            } else if ("detalhes".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                Coleta filtro = new Coleta();
                filtro.setIdColeta(id);
                Coleta coleta = coletaDAO.buscarPorId(filtro);

                if (coleta == null) {
                    mostrarErro(request, response, "Coleta não encontrada.");
                    return;
                }

                Map<Long, BigDecimal> materiais =
                        coletaDAO.listarMateriaisDaColeta(coleta);

                BigDecimal soma = materiais.values().stream()
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal informado = BigDecimal.ZERO;
                if (coleta.getPesoTotalKg() != null) {
                    informado = coleta.getPesoTotalKg();
                }

                Map<Long, String> cooperativas =
                        opcoesOuVazio(coletaDAO::listarCooperativas, "cooperativas");
                Map<Long, String> cooperados =
                        opcoesOuVazio(coletaDAO::listarCooperados, "cooperados");
                Map<Long, String> rotas =
                        opcoesOuVazio(coletaDAO::listarRotas, "rotas");

                request.setAttribute("coleta", coleta);
                request.setAttribute("materiais", materiais);
                request.setAttribute("nomesMateriais", nomesDosMateriais());
                request.setAttribute("somaMateriais", soma);
                request.setAttribute("pesoConfere", informado.compareTo(soma) == 0);
                request.setAttribute("nomeCooperativa",
                        nomeDe(cooperativas, coleta.getRelacionamentos().getIdCooperativa(), "Cooperativa"));
                request.setAttribute("nomeRota",
                        nomeDe(rotas, coleta.getRelacionamentos().getIdRota(), "Rota"));
                request.setAttribute("nomeCooperado",
                        nomeDe(cooperados, coleta.getRelacionamentos().getIdCooperadoResponsavel(), "Cooperado"));

                request.getRequestDispatcher("/detalhes-coleta.jsp")
                        .forward(request, response);

            } else if ("nova".equals(acao)) {
                request.setAttribute("cooperativas",
                        opcoesOuVazio(coletaDAO::listarCooperativas, "cooperativas"));
                request.setAttribute("cooperados",
                        opcoesOuVazio(coletaDAO::listarCooperados, "cooperados"));
                request.setAttribute("rotas",
                        opcoesOuVazio(coletaDAO::listarRotas, "rotas"));
                request.setAttribute("materiaisCatalogo",
                        materialDAO.listarMateriais());

                request.getRequestDispatcher("/nova-coleta.jsp")
                        .forward(request, response);

            } else {
                request.setAttribute("coletas", coletaDAO.listarColetas());
                request.setAttribute("cooperativas",
                        opcoesOuVazio(coletaDAO::listarCooperativas, "cooperativas"));
                request.setAttribute("cooperados",
                        opcoesOuVazio(coletaDAO::listarCooperados, "cooperados"));

                request.getRequestDispatcher("/coletas.jsp")
                        .forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            mostrarErro(request, response, "ID inválido.");
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao consultar coletas.", e);
        }
    }

    // Se a consulta falhar, devolve mapa vazio e a tela usa campos de ID no lugar dos selects
    private Map<Long, String> opcoesOuVazio(Supplier<Map<Long, String>> fonte,
                                            String descricao) {
        try {
            return fonte.get();
        } catch (RuntimeException e) {
            getServletContext().log("Não foi possível carregar " + descricao, e);
            return new LinkedHashMap<>();
        }
    }

    private Map<Long, String> nomesDosMateriais() {
        Map<Long, String> nomes = new LinkedHashMap<>();
        for (Material m : materialDAO.listarMateriais()) {
            nomes.put(m.getIdMaterial(), m.getNome());
        }
        return nomes;
    }

    private String nomeDe(Map<Long, String> mapa, Long id, String rotulo) {
        if (id == null) {
            return null;
        }
        String nome = mapa.get(id);
        if (nome != null) {
            return nome;
        }
        return rotulo + " #" + id;
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");

        try {
            if ("atualizar".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                Coleta filtro = new Coleta();
                filtro.setIdColeta(id);
                Coleta coleta = coletaDAO.buscarPorId(filtro);
                if (coleta == null) {
                    mostrarErro(request, response, "Coleta não encontrada.");
                    return;
                }
                coleta.getRelacionamentos().setIdCooperativa(
                        Long.parseLong(request.getParameter("idCooperativa")));
                if (coleta.getTipo() == TipoColeta.COLETA_EXTERNA) {
                    String rota = request.getParameter("idRota");
                    Long idRota = rota == null || rota.isBlank() ? null : Long.parseLong(rota);
                    if (!java.util.Objects.equals(idRota, coleta.getRelacionamentos().getIdRota())) {
                        coleta.getRelacionamentos().setIdEnderecoRota(null);
                    }
                    coleta.getRelacionamentos().setIdRota(idRota);
                }
                String responsavel = request.getParameter("idCooperadoResponsavel");
                coleta.getRelacionamentos().setIdCooperadoResponsavel(
                        responsavel == null || responsavel.isBlank() ? null : Long.parseLong(responsavel));
                String data = request.getParameter("dataAgendada");
                coleta.setDataAgendada(data == null || data.isBlank() ? null : LocalDate.parse(data));
                coleta.setObservacoes(request.getParameter("observacoes"));
                if (coleta.getTipo() == TipoColeta.ENTREGA_COOPERATIVA) {
                    coleta.setNomeLocalOrigem(request.getParameter("nomeLocalOrigem"));
                }
                String[] idsMateriais = request.getParameterValues("idMaterial");
                String[] quantidades = request.getParameterValues("quantidadeKg");
                if (idsMateriais == null && quantidades == null) {
                    idsMateriais = new String[0];
                    quantidades = new String[0];
                }
                if (idsMateriais == null || quantidades == null || idsMateriais.length != quantidades.length) {
                    throw new IllegalArgumentException("Confira os materiais e suas quantidades.");
                }
                ArrayList<ColetaMaterial> itens = new ArrayList<>();
                java.util.Set<Long> idsUnicos = new java.util.HashSet<>();
                for (int i = 0; i < idsMateriais.length; i++) {
                    ColetaMaterial item = new ColetaMaterial();
                    item.setIdMaterial(Long.parseLong(idsMateriais[i]));
                    item.setQuantidadeKg(new BigDecimal(quantidades[i].replace(",", ".")));
                    if (!idsUnicos.add(item.getIdMaterial())) {
                        throw new IllegalArgumentException("Não repita o mesmo material na coleta.");
                    }
                    itens.add(item);
                }
                java.util.Set<Long> catalogo = new java.util.HashSet<>();
                for (Material material : materialDAO.listarMateriais()) {
                    catalogo.add(material.getIdMaterial());
                }
                if (!catalogo.containsAll(idsUnicos)) {
                    throw new IllegalArgumentException("Há material que não existe mais no catálogo.");
                }
                coleta.getRelacionamentos().setMateriaisColetados(itens);
                if (coletaDAO.atualizarColeta(coleta) == 0) {
                    mostrarErro(request, response, "Coleta não encontrada.");
                    return;
                }
                try {
                    coletaDAO.atualizarMateriaisDaColeta(coleta);
                } catch (RuntimeException e) {
                    getServletContext().log("Falha ao atualizar materiais da coleta.", e);
                    mostrarErro(request, response,
                            "A coleta pode ter sido atualizada parcialmente. Confira os vínculos e materiais antes de tentar novamente.");
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/coleta?acao=detalhes&id=" + id + "&sucesso=atualizada");
            } else if ("cadastrar".equals(acao)) {
                cadastrar(request);
                response.sendRedirect(request.getContextPath()
                        + "/coleta?sucesso=cadastro");

            } else if ("iniciar".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                Coleta alteracao = new Coleta();
                alteracao.setIdColeta(id);
                alteracao.setStatus(StatusColeta.EM_ANDAMENTO);
                if (coletaDAO.atualizarStatus(alteracao) == 0) {
                    mostrarErro(request, response, "Não foi possível iniciar esta coleta.");
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/coleta?acao=detalhes&id=" + id + "&sucesso=iniciada");

            } else if ("finalizar".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                Coleta alteracao = new Coleta();
                alteracao.setIdColeta(id);
                alteracao.setStatus(StatusColeta.CONCLUIDA);
                if (coletaDAO.atualizarStatus(alteracao) == 0) {
                    mostrarErro(request, response, "Não foi possível finalizar esta coleta.");
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/coleta?acao=detalhes&id=" + id + "&sucesso=finalizada");

            } else if ("cancelar".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                Coleta cancelamento = new Coleta();
                cancelamento.setIdColeta(id);
                if (coletaDAO.excluirColeta(cancelamento) == 0) {
                    mostrarErro(request, response, "Não foi possível cancelar esta coleta.");
                    return;
                }
                response.sendRedirect(request.getContextPath()
                        + "/coleta?sucesso=cancelamento");

            } else if ("excluir".equals(acao)) {
                Long id = Long.parseLong(request.getParameter("id"));
                String confirmacao = request.getParameter("confirmacao");

                if (!String.valueOf(id).equals(confirmacao)) {
                    mostrarErro(request, response, "Digite o código correto da coleta.");
                    return;
                }

                Coleta exclusao = new Coleta();
                exclusao.setIdColeta(id);
                if (coletaDAO.apagarColeta(exclusao) == 0) {
                    mostrarErro(request, response, "Coleta não encontrada.");
                    return;
                }

                response.sendRedirect(request.getContextPath()
                        + "/coleta?sucesso=exclusao");

            } else {
                mostrarErro(request, response, "Ação inválida.");
            }

        } catch (NumberFormatException | java.time.format.DateTimeParseException e) {
            reabrirFormulario(request, response, acao, "Confira os campos numéricos e a data.");
        } catch (IllegalArgumentException e) {
            reabrirFormulario(request, response, acao, e.getMessage());
        } catch (RuntimeException e) {
            getServletContext().log("Erro ao processar a coleta.", e);
            mostrarErro(request, response, "Não foi possível concluir a operação da coleta.");
        }
    }

    // Mostra a falha na lista após um redirecionamento, sem confundir erro com sucesso.
    private void mostrarErro(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws IOException {
        request.getSession().setAttribute("erroColeta", mensagem);
        response.sendRedirect(request.getContextPath() + "/coleta");
    }

    // Mantém os parâmetros do POST na tela de cadastro ou edição quando a validação falha.
    private void reabrirFormulario(HttpServletRequest request, HttpServletResponse response,
                                   String acao, String mensagem) throws ServletException, IOException {
        request.setAttribute("erroColeta", mensagem);
        request.setAttribute("dadosEnviados", true);
        if ("cadastrar".equals(acao)) {
            request.setAttribute("cooperativas", opcoesOuVazio(coletaDAO::listarCooperativas, "cooperativas"));
            request.setAttribute("cooperados", opcoesOuVazio(coletaDAO::listarCooperados, "cooperados"));
            request.setAttribute("rotas", opcoesOuVazio(coletaDAO::listarRotas, "rotas"));
            request.setAttribute("materiaisCatalogo", materialDAO.listarMateriais());
            request.getRequestDispatcher("/nova-coleta.jsp").forward(request, response);
            return;
        }
        if ("atualizar".equals(acao)) {
            try {
                Coleta filtro = new Coleta();
                filtro.setIdColeta(Long.parseLong(request.getParameter("id")));
                Coleta coleta = coletaDAO.buscarPorId(filtro);
                if (coleta != null) {
                    request.setAttribute("coleta", coleta);
                    request.setAttribute("cooperativas", opcoesOuVazio(coletaDAO::listarCooperativas, "cooperativas"));
                    request.setAttribute("cooperados", opcoesOuVazio(coletaDAO::listarCooperados, "cooperados"));
                    request.setAttribute("rotas", opcoesOuVazio(coletaDAO::listarRotas, "rotas"));
                    request.setAttribute("materiaisCatalogo", materialDAO.listarMateriais());
                    request.setAttribute("materiaisAtuais", coletaDAO.listarMateriaisDaColeta(coleta));
                    request.getRequestDispatcher("/editar-coleta.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // ID ausente ou inválido: a lista mostra a falha.
            }
        }
        mostrarErro(request, response, mensagem);
    }

    private void cadastrar(HttpServletRequest request) {
        Coleta coleta = new Coleta();

        coleta.getRelacionamentos().setIdCooperativa(
                Long.parseLong(request.getParameter("idCooperativa")));
        coleta.setTipo(TipoColeta.valueOf(request.getParameter("tipo")));
        coleta.setStatus(StatusColeta.AGENDADA);
        coleta.setObservacoes(request.getParameter("observacoes"));

        String data = request.getParameter("dataAgendada");
        if (data != null && !data.isBlank()) {
            coleta.setDataAgendada(LocalDate.parse(data));
        }

        String rota = request.getParameter("idRota");
        if (rota == null || rota.isBlank()) {
            coleta.getRelacionamentos().setIdRota(null);
        } else {
            coleta.getRelacionamentos().setIdRota(Long.parseLong(rota));
        }

        String endereco = request.getParameter("idEnderecoRota");
        if (endereco == null || endereco.isBlank()) {
            coleta.getRelacionamentos().setIdEnderecoRota(null);
        } else {
            coleta.getRelacionamentos().setIdEnderecoRota(Long.parseLong(endereco));
        }

        String responsavel = request.getParameter("idCooperadoResponsavel");
        if (responsavel == null || responsavel.isBlank()) {
            coleta.getRelacionamentos().setIdCooperadoResponsavel(null);
        } else {
            coleta.getRelacionamentos().setIdCooperadoResponsavel(Long.parseLong(responsavel));
        }

        if (coleta.getTipo() == TipoColeta.COLETA_EXTERNA) {
            coleta.setNomeLocalOrigem(null);
        } else if (coleta.getTipo() == TipoColeta.ENTREGA_COOPERATIVA) {
            coleta.getRelacionamentos().setIdRota(null);
            coleta.getRelacionamentos().setIdEnderecoRota(null);
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

        ArrayList<ColetaMaterial> itens = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entrada : materiais.entrySet()) {
            ColetaMaterial item = new ColetaMaterial();
            item.setIdMaterial(entrada.getKey());
            item.setQuantidadeKg(entrada.getValue());
            itens.add(item);
        }
        coleta.getRelacionamentos().setMateriaisColetados(itens);
        if (coletaDAO.cadastrarColeta(coleta) == 0) {
            throw new IllegalStateException("Não foi possível cadastrar a coleta.");
        }
    }
}
