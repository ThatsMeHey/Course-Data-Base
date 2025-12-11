package com.example.hotels;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    private JavaPostgres dataBase = new JavaPostgres();
    private double width, height;

    private Stage primaryStage;
    private Scene primaryScene, tableScene, reportsScene;
    public void setPrimaryStage(Stage primaryStage, Scene primaryScene, double w, double h){
        this.primaryStage = primaryStage;
        this.primaryScene = primaryScene;
        width = w;
        height = h;
    }

    @FXML
    protected void onTablesButtonClick(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tables.fxml"));
            loader.setController(this);
            Scene newScene = new Scene(loader.load(), width, height);
            tableScene = newScene;
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
            Scene newScene = new Scene(loader.load(), width, height);
            reportsScene = newScene;
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
    protected void closeApplication(){
        Platform.exit();
    }


    @FXML
    protected void createView(String fx, Scene scene)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fx));
            Scene newScene = new Scene(loader.load(), width, height);
            TablesController tController = loader.getController();
            tController.setStageParameters(primaryStage, scene);
            primaryStage.setScene(newScene);
            tController.width = width;
            tController.height = height;

            tController.fillTable();

        } catch (IOException e) {}
    }

    @FXML
    protected void onHotelsButtonClick(){
        createView("hotels.fxml", tableScene);
    }
    @FXML
    protected void onStaffButtonClick(){
        createView("staff.fxml", tableScene);
    }
    @FXML
    protected void onPositionButtonClick(){
        createView("positions.fxml", tableScene);
    }
    @FXML
    protected void onRoomsButtonClick(){
        createView("rooms.fxml", tableScene);
    }
    @FXML
    protected void onVisitorsButtonClick(){
        createView("visitors.fxml", tableScene);
    }
    @FXML
    protected void onBookingsButtonClick(){
        createView("bookings.fxml", tableScene);
    }

    @FXML
    protected void onHotelsStatsClick(){
        createView("hotelsStats.fxml", reportsScene);
    }
    @FXML
    protected void onRoomsAnalysisClick(){
        createView("roomAnalysis.fxml", reportsScene);
    }
    @FXML
    protected void onHotelsFinanceAnalysisClick(){
        createView("hotelsFinanceAnalysis.fxml", reportsScene);
    }
}