package elevator.observer;

import elevator.models.Elevator;

public interface ElevatorObserver {

    void update(Elevator elevator);
}