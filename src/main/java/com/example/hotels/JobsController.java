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

public class JobsController extends TablesController{
    //таблица с должностями
    @FXML
    private TableView<JobView> positionsTable;
    @FXML
    private TableColumn<HotelsView, Void> jRowNumber;
    @FXML
    private TableColumn<JobView, String> jCode;
    @FXML
    private TableColumn<JobView, String> jName;
    private final ObservableList<JobView> jobObs = FXCollections.observableArrayList();

    public void fillTable(){
        positionsTable.setEditable(true);
        positionsTable.setItems(jobObs);

        setupRowNumberColumn(jRowNumber);
        jCode.setCellValueFactory(new PropertyValueFactory<>("job_code"));
        jName.setCellValueFactory(new PropertyValueFactory<>("name"));

        jCode.setCellFactory(TextFieldTableCell.forTableColumn());
        jName.setCellFactory(TextFieldTableCell.forTableColumn());

        for (TableColumn<JobView, ?> column : positionsTable.getColumns()) {
            column.setOnEditCommit(this::handleEditCommit);
        }

        try {
            jobObs.setAll(dataBase.getJobs());
        }
        catch (SQLException ex){}
        deleteRow(positionsTable, jobObs);
    }
    private void handleEditCommit(TableColumn.CellEditEvent<JobView, ?> event) {
        JobView row = event.getRowValue();
        TableColumn<JobView, ?> column = event.getTableColumn();
        Object newValue = event.getNewValue();

        if (column == jCode){
            if (((String)newValue).length() == 6 && ((String)newValue).matches("[A-Z0-9]+")){
                dataBase.changeTableValues("job_positions", "job_code", (String)newValue,
                        "job_code", row.getJob_code());
                row.setJob_code((String)newValue);
            }
            else showToast(primaryStage, "Код профессии должен быть уникальным, состоять из 6 символов, и должен включать только цифры и заглавные буквы латинского алфавита");
        }
        else{
            boolean success =  dataBase.changeTableValues("job_positions", "name", (String)newValue,
                    "job_code", row.getJob_code());
            if (success) row.setName((String)newValue);
            else showToast(primaryStage, "Название профессии должно быть уникальным");
        }
        positionsTable.refresh();
    }
    @FXML
    @Override
    public void addNew() {
        InputWindow input = new InputWindow(this, "Код профессии", "Название профессии");
        input.openWindow();
    }
    @FXML
    @Override
    public void openFilters(){
        if (filterWindow == null) {
            filterWindow = new FilterWindow(positionsTable, jobObs);
            filterWindow.openWindow();
        }
        else {
            filterWindow.makeVisible();
        }
    }

    @Override
    public void addValue(ArrayList<String> arr){
        if (arr.get(0).length() == 6 && arr.get(0).matches("[A-Z0-9]+")) {
            String sql = String.format("INSERT INTO job_positions (job_code, name) " +
                            "VALUES ('%s', '%s');", arr.get(0), escapeSql(arr.get(1)));
            boolean success = dataBase.changeTableValues(sql);
            if (success) {
                jobObs.add(new JobView(
                        arr.get(0),
                        arr.get(1)
                ));
            } else showToast(primaryStage, "Неправильный ввод");
        }
        else showToast(primaryStage, "Неправильный ввод");
    }
    @Override
    public void deleteFromDB(Object obj){
        JobView item = (JobView) obj;
        String sql = String.format("DELETE FROM job_positions WHERE job_code = '%s';", item.getJob_code());
        boolean success = dataBase.changeTableValues(sql);
        if (!success) showToast(primaryStage, "Неудалось удалить строку");
    }
}
