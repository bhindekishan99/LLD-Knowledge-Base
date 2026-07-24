# Concurrency in Inventory Management

When designing an inventory system, concurrency becomes important because multiple threads may try to update stock simultaneously.

For example:

- Multiple warehouse workers updating stock
- Customer orders reducing inventory
- Background audit jobs reading inventory
- Warehouse-to-warehouse transfers

Without proper synchronization, the inventory can become inconsistent.

---

# Approach 1: Thread-Safe Collections (`ConcurrentHashMap`)

The easiest way to introduce concurrency is by replacing normal `HashMap` with Java's built-in `ConcurrentHashMap`.

Unlike `HashMap`, `ConcurrentHashMap` does **not** lock the entire map.

Instead, it uses **fine-grained locking (lock striping/internal synchronization)** so different threads can safely update different entries simultaneously.

### Example

- Thread A updates Coke inventory.
- Thread B updates Chips inventory.

Since they operate on different keys, both updates can happen concurrently without blocking each other.

---

## Warehouse Implementation

```java
import java.util.concurrent.ConcurrentHashMap;

class Warehouse {

    String warehouseId;

    // Thread-safe maps
    Map<String, Product> productDetailsMap = new ConcurrentHashMap<>();
    Map<String, Integer> stockQuantitiesMap = new ConcurrentHashMap<>();

    public boolean addProductStock(Product product, int quantityToBeAdded) {

        String sku = product.getSku();

        productDetailsMap.putIfAbsent(sku, product);

        // Atomic update
        stockQuantitiesMap.compute(sku, (key, currentQty) -> {
            int existing = (currentQty == null) ? 0 : currentQty;
            return existing + quantityToBeAdded;
        });

        return isStockBelowThreshold(sku);
    }

    public boolean removeProductStock(String sku, int quantityToBeDeducted) {

        synchronized (this) {

            int currentQty = stockQuantitiesMap.getOrDefault(sku, 0);

            if (currentQty < quantityToBeDeducted) {
                System.out.println("Stock deficiency");
                return false;
            }

            stockQuantitiesMap.put(sku, currentQty - quantityToBeDeducted);

            return isStockBelowThreshold(sku);
        }
    }
}
```

---

# Why `compute()`?

`ConcurrentHashMap.compute()` performs the entire read-modify-write operation atomically.

Instead of:

```java
qty = map.get(key);
qty += 10;
map.put(key, qty);
```

(which is **not thread-safe**),

`compute()` guarantees no other thread can modify that key while the computation is in progress.

---

# Check-Then-Act Problem

Operations like:

```java
if(currentQty >= quantity){
    currentQty -= quantity;
}
```

are **not atomic**.

Two threads can both pass the condition simultaneously and overwrite each other's updates.

To prevent this, the entire sequence must be protected using:

- `synchronized`
- `Lock`
- Atomic operations (`compute()`)

---

# Does Approach 1 Work for Warehouse Transfers?

**No.**

`ConcurrentHashMap` only provides thread safety **inside one warehouse**.

A warehouse transfer touches **two different warehouses**, making it a distributed operation.

---

## Transfer Scenario

```
Warehouse A
      |
 remove()
      |
      |
      |--------> Context Switch
                    |
                    |
             Audit Thread Runs
                    |
Warehouse B
(add not executed yet)
```

At this moment:

```
Warehouse A = Updated
Warehouse B = Not Updated
```

The inventory is temporarily inconsistent.

If the application crashes before the addition completes, the products are permanently lost.

---

# The Split-State Problem

Imagine:

```
Warehouse A

Coke = 100
```

Thread 1 starts transferring 10 Coke bottles to Warehouse B.

### Step 1

```
Warehouse A

100 → 90
```

### Step 2

Before adding to Warehouse B...

A background audit thread runs.

It reads:

```
Warehouse A = 90

Warehouse B = 50
```

The audit concludes:

```
Total Coke = 140
```

Actual total should be:

```
150
```

Those 10 bottles temporarily disappeared from the system.

---

# Why Does This Happen?

Because the transfer is **not atomic**.

Two independent operations occur:

```
remove()

↓

add()
```

Another thread can observe the system in between these two steps.

---

# Computer Science Principle: Scope of Atomicity

## Single Warehouse Operation

```
Warehouse

↓

Remove Stock
```

