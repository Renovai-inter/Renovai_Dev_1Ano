
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // DECLARACAO

    // Variaveis estaticas usadas para estabelecer a conexao com o banco
    private static final String URL = System.getenv("jdbc:postgresql://pg-2a8ff530-renovai-3741.c.aivencloud.com:18259/defaultdb?ssl=require&user=avnadmin&password=AVNS_boJSmYDug3Qhb3iq6XT");
    private static final String USER = System.getenv("avnadmin");
    private static final String PASSWORD = System.getenv("AVNS_boJSmYDug3Qhb3iq6XT");



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

