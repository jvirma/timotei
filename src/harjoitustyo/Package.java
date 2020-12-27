
package harjoitustyo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/* Paketti luokka, Pakettien luonti ja tiedonhaku listoihin tietokannasta ja poisto */

public abstract class Package {
    public static ArrayList<String> Packages = new ArrayList(); 
    public static ArrayList<String> Locations = new ArrayList();
    public static ArrayList<String> PackagesInfo = new ArrayList();
    public static ArrayList DeliveredPackageInfos = new ArrayList();
    public static double distanceTotal;
    public static int brokenPackage;
    
    
    
    /* Luo paketti luokat ja rajoitukset */
    public static void createPackageClass(int pClass, int speed, double weightmax, double sizemax, double distancemax) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);            

            PreparedStatement stmt = c.prepareStatement("INSERT INTO Package VALUES(?, ?, ?, ?, ?);");
            stmt.setInt(1, pClass);
            stmt.setInt(2, speed);
            stmt.setDouble(3, weightmax);
            stmt.setDouble(4, sizemax);
            stmt.setDouble(5, distancemax);
            
            stmt.executeUpdate();
            stmt.close();
            
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        } 
    }
    
    /* Listaa kaikki paketit tietokannasta */
    public static void getAllPackagesToList() {
        Packages.clear();
        PackagesInfo.clear();
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT Item.Name, "
                    + "PackageCreated.StartID, PackageCreated.DestinationID, "
                    + "PackageCreated.Distance, PackageCreated.Class, PackageCreated.PackageID"
                    + " FROM Item INNER JOIN PackageCreated ON Item.ItemID"
                    + " = PackageCreated.ItemID;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int ID = rs.getInt("PackageID");
                int packageClass = rs.getInt("Class");
                String name = rs.getString("Name");
                Packages.add("ID: " + ID + " / " + packageClass + ". luokka / " + name);
                int startID = rs.getInt("StartID");
                String sInfo = SmartPost.getPostInfo(startID);
                int destinationID = rs.getInt("DestinationID");
                String eInfo = SmartPost.getPostInfo(destinationID);
                double dist = rs.getDouble("Distance");
                
                PackagesInfo.add(sInfo + " / " + eInfo + " / " + dist + " km");
                
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    /* Hakee sijainnit paketin ID:n perusteella*/
    public static int getLocations(int packID) {
        int pClass = 0;
        SmartPost.openingTimes.clear();
        Package.Locations.clear();
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT SmartPost.Name, SmartPost.OpeningTime, SmartPost.Latitude,"
                    + " SmartPost.Longitude, PackageCreated.Class FROM PackageCreated "
                    + "INNER JOIN SmartPost ON SmartPost.ID = PackageCreated.StartID WHERE PackageCreated.PackageID = ?;");
            stmt.setInt(1, packID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String lat = rs.getString("Latitude");
                String lng = rs.getString("Longitude");
                String name = rs.getString("Name");
                String info = rs.getString("OpeningTime");
                SmartPost.openingTimes.add(name + ": " + info);
                Package.Locations.add(lat);
                Package.Locations.add(lng);
                pClass = rs.getInt("Class");
            }
            
            PreparedStatement stmt2 = c.prepareStatement("SELECT SmartPost.Name, SmartPost.OpeningTime, SmartPost.Latitude,"
                    + " SmartPost.Longitude FROM PackageCreated INNER JOIN SmartPost "
                    + "ON SmartPost.ID = PackageCreated.DestinationID WHERE PackageCreated.PackageID = ?");
            stmt2.setInt(1, packID);
            ResultSet rs2 = stmt2.executeQuery();
            while (rs2.next()) {
                String lat = rs2.getString("Latitude");
                String lng = rs2.getString("Longitude");
                String name = rs2.getString("Name");
                String info = rs2.getString("OpeningTime");
                SmartPost.openingTimes.add(name + ": " + info);
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
        return pClass;
    }
    /* Hakee luokan limiitit */
    public static ArrayList getLimits(int pClass) {
        ArrayList limits = new ArrayList();
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT WeightMax, SizeMax, DistanceMax FROM Package WHERE Class = ?;");
            stmt.setInt(1, pClass);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String weightmax = rs.getString("WeightMax");
                String sizemax = rs.getString("SizeMax");
                Double distancemax = rs.getDouble("DistanceMax");
                limits.add(weightmax);
                limits.add(sizemax);
                limits.add(distancemax);
            }
            
            stmt.close();
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return limits;
    
    }
    /* Poistaa paketin ID:n perusteella*/
    public static void removePackage(int id) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("DELETE FROM PackageCreated WHERE PackageID = ?;");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    
    }
    /* Lisää paketin toimitettuihin */
    public static void addToDelivered(int packid, int pClass) {
        Connection c = null;
        int itemid = -1;
        int startID = -1;
        int destID = -1;
        Double distance = -1.0;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement("SELECT ItemID, StartID, DestinationID, Distance FROM PackageCreated WHERE PackageID = ?;");
            stmt.setInt(1, packid);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                itemid = rs.getInt("ItemID");
                startID = rs.getInt("StartID");
                destID = rs.getInt("DestinationID");
                distance = rs.getDouble("Distance");
            }
            PreparedStatement stmt2 = c.prepareStatement("INSERT INTO DeliveredPackage VALUES(?, ?, ?, ?, ?, ?, ?);");
            int id = Package.getLastDeliveredPackageID() + 1;
            stmt2.setInt(1, id);
            stmt2.setInt(2, pClass);
            stmt2.setInt(3, itemid);
            stmt2.setInt(4, startID);
            stmt2.setInt(5, destID);
            stmt2.setDouble(6, distance);
            int breakable = Item.checkIfBreakable(itemid);
            double weight = Item.getWeight(itemid);
            
            int broken = 0;
            if (pClass == 1 && breakable == 1)
                broken = 1;
            if (pClass == 3 && breakable == 1 && weight < 5)
                broken = 1;
            
            stmt2.setInt(7, broken);
            stmt2.executeUpdate();
            stmt2.close();
            
            stmt.close();
            
            c.commit();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }
    
    /* Hakee viimeisimmän paketin ID:n*/
    public static int getLastPackageID() {
        Connection c = null;
        int lastID = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT PackageID from PackageCreated ORDER BY PackageID DESC LIMIT 1;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lastID = rs.getInt("PackageID");
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
    /* Hakee viimeisimmän toimitetun paketin ID:n*/
    public static int getLastDeliveredPackageID() {
        Connection c = null;
        int lastID = 0;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT PackageID from DeliveredPackage ORDER BY PackageID DESC LIMIT 1;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lastID = rs.getInt("PackageID");
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
    /* Hakee toimitetut paketit*/
    public static void getDeliveredPackageInfos() {
        Package.DeliveredPackageInfos.clear();
        brokenPackage = 0;
        distanceTotal = 0;
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            
            PreparedStatement stmt = c.prepareStatement("SELECT ItemID, PackageID,"
                    + " Class, StartID, DestinationID, Distance, Broken FROM DeliveredPackage;");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("PackageID");
                int pclass = rs.getInt("Class");
                int itemid = rs.getInt("ItemID");
                int startID = rs.getInt("StartID");
                String sInfo = SmartPost.getPostInfo(startID);
                int destID = rs.getInt("DestinationID");
                String dInfo = SmartPost.getPostInfo(destID);
                double distance = rs.getDouble("Distance");
                distanceTotal = distanceTotal + distance;
                int broken = rs.getInt("Broken");
                String name = Item.getItemName(itemid);
                String temp = "Ei";
                if (broken == 1) {
                    temp = "Kyllä";
                    brokenPackage++;
                }
                Package.DeliveredPackageInfos.add("ID: "+id+" / "+pclass+
                        ". luokka / "+name+" / "+sInfo+" / "+dInfo+" / "+distance+" km / "+temp+"\n");
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
/* Eka luokan rakentaja*/
class FirstClass extends Package {

    protected FirstClass(int id, int itemID, int startID, int destID, double distance) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);            

            PreparedStatement stmt = c.prepareStatement("INSERT INTO PackageCreated VALUES(?, ?, ?, ?, ?, ?);");
            stmt.setInt(1, id);
            stmt.setInt(2, 1);
            stmt.setInt(3, itemID);
            stmt.setInt(4, startID);
            stmt.setInt(5, destID);
            stmt.setDouble(6, distance);
            
            stmt.executeUpdate();
            stmt.close();
            
            c.commit();
            c.close();
            String name = Item.getItemName(itemID);
            Log l = new Log("Luotiin uusi paketti [ID: "+id+" / 1. luokka / " +name+ "]");
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }        
    }
}
/* Toka luokan rakentaja*/
class SecondClass extends Package {

