package com.michal.collectiontracker;

import com.michal.collectiontracker.datamodel.Collection;
import com.michal.collectiontracker.datamodel.CollectionItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainWindowController {
    @FXML
    public VBox leftVBox;
    @FXML
    public StackPane stackPane;
    @FXML
    public FlowPane flowPane;
    @FXML
    public BorderPane rootPane;
    public Label collectionNameLabel;
    public ImageView collectionImage;
    ToggleGroup buttonsGroup = new ToggleGroup();
    private Map<String, Collection> collectionMap = new HashMap<>();
    private String currentCollectionName;

    public void initialize() {
        StackPane.setAlignment(collectionNameLabel, Pos.BOTTOM_LEFT);

        System.out.println(stackPane.getWidth());
        currentCollectionName = null;
    }

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        // fileChooser.setInitialDirectory(new File("data"));  implement later :)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CT save files", "*.sav"));
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());


        try {
            Collection newCollection = new Collection(file);
            if (!collectionMap.containsKey(newCollection.getCollectionName())) {

                collectionMap.put(newCollection.getCollectionName(), newCollection);
                currentCollectionName = newCollection.getCollectionName();


                ToggleButton button = new ToggleButton(newCollection.getCollectionName());
                button.setAlignment(Pos.BOTTOM_LEFT);
                button.setMaxWidth(Double.MAX_VALUE);
                button.setPrefHeight(50);
                button.setWrapText(true);
                button.setToggleGroup(buttonsGroup);

                button.setOnAction(this::handleCollectionChange);
                leftVBox.getChildren().add(button);
            } else {
                Alert alreadyPresentAlert = new Alert(Alert.AlertType.WARNING);
                alreadyPresentAlert.setTitle("Already loaded");
                alreadyPresentAlert.setHeaderText("Selected collection is already loaded");
                alreadyPresentAlert.setContentText("Choose another file");
                alreadyPresentAlert.showAndWait();
            }


        } catch (Exception e) {
            System.out.println("File not loaded properly");
            e.printStackTrace();
        }

    }

    private void handleCollectionChange(ActionEvent actionEvent) {
        String selection = ((ToggleButton) actionEvent.getSource()).getText();
        Collection selectedCollection = collectionMap.get(selection);
        collectionNameLabel.setText(selectedCollection.getCollectionName());
        collectionImage.setFitHeight(stackPane.getMaxHeight());
        collectionImage.setFitWidth(stackPane.getMaxWidth());
        collectionImage.setImage(selectedCollection.getBackgroundImage());
        System.out.println("TOTAL " + selectedCollection.getTotalNumberOfItems());
        System.out.println("OWNED " + selectedCollection.getNumberOfItemsOwned());

        renderCollection(selectedCollection);


    }

    public void renderCollection(Collection collection) {
        flowPane.getChildren().clear();
        for (CollectionItem collectionItem : collection.getCollectionItems()) {
            GridPane gridPane = new GridPane();

            Label itemName = new Label(collectionItem.getName());
            Label itemID = new Label(String.valueOf(collectionItem.getId()));

            ImageView imageView = new ImageView(collectionItem.getImage());
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            gridPane.setGridLinesVisible(true);

            gridPane.add(imageView, 0, 0, 1, 2);
            gridPane.add(itemName, 0, 1, 1, 1);
            gridPane.add(itemID, 2, 0, 1, 1);

            flowPane.getChildren().add(gridPane);
        }
    }

    @FXML
    public void tests() {

    }
}
