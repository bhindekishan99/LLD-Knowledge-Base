## ElevatorController

### Responsibility
- Receive requests.
- Select the best elevator.
- Delegate request to the selected elevator.

### Members

```java
List<Elevator> elevators;
ElevatorSelectionStrategy selectionStrategy;
```

### Constructor

```java
ElevatorController(
    List<Elevator> elevators,
    ElevatorSelectionStrategy selectionStrategy
)
```

### Methods

```java
void submitRequest(ElevatorRequest request);

Elevator selectElevator(ElevatorRequest request);

void assignRequest(
    Elevator elevator,
    ElevatorRequest request
);
```

### Important Algorithm

```text
submitRequest(request)

1. elevator = selectionStrategy.selectElevator(...)
2. elevator.addRequest(request)
3. elevator.processNextRequest()
```
## Elevator

### Responsibility
- Execute assigned requests.
- Move between floors.
- Notify observers.

### Members

```java
int id;
int currentFloor;
Direction direction;
ElevatorState state;

Queue<ElevatorRequest> requests;

SchedulingStrategy schedulingStrategy;

List<ElevatorObserver> observers;
```

### Constructor

```java
Elevator(
    int id,
    SchedulingStrategy schedulingStrategy
)
```

### Methods

```java
void addRequest(ElevatorRequest request);

void processNextRequest();

void moveToFloor(int floor);

void openDoor();

void closeDoor();

void notifyObservers();

void addObserver(ElevatorObserver observer);

void removeObserver(ElevatorObserver observer);
```

### Important Algorithm

```text
processNextRequest()

1. nextFloor =
       schedulingStrategy.getNextFloor(...)
2. moveToFloor(nextFloor)
3. notifyObservers()
```

## SchedulingStrategy

### Responsibility
Determine the next floor for an elevator.

### Methods

```java
int getNextFloor(
    Elevator elevator,
    Queue<ElevatorRequest> requests
);
```

### Implementations

```java
LOOKStrategy

SCANStrategy

FCFSStrategy
```

## ElevatorSelectionStrategy

### Responsibility
Select the best elevator for a new request.

### Methods

```java
Elevator selectElevator(
    List<Elevator> elevators,
    ElevatorRequest request
);
```

### Implementations

```java
NearestElevatorStrategy
```

### Possible Extensions

```text
LeastBusyStrategy

EstimatedArrivalStrategy

AIGroupSchedulingStrategy
```

## ElevatorObserver

### Responsibility
Receive notifications whenever an elevator's state changes.

### Methods

```java
void update(Elevator elevator);
```

### Implementations

```java
ElevatorDisplay
```

### Possible Extensions

```text
MonitoringDashboard

LoggingService

MobileAppNotifier

EmergencyAlertSystem

AnalyticsService
```

## ElevatorDisplay

### Responsibility
Display the elevator's current status.

### Members

```java
int elevatorId;
```

### Constructor

```java
ElevatorDisplay(int elevatorId)
```

### Methods

```java
void update(Elevator elevator);

void show(
    int currentFloor,
    Direction direction,
    ElevatorState state
);
```

### Important Algorithm

```text
update(elevator)

1. Read currentFloor from elevator.
2. Read direction from elevator.
3. Read state from elevator.
4. Call show(...).
```
