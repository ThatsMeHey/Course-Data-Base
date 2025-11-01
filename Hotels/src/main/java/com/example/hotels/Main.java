package com.example.hotels;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    private static List<Stage> openStages = new ArrayList<>(); // храним все открытые окна
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);

        Controller controller = loader.getController();
        controller.setPrimaryStage(primaryStage, scene); // передаём сразу сцену
        primaryStage.setOnCloseRequest(event -> {
            closeAllWindows();
        });

        primaryStage.setTitle("Гостинницы");
        primaryStage.show();
    }

    // Метод для добавления окон в список
    public static void addOpenStage(Stage stage) {
        openStages.add(stage);
    }

    // Метод для закрытия всех окон
    public static void closeAllWindows() {
        for (Stage stage : openStages) {
            if (stage != null && stage.isShowing()) {
                stage.close();
            }
        }
        openStages.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}