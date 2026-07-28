## Design Patterns Used

| Design Pattern | Where Used | Why Used |
|---|---|---|
| **Strategy Pattern** | `DeliveryStrategy` → `SequentialDeliveryStrategy` | Encapsulates different notification delivery algorithms. New strategies such as parallel, retry, or first-success can be added without changing `NotificationService`. |
| **Observer Pattern (Conceptually)** | `Client` publishes notifications that are delivered to its subscribed `Subscriber`s | A client has multiple subscribers interested in its notifications. `SubscriptionRegistry` maintains these subscriptions and `NotificationService` delivers events to them. |
| **Composite-like Composition** | `SequentialDeliveryStrategy` contains `List<NotificationChannel>` | Combines multiple notification channels such as SMS, Email, and Phone into one delivery strategy and executes them as a group. |
