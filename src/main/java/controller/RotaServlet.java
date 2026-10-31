package controller;

import dao.CooperativaDAO;
import dao.RotaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.principais.Cooperativa;
import model.principais.Rota;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/rota")
public class RotaServlet extends HttpServlet {

    // ATRIBUTOS

    private final RotaDAO rotaDAO = new RotaDAO();
    private final CooperativaDAO cooperativaDAO = new CooperativaDAO();

    // === POST: cadastrar, editar e excluir ===========================================================================

    // Processa o cadastro, a edição ou a exclusão de uma rota e volta para a lista.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Leitura da requisição

        String acao = request.getParameter("acao");
        String idRotaTexto = request.getParameter("idRota");
        String idCooperativaTexto = request.getParameter("idCooperativa");
        String nomeRota = request.getParameter("nomeRota");
        int linhas = 0;

        try {

            // Validação dos dados

            Rota rota = new Rota();
            if ("cadastrar".equals(acao) || "editar".equals(acao)) {
                rota = new Rota(Long.parseLong(idCooperativaTexto), nomeRota);
            }
            if ("editar".equals(acao) || "excluir".equals(acao)) {
                rota.setIdRota(Long.parseLong(idRotaTexto));
            }

            // Processamento da ação

            if ("cadastrar".equals(acao)) {
                linhas = rotaDAO.cadastrarRota(rota);
            } else if ("editar".equals(acao)) {
                linhas = rotaDAO.atualizarRota(rota);
            } else if ("excluir".equals(acao)) {
                linhas = rotaDAO.excluirRota(rota);
            } else {
                mostrarErro(request, response, "Ação inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarErro(request, response, "Confira a cooperativa e o código da rota.");
            return;
        } catch (IllegalArgumentException e) {
            mostrarErro(request, response, e.getMessage());
            return;
        } catch (RuntimeException e) {
            if (violacao(e, "23503")) {
                mostrarErro(request, response, "Não é possível concluir: esta rota está vinculada a coletas.");
                return;
            }
            throw new ServletException("Erro ao processar a rota.", e);
        }

        // Preparação da resposta

        if (linhas == 0) {
            mostrarErro(request, response, "Rota não encontrada.");
            return;
        }

        // Encaminhamento

        response.sendRedirect(request.getContextPath() + "/rota?sucesso=" + acao);
    }

    // === GET: listar =================================================================================================

    // Lista as rotas com o nome da cooperativa e abre a página de rotas.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Leitura da requisição

        Object erro = request.getSession().getAttribute("erroRota");
        if (erro != null) {
            request.setAttribute("erroRota", erro);
            request.getSession().removeAttribute("erroRota");
        }

        // Processamento da ação

        try {
            ArrayList<Rota> rotas = rotaDAO.listarRotas();
            Map<Long, String> cooperativas = new LinkedHashMap<>();
            for (Cooperativa coop : cooperativaDAO.exibirCooperativas()) {
                cooperativas.put(coop.getIdCooperativa(), coop.getNome());
            }

            // Preparação da resposta

            request.setAttribute("rotas", rotas);
            request.setAttribute("cooperativas", cooperativas);
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao consultar rotas.", e);
        }

        // Encaminhamento

        request.getRequestDispatcher("/rotas.jsp").forward(request, response);
    }

    // === AUXILIARES ==================================================================================================

    // Mantém a falha visível na tela de rotas após o redirecionamento.
    private void mostrarErro(HttpServletRequest request, HttpServletResponse response, String mensagem)
            throws IOException {
        request.getSession().setAttribute("erroRota", mensagem);
        response.sendRedirect(request.getContextPath() + "/rota");
    }

    // Procura, na cadeia de causas, um erro do banco com o SQLState informado.
    private boolean violacao(Throwable erro, String sqlState) {
        Throwable atual = erro;
        while (atual != null) {
            if (atual instanceof SQLException && sqlState.equals(((SQLException) atual).getSQLState())) {
                return true;
            }
            atual = atual.getCause();
        }
        return false;
    }
}
