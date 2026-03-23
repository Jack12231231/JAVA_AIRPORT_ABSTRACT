module com.example.lib {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.lib to javafx.fxml;
    exports com.example.lib;
}