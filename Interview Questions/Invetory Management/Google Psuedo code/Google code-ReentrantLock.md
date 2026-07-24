# Inventory Management System (Improved Design) - with Reentrant Lock

## 1. Domain Enums and Immutable Data Models

### Category

```java
enum Category {
    ELECTRONICS,
    FOOD,
    FURNITURE
}
```

---

### Product

```java
class Product {

    private final String sku;
    private final String name;
    private final double price;
    private final Category category;
    private final int thresholdQuantity;

    public Product(
            String sku,
            String name,
            double price,
            Category category,
            int thresholdQuantity) {

        this.sku = sku;
        this.name = name;
        this.price = price;
        this.category = category;
        this.thresholdQuantity = thresholdQuantity;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public int getThresholdQuantity() {
        return thresholdQuantity;
    }
}
```

---

# 2. Encapsulated Entity (Thread-Safe Warehouse)

```java
class Warehouse {

    private final String warehouseId;

    private final Map<String, Product> productDetailsMap =
            new ConcurrentHashMap<>();

    private final Map<String, Integer> stockQuantitiesMap =
            new ConcurrentHashMap<>();

    public Warehouse(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public boolean isStockBelowThreshold(String sku) {

        if (!stockQuantitiesMap.containsKey(sku)) {
            return false;
        }

        Product product = productDetailsMap.get(sku);

        int currentQty = stockQuantitiesMap.get(sku);

        return currentQty <= product.getThresholdQuantity();
    }

    public boolean addProductStock(
            Product product,
            int quantityToBeAdded) {

        String sku = product.getSku();

        productDetailsMap.putIfAbsent(sku, product);

        stockQuantitiesMap.compute(
                sku,
                (key, currentQty) -> {

                    int existing =
                            (currentQty == null) ? 0 : currentQty;

                    return existing + quantityToBeAdded;
                });

        return isStockBelowThreshold(sku);
    }

    public boolean removeProductStock(
            String sku,
            int quantityToBeDeducted) {

        if (!hasSufficientStock(sku, quantityToBeDeducted)) {

            throw new IllegalArgumentException(
                    "Insufficient stock in warehouse: "
                            + warehouseId);
        }

        stockQuantitiesMap.compute(
                sku,
                (key, currentQty) -> {

                    int existing =
                            (currentQty == null) ? 0 : currentQty;

                    return existing - quantityToBeDeducted;
                });

        return isStockBelowThreshold(sku);
    }

    public boolean hasSufficientStock(
            String sku,
            int quantityNeeded) {

        return stockQuantitiesMap.getOrDefault(
                sku,
                0
        ) >= quantityNeeded;
    }

    public Product getProductDetails(String sku) {
        return productDetailsMap.get(sku);
    }

    public int getQuantity(String sku) {
        return stockQuantitiesMap.getOrDefault(sku, 0);
    }
}
```

---

# 3. Segregated Observer Interfaces (ISP)

### Stock Movement Listener

```java
interface StockMovementListener {

    void onStockAdded(
            String warehouseId,
            String sku,
            int quantity);

    void onStockRemoved(
            String warehouseId,
            String sku,
            int quantity);

    void onStockTransferred(
            String fromWarehouseId,
            String toWarehouseId,
            String sku,
            int quantity);
}
```

---

### Stock Alert Listener

```java
interface StockAlertListener {

    void onLowStockAlert(
            String warehouseId,
            String sku,
            int currentQuantity);
}
```

---

### WhatsApp Notification Service

```java
class WhatsAppNotificationService
        implements StockAlertListener {

    @Override
    public void onLowStockAlert(
            String warehouseId,
            String sku,
            int currentQuantity) {

        System.out.println(
                "[WhatsApp Alert] -> Warehouse: "
                        + warehouseId
                        + " | SKU: "
                        + sku
                        + " dropped down to "
                        + currentQuantity
                        + " units!"
        );
    }
}
```

---

# 4. Replenishment Strategy

### Strategy Interface

```java
interface ReplenishmentStrategy {

    void productReplenishment(
            Warehouse warehouse,
            Product product);
}
```

---

### Standard Restock Strategy

```java
class StandardRestockMethod
        implements ReplenishmentStrategy {

    @Override
    public void productReplenishment(
            Warehouse warehouse,
            Product product) {

        int restockQuantity = 50;

        warehouse.addProductStock(
                product,
                restockQuantity
        );

        System.out.println(
                "[Auto-Restock Pipeline] Added 50 units of "
                        + product.getSku()
                        + " to warehouse "
                        + warehouse.getWarehouseId()
        );
    }
}
```

---

# 5. Inventory Controller (Thread-Safe)

