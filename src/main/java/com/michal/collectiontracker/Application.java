package com.michal.collectiontracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class Application extends javafx.application.Application {
    FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws IOException {
        fxmlLoader = new FXMLLoader(Application.class.getResource("fxml/mainwindow-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/icon.png"))));

        stage.setTitle("Collection Tracker");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        ((MainWindowController) fxmlLoader.getController()).close();

        File generatedImage = new File(System.getProperty("java.io.tmpdir") + "collectiontemp.png");

        if (generatedImage.exists()) {
            Files.delete(generatedImage.toPath());
        }
        super.stop();
    }
}