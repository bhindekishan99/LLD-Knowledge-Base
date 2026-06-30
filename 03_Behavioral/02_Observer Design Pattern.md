# Observer Design Pattern

> **Category:** Behavioral Design Pattern

---

# 1. Definition

The **Observer Pattern** is a behavioral design pattern where one object (called the **Subject**) automatically notifies multiple dependent objects (called **Observers**) whenever its state changes.

### One-Line Summary

> **When one object changes, automatically notify all interested objects.**

---

# 2. Problem

Suppose we are building a **YouTube Notification System**.

There is a YouTube channel named **CodeWithAryan**.

Many users subscribe to this channel.

For example,

- Alice
- Bob
- Charlie
- David

Whenever the channel uploads a new video, all subscribers should automatically receive a notification.

For example,

```
CodeWithAryan uploads:
"Observer Design Pattern"

↓

Alice gets notified
Bob gets notified
Charlie gets notified
David gets notified
```

At first glance, this looks like a simple requirement.

Let's solve it using the most straightforward approach.

---

# 3. Traditional Approach

## Why This Approach?

Imagine this is the first version of the application.

There is only one YouTube channel and only a few subscribers.

A natural solution would be:

- The YouTube channel stores all subscribers.
- Whenever a new video is uploaded, it directly notifies every subscriber.
- No additional interfaces or classes are required.

This solution is simple, intuitive and perfectly acceptable for a small application.

---

## Design

The YouTube channel stores references to all subscribers.

Whenever a new video is uploaded, it directly calls every subscriber.

```text
                 YouTube Channel
               /       |       \
              /        |        \
             ▼         ▼         ▼
         Alice       Bob     Charlie
                          \
                           ▼
                         David
```

Communication happens directly from the channel to every subscriber.

---

## Traditional UML

```text
+--------------------------------+
|        YouTubeChannel          |
+--------------------------------+
| - subscribers : List           |
+--------------------------------+
| + uploadVideo()                |
| + notifySubscribers()          |
+--------------------------------+

                ▲
                │
      -------------------------
      │         │            │
+-----------+ +-----------+ +-----------+
|Subscriber | |Subscriber | |Subscriber |
+-----------+ +-----------+ +-----------+
| - name    | | - name    | | - name    |
+-----------+ +-----------+ +-----------+
| + notify()| | + notify()| | + notify()|
+-----------+ +-----------+ +-----------+
```

<details>
<summary>💡 Understanding the UML</summary>

### Visibility

| Symbol | Meaning |
|---------|---------|
| `+` | Public |
| `-` | Private |

### Reading this UML

The channel stores all subscribers.

Whenever a video is uploaded, the channel iterates through the list and notifies everyone.

</details>

---

## Implementation

```java
import java.util.ArrayList;
import java.util.List;

public class Subscriber {

    private final String name;

    public Subscriber(String name) {
        this.name = name;
    }

    public void notify(String videoTitle) {
        System.out.println(name + " received notification : " + videoTitle);
    }

}

public class YouTubeChannel {

    private final List<Subscriber> subscribers = new ArrayList<>();

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void uploadVideo(String title) {

        System.out.println("Uploaded : " + title);

        for (Subscriber subscriber : subscribers) {
            subscriber.notify(title);
        }

    }

}
```

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        YouTubeChannel channel = new YouTubeChannel();

        channel.addSubscriber(new Subscriber("Alice"));
        channel.addSubscriber(new Subscriber("Bob"));
        channel.addSubscriber(new Subscriber("Charlie"));
        channel.addSubscriber(new Subscriber("David"));

        channel.uploadVideo("Observer Design Pattern");

    }

}
```

## Output

```text
Uploaded : Observer Design Pattern
Alice received notification : Observer Design Pattern
Bob received notification : Observer Design Pattern
Charlie received notification : Observer Design Pattern
David received notification : Observer Design Pattern
```

---

## Execution Flow

```text
uploadVideo()

      │

      ▼

Loop through all subscribers

      │

      ├────► Alice.notify()

      ├────► Bob.notify()

      ├────► Charlie.notify()

      └────► David.notify()
