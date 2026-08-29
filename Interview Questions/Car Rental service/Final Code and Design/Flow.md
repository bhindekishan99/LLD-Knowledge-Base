# Car Rental Service — Flows

## 1. Book / Checkout Vehicle

- **`RentalBookingOrchestrator.checkoutVehicle()`** → receives `User`, `VehicleType`, `Location`, `startTime`, `endTime` | Main entry point for booking.

- **`RentalBookingOrchestrator` → `ReservationServiceStrategy.reserveVehicle()`** → delegates vehicle reservation.

- **`DefaultReservationServiceStrategy` → `StoreManager.findStoresByCity()`** → finds stores in the requested city.

- **`StoreManager` → candidate vehicles** → gets vehicles matching requested `VehicleType`.

- **Candidate vehicle → `synchronized(vehicle)`** → locks the specific vehicle so different vehicles can still be booked concurrently.

- **`Vehicle.getStatus()`** → checks whether vehicle is `AVAILABLE`.

- **If vehicle is not `AVAILABLE`** → release lock(no need to do explicitly, automatically done by synchronized) → try next candidate vehicle.

- **If vehicle is `AVAILABLE`** → change status from `AVAILABLE` → `RESERVED_HOLD`.

- **Calculate rent** → `RentCalculateStrategy.calculate()`.

- **Create `Reservation`** → associate `User`, `Vehicle`, `Store`, rental period, and calculated rent.

- **`ReservationRepository.add()`** → store the reservation.

- **Return `Reservation`** → `RentalBookingOrchestrator` receives the reservation.

- **Payment** → `PaymentStrategy.executePayment()`.

- **Payment successful** → lock vehicle → verify `RESERVED_HOLD` → set `ReservationStatus.CONFIRMED` → set vehicle status `RENTED`.

- **Payment failed** → lock vehicle → set `ReservationStatus.CANCELLED` → release vehicle: `RESERVED_HOLD → AVAILABLE`.

- **Notification** → `NotificationService.send(reservation)`.

```text
checkoutVehicle()
      ↓
reserveVehicle()
      ↓
find stores
      ↓
find candidate vehicles
      ↓
synchronized(vehicle)
      ↓
check AVAILABLE
      ↓
AVAILABLE → RESERVED_HOLD
      ↓
calculate rent
      ↓
create Reservation
      ↓
save Reservation
      ↓
payment
   ↙       ↘
success    failure
  ↓           ↓
CONFIRMED   CANCELLED
  ↓           ↓
RENTED      AVAILABLE
      ↓
notification
