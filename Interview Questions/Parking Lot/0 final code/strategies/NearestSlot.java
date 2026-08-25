package strategies;

import models.*;

public class NearestSlot implements ParkingStrategy {
    
    public Slot findAvailableSlot(ParkingLot lot, Vehicle vehicle){
        //logic: 
        for(Floor floor : lot.getFloors()){
            Slot foundSlot = floor.findAvailableSlot(vehicle);
            if(foundSlot != null){
                return foundSlot;
            }
        }
        return null;
    }
}
