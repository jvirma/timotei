
package harjoitustyo;

import com.sun.javafx.stage.StageHelper;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/* Paketin luonti  */

public class FXMLPackageController implements Initializable {
    @FXML
    private ComboBox<String> startCity;
    @FXML
    private ComboBox<String> destCity;
    @FXML
    private Button cancelButton;
    @FXML
    private ComboBox<String> comboStartPosts;
    @FXML
    private ComboBox<String> comboDestPosts;
    @FXML
    private ComboBox<String> comboItems;
    @FXML
    private RadioButton firstClassButton;
    @FXML
    private RadioButton secondClassButton;
    final ToggleGroup group = new ToggleGroup();
    @FXML
    private RadioButton thirdClassButton;
    
    int PacketClass = 1;
    int feature = 0;
    @FXML
    private Text warning;
    
    public static Stage packageinfoView = new Stage();
    @FXML
    private TextField UserItemSize;
    @FXML
    private TextField UserItemWeight;
    @FXML
    private TextField UserItemName;
    @FXML
    private ImageView iv1;
    @FXML
    private Text textImage;
    @FXML
    private ImageView iv2;
    @FXML
    private ImageView iv3;
    @FXML
    private ImageView iv4;

    /*Laittaa tiedot comboboxeihin ja oletus asettaa luokaksi 1 */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        iv1.setVisible(false);
        iv2.setVisible(false);
        iv3.setVisible(false);
        iv4.setVisible(false);
        textImage.setVisible(false);
        
