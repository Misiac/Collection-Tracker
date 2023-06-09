package com.michal.collectiontracker;

import javafx.fxml.FXML;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;

public class AddItemDialogController {
    @FXML
    private TextField newName;
    @FXML
    private TextField newNumber;
    @FXML
    private GridPane addItemGrid;
    @FXML
    private DialogPane rootCreationDialog;
    private File imgFile;

    public void initialize() {
        imgFile = null;

    }

    public boolean isInputOkay() {
        try {
            if (newNumber.getText() != null) Integer.parseInt(newNumber.getText());
            else return false;

        } catch (NumberFormatException e) {
            return false;
        }
        if (newName.getText() != null && imgFile != null) {
            return true;
        }
        return false;

    }

    @FXML
    public void chooseImg() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png")
        );
        this.imgFile = fileChooser.showOpenDialog(rootCreationDialog.getScene().getWindow());

    }

    public TextField getNewName() {
        return newName;
    }

    public TextField getNewNumber() {
        return newNumber;
    }

    public File getImgFile() {
        return imgFile;
    }
}
