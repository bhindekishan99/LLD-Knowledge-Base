# Elevator — Thread Safety

- **`elevators` map:** If elevators are fixed after initialization, a normal map is enough for concurrent reads. Use `ConcurrentHashMap` only if elevators can be dynamically added/removed.

- **`ElevatorSelectionStrategy`:** Strategy is stateless and only reads data, so it doesn't need synchronization itself. Ensure `Elevator` getters provide safe access to mutable state.

- **`requests`:** Request is immutable, so it is naturally safe to share between threads. Use `LinkedHashSet<Request>` to avoid duplicate requests while maintaining insertion order.

- **Thread-safe `requests`:** `LinkedHashSet` is not thread-safe, so synchronize `addRequest()`, `removeRequest()`, and `getRequests()`.

- **`getRequests()`:** Return a new snapshot (`new ArrayList<>(requests)`) so the strategy cannot modify the original set. Synchronization is still needed while creating the snapshot because another thread may modify the set simultaneously.

- **Elevator state:** `currentFloor`, `direction`, and `state` are shared mutable data, so protect state-changing operations such as `step()` and request updates using synchronization.

- **Observers:** Use `CopyOnWriteArrayList<ElevatorObserver>` because observers are frequently read/iterated but rarely added/removed.

- **`Request`:** Make `Request` immutable (`final` fields). Immutable objects are safe to share between threads.

- **Main concurrency principle:** Don't make everything concurrent by default. Identify **shared mutable state** and protect the operations that access/change it.