# Mediator Design Pattern

> **Category:** Behavioral Design Pattern

---

# 1. Definition

The **Mediator Pattern** is a behavioral design pattern that introduces a **Mediator** object to centralize communication between multiple objects. Instead of objects communicating directly with each other, they communicate through the mediator.

### One-Line Summary

> **Move communication logic from individual objects to a dedicated coordinator.**

---

# 2. Problem

Suppose we are building an **Online Auction System**.

There are multiple bidders participating in an auction.

Whenever one bidder places a bid, every other bidder should immediately know about it.

For example,

* Alice
* Bob
* Charlie
* David

If Alice places a bid of **₹100**, Bob, Charlie and David should receive a notification.

At first glance, this seems like a simple requirement.

Let's solve it in the most straightforward way.

---

# 3. Traditional Approach

## Why This Approach?

Imagine this is the first version of the application.

There are only a few bidders.

A natural solution would be:

* Every bidder knows all other bidders.
* Whenever someone places a bid, it directly notifies everyone else.
* No additional classes are required.

This solution is simple, intuitive and perfectly acceptable for a small application.

---

## Design

Each bidder stores references to all other bidders.

Whenever a bidder places a bid, it directly communicates with the remaining bidders.

```text
                 Alice
               /   |   \
              /    |    \
             ▼     ▼     ▼
           Bob  Charlie David
```

Communication happens directly between bidders.

---

## Traditional UML

```text
+----------------------+
|       Bidder         |
+----------------------+
| - name : String      |
| - bidders : List     |
+----------------------+
| + placeBid()         |
| + receiveBid()       |
| + setBidders()       |
+----------------------+

Every Bidder stores references to all other Bidder objects.
```

<details>
<summary>💡 Understanding the UML</summary>

### Visibility

| Symbol | Meaning   |
| ------ | --------- |
| `+`    | Public    |
| `-`    | Private   |
| `#`    | Protected |

### Reading this UML

The `Bidder` class contains a list of other bidders.

This means every bidder knows every other bidder.

Whenever communication is required, bidders communicate directly with each other.

</details>

---

## Implementation

```java
import java.util.List;

public class Bidder {

    private final String name;
    private List<Bidder> bidders;

    public Bidder(String name) {
        this.name = name;
    }

    // Registers all bidders participating in the auction.
    public void setBidders(List<Bidder> bidders) {
        this.bidders = bidders;
    }

    // Places a bid and directly notifies other bidders.
    public void placeBid(int bidAmount) {

        System.out.println(name + " placed a bid of ₹" + bidAmount);

        for (Bidder bidder : bidders) {

            if (bidder != this) {
                bidder.receiveBidNotification(this, bidAmount);
            }

        }

    }

    public void receiveBidNotification(Bidder bidder, int bidAmount) {

        System.out.println(
                name + " received notification: "
                + bidder.name
                + " placed a bid of ₹"
                + bidAmount);

    }

}
```

### Client Code

```java
import java.util.List;

public class Main {

    public static void main(String[] args) {

        Bidder alice = new Bidder("Alice");
        Bidder bob = new Bidder("Bob");
        Bidder charlie = new Bidder("Charlie");
        Bidder david = new Bidder("David");

        List<Bidder> bidders = List.of(alice, bob, charlie, david);

        alice.setBidders(bidders);
        bob.setBidders(bidders);
        charlie.setBidders(bidders);
        david.setBidders(bidders);

        alice.placeBid(100);

    }

}
```

### Output

```text
Alice placed a bid of ₹100
Bob received notification: Alice placed a bid of ₹100
Charlie received notification: Alice placed a bid of ₹100
David received notification: Alice placed a bid of ₹100
```

---

## Execution Flow

```text
Alice.placeBid(100)
        │
        ▼
Loop through all bidders
        │
        ├────────► Bob.receiveBidNotification()
        ├────────► Charlie.receiveBidNotification()
        └────────► David.receiveBidNotification()
```

The bidder who places the bid is responsible for notifying every other bidder.

---

