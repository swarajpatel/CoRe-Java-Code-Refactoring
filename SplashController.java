import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Aakash on 29-03-2017.
 */
public class SplashController implements Initializable {

    @FXML
    private StackPane rootPane;

    @Override
    public void initialize(URL url, ResourceBundle rb)  {
        new SplashScreen().start();
    }

    class SplashScreen extends Thread{
        public void run()
        {
            try {
                Thread.sleep(5000);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));

                        loader.setControllerFactory(t -> new EditorController(new EditorModel()));
                        Stage stage=new Stage();
                        stage.getIcons().add(new Image("file:C:\\Users\\Vedant\\IdeaProjects\\code_refactoring\\src\\Image\\logo.PNG"));
                        stage.setTitle("Code Refactoring Tool");

                        stage.setMaximized(true);
                        try {
                            stage.setScene(new Scene(loader.load()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        stage.show();
                        rootPane.getScene().getWindow().hide();
                    }
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
