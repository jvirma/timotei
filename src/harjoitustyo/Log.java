
package harjoitustyo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/* Pitää lokia toiminnoista */
public class Log {
    public static ArrayList<String> Logs = new ArrayList();
    public static String tempLog = "";
    /* Log rakentaja*/
    public Log (String action) {
    Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            

            PreparedStatement stmt = c.prepareStatement("INSERT INTO Log VALUES(?, ?, ?);");
            int id = getLastLog() + 1;
            String time = FXMLWebViewController.getToday();
            stmt.setInt(1, id);
            stmt.setString(2, time);
            stmt.setString(3, action);
            stmt.executeUpdate();
            stmt.close();
            tempLog = tempLog + time + " -- " + action + "\n";
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    /* Hakee viimeisen lokin id:n */
    protected int getLastLog() {
        Connection c = null;
        int lastID = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT LogID FROM Log ORDER BY LogID DESC LIMIT 1;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lastID = rs.getInt("LogID");
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return lastID; 
    }
    /* Hakee kaikki lokit*/
    public static void getLogs() {
        Logs.clear();
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM Log;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String time = rs.getString("Time");
                String action = rs.getString("Action");
                Logs.add(time + " -- " + action);
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    } 
}