## Why This Approach Is Good

Many tutorials immediately call this design "bad."

It isn't.

For a small application, this solution has several advantages.

* Easy to understand.
* Easy to implement.
* Requires very few classes.
* No unnecessary abstractions.
* Perfectly suitable when requirements are small and unlikely to change.

> **A good design solves today's problem without introducing unnecessary complexity.**

Design patterns should be introduced only when the current design starts becoming difficult to maintain.

---

# 4. Traditional Approach Starts Growing

As the application becomes successful, new requirements begin to arrive.

Instead of only notifying bidders, the system now needs to:

* Store every bid in the database.
* Log every bid.
* Send email notifications.
* Notify only active bidders.
* Reject bids after the auction ends.

Where should this logic go?

Most developers naturally add it inside `placeBid()`.

Slowly, the method starts becoming larger.

At this point, the `Bidder` class is no longer responsible for only bidding.

It has slowly become responsible for communication, logging, validation and notification.

This is usually the first sign that the design needs improvement.

---

# 5. Why This Design Starts Breaking

The current design worked well when communication was simple.

However, as more communication-related requirements are added, the `Bidder` class keeps growing.

Ask yourself,

> **Should a bidder really be responsible for all these tasks?**

Probably not.

A bidder's primary responsibility is to participate in the auction.

Communication, logging, validation and notifications are separate responsibilities.

Let's identify the problems.

---

# 6. Problems With The Current Design

## 1. Too Many Responsibilities

Initially, a bidder only had one responsibility.

> Place a bid.

Now it is also responsible for:

* Finding other bidders.
* Notifying them.
* Logging bids.
* Sending emails.
* Applying communication rules.

The class has slowly become responsible for multiple unrelated tasks.

---

## 2. Tight Coupling

Every bidder knows every other bidder.

```text
Alice
 ├── Bob
 ├── Charlie
 └── David
```

As more bidders join the auction,

every bidder becomes aware of more objects.

This increases coupling.

---

## 3. Hard To Maintain

Suppose tomorrow the notification process changes.

For example,

* Notify only premium users.
* Notify only active bidders.
* Send Email before SMS.
* Delay notifications by 5 seconds.

Where should these changes be made?

Inside the `Bidder` class.

Although these are communication-related changes, we still have to modify the bidder.

---

## 4. Difficult To Extend

Imagine adding another feature.

> Store every bid in an audit log.

Again,

the `Bidder` class changes.

Next requirement,

> Reject bids after auction timeout.

Again,

the `Bidder` class changes.

The class keeps changing for reasons unrelated to bidding.

---

# 7. Design Principles Being Violated

## Single Responsibility Principle (SRP)

A bidder should have one primary responsibility.

> Participate in the auction.

Communication is a separate responsibility and should belong somewhere else.

---

## Open-Closed Principle (OCP)

Every new communication rule requires modifying the existing `Bidder` class.

Instead of extending the design,

we keep changing the same class repeatedly.

---

# 8. How To Think Towards The Mediator Pattern

Don't think about the design pattern yet.

Think like a software engineer.

Ask yourself one simple question.

> **Should a bidder really coordinate communication?**

Probably not.

A bidder should only know how to:

* Place a bid.
* Receive bid notifications.

It shouldn't decide:

* Who should be notified.
* When they should be notified.
* Whether the auction has ended.
* Which bidder is currently the highest bidder.
* Whether emails should be sent.

These responsibilities belong somewhere else.

Now ask another question.

> **If bidders shouldn't coordinate communication, then who should?**

We need one dedicated object whose only responsibility is coordinating communication between bidders.

This object can:

* Receive bids.
* Notify bidders.
* Apply communication rules.
* Validate auction state.
* Maintain the highest bid.
* Support future communication-related features.

Instead of this,

```text
Alice ─────► Bob
   │
   ├──────► Charlie
   │
   └──────► David
```

we move to,

```text
              ?
              ▲
              │
 Alice ───────┤
 Bob ─────────┤
 Charlie ─────┤
 David ───────┘
```

