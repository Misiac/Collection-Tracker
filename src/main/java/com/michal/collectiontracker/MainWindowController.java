package com.michal.collectiontracker;

import com.michal.collectiontracker.datamodel.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MainWindowController {
    @FXML
    public VBox leftVBox;
    @FXML
    public StackPane stackPane;
    @FXML
    public FlowPane flowPane;
    @FXML
    public BorderPane rootPane;
    List<Collection> collections = new LinkedList<>();
    public static final String BASIC_STYLE =
            "-fx-font-size:15;" +
                    "-fx-font-weight: bold;" +
                    "-fx-border-width: 0 0 2 0;" +
                    "-fx-border-color: black;";
    public static final String BASIC_COLOR = "-fx-background-color:#989898;";
    public static final String CLICKED_COLOR = "-fx-background-color:#8C8C8C;";

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        // fileChooser.setInitialDirectory(new File("data"));  implement later :)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CT save files", "*.sav"));
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        boolean isPresent = false;
        for (Collection collection : collections) {
            if (file.getAbsolutePath().equals(collection.getFile().getAbsolutePath())) {
                isPresent = true;
                break;
            }
        }
        if (!isPresent) {
            try {
                Collection newCollection = new Collection(file);
                collections.add(newCollection);
                Button button = new Button(newCollection.getCollectionName());
                button.setAlignment(Pos.BOTTOM_LEFT);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setPrefHeight(50);
                button.setWrapText(true);
                button.setStyle(BASIC_STYLE + BASIC_COLOR);

                button.setOnAction(actionEvent -> button.setStyle(BASIC_STYLE + CLICKED_COLOR));


                leftVBox.getChildren().add(button);

            } catch (Exception e) {
                System.out.println("File not loaded properly");
                e.printStackTrace();
            }
        } else {
            System.out.println("Collection is already present");
        }
    }

    private void handleCollectionChange(ActionEvent actionEvent) {

    }

    @FXML
    public void tests() {

    }
}
