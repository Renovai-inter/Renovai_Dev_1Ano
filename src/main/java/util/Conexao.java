
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // DECLARACAO

    // Variaveis estaticas usadas para estabelecer a conexao com o banco
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    // METODOS

    // Metodo para estabelecer conexao com o banco de dados
    public Connection conectar() {

        try {

            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {

            throw new RuntimeException(e);

        }
    }

    // Metodo para desconectar do banco de dados
    public void desconectar(java.sql.Connection conn) {

        if (conn != null) {

            try {

                conn.close();

            } catch (SQLException e) {

                throw new RuntimeException(e);

            }

        }

    }

}

