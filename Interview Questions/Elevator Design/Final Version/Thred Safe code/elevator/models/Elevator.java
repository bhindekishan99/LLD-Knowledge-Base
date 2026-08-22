package elevator.models;

import elevator.enums.Direction;
import elevator.enums.State;
import elevator.observer.ElevatorObserver;
import elevator.strategies.requestselectionstrategy.ElevatorSchedulingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class Elevator {

    private final String id;

    private int currentFloor;

    private Direction direction;

    private State state;

    private final Set<Request> requests;

    private final List<ElevatorObserver> observers;

    private final ElevatorSchedulingStrategy schedulingStrategy;

    public Elevator(
            String id,
            int initialFloor,
            ElevatorSchedulingStrategy schedulingStrategy) {

        this.id = id;
        this.currentFloor = initialFloor;
        this.direction = Direction.IDLE;
        this.state = State.WORKING;

        this.requests = new LinkedHashSet<>();

        this.observers =
                new CopyOnWriteArrayList<>();

        this.schedulingStrategy =
                schedulingStrategy;
    }

    // -----------------------------
    // Request management
    // -----------------------------

    public synchronized void addRequest(Request request) {
        requests.add(request);
    }

    public synchronized void removeRequest(Request request) {
        requests.remove(request);
    }

    /*
     * Return a snapshot.
     *
     * The caller cannot modify the original LinkedHashSet.
     */
    public synchronized List<Request> getRequests() {
        return new ArrayList<>(requests);
    }

    // -----------------------------
    // Elevator simulation
    // -----------------------------

    public synchronized void step() {

        if (state == State.MAINTENANCE) {
            return;
        }

        Request request =
                schedulingStrategy.selectNextRequest(this);

        if (request == null) {

            direction = Direction.IDLE;

            notifyObservers();

            return;
        }

        moveOneFloor(request);
    }

    private void moveOneFloor(Request request) {

        int targetFloor = request.getFloor();

        if (currentFloor == targetFloor) {

            requests.remove(request);

            direction = Direction.IDLE;

            notifyObservers();

            return;
        }

        state = State.WORKING;

        if (targetFloor > currentFloor) {

            direction = Direction.UP;
            currentFloor++;

        } else {

            direction = Direction.DOWN;
            currentFloor--;
        }

        notifyObservers();

        /*
         * Request is completed when elevator
         * reaches the destination.
         */
        if (currentFloor == targetFloor) {

            requests.remove(request);

            if (requests.isEmpty()) {
                direction = Direction.IDLE;
            }

            notifyObservers();
        }
    }

    // -----------------------------
    // Maintenance
    // -----------------------------

    public synchronized void setMaintenance(boolean maintenance) {

        if (maintenance) {
            state = State.MAINTENANCE;
            direction = Direction.IDLE;
        } else {
            state = State.WORKING;
        }

        notifyObservers();
    }

    // -----------------------------
    // Observer
    // -----------------------------

    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ElevatorObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {

        for (ElevatorObserver observer : observers) {
            observer.update(this);
        }
    }

    // -----------------------------
    // Getters
    // -----------------------------

    public String getId() {
        return id;
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized Direction getDirection() {
        return direction;
    }

    public synchronized State getState() {
        return state;
    }
}