package controller;

import dao.CooperadoDAO;
import dao.CooperativaDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.ColetaDAO;
import dao.NegociacaoDAO;
import model.enums.StatusCooperativa;
import model.principais.Coleta;
import model.principais.Cooperado;
import model.principais.Cooperativa;
import model.principais.Negociacao;
import util.SenhaAPI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/cooperativa")
public class CooperativaServlet extends HttpServlet {

    // ATRIBUTOS

    private static final String PAGINA_LISTA = "/Cooperativa-lista.jsp";
    private static final String PAGINA_FORM = "/Cooperativa-form.jsp";
    private static final String PAGINA_DETALHE = "/Cooperativa-detalhe.jsp";

    private final CooperativaDAO dao = new CooperativaDAO();
    private final CooperadoDAO cooperadoDAO = new CooperadoDAO();
    private final ColetaDAO coletaDAO = new ColetaDAO();
    private final NegociacaoDAO negociacaoDAO = new NegociacaoDAO();

    // Abre a lista, o formulário de cadastro ou o detalhe da cooperativa, aplicando o filtro informado.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Leitura da requisição

        HttpSession sessao = request.getSession();
        String[] tipos = {"mensagem", "erro"};
        for (String tipo : tipos) {
            Object texto = sessao.getAttribute(tipo);
            if (texto != null) {
                request.setAttribute(tipo, texto);
                sessao.removeAttribute(tipo);
            }
        }
        String pagina = request.getParameter("pagina");

        if ("nova".equals(pagina)) {
            request.getRequestDispatcher(PAGINA_FORM).forward(request, response);
            return;
        }
        if ("editar".equals(pagina)) {
            long id;
            try {
                id = Long.parseLong(request.getParameter("id"));
            } catch (NumberFormatException e) {
                redirecionar(request, response, "erro", "Cooperativa não encontrada.");
                return;
            }
            Cooperativa filtro = new Cooperativa();
            filtro.setIdCooperativa(id);
            Cooperativa cooperativa;
            try {
                cooperativa = dao.buscarPorId(filtro);
            } catch (RuntimeException e) {
                throw new ServletException("Erro ao consultar a cooperativa.", e);
            }
            if (cooperativa == null) {
                redirecionar(request, response, "erro", "Cooperativa não encontrada.");
                return;
            }
            request.setAttribute("cooperativa", cooperativa);
            request.getRequestDispatcher(PAGINA_FORM).forward(request, response);
            return;
        }
        if ("detalhe".equals(pagina)) {
            mostrarDetalhe(request, response);
            return;
        }

        String busca = request.getParameter("busca");
        String cidade = request.getParameter("cidade");
        String estado = request.getParameter("estado");
        String status = request.getParameter("status");

