package com.michal.collectiontracker;

import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public enum DialogHelper {

    INSTANCE;

    public enum FileChooserType {
        SAV,
        IMG
    }

    public File showCustomFileChooser(Pane rootPane, FileChooserType type) {
        FileChooser fileChooser = new FileChooser();
        if (type == FileChooserType.IMG) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png"));
        } else if (type == FileChooserType.SAV) {
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CT save files", "*.sav"));
        }
        return fileChooser.showOpenDialog(rootPane.getScene().getWindow());
    }

    @SuppressWarnings("SameParameterValue")
    public String showCustomTextInputDialog(String title, String header, String contentText, String setText) {
        TextInputDialog tiDialog = new TextInputDialog();
        tiDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        tiDialog.getDialogPane().getStyleClass().add("alert");

        Stage dialogStage = (Stage) tiDialog.getDialogPane().getScene().getWindow();
        dialogStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/icon.png"))));

        tiDialog.setTitle(title);
        tiDialog.setHeaderText(header);
        tiDialog.setContentText(contentText);
        if (setText != null) {
            tiDialog.getEditor().setText(setText);
        }
        Optional<String> result = tiDialog.showAndWait();
        return result.orElse(null);
    }
}
