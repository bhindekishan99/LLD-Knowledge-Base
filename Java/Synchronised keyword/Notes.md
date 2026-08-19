# Java `synchronized` Keyword

`synchronized` allows **only one thread at a time** to execute code protected by the **same lock**.

## Ways to Use It

### 1. Synchronized Method

```java
public synchronized void increment() {
    count++;
}
```

Locks on `this`.

Equivalent to:

```java
synchronized (this) {
    count++;
}
```

### 2. Synchronized Block

```java
public void increment() {
    // Other code

    synchronized (this) {
        count++;
    }
}
```

Locks only the critical section.

### 3. Specific Lock Object

```java
private final Object lock = new Object();

synchronized (lock) {
    count++;
}
```

A `private` lock gives better control because outside code cannot access it.

### 4. Class Lock

```java
synchronized (Counter.class) {
    count++;
}
```

All `Counter` objects share the same lock.

### 5. Static Synchronized Method

```java
public static synchronized void method() {
    // code
}
```

Locks on `Counter.class`.

## Quick Summary

| Syntax | Lock |
|---|---|
| `synchronized` instance method | `this` |
| `synchronized (this)` | `this` |
| `synchronized (lock)` | Specific object |
| `synchronized (MyClass.class)` | Class object |
| `static synchronized` method | Class object |

## Key Rule

```text
Same lock
   ↓
Threads block each other

Different locks
   ↓
Threads can run concurrently
```