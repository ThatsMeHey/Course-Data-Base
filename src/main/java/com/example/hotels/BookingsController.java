package com.example.hotels;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class BookingsController extends TablesController{
    //таблица с bронями
    @FXML
    private TableView<BookingsView> bookingsTable;
    @FXML
    private TableColumn<BookingsView, Void> bRowNumber;
    @FXML
    private TableColumn<BookingsView, String> bhCode;
    @FXML
    private TableColumn<BookingsView, String> bhName;
    @FXML
    private TableColumn<BookingsView, Integer> brNumber;
    @FXML
    private TableColumn<BookingsView, Integer> bId;
    @FXML
    private TableColumn<BookingsView, String> bvName;
    @FXML
    private TableColumn<BookingsView, Timestamp> bArrival;
    @FXML
    private TableColumn<BookingsView, Timestamp> bDeparture;
    @FXML
    private TableColumn<BookingsView, String> bDescription;
    private final ObservableList<BookingsView> bookingsObs = FXCollections.observableArrayList();
    private ObservableList<String> HotelList = FXCollections.observableArrayList();
    SortedList<String> HotelListSorted = new SortedList<>(HotelList);

    public void fillTable(){
        bookingsTable.setEditable(true);
        bookingsTable.setItems(bookingsObs);

        setupRowNumberColumn(bRowNumber);
        bhCode.setCellValueFactory(new PropertyValueFactory<>("hotel_code"));
        bhName.setCellValueFactory(new PropertyValueFactory<>("hotel_name"));
        brNumber.setCellValueFactory(new PropertyValueFactory<>("room_number"));
        bId.setCellValueFactory(new PropertyValueFactory<>("id"));
        bvName.setCellValueFactory(new PropertyValueFactory<>("name"));
        bArrival.setCellValueFactory(new PropertyValueFactory<>("arrival"));
        bDeparture.setCellValueFactory(new PropertyValueFactory<>("departure"));
        bDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        bhCode.setCellFactory(ComboBoxTableCell.forTableColumn(HotelListSorted)); //выпадающий список
        bvName.setCellFactory(TextFieldTableCell.forTableColumn());
        bArrival.setCellFactory(TextFieldTableCell.forTableColumn(new TimestampStringConverter()));
        bDeparture.setCellFactory(TextFieldTableCell.forTableColumn(new TimestampStringConverter()));
        bDescription.setCellFactory(TextFieldTableCell.forTableColumn());

        bvName.setOnEditCommit(this::handleEditCommit);
        bArrival.setOnEditCommit(this::handleEditCommit);
        bDeparture.setOnEditCommit(this::handleEditCommit);
        bDescription.setOnEditCommit(this::handleEditCommit);

        bhCode.setOnEditCommit(this::handleEditCommitComboBox);

        try {
            bookingsObs.setAll(dataBase.getBookings());
            HotelList.setAll(dataBase.getHotelsCodeNameRoom());
            HotelListSorted.setComparator(String::compareTo);
        }
        catch (SQLException ex){}
        deleteRow(bookingsTable, bookingsObs);
    }
    private void handleEditCommit(TableColumn.CellEditEvent<BookingsView, ?> event) {
        BookingsView row = event.getRowValue();
        TableColumn<BookingsView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        if (column == bvName){
            String sql = String.format("UPDATE reservations SET name = '%s' WHERE id = %s",
                    escapeSql((String)newValue), row.getId());
            dataBase.changeTableValues(sql);
            row.setName((String)newValue);
        }
        else if (column == bArrival){
            Timestamp newTime = (Timestamp)newValue;
            Timestamp now = new Timestamp(System.currentTimeMillis());
            System.out.println(now);
            if (newTime.compareTo(now) > 0 && newTime.compareTo(row.getDeparture()) < 0){
                String sql = String.format("UPDATE reservations SET arrival_date = '%s' WHERE id = %s",
                        newTime, row.getId());
                dataBase.changeTableValues(sql);
                row.setArrival(newTime);
            }
            else showToast(primaryStage, "Заселение в брони должно быть раньше выселения и позже текущего времени");
        }
        else if (column == bDeparture){
            Timestamp newTime = (Timestamp)newValue;
            Timestamp now = new Timestamp(System.currentTimeMillis());
            System.out.println(now);
            if (newTime.compareTo(row.getArrival()) > 0){
                String sql = String.format("UPDATE reservations SET departure_date = '%s' WHERE id = %s",
                        newTime, row.getId());
                dataBase.changeTableValues(sql);
                row.setDeparture(newTime);
            }
            else showToast(primaryStage, "Выселение должно быть позже заселения");
        }
        else if (column == bDescription){
            String sql = String.format("UPDATE reservations SET description = '%s' WHERE id = %s",
                    escapeSql((String)newValue), row.getId());
            dataBase.changeTableValues(sql);
            row.setDescription((String)newValue);
        }
        bookingsTable.refresh();
    }
    private void handleEditCommitComboBox(TableColumn.CellEditEvent<BookingsView, ?> event)
    {
        BookingsView row = event.getRowValue();
        TableColumn<BookingsView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        String[] parts = splitByFirstAndLastSpace((String)newValue);
        String sql = String.format("UPDATE reservations SET hotel_code = '%s' WHERE id = %s;" +
                        "UPDATE reservations SET room_number = %s WHERE id = %s;",
                parts[0], row.getId(), parts[2], row.getId());
        dataBase.changeTableValues(sql);
        row.setHotel_code(parts[0]);
        row.setHotel_name(parts[1]);
        row.setRoom_number(Integer.valueOf(parts[2]));

        bookingsTable.refresh();
    }
    @FXML
    @Override
    public void addNew() {
        InputWindow input = new InputWindow(this, new ComboBoxWithName(new ComboBox<String>(HotelListSorted), "Гостинница"), "Имя", "Дата заселения", "Дата выселения", "Описание");
        input.openWindow();
    }
    @FXML
    @Override
    public void openFilters(){
        if (filterWindow == null) {
            filterWindow = new FilterWindow(bookingsTable, bookingsObs);
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
            if (!(arrival.compareTo(now) > 0 && arrival.compareTo(departure) < 0)){
                showToast(primaryStage, "Заселение в брони должно быть раньше выселения и позже текущего времени");
                return;
            }

            String[] parts = splitByFirstAndLastSpace(arr.get(0));
            if (parts[0].length() == 6 && parts[0].matches("[A-Z0-9]+")) {
                String sql = String.format("INSERT INTO reservations (hotel_code, room_number, name, arrival_date, departure_date, description) " +
                                "VALUES ('%s', %s, '%s', '%s', '%s', '%s') RETURNING id;", parts[0], parts[2], escapeSql(arr.get(1)),
                        arr.get(2), arr.get(3), escapeSql(arr.get(4)));
                int success = dataBase.getId(sql);
                if (success != -1) {
                    bookingsObs.add(new BookingsView(
                            arr.get(1),
                            success,
                            parts[0],
                            parts[1],
                            Integer.valueOf(parts[2]),
                            arrival,
                            departure,
                            arr.get(4)
                    ));
                } else showToast(primaryStage, "Неправильный ввод");
            } else showToast(primaryStage, "Неправильный ввод кода гостинницы и номера комнаты");
        }
        catch (Throwable e){
            showToast(primaryStage, "Неправильный ввод");
        }
    }
    @Override
    public void deleteFromDB(Object obj){
        BookingsView item = (BookingsView)obj;
        String sql = String.format("DELETE FROM reservations WHERE id = %s;", item.getId());
        boolean success = dataBase.changeTableValues(sql);
        if (!success) showToast(primaryStage, "Неудалось удалить строку");
    }
}