```

---

## Why This Approach Is Good

This design is perfectly fine when:

- There is only one notification type.
- Subscribers are simple.
- Business rules are unlikely to change.

It is easy to understand, easy to implement and solves today's problem without introducing unnecessary complexity.

> **A good design solves today's problem, not tomorrow's imaginary problems.**

---

# 4. Traditional Approach Starts Growing

As the application becomes popular, new requirements arrive.

Instead of simply notifying subscribers, the system now needs to:

- Email subscribers.
- Send Push Notifications.
- Send SMS notifications.
- Allow users to unsubscribe.
- Notify only Premium subscribers.
- Notify subscribers based on language.
- Notify subscribers based on notification preferences.

Most developers naturally keep adding code inside `uploadVideo()`.

Slowly, it becomes something like this.

```java
public void uploadVideo(String title) {

    // Save video

    // Validate upload

    // Send Email

    // Send Push Notification

    // Send SMS

    // Notify Premium Users

    // Notify Free Users

    // Update Analytics

    // More business rules...
}
```

At this point,

the `YouTubeChannel` class is no longer responsible for just uploading videos.

It has slowly become responsible for managing every notification-related feature.

This is usually the first sign that the design needs improvement.

---

# 5. Why This Design Starts Breaking

The current design works well as long as notification is simple.

However, as more notification-related requirements are added, the `YouTubeChannel` class keeps growing.

Ask yourself,

> **Should a YouTube channel really be responsible for every type of notification?**

Probably not.

A YouTube channel's primary responsibility is to publish videos.

Email notifications, push notifications, SMS notifications and subscriber management are different responsibilities.

Let's identify the problems.

---

# 6. Problems With The Current Design

## 1. Too Many Responsibilities

Initially, the channel had only one responsibility.

> Upload videos.

Now it is also responsible for:

- Managing subscribers.
- Sending notifications.
- Sending emails.
- Sending push notifications.
- Sending SMS notifications.
- Applying notification rules.

The class has slowly become responsible for multiple unrelated tasks.

---

## 2. Tight Coupling

The YouTube channel directly knows every subscriber.

```text
YouTube Channel
      │
      ├── Alice
      ├── Bob
      ├── Charlie
      └── David
```

As more subscribers join,

the channel becomes increasingly dependent on subscriber implementations.

---

## 3. Hard To Maintain

Suppose tomorrow the notification process changes.

For example,

- Notify only Premium users.
- Notify users based on language.
- Send Email before Push Notification.
- Delay notifications by 5 minutes.

Where should these changes be made?

Inside the `YouTubeChannel` class.

Although these are notification-related changes, we still modify the channel.

---

## 4. Difficult To Extend

Imagine adding another notification mechanism.

> WhatsApp Notification

Again,

the `YouTubeChannel` class changes.

Next requirement,

> Slack Notification

Again,

the `YouTubeChannel` class changes.

The class keeps changing for reasons unrelated to publishing videos.

---

# 7. Design Principles Being Violated

## Single Responsibility Principle (SRP)

A YouTube channel should have one primary responsibility.

> Publish videos.

Notification is a separate responsibility.

---

## Open-Closed Principle (OCP)

Every new notification type requires modifying the existing `YouTubeChannel` class.

Instead of extending the design,

we keep changing the same class repeatedly.

## Dependency Inversion Principle (DIP)

The YoutubeChannel class has direct dependencies on concrete implementations(Subscriber)

Tight coupling to specific implementations: The YoutubeChannel class is tightly coupled to the specific implementations(Subscriber), making it difficult to change or replace them.

---

# 8. How To Think Towards The Observer Pattern

Don't think about the design pattern yet.

Think like a software engineer.

Ask yourself one simple question.

> **Should the YouTube channel know how every subscriber wants to be notified?**

Probably not.

The channel should only know one thing.

> **A new video has been uploaded.**

What happens after that should not be the channel's concern.

Now ask another question.

> **Who is interested in this event?**

The subscribers.

Instead of the channel manually coordinating notifications,

what if subscribers simply register their interest?

Then whenever a new video is uploaded,

the channel only announces:

> **"A new video is available."**

Every interested subscriber reacts automatically.

This changes the relationship from:

```text
Channel

   │

   ├────► Alice

   ├────► Bob

   ├────► Charlie

   └────► David
```

to

```text
           Subscribers

Alice

Bob        ▲

Charlie    │

David      │

           │

      YouTube Channel
