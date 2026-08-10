# Concurrency — LLD Interview Notes

> The important skill is not memorizing primitives. First identify **what problem concurrency is causing**, then choose the solution.

---

# 1. Correctness

## What is the problem?

Multiple threads access the **same shared state**, and their operations can interleave in a way that produces an incorrect result.

The video focuses on two common patterns:

- Check-Then-Act
- Read-Modify-Write

The common root cause is:

```text
Thread A starts an operation
        ↓
There is a gap between operations
        ↓
Thread B interferes during that gap
        ↓
Shared state becomes incorrect
```

## Check-Then-Act

### Problem

The code first checks a condition and then performs an action based on that condition.

```text
CHECK
  ↓
ACT
```

The problem is the gap between the two.

### Example — Ticket Booking

```java
if (seat.isAvailable()) {
    seat.book();
}
```

Two threads can execute this:

```text
Thread A                  Thread B

check → available
                          check → available

book seat
                          book seat
```

Both threads saw the seat as available.

### Other examples

```text
Parking:
check spot → assign car

Inventory:
check stock → process order

Rate limiter:
check limit → allow request
```

### Solution — Lock

The check and action must happen as **one atomic operation**.

```text
Thread A

acquire lock
     ↓
check
     ↓
act
     ↓
release lock

Thread B waits for the lock
```

Java example:

```java
synchronized (lock) {
    if (seat.isAvailable()) {
        seat.book();
    }
}
```

The important point is:

> The entire check + action must be inside the protected section.

---

## Read-Modify-Write

### Problem

An operation may look like one statement but actually consists of:

```text
READ → MODIFY → WRITE
```

Example:

```java
count++;
```

Conceptually:

```text
read count
    ↓
add 1
    ↓
write count
```

### Race condition

Suppose:

```text
count = 5
```

Two threads:

```text
Thread A                  Thread B

read 5                    read 5
calculate 6               calculate 6
write 6                   write 6
```

Final result:

```text
6
```

Expected:

```text
7
```

One update was lost.

### Common examples

- Counter
- Inventory quantity
- Bank balance
- Hit counter
- Metrics

### Solution — Atomic Variable

For a **single variable**, an atomic variable can make the update atomic.

Java:

```java
AtomicInteger count =
        new AtomicInteger(5);

count.incrementAndGet();
```

The video explains that atomic operations can use CPU-level mechanisms such as **Compare-And-Swap (CAS)**.

### Atomic vs Lock

Use this interview rule:

```text
Single variable
      ↓
Atomic variable


Multiple related variables
      ↓
Lock
```

Example:

```text
count++
```

→ Atomic variable is appropriate.

But:

```text
Account A -= 100
Account B += 100
```

Both changes must remain consistent.

→ Use a lock around the complete operation.

---

# 2. Coordination

## What is the problem?

Here the problem is **not that shared state becomes incorrect**.

The problem is:

> One thread produces work, and another thread needs to process that work.

The video uses a welcome-email example.

Sending an email takes about 500 ms.

We don't want the **API/producer thread** to wait for the email to finish.

Instead:

```text
API / Producer Thread
          |
          | create task
          ↓
        Queue
          |
          ↓
Email / Consumer Thread
          |
          ↓
      Send Email
```

The producer can finish the signup request while the consumer processes the email in the background.

---

## Problem: How does the consumer wait for work?

Once we introduce a queue, the **consumer thread** needs to wait when there is no task.

### Naive solution — Busy Waiting

```java
while (true) {
    if (!queue.isEmpty()) {
        Task task = queue.remove();
        process(task);
    }
}
```

The consumer repeatedly checks:

```text
Is there work?
Is there work?
Is there work?
...
```

### Issue

The consumer wastes CPU while there is no work.

---

## Another attempt — Sleep and Poll

We can make the **consumer thread** sleep:

```java
while (true) {

    Task task = queue.poll();

    if (task == null) {
        Thread.sleep(100);
    } else {
        process(task);
    }
}
```

This reduces CPU usage.

But now there is a latency problem.

Suppose:

```text
Consumer checks queue → empty
Consumer sleeps for 100 ms
Producer adds task immediately after the check
```

