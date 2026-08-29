# Car Rental System — UML / Class Diagram

## Enums

- **`VehicleType`** → Values: `SEDAN`, `HATCHBACK`, `SUV`, etc. | Represents the type/category of vehicle supported by the system.

- **`VehicleStatus`** → Values: `AVAILABLE`, `BOOKED`, `DECOMMISSIONED`, `IN_SERVICE` | Represents the current status of a vehicle.

- **`BookingStatus`** → Values: `CREATED`, `CONFIRMED`, `COMPLETED`, `FAILED`, `CANCELLED` | Represents the lifecycle/status of a booking.

- **`PaymentStatus`** → Values: `PENDING`, `SUCCESS`, `FAILED` | Represents the payment status of a booking.

- **`PricingStrategyType`** → Values: `TIME_BASED`, `DISTANCE_BASED` | Represents supported pricing approaches.

## Core Entities

- **`Vehicle`** → Abstract class | Attributes: `licensePlate`, `model`, `status`, `pricePerHour`, `pricePerKm`, `vehicleType`, `bookingCount`, `AtomicBoolean isBooked` | Methods: `incrementBookingCount()` | Base class containing common vehicle information.

- **`Sedan`** → Extends `Vehicle` | Represents a sedan vehicle.

- **`SUV`** → Extends `Vehicle` | Represents an SUV vehicle.

- **`Branch`** → Attributes: `branchId`, `city`, `Map<VehicleType, List<Vehicle>> vehicles` | Methods: `addVehicle()`, `removeVehicle()`, `getVehiclesByType()` | Represents a rental branch and maintains its vehicle inventory.

- **`User`** → Attributes: `userId`, `name`, `email` | Represents the customer renting a vehicle.

- **`Booking`** → Attributes: `bookingId`, `user`, `vehicle`, `pickupBranch`, `dropBranch`, `duration`, `status`, `amount`, `paymentStatus` | Represents a vehicle rental transaction.

## Booking Strategy

- **`BookingStrategy`** → Interface | Method: `bookVehicle(List<Vehicle> vehicles)` | Defines how a vehicle is selected/booked when multiple vehicles are available.

- **`CheapestBookingStrategy`** → Implements `BookingStrategy` | Selects the cheapest available vehicle and uses atomic booking state change to handle concurrent booking attempts.

- **`LeastBookedVehicleStrategy`** → Implements `BookingStrategy` | Selects the available vehicle with the lowest booking count and uses atomic booking state change.

## Pricing Strategy

- **`PricingStrategy`** → Interface | Method: `calculatePrice(Vehicle vehicle, startTime, endTime, distance)` | Defines how rental price is calculated.

- **`HourlyPricingStrategy`** → Implements `PricingStrategy` | Calculates price based on rental duration and vehicle's hourly rate.

- **`DistanceBasedPricingStrategy`** → Implements `PricingStrategy` | Calculates price based on distance and vehicle's per-kilometer rate.

## Payment Strategy

- **`PaymentStrategy`** → Interface | Method: `processPayment(Booking booking)` | Defines how payment is processed.

- **`CardPaymentStrategy`** → Implements `PaymentStrategy` | Processes payment using a card.

- **`CashPaymentStrategy`** → Implements `PaymentStrategy` | Processes payment using cash.

- **`PaymentProcessor`** → Attribute: `PaymentStrategy paymentStrategy` | Method: `pay(Booking booking)` | Context class that delegates payment processing to the injected payment strategy.

## Factory

- **`VehicleFactory`** → Method: `createVehicle(...)` | Centralizes vehicle creation and avoids vehicle-type `if/else` logic in the client.

## Repositories

- **`BranchRepository`** → Attribute: `Map<String, Branch> branches` | Methods: `addBranch()`, `getBranch()`, `removeBranch()`, `getAllBranches()` | Handles CRUD operations for branches.

- **`BookingRepository`** → Attribute: `Map<String, Booking> bookings` | Methods: `addBooking()`, `getBooking()`, `removeBooking()`, `getAllBookings()` | Handles CRUD operations for bookings.

## Service

- **`BookingService`** → Singleton | Attributes: `BranchRepository branchRepository`, `BookingRepository bookingRepository`, `BookingStrategy bookingStrategy`, `PricingStrategy pricingStrategy` | Methods: `bookVehicle()`, `returnVehicle()` | Main business/service layer responsible for booking and returning vehicles; delegates persistence, vehicle selection, pricing, and payment to specialized components.

## Relationships

- **`Vehicle` → `Sedan`** → `Sedan` is-a `Vehicle` | Inheritance.

- **`Vehicle` → `SUV`** → `SUV` is-a `Vehicle` | Inheritance.

- **`Branch` → `Vehicle`** → A branch has multiple vehicles through its vehicle inventory: `1 → *`.

- **`Booking` → `User`** → A booking has one user: `1 → 1`.

