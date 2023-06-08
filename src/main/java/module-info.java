module com.foofinc.floofyzip {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.foofinc.floofyzip to javafx.fxml;
    exports com.foofinc.floofyzip;
}