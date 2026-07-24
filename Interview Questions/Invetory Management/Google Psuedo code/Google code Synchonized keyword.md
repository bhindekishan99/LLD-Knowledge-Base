# Inventory Management System (Using `synchronized`)

## 1. Domain Enums and Immutable Data Models

### Category

```java
enum Category {
    ELECTRONICS,
    FOOD,
    FURNITURE
}
```

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

# 2. Warehouse (Encapsulated Entity)

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

        if (!stockQuantitiesMap.containsKey(sku))
            return false;

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
                            (currentQty == null)
                                    ? 0
                                    : currentQty;

                    return existing + quantityToBeAdded;
                });

        return isStockBelowThreshold(sku);
    }

    public boolean removeProductStock(
            String sku,
            int quantityToBeDeducted) {

        if (!hasSufficientStock(
                sku,
                quantityNeeded(quantityToBeDeducted))) {

            throw new IllegalArgumentException(
                    "Insufficient stock in warehouse: "
                            + warehouseId);
        }

        stockQuantitiesMap.compute(
                sku,
                (key, currentQty) -> {

                    int existing =
                            (currentQty == null)
                                    ? 0
                                    : currentQty;

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

    private int quantityNeeded(int qty) {
        return qty;
    }
}
```

---

# 3. Observer Interfaces

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

## System Audit Logger

```java
class SystemAuditLogger
        implements StockMovementListener {

    @Override
    public void onStockAdded(
            String warehouseId,
            String sku,
            int quantity) {

        System.out.println(
                "[AUDIT LOG] Stock ADDED. Wh: "
                        + warehouseId
                        + " | SKU: "
                        + sku
                        + " | Qty: +"
                        + quantity);
    }

    @Override
    public void onStockRemoved(
            String warehouseId,
            String sku,
            int quantity) {

        System.out.println(
                "[AUDIT LOG] Stock REMOVED. Wh: "
                        + warehouseId
                        + " | SKU: "
                        + sku
                        + " | Qty: -"
                        + quantity);
    }

    @Override
    public void onStockTransferred(
            String fromWarehouseId,
            String toWarehouseId,
            String sku,
            int quantity) {

        System.out.println(
                "[AUDIT LOG] Stock TRANSFERRED. From: "
                        + fromWarehouseId
                        + " -> To: "
                        + toWarehouseId
                        + " | SKU: "
                        + sku
                        + " | Qty: "
                        + quantity);
    }
}
```

---

# 4. Strategy Pattern

## Strategy Interface

```java
interface ReplenishmentStrategy {

    void productReplenishment(
            Warehouse warehouse,
            Product product);
}
```

---

## Standard Restock Strategy

```java
class StandardRestockMethod
        implements ReplenishmentStrategy {

    @Override
    public void productReplenishment(
            Warehouse warehouse,
            Product product) {

        int restockQty = 50;

        warehouse.addProductStock(
                product,
                restockQty);

        System.out.println(
                "[Auto-Restock Pipeline] Ordered restock of 50 units for "
                        + product.getSku());
    }
}
```

---

# 5. Inventory Manager (`synchronized`)

```java
class SynchronizedInventory {

    private final Map<String, Warehouse> warehouses =
            new ConcurrentHashMap<>();

    private final List<StockMovementListener> movementListeners =
            new ArrayList<>();

    private final List<StockAlertListener> alertListeners =
            new ArrayList<>();

    private final ReplenishmentStrategy replenishmentStrategy;

    public SynchronizedInventory(
            ReplenishmentStrategy strategy) {

        this.replenishmentStrategy = strategy;
    }

    public void addWarehouse(Warehouse warehouse) {
        warehouses.put(
                warehouse.getWarehouseId(),
                warehouse);
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

        Warehouse warehouse =
                warehouses.get(warehouseId);

        synchronized (warehouse) {

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
        }
    }

    public void removeProduct(
            String warehouseId,
            String sku,
            int quantity) {

        Warehouse warehouse =
                warehouses.get(warehouseId);

        synchronized (warehouse) {

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
        }
    }

    public void transferProductBetweenWarehouse(
            String fromWarehouseId,
            String toWarehouseId,
            String sku,
            int quantity) {

        Warehouse sourceWarehouse =
                warehouses.get(fromWarehouseId);

        Warehouse targetWarehouse =
                warehouses.get(toWarehouseId);

        int sourceHash =
                System.identityHashCode(sourceWarehouse);

        int targetHash =
                System.identityHashCode(targetWarehouse);

        Warehouse firstResource =
                (sourceHash < targetHash)
                        ? sourceWarehouse
                        : targetWarehouse;

        Warehouse secondResource =
                (sourceHash < targetHash)
                        ? targetWarehouse
                        : sourceWarehouse;

        synchronized (firstResource) {

            synchronized (secondResource) {

                if (!sourceWarehouse.hasSufficientStock(
                        sku,
                        quantity)) {

                    System.out.println(
                            "[System Notice] Transfer cancelled: Insufficient stock.");

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
            }
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

# 6. Execution Driver

```java
public class Main {

    public static void main(String[] args) {

        System.out.println(
                "=== INITIALIZING INVENTORY SYSTEM ===");

        // Setup Strategy
        ReplenishmentStrategy strategy =
                new StandardRestockMethod();

        SynchronizedInventory inventory =
                new SynchronizedInventory(strategy);

        // Register Observers
        inventory.registerMovementListener(
                new SystemAuditLogger());

        inventory.registerAlertListener(
                new WhatsAppNotificationService());

        // Create Warehouses
        Warehouse eastWarehouse =
                new Warehouse("WH-EAST-01");

        Warehouse westWarehouse =
                new Warehouse("WH-WEST-02");

        inventory.addWarehouse(eastWarehouse);
        inventory.addWarehouse(westWarehouse);

        // Create Product
        Product coke = new Product(
                "SKU-COKE-123",
                "Coca-Cola Zero",
                1.99,
                Category.FOOD,
                5);

        System.out.println(
                "\n--- Step 1: Receiving Initial Shipments ---");

        inventory.addProduct(
                "WH-EAST-01",
                coke,
                20);

        System.out.println(
                "\n--- Step 2: Executing Warehouse Transfer ---");

        inventory.transferProductBetweenWarehouse(
                "WH-EAST-01",
                "WH-WEST-02",
                "SKU-COKE-123",
                12);

        System.out.println(
                "\n--- Step 3: Trigger Low Stock Alert ---");

        inventory.removeProduct(
                "WH-EAST-01",
                "SKU-COKE-123",
                4);

        System.out.println(
                "\n=== SYSTEM VERIFICATION COMPLETED ===");
    }
}
```