The consumer may not process the task until the sleep finishes.

So:

```text
Busy waiting
→ wastes CPU

Sleep + polling
→ less CPU waste, but adds latency
```

---

## Solution — Blocking Queue

A **BlockingQueue** solves this waiting problem.

Java example:

```java
BlockingQueue<Task> queue =
        new ArrayBlockingQueue<>(100);

```

### Producer

The **producer thread** adds work:

```java
queue.put(task);
```

### Consumer

The **consumer thread** takes work:

```java
Task task = queue.take();

process(task);
```

### What does `take()` mean?

`take()` means:

> Give me the next item from the queue. If the queue is empty, **wait until an item becomes available**.

So:

```text
Queue has task
     ↓
take() returns immediately


Queue is empty
     ↓
take() blocks the consumer thread
     ↓
consumer waits
```

This is why it is called a **Blocking** Queue.

The consumer is blocked while waiting for work instead of continuously consuming CPU.

---

## How does the consumer get notified?

When the **producer thread** executes:

```java
queue.put(task);
```

the `BlockingQueue` internally coordinates with waiting consumers.

Conceptually:

```text
Consumer
   |
   | take()
   ↓
Queue is empty
   |
   ↓
Consumer waits
```

Then:

```text
Producer
   |
   | put(task)
   ↓
BlockingQueue
   |
   | wakes a waiting consumer
   ↓
Consumer
   |
   | take() returns task
   ↓
process(task)
```

The important point from the video:

> When a producer puts work into an empty blocking queue, a waiting consumer is automatically woken so it can process the task.

The exact internal mechanism is handled by the queue implementation; the LLD interview-level concept is simply:

```text
put() → waiting consumer can wake up
take() → consumer waits if queue is empty
```

---

## Why not just use a normal Queue?

A normal queue does not give the consumer this waiting behavior automatically.

With a normal queue, you would have to implement something like:

```text
check queue
sleep
check again
sleep
...
```

A `BlockingQueue` provides the coordination mechanism for you.

Java provides implementations such as:

```java
ArrayBlockingQueue
LinkedBlockingQueue
```

For the interview, the important concept is:

```text
Producer → put()
BlockingQueue
Consumer → take()
```

---

## Problem: What if producers are faster than consumers?

Now consider:

```text
Producer: 1000 tasks/sec
Consumer: 100 tasks/sec
```

Tasks accumulate:

```text
Producer
   ↓
Queue
████████████████████████
   ↓
Consumer
```

If the queue is unlimited:

```text
Queue keeps growing
       ↓
Memory keeps growing
       ↓
Eventually process can crash
```

---

## Solution — Bounded Blocking Queue

Give the queue a maximum capacity.

```java
BlockingQueue<Task> queue =
        new ArrayBlockingQueue<>(100);
```

Now the queue can hold at most 100 tasks.

When it is full:

```text
Producer
   ↓
queue.put(task)
   ↓
Queue is full
   ↓
Producer waits
```

When a consumer removes a task:

```text
Consumer
   ↓
take()
   ↓
Space becomes available
   ↓
Waiting producer can continue
```

This naturally slows down the producer when consumers cannot keep up.

This is called:

> **Backpressure**

### Coordination summary

```text
Problem:
Producer and consumer need to exchange work
        ↓
BlockingQueue


Problem:
Consumer has no work
        ↓
take() blocks consumer
        ↓
Producer puts task
        ↓
Consumer wakes up


Problem:
Producer is faster than consumer
        ↓
Queue can grow indefinitely
        ↓
Bounded BlockingQueue
        ↓
Backpressure
```

### Interview recognition

If you see:

- Background workers
- Producer / consumer
- Async processing
- Task scheduler
- Background job processor
- Message processing

Think:

> **BlockingQueue**

And usually consider a **bounded** queue.

---

# 3. Scarcity

## What is the problem?

A resource is **finite**, but many threads want to use it.

Example from the video:

```text
External API
Maximum 10 concurrent requests

Application
50 threads
```

Without protection, all 50 threads may try to use the API.

We need:

```text
Maximum 10 threads
        ↓
using the resource at once
```

