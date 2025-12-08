package com.fleetapp.dao;

import com.fleetapp.util.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsDAO {

    // 1. KPI: Total Vehicles
    public int getTotalVehicles() {
        return getSingleInt("SELECT COUNT(*) FROM vehicles");
    }

    // 2. KPI: Active Maintenance
    public int getMaintenanceCount() {
        return getSingleInt("SELECT COUNT(*) FROM vehicles WHERE status = 'MAINTENANCE'");
    }

    // 3. KPI: Total Fuel Cost (Specific Category)
    public double getTotalFuelCost() {
        String sql = "SELECT SUM(cost) FROM maintenance WHERE category = 'Fuel'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    // 4. CHART DATA: Status Breakdown (Pie Chart)
    public Map<String, Integer> getVehicleStatusData() {
        Map<String, Integer> data = new HashMap<>();
        String sql = "SELECT status, COUNT(*) FROM vehicles GROUP BY status";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                data.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    // 5. CHART DATA: Cost by Category (Bar Chart)
    public Map<String, Double> getCostByCategoryData() {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT category, SUM(cost) FROM maintenance GROUP BY category";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String cat = rs.getString(1);
                if (cat == null) cat = "Uncategorized";
                data.put(cat, rs.getDouble(2));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

    // Helper
    private int getSingleInt(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}