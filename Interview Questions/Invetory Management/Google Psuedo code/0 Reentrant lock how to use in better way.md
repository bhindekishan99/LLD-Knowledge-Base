# ReentrantLock at InventoryProduct Level

Yes. in Google code-ReentrantLock.md, you currently have **one `ReentrantLock` per warehouse**:

```java
private final Map<String, ReentrantLock> warehouseLocks =
        new ConcurrentHashMap<>();
```

And:

```java
ReentrantLock lock = warehouseLocks.get(warehouseId);
lock.lock();
```

So you're right: **all products inside the same warehouse share that one lock.**

If you want **one lock per `InventoryProduct`**, create a lock map using a key that uniquely identifies:

```text
Warehouse + Product
```

For example:

```java
private final Map<String, ReentrantLock> productLocks =
        new ConcurrentHashMap<>();
```

Then:

```java
String lockKey = warehouseId + ":" + product.getSku();

ReentrantLock lock = productLocks.computeIfAbsent(
        lockKey,
        k -> new ReentrantLock()
);

lock.lock();

try {
    // update this product
} finally {
    lock.unlock();
}
```

## Locking Model

Now the locking looks like:

```text
productLocks

"W1:P100" → Lock A
"W1:P200" → Lock B
"W2:P100" → Lock C
```

Therefore:

```text
Thread 1:
W1 + P100 → Lock A 🔒

Thread 2:
W1 + P200 → Lock B 🔒

        ↓

Both can run concurrently ✅
```

But:

```text
Thread 1:
W1 + P100 → Lock A 🔒

Thread 2:
W1 + P100 → Lock A → WAIT
```

## Even Cleaner Approach

Since your domain already has `InventoryProduct`, I would actually prefer making the lock part of `InventoryProduct`.

```java
class InventoryProduct {

    private final Product product;
    private int quantity;

    private final ReentrantLock lock = new ReentrantLock();

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    // quantity methods...
}
```

Then `Inventory` becomes:

```java
InventoryProduct inventoryProduct =
        warehouse.getInventoryProduct(sku);

inventoryProduct.lock();

try {
    warehouse.removeProductStock(
            inventoryProduct,
            quantity);

    // notify
    // alert
} finally {
    inventoryProduct.unlock();
}
```

## Mental Model

```text
Warehouse
   |
   +--- InventoryProduct P100
   |         └── 🔒 Lock
   |
   +--- InventoryProduct P200
             └── 🔒 Lock
```

So:

```text
P100 and P200
     ↓
independent locks
     ↓
can execute concurrently
```

For your LLD interview, I'd prefer this approach over maintaining a separate `Map<String, ReentrantLock>` because the lock belongs naturally to the **shared mutable state (`InventoryProduct`) that it protects**.