This is a **scarcity problem**.

---

## Solution — Semaphore

A semaphore can be viewed as a bucket of permits.

For 5 concurrent downloads:

```text
● ● ● ● ●
5 permits
```

A thread must:

```text
acquire permit
      ↓
use resource
      ↓
release permit
```

If no permit is available:

```text
acquire()
    ↓
thread waits
```

### Java example

```java
Semaphore semaphore =
        new Semaphore(5);

semaphore.acquire();

try {
    downloadFile();
} finally {
    semaphore.release();
}
```

This guarantees that at most 5 threads are downloading at the same time.

---

## Important problem — Permit Leak

Consider:

```java
semaphore.acquire();

downloadFile();

semaphore.release();
```

What if `downloadFile()` throws an exception?

```text
acquire()
   ↓
downloadFile()
   ↓
exception
   ↓
release() never executes
```

The permit is lost.

If this happens repeatedly:

```text
5 permits
   ↓
5 failures
   ↓
0 permits
   ↓
future threads wait forever
```

### Solution

Always release the permit in `finally`:

```java
semaphore.acquire();

try {
    downloadFile();
} finally {
    semaphore.release();
}
```

Interview rule:

> **Acquire → Use → Release, even when the operation fails.**

---

## Object / Connection Pool

Sometimes scarcity is about a **fixed set of actual stateful objects**, not just a number of permits.

Example:

```text
Database Connections
```

A database connection:

- Uses memory
- Holds an open TCP connection
- Maintains connection state
- Is expensive to repeatedly create and destroy

Instead of creating a new connection for every query:

```text
Connection Pool

C1
C2
C3
...
C10
```

Threads reuse these connections.

### How does the pool work?

A `BlockingQueue` can store the available connections.

```java
BlockingQueue<Connection> pool =
        new ArrayBlockingQueue<>(10);
```

Take a connection:

```java
Connection connection =
        pool.take();
```

Use it:

```java
try {
    connection.executeQuery();
} finally {
    pool.put(connection);
}
```

If no connection is available:

```text
pool.take()
    ↓
consumer/thread waits
```

When another thread returns a connection:

```text
pool.put(connection)
        ↓
waiting thread can continue
```

### Semaphore vs Object Pool

```text
Semaphore
→ "How many threads may use this resource?"


Object Pool
→ "Which actual reusable resource can I borrow?"
```

Example:

```text
API allows 10 concurrent calls
→ Semaphore(10)


10 reusable database connections
→ Connection Pool
```

---



# 4. Quick Interview Cheat Sheet

| Problem                        | Category     | Solution              |
| ------------------------------ | ------------ | --------------------- |
| Two users book the same seat   | Correctness  | Lock                  |
| Check stock then place order   | Correctness  | Lock                  |
| Two threads increment counter  | Correctness  | Atomic                |
| Update two related values      | Correctness  | Lock                  |
| API produces background tasks  | Coordination | BlockingQueue         |
| Consumer has no work           | Coordination | `take()` blocks     |
| Producer faster than consumer  | Coordination | Bounded BlockingQueue |
| Prevent unlimited queue growth | Coordination | Backpressure          |
| Only 10 API calls concurrently | Scarcity     | Semaphore             |
| Only 5 downloads concurrently  | Scarcity     | Semaphore             |
| Reuse 10 DB connections        | Scarcity     | Object Pool           |
| Return resource after failure  | Scarcity     | `finally`           |

---

# 5. Final Mental Model

```text
                    CONCURRENCY
                         |
          +--------------+--------------+
          |              |              |
          ▼              ▼              ▼
     CORRECTNESS    COORDINATION     SCARCITY
          |              |              |
     Shared state     Work flow     Limited resource
          |              |              |
      +---+---+        Queue       +----+----+
      |       |           |         |         |
      ▼       ▼           ▼         ▼         ▼
 Check-   Read-       Blocking   Semaphore  Object
 Then-    Modify-     Queue                  Pool
 Act      Write          |
      |       |          ▼
     Lock   Atomic   Bounded Queue
                     |
                     ▼
                 Backpressure
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
