# BookMyShow LLD — Class Diagram

> Derived from the provided Shubh transcript. This captures the models, services, repositories, payment Strategy + Factory, and Lock Provider design used in the video.

## 1. Models

```text
+-------------------+          +-------------------+
|      Theater      |          |       Movie       |
+-------------------+          +-------------------+
| id                |          | id                |
| name              |          | title             |
| screens           |          | durationMinutes   |
+-------------------+          +-------------------+
| addScreen()       |          +---------+---------+
| getScreen()       |                    |
+---------+---------+                    | played in
          | has                          v
          v                     +-------------------+
+-------------------+            |       Show        |
|      Screen       |            +-------------------+
+-------------------+            | id                |
| id                |            | movie             |
| seats             |            | theater           |
+-------------------+            | screen            |
| addSeat()         |            | startTime         |
| getSeat()         |            | endTime           |
+---------+---------+            +-------------------+
          |
          | has
          v
+---------------------------+
|       <<abstract>>        |
|           Seat            |
+---------------------------+
| id                        |
| price                     |
+---------------------------+
| getType()                 |
+-------------+-------------+
              |
        +-----+-----+
        |           |
        v           v
+---------------+ +----------------+
| RegularSeat   | | ReclinerSeat   |
+---------------+ +----------------+
| getType()     | | getType()      |
+---------------+ +----------------+

+----------------------------------+
|             Booking              |
+----------------------------------+
| bookingId                        |
| userId                           |
| showId                           |
| seatIds                          |
| status                           |
| paymentType                      |
| amount                           |
+----------------------------------+
```

The `Seat` abstraction makes new seat types extensible: e.g. Premium/VIP can be added by extending `Seat`.

## 2. Enums

```text
BookingStatus
----------------
CREATED
CONFIRMED
FAILED
CANCELLED

PaymentType
----------------
UPI
CARD

SeatStatus
----------------
aAVAILABLE
LOCKED
BOOKED

SeatType
----------------
REGULAR
RECLINER
```

## 3. Services + Repositories

```text
+---------------------+       +-------------------------+
|   TheaterService    |------>| TheaterRepository       |
+---------------------+       +-------------------------+
| createTheater()     |       | save()                  |
| addScreen()         |       | get()                   |
| addSeat()           |       +-------------------------+
+---------------------+

+---------------------+       +-------------------------+
|    MovieService     |------>| MovieRepository         |
+---------------------+       +-------------------------+
| createMovie()       |       | save()                  |
| getMovie()          |       | get()                   |
+---------------------+       +-------------------------+

+---------------------+       +-------------------------+
|     ShowService     |------>| ShowRepository          |
+---------------------+       +-------------------------+
| createShow()        |       | save()                  |
| getShow()           |       | get()                   |
| getShowsByMovie()   |       +-------------------------+
+---------------------+

+---------------------+       +-------------------------+
|   BookingService    |------>| BookingRepository       |
+---------------------+       +-------------------------+
| createBooking()     |       | save()                  |
| confirmBooking()    |       | get()                   |
+---------------------+       +-------------------------+
```

Repositories provide persistence abstraction; in the LLD implementation they use in-memory maps instead of a real database.

## 4. Payment — Strategy + Factory

```text
                         +---------------------------+
                         |    PaymentStrategy        |
                         |       <<interface>>       |
                         +---------------------------+
                         | + pay(Booking): boolean   |
                         +-------------+-------------+
                                       ^
                                       |
                         +-------------+-------------+
                         |                           |
              +---------------------+     +---------------------+
              | UPIPaymentStrategy  |     | CardPaymentStrategy |
              +---------------------+     +---------------------+
              | + pay()             |     | + pay()             |
              +---------------------+     +---------------------+

                         +----------------------------+
                         | PaymentStrategyFactory     |
                         +----------------------------+
                         | + getStrategy(type)        |
                         +----------------------------+
                                      |
                                      v
                              PaymentStrategy
```

`PaymentStrategy` lets each payment method have its own implementation. The factory centralizes the selection logic based on `PaymentType`.

## 5. Lock Provider — Concurrency

```text
                       +-----------------------------+
                       |       LockProvider           |
                       |        <<interface>>         |
                       +-----------------------------+
                       | + tryLock(key, ttl, userId) |
                       | + unlock(key)                |
                       | + isLockExpired(key)        |
                       | + isLockedBy(key, userId)    |
                       +--------------+--------------+
                                      ^
                                      |
                       +--------------+--------------+
                       |                             |
          +-------------------------+    +-------------------------+
          | InMemoryLockProvider    |    | RedisLockProvider      |
          +-------------------------+    +-------------------------+
          | ConcurrentHashMap       |    | Redis                  |
          | <key, LockExpiry>       |    | distributed lock       |
          +-------------------------+    +-------------------------+

+-----------------------------+
|         LockExpiry          |
+-----------------------------+
| deadline                    |
| ownerUserId                 |
+-----------------------------+
```

Lock key:

```text
showId + seatId
```

Example:

```text
SHOW1 + SEAT5
    ↓
SHOW1:SEAT5
```

