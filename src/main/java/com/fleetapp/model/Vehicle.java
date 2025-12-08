package com.fleetapp.model;

public abstract class Vehicle {

    protected int id;
    protected String licensePlate;
    protected String brand;
    protected String model;
    protected int year;
    protected double mileage;
    protected VehicleStatus status;

    public Vehicle(int id, String licensePlate, String brand, String model, int year, double mileage) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.mileage = mileage;
        this.status = VehicleStatus.AVAILABLE;
    }

    public void updateMileage(double newMileage) {
        if (newMileage >= this.mileage) {
            this.mileage = newMileage;
        } else {
            System.err.println("Error: New mileage cannot be lower than current mileage.");
        }
    }

    public boolean isUnderMaintenance() {
        return this.status == VehicleStatus.MAINTENANCE;
    }

    //Abstract method: Forces child classes to declare their type ("Car" or "Truck").
    public abstract String getType();

    public boolean isAvailable() {
        return this.status == VehicleStatus.AVAILABLE;
    }

    // --- GETTERS AND SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getMileage() {
        return mileage;
    }

    // Note: We usually use updateMileage() for logic, but a raw setter is useful for database loading
    public void setMileage(double mileage) {
        this.mileage = mileage;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return getType() + " - " + brand + " " + model + " (" + licensePlate + ")";
    }
}