# Concurrency Essentials — Quick Revision

## Race Condition

Multiple threads access/modify shared data and produce an incorrect result.

```text
Thread 1 ──┐
           ├──→ Shared State
Thread 2 ──┘
```

**Ask:** "What if two threads execute this at the same time?"

## Critical Section

Code that accesses shared mutable data and must be protected.

```java
synchronized (warehouse) {
    if (warehouse.hasStock(sku)) {
        warehouse.removeStock(sku);
    }
}
```

**Rule:** Keep **Check → Decision → Update** together.

## `synchronized`

```java
synchronized (resource) {
    // critical section
}
```

- Locks the specific `resource` object.
- Same lock → threads wait.
- Different locks → threads can run concurrently.
- Prefer locking the smallest required resource.

## Atomicity / Check-Then-Act

```java
if (availableSeats > 0) {
    availableSeats--;
}
```

This is:

```text
CHECK → UPDATE
```

Both should happen atomically.

**Rule:** `Read → Check → Update` usually needs atomicity.

## Deadlock

Two threads wait forever for each other's locks.

```text
Thread 1: Lock A → waits for B
Thread 2: Lock B → waits for A
```

### Solution: Consistent Lock Ordering

Always acquire multiple locks in the same order.

```text
Always: A → B

Thread 1: A → B
Thread 2: A → B
```

## Quick LLD Checklist

```text
1. Shared mutable state?
2. Multiple threads modifying it?
3. Read → Check → Update?
4. What is the smallest resource to lock?
5. Multiple locks required?
6. If yes, use consistent lock ordering.
```

## Quick Revision

| Concept | Remember |
|---|---|
| Race Condition | Threads interfere with shared state |
| Critical Section | Code accessing shared state |
| `synchronized` | One thread per same lock |
| Atomicity | Operation acts as one unit |
| Check-Then-Act | Check + update must be atomic |
| Deadlock | Threads wait forever for each other |
| Lock Ordering | Acquire multiple locks in same order |
| `ConcurrentHashMap` | Thread-safe map, but multi-step logic may still need protection |