Only one resource is involved.

`ConcurrentHashMap` is sufficient.

---

## Warehouse Transfer

```
Warehouse A

↓

Warehouse B
```

Two independent resources are modified.

Both must be protected together.

---

# Approach 2: Explicit Locking

For operations involving multiple resources, we use explicit locks.

```java
import java.util.concurrent.locks.ReentrantLock;
```

Each warehouse has its own lock.

```java
Map<String, ReentrantLock> warehouseLocks = new ConcurrentHashMap<>();
```

---

# Deadlock Problem

Imagine two transfers happening simultaneously.

### Thread A

```
Warehouse A

↓

Warehouse B
```

### Thread B

```
Warehouse B

↓

Warehouse A
```

Thread A locks:

```
Warehouse A
```

Thread B locks:

```
Warehouse B
```

Now:

- Thread A waits for Warehouse B
- Thread B waits for Warehouse A

Neither thread can proceed.

This is a **deadlock**.

---

# Deadlock Prevention

Always acquire locks in a consistent order.

For example:

```
Lock warehouse with smaller ID first.
```

If IDs are:

```
WH-001
WH-002
```

Every thread follows:

```
Lock WH-001

↓

Lock WH-002
```

No circular waiting is possible.

---

# Transfer Implementation

```java
public void transferProductBetweenWarehouse(
        String fromWhId,
        String toWhId,
        String sku,
        int quantity) {

    warehouseLocks.putIfAbsent(fromWhId, new ReentrantLock());
    warehouseLocks.putIfAbsent(toWhId, new ReentrantLock());

    ReentrantLock lock1 = warehouseLocks.get(fromWhId);
    ReentrantLock lock2 = warehouseLocks.get(toWhId);

    ReentrantLock firstLock =
            (fromWhId.compareTo(toWhId) < 0) ? lock1 : lock2;

    ReentrantLock secondLock =
            (fromWhId.compareTo(toWhId) < 0) ? lock2 : lock1;

    firstLock.lock();
    secondLock.lock();

    try {

        Warehouse sourceWh = warehouses.get(fromWhId);
        Warehouse targetWh = warehouses.get(toWhId);

        if (!sourceWh.hasSufficientStock(sku, quantity)) {
            System.out.println("Transfer cancelled");
            return;
        }

        Product product = sourceWh.getProductDetails(sku);

        sourceWh.removeProductStock(sku, quantity);

        targetWh.addProductStock(product, quantity);

    } finally {

        secondLock.unlock();
        firstLock.unlock();
    }
}
```

---

# Why Approach 2 Works

Both warehouses remain locked during the entire transfer.

```
Lock Warehouse A

↓

Lock Warehouse B

↓

Remove Stock

↓

Add Stock

↓

Unlock Warehouse B

↓

Unlock Warehouse A
```

No other thread can observe an intermediate state.

The transfer behaves like a single atomic transaction.

---

# Key Interview Takeaways

## 1. Thread-Safe Collection ≠ Thread-Safe Business Operation

`ConcurrentHashMap` guarantees that **individual map operations** are thread-safe.

It **does not** make an entire business workflow thread-safe.

---

## 2. Check-Then-Act Must Be Atomic

This pattern is unsafe:

```java
if(stock > quantity){
    stock -= quantity;
}
```

Protect it using:

- `synchronized`
- `Lock`
- Atomic map operations

---

## 3. Multi-Resource Operations Need Explicit Coordination

Whenever an operation modifies multiple independent resources (e.g., two warehouses, two bank accounts), thread-safe collections alone are insufficient.

Use explicit locks to coordinate the operation.

---

## 4. Prevent Deadlocks Using Consistent Lock Ordering

Always acquire locks in a deterministic order.

For example:

```
Smaller Warehouse ID

↓

Larger Warehouse ID
```

This eliminates circular waiting and prevents deadlocks.

---

# Summary

| Scenario | Recommended Approach |
|-----------|----------------------|
| Add stock | `ConcurrentHashMap.compute()` |
| Remove stock | `compute()` or `synchronized` |
| Read inventory | `ConcurrentHashMap` |
| Transfer between warehouses | `ReentrantLock` + Consistent Lock Ordering |
| Multi-resource transaction | Explicit Locks |
| Check-Then-Act | Atomic synchronization |
