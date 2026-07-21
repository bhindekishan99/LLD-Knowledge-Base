# InventoryManager

## Responsibility
- Acts as the central controller for the inventory system.
- Manages warehouses, products, replenishment strategy, and inventory notifications.

## Members

```java
private static InventoryManager instance;                 // Singleton instance

private List<Warehouse> warehouses;

private ProductFactory productFactory;

private ReplenishmentStrategy replenishmentStrategy;      // Strategy Pattern

private List<InventoryObserver> observers;                // Observer Pattern
```

## Constructor

```java
private InventoryManager()

InventoryManager getInstance()
```

## Methods

```java
static InventoryManager getInstance();

void setReplenishmentStrategy(
    ReplenishmentStrategy strategy
);

void addWarehouse(Warehouse warehouse);

void removeWarehouse(Warehouse warehouse);

Product getProductBySku(String sku);

void checkAndReplenish(String sku);

void performInventoryCheck();

void notifyObservers(Product product);

void addObserver(
    InventoryObserver observer
);

void removeObserver(
    InventoryObserver observer
);
```

## Important Algorithms

### getInstance()

```text
1. If instance == null
       create InventoryManager
2. Return instance
```

---

### checkAndReplenish(sku)

```text
1. Search product using SKU.
2. If quantity <= threshold
       replenishmentStrategy.replenish(product)
3. notifyObservers(product)
```

---

### performInventoryCheck()

```text
For every warehouse
    For every product
        checkAndReplenish(product.getSku())
```

---

# Warehouse

## Responsibility

- Stores products.
- Performs add/remove/search inventory operations.

## Members

```java
private int id;

private String name;

private String location;

private Map<String, Product> products;   // SKU -> Product
```

## Constructor

```java
Warehouse(
    int id,
    String name,
    String location
)
```

## Methods

```java
void addProduct(
    Product product,
    int quantity
);

boolean removeProduct(
    String sku,
    int quantity
);

int getAvailableQuantity(
    String sku
);

Product getProductBySku(
    String sku
);

Collection<Product> getAllProducts();
```

## Important Algorithms

### addProduct(product, qty)

```text
1. Product exists?
      Yes -> increase quantity.
      No  -> add into Map.
```

---

### removeProduct(sku, qty)

```text
1. Search SKU.
2. Product not found -> return false.
3. Quantity insufficient -> return false.
4. Reduce quantity.
5. return true.
```

---

### getProductBySku(sku)

```text
return products.get(sku)
```

---

# Product

## Responsibility

- Represents a generic inventory product.
- Stores common attributes shared by all product types.

## Members

```java
private String sku;

private String name;

private double price;

private int quantity;

private int threshold;

private ProductCategory category;
```

## Constructor

```java
Abstract class

No direct object creation.
Created via subclasses
(ElectronicsProduct,
 ClothingProduct,
 GroceryProduct)
```

## Methods

```java
String getSku();
void setSku(String sku);

String getName();
void setName(String name);

double getPrice();
void setPrice(double price);

int getQuantity();
void setQuantity(int quantity);

ProductCategory getCategory();
void setCategory(ProductCategory category);

int getThreshold();
void setThreshold(int threshold);

void addStock(int quantity);      // quantity += qty

void removeStock(int quantity);   // quantity -= qty
```

## Important Algorithms

### addStock(qty)

```text
quantity += qty
```

---

### removeStock(qty)

```text
quantity -= qty
```

---

# Concrete Products

## Responsibility

Specialize Product with category-specific attributes.

---

### ElectronicsProduct

#### Members

```java
private String brand;

private int warrantyPeriod;
```

#### Constructor

```java
ElectronicsProduct(
    sku,
    name,
    price,
    quantity,
    threshold
)

// Sets category = ELECTRONICS
```

---

### ClothingProduct

#### Members

```java
private String size;

private String color;
```

#### Constructor

```java
ClothingProduct(
    sku,
    name,
    price,
    quantity,
    threshold
)

// Sets category = CLOTHING
```

---

### GroceryProduct

#### Members

```java
private Date expiryDate;

private boolean refrigerated;
```

#### Constructor

