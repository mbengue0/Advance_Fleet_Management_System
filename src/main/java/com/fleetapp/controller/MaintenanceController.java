package com.fleetapp.controller;

import com.fleetapp.dao.MaintenanceDAO;
import com.fleetapp.dao.VehicleDAO;
import com.fleetapp.model.Maintenance;
import com.fleetapp.model.Vehicle;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class MaintenanceController {

    @FXML private TextField txtDescription, txtProvider, txtCost;
    @FXML private DatePicker dateService;
    @FXML private Label lblVehicleInfo;
    @FXML private ComboBox<String> comboCategory;

    private Vehicle selectedVehicle;
    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    public void initialize() {
        comboCategory.getItems().addAll("Maintenance", "Fuel", "Inspection", "Tires");
        comboCategory.getSelectionModel().selectFirst();
    }

    // This method allows the Home Controller to pass the selected car to this window
    public void setVehicle(Vehicle vehicle) {
        this.selectedVehicle = vehicle;
        lblVehicleInfo.setText("Vehicle: " + vehicle.getBrand() + " " + vehicle.getModel() + " (" + vehicle.getLicensePlate() + ")");
        dateService.setValue(LocalDate.now()); // Default to today
    }

    @FXML
    public void onSave() {
        try {
            // 1. Validate inputs
            if (txtDescription.getText().isEmpty() || txtCost.getText().isEmpty()) {
                showAlert("Please fill in Description and Cost.");
                return;
            }

            // 2. Prepare Data
            String desc = txtDescription.getText();
            String provider = txtProvider.getText();
            double cost = Double.parseDouble(txtCost.getText());
            LocalDate date = dateService.getValue();
            String category = comboCategory.getValue();

            // 3. Create Maintenance Object (ID is 0, auto-generated)
            Maintenance m = new Maintenance(0, selectedVehicle.getId(), desc, date, cost, provider, category);

            // 4. Save Record to DB
            maintenanceDAO.addMaintenance(m);

            // 5. Update Vehicle Status to MAINTENANCE
            vehicleDAO.setUnderMaintenance(selectedVehicle.getId());

            // 6. Close
            closeWindow();

        } catch (NumberFormatException e) {
            showAlert("Cost must be a valid number.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtDescription.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.show();
    }
}