```java
class Inventory {

    private final Map<String, Warehouse> warehouses =
            new ConcurrentHashMap<>();

    private final Map<String, ReentrantLock> warehouseLocks =
            new ConcurrentHashMap<>();

    private final List<StockMovementListener> movementListeners =
            new ArrayList<>();

    private final List<StockAlertListener> alertListeners =
            new ArrayList<>();

    private final ReplenishmentStrategy replenishmentStrategy;

    public Inventory(
            ReplenishmentStrategy replenishmentStrategy) {

        this.replenishmentStrategy =
                replenishmentStrategy;
    }

    public void addWarehouse(Warehouse warehouse) {

        warehouses.put(
                warehouse.getWarehouseId(),
                warehouse);

        warehouseLocks.putIfAbsent(
                warehouse.getWarehouseId(),
                new ReentrantLock());
    }

    public void registerAlertListener(
            StockAlertListener listener) {

        alertListeners.add(listener);
    }

    public void registerMovementListener(
            StockMovementListener listener) {

        movementListeners.add(listener);
    }

    public void addProduct(
            String warehouseId,
            Product product,
            int quantity) {

        ReentrantLock lock =
                warehouseLocks.get(warehouseId);

        lock.lock();

        try {

            Warehouse warehouse =
                    warehouses.get(warehouseId);

            boolean isLowStock =
                    warehouse.addProductStock(
                            product,
                            quantity);

            movementListeners.forEach(listener ->
                    listener.onStockAdded(
                            warehouseId,
                            product.getSku(),
                            quantity));

            if (isLowStock) {

                triggerAlert(
                        warehouseId,
                        product.getSku(),
                        warehouse.getQuantity(
                                product.getSku()));
            }

        } finally {

            lock.unlock();
        }
    }

    public void removeProduct(
            String warehouseId,
            String sku,
            int quantity) {

        ReentrantLock lock =
                warehouseLocks.get(warehouseId);

        lock.lock();

        try {

            Warehouse warehouse =
                    warehouses.get(warehouseId);

            boolean isLowStock =
                    warehouse.removeProductStock(
                            sku,
                            quantity);

            movementListeners.forEach(listener ->
                    listener.onStockRemoved(
                            warehouseId,
                            sku,
                            quantity));

            if (isLowStock) {

                triggerAlert(
                        warehouseId,
                        sku,
                        warehouse.getQuantity(sku));
            }

        } finally {

            lock.unlock();
        }
    }

    public void transferProductBetweenWarehouse(
            String fromWarehouseId,
            String toWarehouseId,
            String sku,
            int quantity) {

        ReentrantLock lock1 =
                warehouseLocks.get(fromWarehouseId);

        ReentrantLock lock2 =
                warehouseLocks.get(toWarehouseId);

        ReentrantLock firstLock =
                (fromWarehouseId.compareTo(toWarehouseId) < 0)
                        ? lock1
                        : lock2;

        ReentrantLock secondLock =
                (fromWarehouseId.compareTo(toWarehouseId) < 0)
                        ? lock2
                        : lock1;

        firstLock.lock();
        secondLock.lock();

        try {

            Warehouse sourceWarehouse =
                    warehouses.get(fromWarehouseId);

            Warehouse targetWarehouse =
                    warehouses.get(toWarehouseId);

            if (!sourceWarehouse.hasSufficientStock(
                    sku,
                    quantity)) {

                System.out.println(
                        "Transfer cancelled: Insufficient stock.");

                return;
            }

            Product product =
                    sourceWarehouse.getProductDetails(sku);

            boolean sourceIsLow =
                    sourceWarehouse.removeProductStock(
                            sku,
                            quantity);

            targetWarehouse.addProductStock(
                    product,
                    quantity);

            movementListeners.forEach(listener ->
                    listener.onStockTransferred(
                            fromWarehouseId,
                            toWarehouseId,
                            sku,
                            quantity));

            if (sourceIsLow) {

                triggerAlert(
                        fromWarehouseId,
                        sku,
                        sourceWarehouse.getQuantity(sku));
            }

        } finally {

            secondLock.unlock();
            firstLock.unlock();
        }
    }

    private void triggerAlert(
            String warehouseId,
            String sku,
            int currentQuantity) {

        alertListeners.forEach(listener ->
                listener.onLowStockAlert(
                        warehouseId,
                        sku,
                        currentQuantity));
    }
}
```

---

# Design Patterns Used

| Pattern | Purpose |
|----------|---------|
| **Observer Pattern** | Notify external systems when stock is added, removed, transferred, or reaches a low-stock threshold. |
| **Strategy Pattern** | Allows different replenishment algorithms without modifying the `Inventory` class. |

---

# Concurrency Mechanisms Used

| Mechanism | Purpose |
|-----------|---------|
| `ConcurrentHashMap` | Thread-safe storage for warehouses and stock quantities. |
| `ConcurrentHashMap.compute()` | Atomic read-modify-write operation for stock updates. |
| `ReentrantLock` | Prevents race conditions while updating warehouse stock. |
| Ordered Locking | Prevents deadlocks during warehouse-to-warehouse transfers. |

---

# SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **SRP** | `Warehouse` manages stock, `Inventory` coordinates operations, observers handle notifications. |
| **OCP** | New replenishment strategies and notification channels can be added without modifying existing code. |
| **LSP** | Any implementation of `ReplenishmentStrategy` or observer interface can replace another. |
| **ISP** | Observer responsibilities are separated into `StockMovementListener` and `StockAlertListener`. |
| **DIP** | `Inventory` depends on the `ReplenishmentStrategy` abstraction instead of concrete implementations. |
