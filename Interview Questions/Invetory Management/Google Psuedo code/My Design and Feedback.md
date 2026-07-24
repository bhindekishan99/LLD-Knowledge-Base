# Inventory Management System - Pseudo Code

## Category

```java
enum Category {
    ELECTRONIC,
    FOOD,
    FURNITURE
}
```

---

# Product

```java
class Product {

    String sku;
    String name;
    double price;
    int quantity;
    Category category;
    int thresholdQuantity;

    // Getters & Setters
}
```

---

# Warehouse

```java
class Warehouse {

    List<Product> listOfProducts;

    void addProduct(Product product);

    void removeProduct(Product product);

    void updateProductQuantity(Product product);

    void displayStock();
}
```

---

# Inventory

```java
class Inventory {

    List<Warehouse> listOfWarehouses;

    List<InventoryObserver> listOfObservers;

    ReplenishmentStrategy replenishmentStrategy;

    void addWarehouse(Warehouse warehouse);

    void removeWarehouse(Warehouse warehouse);

    void addObserver(InventoryObserver observer);

    void removeObserver(InventoryObserver observer);

    void addProduct(Warehouse warehouse, Product product) {

        warehouse.addProduct(product);

        addProductNotification(warehouse, product);

        if (warehouse.getProductThresholdQuantity(product)
                >= warehouse.getProductQuantity(product)) {

            lowStockNotification(warehouse, product);
        }
    }

    void removeProduct(Warehouse warehouse, Product product) {

        warehouse.removeProduct(product);

        removeProductNotification(warehouse, product);

        if (warehouse.getProductThresholdQuantity(product)
                >= warehouse.getProductQuantity(product)) {

            lowStockNotification(warehouse, product);
        }
    }

    void transferProductBetweenWarehouse(
            Warehouse fromWarehouse,
            Warehouse toWarehouse,
            Product product) {

        if (fromWarehouse.isProductAvailable(product)) {

            toWarehouse.addProduct(product);

            fromWarehouse.removeProduct(product);

            transferProductNotification(
                    fromWarehouse,
                    toWarehouse,
                    product
            );

            if (fromWarehouse.getProductThresholdQuantity(product)
                    >= fromWarehouse.getProductQuantity(product)) {

                lowStockNotification(fromWarehouse, product);
            }
        }
    }

    void lowStockNotification(
            Warehouse warehouse,
            Product product) {

        for (InventoryObserver observer : listOfObservers) {

            observer.lowStockNotification(
                    warehouse,
                    product
            );
        }
    }

    void productReplenishment(
            Warehouse warehouse,
            Product product) {

        replenishmentStrategy.productReplenishment(
                warehouse,
                product
        );
    }

    void periodicLowStockProductCheck() {

        for (Warehouse warehouse : listOfWarehouses) {

            for (Product product : warehouse.getProducts()) {

                if (warehouse.getProductQuantity(product)
                        <= warehouse.getProductThresholdQuantity(product)) {

                    lowStockNotification(
                            warehouse,
                            product
                    );
                }
            }
        }
    }
}
```

---

# Inventory Observer

```java
interface InventoryObserver {

    void addProductNotification(
            Warehouse warehouse,
            Product product);

    void removeProductNotification(
            Warehouse warehouse,
            Product product);

    void transferProductNotification(
            Warehouse fromWarehouse,
            Warehouse toWarehouse,
            Product product);

    void lowStockNotification(
            Warehouse warehouse,
            Product product);
}
```

---

# WhatsApp Observer

```java
class WhatsAppObserver implements InventoryObserver {

    @Override
    void addProductNotification(
            Warehouse warehouse,
            Product product) {

        System.out.println(
                "Sending add notification via WhatsApp"
        );
    }

    @Override
    void removeProductNotification(
            Warehouse warehouse,
            Product product) {

        System.out.println(
                "Sending remove notification via WhatsApp"
        );
    }

    @Override
    void transferProductNotification(
            Warehouse fromWarehouse,
            Warehouse toWarehouse,
            Product product) {

        System.out.println(
                "Sending transfer notification via WhatsApp"
        );
    }

    @Override
    void lowStockNotification(
            Warehouse warehouse,
            Product product) {

        System.out.println(
                "ALERT!! Low stock notification via WhatsApp"
        );
    }
}
```

---

# Replenishment Strategy

```java
interface ReplenishmentStrategy {

    void productReplenishment(
            Warehouse warehouse,
            Product product);
}
```

---

# Replenishment Method One

```java
class ReplenishmentMethodOne
        implements ReplenishmentStrategy {

    @Override
    void productReplenishment(
            Warehouse warehouse,
            Product product) {

        int quantity = 10;

        product.setQuantity(quantity);

        warehouse.addProduct(product);
    }
}
```

---

# Design Patterns Used

| Pattern | Usage |
|----------|-------|
| Observer Pattern | Notify external systems when products are added, removed, transferred, or low on stock. |
| Strategy Pattern | Allows different replenishment algorithms without modifying `Inventory`. |

