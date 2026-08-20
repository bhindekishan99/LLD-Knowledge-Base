# Parking Lot — Thread Safety

- **1. Floor responsibility:** We don't need `findAvailableSlot()` in `Floor` because `ParkingStrategy` is responsible for deciding which slot to select. `Floor` should only manage slots using functions like `addSlot()`, `removeSlot()`, and `getSlots()`.

- **2. ParkingStrategy + concurrency:** `ParkingStrategy` should not only find an available slot; it should also attempt to park/reserve the vehicle in that slot. Otherwise, another thread can occupy the slot between `findAvailableSlot()` and `ParkingLotService.parkVehicle()`.

- ***2.a. Check-and-park must be atomic:** The availability check and parking operation must happen as one atomic operation. Otherwise, two threads can both observe the same slot as available and both try to park in it.

- ***2.b. Atomic `tryPark()`:** Put the check + state change inside `Slot`: `public synchronized boolean tryPark(Vehicle vehicle) { if (this.vehicle != null) return false; this.vehicle = vehicle; return true; }`. This guarantees that only one thread can successfully park in a slot.

- ***2.c. `NearestSlotStrategy`:** Strategy iterates through floors and slots and calls `slot.tryPark(vehicle)`. If it returns `true`, the vehicle has been atomically parked and the strategy returns that slot. If it returns `false`, another thread already occupied it, so the strategy continues searching.
- 
```java
class Slot {

    private Vehicle vehicle;

    public synchronized boolean tryPark(Vehicle vehicle) {
        if (this.vehicle != null) {
            return false;
        }

        this.vehicle = vehicle;
        return true;
    }
}

class NearestSlotStrategy implements ParkingStrategy {

    public Slot findAvailableSlot(ParkingLot lot, Vehicle vehicle) {

        for (Floor floor : lot.getFloors()) {
            for (Slot slot : floor.getSlots()) {

                if (slot.tryPark(vehicle)) {
                    return slot;
                }
            }
        }

        return null;
    }
}
```


- **3. Singleton:** `ParkingLotService` is a Singleton because the requirement says only one `ParkingLotService` instance should exist. A thread-safe Singleton prevents multiple threads from creating multiple instances.

- **4. `volatile` in Singleton:** Use `volatile` for the Singleton instance when using double-checked locking. `volatile` provides the required visibility guarantees and prevents another thread from observing an incompletely initialized instance.


- **5. `CopyOnWriteArrayList` for Floor slots:** `Floor` contains a list of slots that is heavily read while searching and only occasionally modified when slots are added/removed. Therefore, `CopyOnWriteArrayList` is suitable for this read-heavy scenario, same for ParkingLot floors


- **14. `ConcurrentHashMap` for tickets:** Active tickets can be accessed by multiple threads, so use `ConcurrentHashMap` instead of `HashMap` for the ticket map.


## Project Structure

- **`model/ParkingLot.java`** → Represents the complete parking facility/building and manages multiple floors.
- **`model/Floor.java`** → Represents one parking floor and manages its slots.
- **`model/Slot.java`** → Represents one parking space and owns its occupancy state.
- **`model/Vehicle.java`** → Represents the vehicle entering the parking lot.
- **`model/Ticket.java`** → Represents a parking session/ticket.
- **`strategy/ParkingStrategy.java`** → Defines how an available slot should be selected.
- **`strategy/NearestSlotStrategy.java`** → Selects the nearest/first suitable slot and uses `tryPark()` to atomically reserve it.
- **`strategy/ParkingChargeStrategy.java`** → Defines how parking charges are calculated.
- **`strategy/PaymentStrategy.java`** → Defines how payment is processed.
- **`service/ParkingLotService.java`** → Orchestrates the overall parking/unparking flow.
- **`service/TicketService.java`** → Manages active tickets using `ConcurrentHashMap`.
- **`Main.java`** → Entry point/client code.

## Thread-Safety Summary

- **`ParkingLotService`** → Thread-safe Singleton using `volatile` + double-checked locking.
- **`ParkingLot.floors`** → `CopyOnWriteArrayList<Floor>` because floors are mostly read and occasionally modified.
- **`Floor.slots`** → `CopyOnWriteArrayList<Slot>` because slots are mostly read and occasionally modified.
- **`Slot` occupancy** → `synchronized tryPark()` so check + park is atomic.
- **Active tickets** → `ConcurrentHashMap`.
- **Main concurrency rule** → Never separate "check availability" from "reserve/park"; make the state transition atomic.# Parking Lot — Thread Safety