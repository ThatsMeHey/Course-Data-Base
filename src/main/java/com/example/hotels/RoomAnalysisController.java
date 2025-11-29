package com.example.hotels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.sql.SQLException;
import java.util.ArrayList;

public class RoomAnalysisController extends ReportsTableController{
    @FXML
    private TableView<RoomAnalysisView> roomAnalysisTable;
    @FXML
    private TableColumn<RoomAnalysisView, Void> hRowNumber;
    @FXML
    private TableColumn<RoomAnalysisView, String> code;
    @FXML
    private TableColumn<RoomAnalysisView, String> name;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> totalRooms;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> availableRooms;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> occupiedRooms;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> currentVisitors;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> activeReservations;
    @FXML
    private TableColumn<RoomAnalysisView, Double> occupiedPercentage;

    @FXML
    private TableView<RoomAnalysisView> roomAnalysisResultTable;
    @FXML
    private TableColumn<RoomAnalysisView, Void> hRowNumberR;
    @FXML
    private TableColumn<RoomAnalysisView, String> codeR;
    @FXML
    private TableColumn<RoomAnalysisView, String> nameR;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> totalRoomsR;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> availableRoomsR;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> occupiedRoomsR;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> currentVisitorsR;
    @FXML
    private TableColumn<RoomAnalysisView, Integer> activeReservationsR;
    @FXML
    private TableColumn<RoomAnalysisView, Double> occupiedPercentageR;
    private final ObservableList<RoomAnalysisView> roomAnalysisObs = FXCollections.observableArrayList();
    private final ObservableList<RoomAnalysisView> roomAnalysisResultObs = FXCollections.observableArrayList();

    public void fillRoomAnalysis(){
        roomAnalysisTable.setItems(roomAnalysisObs);
        setupRowNumberColumn(hRowNumber);

        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalRooms.setCellValueFactory(new PropertyValueFactory<>("totalRooms"));
        availableRooms.setCellValueFactory(new PropertyValueFactory<>("availableRooms"));
        occupiedRooms.setCellValueFactory(new PropertyValueFactory<>("occupiedRooms"));
        currentVisitors.setCellValueFactory(new PropertyValueFactory<>("currentVisitors"));
        activeReservations.setCellValueFactory(new PropertyValueFactory<>("activeReservations"));
        occupiedPercentage.setCellValueFactory(new PropertyValueFactory<>("occupiedPercentage"));

        code.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        totalRooms.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        availableRooms.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        occupiedRooms.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        currentVisitors.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        activeReservations.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        occupiedPercentage.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));


        try{
            roomAnalysisObs.setAll(dataBase.getRoomAnalysis());
        }
        catch (SQLException e){}



        codeR.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameR.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalRoomsR.setCellValueFactory(new PropertyValueFactory<>("totalRooms"));
        availableRoomsR.setCellValueFactory(new PropertyValueFactory<>("availableRooms"));
        occupiedRoomsR.setCellValueFactory(new PropertyValueFactory<>("occupiedRooms"));
        currentVisitorsR.setCellValueFactory(new PropertyValueFactory<>("currentVisitors"));
        activeReservationsR.setCellValueFactory(new PropertyValueFactory<>("activeReservations"));
        occupiedPercentageR.setCellValueFactory(new PropertyValueFactory<>("occupiedPercentage"));

        roomAnalysisResultTable.setItems(roomAnalysisResultObs);
        setupRowNumberColumn(hRowNumberR);

        codeR.setCellFactory(TextFieldTableCell.forTableColumn());
        nameR.setCellFactory(TextFieldTableCell.forTableColumn());
        totalRoomsR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        availableRoomsR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        occupiedRoomsR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        currentVisitorsR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        activeReservationsR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        occupiedPercentageR.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));

        filterWindow = new FilterWindow(roomAnalysisTable, roomAnalysisObs);
        filterWindow.setRepController(this);
        filterWindow.openWindow();
        findResults();
    }

    @Override
    public void findResults() {
        RoomAnalysisView result = new RoomAnalysisView(
                "-", "-", 0, 0, 0, 0,
                0, 0.
        );
        for (Object obj: filterWindow.getFilteredList()){
            RoomAnalysisView view = (RoomAnalysisView) obj;
            result.totalRooms += view.totalRooms;
            result.availableRooms += view.availableRooms;
            result.occupiedRooms += view.occupiedRooms;
            result.currentVisitors += view.currentVisitors;
            result.activeReservations += view.activeReservations;
        }
        result.occupiedPercentage = ((double)result.occupiedRooms / result.totalRooms) * 100;
        roomAnalysisResultObs.clear();
        roomAnalysisResultObs.add(result);
    }
}