    protected SecondClass(int id, int itemID, int startID, int destID, double distance) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);            

            PreparedStatement stmt = c.prepareStatement("INSERT INTO PackageCreated VALUES(?, ?, ?, ?, ?, ?);");
            stmt.setInt(1, id);
            stmt.setInt(2, 2);
            stmt.setInt(3, itemID);
            stmt.setInt(4, startID);
            stmt.setInt(5, destID);
            stmt.setDouble(6, distance);
            
            stmt.executeUpdate();
            stmt.close();
            
            c.commit();
            c.close();
            String name = Item.getItemName(itemID);
            Log l = new Log("Luotiin uusi paketti [ID: "+id+" / 2. luokka / " +name+ "]");
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }        
    }
}
/* Kolmos luokan rakentaja*/
class ThirdClass extends Package {

    protected ThirdClass(int id, int itemID, int startID, int destID, double distance) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);            

            PreparedStatement stmt = c.prepareStatement("INSERT INTO PackageCreated VALUES(?, ?, ?, ?, ?, ?);");
            stmt.setInt(1, id);
            stmt.setInt(2, 3);
            stmt.setInt(3, itemID);
            stmt.setInt(4, startID);
            stmt.setInt(5, destID);
            stmt.setDouble(6, distance);
            
            stmt.executeUpdate();
            stmt.close();
            
            c.commit();
            c.close();
            String name = Item.getItemName(itemID);
            Log l = new Log("Luotiin uusi paketti [ID: "+id+" / 3. luokka / " +name+ "]");
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }        
    }
}
