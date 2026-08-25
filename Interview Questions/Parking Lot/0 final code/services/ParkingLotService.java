package services;

import models.*;
import strategies.ParkingChargeStrategy;
import strategies.ParkingStrategy;
import strategies.PaymentStrategy;

public class ParkingLotService {
    
    private static volatile ParkingLotService parkingLotService;

    private ParkingChargeStrategy parkingChargeStrategy;
    private PaymentStrategy paymentStrategy;
    private ParkingStrategy parkingStrategy;
    private TicketService ticketService;
    private ParkingLot parkingLot;

    private ParkingLotService(){
    }

    public static ParkingLotService getParkingLotServiceInstance(){

        if(parkingLotService != null){
            return parkingLotService;
        }

        synchronized (ParkingLotService.class){
            if(parkingLotService != null){
                return parkingLotService;
            }
            parkingLotService = new ParkingLotService();
        }

        return parkingLotService;

    }

    public boolean parkVehicle(Vehicle vehicle){
        //1. find the available slot
        //2. create a ticket
        //3. slot.vehicle = vehicle
        // retunrn true

        Slot foundSlot = parkingStrategy.findAvailableSlot(parkingLot, vehicle);
        if(foundSlot==null){
            return false;
        }

        ticketService.createTicket(vehicle, foundSlot);

        parkingLot.parkVehicle(foundSlot,vehicle);

        return true;
    }

    public boolean onUnparkVehicle(String vechicleId){
        //1. stamp exit time
        //2. get the ticket and send to  parkingChargeStrategy.calculateCharge(ticket)
        //4. processPayment
        //5. mark slot free
        
        ticketService.setExitTime(vechicleId);
        Ticket ticket = ticketService.getTicket(vechicleId);

        double charge = parkingChargeStrategy.calculateParkingCharge(ticket);

        //here in onUnparkVehicle we can take variable paymentType, based on this we use 
        //factory pattern to get the prefered payment method
        boolean isPaymentSuccessful = paymentStrategy.processPayment(charge);

        if(isPaymentSuccessful == true){
            parkingLot.unParkVehicle(ticket.getSlot());
            return true;
        }
        return false;
        
    }

}
