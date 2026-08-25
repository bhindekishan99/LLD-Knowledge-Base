package models;

import java.util.*;

public class ParkingLot {

    private List<Floor> floors;

    public ParkingLot(){}

    public ParkingLot(List<Floor> floors){
        this.floors = floors;
    }

    public void addFloor(Floor  floor){
        this.floors.add(floor);
    }

    public List<Floor> getFloors(){
        return this.floors;
    }

    public void parkVehicle(Slot slot, Vehicle vehicle){
        slot.parkVehicle(vehicle);
    }

    public void unParkVehicle(Slot slot){
        slot.unParkVehicle();
    }
    
}
