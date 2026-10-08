package JDBC;

import java.sql.*;

// Samler forbindelsesoplysningerne ét sted, så Opgave8a, 8b og 8c kan genbruge dem
public class DBConnection {
    private static final String URL =
            "jdbc:sqlserver://localhost\\SQLExpress;databaseName=Karakterregistrering;"
                    + "user=sa;password=k3gRyepV;trustServerCertificate=true;";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}


