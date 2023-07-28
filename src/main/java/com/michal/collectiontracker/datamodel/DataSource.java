
package com.michal.collectiontracker.datamodel;

import javafx.scene.control.TextField;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataSource {

    private static final String CONNECTION_STRING_START = "jdbc:sqlite:";
    private final String CONNECTION_STRING;
    private Connection connection;
    private PreparedStatement queryItems;
    private PreparedStatement updateBgImage;
    private PreparedStatement queryInfo;
    private PreparedStatement updateItemStatus;
    private PreparedStatement changeNumber;
    private PreparedStatement insertNewItem;
    private PreparedStatement updateDbName;
    private PreparedStatement changeItemImage;
    private PreparedStatement changeItemName;
    private PreparedStatement removeItem;
    private final Set<PreparedStatement> statementsSet = new HashSet<>();

    public DataSource(String absolutePath) {
        this.CONNECTION_STRING = CONNECTION_STRING_START + absolutePath;
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);

            if (!prepareStatements()) {
                throw new SQLException();
            }
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean prepareStatements() {
        try {
            queryItems = connection.prepareStatement("SELECT * FROM Items");
            queryInfo = connection.prepareStatement("SELECT * FROM Info");
            updateItemStatus = connection.prepareStatement("UPDATE Items SET isOwned = ? WHERE ID = ?");
            insertNewItem = connection.prepareStatement("INSERT INTO Items VALUES (?, ?, ?,0)");
            updateBgImage = connection.prepareStatement("UPDATE Info SET COLLECTIONBG = ?");
            updateDbName = connection.prepareStatement("UPDATE Info SET COLLECTIONNAME = ?");
            changeNumber = connection.prepareStatement("UPDATE Items SET ID = ? WHERE ID = ?");
            changeItemImage = connection.prepareStatement("UPDATE Items SET PHOTO = ? WHERE ID = ?");
            changeItemName = connection.prepareStatement("UPDATE Items SET NAME = ? WHERE ID = ?");
            removeItem = connection.prepareStatement("DELETE FROM Items WHERE ID = ?");

            statementsSet.addAll(List.of(queryItems, updateBgImage, queryInfo, updateItemStatus,
                    changeNumber, insertNewItem, updateDbName,
                    changeItemImage, changeItemName, removeItem));
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
            return false;
        }
        return true;
    }

    public DataSource(TextField name, File directory, File img) {
        this.CONNECTION_STRING = CONNECTION_STRING_START + directory.getAbsolutePath() + "/" + name.getText() + ".sav";
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);

        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
        }
        try {
            Statement statement = connection.createStatement();
            statement.execute("""
                    CREATE TABLE "Items" (
                    \t"ID"\tInt NOT NULL,
                    \t"NAME"\tTEXT NOT NULL,
                    \t"PHOTO"\tBLOB,
                    \t"isOwned"\tINTEGER NOT NULL,
                    \tPRIMARY KEY("ID")
                    )""");
            statement.execute("""
                    CREATE TABLE "Info" (
                    \t"COLLECTIONNAME"\tTEXT NOT NULL
                    , "COLLECTIONBG"\tBLOB)""");

            String insertIntoItemsCreationStatement = "INSERT INTO Info VALUES(?,?)";

            PreparedStatement insertIntoItemsCreation = connection.prepareStatement(insertIntoItemsCreationStatement);
            statementsSet.add(insertIntoItemsCreation);
            if (!prepareStatements()) {
                throw new SQLException();
            }
            insertIntoItemsCreation.setString(1, name.getText());
            insertIntoItemsCreation.setBytes(2, covertFileToByteArray(img));
            insertIntoItemsCreation.execute();

        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
        }
    }

    public ResultSet queryItems() {

        try {
            queryItems.execute();
            return queryItems.getResultSet();
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
            return null;
        }
    }

    public ResultSet queryCollectionInfo() {
        try {
            queryInfo.execute();
            return queryInfo.getResultSet();

        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
            return null;
        }
    }

    public boolean updateItemInfo(int selectedItemID, boolean currentCheckBoxStatus) {

        try {
            if (currentCheckBoxStatus) {
                updateItemStatus.setInt(1, 1);
            } else {
                updateItemStatus.setInt(1, 0);
            }
            updateItemStatus.setInt(2, selectedItemID);
            updateItemStatus.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
            return false;
        }
    }

    private static byte[] covertFileToByteArray(File file) {
        ByteArrayOutputStream bos;
        byte[] returnArray = null;
        try (FileInputStream fis = new FileInputStream(file)) {

            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1; ) {
                bos.write(buffer, 0, len);
                returnArray = bos.toByteArray();
                bos.close();
            }
        } catch (IOException e) {
            System.out.println("IOException => " + e.getMessage());
        }
        return returnArray;
    }

    public void close() {
        try {
            for (PreparedStatement statement : statementsSet) {
                if (statement != null) {
                    statement.close();
                }
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
        }
    }

    public void addNewItemToDB(TextField newName, TextField newNumber, File imgFile) {

        if (insertNewItem != null) {
            try {

                insertNewItem.setInt(1, Integer.parseInt(newNumber.getText()));
                insertNewItem.setString(2, newName.getText());
                insertNewItem.setBytes(3, covertFileToByteArray(imgFile));

                insertNewItem.execute();
            } catch (SQLException e) {
                System.out.println("SQLException => " + e.getMessage());
            }
        }
    }

    public void changeDbImage(File newImg) {

        try {
            updateBgImage.setBytes(1, covertFileToByteArray(newImg));
            updateBgImage.execute();
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
        }
    }

    public void changeDbName(String newName) {
        try {
            updateDbName.setString(1, newName);
            updateDbName.execute();
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
        }
    }

    public static void resetStatus(String absolutePath) {

        String query = "UPDATE Items SET isOwned = 0";
        String connectionSting = CONNECTION_STRING_START + absolutePath;
        try (Statement resetStatement = DriverManager.getConnection(connectionSting).createStatement()) {
            resetStatement.execute(query);

        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
        }
    }

    public boolean updateNumber(int oldNumber, int newNumber) {

        try {
            changeNumber.setInt(1, newNumber);
            changeNumber.setInt(2, oldNumber);
            changeNumber.execute();
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
            return false;
        }
        return true;
    }

    public boolean changeItemImage(int id, File tempFile) {

        try {
            changeItemImage.setBytes(1, covertFileToByteArray(tempFile));
            changeItemImage.setInt(2, id);
            changeItemImage.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
            return false;
        }
    }

    public boolean changeItemName(int id, String newName) {

        try {
            changeItemName.setString(1, newName);
            changeItemName.setInt(2, id);

            changeItemName.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
            return false;
        }
    }

    public boolean removeItemFromDb(int itemId) {

        try {
            removeItem.setInt(1, itemId);
            removeItem.execute();

            return true;
        } catch (SQLException e) {
            System.out.println("SQLException => " + e.getMessage());
            return false;
        }
    }
}
