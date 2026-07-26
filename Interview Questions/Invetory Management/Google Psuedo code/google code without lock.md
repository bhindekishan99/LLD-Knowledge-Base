# Inventory Management System (Pseudo Code)

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

    String sku;
    String name;
    double price;
    Category category;
    int thresholdQuantity;

    Constructor(
            sku,
            name,
            price,
            category,
            thresholdQuantity) {

        this.sku = sku;
        this.name = name;
        this.price = price;
        this.category = category;
        this.thresholdQuantity = thresholdQuantity;
    }
}
```

---

# 2. Encapsulated Warehouse Component (O(1) Map Lookups)

```java
class Warehouse {

    String warehouseId;

    Map<String, Product> productDetailsMap;
    Map<String, Integer> stockQuantitiesMap;

    Constructor(warehouseId) {

        this.warehouseId = warehouseId;

        this.productDetailsMap = new Map();

        this.stockQuantitiesMap = new Map();
    }

    boolean isStockBelowThreshold(String sku) {

        if (!stockQuantitiesMap.containsKey(sku)) {
            return false;
        }

        Product product = productDetailsMap.get(sku);

        int currentQuantity =
                stockQuantitiesMap.get(sku);

        return currentQuantity
                <= product.thresholdQuantity;
    }

    boolean addProductStock(
            Product product,
            int quantityToBeAdded) {

        String sku = product.sku;

        if (!productDetailsMap.containsKey(sku)) {

            productDetailsMap.put(
                    sku,
                    product);
        }

        int existingQuantity =
                stockQuantitiesMap.getOrDefault(
                        sku,
                        0);

        stockQuantitiesMap.put(
                sku,
                existingQuantity + quantityToBeAdded);

        return isStockBelowThreshold(sku);
    }

    boolean removeProductStock(
            String sku,
            int quantityToBeDeducted) {

        int currentQuantity =
                stockQuantitiesMap.getOrDefault(
                        sku,
                        0);

        if (currentQuantity < quantityToBeDeducted) {

            Print(
                "Error: Stock deficiency inside warehouse "
                + warehouseId
            );

            return false;
        }

        stockQuantitiesMap.put(
                sku,
                currentQuantity - quantityToBeDeducted);

        return isStockBelowThreshold(sku);
    }

    boolean hasSufficientStock(
            String sku,
            int quantityNeeded) {

        return stockQuantitiesMap.getOrDefault(
                sku,
                0
        ) >= quantityNeeded;
    }

    Product getProductDetails(String sku) {

        return productDetailsMap.get(sku);
    }

    int getQuantity(String sku) {

        return stockQuantitiesMap.getOrDefault(
                sku,
                0);
    }
}
```

---

# 3. Segregated Observer Interfaces (SOLID Alignment)

## Stock Movement Listener

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

## Stock Alert Listener

```java
interface StockAlertListener {

    void onLowStockAlert(
            String warehouseId,
            String sku,
            int currentQuantity);
}
```

---

## WhatsApp Notification Service

```java
class WhatsAppNotificationService
        implements StockAlertListener {

    void onLowStockAlert(
            String warehouseId,
            String sku,
            int currentQuantity) {

        System.out.println(
                "CRITICAL [WhatsApp] -> Warehouse: "
                + warehouseId
                + " | SKU: "
                + sku
                + " dropped to "
                + currentQuantity
        );
    }
}
```

---

# 4. Inventory Controller

```java
class Inventory {

    Map<String, Warehouse> warehouses;

    List<StockMovementListener> movementListeners;

    List<StockAlertListener> alertListeners;

    ReplenishmentStrategy replenishmentStrategy;

    Constructor(ReplenishmentStrategy strategy) {

        this.warehouses = new Map();

        this.movementListeners = new List();

        this.alertListeners = new List();

        this.replenishmentStrategy = strategy;
    }

    void registerAlertListener(
            StockAlertListener listener) {

        alertListeners.add(listener);
    }

    void registerMovementListener(
            StockMovementListener listener) {

        movementListeners.add(listener);
    }

    void addProduct(
            String warehouseId,
            Product product,
            int quantity) {

        Warehouse warehouse =
                warehouses.get(warehouseId);

        boolean isLowStock =
                warehouse.addProductStock(
                        product,
                        quantity);

        notifyMovementListeners(
                "ADD",
                warehouseId,
                "",
                product.sku,
                quantity);

        if (isLowStock) {

            triggerThresholdAlert(
                    warehouseId,
                    product.sku,
                    warehouse.getQuantity(
                            product.sku));
        }
    }

