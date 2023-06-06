package com.michal.collectiontracker;

import com.michal.collectiontracker.datamodel.Collection;
import com.michal.collectiontracker.datamodel.CollectionItem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
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
    public Label collectedNumber;
    ToggleGroup buttonsGroup = new ToggleGroup();
    private Map<String, Collection> collectionMap = new HashMap<>();
    private String currentCollectionName;

    public void initialize() {

        StackPane.setAlignment(collectionNameLabel, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(collectedNumber, Pos.BOTTOM_RIGHT);

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

        if (!selectedCollection.getCollectionName().equals(currentCollectionName)) {
            renderCollection(selectedCollection);
        }


    }

    public void renderCollection(Collection collection) {

        flowPane.getChildren().clear();

        collectionNameLabel.setText(collection.getCollectionName());

        collectionImage.setFitHeight(stackPane.getMaxHeight());
        collectionImage.setFitWidth(stackPane.getMaxWidth());
        collectionImage.setImage(collection.getBackgroundImage());

        collectedNumber.setText(collection.getNumberOfItemsOwned() + " out of " +
                collection.getTotalNumberOfItems() + " Collected");

        for (CollectionItem collectionItem : collection.getCollectionItems()) {
            GridPane gridPane = new GridPane();
            gridPane.setId("collectionGrid");

            Label itemName = new Label(collectionItem.getName());
            Label itemID = new Label("Number: " + String.valueOf(collectionItem.getId()));
            CheckBox checkBox = new CheckBox();

            if (collectionItem.isOwned()) {
                checkBox.setSelected(true);
            }

            ImageView imageView = new ImageView(collectionItem.getImage());
            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            checkBox.setPadding(new Insets(5));
            checkBox.setOnAction(this::handleCheckBoxClick);

            gridPane.add(imageView, 0, 0, 2, 1);
            gridPane.add(itemName, 0, 1, 2, 1);
            gridPane.add(itemID, 0, 2, 1, 1);
            gridPane.add(checkBox, 1, 2, 1, 1);


            GridPane.setHalignment(itemName, HPos.CENTER);
            GridPane.setHalignment(checkBox,HPos.RIGHT);


            flowPane.getChildren().add(gridPane);
        }
        currentCollectionName = collection.getCollectionName();
    }
    private void handleCheckBoxClick(ActionEvent e){
        System.out.println(e.getSource().toString());
    }

    @FXML
    public void tests() {

    }
}
