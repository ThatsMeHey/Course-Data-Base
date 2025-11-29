package com.example.hotels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;
import java.util.ArrayList;

public class StaffController extends TablesController{
    InputWindow input;
    //таблица с персоналом
    @FXML
    private TableView<StaffView> staffTable;
    @FXML
    private TableColumn<HotelsView, Void> sRowNumber;
    @FXML
    private TableColumn<StaffView, String> sCode;
    @FXML
    private TableColumn<StaffView, String> shName;
    @FXML
    private TableColumn<StaffView, String> sName;
    @FXML
    private TableColumn<StaffView, String> sInn;
    @FXML
    private TableColumn<StaffView, String> sjCode;
    @FXML
    private TableColumn<StaffView, String> sjName;
    private final ObservableList<StaffView> staffObs = FXCollections.observableArrayList();
    private ObservableList<String> HotelList = FXCollections.observableArrayList();
    private ObservableList<String> JobList = FXCollections.observableArrayList();

    public void fillStaff(){
        staffTable.setEditable(true);
        staffTable.setItems(staffObs);

        setupRowNumberColumn(sRowNumber);
        sCode.setCellValueFactory(new PropertyValueFactory<>("hotel_code"));
        shName.setCellValueFactory(new PropertyValueFactory<>("hotel_name"));
        sName.setCellValueFactory(new PropertyValueFactory<>("name"));
        sInn.setCellValueFactory(new PropertyValueFactory<>("inn"));
        sjCode.setCellValueFactory(new PropertyValueFactory<>("job_code"));
        sjName.setCellValueFactory(new PropertyValueFactory<>("job_name"));

        sCode.setCellFactory(ComboBoxTableCell.forTableColumn(HotelList)); //выпадающий список
        sName.setCellFactory(TextFieldTableCell.forTableColumn());
        sInn.setCellFactory(TextFieldTableCell.forTableColumn());
        sjCode.setCellFactory(ComboBoxTableCell.forTableColumn(JobList)); //выпадающий список

        sName.setOnEditCommit(this::handleEditCommit);
        sInn.setOnEditCommit(this::handleEditCommit);

        sCode.setOnEditCommit(this::handleEditCommitComboBox);
        sjCode.setOnEditCommit(this::handleEditCommitComboBox);

        try {
            staffObs.setAll(dataBase.getStaff());
            HotelList.setAll(dataBase.getHotelsCodeName());
            JobList.setAll((dataBase.getJobsCodeName()));
        }
        catch (SQLException ex){}
        deleteRow(staffTable, staffObs);
    }
    private void handleEditCommit(TableColumn.CellEditEvent<StaffView, ?> event) {
        StaffView row = event.getRowValue();
        TableColumn<StaffView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        if (column == sName){
            dataBase.changeTableValues("staff", "name", (String)newValue,
                    "inn", row.getInn());
            row.setName((String)newValue);
        }
        else {
            if (((String)newValue).length() == 8 && ((String)newValue).matches("[0-9]+")){
                dataBase.changeTableValues("staff", "inn", (String)newValue,
                        "inn", row.getInn());
                row.setInn((String)newValue);
            }
            else showToast(primaryStage, "ИНН должен быть уникальным, состоять из 8 символов, и должен включать только цифры");
        }
        staffTable.refresh();
    }
    private void handleEditCommitComboBox(TableColumn.CellEditEvent<StaffView, ?> event)
    {
        StaffView row = event.getRowValue();
        TableColumn<StaffView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        if (column == sCode){
            String[] parts = ((String)newValue).split(" ", 2);
            dataBase.changeTableValues("staff", "hotel_code", parts[0],
                    "inn", row.getInn());
            row.setHotel_code(parts[0]);
            row.setHotel_name(parts[1]);
        }
        else {
            String[] parts = ((String)newValue).split(" ", 2);
            dataBase.changeTableValues("staff", "job_code", parts[0],
                    "inn", row.getInn());
            row.setJob_code(parts[0]);
            row.setJob_name(parts[1]);
        }
        staffTable.refresh();
    }
    @FXML
    @Override
    public void addNew() {
        input.openWindow();
    }
    @FXML
    public void addWithExistingJob(){
        input = new InputWindow(this, new ComboBoxWithName(new ComboBox<String>(HotelList), "Гостинница"), "Имя", "ИНН", new ComboBoxWithName(new ComboBox<String>(JobList), "Профессия"));
        addNew();
    }
    @FXML
    public void addWithNotExistingJob(){
        input = new InputWindow(this, "Код профессии", "Название профессии", "Имя", "ИНН", new ComboBoxWithName(new ComboBox<String>(HotelList), "Гостинница"), 3);
        addNew();
    }

