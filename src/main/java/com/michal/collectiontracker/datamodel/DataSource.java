
package com.michal.collectiontracker.datamodel;

import javafx.scene.control.TextField;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;


public class DataSource {

    private final String CONNECTION_STRING_START = "jdbc:sqlite:";
    private final String CONNECTION_STRING;
    private Connection connection;
    private PreparedStatement queryItems;
    private PreparedStatement queryInfo;
    private PreparedStatement updateItemStatus;
    private PreparedStatement insertIntoItemsCreation;
    private PreparedStatement insertNewItem;
    private final String insertIntoItemsCreationStatement = "INSERT INTO Info VALUES(?,?)";
    private final String insertNewItemsStatement = "INSERT INTO Items VALUES (?, ?, ?,0)";


    public DataSource(String absolutePath) {
        this.CONNECTION_STRING = CONNECTION_STRING_START + absolutePath;
        try {

            connection = DriverManager.getConnection(CONNECTION_STRING);

            if (!prepareStatements()) {
                throw new SQLException();
            }


        } catch (Exception e) {
            System.out.println("Data source error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean prepareStatements() {
        try {
            queryItems = connection.prepareStatement("SELECT * FROM Items");
            queryInfo = connection.prepareStatement("SELECT * FROM Info");
            updateItemStatus = connection.prepareStatement("UPDATE Items SET isOwned = ? WHERE ID = ?");
            insertNewItem = connection.prepareStatement(insertNewItemsStatement);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;

    }

    public DataSource(TextField name, File directory, File img) {
        this.CONNECTION_STRING = CONNECTION_STRING_START + directory.getAbsolutePath() + "/" + name.getText() + ".sav";
        try {
            connection = DriverManager.getConnection(CONNECTION_STRING);

        } catch (SQLException e) {
            System.out.println("Data creation error + " + e.getMessage());
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

            insertIntoItemsCreation = connection.prepareStatement(insertIntoItemsCreationStatement);
            if (!prepareStatements()) {
                throw new SQLException();
            }
            insertIntoItemsCreation.setString(1, name.getText());
            insertIntoItemsCreation.setBytes(2, covertFileToByteArray(img));
            insertIntoItemsCreation.execute();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public ResultSet queryItems() {

        try {
            queryItems.execute();
            return queryItems.getResultSet();
        } catch (SQLException e) {
            System.out.println("Error executing statement");
            return null;
        }
    }

    public ResultSet queryCollectionInfo() {
        try {
            queryInfo.execute();
            return queryInfo.getResultSet();

        } catch (SQLException e) {
            System.out.println("Error Executing infoquery");
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
            System.out.println(e.getMessage());
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
            System.err.println(e.getMessage());
        }

        return returnArray;
    }

    public void close() {
        try {
            if (queryItems != null) {
                queryItems.close();
            }
            if (queryInfo != null) {
                queryInfo.close();
            }
            if (updateItemStatus != null) {
                updateItemStatus.close();
            }
            if (insertIntoItemsCreation != null) {
                insertIntoItemsCreation.close();
            }
            if (insertNewItem != null) {
                insertNewItem.close();
            }
            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            System.out.println("Couldn't close the db " + e.getMessage());
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
                System.out.println(e.getMessage());
            }
        }


    }
}