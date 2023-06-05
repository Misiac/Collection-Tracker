
package com.michal.collectiontracker.datamodel;

import javafx.scene.image.Image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;


public class DataSource {

    public final String dbLocation = "testcollection.sav";
    public final String CONNECTION_STRING = "jdbc:sqlite:" + dbLocation;

    private String storeStatement = "INSERT INTO Items VALUES(1,'testName',?)";
    private PreparedStatement store;
    private DataSource dataSource;

    public void initialize() {
        try {

            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(CONNECTION_STRING);

            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS Items (ID Int PRIMARY KEY NOT NULL," +
                    "NAME TEXT NOT NULL," +
                    "PHOTO BLOB NOT NULL)");

            store = connection.prepareStatement(storeStatement);
        } catch (Exception e) {
            System.out.println("Error" + e.getMessage());
        }
    }

    public Image storeFile(File file) throws IOException, SQLException {

        store.setBytes(1, covertFileToByteArray(file));
        store.execute();
        return new Image(new FileInputStream(file));
    }

    public DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = new DataSource();
        }
        return dataSource;

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
