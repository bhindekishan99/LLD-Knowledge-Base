package elevator.strategies.requestselectionstrategy;

import elevator.models.Elevator;
import elevator.models.Request;

public interface ElevatorSchedulingStrategy {

    Request selectNextRequest(Elevator elevator);
}