        try {
            // Processamento da ação

            ArrayList<Cooperativa> cooperativas;
            if (preenchido(busca)) {
                cooperativas = dao.buscarCooperativasPorNomeOuCodigo(busca.trim());
            } else if (preenchido(cidade)) {
                cooperativas = dao.filtrarCooperativasPorCidade(cidade.trim());
            } else if (preenchido(estado)) {
                cooperativas = dao.filtrarCooperativasPorEstado(estado.trim());
            } else if (preenchido(status)) {
                cooperativas = dao.filtrarCooperativasPorStatus(status.trim());
            } else {
                cooperativas = dao.exibirCooperativas();
            }

            // Preparação da resposta

            request.setAttribute("cooperativas", cooperativas);

            // Encaminhamento

            request.getRequestDispatcher(PAGINA_LISTA).forward(request, response);
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao consultar as cooperativas.", e);
        }
    }

    // Encaminha as ações de cadastro, atualização e exclusão de cooperativa.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Leitura da requisição

        request.setCharacterEncoding("UTF-8");
        String acao = request.getParameter("acao");

        // Processamento da ação

        if ("cadastrar".equals(acao)) {
            cadastrar(request, response);
        } else if ("atualizar".equals(acao)) {
            atualizar(request, response);
        } else if ("excluir".equals(acao)) {
            excluir(request, response);
        } else if ("inativar".equals(acao)) {
            alterarStatus(request, response, StatusCooperativa.INATIVA);
        } else if ("reativar".equals(acao)) {
            alterarStatus(request, response, StatusCooperativa.ATIVA);
        } else {
            redirecionar(request, response, "erro", "Ação inválida.");
        }
    }

    // Cadastra a cooperativa e o gestor com senha temporária a partir do formulário.
    private void cadastrar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Leitura da requisição

        String senhaTemporaria = valor(request, "gestor_senha_temporaria");

        // Validação dos dados

        if (valor(request, "nome").isEmpty() || valor(request, "nome_publico").isEmpty()
                || valor(request, "cnpj").isEmpty() || valor(request, "email_institucional").isEmpty()
                || valor(request, "telefone_whatsapp").isEmpty() || valor(request, "cep").isEmpty()
                || valor(request, "endereco").isEmpty() || valor(request, "cidade").isEmpty()
                || valor(request, "estado").isEmpty()) {
            reabrirFormulario(request, response, null, "Preencha todos os dados institucionais obrigatórios.");
            return;
        }
        if (valor(request, "gestor_nome_completo").isEmpty() || valor(request, "gestor_email").isEmpty()
                || senhaTemporaria.isEmpty()) {
            reabrirFormulario(request, response, null, "Preencha nome, e-mail e senha temporária do gestor.");
            return;
        }

        try {
            Cooperativa coop = new Cooperativa();
            montarCooperativa(request, coop, false);
            Cooperado gestor = new Cooperado();
            gestor.setNomeCompleto(valor(request, "gestor_nome_completo"));
            if (!valor(request, "gestor_nome_usuario").isEmpty()) {
                gestor.setNomeUsuario(valor(request, "gestor_nome_usuario"));
            }
            gestor.setEmail(valor(request, "gestor_email"));
            gestor.setSenhaHash(SenhaAPI.gerarHash(senhaTemporaria));

            // Processamento da ação

            int linhas = dao.cadastrarCooperativa(coop, gestor);

            // Encaminhamento

            if (linhas > 0) {
                redirecionar(request, response, "mensagem", "Cooperativa e gestor cadastrados com sucesso.");
            } else {
                reabrirFormulario(request, response, null, "Não foi possível cadastrar a cooperativa e o gestor.");
            }
        } catch (IllegalArgumentException e) {
            reabrirFormulario(request, response, null, e.getMessage());
        } catch (RuntimeException e) {
            if (violacao(e, "23505")) {
                reabrirFormulario(request, response, null,
                        "Já existe cadastro com este CNPJ, e-mail ou nome de usuário.");
                return;
            }
            throw new ServletException("Erro ao cadastrar a cooperativa.", e);
        }
    }

    // Atualiza a cooperativa: carrega os dados atuais e aplica somente os campos enviados no formulário.
    private void atualizar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Leitura da requisição

        long id = lerId(request);

        // Validação dos dados

        if (id <= 0L) {
            redirecionar(request, response, "erro", "Informe a cooperativa e os novos valores.");
            return;
        }
        String[] obrigatorios = {"nome", "nome_publico", "cnpj", "email_institucional",
                "telefone_whatsapp", "cep", "endereco", "cidade", "estado"};
        for (String campo : obrigatorios) {
            if (request.getParameter(campo) != null && valor(request, campo).isEmpty()) {
                reabrirFormulario(request, response, id, "Preencha todos os dados institucionais obrigatórios.");
                return;
            }
        }

        try {
            // Processamento da ação

            Cooperativa filtro = new Cooperativa();
            filtro.setIdCooperativa(id);
            Cooperativa coop = dao.buscarPorId(filtro);
            if (coop == null) {
                redirecionar(request, response, "erro", "Cooperativa não encontrada.");
                return;
            }
            montarCooperativa(request, coop, true);
            int linhas = dao.atualizarCooperativa(coop);

            // Encaminhamento

            if (linhas > 0) {
                redirecionar(request, response, "mensagem", "Cooperativa atualizada com sucesso.");
            } else {
                reabrirFormulario(request, response, id,
                        "Não foi possível atualizar: valor inválido ou cooperativa não encontrada.");
            }
        } catch (IllegalArgumentException e) {
            reabrirFormulario(request, response, id, e.getMessage());
        } catch (RuntimeException e) {
            if (violacao(e, "23505")) {
                reabrirFormulario(request, response, id, "Já existe outra cooperativa com este CNPJ.");
                return;
            }
            throw new ServletException("Erro ao atualizar a cooperativa.", e);
        }
    }

    // Exclui a cooperativa identificada pelo ID enviado, desde que não tenha registros vinculados.
    private void excluir(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Leitura da requisição

        long id = lerId(request);

        // Validação dos dados

        if (id <= 0L) {
            redirecionar(request, response, "erro", "Cooperativa inválida.");
            return;
        }

        try {
            // Processamento da ação

            Cooperativa filtro = new Cooperativa();
            filtro.setIdCooperativa(id);
            Cooperativa coop = dao.buscarPorId(filtro);
            if (coop == null) {
                redirecionar(request, response, "erro", "Cooperativa não encontrada.");
                return;
            }
            String vinculos = descreverVinculos(coop);
            if (vinculos != null) {
                redirecionar(request, response, "erro", "Não é possível excluir: a cooperativa tem "
                        + vinculos + ". Inative a cooperativa ou remova esses registros antes.");
                return;
            }
            int linhas = dao.excluirCooperativa(coop);

            // Encaminhamento

            if (linhas > 0) {
                redirecionar(request, response, "mensagem", "Cooperativa excluída com sucesso.");
            } else {
                redirecionar(request, response, "erro", "Cooperativa não encontrada.");
            }
        } catch (RuntimeException e) {
            if (violacao(e, "23503")) {
                redirecionar(request, response, "erro",
                        "Não é possível excluir: ainda existem registros vinculados a esta cooperativa. Inative-a.");
                return;
            }
            throw new ServletException("Erro ao excluir a cooperativa.", e);
        }
    }

    // Inativa ou reativa a cooperativa, mantendo todo o histórico.
    private void alterarStatus(HttpServletRequest request, HttpServletResponse response,
                               StatusCooperativa novoStatus) throws ServletException, IOException {

        // Leitura da requisição

        long id = lerId(request);

        // Validação dos dados

        if (id <= 0L) {
            redirecionar(request, response, "erro", "Cooperativa inválida.");
            return;
        }

        try {
            // Processamento da ação

            Cooperativa coop = new Cooperativa();
            coop.setIdCooperativa(id);
            coop.setStatus(novoStatus);
            int linhas = dao.alterarStatus(coop);

            // Encaminhamento

            if (linhas > 0) {
                redirecionar(request, response, "mensagem", novoStatus == StatusCooperativa.INATIVA
                        ? "Cooperativa inativada com sucesso." : "Cooperativa reativada com sucesso.");
            } else {
                redirecionar(request, response, "erro", "Cooperativa não encontrada.");
            }
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao alterar o status da cooperativa.", e);
        }
    }

    // Resume os registros vinculados (ex.: "4 cooperados, 10 coletas"); devolve null se não houver nenhum.
    private String descreverVinculos(Cooperativa coop) {
        int[] totais = {
                coop.getRelacionamentos().getQtdCooperados(),
                dao.contarRelacionados("rota", coop),
                dao.contarRelacionados("coleta", coop),
                dao.contarRelacionados("negociacao", coop)
        };
        String[][] nomes = {{"cooperado", "cooperados"}, {"rota", "rotas"},
                {"coleta", "coletas"}, {"negociação", "negociações"}};
        ArrayList<String> partes = new ArrayList<>();
        for (int i = 0; i < totais.length; i++) {
            if (totais[i] > 0) {
                partes.add(totais[i] + " " + (totais[i] == 1 ? nomes[i][0] : nomes[i][1]));
            }
        }
        if (partes.isEmpty()) {
            return null;
        }
        if (partes.size() == 1) {
            return partes.get(0);
        }
        return String.join(", ", partes.subList(0, partes.size() - 1)) + " e " + partes.get(partes.size() - 1);
    }

    // Informa se a falha veio de uma violação do banco com o código SQLState indicado (23505 = duplicado,
    // 23503 = registro ainda referenciado).
    private boolean violacao(Throwable erro, String sqlState) {
        for (Throwable atual = erro; atual != null; atual = atual.getCause()) {
            if (atual instanceof SQLException && sqlState.equals(((SQLException) atual).getSQLState())) {
                return true;
            }
        }
        return false;
    }

    // Preenche a cooperativa pelos setters; na atualização, aplica apenas os campos enviados.
    private void montarCooperativa(HttpServletRequest request, Cooperativa coop, boolean parcial) {
        if (!parcial || request.getParameter("nome") != null) {
            coop.setNome(valor(request, "nome"));
        }
        if (!parcial || request.getParameter("cnpj") != null) {
            coop.setCnpj(valor(request, "cnpj"));
        }
        if (!parcial || request.getParameter("nome_publico") != null) {
            coop.setNomePublico(valor(request, "nome_publico"));
        }
        if (!parcial || request.getParameter("descricao_institucional") != null) {
            coop.setDescricaoInstitucional(valor(request, "descricao_institucional"));
        }
        if (!parcial || request.getParameter("logo_url") != null) {
            coop.setLogoUrl(valor(request, "logo_url"));
        }
        if (!parcial || request.getParameter("email_institucional") != null) {
            coop.setEmailInstitucional(valor(request, "email_institucional"));
        }
        if (!parcial || request.getParameter("telefone_whatsapp") != null) {
            coop.setTelefoneWhatsapp(valor(request, "telefone_whatsapp"));
        }
        if (!parcial || request.getParameter("cep") != null) {
            coop.setCep(valor(request, "cep"));
        }
        if (!parcial || request.getParameter("endereco") != null) {
            coop.setEndereco(valor(request, "endereco"));
        }
        if (!parcial || request.getParameter("cidade") != null) {
            coop.setCidade(valor(request, "cidade"));
        }
        if (!parcial || request.getParameter("estado") != null) {
            coop.setEstado(valor(request, "estado"));
        }
    }

    // Lê o ID da cooperativa enviado no formulário; devolve -1 se estiver ausente ou inválido.
    private long lerId(HttpServletRequest request) {
        String texto = request.getParameter("id_cooperativa");
        if (texto == null) {
            return -1L;
        }
        try {
            return Long.parseLong(texto.trim());
        } catch (NumberFormatException e) {
            return -1L;
        }
    }

    // Abre o detalhe da cooperativa com seus cooperados e os totais de rotas, coletas e negociações.
    private void mostrarDetalhe(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Leitura da requisição

        long id = -1L;
        String texto = request.getParameter("id");
        if (texto != null) {
            try {
                id = Long.parseLong(texto.trim());
            } catch (NumberFormatException e) {
                id = -1L;
            }
        }
        if (id <= 0L) {
            redirecionar(request, response, "erro", "Cooperativa não encontrada.");
            return;
        }

        try {
            // Processamento da ação

            Cooperativa filtro = new Cooperativa();
            filtro.setIdCooperativa(id);
            Cooperativa coop = dao.buscarPorId(filtro);
            if (coop == null) {
                redirecionar(request, response, "erro", "Cooperativa não encontrada.");
                return;
            }
            ArrayList<Cooperado> membros = new ArrayList<>();
            for (Cooperado cooperado : cooperadoDAO.listaCooperados()) {
                if (coop.getIdCooperativa().equals(cooperado.getIdCooperativa())) {
                    membros.add(cooperado);
                }
            }

            ArrayList<Coleta> coletas = new ArrayList<>();
            for (Coleta coleta : coletaDAO.listarColetas()) {
                if (coop.getIdCooperativa().equals(coleta.getRelacionamentos().getIdCooperativa())) {
                    coletas.add(coleta);
                }
            }
            ArrayList<Negociacao> negociacoes = new ArrayList<>();
            for (Negociacao negociacao : negociacaoDAO.listarNegociacoes(null)) {
                if (coop.getIdCooperativa().equals(negociacao.getIdCooperativa())) {
                    negociacoes.add(negociacao);
                }
            }

            // Preparação da resposta

            request.setAttribute("cooperativa", coop);
            request.setAttribute("cooperados", membros);
            request.setAttribute("coletas", coletas);
            request.setAttribute("negociacoes", negociacoes);
            request.setAttribute("totalRotas", dao.contarRelacionados("rota", coop));
            request.setAttribute("totalColetas", dao.contarRelacionados("coleta", coop));
            request.setAttribute("totalNegociacoes", dao.contarRelacionados("negociacao", coop));

            // Encaminhamento

            request.getRequestDispatcher(PAGINA_DETALHE).forward(request, response);
        } catch (RuntimeException e) {
            throw new ServletException("Erro ao consultar a cooperativa.", e);
        }
    }

    // Reabre o mesmo formulário após erro, mantendo os parâmetros enviados na requisição.
    private void reabrirFormulario(HttpServletRequest request, HttpServletResponse response,
                                   Long id, String erro) throws ServletException, IOException {
        request.setAttribute("erro", erro);
        if (id != null) {
            Cooperativa filtro = new Cooperativa();
            filtro.setIdCooperativa(id);
            Cooperativa atual = dao.buscarPorId(filtro);
            if (atual == null) {
                redirecionar(request, response, "erro", "Cooperativa não encontrada.");
                return;
            }
            request.setAttribute("cooperativa", atual);
        }
        request.getRequestDispatcher(PAGINA_FORM).forward(request, response);
    }

    // Guarda a mensagem na sessão e redireciona para a lista de cooperativas.
    private void redirecionar(HttpServletRequest request, HttpServletResponse response,
                              String tipo, String texto) throws IOException {
        request.getSession().setAttribute(tipo, texto);
        response.sendRedirect(request.getContextPath() + "/cooperativa");
    }

    // Lê um parâmetro da requisição sem espaços nas pontas; devolve texto vazio se não existir.
    private String valor(HttpServletRequest request, String nome) {
        String parametro = request.getParameter(nome);
        if (parametro == null) {
            return "";
        }
        return parametro.trim();
    }

    // Informa se o texto tem algum conteúdo além de espaços.
    private boolean preenchido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

}
