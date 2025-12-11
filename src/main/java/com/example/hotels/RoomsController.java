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
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.sql.SQLException;
import java.util.ArrayList;

public class RoomsController extends TablesController{
    //таблица с номерами
    @FXML
    private TableView<RoomsView> roomsTable;
    @FXML
    private TableColumn<HotelsView, Void> rRowNumber;
    @FXML
    private TableColumn<RoomsView, String> rCode;
    @FXML
    private TableColumn<RoomsView, String> rhName;
    @FXML
    private TableColumn<RoomsView, Integer> rNumber;
    @FXML
    private TableColumn<RoomsView, String> rDescription;
    @FXML
    private TableColumn<RoomsView, Double> rPrice;
    @FXML
    private TableColumn<RoomsView, String> rAvailable;
    private final ObservableList<RoomsView> roomsObs = FXCollections.observableArrayList();
    private ObservableList<String> HotelList = FXCollections.observableArrayList();
    private ObservableList<String> variantsList = FXCollections.observableArrayList();

    public void fillTable(){
        roomsTable.setEditable(true);
        roomsTable.setItems(roomsObs);

        setupRowNumberColumn(rRowNumber);
        rCode.setCellValueFactory(new PropertyValueFactory<>("hotel_code"));
        rhName.setCellValueFactory(new PropertyValueFactory<>("hotel_name"));
        rNumber.setCellValueFactory(new PropertyValueFactory<>("room_number"));
        rDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        rPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        rAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));

        rCode.setCellFactory(ComboBoxTableCell.forTableColumn(HotelList)); //выпадающий список
        rNumber.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        rDescription.setCellFactory(TextFieldTableCell.forTableColumn());
        rPrice.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        rAvailable.setCellFactory(ComboBoxTableCell.forTableColumn(variantsList));

        rNumber.setOnEditCommit(this::handleEditCommit);
        rDescription.setOnEditCommit(this::handleEditCommit);
        rPrice.setOnEditCommit(this::handleEditCommit);

        rAvailable.setOnEditCommit(this::handleEditCommitComboBox);
        rCode.setOnEditCommit(this::handleEditCommitComboBox);

        try {
            roomsObs.setAll(dataBase.getRooms());
            HotelList.setAll(dataBase.getHotelsCodeName());
        }
        catch (SQLException ex){}
        variantsList.add("Да");
        variantsList.add("Нет");
        deleteRow(roomsTable, roomsObs);
    }

    private void handleEditCommit(TableColumn.CellEditEvent<RoomsView, ?> event) {
        RoomsView row = event.getRowValue();
        TableColumn<RoomsView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        if (column == rNumber){
            String sql = String.format("UPDATE rooms SET room_number = %s WHERE hotel_code = '%s' AND room_number = %s",
                    (Integer)newValue, row.getHotel_code(), row.getRoom_number());
            boolean success = dataBase.changeTableValues(sql);
            if (success) row.setRoom_number((Integer)newValue);
            else showToast(primaryStage, "Неправильный ввод или такая комната уже существует");
        }
        else if (column == rDescription) {
            String sql = String.format("UPDATE rooms SET description = '%s' WHERE hotel_code = '%s' AND room_number = %s",
                    escapeSql((String)newValue), row.getHotel_code(), row.getRoom_number());
            dataBase.changeTableValues(sql);
            row.setDescription((String)newValue);
        }
        else if (column == rPrice){
            String sql = String.format("UPDATE rooms SET price = %s WHERE hotel_code = '%s' AND room_number = %s",
                    (Double)newValue, row.getHotel_code(), row.getRoom_number());
            boolean success = dataBase.changeTableValues(sql);
            if (success) row.setPrice((Double)newValue);
            else showToast(primaryStage, "Неправильный ввод цены");
        }
        roomsTable.refresh();
    }
    private void handleEditCommitComboBox(TableColumn.CellEditEvent<RoomsView, ?> event)
    {
        RoomsView row = event.getRowValue();
        TableColumn<RoomsView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        if (column == rCode) {
            String[] parts = ((String) newValue).split(" ", 2);
            String sql = String.format("UPDATE rooms SET hotel_code = '%s' WHERE hotel_code = '%s' AND room_number = %s",
                    parts[0], row.getHotel_code(), row.getRoom_number());
            dataBase.changeTableValues(sql);
            row.setHotel_code(parts[0]);
            row.setHotel_name(parts[1]);
        }
        else{
            if (((String)newValue).equals("Да")){
                if (dataBase.roomIsOccupied(row.getHotel_code(), row.getRoom_number())){
                    showToast(primaryStage, "В данной комнате есть проживающие");
                }
                else {
                    String sql = String.format("UPDATE rooms SET available = true WHERE hotel_code = '%s' AND room_number = %s",
                            row.getHotel_code(), row.getRoom_number());
                    dataBase.changeTableValues(sql);
                    row.setAvailable(true);
                }
            }
            else {
                String sql = String.format("UPDATE rooms SET available = false WHERE hotel_code = '%s' AND room_number = %s",
                        row.getHotel_code(), row.getRoom_number());
                dataBase.changeTableValues(sql);
                row.setAvailable(false);
            }
        }

        roomsTable.refresh();
    }
    @FXML
    @Override
    public void addNew() {
        InputWindow input = new InputWindow(this, new ComboBoxWithName(new ComboBox<String>(HotelList), "Гостинница"), "Номер", new ComboBoxWithName(new ComboBox<String>(variantsList), "Доступен для заселения"), "Цена", "Описание");
        input.openWindow();
    }
    @FXML
    @Override
    public void openFilters(){
        if (filterWindow == null) {
            filterWindow = new FilterWindow(roomsTable, roomsObs);
            filterWindow.openWindow();
        }
        else {
            filterWindow.makeVisible();
        }
    }

    @Override
    public void addValue(ArrayList<String> arr){
        try {
            String[] parts = arr.get(0).split(" ", 2);
            if (parts[0].length() == 6 && parts[0].matches("[A-Z0-9]+")) {
                String sql = String.format("INSERT INTO rooms (hotel_code, room_number, available, price, description) " +
                                "VALUES ('%s', %s, %s, %s, '%s');", parts[0], arr.get(1), arr.get(2).equals("Да"),
                        arr.get(3), escapeSql(arr.get(4)));
                boolean success = dataBase.changeTableValues(sql);
                if (success) {
                    roomsObs.add(new RoomsView(
                            parts[0],
                            parts[1],
                            Integer.valueOf(arr.get(1)),
                            arr.get(4),
                            Double.valueOf(arr.get(3)),
                            arr.get(2).equals("Да")
                    ));
                } else showToast(primaryStage, "Неправильный ввод");
            } else showToast(primaryStage, "Неправильный ввод");
        }
        catch (Throwable e){
            showToast(primaryStage, "Неправильный ввод");
        }
    }
    @Override
    public void deleteFromDB(Object obj){
        RoomsView item = (RoomsView) obj;
        String sql = String.format("DELETE FROM rooms WHERE hotel_code = '%s' AND room_number = %s;", item.getHotel_code(), item.getRoom_number());
        boolean success = dataBase.changeTableValues(sql);
        if (!success) showToast(primaryStage, "Неудалось удалить строку");
    }
}
