package com.fleetapp.dao;

import com.fleetapp.model.Maintenance;
import com.fleetapp.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MaintenanceDAO {

    public void addMaintenance(Maintenance record) {
        String sql = "INSERT INTO maintenance (vehicle_id, description, date, cost, provider, category) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, record.getVehicleId());
            stmt.setString(2, record.getDescription());
            stmt.setDate(3, Date.valueOf(record.getDate())); // LocalDate -> SQL Date
            stmt.setDouble(4, record.getCost());
            stmt.setString(5, record.getProvider());
            stmt.setString(6, record.getCategory());

            stmt.executeUpdate();
            System.out.println("Maintenance Record Logged.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public java.util.List<Maintenance> getAllMaintenance() {
        java.util.List<Maintenance> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM maintenance";

        try (Connection conn = DatabaseConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Maintenance m = new Maintenance(
                        rs.getInt("id"),
                        rs.getInt("vehicle_id"),
                        rs.getString("description"),
                        rs.getDate("date").toLocalDate(),
                        rs.getDouble("cost"),
                        rs.getString("provider"),
                        rs.getString("category")
                );
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}