There should be **one object in the middle** responsible for all communication.

That object is called the **Mediator**.

---

# 9. Mediator Pattern

The **Mediator Pattern** introduces a dedicated object that centralizes communication between multiple objects.

Instead of bidders communicating directly with each other,

* Every bidder communicates only with the mediator.
* The mediator decides how communication should happen.

As a result,

* Bidders no longer know about each other.
* Communication logic is centralized.
* Future communication-related changes happen in one place.

The relationship changes from this:

```text
Alice ─────► Bob
   │
   ├──────► Charlie
   │
   └──────► David
```

to this:

```text
             AuctionHouse
            /      |      \
           /       |       \
       Alice      Bob    Charlie
                     \
                    David
```

Now every bidder knows only **one object**—the mediator.

---

# 10. Components

| Component           | Type               | Why do we need it?                                                                                                       |
| ------------------- | ------------------ | ------------------------------------------------------------------------------------------------------------------------ |
| **AuctionMediator** | **Interface**      | Defines a common communication contract so bidders don't depend on a specific mediator implementation.                   |
| **AuctionHouse**    | **Concrete Class** | Implements the communication contract and coordinates all communication between bidders.                                 |
| **Bidder**          | **Concrete Class** | Represents an auction participant. It only places bids and receives notifications instead of coordinating communication. |

---

# 11. UML Comparison

## Traditional Approach

```text
+----------------------+
|       Bidder         |
+----------------------+
| - name : String      |
| - bidders : List     |
+----------------------+
| + placeBid()         |
| + receiveBid()       |
| + setBidders()       |
+----------------------+

Every bidder knows every other bidder.
```

↓

## Mediator Pattern

```text
                         +-------------------------+
                         |    AuctionMediator      |
                         +-------------------------+
                         | + addBidder()           |
                         | + placeBid()            |
                         +-----------▲-------------+
                                     |
                         +-----------+-------------+
                         |      AuctionHouse       |
                         +-------------------------+
                         | - bidders              |
                         +-------------------------+
                         | + addBidder()          |
                         | + placeBid()           |
                         +-----------▲-------------+
                                     |
                  -----------------------------------------
                  |                |                     |
            +-----+------+   +-----+------+     +--------+------+
            |   Bidder   |   |   Bidder   |     |    Bidder     |
            +------------+   +------------+     +---------------+
            | - mediator |   | - mediator |     | - mediator    |
            +------------+   +------------+     +---------------+
```

### What Changed?

| Traditional Approach                    | Mediator Pattern                    |
| --------------------------------------- | ----------------------------------- |
| Bidder knows every bidder.              | Bidder knows only the mediator.     |
| Communication is distributed.           | Communication is centralized.       |
| High coupling.                          | Loose coupling.                     |
| Every bidder coordinates communication. | Mediator coordinates communication. |

<details>
<summary>💡 Understanding the UML Diagram</summary>

### Visibility

| Symbol | Meaning          |
| ------ | ---------------- |
| `+`    | Public Member    |
| `-`    | Private Member   |
| `#`    | Protected Member |

### Reading the UML

The arrow from `Bidder` to `AuctionMediator` means:

* Bidder stores a reference to the mediator.
* Bidder invokes methods on the mediator.
* Bidder does **not** know about other bidders.

Communication now flows through the mediator instead of directly between bidders.

</details>

---

# 12. Implementation

## AuctionMediator.java

```java
public interface AuctionMediator {

    void addBidder(Bidder bidder);

    void placeBid(Bidder bidder, int bidAmount);

}
```

<details>
<summary>💡 Why do we need an interface?</summary>

A common question is:

> **Why not directly store an `AuctionHouse` object inside `Bidder`?**

Initially, we have only one implementation.

```java
public class Bidder {

    private final AuctionHouse auctionHouse;

    public Bidder(String name, AuctionHouse auctionHouse) {
        this.name = name;
        this.auctionHouse = auctionHouse;
    }

}
```

This works perfectly.

---

### New Requirement

