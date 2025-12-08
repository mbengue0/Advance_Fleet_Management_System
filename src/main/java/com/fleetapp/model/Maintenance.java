package com.fleetapp.model;

import java.time.LocalDate;

public class Maintenance {
    private int id;
    private int vehicleId;
    private String description;
    private LocalDate date;
    private double cost;
    private String provider;
    private String category;

    public Maintenance(int id, int vehicleId, String description, LocalDate date, double cost, String provider, String category) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.description = description;
        this.date = date;
        this.cost = cost;
        this.provider = provider;
        this.category = category;
    }

    public String getSummary(){
        return "Maintenance" + this.id + ": " + this.vehicleId + this.description
                + " on "    + this.date + " (Cost: $" + this.cost + ")" + this.provider;
    }

    public int getId() {return id;}
    public int getVehicleId() {return vehicleId;}
    public String getDescription() {return description;}
    public LocalDate getDate() {return date;}
    public double getCost() {return cost;}
    public String getProvider() {return provider;}
    public String getCategory() { return category; }


}
