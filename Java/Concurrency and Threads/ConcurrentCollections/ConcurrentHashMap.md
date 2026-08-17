# ConcurrentHashMap

`ConcurrentHashMap` is the most important concurrent collection. Use it when multiple threads need to read and update key-value data safely.

```java
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

Map<String, Integer> marks = new ConcurrentHashMap<>();
```

## Important Rules

- Allows multiple threads to read and update safely.
- Does not allow `null` keys or `null` values.
- Usually faster and more scalable than wrapping a `HashMap` with `Collections.synchronizedMap()`.
- Iteration is safe while other threads modify the map, but it is not a fixed snapshot.

```java
map.put(null, 10);      // NullPointerException
map.put("java", null);  // NullPointerException
```

## Normal Map Methods

```java
Map<String, Integer> map = new ConcurrentHashMap<>();

map.put("Aman", 90);

System.out.println(map.get("Aman"));             // 90
System.out.println(map.getOrDefault("Neha", 0)); // 0

System.out.println(map.containsKey("Riya")); // true

map.remove("Aman");
map.clear();

System.out.println(map.size());
System.out.println(map.isEmpty());
```

These individual operations are thread-safe.

## Compound Operations

This is not safe for a shared concurrent map:

```java
int count = map.getOrDefault("java", 0);
map.put("java", count + 1);
```

Use atomic methods instead.

## `putIfAbsent()`

Add a value only if the key is missing.

```java
map.putIfAbsent("java", 1);
```

```text
If "java" is absent → adds java=1
If "java" already exists → keeps its old value
```

## `replace()`

Replace only when conditions match.

```java
map.replace("java", 5);       // Replaces current value with 5 if key exists

map.replace("java", 5, 10);   // Changes 5 to 10 only if current value is 5
```

## `remove(key, value)`

Remove only if key and value both match.

```java
map.remove("java", 10);
```

```text
Removes "java" only when its current value is 10.
```

## `computeIfAbsent()`

Create a value only if the key does not exist.

```java
Map<String, List<String>> coursesByStudent =
        new ConcurrentHashMap<>();

coursesByStudent.computeIfAbsent(
    "Aman",
    key -> new ArrayList<>()
).add("Java");
```

Meaning:

```text
If Aman does not exist → create an empty list
Then add Java to Aman’s list
```

Important: `ConcurrentHashMap` protects the map, but the inner `ArrayList` is not automatically thread-safe if multiple threads modify the same list.

## `compute()`

Calculate and store a new value for a key atomically.

```java
map.compute("java", (key, oldValue) -> {
    if (oldValue == null) {
        return 1;
    }

    return oldValue + 1;
});
```

## `computeIfPresent()`

Update a value only if the key exists.

```java
map.computeIfPresent(
    "java",
    (key, oldValue) -> oldValue + 1
);
```

If `"java"` does not exist, nothing happens.

## `merge()`

Best method for frequency counting.

```java
map.merge("java", 1, Integer::sum);
```

Meaning:

```text
If "java" is absent → put java=1
If "java" exists → old value + 1
```

## Word-Frequency Example

```java
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentWordCount {
    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> wordCount = new ConcurrentHashMap<>();

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                wordCount.merge("java", 1, Integer::sum);
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10_000; i++) {
                wordCount.merge("java", 1, Integer::sum);
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        System.out.println(wordCount.get("java")); // 20000
    }
}
```

## Iteration

You can safely iterate while another thread modifies the map.

```java
for (Map.Entry<String, Integer> entry : map.entrySet()) {
    System.out.println(entry.getKey() + " -> " + entry.getValue());
}
```

```text
Safe iteration?                         Yes
Throws ConcurrentModificationException? No
Always sees every newest update?        No
```

## `HashMap` vs `ConcurrentHashMap`

| Feature | `HashMap` | `ConcurrentHashMap` |
|---|---|---|
| Thread-safe | No | Yes |
| Allows `null` key/value | Yes | No |
| Multiple reads | Unsafe with simultaneous writes | Safe |
| Concurrent updates | Unsafe | Safe |
| Iteration during updates | Can fail | Safe, but not a snapshot |
| Atomic update methods | No | `putIfAbsent()`, `compute()`, `merge()`, etc. |

## Limitation to Remember

`ConcurrentHashMap` makes operations on the map safe. It does not automatically make your full business logic safe.

```java
if (!map.containsKey("java")) {
    map.put("java", 1);
}
```

Use this instead:

```java
map.putIfAbsent("java", 1);
```

It also cannot make a multiple-key operation atomic:

```java
map.remove("sender");
map.put("receiver", 100);
```

If both steps must succeed together, use additional synchronization, a `Lock`, or a database transaction.

## Important Methods to Remember

```java
put()
get()
getOrDefault()
remove()
containsKey()

putIfAbsent()
replace()
remove(key, value)

computeIfAbsent()
computeIfPresent()
compute()
merge()
```

Most important for frequency counting:

```java
map.merge(key, 1, Integer::sum);
```