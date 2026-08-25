package services;

import models.*;
import java.util.*;

public class TicketService {
    
    private Map<String, Ticket> vehicleToTicketMap;

    public TicketService() {
        vehicleToTicketMap = new HashMap<>();
    }

    public Ticket getTicket(String vehicleId){
        return vehicleToTicketMap.getOrDefault(vehicleId, null);
    }

    public void addTicket(Ticket ticket){
        String vehicleId = ticket.getVehicle().getId();
        vehicleToTicketMap.put(vehicleId, ticket);
    }

    public void createTicket(Vehicle vehicle, Slot slot){
        Ticket ticket = new Ticket(vehicle,slot,new Date());
        this.addTicket(ticket);
    }

    public void setExitTime(String vehicleId){
        Ticket ticket = vehicleToTicketMap.get(vehicleId);
        ticket.setExitTime(new Date());
    }


}
