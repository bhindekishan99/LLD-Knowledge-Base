# Evolution of the Design

## Initial Approach

Initially, I created the following classes:

- `User`
- `Vehicle`
- `Store`
- `Reservation`
- `StoreManager`

### Workflow

When a booking request comes to `StoreManager`:

1. Search stores based on the user's requested location.
    - For each matching store, search for the requested vehicle type.
    - Store all matching vehicles in an `availableVehicles` list.

2. Pick the first available vehicle.
    - Create a `Reservation` with `PENDING` status.
    - Mark the vehicle status as `RESERVED_HOLD`.
    - While creating the reservation, use `RentCalculateStrategy` to calculate the rental amount.

3. Execute payment using `PaymentStrategy`.
    - **If payment succeeds**
        - `reservation.status = CONFIRMED`
        - `vehicle.status = RENTED`
    - **Else**
        - `reservation.status = CANCELLED`
        - `vehicle.status = AVAILABLE`

---

## Problem with this Design

The biggest issue is that **StoreManager is doing too many things.**

It is responsible for:

- Searching stores
- Searching vehicles
- Creating reservations
- Calculating rent
- Executing payments
- Updating reservation status
- Updating vehicle status

This clearly violates the **Single Responsibility Principle (SRP)**.

---

# Improved Design

Instead of putting everything inside `StoreManager`, responsibilities are divided into dedicated classes.

## Domain Classes

- `User`
- `Vehicle`
- `Store`
- `Reservation`

`Store` contains the list of vehicles and exposes methods like:

```java
getVehicles()

getAvailableVehiclesByType()
```

---

## StoreManager

`StoreManager` is responsible **only for inventory and store management**.

### Responsibilities

- `addStore()`
- `removeStore()`
- `findStoresByLocation()`
- `getLocationOfStore()`

It no longer handles reservation or payment logic.

---

## ReservationService

Since a reservation represents the mapping between:

- User
- Vehicle
- Store

it is responsible for creating reservations.

### Responsibilities

- Create a reservation
- Calculate rental amount using `RentCalculateStrategy`
- Mark reservation as `PENDING`
- Temporarily lock the selected vehicle (`RESERVED_HOLD`)

```java
createPendingReservation(...)
```

---

## Payment Strategy

Payment logic is completely separated using the Strategy Pattern.

```java
PaymentStrategy

↓

CreditCardPayment

UPIPayment

WalletPayment
```

---

## Notification Service

Notification logic is also separated.

```java
NotificationService

↓

WhatsAppNotification

EmailNotification

SMSNotification
```

---

# RentalBookingOrchestrator

Finally, create a `RentalBookingOrchestrator` that coordinates all services.

## Members

- `StoreManager`
- `ReservationService`
- `NotificationService`

Payment is provided using `PaymentStrategy`.

---

## Workflow

When a booking request arrives:

1. Find all stores based on the requested location.

```java
storeManager.findStoresByLocation(location)
```

2. Search each store for the requested vehicle type.

```java
store.getAvailableVehiclesByType(type)
```

3. Create a pending reservation.

```java
reservationService.createPendingReservation(...)
```

This will:

- Calculate rent using `RentCalculateStrategy`
- Set reservation status to `PENDING`
- Set vehicle status to `RESERVED_HOLD`

4. Execute payment.

```java
paymentStrategy.executePayment(reservation.getRentFees())
```

5. Update statuses based on payment result.

**If payment succeeds**

```text
reservation.status = CONFIRMED

vehicle.status = RENTED
```

**Else**

```text
reservation.status = CANCELLED

vehicle.status = AVAILABLE
```

6. Send notification.

```java
notificationService.send(reservation)
```

7. Return the reservation.

---

# Final Architecture

```text
                    RentalBookingOrchestrator
                               │
        ┌──────────────────────┼──────────────────────┐
        │                      │                      │
        ▼                      ▼                      ▼
 StoreManager          ReservationService     NotificationService
        │                      │
        │                      ▼
        │           RentCalculateStrategy
        │
        ▼
      Store
        │
        ▼
     Vehicle

PaymentStrategy is injected into the Orchestrator
and used during checkout.
```

---

# Benefits of the Improved Design

- ✅ Follows the Single Responsibility Principle (SRP)
- ✅ Easy to extend pricing using Strategy Pattern
- ✅ Easy to add new payment methods
- ✅ Easy to add new notification channels
- ✅ Business workflow is centralized inside `RentalBookingOrchestrator`
- ✅ Each class has a single, well-defined responsibility
- ✅ More maintainable and easier to test
