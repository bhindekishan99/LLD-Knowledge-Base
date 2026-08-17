# Java Concurrent Collections

| Normal Collection | Concurrent Alternative | Use |
|---|---|---|
| `HashMap` | `ConcurrentHashMap` | Thread-safe key-value storage |
| `ArrayList` | `CopyOnWriteArrayList` | Many reads and very few writes |
| `HashSet` | `CopyOnWriteArraySet` | Many reads, very few writes, and unique elements |
| `LinkedList` / `ArrayDeque` | `ConcurrentLinkedQueue` | Non-blocking thread-safe queue |
| `Deque` | `ConcurrentLinkedDeque` | Non-blocking thread-safe deque |
| `Queue` | `BlockingQueue` | Producer-consumer problems |
| `TreeMap` | `ConcurrentSkipListMap` | Thread-safe sorted map |
| `TreeSet` | `ConcurrentSkipListSet` | Thread-safe sorted set |
| `Stack` | `?` | ? |