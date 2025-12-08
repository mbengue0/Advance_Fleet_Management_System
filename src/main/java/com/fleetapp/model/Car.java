package com.fleetapp.model;

public class Car extends Vehicle {
    private int seatCount;
    private String fuelType;

    public Car (int id, String licensePlate, String brand, String model, int year, double mileage,int seatCount, String fuelType) {
        super(id, licensePlate, brand, model, year, mileage);
        this.fuelType = fuelType;
        this.seatCount = seatCount;
    }

    @Override
    public String getType() { return "Car"; }

    public int getSeatCount() { return seatCount; }
    public void setSeatCount(int seatCount) { this.seatCount = seatCount; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

}