# Inventory Management System — Thread-Safe Design

## Complete Code

```java
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/*
 * ============================================================
 * ENUMS
 * ============================================================
 */

enum Category {
    ELECTRONICS,
    FOOD,
    FURNITURE
}


/*
 * ============================================================
 * PRODUCT
 * ============================================================
 *
 * Product represents the product definition.
 *
 * It does NOT contain:
 * - quantity
 * - thresholdQuantity
 *
 * Those are inventory-specific properties.
 */

class Product {

    private final String sku;
    private final String name;
    private final double price;
    private final Category category;

    public Product(
            String sku,
            String name,
            double price,
            Category category) {

        this.sku = sku;
        this.name = name;
        this.price = price;
        this.category = category;
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
}


/*
 * ============================================================
 * INVENTORY PRODUCT
 * ============================================================
 *
 * Represents:
 *
 *     Warehouse + Product + Quantity + Threshold
 *
 * This is the actual inventory entry.
 *
 * This object is also used as the lock for stock operations.
 */

class InventoryProduct {

    private final Product product;

    private int quantity;

    /*
     * Threshold is inventory-specific.
     *
     * Example:
     *
     * Warehouse A + Product P1 -> threshold = 10
     * Warehouse B + Product P1 -> threshold = 50
     */
    private final int thresholdQuantity;

    public InventoryProduct(
            Product product,
            int quantity,
            int thresholdQuantity) {

        this.product = product;
        this.quantity = quantity;
        this.thresholdQuantity =
                thresholdQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getThresholdQuantity() {
        return thresholdQuantity;
    }

    public void addQuantity(int quantity) {

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be positive");
        }

        this.quantity += quantity;
    }

    public void removeQuantity(int quantity) {

        if (quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be positive");
        }

        if (this.quantity < quantity) {

            throw new IllegalArgumentException(
                    "Insufficient stock");
        }

        this.quantity -= quantity;
    }

    public boolean isBelowThreshold() {

        return quantity <= thresholdQuantity;
    }
}


/*
 * ============================================================
 * WAREHOUSE
 * ============================================================
 *
 * Warehouse owns and manages InventoryProduct objects.
 *
 * Therefore Warehouse is responsible for:
 *
 * - get InventoryProduct
 * - create InventoryProduct
 * - add stock
 * - remove stock
 *
 * Inventory acts as the coordinator.
 */

class Warehouse {

    private final String warehouseId;

    /*
     * SKU -> InventoryProduct
     *
     * ConcurrentHashMap protects the map itself.
     *
     * Quantity inside InventoryProduct is NOT protected
     * by ConcurrentHashMap.
     *
     * Quantity is protected by:
     *
     *     synchronized(inventoryProduct)
     */
    private final Map<String, InventoryProduct>
            inventoryProducts =
            new ConcurrentHashMap<>();

    public Warehouse(String warehouseId) {

        this.warehouseId = warehouseId;
    }

    public String getWarehouseId() {

        return warehouseId;
    }

    /*
     * Inventory asks Warehouse for the
     * InventoryProduct.
     *
     * computeIfAbsent() makes creation atomic.
     */
    public InventoryProduct getOrCreateInventoryProduct(
            Product product,
            int thresholdQuantity) {

        return inventoryProducts.computeIfAbsent(
                product.getSku(),
                sku -> new InventoryProduct(
                        product,
                        0,
                        thresholdQuantity)
        );
    }

    public InventoryProduct getInventoryProduct(
            String sku) {

        return inventoryProducts.get(sku);
    }

    /*
     * Warehouse performs the actual stock update.
     *
     * Inventory is responsible for acquiring
     * the InventoryProduct lock before calling this.
     */
    public void addProductStock(
            InventoryProduct inventoryProduct,
            int quantity) {

        inventoryProduct.addQuantity(quantity);
    }

    /*
     * Warehouse performs the actual stock removal.
     *
     * Inventory is responsible for acquiring
     * the InventoryProduct lock before calling this.
     */
    public void removeProductStock(
            InventoryProduct inventoryProduct,
            int quantity) {

        inventoryProduct.removeQuantity(quantity);
    }
}


/*
 * ============================================================
 * STOCK MOVEMENT LISTENER
 * ============================================================
 */

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


/*
 * ============================================================
 * STOCK ALERT LISTENER
 * ============================================================
 */

interface StockAlertListener {

    void onLowStockAlert(
            String warehouseId,
            String sku,
            int currentQuantity);
}


/*
 * ============================================================
 * REPLENISHMENT STRATEGY
 * ============================================================
 */

interface ReplenishmentStrategy {

    void productReplenishment(
            Warehouse warehouse,
            Product product);
}


/*
 * ============================================================
 * STANDARD REPLENISHMENT STRATEGY
 * ============================================================
 */

class StandardRestockMethod
        implements ReplenishmentStrategy {

    @Override
    public void productReplenishment(
            Warehouse warehouse,
            Product product) {

        System.out.println(
                "[AUTO RESTOCK] Warehouse: "
                        + warehouse.getWarehouseId()
                        + " | SKU: "
                        + product.getSku());
    }
}


/*
 * ============================================================
 * WHATSAPP NOTIFICATION SERVICE
 * ============================================================
 */

class WhatsAppNotificationService
        implements StockAlertListener {

    @Override
    public void onLowStockAlert(
            String warehouseId,
            String sku,
            int currentQuantity) {

        System.out.println(
                "[WHATSAPP ALERT] Warehouse: "
                        + warehouseId
                        + " | SKU: "
                        + sku
                        + " | Current Quantity: "
                        + currentQuantity);
    }
}


/*
 * ============================================================
 * SYSTEM AUDIT LOGGER
 * ============================================================
 */

class SystemAuditLogger
        implements StockMovementListener {

    @Override
    public void onStockAdded(
            String warehouseId,
            String sku,
            int quantity) {

        System.out.println(
                "[AUDIT] STOCK ADDED | Warehouse: "
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
                "[AUDIT] STOCK REMOVED | Warehouse: "
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
                "[AUDIT] STOCK TRANSFERRED | From: "
                        + fromWarehouseId
                        + " -> To: "
                        + toWarehouseId
                        + " | SKU: "
                        + sku
                        + " | Qty: "
                        + quantity);
    }
}


/*
 * ============================================================
 * INVENTORY
 * ============================================================
 *
 * Inventory is the coordinator.
 *
 * It:
 *
 * - finds Warehouse
 * - asks Warehouse for InventoryProduct
 * - locks InventoryProduct
 * - asks Warehouse to modify stock
 * - notifies observers
 * - triggers alerts
 */

class Inventory {

    /*
     * WarehouseId -> Warehouse
     *
     * Multiple threads can access warehouses safely.
     */
    private final Map<String, Warehouse> warehouses =
            new ConcurrentHashMap<>();

    /*
     * Observers are:
     *
     * - frequently read/iterated
     * - occasionally modified
     *
     * Therefore CopyOnWriteArrayList is suitable.
     */
    private final List<StockMovementListener>
            movementListeners =
            new CopyOnWriteArrayList<>();

    private final List<StockAlertListener>
            alertListeners =
            new CopyOnWriteArrayList<>();

    private final ReplenishmentStrategy
            replenishmentStrategy;

    public Inventory(
            ReplenishmentStrategy replenishmentStrategy) {

        this.replenishmentStrategy =
                replenishmentStrategy;
    }


    /*
     * ========================================================
     * WAREHOUSE
     * ========================================================
     */

    public void addWarehouse(
            Warehouse warehouse) {

        warehouses.put(
                warehouse.getWarehouseId(),
                warehouse);
    }


    /*
     * ========================================================
     * LISTENERS
     * ========================================================
     */

    public void registerMovementListener(
            StockMovementListener listener) {

        movementListeners.add(listener);
    }

    public void registerAlertListener(
            StockAlertListener listener) {

        alertListeners.add(listener);
    }


    /*
     * ========================================================
     * ADD PRODUCT
     * ========================================================
     */

    public void addProduct(
            String warehouseId,
            Product product,
            int quantity,
            int thresholdQuantity) {

        Warehouse warehouse =
                warehouses.get(warehouseId);

        if (warehouse == null) {

            throw new IllegalArgumentException(
                    "Warehouse not found");
        }

        /*
         * Inventory does NOT create InventoryProduct itself.
         *
         * Warehouse owns InventoryProduct objects.
         *
         * Therefore Inventory asks Warehouse to
         * get/create the inventory entry.
         */
        InventoryProduct inventoryProduct =
                warehouse.getOrCreateInventoryProduct(
                        product,
                        thresholdQuantity);

        /*
         * ====================================================
         * LOCK ON INVENTORY PRODUCT
         * ====================================================
         *
         * We lock InventoryProduct because it represents, not on warehouse so in same warehouse more than one thread can
           add/remove/update diff product so more concurrency.
         * We need the following operations to be performed
         * together:
         *
         *     1. Update quantity
         *     2. Notify observers
         *     3. Check threshold
         *
         * Without synchronization, threads can interleave:
         *
         * Thread 1:
         *     quantity 10 -> 15
         *
         * Thread 2:
         *     quantity 15 -> 18
         *     notify -> 18
         *
         * Thread 1:
         *     notify -> 15
         *
         * Observer receives:
         *
         *     18 -> 15
         *
         * This can make the observer think that stock
         * went backwards.
         *
         * Therefore update + notification are performed
         * while holding the same InventoryProduct lock.
         *
         *
         * ====================================================
         * WHY NOT LOCK WAREHOUSE?
         * ====================================================
         *
         * Suppose Warehouse contains:
         *
         *     Product A
         *     Product B
         *
         * If we use:
         *
         *     synchronized(warehouse)
         *
         * then:
         *
         *     Thread 1 -> Product A -> lock Warehouse
         *     Thread 2 -> Product B -> WAIT
         *
         * Product A and Product B are independent,
         * but Thread 2 still has to wait.
         *
         * This is coarse-grained locking.
         *
         *
         * ====================================================
         * WHY INVENTORY PRODUCT?
         * ====================================================
         *
         * InventoryProduct represents exactly:
         *
         *     Warehouse + Product + Quantity
         *
         * Therefore it is the correct fine-grained
         * locking object.
         *
         *     Product A -> Lock A
         *     Product B -> Lock B
         *
         * Different products can be updated concurrently.
         */
        synchronized (inventoryProduct) {

            /*
             * Warehouse performs the actual
             * stock modification.
             */
            warehouse.addProductStock(
                    inventoryProduct,
                    quantity);

            /*
             * Notify observers while still holding
             * the same InventoryProduct lock.
             */
            movementListeners.forEach(listener ->
                    listener.onStockAdded(
                            warehouseId,
                            product.getSku(),
                            quantity));

            /*
             * Check threshold while still holding
             * the lock.
             */
            if (inventoryProduct.isBelowThreshold()) {

                triggerAlert(
                        warehouseId,
                        product.getSku(),
                        inventoryProduct.getQuantity());

                replenishmentStrategy
                        .productReplenishment(
                                warehouse,
                                product);
            }
        }
    }


    /*
     * ========================================================
     * REMOVE PRODUCT
     * ========================================================
     */

    public void removeProduct(
            String warehouseId,
            String sku,
            int quantity) {

        Warehouse warehouse =
                warehouses.get(warehouseId);

        if (warehouse == null) {

            throw new IllegalArgumentException(
                    "Warehouse not found");
        }

        /*
         * Inventory asks Warehouse for the
         * InventoryProduct.
         */
        InventoryProduct inventoryProduct =
                warehouse.getInventoryProduct(sku);

        if (inventoryProduct == null) {

            throw new IllegalArgumentException(
                    "Product not found");
        }

        /*
         * Lock only this specific:
         *
         *     Warehouse + Product
         *
         * inventory entry.
         */
        synchronized (inventoryProduct) {

            /*
             * Warehouse performs the actual removal.
             *
             * Check + remove happen under the same lock.
             */
            warehouse.removeProductStock(
                    inventoryProduct,
                    quantity);

            /*
             * Notify observers while holding
             * the same lock.
             */
            movementListeners.forEach(listener ->
                    listener.onStockRemoved(
                            warehouseId,
                            sku,
                            quantity));

            /*
             * Check low-stock condition.
             */
            if (inventoryProduct.isBelowThreshold()) {

                triggerAlert(
                        warehouseId,
                        sku,
                        inventoryProduct.getQuantity());

                replenishmentStrategy
                        .productReplenishment(
                                warehouse,
                                inventoryProduct
                                        .getProduct());
            }
        }
    }


    /*
     * ========================================================
     * TRANSFER PRODUCT
     * ========================================================
     */

    public void transferProductBetweenWarehouse(
            String fromWarehouseId,
            String toWarehouseId,
            String sku,
            int quantity) {

        Warehouse sourceWarehouse =
                warehouses.get(fromWarehouseId);

        Warehouse targetWarehouse =
                warehouses.get(toWarehouseId);

        if (sourceWarehouse == null ||
                targetWarehouse == null) {

            throw new IllegalArgumentException(
                    "Warehouse not found");
        }

        /*
         * Get source InventoryProduct.
         */
        InventoryProduct sourceProduct =
                sourceWarehouse
                        .getInventoryProduct(sku);

        if (sourceProduct == null) {

            throw new IllegalArgumentException(
                    "Product not found in source warehouse");
        }

        /*
         * Get/create destination InventoryProduct.
         *
         * Warehouse is responsible for creating it.
         */
        InventoryProduct targetProduct =
                targetWarehouse
                        .getOrCreateInventoryProduct(
                                sourceProduct.getProduct(),
                                sourceProduct
                                        .getThresholdQuantity());

        /*
         * ====================================================
         * TWO INVENTORY PRODUCTS NEED TO BE LOCKED
         * ====================================================
         *
         * Transfer changes:
         *
         *     Source  -> quantity decreases
         *     Target  -> quantity increases
         *
         * Therefore both InventoryProducts must be locked.
         *
         *
         * ====================================================
         * DEADLOCK PREVENTION
         * ====================================================
         *
         * Suppose:
         *
         * Thread 1:
         *     locks A
         *     waits for B
         *
         * Thread 2:
         *     locks B
         *     waits for A
         *
         * This can cause deadlock.
         *
         * Therefore all threads acquire the two locks
         * in a deterministic order.
         */

        InventoryProduct first;
        InventoryProduct second;

        if (fromWarehouseId
                .compareTo(toWarehouseId) < 0) {

            first = sourceProduct;
            second = targetProduct;

        } else {

            first = targetProduct;
            second = sourceProduct;
        }

        synchronized (first) {

            synchronized (second) {

                /*
                 * Source Warehouse performs removal.
                 *
                 * Check + remove are protected by
                 * sourceProduct lock.
                 */
                sourceWarehouse.removeProductStock(
                        sourceProduct,
                        quantity);

                /*
                 * Destination Warehouse performs addition.
                 *
                 * targetProduct is also locked.
                 */
                targetWarehouse.addProductStock(
                        targetProduct,
                        quantity);

                /*
                 * Notify observers after the complete
                 * transfer.
                 */
                movementListeners.forEach(listener ->
                        listener.onStockTransferred(
                                fromWarehouseId,
                                toWarehouseId,
                                sku,
                                quantity));

                /*
                 * Check source warehouse threshold.
                 */
                if (sourceProduct.isBelowThreshold()) {

                    triggerAlert(
                            fromWarehouseId,
                            sku,
                            sourceProduct.getQuantity());

                    replenishmentStrategy
                            .productReplenishment(
                                    sourceWarehouse,
                                    sourceProduct
                                            .getProduct());
                }
            }
        }
    }


    /*
     * ========================================================
     * ALERT
     * ========================================================
     */

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


/*
 * ============================================================
 * MAIN
 * ============================================================
 */

public class Main {

    public static void main(String[] args) {

        /*
         * ----------------------------------------------------
         * Products
         * ----------------------------------------------------
         *
         * Product does NOT contain threshold.
         */
        Product laptop =
                new Product(
                        "P100",
                        "Laptop",
                        1000,
                        Category.ELECTRONICS);

        Product chair =
                new Product(
                        "P200",
                        "Office Chair",
                        200,
                        Category.FURNITURE);


        /*
         * ----------------------------------------------------
         * Warehouses
         * ----------------------------------------------------
         */

        Warehouse warehouseA =
                new Warehouse("W1");

        Warehouse warehouseB =
                new Warehouse("W2");


        /*
         * ----------------------------------------------------
         * Inventory
         * ----------------------------------------------------
         */

        ReplenishmentStrategy
                replenishmentStrategy =
                new StandardRestockMethod();

        Inventory inventory =
                new Inventory(
                        replenishmentStrategy);


        inventory.addWarehouse(
                warehouseA);

        inventory.addWarehouse(
                warehouseB);


        /*
         * ----------------------------------------------------
         * Observers
         * ----------------------------------------------------
         */

        inventory.registerMovementListener(
                new SystemAuditLogger());

        inventory.registerAlertListener(
                new WhatsAppNotificationService());


        /*
         * ----------------------------------------------------
         * Add Product
         * ----------------------------------------------------
         *
         * Warehouse W1:
         *
         * Laptop:
         *     Quantity = 100
         *     Threshold = 10
         */
        inventory.addProduct(
                "W1",
                laptop,
                100,
                10);


        /*
         * Warehouse W1:
         *
         * Chair:
         *     Quantity = 50
         *     Threshold = 5
         */
        inventory.addProduct(
                "W1",
                chair,
                50,
                5);


        /*
         * Same Product can have a different
         * threshold in another Warehouse.
         *
         * Warehouse W2:
         *
         * Laptop:
         *     Quantity = 50
         *     Threshold = 20
         */
        inventory.addProduct(
                "W2",
                laptop,
                50,
                20);


        /*
         * ----------------------------------------------------
         * Remove Product
         * ----------------------------------------------------
         */

        inventory.removeProduct(
                "W1",
                "P100",
                20);


        /*
         * ----------------------------------------------------
         * Transfer Product
         * ----------------------------------------------------
         *
         * W1 -> W2
         *
         * Laptop:
         *     10 units transferred
         */
        inventory.transferProductBetweenWarehouse(
                "W1",
                "W2",
                "P100",
                10);
    }
}
```

