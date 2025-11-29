package com.example.hotels;

import javafx.fxml.FXML;

import java.util.ArrayList;

public abstract class ReportsTableController extends TablesController{
    public abstract void findResults();
    @FXML
    public void addNew() {}
    public void addValue(ArrayList<String> arr) {}
    public void deleteFromDB(Object item) {}
    @FXML
    public void openFilters() {}
}
