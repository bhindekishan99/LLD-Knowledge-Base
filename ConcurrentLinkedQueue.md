# ConcurrentLinkedQueue

`ConcurrentLinkedQueue` is a **thread-safe, non-blocking FIFO queue** from `java.util.concurrent`.

## Key Idea

- Uses a **linked-node structure**.
- Multiple threads can safely add/remove elements.
- Uses **CAS (Compare-And-Swap)** instead of traditional locks.

```text
[A] → [B] → [C] → [D]
 ↑                   ↑
Head                Tail
```

## When to Use

Best when:

- Multiple threads access the queue.
- You need a **FIFO** queue.
- You don't want threads to block on locks.

## Example

```java
ConcurrentLinkedQueue<String> queue =
        new ConcurrentLinkedQueue<>();

queue.offer("A");
queue.offer("B");

System.out.println(queue.poll()); // A
System.out.println(queue.poll()); // B
```

## Important Points

- Thread-safe.
- **Non-blocking**.
- FIFO ordering.
- Supports common queue methods like `offer()`, `poll()`, `peek()`, `size()`, etc.
- Does **not allow `null`** elements.
- Does not have a fixed capacity.

## Remember

```text
Thread-safe + Non-blocking + FIFO
              ↓
    ConcurrentLinkedQueue
```