The lock identifies a specific seat for a specific show.

## 6. Complete Relationship

```text
                         Movie
                           |
                           | played in
                           v
                          Show
                       /                           theater        screen
                    |              |
                    v              v
                 Theater        Screen
                                  |
                                  | has
                                  v
                                 Seat
                               /                                    v        v
                         Regular    Recliner


Client
  |
  v
BookingService
  |
  +------> LockProvider
  |            |
  |            +--> InMemoryLockProvider
  |            |
  |            +--> RedisLockProvider
  |
  +------> BookingRepository
  |
  +------> PaymentStrategyFactory
               |
               v
        PaymentStrategy
           /                 v         v
        UPI       CARD
```

## 7. Create Booking Flow

```text
User selects seats
        |
        v
BookingService.createBooking()
        |
        | for every selected seat
        v
LockProvider.tryLock(showId + seatId, TTL, userId)
        |
        +---- failure ---> Seat unavailable
        |
        +---- success
                |
                v
        calculate total price
                |
                v
        create Booking
                |
                v
        BookingRepository.save()
```

Important concurrency rule:

```text
If even ONE selected seat cannot be locked,
the complete booking attempt fails.
```

This prevents a partial booking.

## 8. Confirm Booking Flow

```text
User clicks Pay
       |
       v
confirmBooking()
       |
       v
status == CREATED ?
       |
       v
For every seat:
   isLockExpired(key)?
   isLockedBy(key, userId)?
       |
       +---- failure ---> reject confirmation
       |
       v
PaymentStrategyFactory
       |
       v
PaymentStrategy.pay(booking)
       |
       v
unlock all seats
       |
       v
status = CONFIRMED
```

## 9. TTL / Lock Expiry

```text
User 1
  |
  | select S1
  v
Lock S1
  |
  +---- pays before TTL ----> CONFIRMED
  |
  +---- TTL expires
              |
              v
        lock becomes invalid
              |
              v
        User 2 can lock S1
```

If User 1 tries to pay after expiry, `confirmBooking()` checks the lock and rejects the payment because the lock is expired or no longer owned by User 1.

## 10. Lock Sweeper

The in-memory implementation also uses a scheduled executor:

```text
ScheduledExecutorService
          |
          | periodically
          v
scan ConcurrentHashMap
          |
          v
remove expired locks
```

The sweeper is cleanup. `confirmBooking()` still validates expiry/ownership before accepting payment.

## 11. Horizontal Scaling Problem

In-memory locks work only when the relevant state is shared.

```text
             Load Balancer
              /                      v           v
        Server 1      Server 2
        lock S1       lock S1
        locally       locally
```

User 1 can lock `S1` on Server 1.

User 2 may reach Server 2 and not see that lock.

Therefore both servers can incorrectly believe that `S1` is available.

## 12. Distributed Lock Solution

Use a shared store such as Redis:

```text
             Load Balancer
              /                      v           v
        Server 1      Server 2
             \           /
              \         /
                 Redis
                   |
                 S1 lock
```

Now both servers check the same lock state.

The `LockProvider` abstraction allows:

```text
LockProvider
     |
     +--> InMemoryLockProvider
     |
     +--> RedisLockProvider
```

without changing the core `BookingService` business logic.

## 13. Design Patterns

### Strategy Pattern

```text
PaymentStrategy
      |
      +--> UPI
      +--> CARD
```

Used because payment methods have different implementations and more can be added later.

### Factory Pattern

```text
PaymentType
     |
     v
PaymentStrategyFactory
     |
     v
PaymentStrategy
```

Used to centralize payment-strategy selection.

### Repository Pattern

```text
Service
   |
   v
Repository
   |
   v
In-memory DB / actual DB
```

Used to separate business logic from persistence.

### Lock Provider Abstraction

```text
LockProvider
   |
   +--> InMemoryLockProvider
   +--> RedisLockProvider
```

Used to separate booking logic from the locking mechanism and allow distributed locking later.

## 14. Final Class List

```text
Models
├── Theater
├── Screen
├── Seat <<abstract>>
│   ├── RegularSeat
│   └── ReclinerSeat
├── Movie
├── Show
└── Booking

Enums
├── BookingStatus
├── PaymentType
├── SeatStatus
└── SeatType

Services
├── TheaterService
├── MovieService
├── ShowService
└── BookingService

Repositories
├── TheaterRepository
├── MovieRepository
├── ShowRepository
└── BookingRepository

Payment
├── PaymentStrategy <<interface>>
├── UPIPaymentStrategy
├── CardPaymentStrategy
└── PaymentStrategyFactory

Locking
├── LockProvider <<interface>>
├── InMemoryLockProvider
├── RedisLockProvider
└── LockExpiry
```

## 15. Most Important Part for the LLD Interview

Focus especially on:

```text
BookingService
      |
      +---- LockProvider
      |
      +---- BookingRepository
      |
      +---- PaymentStrategyFactory
```

The critical concurrency requirement is:

```text
Same show + same seat
        +
Multiple users
        ↓
Only ONE user gets the lock
        ↓
Lock has TTL
        ↓
Expired lock becomes available again
```
