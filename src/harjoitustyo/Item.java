

package harjoitustyo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Item {
    
    public static ArrayList<String> ItemNames = new ArrayList();
    
    /* Item rakentaja */
    public Item(int id, String name, int weight, int size, int breakable) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);            

            PreparedStatement stmt = c.prepareStatement("INSERT INTO Item VALUES(?, ?, ?, ?, ?);");
            stmt.setInt(1, id);
            stmt.setString(2, name);
            ItemNames.add(name);
            stmt.setInt(3, weight);
            stmt.setInt(4, size);
            stmt.setInt(5, breakable);
            
            stmt.executeUpdate();
            stmt.close();
            
            c.commit();
            c.close();
            Log l = new Log("Luotiin uusi tavara [" + name + "]");
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }                    
    }
    
    /* Poistaa Itemin nimen perusteella */
    public static void removeItem(String name) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("DELETE FROM Item WHERE Name = ?;");
            stmt.setString(1, name);
            stmt.executeUpdate();
            stmt.close();
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    /* Listaa kaikki tavarat */
    public static void getAllItemsToList() {
        ItemNames.clear();
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT Name FROM Item");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("Name");
                ItemNames.add(name);
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    
    /* Hakee tavaran nimen ID:n perusteella */
    public static String getItemName(int itemid) {
        String name = "";
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT Name FROM Item WHERE ItemID = ?;");
            stmt.setInt(1, itemid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                name = rs.getString("Name");
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return name;
    }
    
    /* Kertoo onko tavara rikottavissa */
    public static int checkIfBreakable(int itemid) {
        int breakable = 0;
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT Breakable FROM Item WHERE ItemID = ?;");
            stmt.setInt(1, itemid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                breakable = rs.getInt("Breakable");
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return breakable;
    }
    /* Antaa esineen painon */
    public static double getWeight(int itemid) {
        double weight = -1;
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT Weight FROM Item WHERE ItemID = ?;");
            stmt.setInt(1, itemid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                weight = rs.getDouble("Weight");
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return weight;
    }
    /* Antaa esineen painon */
    public static int getItemID(String name) {
        int ID = -1;
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT ItemID FROM Item WHERE Name = ?;");
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ID = rs.getInt("ItemID");
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return ID;
    }
}
