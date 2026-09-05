
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // DECLARACAO

    // Variaveis estaticas usadas para estabelecer a conexao com o banco
    private static final String URL = "http://2a8ff530-renovai-3741.c.aivencloud.com:18259/defaultdb?user=avnadmin&password=SUA_SENHA&sslmode=require";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "SUA_SENHA";

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

