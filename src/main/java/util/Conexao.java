package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = "jdbc:postgresql://pg-27a0b4e6-institutojef-c8b8.f.aivencloud.com:26787/defaultdb?ssl=require&user=avnadmin&password=AVNS_Z8SXBKA_tLRt9haYRNs";

    private static final String USER = "avnadmin";

    private static final String PASSWORD = "AVNS_Z8SXBKA_tLRt9haYRNs";

    public Connection conectar() {

        try {
            System.out.println("Conectado com sucesso!");
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {

            throw new RuntimeException(e);

        }
    }

    public void disconectar(java.sql.Connection conn) {
        if (conn != null) {
            try { conn.close();
                System.out.println("Desconectado com sucesso!");} catch (SQLException e) { e.printStackTrace(); }
        }
    }
}

