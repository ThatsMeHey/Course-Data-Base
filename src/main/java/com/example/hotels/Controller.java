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
    protected TablesController createView(String name, String fx, Scene scene)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fx));
            Scene newScene = new Scene(loader.load(), width, height);
            TablesController tController = loader.getController();
            tController.setStageParameters(primaryStage, scene);
            primaryStage.setScene(newScene);
            primaryStage.setTitle(name);
            tController.width = width;
            tController.height = height;

            return tController;
        } catch (IOException e) {
        }
        return null;
    }

    @FXML
    protected void onHotelsButtonClick(){
        TablesController controller = createView("Гостинницы", "hotels.fxml", tableScene);
        if (controller != null) {
            ((HotelsController)controller).fillHotels();
        }
    }
    @FXML
    protected void onStaffButtonClick(){
        TablesController controller = createView("Персонал", "staff.fxml", tableScene);
        if (controller != null) {
            ((StaffController)controller).fillStaff();
        }
    }
    @FXML
    protected void onPositionButtonClick(){
        TablesController  controller = createView("Должности", "positions.fxml", tableScene);
        if (controller != null) {
            ((JobsController)controller).fillJobs();
        }
    }
    @FXML
    protected void onRoomsButtonClick(){
        TablesController controller = createView("Номера", "rooms.fxml", tableScene);
        if (controller != null) {
            ((RoomsController)controller).fillRooms();
        }
    }
    @FXML
    protected void onVisitorsButtonClick(){
        TablesController controller = createView("Посетители", "visitors.fxml", tableScene);
        if (controller != null) {
            ((VisitorsController)controller).fillVisitors();
        }
    }
    @FXML
    protected void onBookingsButtonClick(){
        TablesController controller = createView("Брони", "bookings.fxml", tableScene);
        if (controller != null) {
            ((BookingsController)controller).fillBookings();
        }
    }
    @FXML
    protected void onHotelsStatsClick(){
        TablesController controller = createView("-", "hotelsStats.fxml", reportsScene);
        if (controller != null) {
            ((HotelsStatsController)controller).fillHotelsStats();
        }
    }
    @FXML
    protected void onRoomsAnalysisClick(){
        TablesController controller = createView("-", "roomAnalysis.fxml", reportsScene);
        if (controller != null) {
            ((RoomAnalysisController)controller).fillRoomAnalysis();
        }
    }
    @FXML
    protected void onHotelsFinanceAnalysisClick(){
        TablesController controller = createView("-", "hotelsFinanceAnalysis.fxml", reportsScene);
        if (controller != null) {
            ((HotelsFinanceAnalysisController)controller).fillHotelsFinAnalysis();
        }
    }
}