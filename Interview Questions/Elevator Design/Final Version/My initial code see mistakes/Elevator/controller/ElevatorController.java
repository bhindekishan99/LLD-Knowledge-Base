package controller;

import java.util.*;
import models.*;
import enums.*;
import strategies.*;
import strategies.elevatorstrategy.ElevatorSelectionStrategy;
import strategies.requestselectionstrategy.RequestSelectionStrategy;

public class ElevatorController {

    Map<String, Elevator> elevatorMap = new HashMap<>();
    private static volatile ElevatorController elevatorController = null;
    private ElevatorSelectionStrategy elevatorSelectionStrategy;
    private RequestSelectionStrategy requestSelectionStrategy;
    private ElevatorController(ElevatorSelectionStrategy elevatorSelectionStrategy, RequestSelectionStrategy requestSelectionStrategy){
        this.elevatorSelectionStrategy = elevatorSelectionStrategy;
        this.requestSelectionStrategy = requestSelectionStrategy;
    }

    public static ElevatorController getInstance(ElevatorSelectionStrategy elevatorSelectionStrategy, RequestSelectionStrategy requestSelectionStrategy){
        if(elevatorController != null){
            return elevatorController;
        }

        synchronized(ElevatorController.class){
            if(elevatorController == null){
                elevatorController = new ElevatorController(elevatorSelectionStrategy,requestSelectionStrategy);
            }
            return elevatorController;
        }
    }

    public void addElevator(Elevator elevator){
        elevatorMap.put(elevator.getId(), elevator);
    }

    //external request
    public void request(Direction desDirection, int currentFloor){
        String elevatorId = elevatorSelectionStrategy.selectElevator(elevatorMap, desDirection, currentFloor);
        Elevator elevator = elevatorMap.get(elevatorId);
        elevator.addRequest(currentFloor);
    }

    //ionternal request
    public void request(String elevatorId, int floor){
        Elevator elevator = elevatorMap.get(elevatorId);
        elevator.addRequest(floor);
    }

    public void moveToNextReq(){
        for(Map.Entry<String,Elevator> entry : elevatorMap.entrySet()){
            Elevator elevator = entry.getValue();
            int nextFloor = requestSelectionStrategy.selectNextRequest(elevator);
            elevator.moveTo(nextFloor);
        }
    }
    
}
