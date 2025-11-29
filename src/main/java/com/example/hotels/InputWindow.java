package com.example.hotels;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

class wrapper{
    public boolean isTextField;
    public TextField text;
    public ComboBox<String> box;
    public wrapper(TextField text){
        this.text = text;
        isTextField = true;
    }
    public wrapper(ComboBox<String> box){
        this.box = box;
        isTextField = false;
    }
    public String getValue(){
        if (isTextField){
            return text.getText();
        }
        else {
            return box.getValue();
        }
    }
    public Control getElement(){
        if (isTextField) return text;
        else return box;
    }
}

public class InputWindow {
    private int amount = -1;
    private double maxHeight = 0;
    public Boolean plusButton = false;
    private ArrayList<wrapper> fields = new ArrayList<>();
    private Stage formStage;
    GridPane formLayout;

    public <T> InputWindow(TablesController controller, Object... args) {
        formStage = new Stage(StageStyle.UNDECORATED);
        formLayout = new GridPane();
        formLayout.setVgap(10);
        formLayout.setHgap(10);
        int i = 0;
        int argsSize = 0;
        for (Object a : args){
            argsSize++;
            if (a instanceof Integer){
                plusButton = true;
                amount = (int)a;
                argsSize--;
            }
        }
        for (Object a : args) {
            if (a instanceof String) {
                TextField textField = new TextField();
                textField.setPromptText(String.valueOf(a));
                fields.add(new wrapper(textField));
                formLayout.add(textField, 0, i);
                GridPane.setHgrow(textField, Priority.ALWAYS);
            }
            else if (a instanceof ComboBoxWithName){
                ComboBoxWithName box = (ComboBoxWithName)a;
                ComboBox<String> tempBox = box.getBox();
                tempBox.setPromptText(box.getName());
                fields.add(new wrapper(tempBox));
                formLayout.add(tempBox, 0, i);
                GridPane.setHgrow(tempBox, Priority.ALWAYS);
            }
            i++;
            if (amount != -1 && i == argsSize - amount) {
                GridPane.setMargin(fields.get(fields.size()-1).getElement(), new Insets(0, 0, 20, 0));
            }
        }
        Button submitBtn = new Button("Добавить");
        if (plusButton){
            Button plusBtn = new Button("+");
            formLayout.add(plusBtn, 0, i);
            i++;
            GridPane.setHalignment(plusBtn, HPos.CENTER);
            plusBtn.setOnAction(e -> {
                int filedsSize = fields.size();
                for (int j = fields.size()-amount; j < filedsSize; j++){
                    if (fields.get(j).isTextField){
                        TextField textField = new TextField();
                        textField.setPromptText(fields.get(j).text.getPromptText());
                        fields.add(new wrapper(textField));
                        formLayout.add(textField, 0, fields.size()+1);
                        GridPane.setHgrow(textField, Priority.ALWAYS);
                    }
                    else{
                        ComboBox<String> tempBox = new ComboBox<String>(fields.get(j).box.getItems());
                        tempBox.setPromptText(fields.get(j).box.getPromptText());
                        fields.add(new wrapper(tempBox));
                        formLayout.add(tempBox, 0, fields.size()+1);
                        GridPane.setHgrow(tempBox, Priority.ALWAYS);
                    }
                }
                GridPane.setMargin(fields.get(fields.size()-1).getElement(), new Insets(0, 0, 20, 0));

                int newIndex = GridPane.getRowIndex(fields.get(fields.size()-1).getElement());
                GridPane.setRowIndex(plusBtn, newIndex + 1);
                GridPane.setRowIndex(submitBtn, newIndex + 2);

                if (!(formStage.getScene().getRoot() instanceof ScrollPane)) {
                    double currentHeight = formStage.getHeight();
                    System.out.println(currentHeight);
                    if (currentHeight >= maxHeight-1) {
                        ScrollPane scrollPane = new ScrollPane(formLayout);
                        scrollPane.setFitToWidth(true);
                        formStage.setScene(new Scene(scrollPane, 300, 2*maxHeight));
                    }
                }

            });
        }

        formLayout.add(submitBtn, 0, i);
        GridPane.setHalignment(submitBtn, HPos.CENTER);

        submitBtn.setOnAction(e -> {
            ArrayList<String> values = new ArrayList<>();
            for (int j = 0 ; j < fields.size(); j++){
                values.add(fields.get(j).getValue());
            }
            controller.addValue(values);
            formStage.close();
        });
        formStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                formStage.close();
            }
        });

    }
    public void openWindow() {
        formStage.setScene(new Scene(formLayout));
        formStage.setWidth(300);
        formStage.show();
        maxHeight = formStage.getHeight();
    }

}
