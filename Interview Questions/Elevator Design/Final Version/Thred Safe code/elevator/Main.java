package elevator;

import elevator.controller.ElevatorController;
import elevator.enums.Direction;
import elevator.models.Elevator;
import elevator.observer.Display;
import elevator.strategies.elevatorstrategy.ElevatorSelectionStrategy;
import elevator.strategies.elevatorstrategy.NearestElevatorStrategy;
import elevator.strategies.requestselectionstrategy.DirectionalSchedulingStrategy;
import elevator.strategies.requestselectionstrategy.ElevatorSchedulingStrategy;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Scheduling strategy
        ElevatorSchedulingStrategy schedulingStrategy =
                new DirectionalSchedulingStrategy();

        // Elevators
        Elevator elevator1 =
                new Elevator(
                        "E1",
                        0,
                        schedulingStrategy
                );

        Elevator elevator2 =
                new Elevator(
                        "E2",
                        5,
                        schedulingStrategy
                );

        Elevator elevator3 =
                new Elevator(
                        "E3",
                        9,
                        schedulingStrategy
                );

        // Observer
        Display display = new Display();

        elevator1.addObserver(display);
        elevator2.addObserver(display);
        elevator3.addObserver(display);

        // Elevator selection strategy
        ElevatorSelectionStrategy selectionStrategy =
                new NearestElevatorStrategy();

        // Controller
        ElevatorController controller =
                new ElevatorController(
                        List.of(
                                elevator1,
                                elevator2,
                                elevator3
                        ),
                        selectionStrategy
                );

        // External request
        controller.requestElevator(
                3,
                Direction.UP
        );

        // Internal request
        controller.requestDestination(
                "E1",
                8
        );

        // Simulation
        for (int i = 0; i < 10; i++) {

            System.out.println(
                    "------ TICK " + i + " ------"
            );

            controller.step();
        }
    }
}