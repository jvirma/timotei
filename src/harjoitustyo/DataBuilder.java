
package harjoitustyo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* Hakee ja parseaa datan */
public class DataBuilder {
    private static DataBuilder instance = null;
    private Document doc;
    private String content;
    
    protected DataBuilder(){
    }
    public static DataBuilder getInstance() {
        if (instance == null) {
            instance = new DataBuilder();
        }
        return instance;
    }
    
    /* Tarkistaa onko tietokanta tyhjä*/
    public boolean checkData() {
        Connection c = null;
        boolean check = false;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement("SELECT COUNT(*) AS 'count' FROM SmartPost");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int i = rs.getInt("count");
                if (i > 1)
                check = true;

                
            }
            rs.close();
            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return check;
    }
    /* hakee xml sisällön*/
    public void getContent() throws MalformedURLException, IOException {
    
        URL url = new URL("http://smartpost.ee/fi_apt.xml");
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        content = "";
        String line;        
        while ((line = br.readLine()) != null) {
            content += line + "\n";
        }
        
    }
    /* parseaa xml sisällön  */
    public void getData() {    
        try {
            DocumentBuilderFactory dbFactory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            
            doc = dBuilder.parse(new InputSource(new StringReader(content)));
            
            doc.getDocumentElement().normalize();
            parseCurrentData();
            
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(DataBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /* Parseaa dataa ja luo smartpostit*/
    private void parseCurrentData() {
            
        NodeList nodes = doc.getElementsByTagName("place");
        if (nodes.getLength() != 0) {
        for(int i = 0; i < nodes.getLength(); i++) {            
            Node node = nodes.item(i);
            Element e = (Element) node;
            String temp = e.getElementsByTagName("code").item(0).getTextContent();
            String temp2 = e.getElementsByTagName("city").item(0).getTextContent().toUpperCase();
            String temp3 = e.getElementsByTagName("address").item(0).getTextContent();
            String temp4 = e.getElementsByTagName("availability").item(0).getTextContent();
            String temp5 = e.getElementsByTagName("postoffice").item(0).getTextContent();
            String temp6 = e.getElementsByTagName("lat").item(0).getTextContent();
            String temp7 = e.getElementsByTagName("lng").item(0).getTextContent();
            SmartPost sp = new SmartPost(i + 1 , temp, temp2, temp3, temp4, temp5, temp6, temp7);
            }
        }
            
    }
    /* Tyhjentää tietokannan kaikki taulut*/
    public void clearData() {
            Connection c = null;
            ArrayList<String> tables = new ArrayList();
            String sql;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:database.sqlite3");
                c.setAutoCommit(false); 
                
                PreparedStatement stmt = c.prepareStatement("SELECT name FROM sqlite_master WHERE type='table';");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String table = rs.getString("name");
                    tables.add(table);
                }
            
                for (String t : tables) {
                    sql = "DELETE FROM " + t + ";";
                    PreparedStatement stmt2 = c.prepareStatement(sql);
                    stmt2.executeUpdate();
                }
                c.commit();
                c.close();
            } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
            }

        }
    
    
}
        
    
