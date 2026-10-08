package JDBC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Opgave 8b: Opret en eksamensafvikling
public class Opgave8b {

    public static void main(String[] args) {

        try {
            System.out.println("Opret eksamensafvikling");
            BufferedReader inLine = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Indtast termin (fx S2026 eller V2027): ");
            String termin = inLine.readLine();
            System.out.print("Indtast startdato (åååå-mm-dd): ");
            String startdato = inLine.readLine();
            System.out.print("Indtast slutdato (åååå-mm-dd): ");
            String slutdato = inLine.readLine();
            System.out.print("Indtast eksamens-id: ");
            String eksamenId = inLine.readLine();

            Connection minConnection;
            minConnection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost\\SQLExpress;databaseName=Karakterregistrering;user=sa;password=k3gRyepV;trustServerCertificate=true;");

            // afviklingId er ikke IDENTITY, så det næste ledige id findes med MAX + 1
            String sql = "insert into Eksamensafvikling "
                    + "select isnull(max(afviklingId), 0) + 1, ?, ?, ?, ? from Eksamensafvikling";
            PreparedStatement prestmt = minConnection.prepareStatement(sql);
            prestmt.clearParameters();

            prestmt.setString(1, termin.trim().toUpperCase());
            prestmt.setString(2, startdato.trim()); // SQL Server omdanner selv teksten til en dato
            prestmt.setString(3, slutdato.trim());
            prestmt.setInt(4, Integer.parseInt(eksamenId.trim()));

            prestmt.executeUpdate();
            System.out.println("Eksamensafvikling oprettet");

            if (prestmt != null)
                prestmt.close();
            if (minConnection != null)
                minConnection.close();
        } catch (SQLException sqlEx) {
            // Fejlkoder fra SQL Server
            if (sqlEx.getErrorCode() == 547 && sqlEx.getMessage().contains("FOREIGN KEY")) {
                System.out.println("Fejl: Der findes ingen eksamen med det id");
            } else if (sqlEx.getErrorCode() == 547) {
                System.out.println("Fejl: Slutdato skal være samme dag som eller efter startdato, og termin skal have formen S2026 eller V2026");
            } else if (sqlEx.getErrorCode() == 241) {
                System.out.println("Fejl: Datoen skal skrives som åååå-mm-dd, fx 2026-06-15");
            } else if (sqlEx.getErrorCode() == 2628 || sqlEx.getErrorCode() == 8152) {
                System.out.println("Fejl: Termin må højst være 5 tegn, fx S2026");
            } else {
                System.out.println("Error: " + sqlEx.getErrorCode());
                System.out.println("Error: " + sqlEx.getMessage());
            }
        } catch (NumberFormatException ex) {
            System.out.println("Fejl: Eksamens-id skal være et tal");
        } catch (Exception ex) {
            System.out.println("fejl: " + ex.getMessage());
        }
    }
}