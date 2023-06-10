module com.foofinc.floofyzip {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.foofinc.floofyzip to javafx.fxml;
    exports com.foofinc.floofyzip;
    exports com.foofinc.floofyzip.util;
    opens com.foofinc.floofyzip.util to javafx.fxml;
    exports com.foofinc.floofyzip.controller;
    opens com.foofinc.floofyzip.controller to javafx.fxml;
}