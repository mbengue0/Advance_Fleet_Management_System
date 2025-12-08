package com.fleetapp.dao;

import com.fleetapp.model.Car;
import com.fleetapp.model.Truck;
import com.fleetapp.model.Vehicle;
import com.fleetapp.model.VehicleStatus;
import com.fleetapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // --- CREATE (INSERT) ---
    public void addVehicle(Vehicle vehicle) {
        // The SQL is generic, but we fill specific nulls based on type
        String sql = "INSERT INTO vehicles (type, license_plate, brand, model, year, mileage, status, cargo_capacity, seat_count, fuel_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Shared Data
            stmt.setString(2, vehicle.getLicensePlate());
            stmt.setString(3, vehicle.getBrand());
            stmt.setString(4, vehicle.getModel());
            stmt.setInt(5, vehicle.getYear());
            stmt.setDouble(6, vehicle.getMileage());
            stmt.setString(7, vehicle.getStatus().name()); // Convert Enum to String

            // Specific Data (Polymorphism Logic)
            if (vehicle instanceof Truck) {
                Truck t = (Truck) vehicle;
                stmt.setString(1, "TRUCK");
                stmt.setDouble(8, t.getCargoCapacity()); // cargo_capacity
                stmt.setNull(9, Types.INTEGER);          // seat_count is NULL
                stmt.setNull(10, Types.VARCHAR);         // fuel_type is NULL
            }
            else if (vehicle instanceof Car) {
                Car c = (Car) vehicle;
                stmt.setString(1, "CAR");
                stmt.setNull(8, Types.DOUBLE);           // cargo_capacity is NULL
                stmt.setInt(9, c.getSeatCount());        // seat_count
                stmt.setString(10, c.getFuelType());     // fuel_type
            }

            stmt.executeUpdate();
            System.out.println("Vehicle saved successfully: " + vehicle.getLicensePlate());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update status to MAINTENANCE
    public void setUnderMaintenance(int vehicleId) {
        String sql = "UPDATE vehicles SET status = 'MAINTENANCE' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update status back to AVAILABLE
    public void finishMaintenance(int vehicleId) {
        String sql = "UPDATE vehicles SET status = 'AVAILABLE' WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vehicleId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- READ (SELECT ALL) ---
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> list = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // 1. Get Common Data
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String plate = rs.getString("license_plate");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                int year = rs.getInt("year");
                double mileage = rs.getDouble("mileage");
                // Convert DB String back to Enum
                VehicleStatus status = VehicleStatus.valueOf(rs.getString("status"));

                Vehicle v = null;

                // 2. Decide which Object to create
                if ("TRUCK".equals(type)) {
                    double capacity = rs.getDouble("cargo_capacity");
                    v = new Truck(id, plate, brand, model, year, mileage, capacity);
                }
                else if ("CAR".equals(type)) {
                    int seats = rs.getInt("seat_count");
                    String fuel = rs.getString("fuel_type");
                    v = new Car(id, plate, brand, model, year, mileage, seats, fuel);
                }

                // 3. Set the shared status (since it's not in the constructor usually)
                if (v != null) {
                    v.setStatus(status);
                    list.add(v);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}