## Design Patterns Used in Inventory Management System

| Design Pattern | Used In | Purpose |
|----------------|---------|---------|
| **Factory** | `ProductFactory` | Encapsulates product creation logic and returns the appropriate `Product` subtype (`ElectronicsProduct`, `ClothingProduct`, `GroceryProduct`) based on `ProductCategory`. |
| **Strategy** | `ReplenishmentStrategy` (`JustInTimeStrategy`, `BulkOrderStrategy`) | Allows switching between different inventory replenishment algorithms without modifying `InventoryManager`. |
| **Singleton** | `InventoryManager` | Ensures a single instance manages all inventory operations across warehouses. |
| **Observer** | `InventoryManager` → `InventoryObserver` | Notifies subscribed observers whenever inventory events (e.g., low stock) occur. |
| **Inheritance (Polymorphism)** | `Product` → `ElectronicsProduct`, `ClothingProduct`, `GroceryProduct` | Allows different product types to share common behavior while adding product-specific attributes. |
| **State (Implicit)** | `ProductCategory`, `InventoryOperation` (Enums) | Uses enums to represent product categories and inventory operations instead of implementing the full State Pattern. |
| **Builder** | **Not Used** | Products are created using constructors and setters instead of a Builder. |
| **Command** | **Not Used** | Inventory operations are invoked directly; they are not encapsulated as command objects. |
| **Facade** | **Partially (InventoryManager)** | `InventoryManager` provides a simplified interface for inventory operations, but its primary intent is Singleton. A dedicated `InventoryFacade` would better represent the Facade pattern. |
| **Decorator** | **Not Used** | Product behavior is fixed; no dynamic addition of responsibilities. |
| **Adapter** | **Not Used** | No external systems with incompatible interfaces are integrated. |
