package com.michal.collectiontracker;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
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
    @FXML
    public Label infoLabel;
    public File choosenDirectory;
    public File choosenImg;

    public void initialize() {
        choosenDirectory = null;
        choosenImg = null;
        newName.setContextMenu(new ContextMenu());
    }

    @FXML
    public void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        this.choosenDirectory = directoryChooser.showDialog(rootCreationDialog.getScene().getWindow());
    }

    @FXML
    public void chooseImg() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png")
        );
        this.choosenImg = fileChooser.showOpenDialog(rootCreationDialog.getScene().getWindow());
    }

    public boolean isEverythingSet() {

        if (choosenDirectory == null || choosenImg == null) {
            return false;
        }
        if (choosenDirectory.getAbsolutePath().equals("C:\\")) {
            infoLabel.setVisible(true);
            return false;
        }
        return (getNewName() != null);
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