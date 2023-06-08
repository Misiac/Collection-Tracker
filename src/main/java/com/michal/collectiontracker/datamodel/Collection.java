package com.michal.collectiontracker.datamodel;


import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Collection {

    private Map<Integer, CollectionItem> collectionItems = new HashMap<>();
    private DataSource thisDatasource;
    private File file;
    private String collectionName;
    private Image backgroundImage;
    private int totalNumberOfItems;
    private int numberOfItemsOwned;

    public String getCollectionName() {
        return collectionName;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public DataSource getThisDatasource() {
        return thisDatasource;
    }


    public void updateOwnedStatus(boolean checkBoxStatus) {
        if (checkBoxStatus) {
            numberOfItemsOwned++;
        } else {
            numberOfItemsOwned--;
        }
    }

    public Collection(File collectionFile) throws SQLException, IOException {
        this.file = collectionFile;
        thisDatasource = new DataSource(collectionFile.getAbsolutePath());
        ResultSet items = thisDatasource.queryItems();

        numberOfItemsOwned = 0;

        while (items.next()) {
            CollectionItem newItem = new CollectionItem();

            newItem.setId(items.getInt(1));
            newItem.setName(items.getString(2));
            if (items.getInt(4) == 1) {
                newItem.setOwned(true);
                numberOfItemsOwned++;

            } else {
                newItem.setOwned(false);
            }
            totalNumberOfItems++;

            InputStream inputStream = items.getBinaryStream(3);
            newItem.setImage(new Image(inputStream));
            collectionItems.put(newItem.getId(), newItem);

        }
        ResultSet collectionInfo = thisDatasource.queryCollectionInfo();
        this.collectionName = collectionInfo.getString(1);
        if (collectionInfo.getBinaryStream(2) != null) {
            this.backgroundImage = new Image(collectionInfo.getBinaryStream(2));
        }
    }

    public Collection(TextField name, File directory, File img) {
        thisDatasource = new DataSource(name, directory, img);
        this.file = new File(directory.getAbsolutePath() + "/" + name.getText() + ".sav");
        this.collectionName = name.getText();
        this.backgroundImage = new Image(file.getAbsolutePath());
        numberOfItemsOwned = 0;
        totalNumberOfItems = 0;

    }

    public void loadCollectionFromFile(File collection) {

    }

    public File getFile() {
        return file;
    }

    public Map<Integer, CollectionItem> getCollectionItems() {
        return collectionItems;
    }

    public int getTotalNumberOfItems() {
        return totalNumberOfItems;
    }

    public int getNumberOfItemsOwned() {
        return numberOfItemsOwned;
    }
}

