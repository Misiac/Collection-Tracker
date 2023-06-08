package com.michal.collectiontracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    FXMLLoader fxmlLoader;
    @Override
    public void start(Stage stage) throws IOException {
         fxmlLoader = new FXMLLoader(Application.class.getResource("fxml/mainwindow-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.getIcons().add(new Image(getClass().getResourceAsStream("img/icon.png")));

        stage.setTitle("Collection Tracker");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        //^ maybe someday?


    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        ((MainWindowController)fxmlLoader.getController()).close();
        super.stop();
    }
}