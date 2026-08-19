# Concurrency — Short Interview Notes

## 1. Correctness

**Problem:** Multiple threads access shared state and operations can interleave incorrectly.

### Check-Then-Act

```java
if (seat.isAvailable()) {
    seat.book();
}
```

Two threads can both see the seat as available and both book it.

**Solution:** Use a lock so check + action happen atomically.

```java
synchronized (lock) {
    if (seat.isAvailable()) {
        seat.book();
    }
}
```

### Read-Modify-Write

```java
count++;
```

Actually means:

```text
READ → MODIFY → WRITE
```

Two threads can read the same value and lose an update.

**Solution:**

- Single variable → `AtomicInteger`
- Multiple related variables → `Lock`

---

## 2. Coordination

**Problem:** One thread produces work and another thread processes it.

Example:

```text
Producer → Queue → Consumer
```

### BlockingQueue

Use `BlockingQueue` when consumers should wait for work instead of continuously checking.

```java
BlockingQueue<Task> queue =
        new ArrayBlockingQueue<>(100);
```

Producer:

```java
queue.put(task);
```

Consumer:

```java
Task task = queue.take();
```

- `take()` → waits if queue is empty
- `put()` → adds work and can wake a waiting consumer

### Bounded Queue

A bounded queue prevents unlimited memory growth.

```text
Producer faster than Consumer
        ↓
Queue becomes full
        ↓
Producer waits
        ↓
Backpressure
```

**Interview clue:** Producer/consumer, background jobs, task processing → think `BlockingQueue`.

---

## 3. Scarcity

**Problem:** A resource is limited but many threads want to use it.

Example:

```text
API allows 10 concurrent requests
Application has 50 threads
```

### Semaphore

Use a `Semaphore` to limit concurrent access.

```java
Semaphore semaphore = new Semaphore(5);

semaphore.acquire();

try {
    downloadFile();
} finally {
    semaphore.release();
}
```

Think:

```text
acquire → use resource → release
```

Always release in `finally` to avoid a **permit leak**.

### Object Pool

Use an object pool when you have a fixed number of reusable resources.

Example:

```text
Connection Pool
C1
C2
C3
...
C10
```

A `BlockingQueue` can manage available connections:

```java
Connection connection = pool.take();

try {
    connection.executeQuery();
} finally {
    pool.put(connection);
}
```

### Semaphore vs Object Pool

```text
Semaphore
→ How many threads can use the resource?

Object Pool
→ Which actual reusable resource can I borrow?
```

---

## 4. Interview Cheat Sheet

| Problem | Solution |
|---|---|
| Check-Then-Act | Lock |
| Multiple related variables | Lock |
| Single variable update | Atomic |
| Producer / Consumer | `BlockingQueue` |
| Consumer has no work | `take()` |
| Producer faster than consumer | Bounded `BlockingQueue` |
| Limit concurrent API calls | `Semaphore` |
| Reuse DB connections | Object Pool |
| Return resource after failure | `finally` |

## Final Mental Model

```text
Concurrency
   |
   +── Correctness
   |      ├── Lock
   |      └── Atomic
   |
   +── Coordination
   |      └── BlockingQueue
   |             └── Bounded Queue → Backpressure
   |
   +── Scarcity
          ├── Semaphore
          └── Object Pool
```

## Remember

```text
Correctness
→ Protect shared state

Coordination
→ Move work between threads

Scarcity
→ Control limited resources
```
