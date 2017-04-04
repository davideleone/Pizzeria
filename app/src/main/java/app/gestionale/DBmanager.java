package app.gestionale;


import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBmanager {
    private static Connection conn = null;

    public static Connection getConnessione() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            e.toString();
        }

        String url = "jdbc:postgresql://ec2-54-247-126-127.eu-west-1.compute.amazonaws.com:5432/";
        String dbName = "dbshcp4l2q8pia";
        String userName = "wwjcblmpjhnown";
        String password = "SHRa3gH4-sfcqfMnfXWvepH6kF";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            conn = DriverManager.getConnection(url + dbName + "?sslmode=require", userName, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.print(e.toString());
        }

        return conn;
    }


    public static void chiudiConnessione(Connection conn) {
        try {
            conn.close();
        } catch (SQLException e) {
        }
    }

    public static List<HashMap<String, Object>> selectQuery(String query, String... parametri) {
        List<HashMap<String, Object>> listaRisultati = new ArrayList<HashMap<String, Object>>();
        Connection conn = getConnessione();
        ResultSet rs = null;
        ResultSetMetaData metaData = null;

        try (PreparedStatement pst = conn.prepareStatement(query)) {
            // Inserimento parametri
            for (int i = 0; i < parametri.length; i++) {
                if (Funzioni.isTimestampCorretto(parametri[i].toString())) {
                    pst.setTimestamp(i + 1, Timestamp.valueOf(parametri[i].toString()));
                } else if (Funzioni.isBoolean(parametri[i].toString())) {
                    pst.setBoolean(i + 1, Boolean.getBoolean(parametri[i].toString()));
                } else if (Funzioni.isDouble(parametri[i].toString())) {
                    pst.setDouble(i + 1, Double.parseDouble(parametri[i].toString()));
                } else if (Funzioni.isInteger(parametri[i].toString())) {
                    pst.setInt(i + 1, Integer.parseInt(parametri[i].toString()));
                } else {
                    pst.setString(i + 1, parametri[i].toString());
                }
            }

            rs = pst.executeQuery();

            metaData = rs.getMetaData();
            int colonne = metaData.getColumnCount();

            while (rs.next()) {
                HashMap<String, Object> row = new HashMap<String, Object>(colonne);
                for (int i = 1; i <= colonne; i++) {
                    String isNull = (rs.getObject(i) == null ? "" : rs.getObject(i).toString());
                    row.put(metaData.getColumnName(i), isNull);
                }
                listaRisultati.add(row);
            }

        } catch (SQLException e) {
            System.out.println("Errore nell'esecuzione della query " + query);
            System.out.println("Errore = " + e.getMessage());
        }
        chiudiConnessione(conn);
        return listaRisultati;
    }

    public static int updateQuery(String query, Boolean ritornaIdGenerato, Object... parametri) {
        Connection conn = getConnessione();
        try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Inserimento parametri
            for (int i = 0; i < parametri.length; i++) {
                if (Funzioni.isTimestampCorretto(parametri[i].toString())) {
                    pst.setTimestamp(i + 1, Timestamp.valueOf(parametri[i].toString()));
                } else if (Funzioni.isBoolean(parametri[i].toString())) {
                    pst.setBoolean(i + 1, Boolean.getBoolean(parametri[i].toString()));
                } else if (Funzioni.isDouble(parametri[i].toString())) {
                    pst.setDouble(i + 1, Double.parseDouble(parametri[i].toString()));
                } else if (Funzioni.isInteger(parametri[i].toString())) {
                    pst.setInt(i + 1, Integer.parseInt(parametri[i].toString()));
                } else {
                    pst.setString(i + 1, parametri[i].toString());
                }
            }
            int ris = pst.executeUpdate();

            if (ritornaIdGenerato) {
                ResultSet rs = pst.getGeneratedKeys();
                rs.next();
                return (rs.getInt(1));
            } else {
                return ris;
            }
        } catch (SQLException e) {
            System.out.println("Errore nell'esecuzione della query " + query);
            System.out.println("Errore = " + e.getMessage());
        }

        chiudiConnessione(conn);

        return -1;
    }

}
