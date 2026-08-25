package models;

import java.util.*;

public class Floor {

    private List<Slot> slots;

    public Floor(List<Slot> slots){
        this.slots = slots;
    }

    public void addSlot(Slot slot){
        this.slots.add(slot);
    }

    //public Slot getSlot()

    public Slot findAvailableSlot(Vehicle vehicle){
        for(Slot slot : slots){
            if(slot.getType() == vehicle.getType()){
                return slot;
            }
        }
        return null;
    }
    
}
