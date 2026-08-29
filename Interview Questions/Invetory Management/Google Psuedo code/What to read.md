Note: Insted of having Product and Quantity map in warehouse we can use **Has-A relationship**, Inventory has Product then 
we have Quantity, minimumThresholdQuantity

**InventoryProduct** | Replace separate product and quantity maps with an `InventoryProduct` entity containing `Product` + `quantity` (and other inventory-specific attributes).

**Warehouse** | Maintain a single `Map<String, InventoryProduct>` so the product-stock relationship and related data stay together.

**What and How to read**
1. Design without threading: google code without lock.md
2. Concept of thread: Google code with thread safty Concept.md
3. Thread safty using **synchronized keyword**: Google code Synchonized keyword.md
4. Thread safty using **ReentrantLock**: Google code-ReentrantLock.md
