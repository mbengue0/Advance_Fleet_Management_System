package com.fleetapp.dao;

import com.fleetapp.model.Driver;
import com.fleetapp.model.DriverStatus;
import com.fleetapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DriverDAO {

    public void addDriver(Driver driver) {
        String sql = "INSERT INTO drivers (name, license_number, license_expiry, phone, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, driver.getFullName());
            stmt.setString(2, driver.getLicenseNumber());
            // Convert Java LocalDate to SQL Date
            stmt.setDate(3, Date.valueOf(driver.getLicenseExpiryDate()));
            stmt.setString(4, driver.getPhoneNumber());
            stmt.setString(5, driver.getStatus().name());

            stmt.executeUpdate();
            System.out.println("Driver saved: " + driver.getFullName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Driver> getAllDrivers() {
        List<Driver> list = new ArrayList<>();
        String sql = "SELECT * FROM drivers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Driver d = new Driver(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("license_number"),
                        rs.getDate("license_expiry").toLocalDate(), // SQL Date -> LocalDate
                        rs.getString("phone"),
                        DriverStatus.valueOf(rs.getString("status"))
                );
                list.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}