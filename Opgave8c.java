package JDBC;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Opgave 8c: Vis de studerende der har deltaget i en given eksamen i en given termin
public class Opgave8c {

    public static void main(String[] args) {

        try {
            System.out.println("Vis deltagere i en eksamen");
            BufferedReader inLine = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Indtast eksamensnavn: ");
            String eksamen = inLine.readLine();
            System.out.print("Indtast termin (fx S2025): ");
            String termin = inLine.readLine();

            Connection minConnection;
            minConnection = DriverManager.getConnection(
                    "jdbc:sqlserver://localhost\\SQLExpress;databaseName=Karakterregistrering;user=sa;password=k3gRyepV;trustServerCertificate=true;");

            // Eksamensnavne er kun unikke inden for en uddannelse, derfor vises uddannelsen også
            String sql = "select u.navn, s.studerendeId, s.navn, f.karakter, f.administrativ "
                    + "from Eksamen e "
                    + "join Uddannelse u on u.uddannelseId = e.uddannelseId "
                    + "join Eksamensafvikling a on a.eksamenId = e.eksamenId "
                    + "join Eksamensforsoeg f on f.afviklingId = a.afviklingId "
                    + "join Studerende s on s.studerendeId = f.studerendeId "
                    + "where e.navn = ? and a.termin = ? "
                    + "order by u.navn, s.navn";
            PreparedStatement prestmt = minConnection.prepareStatement(sql);
            prestmt.clearParameters();

            prestmt.setString(1, eksamen.trim());
            prestmt.setString(2, termin.trim().toUpperCase());

            ResultSet res = prestmt.executeQuery();
            boolean fundet = false;
            System.out.println("Uddannelse\t\tId\tNavn\tBedømmelse");
            while (res.next()) {
                fundet = true;
                // Karakteren er NULL, hvis den studerende har fået en administrativ kode
                String bedoemmelse = res.getString(4);
                if (bedoemmelse == null)
                    bedoemmelse = res.getString(5);
                System.out.println(res.getString(1) + "\t\t" + res.getString(2) + "\t" + res.getString(3) + "\t" + bedoemmelse);
            }
            if (!fundet)
                System.out.println("Ingen studerende har deltaget i " + eksamen + " i termin " + termin);

            if (res != null)
                res.close();
            if (prestmt != null)
                prestmt.close();
            if (minConnection != null)
                minConnection.close();
        } catch (SQLException sqlEx) {
            System.out.println("Error: " + sqlEx.getErrorCode());
            System.out.println("Error: " + sqlEx.getMessage());
        } catch (Exception ex) {
            System.out.println("fejl: " + ex.getMessage());
        }
    }
}