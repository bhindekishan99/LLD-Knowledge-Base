package elevator.strategies.elevatorstrategy;

import elevator.enums.Direction;
import elevator.enums.State;
import elevator.models.Elevator;

import java.util.Collection;

public class NearestElevatorStrategy
        implements ElevatorSelectionStrategy {

    @Override
    public Elevator selectElevator(
            Collection<Elevator> elevators,
            int floor,
            Direction direction) {

        Elevator nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {

            if (elevator.getState() == State.MAINTENANCE) {
                continue;
            }

            int distance =
                    Math.abs(elevator.getCurrentFloor() - floor);

            if (distance < minDistance) {
                minDistance = distance;
                nearest = elevator;
            }
        }

        return nearest;
    }
}