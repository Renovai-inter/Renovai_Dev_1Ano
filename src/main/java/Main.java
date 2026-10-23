import util.ConexaoBD;

import java.sql.Connection;
import java.sql.SQLException;

// Classe Main para verificar a conexão com o banco sem usar Servlets.
public class Main {

    // Abre a conexão configurada e informa se ela está disponível.
    public static void main(String[] args) {
        if (System.getenv("DB_URL") == null || System.getenv("DB_USER") == null || System.getenv("DB_PASSWORD") == null) {
            System.out.println("Teste de conexão indisponível: configure DB_URL, DB_USER e DB_PASSWORD.");
            return;
        }

        try (Connection conexao = ConexaoBD.conectar()) {
            if (conexao.isClosed()) {
                System.out.println("Falha no teste de conexão: a conexão está fechada.");
            } else {
                System.out.println("Conexão com o banco estabelecida com sucesso.");
            }
        } catch (SQLException | RuntimeException e) {
            System.out.println("Falha no teste de conexão com o banco.");
            Throwable causa = e;
            if (e.getCause() != null) {
                causa = e.getCause();
            }
            Throwable origem = causa;
            while (origem.getCause() != null) {
                origem = origem.getCause();
            }
            if (causa instanceof SQLException) {
                SQLException erroSql = (SQLException) causa;
                System.out.println("Código SQLSTATE: " + erroSql.getSQLState());
            }
            System.out.println("Tipo de falha: " + origem.getClass().getSimpleName());
        }
    }
}
