package com.michal.collectiontracker;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

public class CreationDialogController {
    @FXML
    public GridPane creationGrid;
    @FXML
    public TextField newName;
    @FXML
    public DialogPane rootCreationDialog;
    public File choosenDirectory;
    public File choosenImg;

    public void initialize() {
        choosenDirectory = null;
        choosenImg = null;

    }

    @FXML
    public void chooseDirectory(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        this.choosenDirectory = directoryChooser.showDialog(rootCreationDialog.getScene().getWindow());
    }

    @FXML
    public void chooseImg(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png")
        );
        this.choosenImg = fileChooser.showOpenDialog(rootCreationDialog.getScene().getWindow());
    }

    public boolean isEverythingSet() {
        if (choosenImg != null && choosenDirectory != null) {
            return true;
        }
        return false;
    }

    public TextField getNewName() {
        return newName;
    }

    public File getChoosenDirectory() {
        return choosenDirectory;
    }

    public File getChoosenImg() {
        return choosenImg;
    }
}