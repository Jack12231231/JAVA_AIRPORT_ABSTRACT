module com.example.logs {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.logs to javafx.fxml;
    exports com.example.logs;
}