package com.example.hotels;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;


public class FilterWindow<T> {
    private ArrayList<TextField> fields = new ArrayList<>();
    private ArrayList<MenuButton> filters = new ArrayList<>();
    private ComboBox<String> columns = new ComboBox<>();
    private ComboBox<String> variants = new ComboBox<>();
    private ArrayList<Pair<Button, Button>> buttons = new ArrayList<>();
    private Stage formStage;
    GridPane formLayout;
    private FilteredList<T> filteredList;
    private ObservableList<T> originalItems;
    private TableView<T> tableView;
    SortedList<T> sortedList;
    ReportsTableController repController;
    private boolean fullWindow = false;

    public FilteredList<T> getFilteredList() {return filteredList;}
    public void setRepController(ReportsTableController controller){
        repController = controller;
        Label sortColumn = new Label("Сортировать по:");
        sortColumn.setPadding(new Insets(20, 0, 0, 0));
        formLayout.add(sortColumn, 0, 5);
        int i = 0;
        tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, Event::consume);
        tableView.addEventFilter(MouseEvent.MOUSE_RELEASED, Event::consume);
        tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, Event::consume);
        for (TableColumn<T, ?> col : tableView.getColumns()) {
            if (i == 0) {
                i++;
                continue;
            }
            if (i == 1){
                columns.setValue(col.getText());
            }
            TableColumn<T, Object> newCol = new TableColumn<>(col.getText());
            columns.getItems().add(newCol.getText());
            i++;
        }
        formLayout.add(columns, 0, 6);

        Label sortDirection = new Label("Напрвление сортировки:");
        formLayout.add(sortDirection, 0, 7);
        variants.getItems().add("По возрастанию");
        variants.getItems().add("По убыванию");
        variants.setValue("По возрастанию");
        formLayout.add(variants, 0, 8);

        Button createReportBtn = new Button("Создать отчёт");
        formLayout.add(createReportBtn, 0, 9);
        createReportBtn.setOnAction(e -> formStage.toBack());
        fullWindow = true;

        applySorting();

        columns.setOnAction(e -> applySorting());
        variants.setOnAction(e -> applySorting());
    }
    private void applySorting(){
        TableColumn<T, ?> column = tableView.getColumns().get(columns.getSelectionModel().getSelectedIndex() + 1); // or get by index/name
        tableView.getSortOrder().clear();
        tableView.getSortOrder().add(column);

        if (variants.getSelectionModel().getSelectedIndex() == 0)
            column.setSortType(TableColumn.SortType.ASCENDING);
        else
            column.setSortType(TableColumn.SortType.DESCENDING);
        tableView.sort();
    }

    public FilterWindow(TableView<T> tableView, ObservableList<T> originalItems) {
        this.originalItems = originalItems; // твои данные
        filteredList = new FilteredList<>(originalItems, p -> true);
        this.tableView = tableView;
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        this.tableView.setItems(sortedList);

        formStage = new Stage(StageStyle.UNDECORATED);
        formLayout = new GridPane();
        formLayout.setVgap(10);
        formLayout.setHgap(10);

        int i = 0;
        //первичное заполнение
        for (TableColumn<T, ?> column : tableView.getColumns()) {
            if (i == 0) {
                i++;
                continue;
            }
            TextField textField = new TextField();
            MenuButton menuButton = new MenuButton(column.getText());
            textField.setPromptText(column.getText());
            formLayout.add(textField, i-1, 0);
            formLayout.add(menuButton, i-1, 1);
            fields.add(textField);
            filters.add(menuButton);

            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.millis(500),
                        ae -> applyFilter()
                ));
                timeline.setCycleCount(1);
                timeline.play();
            });


            Set<String> uniqueValues = getUniqueValuesForColumn(column);
            for (String val : uniqueValues) {
                CheckBox cb = new CheckBox(val);
                cb.setSelected(true);
                cb.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilter());

                CustomMenuItem menuItem = new CustomMenuItem(cb);
                menuItem.setHideOnClick(false);
                menuButton.getItems().add(menuItem);
            }

            Button pickAll = new Button("Включить всё");
            Button disableAll = new Button("Исключить всё ");
            Pair<Button, Button> pair = new Pair<>(pickAll, disableAll);
            formLayout.add(pickAll, i-1, 2);
            formLayout.add(disableAll, i-1, 3);
            buttons.add(pair);

            int k = i-1;
            pickAll.setOnAction(e -> {
                for (MenuItem item : filters.get(k).getItems()){
                    if (item instanceof CustomMenuItem cmi) {
                        Node content = cmi.getContent();
                        if (content instanceof CheckBox cb) {
                            cb.setSelected(true);
                        }
                    }
                }
            });
            disableAll.setOnAction(e -> {
                for (MenuItem item : filters.get(k).getItems()){
                    if (item instanceof CustomMenuItem cmi) {
                        Node content = cmi.getContent();
                        if (content instanceof CheckBox cb) {
                            cb.setSelected(false);
                        }
                    }
                }
            });
            i++;
        }
        Button clearFilters = new Button("Сбросить фильтры");
        formLayout.add(clearFilters, 0, 4);

        clearFilters.setOnAction(e -> {
            for (TextField text : fields){
                text.setText("");
            }
            for (MenuButton menuButton : filters){
                for (MenuItem item : menuButton.getItems()){
                    if (item instanceof CustomMenuItem cmi) {
                        Node content = cmi.getContent();
                        if (content instanceof CheckBox cb) {
                            cb.setSelected(true);
                        }
                    }
                }
            }
        });
        makeUpdatetable();
    }
    private void makeUpdatetable()
    {
        originalItems.addListener((ListChangeListener<T>) change -> {
            TableView<T> copyTable = new TableView();
            for (TableColumn<T, ?> col : tableView.getColumns()) {
                TableColumn<T, Object> newCol = new TableColumn<>(col.getText());

                // Копируем CellValueFactory
                newCol.setCellValueFactory(cellData -> {
                    ObservableValue<?> originalValue = col.getCellObservableValue(cellData.getValue());
                    return (ObservableValue<Object>) originalValue;
                });

                copyTable.getColumns().add(newCol);
            }

            TableView<T> copyTableOrig = new TableView();
            for (TableColumn<T, ?> col : tableView.getColumns()) {
                TableColumn<T, Object> newCol = new TableColumn<>(col.getText());

                // Копируем CellValueFactory
                newCol.setCellValueFactory(cellData -> {
                    ObservableValue<?> originalValue = col.getCellObservableValue(cellData.getValue());
                    return (ObservableValue<Object>) originalValue;
                });

                copyTableOrig.getColumns().add(newCol);
            }
            copyTableOrig.setItems(originalItems);

            while (change.next()) {
                if (change.wasRemoved()) {
                    ObservableList<T> removedObservable = FXCollections.observableArrayList(change.getRemoved());
                    copyTable.setItems(removedObservable);
                    for (int i = 0; i < tableView.getColumns().size(); i++) {
                        if (i == 0) continue;
                        TableColumn<T, ?> originalCol = copyTableOrig.getColumns().get(i);
                        TableColumn<T, ?> copyCol = copyTable.getColumns().get(i);

                        Set<String> uniqueValuesCopy = getUniqueValuesForColumn(copyCol);
                        Set<String> uniqueValuesOrig = getUniqueValuesForColumn(originalCol);
                        for (String val : uniqueValuesCopy) {
                            if (!uniqueValuesOrig.contains(val)) {
                                filters.get(i - 1).getItems().removeIf(item -> {
                                    if (item instanceof CustomMenuItem cmi) {
                                        Node content = cmi.getContent();
                                        if (content instanceof CheckBox cb) {
                                            return cb.getText().equals(val); // если имя совпадает — удаляем
                                        }
                                    }
                                    return false;
                                });
                            }
                        }
                    }
                }
                else if (change.wasAdded()) {
                    ObservableList<T> addedObservable = FXCollections.observableArrayList(change.getAddedSubList());
                    copyTable.setItems(addedObservable);
                    for (int i = 0; i < tableView.getColumns().size(); i++) {
                        if (i == 0) continue;
                        TableColumn<T, ?> copyCol = copyTable.getColumns().get(i);

                        Set<String> uniqueValuesCopy = getUniqueValuesForColumn(copyCol);
                        Set<String> uniqueValuesOrig = ToStrings(filters.get(i-1).getItems());
                        for (String val : uniqueValuesCopy) {
                            if (!uniqueValuesOrig.contains(val)) {
                                CheckBox cb = new CheckBox(val);
                                cb.setSelected(true);
                                cb.selectedProperty().addListener((obs, oldVal, newVal) -> applyFilter());

                                CustomMenuItem menuItem = new CustomMenuItem(cb);
                                menuItem.setHideOnClick(false);
                                filters.get(i - 1).getItems().add(menuItem);
                            }
                        }
                    }
                }
            }
        });
    }
    private void applyFilter(){
        filteredList.setPredicate(row -> {
            for (int i = 1; i < tableView.getColumns().size(); i++) {
                TableColumn<T, ?> col = tableView.getColumns().get(i);
                MenuButton mb = filters.get(i-1);

                Set<String> allowedValues = mb.getItems().stream()
                        .map(item -> (CheckBox)((CustomMenuItem)item).getContent())
                        .filter(CheckBox::isSelected)
                        .map(CheckBox::getText)
                        .collect(Collectors.toSet());
                Object cellValue = col.getCellObservableValue(row).getValue();
                String cellText = cellValue == null ? "" : cellValue.toString();
                if (!(allowedValues.contains(cellText) && cellText.toLowerCase().contains(fields.get(i-1).getText().toLowerCase()))) {
                    return false;
                }
            }
            return true;
        });
        if (repController != null) repController.findResults();
    }
    private Set<String> ToStrings(ObservableList<MenuItem> list){
        Set<String> newSet = new HashSet<>();
        for (MenuItem item : list){
            if (item instanceof CustomMenuItem cmi) {
                Node content = cmi.getContent();
                if (content instanceof CheckBox cb) {
                    newSet.add(cb.getText());
                }
            }
        }
        return newSet;
    }
    private Set<String> getUniqueValuesForColumn(TableColumn<T, ?> column) {
        if (column.getCellValueFactory() == null) {
            return new HashSet<>();
        }
        return column.getTableView().getItems().stream()
                .map(row -> {
                    Object val = column.getCellObservableValue(row).getValue();
                    return val == null ? "" : val.toString();
                })
                .collect(Collectors.toSet());
    }

    public void openWindow() {
        if (fullWindow) {
            formLayout.setPadding(new Insets(30, 0, 0, 30));
            formStage.setScene(new Scene(formLayout, repController.width, repController.height));
        }
        else formStage.setScene(new Scene(formLayout));
        formStage.show();
    }
    public void makeVisible(){
        formStage.toFront();
        formStage.requestFocus();
    }
    public void closeFilter(){
        formStage.close();
    }
}
