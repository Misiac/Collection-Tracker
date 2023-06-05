package com.michal.collectiontracker;

import com.michal.collectiontracker.datamodel.DataSource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class MainWindowController {
    @FXML
    public ImageView imageView;

    @FXML
    public Button open;

    @FXML
    private void chooseFile() {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());
        try {
            DataSource dataSource = new DataSource();
            imageView.setImage(dataSource.storeFile(file));

        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }


    }
}
