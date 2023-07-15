package com.michal.collectiontracker.datamodel;


import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
    private static File tempFile = new File(System.getProperty("java.io.tmpdir") + "collectiontemp.png");

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

    public Collection(File collectionFile) throws SQLException {
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
        try {
            this.backgroundImage = new Image(img.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            System.out.println("path error");
        }
        numberOfItemsOwned = 0;
        totalNumberOfItems = 0;

    }

    public static void saveImageToFile(Image image) {
        try {

            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(bufferedImage, "png", tempFile);

        } catch (IOException e) {
            System.out.println("Failed to save image: " + e.getMessage());
        }
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

    public void addItem(TextField newName, TextField newNumber, File imgFile) {


        Image resizedImage;
        try {
            resizedImage = new Image(imgFile.toURI().toURL().toExternalForm(), 127, 127, true, true);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        saveImageToFile(resizedImage);


        int number = Integer.parseInt(newNumber.getText());
        CollectionItem newCollectionItem;

        newCollectionItem = new CollectionItem(number, newName.getText(), resizedImage, false);
        thisDatasource.addNewItemToDB(newName, newNumber, tempFile);

        collectionItems.put(number, newCollectionItem);
        totalNumberOfItems++;

    }
}

