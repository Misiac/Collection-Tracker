package com.michal.collectiontracker;

import com.michal.collectiontracker.datamodel.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
    ToggleGroup buttonsGroup = new ToggleGroup();

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
                ToggleButton button = new ToggleButton(newCollection.getCollectionName());
                button.setAlignment(Pos.BOTTOM_LEFT);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setPrefHeight(50);
                button.setWrapText(true);
                button.setToggleGroup(buttonsGroup);

                button.setOnAction(this::handleCollectionChange);
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