    @FXML
    @Override
    public void openFilters(){
        if (filterWindow == null) {
            filterWindow = new FilterWindow(staffTable, staffObs);
            filterWindow.openWindow();
        }
        else {
            filterWindow.makeVisible();
        }
    }

    @Override
    public void addValue(ArrayList<String> arr){
        if (arr.size() == 4) {
            try {
                String[] partsHotel = arr.get(0).split(" ", 2);
                String[] partsJob = arr.get(3).split(" ", 2);
                if (partsHotel[0].length() == 6 && partsHotel[0].matches("[A-Z0-9]+") &&
                        arr.get(2).length() == 8 && arr.get(2).matches("[0-9]+") &&
                        partsJob[0].length() == 6 && partsJob[0].matches("[A-Z0-9]+")) {
                    String sql = String.format("INSERT INTO staff (hotel_code, name, inn, job_code) " +
                                    "VALUES ('%s', '%s', '%s', '%s');", partsHotel[0], escapeSql(arr.get(1)), arr.get(2),
                            partsJob[0]);
                    boolean success = dataBase.changeTableValues(sql);
                    if (success) {
                        staffObs.add(new StaffView(
                                partsHotel[0],
                                partsHotel[1],
                                arr.get(1),
                                arr.get(2),
                                partsJob[0],
                                partsJob[1]
                        ));
                    } else showToast(primaryStage, "Неправильный ввод");
                } else showToast(primaryStage, "Неправильный ввод");
            } catch (Throwable e) {
                showToast(primaryStage, "Неправильный ввод");
            }
        }
        else{
            try {
                if (arr.get(0).length() == 6 && arr.get(0).matches("[A-Z0-9]+")) {
                    String sql = String.format("INSERT INTO job_positions (job_code, name) " +
                            "VALUES ('%s', '%s');", arr.get(0), escapeSql(arr.get(1)));
                    dataBase.changeTableValues(sql);
                }
                else {
                    showToast(primaryStage, "Неправильный ввод");
                    return;
                }

                int len = arr.size();
                for (int i = 2; i < len; i+=3) {
                    String[] partsHotel = arr.get(i+2).split(" ", 2);
                    if (partsHotel[0].length() == 6 && partsHotel[0].matches("[A-Z0-9]+") &&
                            arr.get(i+1).length() == 8 && arr.get(i+1).matches("[0-9]+")) {
                        String sql = String.format("INSERT INTO staff (hotel_code, name, inn, job_code) " +
                                "VALUES ('%s', '%s', '%s', '%s');", partsHotel[0], escapeSql(arr.get(i)), arr.get(i+1), arr.get(0));
                        boolean success = dataBase.changeTableValues(sql);
                        if (success) {
                            staffObs.add(new StaffView(
                                    partsHotel[0],
                                    partsHotel[1],
                                    arr.get(1),
                                    arr.get(2),
                                    arr.get(0),
                                    arr.get(1)
                            ));
                        } else showToast(primaryStage, "Неправильный ввод");
                    } else showToast(primaryStage, "Неправильный ввод");
                }
            } catch (Throwable e) {
                showToast(primaryStage, "Неправильный ввод");
            }
        }
    }
    @Override
    public void deleteFromDB(Object obj){
        StaffView item = (StaffView) obj;
        String sql = String.format("DELETE FROM staff WHERE inn = '%s';", item.getInn());
        boolean success = dataBase.changeTableValues(sql);
        if (!success) showToast(primaryStage, "Неудалось удалить строку");
    }
}
