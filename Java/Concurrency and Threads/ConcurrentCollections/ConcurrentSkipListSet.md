# ConcurrentSkipListSet

`ConcurrentSkipListSet` is a **thread-safe, sorted Set** from `java.util.concurrent`.

## Key Idea

- Stores **unique elements**.
- Elements are automatically kept in **sorted order**.
- Uses a **Skip List** internally.
- Supports concurrent access from multiple threads.
- Expected `O(log n)` for `add`, `remove`, and `contains`.

## Example

```java
ConcurrentSkipListSet<Integer> set =
        new ConcurrentSkipListSet<>();

set.add(30);
set.add(10);
set.add(20);
set.add(10); // Duplicate ignored

System.out.println(set);
// [10, 20, 30]
```

## Important Points

- Thread-safe.
- Sorted automatically.
- No duplicate elements.
- Does not allow `null`.
- Good for **concurrent + sorted + unique** data.
- Supports methods like `first()`, `last()`, `higher()`, and `lower()`.

## Remember

```text
Concurrent + Sorted + Unique
            ↓
ConcurrentSkipListSet
```