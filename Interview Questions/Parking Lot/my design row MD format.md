Here is the entire design formatted as a clean, copy-pasteable Markdown block. You can copy the code block below and save it directly as a .md file (e.g., ParkingLotLLD.md). [1, 2] 

# Low-Level Design (LLD): Parking Lot System## 1. Enums & Core Entities
### `VehicleSize` (Enum)
* `SMALL`
* `MEDIUM`
* `LARGE`

### `Vehicle` (Abstract Class)* **Members:**
  * `vehicleSize: VehicleSize`
  * `licenseNumber: String`
  * `paymentInfo: Payment` *(Decoupled to a dedicated Payment class for scalability)** **Methods:**
  * `Constructor(vehicleSize, licenseNumber, paymentInfo)`
  * Getters for all fields
### Concrete Vehicles* **`Bike`** (Extends `Vehicle` → sets `vehicleSize = VehicleSize.SMALL`)
* **`Car`** (Extends `Vehicle` → sets `vehicleSize = VehicleSize.MEDIUM`)
* **`Truck`** (Extends `Vehicle` → sets `vehicleSize = VehicleSize.LARGE`)
---## 2. Infrastructure & Layout
### `ParkingSlot` (Class)* **Members:**
  * `slotNumber: String`
  * `vehicleSize: VehicleSize`
  * `parkedVehicle: Vehicle` (Nullable)* **Methods:**
  * `Constructor(slotNumber, vehicleSize)`
  * `isAvailable(): Boolean` → Returns true if `parkedVehicle` is null
  * `parkVehicle(vehicle: Vehicle): Void`
  * `unParkVehicle(): Void`

### `ParkingFloor` (Class)* **Members:**
  * `floorNumber: Int`
  * `slots: List<ParkingSlot>`* **Methods:**
  * `Constructor(floorNumber)`
  * `addParkingSlot(slot: ParkingSlot): Void`

### `ParkingLot` (Singleton Class)* **Members:**
  * `static instance: ParkingLot`
  * `floors: List<ParkingFloor>`* **Methods:**
  * `static getInstance(): ParkingLot`
  * `addParkingFloor(floor: ParkingFloor): Void`
---## 3. Core Strategy Interfaces & Implementations### Parking Strategy (Strategy Pattern)Determines **where** a vehicle should be placed based on specific rules.
* **`ParkingStrategy` (Interface)**
  * `findSlot(lot: ParkingLot, vehicle: Vehicle): ParkingSlot`
* **`NearestParkingStrategy`** (Implements `ParkingStrategy`)
  * Finds the first available slot starting from the lowest/nearest floor.
* **`FarthestParkingStrategy`** (Implements `ParkingStrategy`)
  * Finds the first available slot starting from the highest/farthest floor.
* **`BestFitParkingStrategy`** (Implements `ParkingStrategy`)
  * Finds the most tightly fitted available slot matching the vehicle size exactly.
### Fee Strategy (Strategy Pattern)Calculates the billing amount based on time, size, or external factors.
* **`FeeStrategy` (Interface)**
  * `calculateFees(vehicle: Vehicle, ticket: Ticket): Double`
* **`TimeSizeBasedFeeStrategy`** (Implements `FeeStrategy`)
  * Computes cost based purely on elapsed duration and `VehicleSize`.
* **`PeakHourFeeStrategy`** (Implements `FeeStrategy`)
  * Computes cost based on duration, `VehicleSize`, and dynamic peak-hour surges.
---## 4. Scalable Payment Subsystem
### `Payment` (Class)
*Encapsulates transactional metadata, decoupling payment logic from the core Vehicle lifecycle.** **Members:**
  * `amount: Double`
  * `paymentStatus: PaymentStatus` (Enum: PENDING, SUCCESS, FAILED, REFUNDED)
  * `transactionId: String`
  * `paymentStrategy: PaymentStrategy`* **Methods:**
  * `Constructor(paymentStrategy)`
  * `setPaymentStrategy(paymentStrategy: PaymentStrategy): Void`
  * `makePayment(fees: Double): Boolean` → Delegates processing to `paymentStrategy.processPayment(fees)`
### Payment Processing Strategy* **`PaymentStrategy` (Interface)**
  * `processPayment(amount: Double): Boolean`
* **`CardPaymentStrategy`** → Processes credit/debit transactions.
* **`UPIPaymentStrategy`** → Processes instant mobile/QR payments.
* **`CashPaymentStrategy`** → Processes physical currency tracking at the counter.
---## 5. Operations & System Orchestration
### `Ticket` (Class)* **Members:**
  * `ticketId: String`
  * `parkingSlot: ParkingSlot`
  * `vehicle: Vehicle`
  * `entryTime: DateTime`
  * `exitTime: DateTime`
  * `paymentDetails: Payment`* **Methods:**
  * `Constructor(ticketId, parkingSlot, vehicle, entryTime)`
  * `setExitTime(exitTime): Void`
  * `setPaymentDetails(payment: Payment): Void`

### `ParkingLotManager` (Facade Pattern)
*Acts as the central control hub, simplifying complex multi-step parking operations for the client.** **Members:**
  * `parkingLot: ParkingLot`
  * `activeTickets: Map<Vehicle, Ticket>`
  * `parkingStrategy: ParkingStrategy`
  * `feeStrategy: FeeStrategy`* **Methods:**
  * `Constructor(parkingStrategy, feeStrategy)` → Links the `ParkingLot` singleton instance and initializes an empty ticket map.
  * `vehicleEntry(vehicle: Vehicle): Ticket`
    1. Finds an empty spot: `ParkingSlot slot = parkingStrategy.findSlot(parkingLot, vehicle)`
    2. Parks the vehicle: `slot.parkVehicle(vehicle)`
    3. Spawns a ticket: `Ticket ticket = new Ticket(generateId(), slot, vehicle, currentTime)`
    4. Tracks it: `activeTickets.put(vehicle, ticket)`
    5. Returns the ticket.
  * `vehicleExit(vehicle: Vehicle): Void`
    1. Fetches ticket: `Ticket ticket = activeTickets.get(vehicle)`
    2. Sets timestamp: `ticket.setExitTime(currentTime)`
    3. Calculates bill: `Double totalFees = feeStrategy.calculateFees(vehicle, ticket)`
    4. Processes transactional payment: `vehicle.paymentInfo.makePayment(totalFees)`
    5. Updates ticket data: `ticket.setPaymentDetails(vehicle.paymentInfo)`
    6. Frees infrastructure: `ticket.parkingSlot.unParkVehicle()`
    7. Clears active record: `activeTickets.remove(vehicle)`
---
## 6. System Execution Lifecycle (`Client`)
1. **System Initialization:**
   * Create `ParkingSlot` objects of small, medium, and large variants.
   * Instantiates `ParkingFloor` systems and inserts the slots.
   * Fetches the global `ParkingLot` singleton and appends the configured floors.2. **Strategy Selection:**
   * Instantiate chosen behavioral algorithms (e.g., `NearestParkingStrategy`, `TimeSizeBasedFeeStrategy`).
   * Initialize the `ParkingLotManager` with these selected strategies.3. **Execution Workflow Simulation:**
   * Instantiate a `Bike`, `Car`, and `Truck` alongside their preferred payment methods.
   * Execute entry sequences via `parkingLotManager.vehicleEntry(vehicle)`.
   * Simulate elapsed duration.
   * Execute exit and automated billing sequences via `parkingLotManager.vehicleExit(vehicle)`.
