package elevator.strategies.requestselectionstrategy;

import elevator.enums.Direction;
import elevator.enums.RequestType;
import elevator.models.Elevator;
import elevator.models.Request;

import java.util.List;

public class DirectionalSchedulingStrategy
        implements ElevatorSchedulingStrategy {

    @Override
    public Request selectNextRequest(Elevator elevator) {

        List<Request> requests = elevator.getRequests();

        if (requests.isEmpty()) {
            return null;
        }

        Request bestRequest = null;
        int bestDistance = Integer.MAX_VALUE;

        int currentFloor = elevator.getCurrentFloor();
        Direction direction = elevator.getDirection();

        // First try requests compatible with current direction.
        for (Request request : requests) {

            if (!isCompatible(
                    request,
                    currentFloor,
                    direction)) {
                continue;
            }

            int distance =
                    Math.abs(currentFloor - request.getFloor());

            if (distance < bestDistance) {
                bestDistance = distance;
                bestRequest = request;
            }
        }

        // If no compatible request exists,
        // choose the nearest request and reverse direction.
        if (bestRequest == null) {

            bestDistance = Integer.MAX_VALUE;

            for (Request request : requests) {

                int distance =
                        Math.abs(currentFloor - request.getFloor());

                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestRequest = request;
                }
            }
        }

        return bestRequest;
    }

    private boolean isCompatible(
            Request request,
            int currentFloor,
            Direction direction) {

        int requestFloor = request.getFloor();

        if (currentFloor == requestFloor) {
            return true;
        }

        if (direction == Direction.IDLE) {
            return true;
        }

        if (direction == Direction.UP) {

            if (requestFloor < currentFloor) {
                return false;
            }

            return request.getType() == RequestType.DESTINATION
                    || request.getType() == RequestType.PICKUP_UP;
        }

        if (direction == Direction.DOWN) {

            if (requestFloor > currentFloor) {
                return false;
            }

            return request.getType() == RequestType.DESTINATION
                    || request.getType() == RequestType.PICKUP_DOWN;
        }

        return true;
    }
}