# ConcurrentSkipListMap

`ConcurrentSkipListMap` is a **thread-safe, sorted map** from `java.util.concurrent`.

## Key Idea

- Stores key-value pairs in **sorted order**.
- Supports concurrent access from multiple threads.
- Based on a **Skip List** data structure.
- Provides expected **O(log n)** time for `get`, `put`, and `remove`.

```text
Keys:
10 → 20 → 30 → 40 → 50
```

## Example

```java
ConcurrentSkipListMap<Integer, String> map =
        new ConcurrentSkipListMap<>();

map.put(30, "C");
map.put(10, "A");
map.put(20, "B");

System.out.println(map);
// {10=A, 20=B, 30=C}
```

## Important Points

- Thread-safe.
- Keys are automatically **sorted**.
- Does **not allow `null` keys or values**.
- Useful when you need a **sorted map + concurrent access**.
- Supports methods like `firstKey()`, `lastKey()`, `higherKey()`, `lowerKey()`.

## Remember

```text
Concurrent + Sorted Map
          ↓
ConcurrentSkipListMap
```

## ConcurrentSkipListMap vs ConcurrentHashMap

| Feature | `ConcurrentHashMap` | `ConcurrentSkipListMap` |
|---|---|---|
| Thread-safe | Yes | Yes |
| Ordering | No guaranteed sorted order | **Sorted by key** |
| Data structure | Hash table | Skip List |
| `get/put/remove` | Expected O(1) | Expected O(log n) |
| `null` keys/values | Not allowed | Not allowed |
| Best when | Fast concurrent map access | Concurrent access **+ sorted keys** |

### Example

```java
ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
// Fast lookup, no sorted order

ConcurrentSkipListMap<Integer, String> sortedMap =
        new ConcurrentSkipListMap<>();
// Keys automatically sorted
```

### Remember

```text
Need fast concurrent Map
        ↓
ConcurrentHashMap

Need concurrent + sorted Map
        ↓
ConcurrentSkipListMap
```