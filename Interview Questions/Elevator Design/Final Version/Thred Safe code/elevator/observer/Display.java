package elevator.observer;

import elevator.models.Elevator;

public class Display implements ElevatorObserver {

    @Override
    public void update(Elevator elevator) {

        System.out.println(
                "Elevator " + elevator.getId()
                        + " | Floor: " + elevator.getCurrentFloor()
                        + " | Direction: " + elevator.getDirection()
                        + " | State: " + elevator.getState()
        );
    }
}