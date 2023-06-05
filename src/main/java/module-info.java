module com.michal.collectiontracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.michal.collectiontracker to javafx.fxml;
    exports com.michal.collectiontracker;
}