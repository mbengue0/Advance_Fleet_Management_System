package com.fleetapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // 1. Database Configuration
    private static final String URL = "jdbc:mysql://localhost:3306/fleet_db"; // Make sure this DB exists!
    private static final String USER = "root";
    private static final String PASSWORD = "Mycourse123#"; // <--- CHANGE THIS to your MySQL password

    // 2. The Method to Get a Connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 3. The "Setup" Method (Run this once to create tables automatically)
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Table 1: Vehicles (Inheritance Strategy: Single Table)
            // We store "CAR" or "TRUCK" in the 'type' column.
            // 'cargo_capacity' is null for Cars. 'seat_count' is null for Trucks.
            String sqlVehicles = "CREATE TABLE IF NOT EXISTS vehicles (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "type VARCHAR(10) NOT NULL, " +
                    "license_plate VARCHAR(20) UNIQUE NOT NULL, " +
                    "brand VARCHAR(50), " +
                    "model VARCHAR(50), " +
                    "year INT, " +
                    "mileage DOUBLE, " +
                    "status VARCHAR(20), " +
                    "cargo_capacity DOUBLE, " + // For Trucks
                    "seat_count INT, " +        // For Cars
                    "fuel_type VARCHAR(20)" +   // For Cars
                    ")";
            stmt.execute(sqlVehicles);

            // Table 2: Drivers
            String sqlDrivers = "CREATE TABLE IF NOT EXISTS drivers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "license_number VARCHAR(50) UNIQUE, " +
                    "license_expiry DATE, " +
                    "phone VARCHAR(20), " +
                    "status VARCHAR(20)" +
                    ")";
            stmt.execute(sqlDrivers);

            // Table 3: Trips
            String sqlTrips = "CREATE TABLE IF NOT EXISTS trips (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "vehicle_id INT, " +
                    "driver_id INT, " +
                    "start_location VARCHAR(100), " +
                    "end_location VARCHAR(100), " +
                    "departure_time DATETIME, " +
                    "arrival_time DATETIME, " +
                    "status VARCHAR(20), " +
                    "FOREIGN KEY (vehicle_id) REFERENCES vehicles(id), " +
                    "FOREIGN KEY (driver_id) REFERENCES drivers(id)" +
                    ")";
            stmt.execute(sqlTrips);

            // Table 4: Maintenance Logs
            String sqlMaintenance = "CREATE TABLE IF NOT EXISTS maintenance (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "vehicle_id INT, " +
                    "description VARCHAR(255), " +
                    "date DATE, " +
                    "cost DOUBLE, " +
                    "provider VARCHAR(100), " +
                    "FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)" +
                    ")";
            stmt.execute(sqlMaintenance);

            // Table 5: App Users (Admins)
            String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(50) NOT NULL" +
                    ")";
            stmt.execute(sqlUsers);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database initialization failed. Check your username/password.");
        }
    }
}