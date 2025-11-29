package com.example.hotels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class VisitorsController extends TablesController{
    //таблица с посетителями
    @FXML
    private TableView<VisitorsView> visitorsTable;
    @FXML
    private TableColumn<HotelsView, Void> vRowNumber;
    @FXML
    private TableColumn<VisitorsView, String> vhCode;
    @FXML
    private TableColumn<VisitorsView, String> vhName;
    @FXML
    private TableColumn<VisitorsView, Integer> vrNumber;
    @FXML
    private TableColumn<VisitorsView, Integer> vId;
    @FXML
    private TableColumn<VisitorsView, String> vName;
    @FXML
    private TableColumn<VisitorsView, Timestamp> vArrival;
    @FXML
    private TableColumn<VisitorsView, Timestamp> vDeparture;
    @FXML
    private TableColumn<VisitorsView, String> vDescription;
    private final ObservableList<VisitorsView> visitorsObs = FXCollections.observableArrayList();
    private ObservableList<String> HotelList = FXCollections.observableArrayList();
    SortedList<String> HotelListSorted = new SortedList<>(HotelList);

    public void fillVisitors(){
        visitorsTable.setEditable(true);
        visitorsTable.setItems(visitorsObs);

        setupRowNumberColumn(vRowNumber);
        vhCode.setCellValueFactory(new PropertyValueFactory<>("hotel_code"));
        vhName.setCellValueFactory(new PropertyValueFactory<>("hotel_name"));
        vrNumber.setCellValueFactory(new PropertyValueFactory<>("room_number"));
        vId.setCellValueFactory(new PropertyValueFactory<>("id"));
        vName.setCellValueFactory(new PropertyValueFactory<>("name"));
        vArrival.setCellValueFactory(new PropertyValueFactory<>("arrival"));
        vDeparture.setCellValueFactory(new PropertyValueFactory<>("departure"));
        vDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        vName.setCellFactory(TextFieldTableCell.forTableColumn());
        vDeparture.setCellFactory(TextFieldTableCell.forTableColumn(new TimestampStringConverter()));
        vDescription.setCellFactory(TextFieldTableCell.forTableColumn());

        vhName.setOnEditCommit(this::handleEditCommit);
        vArrival.setCellFactory(column -> new TableCell<VisitorsView, Timestamp>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    // Конвертируем Timestamp в LocalDateTime и форматируем
                    LocalDateTime dateTime = item.toLocalDateTime();
                    setText(dateTime.format(formatter));
                }
            }
        });
        vDeparture.setOnEditCommit(this::handleEditCommit);
        vDescription.setOnEditCommit(this::handleEditCommit);

        try {
            visitorsObs.setAll(dataBase.getVisitors());
        }
        catch (SQLException ex){}
        deleteRow(visitorsTable, visitorsObs);
    }

    private void handleEditCommit(TableColumn.CellEditEvent<VisitorsView, ?> event) {
        VisitorsView row = event.getRowValue();
        TableColumn<VisitorsView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        if (column == vName){
            String sql = String.format("UPDATE visitors SET name = '%s' WHERE id = %s",
                    escapeSql((String)newValue), row.getId());
            dataBase.changeTableValues(sql);
            row.setName((String)newValue);
        }
        else if (column == vDeparture){
            Timestamp newTime = (Timestamp)newValue;
            Timestamp now = new Timestamp(System.currentTimeMillis());
            System.out.println(now);
            if (newTime.compareTo(row.getArrival()) > 0 && newTime.compareTo(now) > 0){
                String sql = String.format("UPDATE visitors SET departure_date = '%s' WHERE id = %s",
                        newTime, row.getId());
                dataBase.changeTableValues(sql);
                row.setDeparture(newTime);
            }
            else showToast(primaryStage, "Выселение должно быть позже заселения и текущего времени");
        }
        else if (column == vDescription){
            String sql = String.format("UPDATE visitors SET description = '%s' WHERE id = %s",
                    escapeSql((String)newValue), row.getId());
            dataBase.changeTableValues(sql);
            row.setDescription((String)newValue);
        }
        visitorsTable.refresh();
    }
    @FXML
    @Override
    public void addNew() {
        try {
            HotelList.setAll(dataBase.getHotelsCodeNameRoomEmpty());
            HotelListSorted.setComparator(String::compareTo);
        }
        catch (SQLException e){}
        InputWindow input = new InputWindow(this, new ComboBoxWithName(new ComboBox<String>(HotelListSorted), "Гостинница"), "Имя", "Дата заселения", "Дата выселения", "Описание");
        input.openWindow();
    }
    @FXML
    @Override
    public void openFilters(){
        if (filterWindow == null) {
            filterWindow = new FilterWindow(visitorsTable, visitorsObs);
            filterWindow.openWindow();
        }
        else {
            filterWindow.makeVisible();
        }
    }

    @Override
    public void addValue(ArrayList<String> arr){
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(arr.get(2), formatter);
            Timestamp arrival = Timestamp.valueOf(dateTime);
            dateTime = LocalDateTime.parse(arr.get(3), formatter);
            Timestamp departure = Timestamp.valueOf(dateTime);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if (!(arrival.compareTo(now) < 0 && arrival.compareTo(departure) < 0 && departure.compareTo(now) > 0)){
                showToast(primaryStage, "Заселение в брони должно быть раньше выселения и раньше текущего времени. Выселение должно быть позже текущего времени");
                return;
            }

            String[] parts = splitByFirstAndLastSpace(arr.get(0));
            if (parts[0].length() == 6 && parts[0].matches("[A-Z0-9]+")) {
                String sql = String.format("INSERT INTO visitors (hotel_code, room_number, name, arrival_date, departure_date, description) " +
                                "VALUES ('%s', %s, '%s', '%s', '%s', '%s') RETURNING id;", parts[0], parts[2], escapeSql(arr.get(1)),
                        arr.get(2), arr.get(3), escapeSql(arr.get(4)));
                int success = dataBase.getId(sql);
                if (success != -1) {
                    visitorsObs.add(new VisitorsView(
                            parts[0],
                            parts[1],
                            Integer.valueOf(parts[2]),
                            success,
                            arr.get(1),
                            arrival,
                            departure,
                            arr.get(4)
                    ));
                    HotelList.remove(arr.get(0));
                } else showToast(primaryStage, "Неправильный ввод");
            } else showToast(primaryStage, "Неправильный ввод кода гостинницы и номера комнаты");
        }
        catch (Throwable e){
            showToast(primaryStage, "Неправильный ввод");
        }
    }
    @Override
    public void deleteFromDB(Object obj){
        VisitorsView item = (VisitorsView) obj;
        String sql = String.format("DELETE FROM visitors WHERE id = %s;", item.getId());
        boolean success = dataBase.changeTableValues(sql);
        if (!success) showToast(primaryStage, "Неудалось удалить строку");
        else HotelList.add(String.format("%s %s %s", item.getHotel_code(), item.getHotel_name(), item.getRoom_number()));
    }
}
