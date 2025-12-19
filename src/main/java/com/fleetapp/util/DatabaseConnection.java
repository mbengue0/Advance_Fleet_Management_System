package com.fleetapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseConnection {

    // 1. Database Configuration
    private static final String URL = "jdbc:mysql://localhost:3306/fleet_db"; // Make sure this DB exists!
    private static final String USER = "root";
    private static final String PASSWORD = "Mycourse123#"; // <--- CHANGE THIS to your MySQL password

    // 2. The Method to Get a Connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Vehicles
            String sqlVehicles = "CREATE TABLE IF NOT EXISTS vehicles (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "type VARCHAR(10) NOT NULL, " +
                    "license_plate VARCHAR(20) UNIQUE NOT NULL, " +
                    "brand VARCHAR(50), " +
                    "model VARCHAR(50), " +
                    "year INT, " +
                    "mileage DOUBLE, " +
                    "status VARCHAR(20), " +
                    "cargo_capacity DOUBLE, " +
                    "seat_count INT, " +
                    "fuel_type VARCHAR(20)" +
                    ")";
            stmt.execute(sqlVehicles);

            // 2. Drivers
            String sqlDrivers = "CREATE TABLE IF NOT EXISTS drivers (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "license_number VARCHAR(50) UNIQUE, " +
                    "license_expiry DATE, " +
                    "phone VARCHAR(20), " +
                    "status VARCHAR(20)" +
                    ")";
            stmt.execute(sqlDrivers);

            // 3. Trips
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

            // 4. Maintenance
            String sqlMaintenance = "CREATE TABLE IF NOT EXISTS maintenance (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "vehicle_id INT, " +
                    "description VARCHAR(255), " +
                    "date DATE, " +
                    "cost DOUBLE, " +
                    "provider VARCHAR(100), " +
                    "category VARCHAR(50), " +  // Added Category
                    "FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)" +
                    ")";
            stmt.execute(sqlMaintenance);

            // 5. Users (Updated to include ROLE)
            String sqlUsers = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(50) NOT NULL, " +
                    "role VARCHAR(20) DEFAULT 'MANAGER'" + // Added Role
                    ")";
            stmt.execute(sqlUsers);

            // --- SEED DATA: Create Default Admin if none exists ---
            checkAndCreateDefaultAdmin(conn);

            System.out.println("Database initialized successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Database initialization failed.");
        }
    }

    // Helper method to create the default user
    private static void checkAndCreateDefaultAdmin(Connection conn) throws SQLException {
        String countSql = "SELECT COUNT(*) FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSql)) {

            if (rs.next() && rs.getInt(1) == 0) {
                // Table is empty, create the Super Admin
                System.out.println("No users found. Creating default Super Admin...");

                String insertSql = "INSERT INTO users (username, password, role) VALUES ('admin', '1234', 'SUPER_ADMIN')";
                stmt.executeUpdate(insertSql);

                System.out.println("Default user created: admin / 1234");
            }
        }
    }
}