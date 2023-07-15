package com.michal.collectiontracker;

import com.michal.collectiontracker.datamodel.Collection;
import com.michal.collectiontracker.datamodel.CollectionItem;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class MainWindowController {
    @FXML
    public VBox leftVBox;
    @FXML
    public StackPane stackPane;
    @FXML
    public FlowPane flowPane;
    @FXML
    public BorderPane rootPane;
    @FXML
    public Label collectionNameLabel;
    @FXML
    public ImageView collectionImage;
    @FXML
    public Label collectedNumber;
    @FXML
    public Button creationButton;
    @FXML
    public ScrollPane scrollPane;
    Button addButton;
    ToggleGroup buttonsGroup;
    private Map<String, Collection> collectionMap = new HashMap<>();
    private Map<CheckBox, Integer> checkBoxMap = new HashMap<>();
    private String currentCollectionName;
    boolean isCreationModeEnabled;


    public void initialize() {
        buttonsGroup = new ToggleGroup();
        currentCollectionName = null;
        isCreationModeEnabled = false;

        scrollPane.fitToHeightProperty().set(true);
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        addButton = new Button("Add item");
        addButton.setId("addButton");
        FlowPane.setMargin(addButton, new Insets(20, 0, 0, 10));
        addButton.setGraphic(new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/plus.png")))));
        addButton.setOnAction(this::showAddItemDialog);


        StackPane.setAlignment(collectionNameLabel, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(collectedNumber, Pos.BOTTOM_RIGHT);

        creationButton.setVisible(false);
        addButton.setVisible(false);


    }

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        // fileChooser.setInitialDirectory(new File("data"));  implement later :)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CT save files", "*.sav"));
        File file = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
        if (file == null) return;
        try {

            Collection newCollection = new Collection(file);
            loadCollection(newCollection);

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
        checkBoxMap.clear();

        collectionNameLabel.setText(collection.getCollectionName());

        collectionImage.setFitHeight(stackPane.getMaxHeight());
        collectionImage.setFitWidth(stackPane.getMaxWidth());
        collectionImage.setImage(collection.getBackgroundImage());

        calculateCollectedNumber(collection);

        for (CollectionItem collectionItem : collection.getCollectionItems().values()) {
            GridPane gridPane = new GridPane();
            gridPane.getStyleClass().add("collectionGrid");

            Label itemName = new Label(collectionItem.getName());
            Label itemID = new Label("Number: " + collectionItem.getId());
            CheckBox checkBox = new CheckBox();
            checkBoxMap.put(checkBox, collectionItem.getId());

            if (collectionItem.isOwned()) {
                checkBox.setSelected(true);
            }

            ImageView imageView = new ImageView(collectionItem.getImage());

            gridPane.setPrefWidth(128);

            imageView.setFitHeight(127);
            imageView.setFitWidth(127);


            checkBox.setPadding(new Insets(5));
            checkBox.setOnAction(this::handleCheckBoxClick);

            gridPane.add(imageView, 0, 0, 2, 1);
            gridPane.add(itemName, 0, 1, 2, 1);
            gridPane.add(itemID, 0, 2, 1, 1);
            gridPane.add(checkBox, 1, 2, 1, 1);


            GridPane.setHalignment(itemName, HPos.CENTER);
            GridPane.setHalignment(checkBox, HPos.RIGHT);


            flowPane.getChildren().add(gridPane);
        }


        flowPane.getChildren().add(addButton);
        if (isCreationModeEnabled) {
            addButton.setVisible(true);
        }
        currentCollectionName = collection.getCollectionName();
    }

    private void handleCheckBoxClick(ActionEvent e) {
        int selectedItemID = checkBoxMap.get((CheckBox) e.getSource());
        boolean currentCheckBoxStatus = ((CheckBox) e.getSource()).isSelected();

        Collection currentCollection = collectionMap.get(currentCollectionName);
        if (currentCollection.getThisDatasource().updateItemInfo(selectedItemID, currentCheckBoxStatus)) {
            currentCollection.getCollectionItems().get(selectedItemID).setOwned(currentCheckBoxStatus);
            currentCollection.updateOwnedStatus(currentCheckBoxStatus);
            calculateCollectedNumber(currentCollection);
        }


    }

    private void calculateCollectedNumber(Collection collection) {
        collectedNumber.setText(collection.getNumberOfItemsOwned() + " out of " +
                collection.getTotalNumberOfItems() + " Collected");
    }

    @FXML
    public void close() {

        for (Map.Entry<String, Collection> entry : collectionMap.entrySet()) {
            entry.getValue().getThisDatasource().close();

        }
        Platform.exit();
    }

    @FXML
    public void showAboutDialog() {
        Dialog<ButtonType> aboutDialog = new Dialog<>();
        aboutDialog.initOwner(rootPane.getScene().getWindow());
        aboutDialog.setTitle("About");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("fxml/aboutdialog.fxml"));

        try {
            aboutDialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        aboutDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        Optional<ButtonType> result = aboutDialog.showAndWait();

    }


    @FXML
    public void handleCreationModeSwitch() {
        isCreationModeEnabled ^= true;
        creationButton.setVisible(isCreationModeEnabled);
        if (currentCollectionName != null) {
            addButton.setVisible(isCreationModeEnabled);
        }

    }

    @FXML
    public void showCreationDialog() {
        Dialog<ButtonType> creationDialog = new Dialog<>();
        creationDialog.initOwner(rootPane.getScene().getWindow());
        creationDialog.setTitle("Create new collection");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("fxml/creationdialog.fxml"));

        try {
            creationDialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Error loading creation dialog");
        }
        CreationDialogController controller = fxmlLoader.getController();
        creationDialog.getDialogPane().getButtonTypes().add(new ButtonType("Create", ButtonType.OK.getButtonData()));

        creationDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result;

        do {
            result = creationDialog.showAndWait();

            if (result.get().getButtonData().isCancelButton()) return;
        } while (!controller.isEverythingSet());


        createNewCollection(
                controller.getNewName(),
                controller.getChoosenDirectory(),
                controller.getChoosenImg()
        );


    }

    private void showAddItemDialog(ActionEvent e) {
        Dialog<ButtonType> addItemDialog = new Dialog<>();
        addItemDialog.initOwner(rootPane.getScene().getWindow());
        addItemDialog.setTitle("Add new item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("fxml/additemdialog.fxml"));

        try {
            addItemDialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException ex) {
            System.out.println("Error loading creation dialog");
        }
        AddItemDialogController controller = fxmlLoader.getController();
        addItemDialog.getDialogPane().getButtonTypes().add(new ButtonType("Add", ButtonType.OK.getButtonData()));

        addItemDialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result;

        do {
            result = addItemDialog.showAndWait();

            if (result.get().getButtonData().isCancelButton()) return;
        } while (!controller.isInputOkay());

        Collection currentCollection = collectionMap.get(currentCollectionName);
        currentCollection.addItem(
                controller.getNewName(),
                controller.getNewNumber(),
                controller.getImgFile());
        renderCollection(currentCollection);


    }

    private void createNewCollection(TextField newName, File choosenDirectory, File choosenImg) {
        Collection newCollection = new Collection(newName, choosenDirectory, choosenImg);
        loadCollection(newCollection);

    }

    private void loadCollection(Collection collection) {
        try {
            if (!collectionMap.containsKey(collection.getCollectionName())) {

                collectionMap.put(collection.getCollectionName(), collection);


                ToggleButton button = new ToggleButton(collection.getCollectionName());
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
        } catch (Exception ignored) {

        }
    }
}
