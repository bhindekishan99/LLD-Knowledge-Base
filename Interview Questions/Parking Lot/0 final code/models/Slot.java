package models;

import enums.VehicleType;

public class Slot{
    private String id;
    private VehicleType type;
    private Vehicle vehicle;

    public Slot(String id, VehicleType type){
        this.id = id;
        this.type = type;
    }

    public void parkVehicle(Vehicle vehicle){
        this.vehicle = vehicle;
    }

    public void unParkVehicle(){
        this.vehicle = null;
    }
    //getters and setters

    public VehicleType getType(){
        return this.type;
    }
}