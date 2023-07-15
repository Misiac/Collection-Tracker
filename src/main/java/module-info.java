module com.michal.collectiontracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires java.desktop;
    requires javafx.swing;


    opens com.michal.collectiontracker to javafx.fxml;

    exports com.michal.collectiontracker;
}