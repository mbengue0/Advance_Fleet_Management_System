package com.fleetapp.model;

import java.time.LocalDate;

public class Driver {
    private int id;
    private String name;
    private String licenseNumber;
    private LocalDate licenseExpiryDate;
    private String phoneNumber;
    private DriverStatus status;

    public Driver(int id, String name, String licenseNumber, LocalDate licenseExpiryDate, String phoneNumber, DriverStatus status) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.licenseExpiryDate = licenseExpiryDate;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    public boolean isLicenseActive() {
        return licenseExpiryDate.isAfter(LocalDate.now());
    }

    public String getFullName() { return this.name; }

    public LocalDate getLicenseExpiryDate() { return licenseExpiryDate; }
    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) { this.licenseExpiryDate = licenseExpiryDate; }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public DriverStatus getStatus() { return status; }
    public void setStatus(DriverStatus status) { this.status = status; }

    @Override
    public String toString() {
        return name + " (Lic: " + licenseNumber + ")";
    }
}