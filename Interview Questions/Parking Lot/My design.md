# Parking Lot LLD

## Requirements

- Support different vehicle types (Bike, Car, Truck).
- Support multiple parking floors.
- Different parking strategies (Nearest, Farthest, Best Fit).
- Different fee calculation strategies.
- Different payment strategies.
- Easily extensible for future requirements.

---

# Entities

- Vehicle
- ParkingSlot
- ParkingFloor
- ParkingLot
- Ticket
- ParkingLotManager

---

# Enums

## VehicleSize

```java
SMALL,
MEDIUM,
LARGE
```

---

# Vehicle (Abstract Class)

### Responsibility

Represents a vehicle and stores common vehicle information.

### Members

- VehicleSize vehicleSize
- String licenseNumber
- PaymentStrategy paymentStrategy *(Current Design)*

> **Future Enhancement**
>
> If payment requirements grow (TransactionId, Refund, GST, Payment Status, etc.), replace `PaymentStrategy` with a `Payment` object.
>
> ```text
> Vehicle
>     ↓
> Payment
>     ├── amount
>     ├── paymentStrategy
>     ├── paymentStatus
>     ├── paymentType
>     ├── transactionId
>     └── paymentTime
> ```

### Methods

- constructor(...)
- getVehicleSize()
- getLicenseNumber()
- getPaymentStrategy()
- setPaymentStrategy(...)

---

# Bike

### Responsibility

Represents a bike.

### Parent

Vehicle

### Initialization

```java
vehicleSize = VehicleSize.SMALL;
```

---

# Car

### Responsibility

Represents a car.

### Parent

Vehicle

### Initialization

```java
vehicleSize = VehicleSize.MEDIUM;
```

---

# Truck

### Responsibility

Represents a truck.

### Parent

Vehicle

### Initialization

```java
vehicleSize = VehicleSize.LARGE;
```

---

# ParkingSlot

### Responsibility

Represents a physical parking slot.
Maintains its own occupancy state and performs park/unpark operations.

### Members

- VehicleSize vehicleSize
- Vehicle parkedVehicle
- int slotNumber

### Methods

- constructor(...)
- boolean isAvailable()
- void parkVehicle(Vehicle vehicle)
- void unParkVehicle()

---

# ParkingFloor

### Responsibility

Represents a parking floor and manages all parking slots present on that floor.

### Members

- int floorNumber
- List<ParkingSlot> parkingSlots

### Methods

- constructor(...)
- addParkingSlot(ParkingSlot slot)

---

# ParkingLot (Singleton)

### Responsibility

Represents the complete parking lot and manages all parking floors.

### Design Pattern

**Singleton**

### Members

- static ParkingLot instance
- List<ParkingFloor> parkingFloors

### Methods

- getInstance()
- addParkingFloor(ParkingFloor floor)

---

# ParkingStrategy (Interface)

### Responsibility

Defines different parking algorithms.
Responsible for finding a suitable parking slot based on the implemented strategy and delegating the actual parking operation to the selected `ParkingSlot`.

### Methods

- ParkingSlot parkVehicle(ParkingLot parkingLot, Vehicle vehicle)

> **Note**
>
> `ParkingStrategy` decides **where** to park.
>
> `ParkingSlot` performs the actual **park/unpark** operation.

---

# NearestParkingStrategy

### Responsibility

Parks the vehicle in the first available slot on the nearest floor.

### Parent

ParkingStrategy

### Methods

- ParkingSlot parkVehicle(ParkingLot parkingLot, Vehicle vehicle)

---

# FarthestParkingStrategy

### Responsibility

Parks the vehicle in the first available slot on the farthest floor.

### Parent

ParkingStrategy

### Methods

- ParkingSlot parkVehicle(ParkingLot parkingLot, Vehicle vehicle)

---

# BestParkingStrategy

### Responsibility

Parks the vehicle in the best-fitted available slot (minimum required slot size).

### Parent

ParkingStrategy

### Methods

- ParkingSlot parkVehicle(ParkingLot parkingLot, Vehicle vehicle)

---

# FeeStrategy (Interface)

### Responsibility

Defines different fee calculation algorithms.

### Methods

- double calculateFees(Vehicle vehicle, Ticket ticket)

---

# TimeSizeBasedFeeStrategy

### Responsibility

Calculates parking fees based on parking duration and vehicle size.

### Parent

FeeStrategy

### Methods

- double calculateFees(Vehicle vehicle, Ticket ticket)

---

# TimeSizePeakHourBasedFeeStrategy

### Responsibility

Calculates parking fees based on parking duration, vehicle size and peak hours.

### Parent

FeeStrategy

### Methods

- double calculateFees(Vehicle vehicle, Ticket ticket)

---

# PaymentStrategy (Interface)

### Responsibility

Defines different payment algorithms.

### Methods

- void processPayment(double amount)

---

# CardPaymentStrategy

### Responsibility

Processes payment using Card.

### Parent

PaymentStrategy

### Methods

- void processPayment(double amount)

---

# UPIPaymentStrategy

### Responsibility

Processes payment using UPI.

### Parent

PaymentStrategy

### Methods

- void processPayment(double amount)

---

# CashPaymentStrategy

### Responsibility

Processes payment using Cash.

### Parent

PaymentStrategy

### Methods

- void processPayment(double amount)

---

# Ticket

### Responsibility

Represents a parking session.
Stores parking details from vehicle entry until exit.

### Members

- int ticketId
- ParkingSlot parkingSlot
- Vehicle vehicle
- LocalDateTime entryTime
- LocalDateTime exitTime
- double fees
- PaymentStrategy paymentStrategy *(Current Design)*

