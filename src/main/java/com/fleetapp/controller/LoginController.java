package com.fleetapp.controller;

import com.fleetapp.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Label lblMessage;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void onLoginClick() {
        String user = txtUser.getText().trim();
        String pass = txtPass.getText().trim();

        // 1. Call DAO (Now returns a User object)
        com.fleetapp.model.User loggedInUser = userDAO.validateLogin(user, pass);

        if (loggedInUser != null) {
            // 2. Save to Session
            com.fleetapp.util.UserSession.setCurrentUser(loggedInUser);

            // 3. Open Dashboard
            openDashboard();
        } else {
            lblMessage.setText("Invalid Username or Password.");
        }
    }

    private void openDashboard() {
        try {
            // Load the Main Dashboard
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
            javafx.scene.Parent root = loader.load();

            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            // --- ADD THIS LINE TO RELOAD CSS ---
            try {
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS not found: " + e.getMessage());
            }
            // -----------------------------------

            // Get current stage (window) and switch scene
            javafx.stage.Stage stage = (javafx.stage.Stage) txtUser.getScene().getWindow();
            stage.setTitle("Advanced Fleet Management - Dashboard");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}