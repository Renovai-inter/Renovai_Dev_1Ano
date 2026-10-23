package controller;

import dao.CooperadoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.enums.AtividadeCooperado;
import model.enums.CargoCooperado;
import model.enums.StatusCooperado;
import model.principais.Cooperado;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/cooperado")
public class CooperadoServlet extends HttpServlet {

    private final CooperadoDAO dao = new CooperadoDAO();

    // Abre as páginas de lista, cadastro, detalhe e edição.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String acao = request.getParameter("acao");
        try {
            if ("ver".equals(acao)) {
                abrirVer(request, response, lerId(request));
            } else if ("novo".equals(acao)) {
                abrirNovo(request, response);
            } else if ("editar".equals(acao)) {
                abrirEditar(request, response, lerId(request));
            } else {
                abrirLista(request, response);
            }
        } catch (RuntimeException e) {
            throw new ServletException("Falha ao consultar cooperados.", e);
        }
    }

    // Encaminha as ações de cadastro, edição, senha, inativação e exclusão.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");
        try {
            if ("salvar".equals(acao)) {
                salvar(request, response);
            } else if ("atualizar".equals(acao)) {
                atualizar(request, response);
            } else if ("redefinirSenha".equals(acao)) {
                redefinirSenha(request, response);
            } else if ("inativar".equals(acao)) {
                inativar(request, response);
            } else if ("excluir".equals(acao)) {
                excluir(request, response);
            } else {
                response.sendRedirect("cooperado");
            }
        } catch (RuntimeException e) {
            throw new ServletException("Falha ao processar cooperado.", e);
        }
    }

    // Entrega a lista ao JSP existente.
    private void abrirLista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("lista", dao.listaCooperados());
        request.getRequestDispatcher("/cooperado-lista.jsp").forward(request, response);
    }

    // Carrega o cooperado e seus vínculos para a página de detalhe.
    private void abrirVer(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {
        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }
        Cooperado filtro = new Cooperado();
        filtro.setIdCooperado(id);
        Cooperado cooperado = dao.buscarCooperadoPorId(filtro);
        if (cooperado == null) {
            response.sendRedirect("cooperado");
            return;
        }
        request.setAttribute("cooperado", cooperado);
        request.setAttribute("permissoesDoCooperado", cooperado.getPermissoes());
        request.setAttribute("todasPermissoes", dao.listarTodasPermissoes());
        request.setAttribute("totalColetas", dao.contarColetasDoCooperado(cooperado));
        ArrayList<String> atividadesOpcoes = new ArrayList<>();
        for (AtividadeCooperado atividade : AtividadeCooperado.values()) {
            atividadesOpcoes.add(atividade.name());
        }
        request.setAttribute("atividadesOpcoes", atividadesOpcoes);
        request.getRequestDispatcher("/cooperado-ver.jsp").forward(request, response);
    }

    // Prepara o formulário de cadastro com as opções do banco.
    private void abrirNovo(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("cooperativas", dao.listarCooperativas());
        request.setAttribute("todasPermissoes", dao.listarTodasPermissoes());
        ArrayList<String> cargos = new ArrayList<>();
        for (CargoCooperado cargo : CargoCooperado.values()) {
            cargos.add(cargo.name());
        }
        ArrayList<String> atividadesOpcoes = new ArrayList<>();
        for (AtividadeCooperado atividade : AtividadeCooperado.values()) {
            atividadesOpcoes.add(atividade.name());
        }
        request.setAttribute("cargos", cargos);
        request.setAttribute("atividadesOpcoes", atividadesOpcoes);
        request.getRequestDispatcher("/cooperado-novo.jsp").forward(request, response);
    }

    // Prepara o formulário de edição e apresenta uma senha inicial uma única vez.
    private void abrirEditar(HttpServletRequest request, HttpServletResponse response, Long id)
            throws ServletException, IOException {
        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }
        Cooperado filtro = new Cooperado();
        filtro.setIdCooperado(id);
        Cooperado cooperado = dao.buscarCooperadoPorId(filtro);
        if (cooperado == null) {
            response.sendRedirect("cooperado");
            return;
        }
        request.setAttribute("cooperado", cooperado);
        request.setAttribute("cooperativas", dao.listarCooperativas());
        request.setAttribute("totalColetas", dao.contarColetasDoCooperado(cooperado));
        ArrayList<String> cargos = new ArrayList<>();
        for (CargoCooperado cargo : CargoCooperado.values()) {
            cargos.add(cargo.name());
        }
        ArrayList<String> statusOpcoes = new ArrayList<>();
        for (StatusCooperado status : StatusCooperado.values()) {
            statusOpcoes.add(status.name());
        }
        ArrayList<String> atividadesOpcoes = new ArrayList<>();
        for (AtividadeCooperado atividade : AtividadeCooperado.values()) {
            atividadesOpcoes.add(atividade.name());
        }
        request.setAttribute("cargos", cargos);
        request.setAttribute("statusOpcoes", statusOpcoes);
        request.setAttribute("atividadesOpcoes", atividadesOpcoes);
        HttpSession sessao = request.getSession(false);
        if (sessao != null && id.equals(sessao.getAttribute("senhaInicialCooperadoId"))) {
            request.setAttribute("senhaNova", sessao.getAttribute("senhaInicialCooperado"));
            sessao.removeAttribute("senhaInicialCooperadoId");
            sessao.removeAttribute("senhaInicialCooperado");
        }
        request.getRequestDispatcher("/cooperado-editar.jsp").forward(request, response);
    }

    // Cadastra conta, cooperado, atividades e permissões informados no formulário.
    private void salvar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String nome = obrigatorio(request.getParameter("nomeCompleto"), "nome completo");
            String email = obrigatorio(request.getParameter("email"), "e-mail");
            Long idCooperativa = idObrigatorio(request.getParameter("idCooperativa"), "cooperativa");
            CargoCooperado cargo = lerCargo(request.getParameter("cargo"));
            ArrayList<AtividadeCooperado> atividades = lerAtividades(
                    request.getParameterValues("atividades"));
            ArrayList<Long> permissoes = lerPermissoes(request.getParameterValues("permissoes"));
            Cooperado entrada = new Cooperado(idCooperativa, nome, email, cargo,
                    atividades, permissoes);
            int linhas = dao.cadastrarCooperado(entrada);
            if (linhas == 0) {
                request.setAttribute("erro", "Não foi possível cadastrar o cooperado.");
                abrirNovo(request, response);
                return;
            }
            HttpSession sessao = request.getSession();
            sessao.setAttribute("senhaInicialCooperadoId", entrada.getIdCooperado());
            sessao.setAttribute("senhaInicialCooperado", entrada.getSenhaInicial());
            response.sendRedirect("cooperado?acao=editar&id=" + entrada.getIdCooperado());
        } catch (IllegalArgumentException e) {
            request.setAttribute("erro", e.getMessage());
            abrirNovo(request, response);
        }
    }

    // Atualiza identificação, vínculo e seleção de atividades.
    private void atualizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = lerId(request);
        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }
        try {
            String nome = obrigatorio(request.getParameter("nomeCompleto"), "nome completo");
            String email = obrigatorio(request.getParameter("email"), "e-mail");
            Long idCooperativa = idObrigatorio(request.getParameter("idCooperativa"), "cooperativa");
            CargoCooperado cargo = lerCargo(request.getParameter("cargo"));
            StatusCooperado status = lerStatus(request.getParameter("status"));
            ArrayList<AtividadeCooperado> atividades = lerAtividades(
                    request.getParameterValues("atividades"));
            Cooperado alteracao = new Cooperado();
            alteracao.setIdCooperado(id);
            alteracao.setNomeCompleto(nome);
            alteracao.setEmail(email);
            alteracao.setIdCooperativa(idCooperativa);
            alteracao.setCargo(cargo);
            alteracao.setStatus(status);
            alteracao.setAtividadesCooperado(atividades);
            int linhas = dao.editarCooperado(alteracao);
            if (linhas == 0) {
                response.sendRedirect("cooperado");
                return;
            }
            response.sendRedirect("cooperado?acao=ver&id=" + id);
        } catch (IllegalArgumentException e) {
            request.setAttribute("erro", e.getMessage());
            abrirEditar(request, response, id);
        }
    }

    // Gera uma nova senha temporária e a mostra uma única vez na edição.
    private void redefinirSenha(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = lerId(request);
        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }
        Cooperado cooperado = new Cooperado();
        cooperado.setIdCooperado(id);
        int linhas = dao.redefinirSenha(cooperado);
        if (linhas == 0) {
            response.sendRedirect("cooperado");
            return;
        }
        request.setAttribute("senhaNova", cooperado.getSenhaInicial());
        abrirEditar(request, response, id);
    }

    // Mantém o registro e altera apenas o status.
    private void inativar(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Long id = lerId(request);
        if (id != null) {
            Cooperado cooperado = new Cooperado();
            cooperado.setIdCooperado(id);
            dao.inativarCooperado(cooperado);
        }
        response.sendRedirect("cooperado");
    }

    // Exclui o cooperado quando as referências do banco permitem.
    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = lerId(request);
        if (id == null) {
            response.sendRedirect("cooperado");
            return;
        }
        try {
            Cooperado cooperado = new Cooperado();
            cooperado.setIdCooperado(id);
            dao.excluirCooperado(cooperado);
            response.sendRedirect("cooperado");
        } catch (RuntimeException e) {
            if (restricaoDeReferencia(e)) {
                request.setAttribute("erro",
                        "Este cooperado tem registros relacionados e não pode ser excluído. " +
                        "Use a opção Alterar status para inativo.");
                abrirVer(request, response, id);
            } else {
                throw e;
            }
        }
    }

    // Lê um ID opcional da requisição.
    private Long lerId(HttpServletRequest request) {
        String valor = request.getParameter("id");
        if (valor == null) {
            return null;
        }
        try {
            long id = Long.parseLong(valor);
            if (id <= 0) {
                return null;
            }
            return id;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Preencha " + campo + ".");
        }
        return valor.trim();
    }

    private Long idObrigatorio(String valor, String campo) {
        try {
            long id = Long.parseLong(obrigatorio(valor, campo));
            if (id <= 0) {
                throw new IllegalArgumentException("Selecione " + campo + ".");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Selecione " + campo + ".", e);
        }
    }

    private CargoCooperado lerCargo(String valor) {
        if (valor == null || valor.isBlank() || "Cooperado".equalsIgnoreCase(valor)) {
            return null;
        }
        for (CargoCooperado cargo : CargoCooperado.values()) {
            if (cargo.name().equalsIgnoreCase(valor)) {
                return cargo;
            }
        }
        throw new IllegalArgumentException("Cargo não permitido: " + valor + ".");
    }

    private StatusCooperado lerStatus(String valor) {
        for (StatusCooperado status : StatusCooperado.values()) {
            if (status.name().equalsIgnoreCase(valor)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status não permitido.");
    }

    private ArrayList<AtividadeCooperado> lerAtividades(String[] valores) {
        ArrayList<AtividadeCooperado> atividades = new ArrayList<>();
        if (valores == null) {
            return atividades;
        }
        for (String valor : valores) {
            AtividadeCooperado encontrada = null;
            if ("Coleta externa".equalsIgnoreCase(valor)) {
                encontrada = AtividadeCooperado.COLETA_EXTERNA;
            } else if ("Pesagem".equalsIgnoreCase(valor)) {
                encontrada = AtividadeCooperado.PESAGEM;
            } else if ("Prensagem".equalsIgnoreCase(valor)) {
                encontrada = AtividadeCooperado.PRENSAGEM;
            } else {
                for (AtividadeCooperado atividade : AtividadeCooperado.values()) {
                    if (atividade.name().equalsIgnoreCase(valor)) {
                        encontrada = atividade;
                    }
                }
            }
            if (encontrada == null) {
                throw new IllegalArgumentException("Atividade sem correspondência no cadastro: " +
                        valor + ".");
            }
            if (!atividades.contains(encontrada)) {
                atividades.add(encontrada);
            }
        }
        return atividades;
    }

    private ArrayList<Long> lerPermissoes(String[] valores) {
        ArrayList<Long> permissoes = new ArrayList<>();
        if (valores == null) {
            return permissoes;
        }
        for (String valor : valores) {
            Long id = idObrigatorio(valor, "uma permissão válida");
            if (!permissoes.contains(id)) {
                permissoes.add(id);
            }
        }
        return permissoes;
    }

    private boolean restricaoDeReferencia(Throwable erro) {
        Throwable causa = erro;
        while (causa != null) {
            if (causa instanceof SQLException && "23503".equals(((SQLException) causa).getSQLState())) {
                return true;
            }
            causa = causa.getCause();
        }
        return false;
    }
}