```

Subscribers register themselves with the channel.

Whenever the channel's state changes,

it simply informs all registered subscribers.

The subscribers decide how to react.

This is exactly the idea behind the **Observer Pattern**.

---

# 9. Observer Pattern

The **Observer Pattern** establishes a **one-to-many dependency** between objects.

When the **Subject** changes its state,

all registered **Observers** are automatically notified.

Instead of the subject knowing how every observer behaves,

it simply broadcasts the event.

Each observer reacts independently.

As a result,

- The subject becomes independent of concrete observers.
- New observers can be added without modifying the subject.
- Notification logic becomes flexible and extensible.

---

# 10. Components

| Component | Type | Why do we need it? |
|-----------|------|--------------------|
| **Subscriber** | **Interface** | Defines a common notification contract. The subject notifies all subscribers without knowing their concrete implementation. |
| **YouTubeSubscriber** | **Concrete Class** | Represents a subscriber interested in channel updates. |
| **Channel** | **Interface** | Defines operations for registering, removing and notifying subscribers. |
| **YouTubeChannel** | **Concrete Class** | Maintains subscribers and broadcasts notifications whenever a new video is uploaded. |

---

# 11. UML Comparison

## Traditional Approach

```text
               YouTube Channel
              /      |      \
             /       |       \
        Alice      Bob    Charlie
                         \
                          David
```

The channel directly communicates with every subscriber.

↓

## Observer Pattern

```text
                  +----------------------+
                  |      Channel         |
                  +----------------------+
                  | + subscribe()        |
                  | + unsubscribe()      |
                  | + notifySubscribers()|
                  +----------▲-----------+
                             |
                  +----------+-----------+
                  |   YouTubeChannel     |
                  +----------------------+
                  | - subscribers        |
                  +----------------------+

                             ▲
                             │
                    stores List<Subscriber>

                             │

        --------------------------------------------

        │                │                │

+----------------+ +----------------+ +----------------+
|   Subscriber   | |   Subscriber   | |   Subscriber   |
+----------------+ +----------------+ +----------------+
| + update()     | | + update()     | | + update()     |
+--------▲-------+ +--------▲-------+ +--------▲-------+
         |                  |                  |
         |                  |                  |
+----------------+ +----------------+ +----------------+
| EmailSubscriber| | MobileSubscriber| | PremiumSubscriber|
+----------------+ +----------------+ +----------------+
```

### What Changed?

| Traditional Approach | Observer Pattern |
|----------------------|------------------|
| Channel depends on concrete subscribers. | Channel depends only on the Subscriber interface. |
| Notification logic is tightly coupled. | Notification happens through a common contract. |
| Adding new subscriber types may require changes. | New subscriber types can be added without modifying the channel. |

<details>
<summary>💡 Understanding the UML</summary>

### Visibility

| Symbol | Meaning |
|---------|---------|
| `+` | Public Member |
| `-` | Private Member |

### Reading the UML

- `YouTubeChannel` stores a collection of `Subscriber`.
- Every concrete subscriber implements the `Subscriber` interface.
- When a video is uploaded, the channel simply calls `update()` on every registered subscriber.
- The channel never needs to know what each subscriber actually does after receiving the notification.

This keeps the channel independent of concrete subscriber implementations.

</details>

---

# 12. Implementation

## Subscriber.java

```java
public interface Subscriber {

    void update(String channelName, String videoTitle);

}
```

<details>
<summary>💡 Why do we need an interface?</summary>

A common question is:

> **Why not directly store `YouTubeSubscriber` objects inside `YouTubeChannel`?**

Initially, we have only one subscriber type.

```java
private List<YouTubeSubscriber> subscribers;
```

This works perfectly.

---

### New Requirement

Later, the business introduces multiple notification mechanisms.

- Email Subscriber
- Mobile App Subscriber
- Premium Subscriber

Now we have multiple subscriber implementations.

```java
class EmailSubscriber { }

class MobileSubscriber { }

class PremiumSubscriber { }
```

If `YouTubeChannel` stores only `YouTubeSubscriber` objects,

it cannot work with these new subscriber types without modification.

Instead, define a common contract.

```java
public interface Subscriber {

    void update(String channelName, String videoTitle);

}
```

Now every subscriber implements the same interface.

```java
class EmailSubscriber implements Subscriber { }

class MobileSubscriber implements Subscriber { }

class PremiumSubscriber implements Subscriber { }
```

The channel stores

```java
List<Subscriber>
```

instead of

```java
List<YouTubeSubscriber>
```

Now any new subscriber type works automatically without modifying the channel.

This follows the **Dependency Inversion Principle (DIP)**.

> **High-level modules should depend on abstractions, not concrete implementations.**

</details>

---

## Channel.java

```java
public interface Channel {

    void subscribe(Subscriber subscriber);

    void unsubscribe(Subscriber subscriber);

    void uploadVideo(String videoTitle);

}
```

---

## YouTubeChannel.java

```java
import java.util.ArrayList;
import java.util.List;

