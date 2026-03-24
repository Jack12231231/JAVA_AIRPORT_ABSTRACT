module com.example.airport {
    requires javafx.controls;
    requires javafx.fxml;
    requires log4j;


    opens com.example.airport to javafx.fxml;
    exports com.example.airport;
}