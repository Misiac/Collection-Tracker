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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Collection {

    private final Map<Integer, CollectionItem> collectionItems = new HashMap<>();
    private final DataSource datasource;
    private String collectionName;
    private Image backgroundImage;
    private int totalNumberOfItems;
    private int numberOfItemsOwned;
    private final Path filePath;
    private static final File tempFile = new File(System.getProperty("java.io.tmpdir") + "collectiontemp.png");

    public String getCollectionName() {
        return collectionName;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public DataSource getDatasource() {
        return datasource;
    }


    public void updateOwnedStatus(boolean checkBoxStatus) {
        if (checkBoxStatus) {
            numberOfItemsOwned++;
        } else {
            numberOfItemsOwned--;
        }
    }

    public Collection(File collectionFile) throws SQLException {
        datasource = new DataSource(collectionFile.getAbsolutePath());
        ResultSet items = datasource.queryItems();
        this.filePath = collectionFile.toPath();

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
        ResultSet collectionInfo = datasource.queryCollectionInfo();
        this.collectionName = collectionInfo.getString(1);
        if (collectionInfo.getBinaryStream(2) != null) {
            this.backgroundImage = new Image(collectionInfo.getBinaryStream(2));
        }
    }

    public Collection(TextField name, File directory, File img) {

        datasource = new DataSource(name, directory, img);

        this.collectionName = name.getText();
        String pathString = directory.getAbsolutePath() + File.separator + collectionName + ".sav";
        filePath = Paths.get(pathString);
        try {
            this.backgroundImage = new Image(img.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            System.out.println("path error");
        }
        numberOfItemsOwned = 0;
        totalNumberOfItems = 0;
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

    private static Image resizeAndSaveImageFromFile(File imgFile) {
        Image resizedImage = null;
        try {
            resizedImage = new Image(imgFile.toURI().toURL().toExternalForm(), 127, 127, true, true);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(resizedImage, null);
            ImageIO.write(bufferedImage, "png", tempFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resizedImage;
    }

    public void addItem(TextField newName, TextField newNumber, File imgFile) {

        Image resizedImage = resizeAndSaveImageFromFile(imgFile);

        int number = Integer.parseInt(newNumber.getText());
        CollectionItem newCollectionItem;

        newCollectionItem = new CollectionItem(number, newName.getText(), resizedImage, false);
        datasource.addNewItemToDB(newName, newNumber, tempFile);

        collectionItems.put(number, newCollectionItem);
        totalNumberOfItems++;
    }

    public void updateBgImage(File newImg) {

        datasource.changeDbImage(newImg);
        try {
            this.backgroundImage = new Image(newImg.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCollectionName(String newName) {
        this.collectionName = newName;
        this.datasource.changeDbName(newName);
    }

    public void unload() {
        this.datasource.close();
    }

    public void copyCollection(File targetDir) {
        String filename = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().indexOf("."));
        String newFilename = filename + "_shareCopy.sav";
        Path destination = Paths.get(targetDir.getAbsolutePath(), newFilename);

        try {
            Files.copy(filePath, destination);
            DataSource.resetStatus(destination.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean changeNumber(int oldNumber, int newNumber) {

        boolean alreadyExist = false;
        Integer boxedNewNumber = newNumber;
        for (Integer itemId : collectionItems.keySet()) {
            if (itemId.equals(boxedNewNumber)) {
                alreadyExist = true;
                break;
            }
        }
        if (!alreadyExist) {
            boolean methodResult = datasource.updateNumber(oldNumber, newNumber);
            if (methodResult) {
                var changedItem = collectionItems.get(oldNumber);
                changedItem.setId(newNumber);
                collectionItems.remove(oldNumber);
                collectionItems.put(newNumber, changedItem);
                return true;
            }
        }
        return false;
    }

    public Image changeImage(int id, File img) {

        Image resizedImage = resizeAndSaveImageFromFile(img);
        boolean methodResult = datasource.changeItemImage(id, tempFile);
        if (methodResult) {
            CollectionItem collectionItem = collectionItems.get(id);
            collectionItem.setImage(resizedImage);
            return resizedImage;
        }
        return null;

    }
}