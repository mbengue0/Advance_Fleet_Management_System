package com.fleetapp.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Trip {
    private int id;
    private int vehicleId;
    private int driverId;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String startLocation;
    private String endLocation;
    private TripStatus status;

    // Constructor
    public Trip(int id, int vehicleId, int driverId, LocalDateTime departureTime, LocalDateTime arrivalTime, String startLocation, String endLocation, TripStatus status) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.status = status;
    }

    public double getDurationHours() {
        if (departureTime == null || arrivalTime == null) return 0;
        return Duration.between(departureTime, arrivalTime).toHours();
    }

    public void completeTrip() {
        this.arrivalTime = LocalDateTime.now();
        this.status = TripStatus.COMPLETED;
    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }
    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }
    public TripStatus getStatus() { return status; }
    public void setStatus(TripStatus status) { this.status = status; }
}