# BookMyShow — LLD Quick Revision

## 1. Class Diagram

```text
┌─────────────────────────────┐
│ <<abstract>> Seat           │
├─────────────────────────────┤
│ id                          │
│ price                       │
├─────────────────────────────┤
│ getId()                     │
│ getPrice()                  │
│ getType()                   │
└──────────────┬──────────────┘
               │
        ┌──────┴──────┐
        ▼             ▼
┌────────────────┐ ┌────────────────┐
│ RegularSeat    │ │ ReclinerSeat   │
├────────────────┤ ├────────────────┤
│                │ │                │
└────────────────┘ └────────────────┘

┌─────────────────────────────┐
│ Theater                     │
├─────────────────────────────┤
│ id                          │
│ name                        │
│ screens                     │
│                             │
│ Map<String, Screen>         │
│ key   = screenId            │
│ value = Screen              │
├─────────────────────────────┤
│ addScreen()                 │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│ Screen                      │
├─────────────────────────────┤
│ id                          │
│ seats                       │
│                             │
│ Map<String, Seat>           │
│ key   = seatId              │
│ value = Seat                │
├─────────────────────────────┤
│ addSeat()                   │
│ getSeat()                   │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│ Seat                        │
├─────────────────────────────┤
│ id                          │
│ price                       │
├─────────────────────────────┤
│ getId()                     │
│ getPrice()                  │
│ getType()                   │
└─────────────────────────────┘


┌─────────────────────────────┐
│ Movie                       │
├─────────────────────────────┤
│ id                          │
│ title                       │
│ duration                    │
└──────────────┬──────────────┘
               │
               ▼
┌────────────────────────────────────┐
│ Show                               │
├────────────────────────────────────┤
│ id                                 │
│ movie                              │
│ theater                            │
│ screen                             │
│ startTime                          │
│ endTime                            │
├────────────────────────────────────┤
│ getSeat()                          │
└────────────────┬───────────────────┘
                 │
                 ▼
┌─────────────────────────────┐
│ Booking                     │
├─────────────────────────────┤
│ bookingId                   │
│ userId                      │
│ showId                      │
│ seatIds                     │
│ amount                      │
│ paymentType                 │
│ status                      │
└─────────────────────────────┘


┌────────────────────────────────────┐
│ BookingService                     │
├────────────────────────────────────┤
│ lockProvider                       │
│ bookingRepository                  │
│ paymentFactory                     │
│ bookingCounter                    │
├────────────────────────────────────┤
│ createBooking()                    │
│ confirmBooking()                   │
│ releaseLocks()                     │
│ createLockKey()                    │
└───────────┬──────────┬─────────────┘
            │          │
            ▼          ▼
┌────────────────┐  ┌──────────────────────────┐
│ LockProvider   │  │ BookingRepository        │
├────────────────┤  ├──────────────────────────┤
│ tryLock()      │  │ save()                   │
│ unlock()       │  │ get()                    │
│ isLockedBy()   │  │ getConfirmedSeatIds()    │
│ getLockedSeatIds│ │ markSeatsConfirmed()     │
└───────┬────────┘  └──────────────────────────┘
        │
        ▼
┌─────────────────────────────┐
│ InMemoryLockProvider        │
├─────────────────────────────┤
│ locks                       │
│                             │
│ Map<String, LockExpiry>     │
│ key   = showId:seatId       │
│ value = LockExpiry          │
├─────────────────────────────┤
│ tryLock()                   │
│ unlock()                    │
│ isLockedBy()                │
│ getLockedSeatIds()          │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│ LockExpiry                  │
├─────────────────────────────┤
│ deadline                    │
│ ownerUserId                 │
└─────────────────────────────┘


┌────────────────────────────────┐
│ PaymentStrategy                │
├────────────────────────────────┤
│ pay(Booking)                   │
└───────────────┬────────────────┘
                │
        ┌───────┴────────┐
        ▼                ▼
┌────────────────┐ ┌────────────────┐
│ UpiPayment     │ │ CardPayment    │
│ Strategy       │ │ Strategy       │
├────────────────┤ ├────────────────┤
│ pay()          │ │ pay()          │
└────────────────┘ └────────────────┘


┌────────────────────────────────────┐
│ SeatAvailabilityService            │
├────────────────────────────────────┤
│ bookingRepository                  │
│ lockProvider                       │
├────────────────────────────────────┤
│ getAvailableSeats()                │
└────────────────────────────────────┘
```

---

## 2. Important Maps — Key → Value

### `Theater`

```text
Map<String, Screen>

screenId → Screen
```

### `Screen`

```text
Map<String, Seat>

seatId → Seat
```

### `TheaterRepository`

```text
Map<String, Theater>

theaterId → Theater
```

### `MovieRepository`

```text
Map<String, Movie>

movieId → Movie
```

### `ShowRepository`

```text
Map<String, Show>

showId → Show
```

### `BookingRepository`

```text
Map<String, Booking>

bookingId → Booking
```

