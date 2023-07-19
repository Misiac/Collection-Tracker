package com.michal.collectiontracker;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class AddItemDialogController {

    @FXML
    private TextField newName;
    @FXML
    private TextField newNumber;
    @FXML
    private DialogPane rootCreationDialog;
    private File imgFile;
    private static File previousPath;

    public void initialize() {
        imgFile = null;
        newName.setContextMenu(new ContextMenu());
        newNumber.setContextMenu(new ContextMenu());
    }

    public boolean isInputOkay() {
        try {
            if (newNumber.getText() != null) Integer.parseInt(newNumber.getText());
            else return false;

        } catch (NumberFormatException e) {
            return false;
        }
        return newName.getText() != null && imgFile != null;
    }

    @FXML
    public void chooseImg() {
        FileChooser fileChooser = new FileChooser();
        if (previousPath != null) {
            fileChooser.setInitialDirectory(previousPath);
        }
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png")
        );
        this.imgFile = fileChooser.showOpenDialog(rootCreationDialog.getScene().getWindow());

        previousPath = imgFile.getParentFile();
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