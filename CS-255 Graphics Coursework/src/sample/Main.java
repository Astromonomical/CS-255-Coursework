package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
 /**
  * @author: Scott Simmons (960689)
  * I, Scott Simmons hereby declare that all code provided in this file was
  * solely written and produced by myself.
  * */
public class Main extends Application {
	public static ImageView imagePort;


    @Override
    public void start(Stage primaryStage) throws Exception{
    	Parent root = FXMLLoader.load(getClass().getResource("photoshop.fxml"));
        primaryStage.setTitle("Photoshop");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