- **`Booking` → `Vehicle`** → A booking has one rented vehicle: `1 → 1`.

- **`Booking` → `Branch`** → A booking has one pickup branch and one drop branch: `1 → 2`.

- **`Booking` → `BookingStatus`** → A booking has one booking status.

- **`Booking` → `PaymentStatus`** → A booking has one payment status.

- **`Vehicle` → `VehicleType`** → Each vehicle has one vehicle type.

- **`Vehicle` → `VehicleStatus`** → Each vehicle has one current status.

- **`BookingService` → `BranchRepository`** → Booking service uses branch repository for branch-related data operations.

- **`BookingService` → `BookingRepository`** → Booking service uses booking repository for booking-related data operations.

- **`BookingService` → `BookingStrategy`** → Booking service uses a strategy to select/book a vehicle.

- **`BookingService` → `PricingStrategy`** → Booking service uses a strategy to calculate rental price.

- **`BookingService` → `PaymentProcessor`** → Booking service delegates payment processing to the payment processor.

- **`PaymentProcessor` → `PaymentStrategy`** → Payment processor uses the injected payment strategy.

- **`BookingStrategy` → `CheapestBookingStrategy`** → `CheapestBookingStrategy` implements `BookingStrategy`.

- **`BookingStrategy` → `LeastBookedVehicleStrategy`** → `LeastBookedVehicleStrategy` implements `BookingStrategy`.

- **`PricingStrategy` → `HourlyPricingStrategy`** → `HourlyPricingStrategy` implements `PricingStrategy`.

- **`PricingStrategy` → `DistanceBasedPricingStrategy`** → `DistanceBasedPricingStrategy` implements `PricingStrategy`.

- **`PaymentStrategy` → `CardPaymentStrategy`** → `CardPaymentStrategy` implements `PaymentStrategy`.

- **`PaymentStrategy` → `CashPaymentStrategy`** → `CashPaymentStrategy` implements `PaymentStrategy`.

- **`VehicleFactory` → `Vehicle`** → Factory creates concrete `Vehicle` objects such as `Sedan` and `SUV`.

## Main Booking Flow

- **Book vehicle** → `BookingService.bookVehicle()` → validate pickup branch → get available vehicles by `VehicleType` → `BookingStrategy.bookVehicle()` → calculate price using `PricingStrategy` → create `Booking` → process payment using `PaymentProcessor` → on success mark booking `CONFIRMED` and vehicle `BOOKED`.

- **Failed payment** → Payment fails → booking becomes `FAILED` → vehicle's atomic booking state is rolled back so another user can book it.

- **Return vehicle** → `BookingService.returnVehicle(bookingId)` → fetch booking from `BookingRepository` → validate booking → mark booking `COMPLETED` → reset vehicle booking state → add vehicle to drop branch.

## Concurrency

- **Vehicle booking** → `Vehicle.isBooked` is an `AtomicBoolean` → use atomic `compareAndSet(false, true)` so only one concurrent thread can successfully reserve the same vehicle.

- **Booking strategies** → Both `CheapestBookingStrategy` and `LeastBookedVehicleStrategy` iterate through candidate vehicles and attempt an atomic state transition; if one vehicle is already booked, they try the next available vehicle.

- **Important concurrency boundary** → The atomic operation is the transition `isBooked: false → true`; this prevents two users from successfully booking the same vehicle simultaneously.

## High-Level Relationship

- **`BookingService` → `BookingRepository` + `BranchRepository` + `BookingStrategy` + `PricingStrategy` + `PaymentProcessor`** → Service orchestrates the business flow while delegating specialized responsibilities.

- **`Branch` → `Vehicle`** → Branch owns/maintains the vehicle inventory.

- **`Booking` → `User` + `Vehicle` + `Branch`** → Booking connects the customer, rented vehicle, pickup branch, and drop branch.

- **Strategy families** → `BookingStrategy`, `PricingStrategy`, and `PaymentStrategy` each use the Strategy pattern so new algorithms/payment methods can be added without modifying existing code.

- **Factory** → `VehicleFactory` centralizes creation of concrete vehicle types.

- **Singleton** → `BookingService` has one shared service instance.

## Code Struture
```Text
BookingService
├── BranchRepository
├── BookingRepository
├── BookingStrategy
│   ├── CheapestBookingStrategy
│   └── LeastBookedVehicleStrategy
├── PricingStrategy
│   ├── HourlyPricingStrategy
│   └── DistanceBasedPricingStrategy
└── PaymentProcessor
    └── PaymentStrategy
        ├── CardPaymentStrategy
        └── CashPaymentStrategy

Branch
└── Vehicle*
    ├── Sedan
    └── SUV

Booking
├── User
├── Vehicle
├── Pickup Branch
└── Drop Branch

VehicleFactory
└── creates Vehicle
```
