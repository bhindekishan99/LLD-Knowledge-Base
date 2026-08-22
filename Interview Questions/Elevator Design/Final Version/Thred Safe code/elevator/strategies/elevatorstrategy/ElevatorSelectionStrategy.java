package elevator.strategies.elevatorstrategy;

import elevator.enums.Direction;
import elevator.models.Elevator;

import java.util.Collection;

public interface ElevatorSelectionStrategy {

    Elevator selectElevator(
            Collection<Elevator> elevators,
            int floor,
            Direction direction
    );
}