## Responsibility Split

```text
Inventory
│
├── Finds Warehouse
├── Gets InventoryProduct from Warehouse
├── Acquires InventoryProduct lock
├── Coordinates operation
├── Notifies observers
└── Triggers alerts
        │
        ↓
Warehouse
│
├── Owns InventoryProduct collection
├── Creates/fetches InventoryProduct
├── Adds stock
└── Removes stock
        │
        ↓
InventoryProduct
│
├── Product
├── Quantity
└── Threshold
```

## Concurrency Model

```text
                    Inventory
                       |
                       ↓
                   Warehouse
                       |
                       ↓
             InventoryProduct
                       |
                    🔒 LOCK
                       |
             ┌─────────┴─────────┐
             ↓                   ↓
        Stock Update         Notification
             ↓                   ↓
        Warehouse             Observer
        operation
```

## Important Interview Points

- `ConcurrentHashMap` protects the **map**, not the `quantity` field inside `InventoryProduct`.

- `CopyOnWriteArrayList` is used for observers because the list is **read/iterated frequently and modified occasionally**.

- `InventoryProduct` is used as the lock because it represents the exact **Warehouse + Product + Quantity** relationship.

- We do **not** lock the entire `Warehouse` because that would unnecessarily serialize updates to different products.

- We do **not** lock `Product` because the same `Product` can exist in multiple warehouses, while each warehouse has its own `InventoryProduct`.

- `addProduct()` performs:

```text
get InventoryProduct
        ↓
lock InventoryProduct
        ↓
Warehouse.addProductStock()
        ↓
notify observers
        ↓
check threshold
        ↓
unlock
```

- `removeProduct()` follows the same pattern.

- `transferProductBetweenWarehouse()` locks both source and destination `InventoryProduct` objects.

- Transfer locks are acquired in deterministic order to prevent deadlock.

## Final Design

```text
Product
    │
    │ referenced by
    ↓
InventoryProduct
    ├── Product
    ├── Quantity
    └── Threshold
          ↑
          │ owned by
          │
       Warehouse
          │
          │ managed by
          ↓
       Inventory
          │
          ├── StockMovementListener
          ├── StockAlertListener
          └── ReplenishmentStrategy
```
