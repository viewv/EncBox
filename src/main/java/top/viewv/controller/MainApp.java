package top.viewv.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new
                    FXMLLoader(Objects.requireNonNull(getClass()).getClassLoader()
                    .getResource("data/ui/Main.fxml"));
            System.out.println("Initial Success");

            Parent root = loader.load();
            Scene scene = new Scene(root);

            String css = Objects.requireNonNull(MainApp.class.getClassLoader().getResource("data/style/main.css")).toExternalForm();
            scene.getStylesheets().add(css);

            primaryStage.setTitle("EncBox");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
