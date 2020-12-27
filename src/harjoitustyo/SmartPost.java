
package harjoitustyo;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

/* SmartPost luokka, SmartPostien luonti, tiedonhaku listoihin tietokannasta ja poisto */

public class SmartPost {
    public static ArrayList<String> citys = new ArrayList();
    public static ArrayList<String> Locations = new ArrayList();
    public static ArrayList<String> addresses = new ArrayList();
    public static ArrayList<String> posts = new ArrayList();
    public static ArrayList<String> openingTimes = new ArrayList();
    public static ArrayList<String> postcodes = new ArrayList();
    
    
    /* SmartPost rakentaja */
    public SmartPost(int id, String code, String city, String address, String openingtime, 
            String name, String lat, String lng) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);            

            PreparedStatement stmt = c.prepareStatement("INSERT INTO SmartPost VALUES(?, ?, ?, ?, ?, ?, ?);");
            stmt.setInt(1, id);
            stmt.setString(2, name);
            stmt.setString(3, lat);
            stmt.setString(4, lng);
            stmt.setString(5, address);
            stmt.setString(6, code);
            stmt.setString(7, openingtime);
            /* Tarkistaa onko Postitoimipaikka jo lisätty, jos ei, niin lisää */
            if(!postcodes.contains(code)) {              
                PreparedStatement stmt2 = c.prepareStatement("INSERT INTO PostOffice VALUES(?, ?);");
                stmt2.setString(1, code);
                stmt2.setString(2, city);
                stmt2.executeUpdate();
                stmt2.close();
                if (!citys.contains(city.toUpperCase())) {
                    citys.add(city.toUpperCase());
                }
            }
            Collections.sort(SmartPost.citys);
            postcodes.add(code);
            stmt.executeUpdate();
            stmt.close();
            
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        } 
        
    }
    
    /* Hakee haetun kaupungin SmartPost osoitteet ja aukiolotiedot */
    public static void getLatLngAndOpeningTime(String city) {
        
        ArrayList<String> postcodes = new ArrayList();

        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement("SELECT Postcode FROM PostOffice WHERE City = ?;");
            stmt.setString(1, city);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String postcode = rs.getString("Postcode");
                postcodes.add(postcode);
            }
            
            for(String p : postcodes) {
                PreparedStatement stmt2 = c.prepareStatement("SELECT Name, Latitude, Longitude, OpeningTime FROM SmartPost WHERE Postcode = ?;");
                stmt2.setString(1, p);
                ResultSet rs2 = stmt2.executeQuery();
                while (rs2.next()) {
                    String lat = rs2.getString("Latitude");
                    String lng = rs2.getString("Longitude");
                    Locations.add(lat);
                    Locations.add(lng);
                    String name = rs2.getString("Name");
                    String openingTime = rs2.getString("OpeningTime");

                    openingTimes.add(name + ": " + openingTime);
                }
                rs2.close();
                stmt2.close();
            }
            rs.close();
            stmt.close();

            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        
    }
    /* Palauttaa kahden SmartPostID:n perusteella etäisyyden */
    public static double getDistance(int startPostID, int destPostID) {
        double distance = 0;
        Package.Locations.clear();
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT Latitude, Longitude, ID FROM SmartPost WHERE ID = ?");
            stmt.setInt(1, startPostID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String lat = rs.getString("Latitude");
                String lng = rs.getString("Longitude");
                Package.Locations.add(lat);
                Package.Locations.add(lng);
            }
            
            PreparedStatement stmt2 = c.prepareStatement("SELECT Latitude, Longitude, ID FROM SmartPost WHERE ID = ?");
            stmt2.setInt(1, destPostID);
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                String lat = rs2.getString("Latitude");
                String lng = rs2.getString("Longitude");
                Package.Locations.add(lat);
                Package.Locations.add(lng);
            }            
            rs.close();
            stmt.close();
            rs2.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        distance = (double)FXMLWebViewController.webview.getEngine().executeScript("document.measurePath("+ Package.Locations +");");
        return distance;
    }
    
    /* Hakee haetun kaupungin SmartPostit listaan */
    public static void getSmartPosts(String city) {
        posts.clear();
        Connection c = null;
        ArrayList<String> postcodes = new ArrayList();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT Postcode FROM PostOffice WHERE City = ?;");
            stmt.setString(1, city);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String postcode = rs.getString("Postcode");
                postcodes.add(postcode);
            }
            
            for(String p : postcodes) {
                PreparedStatement stmt2 = c.prepareStatement("SELECT Name FROM SmartPost WHERE Postcode = ?;");
                stmt2.setString(1, p);
                ResultSet rs2 = stmt2.executeQuery();
                while (rs2.next()) {
                    String name = rs2.getString("Name");
                    posts.add(name);
                }
                rs2.close();
                stmt2.close();
            }
            rs.close();
            stmt.close();

            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }       
    }
    /* Hakee haetun kaupungin ID:n nimen mukaan */
    public static int getPostID(String postname) {
        Connection c = null;
        int postID = -1;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT ID, Name FROM SmartPost WHERE Name = ?;");
            stmt.setString(1, postname);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                postID = rs.getInt("ID");
            }
            rs.close();
            stmt.close();
            c.close();
            
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return postID;
    }
    /* Hakee kaikki kaupungit listaan */
    public static void getAllCitysToList() {
        citys.clear();
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT City FROM PostOffice;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String city = rs.getString("City");
                if (!citys.contains(city.toUpperCase())) {
                    citys.add(city.toUpperCase());
                }
            }
            rs.close();
            stmt.close();
            c.close();
        Collections.sort(SmartPost.citys);    
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }       
    }
    /* Hakee viimeisen SmartPostinID:n ja palauttaa sen*/
    public static int getLastSmartPostID() {
        Connection c = null;
        int lastID = -1;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT ID from SmartPost ORDER BY ID DESC LIMIT 1;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lastID = rs.getInt("ID");
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
    /* Poistaa Smartpostin ID:n perusteella*/
    public static void removeSmartPost(int ID) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("DELETE FROM SmartPost WHERE ID = ?;");
            stmt.setInt(1, ID);
            stmt.executeUpdate();
            stmt.close();
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }   
    }
    /* Poistaa PostCoden jos se ei ole muissa kaupungeissa */
    public static void removePostcode(int ID) {
        Connection c = null;
        String pcode = null;
        int count = 0;
        
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*), ID, Postcode"
                    + " FROM SmartPost WHERE ID = ?;");
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pcode = rs.getString("Postcode");
                count = rs.getInt("COUNT(*)");
            }
            if (count == 1) {
                PreparedStatement stmt2 = c.prepareStatement("DELETE FROM PostOffice WHERE Postcode = ?;");
                stmt2.setString(1, pcode);
                stmt2.executeUpdate();
                stmt2.close();
            }
            
            rs.close();
            stmt.close();
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }   
    }
    
    /* Hakee SmartPostin osoitetietoja ja palauttaa sen tekstinä*/
    public static String getPostInfo(int ID) {
        ArrayList<String> postcodes = new ArrayList();
        Connection c = null;
        String info = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            String road = "";
            String city = "";
            
            PreparedStatement stmt = c.prepareStatement("SELECT Road, Postcode FROM SmartPost WHERE ID = ?;");
            stmt.setInt(1, ID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                road = rs.getString("Road");
                String postcode = rs.getString("Postcode");
                
                PreparedStatement stmt2 = c.prepareStatement("SELECT City FROM PostOffice WHERE Postcode = ?;");
                stmt2.setString(1, postcode);
                ResultSet rs2 = stmt2.executeQuery();
                while (rs2.next()) {
                    city = rs2.getString("City");
                    info = road + ", " + postcode + " " + city;
                }
                rs2.close();
                stmt2.close();
            }
            rs.close();
            stmt.close();
            c.close();                        
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return info;                
    }   
}