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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @FXML
    public Button menuButton;
    Button addButton;
    ToggleGroup buttonsGroup;
    private final Map<String, Collection> collectionMap = new HashMap<>();
    private final Map<String, ToggleButton> buttonMap = new HashMap<>();
    private final Map<CheckBox, Integer> checkBoxMap = new HashMap<>();
    private String currentCollectionName;
    boolean isCreationModeEnabled;


    public void initialize() {
        buttonsGroup = new ToggleGroup();
        currentCollectionName = null;
        isCreationModeEnabled = false;
        resetStackPane();

        scrollPane.setStyle("-fx-background-color:transparent;");
        scrollPane.fitToHeightProperty().set(true);
        scrollPane.fitToWidthProperty().set(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        addButton = new Button("Add item");
        addButton.setId("addButton");
        FlowPane.setMargin(addButton, new Insets(20, 0, 0, 10));
        addButton.setGraphic(new ImageView(
                new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/plus.png")))));
        addButton.setOnAction(this::showAddItemDialog);


        StackPane.setAlignment(menuButton, Pos.TOP_RIGHT);
        StackPane.setAlignment(collectionNameLabel, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(collectedNumber, Pos.BOTTOM_RIGHT);


        creationButton.setVisible(false);
        addButton.setVisible(false);


        MenuItem item1 = new MenuItem("Share non-selected collection");
        item1.setOnAction(e -> shareCollection());

        MenuItem item2 = new MenuItem("Change name");
        item2.setOnAction(e -> changeName());

        MenuItem item3 = new MenuItem("Change collection bg image");
        item3.setOnAction(e -> handleUpdateBgImage());

        MenuItem item4 = new MenuItem("Unload collection");
        item4.setOnAction(e -> unloadCollection());


        scrollPane.getContent().setOnScroll(scrollEvent -> {
            double deltaY = scrollEvent.getDeltaY() * 0.005;
            scrollPane.setVvalue(scrollPane.getVvalue() - deltaY);
        });

        ContextMenu contextMenu = new ContextMenu(item1, item2, item3, item4);

        menuButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.isPrimaryButtonDown()) {
                contextMenu.show(menuButton, event.getScreenX(), event.getScreenY());
            }
        });
    }

    private void changeName() {

        TextInputDialog tiDialog = new TextInputDialog();
        tiDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        tiDialog.getDialogPane().getStyleClass().add("alert");

        tiDialog.setTitle("Change collection name");
        tiDialog.setHeaderText("Enter new name \n(Filename won't change)");
        tiDialog.setContentText("Name:");
        tiDialog.getEditor().setText(currentCollectionName);

        Optional<String> result = tiDialog.showAndWait();
        if (result.isPresent()) {
            String newName = result.get();
            if (!newName.equals("")) {
                var currentCollection = collectionMap.get(currentCollectionName);
                String oldName = currentCollection.getCollectionName();

                var currentColBtn = buttonMap.get(oldName);
                currentColBtn.setText(newName);
                buttonMap.remove(oldName);
                buttonMap.put(newName, currentColBtn);

                currentCollection.updateCollectionName(newName);


                collectionMap.remove(oldName);
                collectionMap.put(newName, currentCollection);

                collectionNameLabel.setText(newName);

                currentCollectionName = newName;

            }

        }
    }

    private void unloadCollection() {

        var currentCol = collectionMap.get(currentCollectionName);
        currentCol.unload();
        collectionMap.remove(currentCollectionName);

        flowPane.getChildren().clear();
        checkBoxMap.clear();
        leftVBox.getChildren().remove(buttonMap.get(currentCollectionName));
        resetStackPane();

        collectionImage.setImage(null);
        currentCollectionName = null;

    }

    private void resetStackPane() {
        collectionNameLabel.setText("");
        menuButton.setVisible(false);
        collectedNumber.setText("");
    }

    private void shareCollection() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File targetDir = directoryChooser.showDialog(rootPane.getScene().getWindow());
        var currentCol = collectionMap.get(currentCollectionName);
        if (targetDir != null) {
            currentCol.copyCollection(targetDir);
        }

    }

    private void handleUpdateBgImage() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image", "*.jpg", "*.png")
        );
        File newImg = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        if (newImg != null) {
            var currentCol = collectionMap.get(currentCollectionName);
            currentCol.updateBgImage(newImg);
            collectionImage.setFitHeight(stackPane.getMaxHeight());
            collectionImage.setFitWidth(stackPane.getMaxWidth());
            collectionImage.setImage(currentCol.getBackgroundImage());
        }

    }

    @FXML
    public void chooseCollectionFile() {
        FileChooser fileChooser = new FileChooser();
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
        } else {
            ((ToggleButton) actionEvent.getSource()).setSelected(true);
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
            itemName.getStyleClass().add("itemName");

            Label itemID = new Label("Number: " + collectionItem.getId());
            itemID.getStyleClass().add("itemId");

            CheckBox checkBox = new CheckBox();
            checkBox.getStyleClass().add("itemBox");
            checkBoxMap.put(checkBox, collectionItem.getId());

            if (collectionItem.isOwned()) {
                checkBox.setSelected(true);
            }

            ImageView imageView = new ImageView(collectionItem.getImage());

            gridPane.setPrefWidth(128);
            gridPane.setPrefHeight(150);
            gridPane.setMaxHeight(150);

            MenuItem item1 = new MenuItem("Change number");
            item1.setOnAction(event -> handleNumberChange(itemID));
            MenuItem item2 = new MenuItem("Change name");
            MenuItem item3 = new MenuItem("Change image");
            MenuItem item4 = new MenuItem("Remove item");

            ContextMenu contextMenu = new ContextMenu(item1, item2, item3, item4);


            gridPane.setOnContextMenuRequested(event -> contextMenu.show(gridPane, event.getScreenX(), event.getScreenY()));

            imageView.setFitHeight(124);
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

        menuButton.setVisible(true);
        flowPane.getChildren().add(addButton);
        if (isCreationModeEnabled) {
            addButton.setVisible(true);
        }
        currentCollectionName = collection.getCollectionName();
    }

    private void showCustomAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/icon.png"))));

        dialogPane.getStyleClass().add("alert");

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    private void handleNumberChange(Label oldNumberLabel) {

        TextInputDialog tiDialog = new TextInputDialog();
        tiDialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        tiDialog.getDialogPane().getStyleClass().add("alert");

        tiDialog.setTitle("Change item number");
        tiDialog.setHeaderText("Enter new number \n(It has to be unique)");
        tiDialog.setContentText("Number:");

        String oldNumber = oldNumberLabel.getText();
        oldNumber = oldNumber.substring(oldNumber.indexOf(":") + 2);
        tiDialog.getEditor().setText(oldNumber);

        Optional<String> result = tiDialog.showAndWait();
        if (result.isPresent()) {
            if (!result.get().equals("")) {
                try {
                    int newNumber = Integer.parseInt(result.get());
                    var currentCollection = collectionMap.get(currentCollectionName);
                    boolean methodResult = currentCollection.changeNumber(Integer.parseInt(oldNumber), newNumber);
                    if (methodResult) {
                        oldNumberLabel.setText("Number: " + newNumber);
                    } else {
                        showCustomAlert(Alert.AlertType.ERROR,
                                "Error",
                                "Can't change number",
                                "Provided number is not valid"
                        );
                    }
                } catch (NumberFormatException e) {
                    return; // TODO: 19.07.2023
                }
            }
        }
    }

    private void handleCheckBoxClick(ActionEvent e) {
        int selectedItemID = checkBoxMap.get((CheckBox) e.getSource());
        boolean currentCheckBoxStatus = ((CheckBox) e.getSource()).isSelected();

        Collection currentCollection = collectionMap.get(currentCollectionName);
        if (currentCollection.getDatasource().updateItemInfo(selectedItemID, currentCheckBoxStatus)) {
            currentCollection.getCollectionItems().get(selectedItemID).setOwned(currentCheckBoxStatus);
            currentCollection.updateOwnedStatus(currentCheckBoxStatus);
            calculateCollectedNumber(currentCollection);
        }


    }

    private void calculateCollectedNumber(Collection collection) {
        collectedNumber.setText(collection.getNumberOfItemsOwned() + " out of " +
                collection.getTotalNumberOfItems());
    }

    @FXML
    public void close() {

        for (Map.Entry<String, Collection> entry : collectionMap.entrySet()) {
            entry.getValue().getDatasource().close();

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

        aboutDialog.showAndWait();
    }


    @FXML
    public void handleCreationModeSwitch() {
        isCreationModeEnabled ^= true;
        creationButton.setVisible(isCreationModeEnabled);
        if (currentCollectionName != null) {
            addButton.setVisible(isCreationModeEnabled);
        }
        Stage stage = (Stage) rootPane.getScene().getWindow();
        if (isCreationModeEnabled) {
            stage.setTitle("Collection Tracker - Creation mode");
        } else {
            stage.setTitle("Collection Tracker");
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
        Path futureCollectionPath = null;
        do {
            result = creationDialog.showAndWait();

            if (controller.choosenDirectory != null) {
                futureCollectionPath = Paths.get(controller.choosenDirectory.getPath()
                        + File.separator + controller.newName.getText() + ".sav");
                if (Files.exists(futureCollectionPath) && controller.isEverythingSet()) {

                    showCustomAlert(Alert.AlertType.WARNING,
                            "Collection exists",
                            "Collection with the choosen path and name already exists",
                            "Choose another name or path"
                    );
                }

            }
            if (result.isPresent()) {
                if (result.get().getButtonData().isCancelButton()) return;
            }


        } while (!controller.isEverythingSet() || Files.exists(Objects.requireNonNull(futureCollectionPath)));


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

            if (result.isPresent()) {
                if (result.get().getButtonData().isCancelButton()) return;
            }


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
                button.setPrefHeight(55);
                button.setWrapText(true);
                button.setToggleGroup(buttonsGroup);
                button.setOnAction(this::handleCollectionChange);

                buttonMap.put(collection.getCollectionName(), button);
                leftVBox.getChildren().add(button);
            } else {
                showCustomAlert(Alert.AlertType.WARNING,
                        "Already loaded",
                        "Selected collection is already loaded",
                        "Choose another file");
            }
        } catch (Exception ignored) {

        }
    }
}