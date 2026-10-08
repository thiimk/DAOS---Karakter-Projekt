package JDBC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

// Opgave 8a: Opret et eksamensforsøg for én studerende på én eksamensafvikling
public class Opgave8a {

    public static void main(String[] args) {

        try {
            System.out.println("Opret eksamensforsøg");
            BufferedReader inLine = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Indtast studerende-id: ");
            String studerendeId = inLine.readLine();
            System.out.print("Indtast afviklings-id: ");
            String afviklingId = inLine.readLine();
            System.out.print("Indtast karakter (-3, 0, 2, 4, 7, 10, 12) eller administrativ kode (SY, IM, IA): ");
            String bedoemmelse = inLine.readLine().trim().toUpperCase();

            Connection minConnection;
            minConnection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost\\SQLExpress;databaseName=Karakterregistrering;user=sa;password=k3gRyepV;trustServerCertificate=true;");

            String sql = "insert into Eksamensforsoeg values(?,?,?,?)"; // studerendeId, afviklingId, karakter, administrativ
            PreparedStatement prestmt = minConnection.prepareStatement(sql);
            prestmt.clearParameters();

            prestmt.setInt(1, Integer.parseInt(studerendeId.trim()));
            prestmt.setInt(2, Integer.parseInt(afviklingId.trim()));

            // Et forsøg har enten en karakter eller en administrativ kode - den anden sættes til NULL
            if (bedoemmelse.equals("SY") || bedoemmelse.equals("IM") || bedoemmelse.equals("IA")) {
                prestmt.setNull(3, Types.INTEGER);
                prestmt.setString(4, bedoemmelse);
            } else {
                prestmt.setInt(3, Integer.parseInt(bedoemmelse));
                prestmt.setNull(4, Types.CHAR);
            }

            prestmt.executeUpdate();
            System.out.println("Eksamensforsøg oprettet");

            if (prestmt != null)
                prestmt.close();
            if (minConnection != null)
                minConnection.close();
        } catch (SQLException sqlEx) {
            // Fejlkoder fra SQL Server
            if (sqlEx.getErrorCode() == 2627) {
                System.out.println("Fejl: Den studerende har allerede et forsøg på denne afvikling");
            } else if (sqlEx.getErrorCode() == 547 && sqlEx.getMessage().contains("FOREIGN KEY")) {
                System.out.println("Fejl: Den studerende eller afviklingen findes ikke");
            } else if (sqlEx.getErrorCode() == 547) {
                System.out.println("Fejl: Ugyldig karakter - lovlige værdier er -3, 0, 2, 4, 7, 10, 12");
            } else if (sqlEx.getErrorCode() == 50001) {
                System.out.println("Fejl: Den studerende har allerede bestået denne eksamen");
            } else {
                System.out.println("Error: " + sqlEx.getErrorCode());
                System.out.println("Error: " + sqlEx.getMessage());
            }
        } catch (NumberFormatException ex) {
            System.out.println("Fejl: id og karakter skal være tal (eller SY, IM, IA)");
        } catch (Exception ex) {
            System.out.println("fejl: " + ex.getMessage());
        }
    }
}