// Represents the subject.
public class YouTubeChannel implements Channel {

    private final String channelName;
    private final List<Subscriber> subscribers = new ArrayList<>();

    public YouTubeChannel(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void unsubscribe(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void uploadVideo(String videoTitle) {

        System.out.println(channelName + " uploaded: " + videoTitle);

        notifySubscribers(videoTitle);

    }

    // Notifies all registered subscribers.
    private void notifySubscribers(String videoTitle) {

        for (Subscriber subscriber : subscribers) {
            subscriber.update(channelName, videoTitle);
        }

    }

}
```

---

## YouTubeSubscriber.java

```java
// Represents a subscriber interested in channel updates.
public class YouTubeSubscriber implements Subscriber {

    private final String name;

    public YouTubeSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void update(String channelName, String videoTitle) {

        System.out.println(
                name
                + " received notification: "
                + channelName
                + " uploaded "
                + videoTitle);

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        Channel channel = new YouTubeChannel("CodeWithAryan");

        Subscriber alice = new YouTubeSubscriber("Alice");
        Subscriber bob = new YouTubeSubscriber("Bob");
        Subscriber charlie = new YouTubeSubscriber("Charlie");
        Subscriber david = new YouTubeSubscriber("David");

        channel.subscribe(alice);
        channel.subscribe(bob);
        channel.subscribe(charlie);
        channel.subscribe(david);

        channel.uploadVideo("Observer Design Pattern");

    }

}
```

---

## Output

```text
CodeWithAryan uploaded: Observer Design Pattern

Alice received notification: CodeWithAryan uploaded Observer Design Pattern
Bob received notification: CodeWithAryan uploaded Observer Design Pattern
Charlie received notification: CodeWithAryan uploaded Observer Design Pattern
David received notification: CodeWithAryan uploaded Observer Design Pattern
```

---

# 13. Execution Flow

```text
uploadVideo()

      │

      ▼

notifySubscribers()

      │

      ▼

Loop through all subscribers

      │

      ├────────► Alice.update()

      ├────────► Bob.update()

      ├────────► Charlie.update()

      └────────► David.update()
```

### Communication Flow Comparison

#### Traditional Approach

```text
YouTubeChannel

      │

      ├────────► Alice

      ├────────► Bob

      ├────────► Charlie

      └────────► David
```

The channel directly manages every notification.

---

#### Observer Pattern

```text
YouTubeChannel

      │

      ▼

List<Subscriber>

      │

      ├────────► EmailSubscriber

      ├────────► MobileSubscriber

      ├────────► PremiumSubscriber

      └────────► Future Subscriber...
```

The channel only broadcasts an event.

Each subscriber independently decides how to react.

---

# 14. Design Decisions

This section explains **why** the implementation is designed this way.

---

## Why does `YouTubeChannel` store `List<Subscriber>`?

The channel needs to know:

- Who is interested in updates.
- Who should receive notifications.

It doesn't need to know **how** each subscriber reacts.

Therefore,

it stores the abstraction (`Subscriber`) instead of concrete subscriber classes.

---

## Why doesn't the channel know concrete subscriber types?

Imagine tomorrow we introduce:

- WhatsAppSubscriber
- SlackSubscriber
- DiscordSubscriber

Should the channel change?

**No.**

Every new subscriber simply implements the `Subscriber` interface.

The channel remains unchanged.

---

## Why does every subscriber implement the same interface?

The channel should notify every subscriber in exactly the same way.

Instead of writing different methods like:

```java
notifyEmailSubscriber()

notifyMobileSubscriber()

notifyPremiumSubscriber()
```

it simply calls

```java
subscriber.update(...)
```

This keeps the notification mechanism simple and extensible.

---

## Why separate `Channel` and `YouTubeChannel`?

`Channel` defines **what operations** every channel supports.

`YouTubeChannel` defines **how** those operations are implemented.

This allows future implementations like:

- GamingChannel
- NewsChannel
- EducationChannel

without changing subscriber code.

---

## Why doesn't the channel decide how notifications are delivered?

The channel's responsibility ends after broadcasting the event.

Whether the notification becomes:

- Email
- Push Notification
- SMS
- Desktop Notification

is the subscriber's responsibility.

This keeps responsibilities clearly separated.

---

# 15. Advantages

- Loose coupling between the subject and observers.
- New observer types can be added without modifying the subject.
- Supports the Open-Closed Principle.
- Makes event-driven communication simple.
- Allows multiple objects to react independently to the same event.
- Improves maintainability as notification mechanisms grow.

---

# 16. Disadvantages

- Every observer receives notifications unless additional filtering logic is introduced.
- Notification order may become important in some applications.
- If there are thousands of observers, notifying everyone may affect performance.
- Debugging can be harder because one event may trigger updates in many observers.

---

# 17. Before vs After

| Traditional Approach | Observer Pattern |
|----------------------|------------------|
| Subject directly knows concrete subscribers. | Subject knows only the Subscriber interface. |
| Notification logic grows inside the subject. | Notification logic is delegated to observers. |
| New notification types require modifying the subject. | New notification types are added by creating new observers. |
| High coupling. | Loose coupling. |

---

# 18. Real-World Examples

- YouTube Notifications
- Instagram Followers
- Facebook Notifications
- Stock Market Alerts
- Weather Applications
- News Subscription Systems
- Chat Applications
- Event-driven GUI Frameworks (Button Click Listeners)

---

# 19. When Should You Think of the Observer Pattern?

Consider using the Observer Pattern when:

- One object changes its state frequently.
- Multiple objects need to react to those changes.
- New listeners or subscribers may be added in the future.
- You want to avoid tightly coupling the publisher with every receiver.

A simple question to ask yourself is:

> **"When this object changes, do multiple other objects need to know about it?"**

If the answer is **Yes**, consider using the Observer Pattern.

---

# 20. Interview Questions

### Why is Observer a Behavioral Design Pattern?

Because it defines **how objects communicate when the state of one object changes.**

---

### What is the difference between Subject and Observer?

- **Subject** owns the state.
- **Observer** reacts to changes in that state.

---

### What is the benefit of using interfaces in Observer?

Interfaces allow the subject to notify every observer through a common contract.

The subject doesn't care whether the observer sends:

- Email
- Push Notification
- SMS
- Slack Notification

It simply calls `update()`.

---

### Does Observer follow the Open-Closed Principle?

Yes.

New observer types are introduced by implementing the `Subscriber` interface.

The subject remains unchanged.

---

# 21. Extending the Design

One of the biggest strengths of the Observer Pattern is that **new notification mechanisms can be added without modifying the subject.**

Suppose the Product Owner gives the following requirements.

- Send Email Notifications.
- Send Push Notifications.
- Send WhatsApp Notifications.
- Send Slack Notifications.

Should we modify `YouTubeChannel`?

**No.**

The channel's responsibility is still the same.

> **Publish a video and notify subscribers.**

Instead, create a new observer.

## Example: WhatsApp Subscriber

```java
public class WhatsAppSubscriber implements Subscriber {

    private final String phoneNumber;

    public WhatsAppSubscriber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void update(String channelName, String videoTitle) {

        System.out.println(
                "Sending WhatsApp message to "
                + phoneNumber
                + " : "
                + channelName
                + " uploaded "
                + videoTitle);

    }

}
```

Client code remains almost the same.

```java
channel.subscribe(new WhatsAppSubscriber("+91-9876543210"));
```

Notice what changed.

- `Subscriber` interface → No change
- `YouTubeChannel` → No change
- Existing subscribers → No change

Only one new class was added.

This demonstrates one of the biggest strengths of the Observer Pattern:

> **New notification mechanisms are added by creating new observers instead of modifying the publisher.**

---

# 22. Summary

In the traditional approach,

the YouTube channel directly manages every notification.

As notification requirements grow,

the channel becomes responsible for multiple unrelated tasks.

The Observer Pattern separates these responsibilities.

The subject simply announces that its state has changed.

Each observer independently decides how to react.

As a result:

- Coupling is reduced.
- The system becomes easier to extend.
- New notification types can be added without modifying existing code.

---

# 23. Quick Revision

## Intent

Automatically notify all interested objects whenever another object's state changes.

---

## Core Idea

```text
Traditional

Subject ─────► Observer

Observer Pattern

Subject ─────► Observer Interface ─────► Concrete Observers
```

---

## Components

- **Subscriber** → Observer contract.
- **YouTubeSubscriber** → Concrete Observer.
- **Channel** → Subject contract.
- **YouTubeChannel** → Concrete Subject.

---

## Key Benefits

- Loose Coupling
- Event-driven Communication
- Easy to Extend
- Supports Open-Closed Principle

---

## Think of Observer When

- One object changes.
- Many other objects need to react.
- New listeners may be added in the future.

> **If one object changes and many objects should react automatically, think Observer.**