### Methods

- constructor(...) -> initialize ticketId, parkingSlot, vehicle, entryTime  
- setExitTime(...)
- setFees(...)
- setPaymentStrategy(...)
- getPaymentStrategy()

> **Future Enhancement**
>
> If payment requirements grow (Payment Status, Transaction ID, Refund,
> Partial Refund, GST, Payment Time, Gateway Response, etc.),
> introduce a dedicated `Payment` entity.
>
> ```text
> Ticket
>     │
>     ▼
> Payment
>     ├── amount
>     ├── paymentStrategy
>     ├── paymentStatus
>     ├── paymentType
>     ├── transactionId
>     ├── paymentTime
>     └── refundDetails
> ```
>
> This keeps `Ticket` focused only on the parking session while
> `Payment` manages everything related to payment.

---

# ParkingLotManager (Facade)

### Responsibility

Acts as the entry point of the Parking Lot system.
Coordinates vehicle entry, exit, parking, fee calculation and payment.

### Design Pattern

**Facade**

### Members

- ParkingLot parkingLot
- Map<Vehicle, Ticket> vehicleToTicketMap
- ParkingStrategy parkingStrategy
- FeeStrategy feeStrategy

### Methods

#### constructor(...)

- Get ParkingLot singleton instance.
- Initialize `vehicleToTicketMap`.
- Assign `ParkingStrategy`.
- Assign `FeeStrategy`.

---

#### vehicleEntry(Vehicle vehicle)

1. `ParkingSlot slot = parkingStrategy.parkVehicle(parkingLot, vehicle)
2. slot.parkVehicle(vehicle)
3. Create Ticket. constructor(ticketId,parkedSlot,vehicle,entryTime)
4. Store `<Vehicle, Ticket>` in the map.
5. Return Ticket.

---

#### vehicleExit(Vehicle vehicle)

1. Retrieve Ticket from `vehicleToTicketMap`.
3. Set exit time.
4. Calculate parking fees using `FeeStrategy`.
5. Store calculated fees in Ticket.
6. Process payment using selected `PaymentStrategy`.
7. store paymentStrategy in ticket
8. slot = ticket.getSlot();
9. Unpark vehicle - slot.unPark()
10. Remove `<Vehicle, Ticket>` from the map.

---

# Client

### Responsibility

Initializes the Parking Lot system and demonstrates its usage.

### Steps

1. Create ParkingSlots.
2. Create ParkingFloors and add ParkingSlots to them.
3. Get ParkingLot singleton instance and add ParkingFloors.
4. Create required Strategies.
    - ParkingStrategy
    - FeeStrategy
5. Create ParkingLotManager - assign parkingLot singleton instance, creates empty vehicleToTicket Map, assign ParkingStrategy, assign FeeStategy
6. Create Vehicles.
    - Bike
    - Car
    - Truck
7. Perform vehicle entry.
8. Perform vehicle exit.

### Sample Flow

```java
Bike bike = new Bike("GJ01AB1234");
Car car = new Car("GJ01CD5678");
Truck truck = new Truck("GJ01EF9999");

parkingLotManager.vehicleEntry(bike);

parkingLotManager.vehicleEntry(car);

parkingLotManager.vehicleEntry(truck);

parkingLotManager.vehicleExit(bike);

parkingLotManager.vehicleExit(car);

parkingLotManager.vehicleExit(truck);
```
# Possible Extensions

### 1. New Parking / Fee / Payment Algorithm

Simply create a new implementation of `ParkingStrategy`, `FeeStrategy` or `PaymentStrategy`. Existing classes remain unchanged (Open/Closed Principle).

---

### 2. Complex Payment Requirements

If payment requirements grow (Transaction ID, Refund, GST, Gateway Response, etc.), create a new `Payment` class to store all payment-related information.

Move `PaymentStrategy` from `Vehicle` to `Payment`, once payment is done add paymnet reference in ticket reason begin in future vehicle change the payment stretagy so we lost this info. so we record in ticket 

```java
vehicle.getPayment().processPayment();
```

This keeps payment-related logic and data in a single class.

---

### 3. Multiple Parking Lots

Replace the `ParkingLot` Singleton with a `ParkingLotService` that manages multiple `ParkingLot` instances.

```text
ParkingLotService
        │
        ├── ParkingLot (Hyderabad)
        ├── ParkingLot (Bangalore)
        └── ParkingLot (Pune)
```

---

### 4. Display Available Slots

Maintain an `availableSlotCount` in `ParkingFloor` and update it whenever a vehicle is parked or unparked. This avoids iterating through all slots every time.

---

### 5. Search Vehicle by License Number

Maintain

```java
Map<String, Ticket> licenseNumberToTicket;
```

to locate a parked vehicle in **O(1)** time.

---

### 6. Reservation Support

Create a new `Reservation` class containing vehicle, parking slot, start time and end time.

Before assigning a slot, `ParkingStrategy` should ignore slots that have an active reservation for another vehicle.

---

### 7. EV Charging Support

Add

```java
boolean supportsCharging;
```

to `ParkingSlot` (or create `EVParkingSlot` if EV slots require additional behavior).

Update `ParkingStrategy` to allocate charging-enabled slots only for EVs.

---

### 8. Parking Slot States

Replace

```java
isAvailable()
```

with

```java
enum ParkingSlotStatus {
    AVAILABLE,
    OCCUPIED,
    RESERVED,
    OUT_OF_SERVICE
}
```

`ParkingStrategy` should allocate only `AVAILABLE` slots, automatically ignoring reserved or maintenance slots.
