# ConcurrentLinkedDeque

`ConcurrentLinkedDeque` is a **thread-safe, non-blocking deque** from `java.util.concurrent`.

It is implemented using a **linked list of nodes**, where each node contains a value and references to other nodes.

```text
[A] ↔ [B] ↔ [C] ↔ [D]
 ↑                   ↑
Head                Tail
```

## Key Features

- **Thread-safe** — multiple threads can access it safely.
- **Non-blocking** — Non-blocking means a thread doesn't wait for another thread to release a lock;
if its CAS fails, it can retry using the updated state.
- Uses **CAS (Compare-And-Swap)** to atomically update node references.
- Supports insertion and removal from **both ends** of the deque.
- Does not allow `null` elements.

## CAS

`ConcurrentLinkedDeque` uses CAS to safely modify its linked nodes.

```text
CAS(variable, expectedValue, newValue)
```

It means:

> Change the value only if it is still equal to the expected value.

If CAS fails because another thread changed the value, the operation can retry using the updated state.

## Example

```java
ConcurrentLinkedDeque<Integer> deque = new ConcurrentLinkedDeque<>();

deque.addFirst(10);
deque.addLast(20);

System.out.println(deque.pollFirst()); // 10
System.out.println(deque.pollLast());  // 20
```

## Key Point

> **`ConcurrentLinkedDeque` = thread-safe linked deque + non-blocking operations + CAS.**

## How This Applies to `ConcurrentLinkedDeque`

Remember the linked structure:

```text
[A] → [B] → [C]
```

Suppose a thread wants to add `D`:

```text
[A] → [B] → [C] → [D]
```

It needs to change a pointer.

Conceptually, it can do:

```text
CAS(C.next, null, D)
```

Meaning:

> "If `C.next` is still `null`, make it point to `D`."

If another thread got there first:

```text
C.next → X
```

then:

```text
CAS(C.next, null, D)
```

fails because the expected value (`null`) is no longer the actual value.

The thread does **not acquire a lock** around the entire deque. It observes the updated state and **retries** the operation.

```text
CAS
 ↓
Success? ── Yes → Done ✅
   │
   No
   ↓
Read updated state
   ↓
Retry 🔄
```

This is why `ConcurrentLinkedDeque` can provide **non-blocking concurrent access**.
