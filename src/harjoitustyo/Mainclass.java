
package harjoitustyo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/* Pääohjelma, avaa webview ikkunan*/
public class Mainclass extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLWebView.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("newCSS.css");
        stage.setScene(scene);

        stage.show();
        stage.setTitle("TIMOTEI // Toiminnaltaan Itellaa Muistuttava Ohjelmisto, Tietokantaa Edellyttävä Integraatio");
        
        stage.setOnHiding(new EventHandler<WindowEvent>() {
                    
                    @Override
                    public void handle(WindowEvent event) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Platform.exit();
                            }
                        });
                    }
                });
    }
/* Suljettaessa tekee loki tekstitiedoston */
    @Override
    public void stop(){
        Log l = new Log("TIMOTEI sulkeutui");
        try {
            PrintWriter out = new PrintWriter("kirjanpito.txt");
            out.println("Loki:\n\n" + Log.tempLog + "\nVarasto tilanne:\n");
            int i = 0;
            FXMLWebViewController.PackagesTextArea.setText("Paketin ID / Luokka / Esine / Lähtöpaikka / Saapumispaikka / Etäisyys\n\n");
            for (String p : Package.Packages) {
                FXMLWebViewController.PackagesTextArea.setText(FXMLWebViewController.PackagesTextArea.getText() + p  + " / "+ Package.PackagesInfo.get(i)+ "\n");
                i++;
        }
            out.println(FXMLWebViewController.PackagesTextArea.getText());
            out.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Mainclass.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    public static void main(String[] args) {
        launch(args);
    }
    
}
