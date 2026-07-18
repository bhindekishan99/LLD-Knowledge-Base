# Design Patterns Used in Elevator System

| Design Pattern | Used In | Purpose |
|---------------|---------|---------|
| **Strategy** | `SchedulingStrategy` (`FCFS`, `SCAN`, `LOOK`, `EnergyEfficient`) | Allows changing the elevator scheduling algorithm without modifying `ElevatorController`. |
| **Observer** | `Elevator` → `ElevatorObserver` (`ElevatorDisplay`) | Notify displays/monitoring systems whenever elevator state or floor changes. |
| **Command** | `ElevatorRequest` implements `ElevatorCommand` | Encapsulates internal/external requests as objects so they can be queued, executed, cancelled, or prioritized. |
| **State** *(Implicit)* | `ElevatorState` (`IDLE`, `MOVING`, `STOPPED`, `MAINTENANCE`) | Represents the current operational state of the elevator. (Uses enum instead of full State Pattern.) |
| **Singleton** *(Optional Enhancement)* | `ElevatorController` | Can be implemented as Singleton if the building should have only one controller. *(Not implemented in this design.)* |
| **Factory** *(Possible Enhancement)* | Creating `SchedulingStrategy` | Can be used to create scheduling strategies based on configuration or runtime selection. *(Not implemented in this design.)* |
