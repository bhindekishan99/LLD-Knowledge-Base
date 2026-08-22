package elevator.controller;

import elevator.enums.Direction;
import elevator.enums.RequestType;
import elevator.models.Elevator;
import elevator.models.Request;
import elevator.strategies.elevatorstrategy.ElevatorSelectionStrategy;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ElevatorController {

    private final ConcurrentHashMap<String, Elevator> elevators;

    private final ElevatorSelectionStrategy elevatorSelectionStrategy;

    public ElevatorController(
            Collection<Elevator> elevators,
            ElevatorSelectionStrategy elevatorSelectionStrategy) {

        this.elevators = new ConcurrentHashMap<>();

        for (Elevator elevator : elevators) {
            this.elevators.put(
                    elevator.getId(),
                    elevator
            );
        }

        this.elevatorSelectionStrategy =
                elevatorSelectionStrategy;
    }

    public void requestElevator(
            int floor,
            Direction direction) {

        RequestType requestType =
                direction == Direction.UP
                        ? RequestType.PICKUP_UP
                        : RequestType.PICKUP_DOWN;

        Request request =
                new Request(floor, requestType);

        Elevator elevator =
                elevatorSelectionStrategy.selectElevator(
                        elevators.values(),
                        floor,
                        direction
                );

        if (elevator != null) {
            elevator.addRequest(request);
        }
    }

    public void requestDestination(
            String elevatorId,
            int destinationFloor) {

        Elevator elevator =
                elevators.get(elevatorId);

        if (elevator == null) {
            return;
        }

        Request request =
                new Request(
                        destinationFloor,
                        RequestType.DESTINATION
                );

        elevator.addRequest(request);
    }

    public void step() {

        for (Elevator elevator : elevators.values()) {
            elevator.step();
        }
    }
}