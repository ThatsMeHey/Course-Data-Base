package com.example.hotels;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static List<Stage> openStages = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double width = screenBounds.getWidth();
        double height = screenBounds.getHeight();
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);

        Controller controller = loader.getController();
        controller.setPrimaryStage(primaryStage, scene, width, height); // передаём сразу сцену

        primaryStage.setTitle("Гостинницы");
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}