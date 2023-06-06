package com.michal.collectiontracker;

import com.michal.collectiontracker.datamodel.Collection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MainWindowController {
    @FXML
    public ImageView imageView;
    public VBox leftVBox;
    public StackPane stackPane;
    public FlowPane flowPane;
    List<Collection> collections = new LinkedList<>();

    @FXML
    public void chooseFile() {
        FileChooser fileChooser = new FileChooser();
        // fileChooser.setInitialDirectory(new File("data"));  implement later :)
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CT save files", "*.sav"));
        File file = fileChooser.showOpenDialog(imageView.getScene().getWindow());

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

            } catch (Exception e) {
                System.out.println("File not loaded properly");
                e.printStackTrace();
            }
        } else {
            System.out.println("Collection is already present");
        }
//        Image image = collections.get(0).getCollectionItems().get(0).getImage();
//        imageView.setImage(image);

    }

    @FXML
    public void tests() {
        leftVBox.setStyle("-fx-background-color:red");
        stackPane.setStyle("-fx-background-color:blue");
        flowPane.setStyle("-fx-background-color:green");
    }
}
