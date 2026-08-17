# `HashMap` vs `LinkedHashMap` vs `TreeMap`

| Map | Key order | Internal structure | Typical `put()`, `get()`, `remove()` |
|---|---|---|---|
| `HashMap` | No guaranteed order | Hash table | `O(1)` average |
| `LinkedHashMap` | Insertion order | Hash table + linked list | `O(1)` average |
| `TreeMap` | Keys sorted | Red-Black Tree | `O(log n)` |

## `HashMap`

```java
Map<Integer, String> hashMap = new HashMap<>();

hashMap.put(3, "C");
hashMap.put(1, "A");
hashMap.put(2, "B");

// Iteration order is not guaranteed.
```

## `LinkedHashMap`

```java
Map<Integer, String> linkedHashMap = new LinkedHashMap<>();

linkedHashMap.put(3, "C");
linkedHashMap.put(1, "A");
linkedHashMap.put(2, "B");

// Iteration order: 3=C, 1=A, 2=B
```

## `TreeMap`

```java
Map<Integer, String> treeMap = new TreeMap<>();

treeMap.put(3, "C");
treeMap.put(1, "A");
treeMap.put(2, "B");

// Iteration order: 1=A, 2=B
```

## Which One Should You Use?

- `HashMap` — Default choice when order does not matter.
- `LinkedHashMap` — Use when insertion order must be preserved.
- `TreeMap` — Use when keys must stay sorted or when navigation methods such as `floorKey()` and `ceilingKey()` are needed.