    void removeProduct(
            String warehouseId,
            String sku,
            int quantity) {

        Warehouse warehouse =
                warehouses.get(warehouseId);

        boolean isLowStock =
                warehouse.removeProductStock(
                        sku,
                        quantity);

        notifyMovementListeners(
                "REMOVE",
                warehouseId,
                "",
                sku,
                quantity);

        if (isLowStock) {

            triggerThresholdAlert(
                    warehouseId,
                    sku,
                    warehouse.getQuantity(sku));
        }
    }

    void transferProductBetweenWarehouse(
            String fromWarehouseId,
            String toWarehouseId,
            String sku,
            int quantity) {

        Warehouse sourceWarehouse =
                warehouses.get(fromWarehouseId);

        Warehouse targetWarehouse =
                warehouses.get(toWarehouseId);

        if (!sourceWarehouse.hasSufficientStock(
                sku,
                quantity)) {

            Print(
                "Transfer cancelled: Missing source items."
            );

            return;
        }

        Product product =
                sourceWarehouse.getProductDetails(
                        sku);

        boolean sourceIsLow =
                sourceWarehouse.removeProductStock(
                        sku,
                        quantity);

        targetWarehouse.addProductStock(
                product,
                quantity);

        notifyMovementListeners(
                "TRANSFER",
                fromWarehouseId,
                toWarehouseId,
                sku,
                quantity);

        if (sourceIsLow) {

            triggerThresholdAlert(
                    fromWarehouseId,
                    sku,
                    sourceWarehouse.getQuantity(sku));
        }
    }

    void periodicLowStockProductCheck() {

        for (Warehouse warehouse
                : warehouses.values()) {

            for (String sku
                    : warehouse.productDetailsMap.keySet()) {

                if (warehouse.isStockBelowThreshold(sku)) {

                    triggerThresholdAlert(
                            warehouse.warehouseId,
                            sku,
                            warehouse.getQuantity(sku));
                }
            }
        }
    }

    void invokeReplenishment(
            String warehouseId,
            String sku) {

        Warehouse warehouse =
                warehouses.get(warehouseId);

        Product product =
                warehouse.getProductDetails(sku);

        replenishmentStrategy.productReplenishment(
                warehouse,
                product);
    }

    private void triggerThresholdAlert(
            String warehouseId,
            String sku,
            int currentQuantity) {

        for (StockAlertListener listener
                : alertListeners) {

            listener.onLowStockAlert(
                    warehouseId,
                    sku,
                    currentQuantity);
        }
    }

    private void notifyMovementListeners(
            String type,
            String warehouse1,
            String warehouse2,
            String sku,
            int quantity) {

        for (StockMovementListener listener
                : movementListeners) {

            if (type == "ADD") {

                listener.onStockAdded(
                        warehouse1,
                        sku,
                        quantity);
            }

            if (type == "REMOVE") {

                listener.onStockRemoved(
                        warehouse1,
                        sku,
                        quantity);
            }

            if (type == "TRANSFER") {

                listener.onStockTransferred(
                        warehouse1,
                        warehouse2,
                        sku,
                        quantity);
            }
        }
    }
}
```

---

# 5. Configurable Replenishment Strategy

## Strategy Interface

```java
interface ReplenishmentStrategy {

    void productReplenishment(
            Warehouse warehouse,
            Product product);
}
```

---

## Standard Restock Method

```java
class StandardRestockMethod
        implements ReplenishmentStrategy {

    void productReplenishment(
            Warehouse warehouse,
            Product product) {

        int defaultBatchOrderSize = 50;

        warehouse.addProductStock(
                product,
                defaultBatchOrderSize);

        Print(
            "System automatic pipeline ordered restock of 50 units for "
            + product.sku
        );
    }
}
```

---

# Design Patterns Used

| Pattern | Purpose |
|----------|---------|
| **Observer Pattern** | Notify external systems whenever stock is added, removed, transferred, or falls below the threshold. |
| **Strategy Pattern** | Allows different replenishment algorithms without modifying the `Inventory` class. |

---

# SOLID Principles Applied

| Principle | Application |
|-----------|-------------|
| **SRP** | `Warehouse` manages stock operations, `Inventory` coordinates workflows, observers handle notifications. |
| **OCP** | New replenishment strategies and notification channels can be introduced without modifying existing classes. |
| **LSP** | Any implementation of `ReplenishmentStrategy` or observer interfaces can be substituted. |
| **ISP** | Observer responsibilities are split into `StockMovementListener` and `StockAlertListener`. |
| **DIP** | `Inventory` depends on the `ReplenishmentStrategy` abstraction rather than a concrete implementation. |