After a few months, the business introduces two new auction types.

* Silent Auction
* Reverse Auction

Now we have multiple implementations.

```java
class AuctionHouse { }

class SilentAuctionHouse { }

class ReverseAuctionHouse { }
```

Our `Bidder` class is tightly coupled to `AuctionHouse`.

```java
private final AuctionHouse auctionHouse;
```

If we want to use `SilentAuctionHouse`, we must modify the `Bidder` class.

```java
// Old
private final AuctionHouse auctionHouse;

// New
private final SilentAuctionHouse auctionHouse;
```

If another auction type is introduced later, we'll modify the `Bidder` class again.

This means the `Bidder` class changes whenever a new mediator implementation is added.

---

### Better Solution

Instead, define a common interface.

```java
public interface AuctionMediator {

    void addBidder(Bidder bidder);

    void placeBid(Bidder bidder, int bidAmount);

}
```

Now every auction implementation follows the same contract.

```java
class AuctionHouse implements AuctionMediator { }

class SilentAuctionHouse implements AuctionMediator { }

class ReverseAuctionHouse implements AuctionMediator { }
```

The `Bidder` now depends only on the interface.

```java
private final AuctionMediator mediator;
```

The client decides which implementation to provide.

```java
AuctionMediator mediator = new AuctionHouse();

// or

AuctionMediator mediator = new SilentAuctionHouse();

// or

AuctionMediator mediator = new ReverseAuctionHouse();

Bidder bidder = new Bidder("Alice", mediator);
```

Notice that the `Bidder` class never changes.

Only the object passed to it changes.

This follows the **Dependency Inversion Principle (DIP)**.

> **High-level modules should depend on abstractions, not concrete implementations.**

The `Bidder` doesn't care which auction implementation it receives.

It only cares that the object follows the `AuctionMediator` contract.

</details>

---

## AuctionHouse.java

```java
import java.util.ArrayList;
import java.util.List;

// Coordinates communication between all bidders.
public class AuctionHouse implements AuctionMediator {

    private final List<Bidder> bidders = new ArrayList<>();

    @Override
    public void addBidder(Bidder bidder) {
        bidders.add(bidder);
    }

    @Override
    public void placeBid(Bidder bidder, int bidAmount) {

        System.out.println(
                bidder.getName() + " placed a bid of ₹" + bidAmount);

        for (Bidder currentBidder : bidders) {

            if (currentBidder != bidder) {
                currentBidder.receiveBidNotification(
                        bidder,
                        bidAmount);
            }

        }

    }

}
```

---

## Bidder.java

```java
// Represents an auction participant.
public class Bidder {

    private final String name;
    private final AuctionMediator mediator;

    public Bidder(String name, AuctionMediator mediator) {
        this.name = name;
        this.mediator = mediator;
    }

    public String getName() {
        return name;
    }

    // Places a bid through the mediator.
    public void placeBid(int bidAmount) {
        mediator.placeBid(this, bidAmount);
    }

    // Receives notification when another bidder places a bid.
    public void receiveBidNotification(Bidder bidder, int bidAmount) {

        System.out.println(
                name + " received notification: "
                + bidder.getName()
                + " placed a bid of ₹"
                + bidAmount);

    }

}
```

### Client Code

```java
public class Main {

    public static void main(String[] args) {

        AuctionMediator auctionHouse = new AuctionHouse();

        Bidder alice = new Bidder("Alice", auctionHouse);
        Bidder bob = new Bidder("Bob", auctionHouse);
        Bidder charlie = new Bidder("Charlie", auctionHouse);
        Bidder david = new Bidder("David", auctionHouse);

        auctionHouse.addBidder(alice);
        auctionHouse.addBidder(bob);
        auctionHouse.addBidder(charlie);
        auctionHouse.addBidder(david);

        alice.placeBid(100);

    }

}
```

### Output

```text
Alice placed a bid of ₹100
Bob received notification: Alice placed a bid of ₹100
Charlie received notification: Alice placed a bid of ₹100
David received notification: Alice placed a bid of ₹100
```

