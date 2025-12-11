package com.example.hotels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.sql.SQLException;

public class HotelsStatsController extends ReportsTableController{
    @FXML
    private TableView<HotelStatsView> hotelStatsTable;
    @FXML
    private TableColumn<HotelStatsView, Void> hRowNumber;
    @FXML
    private TableColumn<HotelStatsView, String> code;
    @FXML
    private TableColumn<HotelStatsView, String> name;
    @FXML
    private TableColumn<HotelStatsView, Integer> staffCount;
    @FXML
    private TableColumn<HotelStatsView, Integer> totalRooms;
    @FXML
    private TableColumn<HotelStatsView, Integer> availableRooms;
    @FXML
    private TableColumn<HotelStatsView, Integer> currentVisitors;

    @FXML
    private TableView<HotelStatsView> hotelStatsResultTable;
    @FXML
    private TableColumn<HotelStatsView, Void> hRowNumberR;
    @FXML
    private TableColumn<HotelStatsView, String> codeR;
    @FXML
    private TableColumn<HotelStatsView, String> nameR;
    @FXML
    private TableColumn<HotelStatsView, Integer> staffCountR;
    @FXML
    private TableColumn<HotelStatsView, Integer> totalRoomsR;
    @FXML
    private TableColumn<HotelStatsView, Integer> availableRoomsR;
    @FXML
    private TableColumn<HotelStatsView, Integer> currentVisitorsR;
    private final ObservableList<HotelStatsView> hotelsStatsObs = FXCollections.observableArrayList();
    private final ObservableList<HotelStatsView> hotelsStatsResultObs = FXCollections.observableArrayList();

    public void fillTable(){
        hotelStatsTable.setItems(hotelsStatsObs);
        setupRowNumberColumn(hRowNumber);

        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        staffCount.setCellValueFactory(new PropertyValueFactory<>("staffCount"));
        totalRooms.setCellValueFactory(new PropertyValueFactory<>("totalRooms"));
        availableRooms.setCellValueFactory(new PropertyValueFactory<>("availableRooms"));
        currentVisitors.setCellValueFactory(new PropertyValueFactory<>("currentVisitors"));

        code.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        staffCount.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        totalRooms.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        availableRooms.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        currentVisitors.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        try{
            hotelsStatsObs.setAll(dataBase.getHotelsStats());
        }
        catch (SQLException e){}



        codeR.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameR.setCellValueFactory(new PropertyValueFactory<>("name"));
        staffCountR.setCellValueFactory(new PropertyValueFactory<>("staffCount"));
        totalRoomsR.setCellValueFactory(new PropertyValueFactory<>("totalRooms"));
        availableRoomsR.setCellValueFactory(new PropertyValueFactory<>("availableRooms"));
        currentVisitorsR.setCellValueFactory(new PropertyValueFactory<>("currentVisitors"));

        hotelStatsResultTable.setItems(hotelsStatsResultObs);
        setupRowNumberColumn(hRowNumberR);

        codeR.setCellFactory(TextFieldTableCell.forTableColumn());
        nameR.setCellFactory(TextFieldTableCell.forTableColumn());
        staffCountR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        totalRoomsR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        availableRoomsR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        currentVisitorsR.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        filterWindow = new FilterWindow(hotelStatsTable, hotelsStatsObs);
        filterWindow.setRepController(this);
        filterWindow.openWindow();
        findResults();
    }

    @Override
    public void findResults() {
        HotelStatsView result = new HotelStatsView(
                "-", "-", 0, 0, 0, 0
        );
        for (Object obj: filterWindow.getFilteredList()){
            HotelStatsView view = (HotelStatsView)obj;
            result.staffCount += view.staffCount;
            result.totalRooms += view.totalRooms;
            result.availableRooms += view.availableRooms;
            result.currentVisitors += view.currentVisitors;
        }
        hotelsStatsResultObs.clear();
        hotelsStatsResultObs.add(result);
    }
}
