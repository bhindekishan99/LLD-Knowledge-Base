# Concurrency Essentials for LLD

> Goal: Learn only the concurrency concepts required to identify and handle common concurrency problems during LLD interviews.

---

# 1. Race Condition

A **race condition** happens when multiple threads access and modify shared data concurrently, and the final result depends on the order in which the threads execute.

### LLD Takeaway

Whenever multiple requests can modify the same shared resource, ask:

> "What happens if two threads execute this operation at the same time?"

Common examples:

```text
Inventory quantity
Available seats
Parking spots
Account balance
Vending machine stock
```

---

# 2. Critical Section

A **critical section** is the portion of code that accesses shared mutable data and must be protected from conflicting concurrent execution.

Example:

```java
if (warehouse.hasStock(sku, quantity)) {
    warehouse.removeStock(sku, quantity);
}
```

The entire operation is critical.

It would be incorrect to protect only:

```java
warehouse.removeStock(...);
```

because two threads could still simultaneously pass:

```java
warehouse.hasStock(...);
```

Think:

```text
Check
  ↓
Decision
  ↓
Update

All together = Critical Section
```

---

# 3. synchronized

### `synchronized(object)`

```java
synchronized (warehouse) {
    // critical section
}
```

`warehouse` is the **lock object**.

It means:

> Acquire the lock of this particular `warehouse` object → execute the block → release the lock.

If two threads synchronize on the **same object**:

```text
Thread 1 → Warehouse A 🔒 → Executes
Thread 2 → Warehouse A    → Waits
```

If they synchronize on **different objects**:

```text
Thread 1 → Warehouse A 🔒
Thread 2 → Warehouse B 🔒

Both can execute simultaneously.
```

### Key Point

```java
synchronized (X) { ... }
```

means:

> Only one thread at a time can execute code synchronized on the **same `X` object**.
Java's `synchronized` allows only one thread at a time to execute a critical section protected by the **same lock object**.

Example:

```java
synchronized (warehouse) {

    if (warehouse.hasStock(sku, quantity)) {
        warehouse.removeStock(sku, quantity);
    }
}
```

If two threads operate on the same warehouse:

```text
Thread 1
    ↓
Lock Warehouse A
    ↓
Check + Update
    ↓
Unlock Warehouse A

Thread 2
    ↓
Waits until lock becomes available
```

But another thread operating on a different warehouse can continue:

```text
Thread 1 → Warehouse A 🔒

Thread 2 → Warehouse B 🔒
```

### LLD Takeaway

Prefer locking the **specific resource being modified** instead of unnecessarily locking the entire system.

For example:

```java
synchronized (warehouse)
```

is generally better than:

```java
synchronized (inventory)
```

when operations on different warehouses can safely happen independently.

---

# 4. Atomicity and Check-Then-Act

An operation is **atomic** when other threads cannot observe or interfere with it halfway through.

A very common LLD concurrency problem is:

## Check-Then-Act

```java
if (availableSeats >= requestedSeats) {
    availableSeats -= requestedSeats;
}
```

This contains two logical steps:

```text
CHECK
Is stock available?

↓

ACT
Reduce stock
```

These must behave as **one operation**.

Otherwise:

```text
Thread 1 → Check ✓

Thread 2 → Check ✓

Thread 1 → Update

Thread 2 → Update
```

can produce invalid state.

Solution:

```java
synchronized (resource) {

    if (resource.isAvailable()) {
        resource.reserve();
    }
}
```

### LLD Rule

Whenever you see:

```text
Read
↓
Make decision
↓
Update shared state
```

think:

> **This probably needs to be atomic.**

---

# 5. Deadlock

A **deadlock** happens when threads wait forever for locks held by each other.

Consider warehouse transfers.

Thread 1:

```text
Transfer A → B
```

Thread 2:

```text
Transfer B → A
```

Suppose:

```text
Thread 1 locks A
Thread 2 locks B

Thread 1 waits for B
Thread 2 waits for A
```

