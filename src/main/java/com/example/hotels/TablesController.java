package com.example.hotels;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;


public abstract class TablesController {
    protected JavaPostgres dataBase = new JavaPostgres();
    protected Stage primaryStage;
    protected Scene primaryScene;
    public FilterWindow filterWindow;
    public double width, height;

    public void setStageParameters(Stage st, Scene sn)
    {
        primaryStage = st;
        primaryScene = sn;
    }

    protected <T> void setupRowNumberColumn(TableColumn<T, Void> column) {
        column.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });
    }
    protected  <T> void setTimestampDisplayFormat(TableColumn<T, Timestamp> column) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        column.setCellFactory(col -> new TableCell<T, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.toLocalDateTime().format(formatter));
            }
        });
    }

    public abstract void fillTable();

    @FXML
    protected void onBackButtonClick(){
        primaryStage.setScene(primaryScene);
        if (filterWindow != null){
            filterWindow.closeFilter();
        }
    }

    public String escapeSql(String input) {
        if (input == null) return null;
        return input.replace("'", "''");
    }

    public void showToast(Stage owner, String message) {
        Popup popup = new Popup();

        Label label = new Label(message);
        label.setStyle(
        "-fx-background-color: rgba(255,0,0,0.7);" +
                "-fx-text-fill: white;" +
                "-fx-padding: 10px;" +
                "-fx-border-radius: 5px;" +
                "-fx-background-radius: 5px;"
        );
        popup.getContent().add(label);

        popup.setAutoHide(true);
        popup.show(owner);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> popup.hide()));
        timeline.play();
    }

    public static String[] splitByFirstAndLastSpace(String s) {
        int first = s.indexOf(' ');
        int last = s.lastIndexOf(' ');
        return new String[] {
                s.substring(0, first),
                s.substring(first + 1, last).trim(),
                s.substring(last + 1)
        };
    }
    @FXML
    public abstract void addNew();
    public abstract void addValue(ArrayList<String> arr);

    public <T> void deleteRow(TableView<T> tableView, ObservableList<T> list){
        tableView.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();

            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("Удалить");

            deleteItem.setOnAction(e -> {
                T item = row.getItem();
                if (item != null) {
                    deleteFromDB(item);
                    list.remove(item);
                }
            });

            contextMenu.getItems().add(deleteItem);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings
                            .when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(contextMenu)
            );

            return row;
        });
    }
    public abstract void deleteFromDB(Object item);

    @FXML
    public abstract void openFilters();

}