        for(String name : Item.ItemNames) {
            comboItems.getItems().add(name);
        }      
        for(int i = 0; i < SmartPost.citys.size(); i++) {
            startCity.getItems().add(SmartPost.citys.get(i));
            destCity.getItems().add(SmartPost.citys.get(i));
        }
        firstClassButton.setToggleGroup(group);
        firstClassButton.setSelected(true);
        secondClassButton.setToggleGroup(group);
        thirdClassButton.setToggleGroup(group);
    }    
    /*Sulkeminen */
    @FXML
    private void cancelAction(ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        packageinfoView.close();
    }

    /*Hakee kaupungin SmartPostit aloitus comboboxiin */
    @FXML
    private void startList(ActionEvent event) {
        comboStartPosts.getItems().clear();
        String city = startCity.getSelectionModel().getSelectedItem();
        SmartPost.getSmartPosts(city);
        for(String post : SmartPost.posts) {
            comboStartPosts.getItems().add(post);            
        }
    }

    /*Hakee kaupungin SmartPostit määränpää comboboxiin */
    @FXML
    private void destList(ActionEvent event) {
        comboDestPosts.getItems().clear();
        String city = destCity.getSelectionModel().getSelectedItem();
        SmartPost.getSmartPosts(city);
        for(String post : SmartPost.posts) {
            comboDestPosts.getItems().add(post);
        }
    }
    
    /* Vaihtaa paketin luokkaa toimenpiteen mukaan  */
    @FXML
    private void firstButtonAction(ActionEvent event) {
        PacketClass = 1;
    }

    @FXML
    private void secondButtonAction(ActionEvent event) {
        PacketClass = 2;
    }

    @FXML
    private void thirdButtonAction(ActionEvent event) {
        PacketClass = 3;
    }

    /* Vaihtaa esineen rikkoutumaattomuutta  */
    @FXML
    private void featureAction(ActionEvent event) {
        if (feature == 0)
            feature = 1;
        else if (feature == 1)
            feature = 0;
    }

    /* Luo paketin, jos ei virheitä ja sulkee paketinluonti ikkunan  */
    @FXML
    private void createButtonAction(ActionEvent event) {
        int succesSave = 0;
        if (checkErrors())  {
            int itemid = -1;
            int id = Package.getLastPackageID() + 1;
            String startPost = comboStartPosts.getSelectionModel().getSelectedItem();
            int startPostID = SmartPost.getPostID(startPost);
            String destPost = comboDestPosts.getSelectionModel().getSelectedItem();
            int destPostID = SmartPost.getPostID(destPost);
            double distance = SmartPost.getDistance(startPostID, destPostID);
            ArrayList limits = Package.getLimits(PacketClass);
            double distanceMax = Double.valueOf(limits.get(2).toString());
            int distanceLimit = 0;
            if (distance > distanceMax) {
                warning.setText("Matka liian pitkä! (" + distance + " km)");
                printWarning();
                distanceLimit = 1;
            }
                      
            if (!comboItems.getSelectionModel().isEmpty())
                itemid = Item.getItemID(comboItems.getSelectionModel().getSelectedItem());

                //itemid = comboItems.getSelectionModel().getSelectedIndex() + 1;
            else if (distanceLimit == 0) {
                itemid = Item.ItemNames.size() + 1;
                String itemName = UserItemName.getText();
                int weight = Integer.parseInt(UserItemWeight.getText());
                int size = Integer.parseInt(UserItemSize.getText());
                Item item = new Item(itemid, itemName, weight, size, feature);
            }

            if (PacketClass == 1 && distanceLimit == 0) {
                    Package p = new FirstClass(id, itemid, startPostID, destPostID, distance);
                    succesSave = 1;
                }
            else if (PacketClass == 2 && distanceLimit == 0) {
                Package p = new SecondClass(id, itemid, startPostID, destPostID, distance);
                succesSave = 1;
            }
            else if (PacketClass == 3 && distanceLimit == 0) {            
                Package p = new ThirdClass(id, itemid, startPostID, destPostID, distance);
                succesSave = 1;
            }
        
            if (succesSave == 1) {
                Package.getAllPackagesToList();
                FXMLWebViewController.updatePackages();
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
                packageinfoView.close();
            }
        }
    }
    
    /* Avaa info-ikkunan  */
    @FXML
    private void infoAction(ActionEvent event) {
        if (StageHelper.getStages().size() <= 2) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource("FXMLPackageInfos.fxml"));
            Scene scene = new Scene(page);
            packageinfoView.setScene(scene);
            packageinfoView.show();
            packageinfoView.setTitle("TIMOTEI // Infoa luokista");

        } catch (IOException ex) {
            Logger.getLogger(FXMLWebViewController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /* Tulostaa varoituksen joka häviää ajan kuluessa  */
    private void printWarning() {
        FadeTransition ft = new FadeTransition(Duration.millis(4000), warning);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false); 
        ft.play();
    }
    /* Tarkistaa onko käyttäjä tehnyt virheitä tai esine liian suuri/painava  */
    private boolean checkErrors() {
        ArrayList limits = Package.getLimits(PacketClass);
        double weightMax = Double.valueOf(limits.get(0).toString());
        double sizeMax = Double.valueOf(limits.get(1).toString());
        
        if (comboItems.getSelectionModel().isEmpty() && (UserItemName.getText().equals("")
                || UserItemSize.getText().equals("") || UserItemWeight.getText().equals("")))  {
            warning.setText("Valitse esine tai luo uusi");
            printWarning();
            return false;
        }
        else if (comboStartPosts.getItems().isEmpty() ||
            comboDestPosts.getItems().isEmpty()) {
            warning.setText("Valitse kaupungit ja SmartPostit");
            printWarning();
            return false;
        }
        else if (comboStartPosts.getSelectionModel().isEmpty() ||
            comboDestPosts.getSelectionModel().isEmpty()) {
            warning.setText("Valitse kaupungit ja SmartPostit");
            printWarning();
            return false;
            }
        
        else if (comboStartPosts.getSelectionModel().getSelectedItem().isEmpty() || comboDestPosts.getSelectionModel().getSelectedItem().isEmpty()) {
            warning.setText("Valitse kaupungit ja SmartPostit");    
            printWarning();
            return false;
        }
        
        
        else if (!UserItemWeight.getText().isEmpty() && Integer.parseInt(UserItemWeight.getText()) > weightMax) {
            Integer.parseInt(UserItemWeight.getText());
            warning.setText("Liian painava esine!");
            printWarning();
            return false;
        }
        else if (!UserItemSize.getText().isEmpty() && Integer.parseInt(UserItemSize.getText()) > sizeMax) {
            warning.setText("Liian iso esine!");
            printWarning();
            return false;
        }
        
        return true;
    }       
/* Tyhjentää Item-valinnan jos haluaakin tehdä oman itemin  */
    @FXML
    private void clearCombo(ActionEvent event) {
        comboItems.getSelectionModel().clearSelection();
        iv1.setVisible(false);
        textImage.setVisible(false);
        iv2.setVisible(false);
        iv3.setVisible(false);
        iv4.setVisible(false);
    }
    /* Näyttää oletusesineiden kuvan jos valittu*/
    @FXML
    private void comboItemAction(ActionEvent event) {
        if (!comboItems.getSelectionModel().isEmpty()) {
            if (comboItems.getSelectionModel().getSelectedItem().equals("Muumimuki kokoelma")) {
                textImage.setVisible(true);
                iv1.setVisible(true);
                iv2.setVisible(false);
                iv3.setVisible(false);
                iv4.setVisible(false);
            }
            else if (comboItems.getSelectionModel().getSelectedItem().equals("Virallinen EM-kisapallo")) {
                textImage.setVisible(true);
                iv2.setVisible(true);
                iv1.setVisible(false);
                iv3.setVisible(false);
                iv4.setVisible(false);
            }
            else if (comboItems.getSelectionModel().getSelectedItem().equals("Ikea-sohva")) {
                textImage.setVisible(true);
                iv2.setVisible(false);
                iv1.setVisible(false);
                iv3.setVisible(true);
                iv4.setVisible(false);
            }
            else if (comboItems.getSelectionModel().getSelectedItem().equals("Pesäpallomaila")) {
                textImage.setVisible(true);
                iv2.setVisible(false);
                iv1.setVisible(false);
                iv3.setVisible(false);
                iv4.setVisible(true);
            }
            
            else {
                iv1.setVisible(false);
                iv2.setVisible(false);
                iv3.setVisible(false);
                iv4.setVisible(false);
                textImage.setVisible(false);  
            }
        }
    }
}