Now:

```text
Thread 1
A 🔒 → waiting for B

Thread 2
B 🔒 → waiting for A
```

Neither can continue.

This is a **deadlock**.

## Simple Solution: Consistent Lock Ordering

Always acquire locks in the same deterministic order.

For example:

```text
Always lock smaller warehouseId first.
```

Therefore both:

```text
Transfer A → B
```

and:

```text
Transfer B → A
```

lock:

```text
A
↓
B
```

Example:

```java
Warehouse first;
Warehouse second;

if (source.getId().compareTo(target.getId()) < 0) {
    first = source;
    second = target;
} else {
    first = target;
    second = source;
}

synchronized (first) {
    synchronized (second) {

        // Transfer stock

    }
}
```

### LLD Takeaway

When one operation needs locks on **multiple resources**, think:

> "Could different threads acquire these resources in different orders?"

If yes, consider **consistent lock ordering**.

---

# 6. ConcurrentHashMap

Normal:

```java
HashMap
```

is not designed for concurrent modifications.

Java provides:

```java
ConcurrentHashMap
```

for thread-safe concurrent access to a map.

Example:

```java
Map<String, Warehouse> warehouses =
        new ConcurrentHashMap<>();
```

It also provides atomic operations such as:

```java
putIfAbsent(...)
compute(...)
computeIfAbsent(...)
```

Example:

```java
stock.compute(sku, (key, currentQuantity) -> {
    int current = currentQuantity == null ? 0 : currentQuantity;
    return current + quantity;
});
```

The `compute()` operation for that key is performed atomically.

## Important: ConcurrentHashMap Does NOT Make Your Business Flow Atomic

Suppose:

```java
if (stock.get(sku) >= requestedQuantity) {
    stock.put(sku, stock.get(sku) - requestedQuantity);
}
```

Even if `stock` is a:

```java
ConcurrentHashMap
```

the complete operation is still:

```text
get()
 ↓
check
 ↓
get()
 ↓
put()
```

Each individual map operation may be thread-safe, but the **whole business operation is not automatically atomic**.

This is an important interview distinction.

---

# Quick LLD Checklist

When concurrency comes up in an LLD interview, ask:

```text
1. Is there shared mutable state?
             ↓
2. Can multiple requests modify it simultaneously?
             ↓
3. Is there a Read → Check → Update operation?
             ↓
4. What is the smallest resource I should lock?
             ↓
5. Does the operation require multiple locks?
             ↓
6. If yes, can consistent lock ordering prevent deadlock?
```

---

# Common LLD Examples

| Problem | Shared Resource | Concurrency Concern |
|---|---|---|
| Inventory | Product quantity | Two users purchasing the same stock |
| BookMyShow | Seat | Two users booking the same seat |
| Parking Lot | Parking spot | Two cars getting the same spot |
| Vending Machine | Product quantity | Multiple purchases |
| Banking | Account balance | Concurrent withdrawal |
| Warehouse Transfer | Two warehouses | Atomicity + deadlock |

---

# What NOT to Study Right Now

For normal LLD preparation, don't spend significant time on:

```text
volatile
wait() / notify()
Semaphore
CountDownLatch
CyclicBarrier
ForkJoinPool
CAS internals
Java Memory Model internals
Lock-free algorithms
ThreadPool internals
Advanced CompletableFuture
```

Learn them later only when required.

---

# One-Line Revision

**Race Condition**
→ Multiple threads interfere with shared state.

**Critical Section**
→ Code accessing shared state that needs protection.

**synchronized**
→ Allows one thread at a time for the same lock.

**Atomicity**
→ Operation happens as one indivisible unit.

**Check-Then-Act**
→ Check + update must usually be atomic.

**Deadlock**
→ Threads wait forever for each other's locks.

**Consistent Lock Ordering**
→ Acquire multiple locks in the same order.

**ConcurrentHashMap**
→ Thread-safe map operations, but does not automatically make multi-step business logic atomic.
