package strategies.elevatorstrategy;

import java.util.*;
import enums.*;
import models.*;


public interface ElevatorSelectionStrategy {

    public String selectElevator(Map<String,Elevator> elevatorMap, Direction destinationDirection, int currentFloor);
    
}
