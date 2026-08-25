package strategies;

import models.*;

public interface ParkingStrategy {

    public Slot findAvailableSlot(ParkingLot lot, Vehicle vehicle);
    
}
