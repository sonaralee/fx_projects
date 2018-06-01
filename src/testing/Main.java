package testing;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("testing.fxml"));

        primaryStage.setTitle("Hello JavaFX!");
        primaryStage.setScene(new Scene(root, 500, 300));
        primaryStage.show();

//        GridPane root = new GridPane();
//        root.setAlignment(Pos.TOP_CENTER);
//        root.setVgap(10);
//        root.setHgap(10);
//        Label greeting = new Label("Welcome to JavaFX!");
//        greeting.setTextFill(Color.GREEN);
//        greeting.setFont(Font.font("Times New Roman", FontWeight.BOLD, 70));
//        root.getChildren().add(greeting);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
