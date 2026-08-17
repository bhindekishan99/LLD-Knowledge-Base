Java "synchronized" Keyword

"synchronized" ensures that only one thread at a time can execute a critical section using the same lock.

1. Synchronized Method

class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }
}

The lock is on the current object ("this").

Counter object
      ↓
     🔒

Equivalent to:

public void increment() {
    synchronized (this) {
        count++;
    }
}

---

2. Synchronized Block on "this"

public void increment() {

    // Other code

    synchronized (this) {
        count++;
    }

    // Other code
}

Only the code inside the block is protected.

Use when: you don't want to lock the entire method.

---

3. Synchronized Block on a Specific Object

class Counter {
    private int count = 0;

    private final Object lock = new Object();

    public void increment() {
        synchronized (lock) {
            count++;
        }
    }
}

Here, "lock" is the object used for synchronization:

lock object
    ↓
   🔒

Why use a private lock?

private final Object lock = new Object();

Because outside code cannot access the lock.

This gives the class better control over synchronization than:

synchronized (this)

because outside code can access the "this" object.

---

4. Synchronized Block on a Class

Yes, you can synchronize on a class:

synchronized (Counter.class) {
    count++;
}

The lock is:

Counter.class
     ↓
    🔒

All "Counter" objects use the same lock.

Counter c1 = new Counter();
Counter c2 = new Counter();

Both use:

synchronized (Counter.class)

Therefore, only one thread can enter the block at a time.

---

5. Static Synchronized Method

public static synchronized void increment() {
    count++;
}

A static synchronized method locks on:

Counter.class

It is conceptually equivalent to:

public static void increment() {
    synchronized (Counter.class) {
        count++;
    }
}

---

Quick Summary

Syntax| Lock
"synchronized void method()"| "this" object
"synchronized (this)"| "this" object
"synchronized (lock)"| Specific lock object
"synchronized (ClassName.class)"| Class object
"static synchronized method()"| Class object

Easy Rule

synchronized(this)
        ↓
   One object

synchronized(lock)
        ↓
   Specific lock object

synchronized(MyClass.class)
        ↓
   Shared by all MyClass objects

Important Rule

"synchronized" only protects code when threads use the same lock.

Thread 1 ──→ 🔒 ←── Thread 2
              ↑
          same lock

If two threads use different locks, they do not block each other.
