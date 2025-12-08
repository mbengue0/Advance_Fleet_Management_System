package com.fleetapp.controller;

import com.fleetapp.dao.DriverDAO;
import com.fleetapp.model.Driver;
import com.fleetapp.model.DriverStatus;
import com.fleetapp.util.AlertHelper;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class AddDriverController {

    @FXML private TextField txtName, txtLicense, txtPhone;
    @FXML private DatePicker dateExpiry;

    private DriverDAO driverDAO = new DriverDAO();

    @FXML
    public void onSave() {
        try {
            // Get raw input
            String name = txtName.getText().trim();
            String license = txtLicense.getText().trim();
            String phone = txtPhone.getText().trim();
            LocalDate expiry = dateExpiry.getValue();

            // --- 1. CHECK FOR EMPTY FIELDS ---
            if (name.isEmpty() || license.isEmpty() || phone.isEmpty()) {
                AlertHelper.showError("Validation Error", "Please fill in all text fields.");
                return;
            }
            if (expiry == null) {
                AlertHelper.showError("Validation Error", "License Expiry Date is mandatory.");
                return;
            }

            // --- 2. VALIDATE NAME ---
            if (!name.matches("^[a-zA-Z\\s\\-]+$")) {
                AlertHelper.showError("Invalid Name", "Name can only contain letters, spaces, and hyphens.");
                return;
            }
            if (name.length() < 3) {
                AlertHelper.showError("Invalid Name", "Name is too short.");
                return;
            }

            // --- 3. VALIDATE PHONE (SENEGAL STANDARD) ---
            if (!phone.matches("^[0-9\\+\\-\\s]+$")) {
                AlertHelper.showError("Invalid Phone", "Phone number contains invalid characters.");
                return;
            }

            // Count actual digits
            String pureNumbers = phone.replaceAll("[^0-9]", "");

            // Rule:
            // 9 digits = Local format (e.g., 77 111 22 33)
            // 12 digits = International format with 221 (e.g., 221 77 111 22 33)
            if (pureNumbers.length() != 9 && pureNumbers.length() != 12) {
                AlertHelper.showError("Invalid Phone", "Invalid Senegal Number.\nMust be 9 digits (e.g., 77 xxx xx xx) or 12 digits with 221.");
                return;
            }

            // --- 4. VALIDATE LICENSE (STRICT LENGTH) ---
            // Must be Alphanumeric
            if (!license.matches("^[a-zA-Z0-9\\-]+$")) {
                AlertHelper.showError("Invalid License", "License must be Alphanumeric (A-Z, 0-9).");
                return;
            }

            // Strict Length: Must be between 8 and 12 characters
            if (license.length() < 8 || license.length() > 12) {
                AlertHelper.showError("Invalid License", "License Number must be between 8 and 12 characters.");
                return;
            }

            // --- 5. SAVE ---
            Driver d = new Driver(0, name, license, expiry, phone, DriverStatus.AVAILABLE);
            driverDAO.addDriver(d);

            AlertHelper.showInfo("Success", "Driver registered successfully!");
            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate")) {
                AlertHelper.showError("Duplicate Error", "This License Number is already registered!");
            } else {
                AlertHelper.showError("Database Error", "Could not save driver.\nDetails: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }
}