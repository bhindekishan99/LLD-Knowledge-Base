# CopyOnWriteArraySet

`CopyOnWriteArraySet` is a **thread-safe Set** from `java.util.concurrent`.

## Key Idea

- **Read:** Uses the existing array.
- **Write:** Creates a new copy of the array and applies the change.
- Does **not allow duplicate elements**.

```text
Read  → existing array
Write → copy array → modify → replace
```

## When to Use

Best when:

- Many **reads**
- Very few **writes**
- Duplicates are not allowed
- Multiple threads access the Set

## Example

```java
CopyOnWriteArraySet<String> set =
        new CopyOnWriteArraySet<>();

set.add("A");
set.add("B");
set.add("A");  // ignored

System.out.println(set); // [A, B]
```

## Important Points

- Thread-safe.
- Supports common `Set` methods like `add()`, `remove()`, `contains()`, `size()`, etc.
- Iterator works on a **snapshot**.
- Writes are expensive because the array is copied.
- Not suitable for frequent writes or very large Sets.

## Remember

```text
Many Reads + Few Writes + No Duplicates
                ↓
      CopyOnWriteArraySet
```