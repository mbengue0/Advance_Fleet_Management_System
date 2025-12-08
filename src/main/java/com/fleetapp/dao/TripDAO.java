package com.fleetapp.dao;

import com.fleetapp.model.Trip;
import com.fleetapp.model.TripStatus;
import com.fleetapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripDAO {

    public void addTrip(Trip trip) {
        String sql = "INSERT INTO trips (vehicle_id, driver_id, start_location, end_location, departure_time, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trip.getVehicleId());
            stmt.setInt(2, trip.getDriverId());
            stmt.setString(3, trip.getStartLocation());
            stmt.setString(4, trip.getEndLocation());
            stmt.setTimestamp(5, Timestamp.valueOf(trip.getDepartureTime())); // LocalDateTime -> SQL Timestamp
            stmt.setString(6, trip.getStatus().name());

            stmt.executeUpdate();
            System.out.println("Trip Dispatch Recorded.");

            // --- AUTO-UPDATE STATUS ---
            updateEntityStatus(conn, "vehicles", trip.getVehicleId(), "ON_TRIP");
            updateEntityStatus(conn, "drivers", trip.getDriverId(), "ON_TRIP");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper to update status of Car/Driver automatically
    private void updateEntityStatus(Connection conn, String table, int id, String status) throws SQLException {
        String sql = "UPDATE " + table + " SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void completeTrip(Trip trip) {
        String sql = "UPDATE trips SET status = 'COMPLETED', arrival_time = NOW() WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 1. Mark Trip as Completed
            stmt.setInt(1, trip.getId());
            stmt.executeUpdate();

            // 2. Free up the Vehicle and Driver
            updateEntityStatus(conn, "vehicles", trip.getVehicleId(), "AVAILABLE");
            updateEntityStatus(conn, "drivers", trip.getDriverId(), "AVAILABLE");

            System.out.println("Trip " + trip.getId() + " completed. Resources released.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Trip> getAllTrips() {
        List<Trip> list = new ArrayList<>();
        String sql = "SELECT * FROM trips";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Note: handling null arrival time
                Timestamp arrival = rs.getTimestamp("arrival_time");

                Trip t = new Trip(
                        rs.getInt("id"),
                        rs.getInt("vehicle_id"),
                        rs.getInt("driver_id"),
                        rs.getTimestamp("departure_time").toLocalDateTime(),
                        (arrival != null) ? arrival.toLocalDateTime() : null, // Handle nulls
                        rs.getString("start_location"),
                        rs.getString("end_location"),
                        TripStatus.valueOf(rs.getString("status"))
                );
                list.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}