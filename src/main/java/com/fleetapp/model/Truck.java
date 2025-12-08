package com.fleetapp.model;

public class Truck extends Vehicle {
    private double cargoCapacity;

    public Truck (int id, String licensePlate, String brand, String model, int year, double mileage, double cargoCapacity) {
        super(id, licensePlate, brand, model, year, mileage);
        this.cargoCapacity = cargoCapacity;
    }

    public  String getType(){
        return "Truck";
    }

    public boolean isOverLoaded(double loadweight) {
        return loadweight > cargoCapacity;

    }

    public double getCargoCapacity() {
        return cargoCapacity;
    }

}