Confirmed seats:

```text
Map<String, Set<String>>

showId → Set<seatId>
```

Example:

```text
SHOW1 → {S1, S2, S5}
SHOW2 → {S1, S3}
```

### `InMemoryLockProvider`

```text
Map<String, LockExpiry>

showId:seatId → LockExpiry
```

Example:

```text
SHOW1:S1 → LockExpiry
              ├── ownerUserId = USER1
              └── deadline = ...
```

---

## 3. `createBooking()`

```text
validate seats
      ↓
reject duplicate seats
      ↓
check confirmed seats
      ↓
for each seat
      ↓
validate seat does not exists in confirmed seats
      ↓
tryLock(showId:seatId)
      ↓
if any lock fails
    → release acquired locks
    → fail
      ↓
all seats locked
      ↓
calculate amount
      ↓
create Booking(CREATED)
      ↓
save booking
```

All-or-nothing:

```text
S1 → locked
S2 → lock fails
     ↓
release S1
     ↓
booking fails
```

---

## 4. `confirmBooking()`

```text
get booking
      ↓
already CONFIRMED?
      → true
      ↓
must be CREATED
      ↓
verify user owns every lock -> LockExpiry.owenrUserId == CurrentUserID and LockExpiry.TTL <= currentTime() 
      ↓
PaymentFectory gives -> PaymentStrategy
      ↓
      pay()
   ↙      ↘
fail     success
 ↓          ↓
FAILED   Add seats in BookingRepository.ConfirmedSeats
 ↓          ↓
release   CONFIRMED
locks        ↓
          release locks
```

Success order:

```text
mark seats confirmed
        ↓
status = CONFIRMED
        ↓
release temporary locks
```

---

## 5. Seat Availability

`SeatAvailabilityService`: 

```text
All seats
    -
Confirmed seats
    -
Temporarily locked seats
    =
Available seats
```

`SeatAvailabilityService` gets:

```text
all seats       → Screen
confirmed seats → BookingRepository
locked seats    → LockProvider
```

---

## 6. Seat Lock used in InMemoryLockProvider

```text
lock key = showId + ":" + seatId

Example:
SHOW1:S1
```

```text
Available
    ↓
Temporarily Locked
    ↓
Payment
   ↙  ↘
Fail  Success
 ↓       ↓
Free   Confirmed
```

Temporary lock has a TTL of 2 minutes.

---

## 7. Concurrency / Thread Safety

### Where concurrency is handled

| Place | What is shared? | Key | Value | Solution |
|---|---|---|---|---|
| `TheaterRepository` | `theaters` map | `theaterId` | `Theater` | `ConcurrentHashMap` |
| `MovieRepository` | `movies` map | `movieId` | `Movie` | `ConcurrentHashMap` |
| `ShowRepository` | `shows` map | `showId` | `Show` | `ConcurrentHashMap` |
| `BookingRepository` | `bookings` map | `bookingId` | `Booking` | `ConcurrentHashMap` |
| `BookingRepository` | confirmed seats | `showId` | `Set<seatId>` | `ConcurrentHashMap.newKeySet()` |
| `InMemoryLockProvider` | `locks` map | `showId:seatId` | `LockExpiry` | `ConcurrentHashMap` |
| `BookingService` | booking ID counter | — | — | `AtomicInteger` |

### Most important: Seat Lock

For a seat:

```text
lockKey = showId + ":" + seatId

Example:
SHOW1:S1
```

Two users may call:

```text
tryLock("SHOW1:S1")
```

concurrently.

`tryLock()` must do these two steps as **one atomic operation**:

```text
1. Check whether SHOW1:S1 is free/expired
2. If free → assign it to the user
```

In the code, this is achieved using:

```java
locks.compute(key, ...)
```

So only one user can successfully acquire the same `showId + seatId` lock.

### Why `ConcurrentHashMap` for confirmed seats?

```text
showId → Set<seatId>
```

Example:

```text
SHOW1 → {S1, S2, S5}
```

The set is:

```java
ConcurrentHashMap.newKeySet()
```

because multiple booking confirmations may update confirmed seats concurrently.

### Why `AtomicInteger`?

```java
bookingCounter.getAndIncrement()
```

generates booking IDs safely when multiple users create bookings concurrently.

## 8. Design Patterns

```text
Strategy
→ PaymentStrategy

Factory
→ PaymentStrategyFactory

Repository
→ TheaterRepository
→ MovieRepository
→ ShowRepository
→ BookingRepository

Provider abstraction
→ LockProvider
```

---

## 9. 30-Second Interview Explanation

> Theater contains screens and screens contain seats. A Show represents a movie playing on a particular screen at a particular time. When a booking is created, all requested seats are temporarily locked with a TTL. If any seat cannot be locked, previously acquired locks are released and the booking fails. After successful payment, the seats are marked as confirmed and temporary locks are released. Payment uses Strategy + Factory, while repositories and LockProvider abstract data access and locking.