---

# Responsibilities

## Product

- Stores product information.
- Maintains quantity and threshold quantity.

## Warehouse

- Manages products within a warehouse.
- Adds, removes, updates, and displays stock.

## Inventory

- Coordinates multiple warehouses.
- Transfers products between warehouses.
- Performs periodic low-stock checks.
- Notifies observers.
- Delegates replenishment to a strategy.

## InventoryObserver

- Receives inventory-related notifications.

# Feedback

## 1. Responsibility of Threshold Validation

### Observation

Currently, the `Inventory` class performs the logic to determine whether a product's quantity has fallen below its threshold.

```java
if (warehouse.getProductThresholdQuantity(product)
        >= warehouse.getProductQuantity(product)) {

    lowStockNotification(warehouse, product);
}
```

### Issue

This logic belongs to the **Warehouse**, not the **Inventory**.

The `Warehouse` owns the products and their quantities, so it should also be responsible for determining whether a product is running low.

This follows the **Single Responsibility Principle (SRP)** and improves encapsulation.

### Suggested Improvement

Move the threshold calculation into the `Warehouse` class.

```java
class Warehouse {

    boolean isProductBelowThreshold(Product product) {
        return getProductQuantity(product)
                <= getProductThresholdQuantity(product);
    }
}
```

Then simply use:

```java
if (warehouse.isProductBelowThreshold(product)) {
    lowStockNotification(warehouse, product);
}
```

---

# 2. Duplicate Code Pathways

### Observation

The exact same low-stock check appears in multiple places:

- `addProduct()`
- `removeProduct()`
- `transferProductBetweenWarehouse()`

```java
if (warehouse.getProductThresholdQuantity(product)
        >= warehouse.getProductQuantity(product)) {

    lowStockNotification(warehouse, product);
}
```

### Issue

This violates the **DRY (Don't Repeat Yourself)** principle.

If the threshold logic changes in the future, the same code must be modified in multiple locations, increasing maintenance effort and the risk of inconsistencies.

### Suggested Improvement

Extract the common logic into a helper method.

```java
private void checkAndNotifyLowStock(
        Warehouse warehouse,
        Product product) {

    if (warehouse.isProductBelowThreshold(product)) {
        lowStockNotification(warehouse, product);
    }
}
```

Then simply call:

```java
checkAndNotifyLowStock(warehouse, product);
```

from every operation.

---

# 3. Violation of the Interface Segregation Principle (ISP)

### Observation

The `InventoryObserver` interface combines multiple unrelated events into a single interface.

```java
interface InventoryObserver {

    addProductNotification(...);

    removeProductNotification(...);

    transferProductNotification(...);

    lowStockNotification(...);
}
```

### Issue

This violates the **Interface Segregation Principle (ISP)**.

Some observers may only be interested in a subset of these events.

For example:

- A Purchasing Service may only care about **low-stock notifications**.
- An Audit Service may only care about **transfer events**.
- An Analytics Service may only care about **product additions**.

Such observers are forced to implement unnecessary methods with empty bodies.

### Suggested Improvement

Split the interface into smaller, more focused interfaces.

```java
interface LowStockObserver {
    void lowStockNotification(...);
}

interface ProductObserver {
    void addProductNotification(...);
    void removeProductNotification(...);
}

interface TransferObserver {
    void transferProductNotification(...);
}
```

This follows the **Interface Segregation Principle (ISP)** and keeps implementations clean.

---

# 4. Leakage of Internal Domain Objects

### Observation

The notification methods expose internal domain objects directly.

```java
observer.lowStockNotification(
        warehouse,
        product
);
```

### Issue

External notification systems (e.g., WhatsApp, Email, SMS) should not depend on internal business entities like `Warehouse` and `Product`.

Doing so tightly couples the notification layer with the domain model.

In many real-world applications, these objects may contain sensitive or unnecessary information.

### Suggested Improvement

Pass only the information required by the notification service.

For example:

```java
observer.lowStockNotification(
        warehouse.getWarehouseId(),
        product.getSku(),
        product.getName(),
        warehouse.getProductQuantity(product)
);
```

or

```java
observer.lowStockNotification(
        warehouseId,
        sku,
        currentQuantity
);
```

This reduces coupling and prevents leakage of internal domain objects.

---

# Summary

| Feedback | Recommendation |
|-----------|----------------|
| Threshold calculation in `Inventory` | Move the logic to `Warehouse` since it owns product quantities. |
| Duplicate low-stock checks | Extract into a common helper method (`checkAndNotifyLowStock()`). |
| Large `InventoryObserver` interface | Split into smaller interfaces following the Interface Segregation Principle (ISP). |
| Passing `Warehouse` and `Product` to observers | Pass only the required primitive values (e.g., `warehouseId`, `sku`, `quantity`) to reduce coupling. |


## ReplenishmentStrategy

- Defines different product replenishment algorithms.