```java
GroceryProduct(
    sku,
    name,
    price,
    quantity,
    threshold
)

// Sets category = GROCERY
```

# ProductFactory

## Responsibility

- Creates the appropriate `Product` object based on `ProductCategory`.
- Encapsulates object creation logic (Factory Pattern).

## Members

```java
// No instance variables
```

## Constructor

```java
ProductFactory()
```

## Methods

```java
Product createProduct(
    ProductCategory category,
    String sku,
    String name,
    double price,
    int quantity,
    int threshold
);
```

## Important Algorithm

### createProduct(...)

```text
switch(category)

ELECTRONICS
    -> new ElectronicsProduct(...)

CLOTHING
    -> new ClothingProduct(...)

GROCERY
    -> new GroceryProduct(...)

default
    -> throw IllegalArgumentException
```

---

# ReplenishmentStrategy

## Responsibility

- Defines different inventory replenishment algorithms.
- Allows InventoryManager to switch algorithms at runtime (Strategy Pattern).

## Methods

```java
void replenish(Product product);
```

## Implementations

```text
JustInTimeStrategy

BulkOrderStrategy
```

---

# JustInTimeStrategy

## Responsibility

- Replenish only the required quantity.
- Reduces inventory holding cost.

## Methods

```java
void replenish(Product product);
```

## Important Algorithm

### replenish(product)

```text
1. Calculate required quantity.
2. Place minimum required order.
3. Update inventory.
```

---

# BulkOrderStrategy

## Responsibility

- Replenish products in bulk.
- Optimized for lower ordering cost.

## Methods

```java
void replenish(Product product);
```

## Important Algorithm

### replenish(product)

```text
1. Determine bulk order quantity.
2. Order products in large quantity.
3. Update inventory.
```

---

# InventoryObserver

## Responsibility

- Receive inventory notifications.
- Implement Observer Pattern.

## Methods

```java
void update(Product product);
```

## Implementations

```text
InventoryAlert
```

### Possible Extensions

```text
EmailNotifier

SMSNotifier

SlackNotifier

DashboardNotifier

AuditLogger
```

---

# InventoryAlert

## Responsibility

- Notify when product quantity falls below threshold.

## Constructor

```java
InventoryAlert()
```

## Methods

```java
void update(Product product);
```

## Important Algorithm

### update(product)

```text
1. Read product quantity.
2. Read threshold.
3. Display / send low-stock alert.
```

---

# ProductCategory

## Responsibility

- Represents different categories of products.

## Values

```java
ELECTRONICS

CLOTHING

GROCERY

FURNITURE

OTHER
```

---

# InventoryOperation

## Responsibility

- Represents inventory actions performed on products.

## Values

```java
ADD

REMOVE

TRANSFER

ADJUST
```

---

# Overall Flow

## Product Creation

```text
InventoryManager

        │
        ▼

ProductFactory

        │
        ▼

Concrete Product
(Electronics /
 Clothing /
 Grocery)

        │
        ▼

Warehouse.addProduct(...)
```

---

## Low Stock Flow

```text
performInventoryCheck()

        │
        ▼

checkAndReplenish()

        │
        ▼

quantity <= threshold ?

      Yes
       │
       ▼

ReplenishmentStrategy

       │
       ▼

Inventory Updated

       │
       ▼

notifyObservers()

       │
       ▼

InventoryAlert
```

---

## Runtime Strategy Change

```text
InventoryManager

        │

setReplenishmentStrategy()

        │
        ▼

JustInTimeStrategy

        OR

BulkOrderStrategy
```

---

# Complete Object Relationship

```text
                InventoryManager
                       │
     ┌─────────────────┼──────────────────┐
     │                 │                  │
     ▼                 ▼                  ▼
Warehouse        ProductFactory     ReplenishmentStrategy
     │                 │                  ▲
     │                 ▼                  │
     │            Product (abstract)      │
     │                 ▲                  │
     │        ┌────────┼────────┐          │
     │        │        │        │          │
     ▼        ▼        ▼        ▼          │
Products  Electronics Clothing Grocery     │
     │                                     │
     └──────────────► InventoryManager ◄───┘
                              │
                              ▼
                     InventoryObserver
                              │
                              ▼
                      InventoryAlert
```
