package com.example.appenglish_nouns;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("maket/main-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("img/icon.png")));
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Nouns!");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}