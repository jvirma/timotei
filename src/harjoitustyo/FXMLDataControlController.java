

package harjoitustyo;

import static harjoitustyo.FXMLWebViewController.ActionLogText;
import static harjoitustyo.FXMLWebViewController.getToday;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

/* Datan hallinta ikkuna*/
public class FXMLDataControlController implements Initializable {
    @FXML
    private TextField newSmartPostInput;
    @FXML
    private ComboBox<String> comboCitys;
    @FXML
    private ComboBox<String> comboSmartPosts;
    @FXML
    private ComboBox<String> comboPackages;
    @FXML
    private ComboBox<String> comboItems;

/* Laittaa kaupungit, paketit ja tavarat comboboxeihin  */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        SmartPost.getAllCitysToList();
        comboCitys.getItems().clear();
        for(int i = 0; i < SmartPost.citys.size(); i++) {
            comboCitys.getItems().add(SmartPost.citys.get(i));
        }
        comboPackages.getItems().clear();
        for ( String p : Package.Packages) {
            comboPackages.getItems().add(p);
        }
        comboItems.getItems().clear();
        for(String name : Item.ItemNames) {
            comboItems.getItems().add(name);
        }                
    }    
/* Luo SmartPostin  */
    @FXML
    private void addSmartPostAction(ActionEvent event) {
        String temp = newSmartPostInput.getText();
        String[] parts = temp.split(", ");
        int newID = SmartPost.getLastSmartPostID();
        SmartPost sp = new SmartPost(newID + 1, parts[5], parts[3].toUpperCase(), parts[4], parts[6], parts[0], parts[1], parts[2]);
        FXMLWebViewController.updateSmartPosts();
        newSmartPostInput.clear();
        SmartPost.getAllCitysToList();
        comboCitys.getItems().clear();
        for(int i = 0; i < SmartPost.citys.size(); i++) {
            comboCitys.getItems().add(SmartPost.citys.get(i));
        }
        Log l = new Log("Luotiin uusi SmartPost [" + parts[0] + " // " + parts[4] +", " + parts[3].toUpperCase()+  "]");     
    }

/* Poistaa paketin  */
    @FXML
    private void removePackage(ActionEvent event) {
        if (!comboPackages.getSelectionModel().isEmpty()) {
            String temp = comboPackages.getSelectionModel().getSelectedItem();
            String[] parts = temp.split("ID: ");
            parts = parts[1].split(" /");
            Package.removePackage(Integer.parseInt(parts[0]));
            Log l = new Log("Poistettiin Paketti [" +temp+ "]");
            comboPackages.getSelectionModel().clearSelection();
            Package.getAllPackagesToList();
            comboPackages.getItems().clear();
            for ( String p : Package.Packages) {
                comboPackages.getItems().add(p);
            }
            FXMLWebViewController.updatePackages();
        }
    }

/* Poistaa tavaran  */
    @FXML
    private void removeItem(ActionEvent event) {
        if (!comboItems.getSelectionModel().isEmpty()) {
            String name = comboItems.getSelectionModel().getSelectedItem();
            Item.removeItem(name);
            Item.getAllItemsToList();
            Log l = new Log("Poistettiin Tavara [" +name+ "]");
            comboItems.getItems().clear();
            for(String n : Item.ItemNames) {
                comboItems.getItems().add(n);
            }
        }
    }

/* Päivittää kaupungit comboboxiin  */
    @FXML
    private void listSmartPostAction(ActionEvent event) {
        comboSmartPosts.getItems().clear();
        String city = comboCitys.getSelectionModel().getSelectedItem();
        SmartPost.getSmartPosts(city);
        for(String post : SmartPost.posts) {
            comboSmartPosts.getItems().add(post);   
        }                
    }
/* Poistaa SmartPostin  */
    @FXML
    private void removeSmartPostAction(ActionEvent event) {
        if (!comboSmartPosts.getSelectionModel().isEmpty()) {
            String name = comboSmartPosts.getSelectionModel().getSelectedItem();
            int id = SmartPost.getPostID(name);
            SmartPost.removePostcode(id);
            SmartPost.removeSmartPost(id);
        
            SmartPost.getAllCitysToList();
            Log l = new Log("Poistettiin SmartPost [" +name+ "]");
            comboCitys.getItems().clear();
            for(int i = 0; i < SmartPost.citys.size(); i++) {
                comboCitys.getItems().add(SmartPost.citys.get(i));
            }
            FXMLWebViewController.updateSmartPosts();
            comboSmartPosts.getSelectionModel().clearSelection();
            comboCitys.getSelectionModel().clearSelection();  
        }
    }  
}
