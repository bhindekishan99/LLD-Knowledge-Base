# CopyOnWriteArrayList

`CopyOnWriteArrayList` is a **thread-safe List** from `java.util.concurrent`.

## Key Idea

- **Read:** Uses the existing array.
- **Write:** Creates a new copy of the array and applies the change.

```text
Read  → existing array
Write → copy array → modify → replace
```

## When to Use

Best when:

- Many **reads**
- Very few **writes**
- Multiple threads access the list

## Example

```java
CopyOnWriteArrayList<String> list =
        new CopyOnWriteArrayList<>();

list.add("A");
list.add("B");

for (String item : list) {
    System.out.println(item);
}
```

## Important Points

- Thread-safe.
- Readers can continue while another thread writes.
- Iterator works on a **snapshot** of the list.
- Writes are expensive because the array is copied.
- Not suitable for frequent writes or very large lists.

## Remember

```text
Many Reads + Few Writes
        ↓
CopyOnWriteArrayList
```


## Supports same methods as ArrayList 

```Text
add()
get()
set()
remove()
contains()
size()
isEmpty()
clear()
```