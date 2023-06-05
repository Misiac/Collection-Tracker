
package com.michal.collectiontracker.datamodel;

import javafx.scene.image.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;


public class DataSource {

    private final String CONNECTION_STRING_START = "jdbc:sqlite:";
    private final String CONNECTION_STRING;

    private String storeStatement = "INSERT INTO Items VALUES(1,'testName',?)";
    private PreparedStatement store;

    public DataSource(String absolutePath) {
        this.CONNECTION_STRING = CONNECTION_STRING_START + absolutePath;

        try {
            System.out.println(CONNECTION_STRING);

            Connection connection = DriverManager.getConnection(CONNECTION_STRING);

            Statement statement = connection.createStatement();
//            statement.execute("CREATE TABLE IF NOT EXISTS Items (ID Int PRIMARY KEY NOT NULL," +
//                    "NAME TEXT NOT NULL," +
//                    "PHOTO BLOB NOT NULL)");
            // ^ this is for testing purposes

            store = connection.prepareStatement(storeStatement);

            statement.execute("SELECT * FROM Items");
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
                System.out.println(resultSet.getString(2));
            }
        } catch (Exception e) {
            System.out.println("Data source error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public Image storeFile(File file) throws IOException, SQLException {

        store.setBytes(1, covertFileToByteArray(file));
        store.execute();
        return new Image(new FileInputStream(file));
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
}
