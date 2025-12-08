package com.fleetapp.dao;

import com.fleetapp.util.DatabaseConnection;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AnalyticsDAO {

    public int getTotalVehicles() {
        return getCount("SELECT COUNT(*) FROM vehicles");
    }

    public int getMaintenanceCount() {
        return getCount("SELECT COUNT(*) FROM vehicles WHERE status = 'MAINTENANCE'");
    }

    public double getTotalFuelCost() {
        String sql = "SELECT SUM(cost) FROM maintenance WHERE category = 'Fuel'";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

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

    private int getCount(String sql) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Map<String, Double> getCostByCategoryData() {
        Map<String, Double> data = new HashMap<>();
        String sql = "SELECT category, SUM(cost) FROM maintenance GROUP BY category";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Handle null categories by calling them "Uncategorized"
                String cat = rs.getString(1);
                if (cat == null || cat.isEmpty()) cat = "Uncategorized";

                data.put(cat, rs.getDouble(2));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return data;
    }

}