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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/home-view.fxml"));
            Scene scene = new Scene(loader.load(), 900, 600);

            // Get current stage (window) and switch scene
            Stage stage = (Stage) txtUser.getScene().getWindow();
            stage.setTitle("Advanced Fleet Management - Dashboard");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}