# BlockingQueue

`BlockingQueue` is a **thread-safe queue** that can make threads **wait** when the queue is empty or full.

## Key Idea

- `put()` → waits if the queue is full.
- `take()` → waits if the queue is empty.
- Commonly used for **Producer-Consumer** problems.

```text
Producer
   ↓ put()
BlockingQueue
   ↓ take()
Consumer
```

## Example

```java
BlockingQueue<String> queue =
        new ArrayBlockingQueue<>(10);

queue.put("A");

String item = queue.take();
```

If the queue is empty:

```text
take()
  ↓
Thread waits
  ↓
Producer puts item
  ↓
Thread continues
```

If the queue is full:

```text
put()
  ↓
Thread waits
  ↓
Consumer removes item
  ↓
Thread continues
```

## Important Points

- Thread-safe.
- Supports **blocking** operations.
- Can be **bounded** using a capacity.
- Common implementations:
  - `ArrayBlockingQueue`
  - `LinkedBlockingQueue`
- Useful for **Producer-Consumer** and background task processing.

## Remember

```text
Producer → put() → BlockingQueue → take() → Consumer
```

**BlockingQueue = Thread-safe queue + waiting when necessary**