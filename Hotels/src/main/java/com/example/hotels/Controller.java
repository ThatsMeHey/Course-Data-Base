package com.example.hotels;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class Controller {
    private JavaPostgres dataBase = new JavaPostgres();
    Integer x = 600, y = 600;
    boolean[] table = new boolean[6];

    private Stage primaryStage;
    private Scene primaryScene;
    public void setPrimaryStage(Stage primaryStage, Scene primaryScene){
        this.primaryStage = primaryStage;
        this.primaryScene = primaryScene;
    }
    public Controller() {Arrays.fill(table, false);}

    @FXML
    protected void onTablesButtonClick(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tables.fxml"));
            loader.setController(this);
            Scene newScene = new Scene(loader.load(), x, y);
            primaryStage.setScene(newScene); // заменяем сцену
            primaryStage.setTitle("Таблицы");
        }
        catch (IOException e){}
    }

    @FXML
    protected void onReportButtonClick(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("reports.fxml"));
            loader.setController(this);
            Scene newScene = new Scene(loader.load(), x, y);
            primaryStage.setScene(newScene); // заменяем сцену
            primaryStage.setTitle("Отчёты");
        }
        catch (IOException e){}
    }

    @FXML
    protected void onBackButtonClick(){
        primaryStage.setScene(primaryScene);
    }


    @FXML
    protected void createView(Integer i, String name, String fx)
    {
        if (!table[i]) {
            table[i] = true;
            try {
                Stage newStage = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fx));
                Parent root = loader.load();
                newStage.setTitle(name);
                newStage.setScene(new Scene(root, x, y));
                newStage.show();
                newStage.setOnCloseRequest(event -> {
                    table[i] = false;
                });
                Random random = new Random();
                double angle = random.nextDouble() * 2 * Math.PI;

                newStage.setX(primaryStage.getX() + Math.cos(angle) * 50);
                newStage.setY(primaryStage.getY() + Math.sin(angle) * 50);
                Main.addOpenStage(newStage);
            } catch (IOException e) {
            }
        }
    }

    @FXML
    protected void onHotelsButtonClick(){
        createView(0, "Гостинницы", "hotels.fxml");
    }
    @FXML
    protected void onStaffButtonClick(){
        createView(1, "Персонал", "staff.fxml");
    }
    @FXML
    protected void onPositionButtonClick(){
        createView(2, "Должности", "positions.fxml");
    }
    @FXML
    protected void onRoomsButtonClick(){
        createView(3, "Номера", "rooms.fxml");
    }
    @FXML
    protected void onVisitorsButtonClick(){
        createView(4, "Посетители", "visitors.fxml");
    }
    @FXML
    protected void onBookingsButtonClick(){
        createView(5, "Брони", "bookings.fxml");
    }
}