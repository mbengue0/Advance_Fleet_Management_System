package com.fleetapp.controller;
import com.fleetapp.util.AlertHelper;

import com.fleetapp.dao.VehicleDAO;
import com.fleetapp.model.Car;
import com.fleetapp.model.Truck;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class AddVehicleController {

    @FXML private ComboBox<String> typeCombo;
    @FXML private TextField txtPlate, txtBrand, txtModel, txtYear, txtMileage, txtSpecial, txtFuel;
    @FXML private Label lblSpecial, lblFuel;

    private VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML
    public void initialize() {
        // Default to Car
        typeCombo.getSelectionModel().select("Car");
        onTypeChange();
    }

    @FXML
    public void onTypeChange() {
        String selected = typeCombo.getValue();
        if ("Truck".equals(selected)) {
            lblSpecial.setText("Cargo Capacity (tons):");
            lblFuel.setVisible(false);
            txtFuel.setVisible(false);
        } else {
            lblSpecial.setText("Seat Count:");
            lblFuel.setVisible(true);
            txtFuel.setVisible(true);
        }
    }


    @FXML
    public void onSave() {
        try {
            // 1. Basic Empty Check
            if (txtPlate.getText().trim().isEmpty() ||
                    txtBrand.getText().trim().isEmpty() ||
                    txtMileage.getText().trim().isEmpty()) {
                AlertHelper.showError("Validation Error", "Please fill in all fields.");
                return;
            }

            // 2. Data Parsing with Range Checks
            String type = typeCombo.getValue();
            String plate = txtPlate.getText().trim();
            String brand = txtBrand.getText().trim();
            String model = txtModel.getText().trim();
            int year;
            double mileage;

            try {
                year = Integer.parseInt(txtYear.getText().trim());
                mileage = Double.parseDouble(txtMileage.getText().trim());
            } catch (NumberFormatException e) {
                AlertHelper.showError("Invalid Input", "Year and Mileage must be valid numbers.");
                return;
            }

            // --- STRICT VALIDATION START ---

            // Rule A: License Plate Format (Letters, Numbers, Dashes only, 4-10 chars)
            if (!plate.matches("^[A-Z0-9\\-]+$")) {
                AlertHelper.showError("Invalid Plate", "License Plate must be uppercase Alphanumeric (A-Z, 0-9) and dashes only.");
                return;
            }
            if (plate.length() < 4 || plate.length() > 10) {
                AlertHelper.showError("Invalid Plate", "License Plate length must be between 4 and 10 characters.");
                return;
            }

            // Rule B: Realistic Year (1980 to Next Year)
            int currentYear = java.time.Year.now().getValue();
            if (year < 1980 || year > currentYear + 1) {
                AlertHelper.showError("Invalid Year", "Vehicle Year must be between 1980 and " + (currentYear + 1));
                return;
            }

            // Rule C: Positive Mileage
            if (mileage < 0) {
                AlertHelper.showError("Invalid Mileage", "Mileage cannot be negative.");
                return;
            }

            // --- STRICT VALIDATION END ---

            // 3. Logic for Car vs Truck
            if ("Car".equals(type)) {
                int seats;
                try {
                    seats = Integer.parseInt(txtSpecial.getText().trim());
                } catch (NumberFormatException e) {
                    AlertHelper.showError("Invalid Input", "Seat Count must be a number.");
                    return;
                }

                if (seats < 1 || seats > 60) {
                    AlertHelper.showError("Invalid Input", "Seat count must be between 1 and 60.");
                    return;
                }

                String fuel = txtFuel.getText().trim();
                Car c = new Car(0, plate, brand, model, year, mileage, seats, fuel);
                vehicleDAO.addVehicle(c);
            } else {
                double capacity;
                try {
                    capacity = Double.parseDouble(txtSpecial.getText().trim());
                } catch (NumberFormatException e) {
                    AlertHelper.showError("Invalid Input", "Capacity must be a number.");
                    return;
                }

                if (capacity <= 0) {
                    AlertHelper.showError("Invalid Input", "Capacity must be greater than 0.");
                    return;
                }

                Truck t = new Truck(0, plate, brand, model, year, mileage, capacity);
                vehicleDAO.addVehicle(t);
            }

            AlertHelper.showInfo("Success", "Vehicle Added Successfully!");
            closeWindow();

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage().contains("Duplicate")) {
                AlertHelper.showError("Duplicate Error", "A Vehicle with this License Plate already exists.");
            } else {
                AlertHelper.showError("Database Error", e.getMessage());
            }
        }
    }

    @FXML
    public void onCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) txtPlate.getScene().getWindow();
        stage.close();
    }
}