package com.fleetapp.controller;

// --- MODELS ---
import com.fleetapp.dao.*;
import com.fleetapp.model.Vehicle;
import com.fleetapp.model.Driver;
import com.fleetapp.model.Trip;
import com.fleetapp.model.Maintenance;
import com.fleetapp.model.User;
import com.fleetapp.util.UserSession;
import com.fleetapp.dao.AnalyticsDAO;
import java.util.Map;


// --- DAOs ---

// --- JAVAFX TOOLS ---
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab; // Import Tab
import javafx.scene.control.TabPane; // Import TabPane
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class HomeController {

    // --- VEHICLE TABLE ---
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, Integer> colId;
    @FXML private TableColumn<Vehicle, String> colType, colPlate, colBrand, colModel, colStatus;
    @FXML private TableColumn<Vehicle, Double> colMileage;

    // --- DRIVER TABLE (NEW) ---
    @FXML private TableView<Driver> driverTable;
    @FXML private TableColumn<Driver, Integer> colDriverId;
    @FXML private TableColumn<Driver, String> colDriverName, colLicense, colExpiry, colPhone, colDriverStatus;

    // --- TRIP TABLE ---
    @FXML private TableView<Trip> tripTable;
    @FXML private TableColumn<Trip, Integer> colTripId, colTripVehicle, colTripDriver;
    @FXML private TableColumn<Trip, String> colStart, colEnd, colDep, colTripStatus;

    @FXML private TableView<Maintenance> maintenanceTable;
    @FXML private TableColumn<Maintenance, Integer> colMaintId, colMaintVehicle;
    @FXML private TableColumn<Maintenance, String> colMaintDesc, colMaintProv;
    @FXML private TableColumn<Maintenance, Double> colMaintCost;
    @FXML private TableColumn<Maintenance, String> colMaintDate;

    // --- ADMIN TAB VARIABLES ---
    @FXML private ListView<String> userList; // Simple list of names
    @FXML private TextField txtNewUser, txtNewPass;

    @FXML private Tab adminTab; // Link to the FXML Tab
    @FXML private TabPane mainTabPane; // Link to the main TabPane container

    @FXML private Label lblTotalVehicles, lblMaintCount, lblFuelCost;
    @FXML private PieChart statusChart;
    @FXML private BarChart<String, Number> costChart;

    private AnalyticsDAO analyticsDAO = new AnalyticsDAO();
    private MaintenanceDAO maintenanceDAO = new MaintenanceDAO(); // Add this DAO
    private TripDAO tripDAO = new TripDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();
    private DriverDAO driverDAO = new DriverDAO(); // New DAO
    private UserDAO userDAO = new UserDAO();


    @FXML
    public void initialize() {
        setupVehicleColumns();
        setupDriverColumns(); // Setup the new table
        setupTripColumns();
        setupMaintenanceColumns();
        checkSecurity();
        loadDashboardData();

        loadData();
    }

    public void loadDashboardData() {
        // 1. KPI Cards (The Big Numbers)
        lblTotalVehicles.setText(String.valueOf(analyticsDAO.getTotalVehicles()));
        lblMaintCount.setText(String.valueOf(analyticsDAO.getMaintenanceCount()));

        // Format Currency nicely ($1,200.50)
        lblFuelCost.setText(String.format("$%.2f", analyticsDAO.getTotalFuelCost()));

        // 2. Pie Chart (Fleet Status)
        statusChart.getData().clear();
        Map<String, Integer> statusData = analyticsDAO.getVehicleStatusData();
        for (Map.Entry<String, Integer> entry : statusData.entrySet()) {
            statusChart.getData().add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }

        // 3. Bar Chart (Cost by Category) <--- THIS IS WHAT YOU NEED
        costChart.getData().clear();

        // Create a data series
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Expenses by Category");

        // Fetch data from Database
        Map<String, Double> costData = analyticsDAO.getCostByCategoryData();

        // Loop through (Fuel, Maintenance, Tires...) and add bars
        for (Map.Entry<String, Double> entry : costData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add the series to the chart
        costChart.getData().add(series);
    }

    private void setupVehicleColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colPlate.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colMileage.setCellValueFactory(new PropertyValueFactory<>("mileage"));
        colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getType()));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus().toString()));
    }

    private void setupDriverColumns() {
        colDriverId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDriverName.setCellValueFactory(new PropertyValueFactory<>("fullName")); // Uses getFullName()
        colLicense.setCellValueFactory(new PropertyValueFactory<>("licenseNumber"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colExpiry.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getLicenseExpiryDate().toString()));
        colDriverStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus().toString()));
    }

    private void setupTripColumns() {
        colTripId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTripVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colTripDriver.setCellValueFactory(new PropertyValueFactory<>("driverId"));
        colStart.setCellValueFactory(new PropertyValueFactory<>("startLocation"));
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endLocation"));
        colDep.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        colTripStatus.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatus().toString()));
    }

    private void setupMaintenanceColumns() {
        colMaintId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMaintVehicle.setCellValueFactory(new PropertyValueFactory<>("vehicleId"));
        colMaintDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colMaintCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
        colMaintProv.setCellValueFactory(new PropertyValueFactory<>("provider"));
        // Date needs a simple string conversion
        colMaintDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));
    }

    public void loadData() {
        vehicleTable.setItems(FXCollections.observableArrayList(vehicleDAO.getAllVehicles()));
        driverTable.setItems(FXCollections.observableArrayList(driverDAO.getAllDrivers())); // Load Drivers
        tripTable.setItems(FXCollections.observableArrayList(tripDAO.getAllTrips()));
        maintenanceTable.setItems(FXCollections.observableArrayList(maintenanceDAO.getAllMaintenance()));
        loadUsers();
    }

    private void loadUsers() {
        userList.getItems().clear();
        for (User u : userDAO.getAllUsers()) {
            userList.getItems().add(u.getUsername());
        }
    }

    @FXML
    public void onRefreshClick() {
        loadData();
        loadDashboardData();
    }

    @FXML
    public void onLogoutClick() {
        try {
            UserSession.clear();
            // 1. Load the Login View
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/login-view.fxml"));
            javafx.scene.Parent root = loader.load();

            // 2. Get the current Stage (Window) from any element (e.g., the table)
            javafx.stage.Stage stage = (javafx.stage.Stage) vehicleTable.getScene().getWindow();

            // 3. Switch the Scene
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Fleet Management - Login");
            stage.centerOnScreen();

            System.out.println("User logged out successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAddVehicleClick() {
        openWindow("/fxml/add-vehicle.fxml", "Add Vehicle");
        loadDashboardData();
    }

    @FXML
    public void onAddDriverClick() {
        // Reusing the helper method we wrote earlier
        openWindow("/fxml/add-driver.fxml", "Register New Driver");
    }

    @FXML
    public void onAddTripClick() {
        openWindow("/fxml/dispatch-trip.fxml", "Dispatch New Trip");
        loadDashboardData();
    }

    @FXML
    public void onCompleteTripClick() {
        Trip selected = tripTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            com.fleetapp.util.AlertHelper.showError("Selection Error", "Please select a trip from the table.");
            return;
        }

        if (selected.getStatus().toString().equals("COMPLETED")) {
            com.fleetapp.util.AlertHelper.showInfo("Action Ignored", "This trip is already finished!");
            return;
        }

        if (com.fleetapp.util.AlertHelper.showConfirmation("Finish Trip", "Mark this trip as completed and release vehicle?")) {
            tripDAO.completeTrip(selected);
            loadData();
            loadDashboardData();
        }
    }

    @FXML
    public void onServiceClick() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            com.fleetapp.util.AlertHelper.showError("Selection Error", "Please select a vehicle first.");
            return;
        }

        // CHECK 1: Is it on the road?
        if (selected.getStatus() == com.fleetapp.model.VehicleStatus.ON_TRIP) {
            com.fleetapp.util.AlertHelper.showError("Operation Failed", "Cannot service a vehicle that is currently ON A TRIP.\nPlease complete the trip first.");
            return;
        }

        // CHECK 2: Is it already in the garage? (THE FIX)
        if (selected.getStatus() == com.fleetapp.model.VehicleStatus.MAINTENANCE) {
            com.fleetapp.util.AlertHelper.showError("Operation Failed", "This vehicle is ALREADY under maintenance.\nYou must finish the current service before starting a new one.");
            return;
        }

        try {
            // 2. Load the FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/maintenance-form.fxml"));
            javafx.scene.Parent root = loader.load();

            // 3. Get the Controller and PASS THE VEHICLE
            MaintenanceController controller = loader.getController();
            controller.setVehicle(selected);

            // 4. Show the Window
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Log Maintenance");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            // 5. Refresh Table (Status should be MAINTENANCE now)
            loadData();
            loadDashboardData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDeleteUserClick() {
        String selectedUser = userList.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            com.fleetapp.util.AlertHelper.showError("Selection Error", "Please select a user to delete.");
            return;
        }

        com.fleetapp.model.User currentUser = UserSession.getCurrentUser();
        if (currentUser.getUsername().equals(selectedUser)) {
            // STOP THE USER from deleting themselves
            com.fleetapp.util.AlertHelper.showError("Security Warning", "You cannot delete your own account while logged in!");
            return;
        }

        // Confirmation Dialog
        if (com.fleetapp.util.AlertHelper.showConfirmation("Delete User", "Are you sure you want to delete " + selectedUser + "?")) {
            userDAO.deleteUser(selectedUser);
            loadUsers();
            com.fleetapp.util.AlertHelper.showInfo("Deleted", "User has been removed.");
        }
    }

    @FXML
    public void onFinishServiceClick() {
        Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        // Logic: Only fix cars that are actually broken
        if (selected.getStatus() == com.fleetapp.model.VehicleStatus.MAINTENANCE) {
            vehicleDAO.finishMaintenance(selected.getId());
            loadData();
            loadDashboardData();
        }
    }

    @FXML
    public void onCreateUserClick() {
        String user = txtNewUser.getText();
        String pass = txtNewPass.getText();

        if (!user.isEmpty() && !pass.isEmpty()) {
            if (userDAO.registerUser(user, pass)) {
                System.out.println("User Created.");
                txtNewUser.clear();
                txtNewPass.clear();
                loadUsers(); // Refresh the list
            } else {
                System.out.println("Error: Username exists.");
            }
        }
    }

    private void checkSecurity() {
        // 1. Get current user
        com.fleetapp.model.User user = UserSession.getCurrentUser();

        if (user != null) {
            System.out.println("Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");

            // 2. If NOT Super Admin, remove the tab
            if (!"SUPER_ADMIN".equals(user.getRole())) {
                // Remove the Admin tab from the view
                mainTabPane.getTabs().remove(adminTab);
            }
        }
    }


    // Helper to open windows easily
    private void openWindow(String fxmlPath, String title) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle(title);
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}