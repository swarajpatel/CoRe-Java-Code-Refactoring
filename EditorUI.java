/**
 * Created by Vedant on 07-Dec-16.
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EditorUI extends Application {

    @Override

    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("splash.fxml"));
        stage.setScene(new Scene(loader.load()));

        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();



    }

    public static void main(String[] args) {

        launch(args);

    }


}

