package com.fleetapp.controller;

import com.fleetapp.dao.DriverDAO;
import com.fleetapp.dao.TripDAO;
import com.fleetapp.dao.VehicleDAO;
import com.fleetapp.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class DispatchTripController {

    @FXML private ComboBox<Vehicle> comboVehicle;
    @FXML private ComboBox<Driver> comboDriver;
    @FXML private TextField txtStart, txtEnd;

    private VehicleDAO vehicleDAO = new VehicleDAO();
    private DriverDAO driverDAO = new DriverDAO();
    private TripDAO tripDAO = new TripDAO();

    @FXML
    public void initialize() {
        loadAvailableResources();
    }

    private void loadAvailableResources() {
        // 1. Vehicles: Must be AVAILABLE
        List<Vehicle> allVehicles = vehicleDAO.getAllVehicles();
        List<Vehicle> availableVehicles = allVehicles.stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .collect(Collectors.toList());

        comboVehicle.setItems(FXCollections.observableArrayList(availableVehicles));

        // 2. Drivers: Must be AVAILABLE ... AND ... Have an Active License
        List<Driver> allDrivers = driverDAO.getAllDrivers();

        List<Driver> validDrivers = allDrivers.stream()
                // Check 1: Are they currently free?
                .filter(d -> d.getStatus() == DriverStatus.AVAILABLE)
                // Check 2: Is their license valid? (The method we wrote in the Model!)
                .filter(d -> d.isLicenseActive())
                .collect(Collectors.toList());

        comboDriver.setItems(FXCollections.observableArrayList(validDrivers));
    }

    @FXML
    public void onDispatch() {
        try {
            // 1. Resource Validation
            if (comboVehicle.getValue() == null || comboDriver.getValue() == null) {
                com.fleetapp.util.AlertHelper.showError("Missing Data", "You must select both a Vehicle and a Driver.");
                return;
            }

            // 2. Location Validation
            String start = txtStart.getText().trim();
            String end = txtEnd.getText().trim();

            if (start.isEmpty() || end.isEmpty()) {
                com.fleetapp.util.AlertHelper.showError("Validation Error", "Please enter Start and End locations.");
                return;
            }

            // Rule: Circular Trip Check
            if (start.equalsIgnoreCase(end)) {
                com.fleetapp.util.AlertHelper.showError("Logic Error", "Start and End locations cannot be identical.");
                return;
            }

            // 3. Create Trip Object
            com.fleetapp.model.Trip trip = new com.fleetapp.model.Trip(
                    0, // ID auto-generated
                    comboVehicle.getValue().getId(),
                    comboDriver.getValue().getId(),
                    LocalDateTime.now(), // Start NOW
                    null,
                    start,
                    end,
                    com.fleetapp.model.TripStatus.IN_PROGRESS
            );

            // 4. Save
            tripDAO.addTrip(trip);

            com.fleetapp.util.AlertHelper.showInfo("Success", "Trip Dispatched Successfully!");
            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            com.fleetapp.util.AlertHelper.showError("Error", e.getMessage());
        }
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtStart.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}