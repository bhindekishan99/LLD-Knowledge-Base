package models;

import java.util.*;

public class Ticket {
    //Vehicle, slot, entryTime, exitTime, charge, paymentMehod

    private Vehicle vehicle;
    private Slot slot;
    private Date entryTime;
    private Date exitTime;
    private double charge;

    public Ticket(Vehicle vehicle,Slot slot,Date entryTime){
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = entryTime;
    }

    public Vehicle getVehicle(){
        return this.vehicle;
    }

    public void setExitTime(Date date){
        this.exitTime = date;
    }

    public Slot getSlot(){
        return this.slot;
    }
    //getter and setters
}
