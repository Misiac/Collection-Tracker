
package com.michal.collectiontracker.datamodel;

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

//    private String storeStatement = "INSERT INTO Items VALUES(1,'testName',?)";
//    private PreparedStatement store;


    public DataSource(String absolutePath) {
        this.CONNECTION_STRING = CONNECTION_STRING_START + absolutePath;

        try {

            connection = DriverManager.getConnection(CONNECTION_STRING);

            queryItems = connection.prepareStatement("SELECT * FROM Items");
            queryInfo = connection.prepareStatement("SELECT * FROM Info");

        } catch (Exception e) {
            System.out.println("Data source error: " + e.getMessage());
            e.printStackTrace();
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

    public String queryCollectionInfo() {
        try {
            queryInfo.execute();
            return queryInfo.getResultSet().getString(1);

        } catch (SQLException e) {
            System.out.println("Error Executing infoquery");
            return null;
        }
    }

    private static byte[] covertFileToByteArray(File file) {
        ByteArrayOutputStream bos = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1; ) {
                bos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }

    public void close() {
        try {
            if (queryItems != null) {
                queryItems.close();
            }
            if (queryInfo != null) {
                queryItems.close();
            }

            if (connection != null) {
                connection.close();
            }

        } catch (SQLException e) {
            System.out.println("Couldn't close the db " + e.getMessage());
        }
    }

//    public Image storeFile(File file) throws IOException, SQLException {
//
//        store.setBytes(1, covertFileToByteArray(file));
//        store.execute();
//        return new Image(new FileInputStream(file));
//    }


}