---

# 13. Execution Flow

```text
Alice.placeBid(100)
        │
        ▼
Bidder.placeBid()
        │
        ▼
AuctionMediator.placeBid()
        │
        ▼
AuctionHouse receives the bid
        │
        ▼
Iterates through all registered bidders
        │
        ├────────► Bob.receiveBidNotification()
        ├────────► Charlie.receiveBidNotification()
        └────────► David.receiveBidNotification()
```

### Communication Flow Comparison

#### Traditional Approach

```text
Alice
   │
   ├────────► Bob
   ├────────► Charlie
   └────────► David
```

Here, Alice is responsible for coordinating communication.

---

#### Mediator Pattern

```text
Alice
   │
   ▼
AuctionHouse
   │
   ├────────► Bob
   ├────────► Charlie
   └────────► David
```

Here, Alice only forwards the request to the mediator.

The mediator becomes responsible for all communication.

---

# 14. Design Decisions

This section explains **why** the implementation is designed this way.

---

## Why does `Bidder` store a reference to `AuctionMediator`?

A bidder only has one responsibility:

> **Participate in the auction.**

To place a bid, it only needs to know **where to send the request**.

It doesn't need to know:

* Who else is participating.
* How notifications are sent.
* Which bidders should receive notifications.
* Whether the auction has ended.

It simply forwards the request to the mediator.

This keeps the bidder focused on its own responsibility.

---

## Why does `AuctionHouse` maintain the list of bidders?

Someone in the system must know:

* Who is participating.
* Who should receive notifications.
* How communication should happen.

Since communication is the mediator's responsibility, it naturally owns this information.

If tomorrow the business says:

* Notify only premium bidders.
* Notify only active bidders.
* Ignore blocked bidders.

only the mediator changes.

The `Bidder` class remains untouched.

---

## Why move communication into the mediator?

Communication is a **system responsibility**, not an individual bidder's responsibility.

If every bidder coordinates communication,

every bidder must change whenever communication rules change.

By moving communication into one place,

all communication-related changes become localized.

---

## Why doesn't `Bidder` know about other bidders?

Because it never needs that information.

Its responsibilities are only:

* Place a bid.
* Receive notifications.

Knowing every other bidder only increases coupling.

The mediator already knows all participants and can coordinate communication.

---

## Why use an Interface?

Today we have only one implementation.

```text
AuctionHouse
```

Tomorrow we might introduce:

```text
AuctionHouse

SilentAuctionHouse

ReverseAuctionHouse
```

If `Bidder` depends directly on `AuctionHouse`,

it must change whenever we switch implementations.

Instead,

`Bidder` depends on the `AuctionMediator` interface.

Now only the object passed by the client changes.

The `Bidder` class remains unchanged.

This follows the **Dependency Inversion Principle (DIP)**.

---

# 15. Advantages

* Reduces coupling between collaborating objects.
* Centralizes communication in one place.
* Keeps individual classes focused on their own responsibility.
* New communication rules usually require changes only in the mediator.
* Makes the system easier to extend and maintain.
* Makes objects easier to reuse because they don't depend on each other directly.


---

# 16. Disadvantages

* The mediator can become very large if too many responsibilities are added.
* A poorly designed mediator can become a **God Object**.
* Too much business logic inside the mediator can make it difficult to maintain.
* Debugging sometimes requires tracing requests through the mediator.

---

# 17. Before vs After

| Traditional Approach                                 | Mediator Pattern                                     |
| ---------------------------------------------------- | ---------------------------------------------------- |
| Objects communicate directly.                        | Objects communicate through a mediator.              |
| High coupling.                                       | Loose coupling.                                      |
| Communication logic is distributed.                  | Communication logic is centralized.                  |
| Every object coordinates communication.              | Mediator coordinates communication.                  |
| Every communication change affects multiple classes. | Most communication changes affect only the mediator. |

---

# 18. Real-World Examples

