package com.example.hotels;

import javafx.scene.control.ComboBox;

public class ComboBoxWithName {
    private ComboBox<String> comboBox;
     private String name;

    public ComboBoxWithName( ComboBox<String> box, String name){
        this.comboBox = box;
        this.name = name;
    }

    public ComboBox<String> getBox() {return comboBox;}
    public String getName() {return name;}
}
