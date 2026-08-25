package models;

import enums.*;

public class Vehicle{
    private String id;
    private String name;
    private VehicleType type;

    Vehicle(String id, String name, VehicleType type){
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public VehicleType getType(){
        return type;
    }

    public String getId(){
        return this.id;
    }
}