* Online Auction System
* Air Traffic Control System
* Chat Room
* Railway Traffic Control
* Smart Home Hub
* Flight Control Tower
* Customer Support Chat System
* UI Dialog Box coordinating multiple UI components

---

# 19. When Should You Think of the Mediator Pattern?

Consider using the Mediator Pattern when:

* Many objects communicate with each other.
* Objects are becoming tightly coupled.
* Communication rules keep changing.
* Communication logic is duplicated across multiple classes.
* You want one place to manage all interactions.

A simple question to ask yourself is:

> **"Are my objects spending more time talking to each other than doing their own work?"**

If the answer is **Yes**, consider introducing a mediator.

---

# 20. Interview Questions

### Why is Mediator a Behavioral Design Pattern?

Because it defines **how objects communicate and collaborate**.

---

### Why not let objects communicate directly?

Direct communication creates tight coupling.

As the number of objects grows,

communication becomes increasingly difficult to maintain.

The mediator centralizes that communication.

---

### How is Mediator different from Observer?

| Mediator                                    | Observer                                            |
| ------------------------------------------- | --------------------------------------------------- |
| Communication is coordinated by one object. | One object broadcasts events to multiple observers. |
| Objects communicate through the mediator.   | Observers subscribe to a publisher.                 |
| Focuses on coordinating interactions.       | Focuses on notifying state changes.                 |

---

### Can the mediator become a problem?

Yes.

If every new feature is added to the mediator,

it can become a **God Object**.

The mediator should coordinate communication,

not own unrelated business logic.

---

### Which SOLID principles does Mediator improve?

* **Single Responsibility Principle (SRP)**
* **Open-Closed Principle (OCP)**
* **Dependency Inversion Principle (DIP)**

---

# 21. Extending the Design

One of the biggest advantages of the Mediator Pattern is that **new communication-related features are usually added by extending or modifying the mediator instead of changing every participant.**

Suppose the Product Owner gives the following requirements:

* Log every bid.
* Reject bids after the auction ends.
* Notify only premium bidders.

Should we modify `Bidder`?

**No.**

The bidder's responsibility hasn't changed.

Instead,

extend the mediator.

```java
public class ExtendedAuctionHouse extends AuctionHouse {

    private final long biddingEndTime;

    public ExtendedAuctionHouse(long biddingEndTime) {
        this.biddingEndTime = biddingEndTime;
    }

    @Override
    public void placeBid(Bidder bidder, int bidAmount) {

        if (System.currentTimeMillis() > biddingEndTime) {
            System.out.println("Bidding time is over.");
            return;
        }

        System.out.println("LOG: "
                + bidder.getName()
                + " placed a bid of ₹"
                + bidAmount);

        super.placeBid(bidder, bidAmount);

    }

}
```

Notice what changed.

Only the mediator changed.

The `Bidder` class remained untouched.

This demonstrates one of the biggest strengths of the Mediator Pattern:

> **Communication evolves by changing one central coordinator instead of every participating object.**

---

# 22. Summary

The traditional approach works well for small applications,

but as communication rules grow,

every participating object becomes harder to maintain.

The Mediator Pattern solves this by introducing a dedicated coordinator.

Instead of objects communicating directly,

they communicate through the mediator.

As a result:

* Communication is centralized.
* Coupling is reduced.
* Classes become easier to understand.
* Future communication changes happen in one place.

---

# 23. Quick Revision

## Intent

Move communication logic from individual objects to a dedicated coordinator.

---

## Core Idea

```text
Traditional

Object ─────► Object

Mediator

Object ─────► Mediator ─────► Object
```

---

## Components

* **AuctionMediator** → Communication contract.
* **AuctionHouse** → Coordinates communication.
* **Bidder** → Places bids through the mediator.

---

## Key Benefits

* Loose Coupling
* Centralized Communication
* Better Maintainability
* Easier Extensibility

---

## Think of Mediator When

* Objects communicate with many other objects.
* Communication rules keep changing.
* Communication itself becomes a separate responsibility.

> **If objects are talking to each other too much, introduce someone in the middle to coordinate the conversation.**





