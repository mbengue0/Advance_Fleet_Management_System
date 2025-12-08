package com.fleetapp;

import com.fleetapp.util.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);

        // --- ADD THIS LINE FOR CSS ---
        // This tries to load the CSS, but if it fails, the app still runs (Safe Mode)
        try {
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("Warning: Could not load CSS file. Running without styles.");
        }
        // -----------------------------

        stage.setTitle("Fleet Management - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Ensure Database is ready before GUI starts
        DatabaseConnection.initializeDatabase();

        // Launch the JavaFX Application
        launch();
    }
}