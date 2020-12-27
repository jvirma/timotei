
package harjoitustyo;

import com.sun.javafx.stage.StageHelper;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.xml.sax.SAXException;

/*WebView ikkuna jossa kartat ja päävalikko */

public class FXMLWebViewController implements Initializable {
    @FXML
    public static WebView webview;
    @FXML
    private static ComboBox<String> comboBox;
    @FXML
    public static ComboBox<String> comboPackages;
    public static TextArea ActionLogText;
    @FXML
    public static TextArea PackagesTextArea;
    @FXML
    public static Text PackageCounterText;
    @FXML
    private Pane startMenu;
    @FXML
    private Text DeliveredPackageCountText;
    @FXML
    private TextArea DeliveredPackagesTextArea;
    @FXML
    private ListView<String> listViewLog;
    @FXML
    private Text timer;
    public int t = 0;
    @FXML
    private Text textStatistic;
    @FXML
    private Text textTotalKms;
    

    /* Tarkistaa onko tietokanta tyhjä ja päivittää sen ilman menu kyselyä, aloittaa timerin */
    @Override
    public void initialize(URL url, ResourceBundle rb)  {
        Timeline oneSecond = new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                t++;
                timer.setText(String.valueOf(t) + " sekuntia");
            }
        }));
        oneSecond.setCycleCount(Timeline.INDEFINITE);
        oneSecond.play();
        
        DataBuilder db = DataBuilder.getInstance();
        if (!db.checkData()) {
            startMenu.setVisible(false);
            webview.getEngine().load(getClass().getResource("index.html").toExternalForm());
            try {
                db.getContent();
                db.getData();
            } catch (IOException ex) {
                Logger.getLogger(FXMLWebViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
            SmartPost.getAllCitysToList();
            for(int i = 0; i < SmartPost.citys.size(); i++) {
            comboBox.getItems().add(SmartPost.citys.get(i));
            } 
            Item i1 = new Item(1, "Muumimuki kokoelma", 2, 12500, 1);
            Item i2 = new Item(2, "Virallinen EM-kisapallo", 2, 10000, 0);
            Item i3 = new Item(3, "Ikea-sohva", 10, 25000, 0);
            Item i4 = new Item(4, "Pesäpallomaila", 1, 11111, 0);
            Package.createPackageClass(1, 40, 10, 5000, 150);
            Package.createPackageClass(2, 10, 10, 3000, 10000);
            Package.createPackageClass(3, 20, 10, 100000, 350);
        }
        Log l = new Log("TIMOTEI käynnistyi");
    }

    /* Piirtää Markerin listoja hyödyntäen */
    @FXML
    private void drawMarker(ActionEvent event) throws IOException, SAXException {
        if(!comboBox.getSelectionModel().isEmpty()) {
            SmartPost.openingTimes.clear();
            SmartPost.Locations.clear();
            String city = comboBox.getSelectionModel().getSelectedItem();
            SmartPost.getLatLngAndOpeningTime(city);
            int j = 0;
            for (int i = 0; i < SmartPost.Locations.size(); i = i + 2) {
                webview.getEngine().executeScript("document.goToLoca('" + SmartPost.Locations.get(i) + "', '" + SmartPost.Locations.get(i+1) + "', '" + SmartPost.openingTimes.get(j) + "', 'orange');");
                j++;
            }
            Log l = new Log("Kaupungin " +city+ " SmartPostit piirretty kartalle");
        }
    }
        
    /* Avaa paketin luonti ikkunan */
    @FXML
    private void createPackage(ActionEvent event) {
        if (StageHelper.getStages().size() <= 1) {
            try {
                Stage packageView = new Stage();
                Parent page = FXMLLoader.load(getClass().getResource("FXMLPackage.fxml"));
                Scene scene = new Scene(page);
                scene.getStylesheets().add("newCSS.css");
                packageView.setScene(scene);
                packageView.show();
                packageView.setTitle("TIMOTEI // Paketin luonti");
                /* Sulkee myös info-ikkunan sujettaessa */
                packageView.setOnHiding(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                            FXMLPackageController.packageinfoView.close();
                            }
                        });
                    }
                });
            
            } catch (IOException ex) {
                Logger.getLogger(FXMLWebViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    /* Päivittää paketit comboboxiin */
    public static void updatePackages() {
        comboPackages.getItems().clear();
        for ( String p : Package.Packages) {
            comboPackages.getItems().add(p);
        }
    }
    
    /* Palauttaa tämän päivän ja ajan */
    public static String getToday() {
        Date dNow = new Date();
        SimpleDateFormat df = new SimpleDateFormat("'['yyyy.MM.dd HH:mm:ss']'"); 
        return df.format(dNow);        
    }
    
    /* Päivittää SmartPost kaupungit comboboxiin */
    public static void updateSmartPosts() {
        SmartPost.getAllCitysToList();
        for(int i = 0; i < SmartPost.citys.size(); i++) {
            comboBox.getItems().add(SmartPost.citys.get(i));
        }
    }
       
    /* Lähettää valitun paketin */
    @FXML
    private void sendPackage(ActionEvent event) {
        if(!comboPackages.getSelectionModel().isEmpty()) {
            String p = comboPackages.getSelectionModel().getSelectedItem();
            String[] parts = p.split(" / ");
            String[] parts2 = parts[0].split(" ");
            int packIndex = Integer.parseInt(parts2[1]);
            int pClass = Package.getLocations(packIndex);
            webview.getEngine().executeScript("document.createPath(" + Package.Locations + ", 'black', " + pClass + ");");
            int j = 0;
            for (int i = 0; i < Package.Locations.size(); i = i + 2) {
                webview.getEngine().executeScript("document.goToLoca('" + Package.Locations.get(i) + "', '" + Package.Locations.get(i+1) + "', '" + SmartPost.openingTimes.get(j) + "', 'orange');");
                j++;
            }
            Package.addToDelivered(packIndex, pClass);
            Log l = new Log("Lähetettiin paketti [" + comboPackages.getSelectionModel().getSelectedItem() + "]");
            Package.removePackage(packIndex);
            Package.getAllPackagesToList();
            updatePackages();
            comboPackages.getSelectionModel().clearSelection();
            
        }
    }
    
    /* Tyhjentää reitit kartalta */
    @FXML
    private void clearRoadAction(ActionEvent event) {
        webview.getEngine().executeScript("document.deleteMarkers();");
        webview.getEngine().executeScript("document.deletePaths();");
        Log l = new Log("Kartta tyhjennetty");
    }
    
    /* Avaa datanhallinta ikkunan */
    @FXML
    private void editDataAction(ActionEvent event) {
        if (StageHelper.getStages().size() <= 1) {
            try {
                Stage packageView = new Stage();
                Parent page = FXMLLoader.load(getClass().getResource("FXMLDataControl.fxml"));
                Scene scene = new Scene(page);
                scene.getStylesheets().add("newCSS.css");
                packageView.setScene(scene);
                packageView.show();
                packageView.setTitle("TIMOTEI // Datan hallinta");             
            
            } catch (IOException ex) {
                Logger.getLogger(FXMLWebViewController.class.getName()).log(Level.SEVERE, null, ex);
            }                       
        }
        
    }
    
    /* Päivittää Paketti tabin tietoja */
    @FXML
    private void PackageUpdateAction(Event event) {
        PackagesTextArea.setText("Paketin ID / Luokka / Esine / Lähtöpaikka / Saapumispaikka / Etäisyys\n\n");

        PackageCounterText.setText(String.valueOf(Package.Packages.size()));
        int i = 0;
        for (String p : Package.Packages) {
            PackagesTextArea.setText(PackagesTextArea.getText() + p  + " / "+ Package.PackagesInfo.get(i)+ "\n");
            i++;
        }
        DeliveredPackagesTextArea.setText("Paketin ID / Luokka / Esine / Lähtöpaikka / Saapumispaikka / Etäisyys / Rikki\n\n");
        Package.getDeliveredPackageInfos();
        DeliveredPackageCountText.setText(String.valueOf(Package.DeliveredPackageInfos.size()));
        for (Object p : Package.DeliveredPackageInfos) {
            DeliveredPackagesTextArea.setText(DeliveredPackagesTextArea.getText() + p.toString());
        }
       
    }
    /* Tyhjentää tietokannan ja luo oletus itemit ja pakettiluokat*/
    @FXML
    private void clearDatabaseAction(ActionEvent event) {
        startMenu.setVisible(false);
        DataBuilder db = DataBuilder.getInstance();
        db.clearData();
        try {
            db.getContent();
        } catch (IOException ex) {
            Logger.getLogger(FXMLWebViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
        db.getData();
        Log l = new Log("TIMOTEI käynnistyi");    
        Item i1 = new Item(1, "Muumimuki kokoelma", 2, 12500, 1);
        Item i2 = new Item(2, "Virallinen EM-kisapallo", 2, 10000, 0);
        Item i3 = new Item(3, "Ikea-sohva", 10, 25000, 0);
        Item i4 = new Item(4, "Pesäpallomaila", 1, 11111, 0);
        Package.createPackageClass(1, 40, 10, 5000, 150);
        Package.createPackageClass(2, 10, 10, 3000, 10000);
        Package.createPackageClass(3, 20, 10, 100000, 350);
        
        webview.getEngine().load(getClass().getResource("index.html").toExternalForm());
        SmartPost.getAllCitysToList();
        for(int i = 0; i < SmartPost.citys.size(); i++) {
            comboBox.getItems().add(SmartPost.citys.get(i));
        }
        
    }
    /* Toiminto jossa jatketaan vanhalla tietokannalla */
    @FXML
    private void continueAction(ActionEvent event) {
        Log l = new Log("Jatkettiin vanhalla tietokannalla");
        startMenu.setVisible(false);
        webview.getEngine().load(getClass().getResource("index.html").toExternalForm());
        SmartPost.getAllCitysToList();
        for(int i = 0; i < SmartPost.citys.size(); i++) {
            comboBox.getItems().add(SmartPost.citys.get(i));
        }
        Package.getAllPackagesToList();
        updatePackages();
        Item.getAllItemsToList();
    }

    /* Päivittää login*/
    @FXML
    private void updateLogAction(Event event) {
        listViewLog.getItems().clear();
        Log.getLogs();
        for(String log : Log.Logs)
            listViewLog.getItems().add(log);
    }
    /* Päivittää statistiikkaa */
    @FXML
    private void statisticAction(Event event) {
        Package.getDeliveredPackageInfos();
        textTotalKms.setText(String.valueOf(Package.distanceTotal) + " kilometriä");
        textStatistic.setText(String.valueOf(Package.DeliveredPackageInfos.size()
                - Package.brokenPackage) + "/" + Package.DeliveredPackageInfos.size() + " pakettia");
    }
}
