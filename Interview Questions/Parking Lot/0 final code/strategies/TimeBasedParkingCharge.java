package strategies;

import models.*;

public class TimeBasedParkingCharge implements ParkingChargeStrategy {
    public double calculateParkingCharge(Ticket ticket){

        double parkingCharge = 5;

        return parkingCharge;
    }
}
