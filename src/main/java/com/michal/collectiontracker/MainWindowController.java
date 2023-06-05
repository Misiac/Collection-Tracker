package com.michal.collectiontracker;

import com.michal.collectiontracker.datamodel.Collection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MainWindowController {
    @FXML
    public ImageView imageView;

    @FXML
    public Button open;
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
            }
        }else {
            System.out.println("Collection is already present");
        }

    }
}
