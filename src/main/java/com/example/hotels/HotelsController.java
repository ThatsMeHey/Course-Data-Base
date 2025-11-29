package com.example.hotels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;
import java.util.ArrayList;

public class HotelsController extends TablesController {
    //таблица с гостинницами
    @FXML
    private TableView<HotelsView> hotelsTable;
    @FXML
    private TableColumn<HotelsView, Void> hRowNumber;
    @FXML
    private TableColumn<HotelsView, String> code;
    @FXML
    private TableColumn<HotelsView, String> name;
    @FXML
    private TableColumn<HotelsView, String> inn;
    @FXML
    private TableColumn<HotelsView, String> director;
    @FXML
    private TableColumn<HotelsView, String> owner;
    @FXML
    private TableColumn<HotelsView, String> address;
    private final ObservableList<HotelsView> hotelsObs = FXCollections.observableArrayList();

    public void fillHotels(){
        hotelsTable.setEditable(true);
        hotelsTable.setItems(hotelsObs);

        setupRowNumberColumn(hRowNumber);
        code.setCellValueFactory(new PropertyValueFactory<>("hotel_code"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        inn.setCellValueFactory(new PropertyValueFactory<>("inn"));
        director.setCellValueFactory(new PropertyValueFactory<>("director"));
        owner.setCellValueFactory(new PropertyValueFactory<>("owner"));
        address.setCellValueFactory(new PropertyValueFactory<>("address"));

        code.setCellFactory(TextFieldTableCell.forTableColumn());
        name.setCellFactory(TextFieldTableCell.forTableColumn());
        inn.setCellFactory(TextFieldTableCell.forTableColumn());
        director.setCellFactory(TextFieldTableCell.forTableColumn());
        owner.setCellFactory(TextFieldTableCell.forTableColumn());
        address.setCellFactory(TextFieldTableCell.forTableColumn());

        for (TableColumn<HotelsView, ?> column : hotelsTable.getColumns()) {
            column.setOnEditCommit(this::handleEditCommit);
        }

        try {
            hotelsObs.setAll(dataBase.getHotels());
        }
        catch (SQLException ex){}
        deleteRow(hotelsTable, hotelsObs);
    }
    private void handleEditCommit(TableColumn.CellEditEvent<HotelsView, ?> event) {
        HotelsView row = event.getRowValue();
        TableColumn<HotelsView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        if (column == code){
            if (((String)newValue).length() == 6 && ((String)newValue).matches("[A-Z0-9]+")){
                dataBase.changeTableValues("hotels", "hotel_code", (String)newValue,
                        "hotel_code", row.getHotel_code());
                row.setHotel_code((String)newValue);
            }
            else showToast(primaryStage, "Код гостинницы должен быть уникальным, состоять из 6 символов, и должен включать только цифры и заглавные буквы латинского алфавита");
        }
        else if (column == inn){
            if (((String)newValue).length() == 8 && ((String)newValue).matches("[0-9]+")){
                dataBase.changeTableValues("hotels", "inn", (String)newValue,
                        "hotel_code", row.getHotel_code());
                row.setInn((String)newValue);
            }
            else showToast(primaryStage, "ИНН должен быть уникальным, состоять из 8 символов, и должен включать только цифры");
        }
        else {
            if (column == name){
                dataBase.changeTableValues("hotels", "name", (String)newValue,
                        "hotel_code", row.getHotel_code());
                row.setName((String)newValue);
            }
            else if (column == director){
                dataBase.changeTableValues("hotels", "director", (String)newValue,
                        "hotel_code", row.getHotel_code());
                row.setDirector((String)newValue);
            }
            else if (column == owner){
                dataBase.changeTableValues("hotels", "owner", (String)newValue,
                        "hotel_code", row.getHotel_code());
                row.setOwner((String)newValue);
            }
            else if (column == address){
                dataBase.changeTableValues("hotels", "address", (String)newValue,
                        "hotel_code", row.getHotel_code());
                row.setAddress((String)newValue);
            }
        }
        hotelsTable.refresh();
    }
    @FXML
    @Override
    public void addNew() {
        InputWindow input = new InputWindow(this, "Код гостинницы", "Название гостинницы", "ИНН", "Директор", "Владелец", "Адрес");
        input.openWindow();
    }

    @FXML
    @Override
    public void openFilters(){
        if (filterWindow == null) {
            filterWindow = new FilterWindow(hotelsTable, hotelsObs);
            filterWindow.openWindow();
        }
        else {
            filterWindow.makeVisible();
        }
    }

    @Override
    public void addValue(ArrayList<String> arr){
        if (arr.get(0).length() == 6 && arr.get(0).matches("[A-Z0-9]+") &&
                arr.get(2).length() == 8 && arr.get(2).matches("[0-9]+")) {
            String sql = String.format("INSERT INTO hotels (hotel_code, name, inn, director, owner, address) " +
                            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');", arr.get(0), escapeSql(arr.get(1)), arr.get(2),
                    escapeSql(arr.get(3)), escapeSql(arr.get(4)), escapeSql(arr.get(5)));
            boolean success = dataBase.changeTableValues(sql);
            if (success) {
                hotelsObs.add(new HotelsView(
                        arr.get(0),
                        arr.get(1),
                        arr.get(2),
                        arr.get(3),
                        arr.get(4),
                        arr.get(5)
                ));
            } else showToast(primaryStage, "Неправильный ввод");
        }
        else showToast(primaryStage, "Неправильный ввод");
    }

    @Override
    public void deleteFromDB(Object obj){
        HotelsView item = (HotelsView) obj;
        String sql = String.format("DELETE FROM hotels WHERE hotel_code = '%s';", item.getHotel_code());
        boolean success = dataBase.changeTableValues(sql);
        if (!success) showToast(primaryStage, "Неудалось удалить строку");
    }
}
