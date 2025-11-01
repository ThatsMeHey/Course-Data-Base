module com.example.hotels {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.hotels to javafx.fxml;
    exports com.example.hotels;
}