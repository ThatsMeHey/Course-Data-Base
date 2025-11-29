package com.example.hotels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.sql.SQLException;

public class HotelsFinanceAnalysisController extends ReportsTableController {
    @FXML
    private TableView<HotelsFinanceAnalysisView> hotelsFinanceAnalysisTable;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Void> hRowNumber;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, String> code;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, String> name;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Integer> totalRooms;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> potentialIncome;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> avgRoomPrice;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> minRoomPrice;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> maxRoomPrice;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> currentIncome;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Integer> totalStaff;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Integer> uniquePosCount;

    @FXML
    private TableView<HotelsFinanceAnalysisView> hotelsFinanceAnalysisResultTable;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Void> hRowNumberR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, String> codeR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, String> nameR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Integer> totalRoomsR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> potentialIncomeR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> avgRoomPriceR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> minRoomPriceR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> maxRoomPriceR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Double> currentIncomeR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Integer> totalStaffR;
    @FXML
    private TableColumn<HotelsFinanceAnalysisView, Integer> uniquePosCountR;
    private final ObservableList<HotelsFinanceAnalysisView> hotelsFinAnalysisObs = FXCollections.observableArrayList();
    private final ObservableList<HotelsFinanceAnalysisView> hotelsFinAnalysisResultObs = FXCollections.observableArrayList();

    public void fillHotelsFinAnalysis(){
        hotelsFinanceAnalysisTable.setItems(hotelsFinAnalysisObs);
        setupRowNumberColumn(hRowNumber);

        code.setCellValueFactory(new PropertyValueFactory<>("code"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalRooms.setCellValueFactory(new PropertyValueFactory<>("totalRooms"));
        potentialIncome.setCellValueFactory(new PropertyValueFactory<>("potentialIncome"));
        avgRoomPrice.setCellValueFactory(new PropertyValueFactory<>("avgRoomPrice"));
        minRoomPrice.setCellValueFactory(new PropertyValueFactory<>("minRoomPrice"));
        maxRoomPrice.setCellValueFactory(new PropertyValueFactory<>("maxRoomPrice"));
        currentIncome.setCellValueFactory(new PropertyValueFactory<>("currentIncome"));
        totalStaff.setCellValueFactory(new PropertyValueFactory<>("totalStaff"));
        uniquePosCount.setCellValueFactory(new PropertyValueFactory<>("uniquePosCount"));

        code.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        totalRooms.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        potentialIncome.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        avgRoomPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        minRoomPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        maxRoomPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        currentIncome.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        totalStaff.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        uniquePosCount.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        try{
            hotelsFinAnalysisObs.setAll(dataBase.getHotelsFinAnalysis());
        }
        catch (SQLException e){}



        codeR.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameR.setCellValueFactory(new PropertyValueFactory<>("name"));
        totalRoomsR.setCellValueFactory(new PropertyValueFactory<>("totalRooms"));
        potentialIncomeR.setCellValueFactory(new PropertyValueFactory<>("potentialIncome"));
        avgRoomPriceR.setCellValueFactory(new PropertyValueFactory<>("avgRoomPrice"));
        minRoomPriceR.setCellValueFactory(new PropertyValueFactory<>("minRoomPrice"));
        maxRoomPriceR.setCellValueFactory(new PropertyValueFactory<>("maxRoomPrice"));
        currentIncomeR.setCellValueFactory(new PropertyValueFactory<>("currentIncome"));
        totalStaffR.setCellValueFactory(new PropertyValueFactory<>("totalStaff"));
        uniquePosCountR.setCellValueFactory(new PropertyValueFactory<>("uniquePosCount"));

        hotelsFinanceAnalysisResultTable.setItems(hotelsFinAnalysisResultObs);
        setupRowNumberColumn(hRowNumberR);


        filterWindow = new FilterWindow(hotelsFinanceAnalysisTable, hotelsFinAnalysisObs);
        filterWindow.setRepController(this);
        filterWindow.openWindow();
        findResults();
    }

    @Override
    public void findResults() {
        HotelsFinanceAnalysisView result = new HotelsFinanceAnalysisView(
                "-", "-", 0, 0., 0., Double.MAX_VALUE, -1, 0.,0, 0
        );
        double sumPrice = 0;
        for (Object obj: filterWindow.getFilteredList()){
            HotelsFinanceAnalysisView view = (HotelsFinanceAnalysisView)obj;
            result.totalRooms += view.totalRooms;
            result.potentialIncome += view.potentialIncome;
            sumPrice += view.avgRoomPrice * view.totalRooms;
            result.minRoomPrice = Math.min(view.minRoomPrice, result.minRoomPrice);
            result.maxRoomPrice = Math.max(view.maxRoomPrice, result.maxRoomPrice);
            result.currentIncome += view.currentIncome;
            result.totalStaff += view.totalStaff;
            result.uniquePosCount += view.uniquePosCount;
        }
        result.avgRoomPrice = Math.round(sumPrice / (double)result.totalRooms * 100) / 100;
        hotelsFinAnalysisResultObs.clear();
        hotelsFinAnalysisResultObs.add(result);
    }
}
