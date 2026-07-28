# Notification System Design

## Problem Statement

Design a notification platform similar to the systems used by Amazon, AWS, Netflix, Flipkart, etc.

The platform should allow clients to publish notifications and deliver them to subscribers based on their subscription preferences.

Subscribers should be able to configure how they want to receive notifications for different clients and different priorities.

The system should be extensible enough to support new notification channels and new delivery mechanisms without changing existing code.

---

# Thinking Process

Instead of immediately thinking about classes, let's imagine we own a company.

---

## Step 1: Imagine you own a company called **NotifyMe**

Companies don't want to build their own notification infrastructure.

Instead, they integrate with our platform.

For example:

- Amazon Shopping
- AWS
- Netflix
- Flipkart

Whenever they want to notify their users, they simply call our platform.

```
Amazon

↓

"Payment Failed"

↓

NotifyMe
```

---

## Step 2: What's the first question our platform should ask?

Not

> Should I send an Email?

Not

> Should I send an SMS?

The first question is much simpler.

> **Who should receive this notification?**

Without knowing the recipients, there is no point deciding how to deliver it.

---

## Step 3: Where do we find the subscribers?

Suppose Amazon has three subscribers.

```
Amazon

↓

Bob

Alice

Charlie
```

Somewhere our platform must maintain this information.

Conceptually it is nothing more than a phonebook.

```
Client

↓

Subscribers
```

Since this has a single responsibility—

> Given a client, return all its subscribers.

—we naturally derive a new object.

## SubscriptionRegistry

**Responsibility**

- Register subscriptions.
- Remove subscriptions.
- Return all subscribers for a client.

Notice that it **does not send notifications**.

It only answers one question:

> Who subscribed?

---

## Step 4: Bob is a subscriber

The registry tells us

```
Bob is subscribed.
```

Can we send a notification now?

**No.**

Because we still don't know **how Bob wants to receive notifications**.

---

## Step 5: How does Bob want notifications?

Suppose Bob configures his preferences like this.

```
Amazon

HIGH
↓

Phone

↓

SMS

↓

Email
```

For AWS he may configure something completely different.

```
AWS

HIGH
↓

Email

LOW
↓

SMS
```

Notice something interesting.

These settings are **not about Bob alone**.

They are also **not about Amazon alone**.

They belong to the relationship between Bob and Amazon.

```
Bob

+

Amazon

↓

Notification Preferences
```

Since this relationship has its own data, we derive another object.

## SubscriptionPreference

**Responsibility**

Store notification preferences for a subscriber for a particular client.

For example:

```
Bob

+

Amazon

↓

HIGH → Phone → SMS → Email

MEDIUM → Email

LOW → Push
```

---

## Step 6: We know Bob's preference

Now we know that for HIGH priority notifications Bob wants

```
Phone

↓

SMS

↓

Email
```

The next question is

> Who actually knows how to send an Email?

Certainly not Bob.

Certainly not Amazon.

Certainly not SubscriptionPreference.

Some object must know **how to send an Email**.

Hence we derive

## NotificationChannel

Examples:

- EmailChannel
- SmsChannel
- PhoneChannel
- PushChannel

Each channel has only one responsibility.

```
EmailChannel

↓

Send Email
```

```
SmsChannel

↓

Send SMS
```

```
PhoneChannel

↓

Make Phone Call
```

---

## Step 7: We know **what** Bob wants. But **how** should we deliver it?

Bob's preference for HIGH priority notifications is

```
Phone

↓

SMS

↓

Email
```

At first glance, this looks like just a list of channels.

So why don't we simply store

```java
HIGH -> List<NotificationChannel>
```

This is a perfectly valid design and is where we should start.

However, let's see what happens when the business evolves.

---

## Step 8: Business requirements change

Initially, HIGH priority notifications are delivered like this.

```
SMS

↓

Email
```

A few months later, the product manager comes with a new requirement.

> For HIGH priority notifications:
>
> - Send an SMS.
> - If the SMS fails, retry twice.
> - If it still fails, make a phone call.
> - Finally, send an Email.

Now the delivery looks like this.

```
SMS

↓

Retry Twice

↓

Phone

↓

Email
```

Notice something.

This is no longer just a collection of channels.

It is a **plan** for delivering a notification.

---

## Step 9: More business requirements

Different customers now want different delivery behaviours.

Amazon wants

```
SMS

↓

Email
```

AWS wants

```
Phone

↓

SMS

↓

Email
```

Netflix wants

```
Email

↓

Push Notification
```

Later another client requests

```
Email and SMS in Parallel

↓

Phone Call if both fail
```

Now we don't simply have different channels.

We have different **delivery algorithms**.

---

## Step 10: Deriving a new object

Whenever the business introduces a new behaviour that can evolve independently, we should ask

> Does this deserve its own object?

The answer here is **Yes**.

The delivery plan itself becomes an object.

We call it

## DeliveryStrategy

Its responsibility is simple.

> Execute the delivery plan for a notification.

It does **not** know

- who subscribed,
- who published the notification,
- or how Email is sent.

It only knows

> In what order and by what logic should the channels be executed?

---

## Step 11: Different strategies

As business requirements grow, we can introduce different strategies.

Sequential Delivery

```
SMS

↓

Email
```

Retry Delivery

```
SMS

↓

Retry Twice

↓

Phone

↓

Email
```

Parallel Delivery

```
Email        SMS

     ↓

 Wait for both
```

First Successful Delivery

```
SMS

↓

If Success

Stop

↓

Else

Phone

↓

Else

Email
```

Each of these represents a different **algorithm** for delivering notifications.

Notice that we create a new strategy **only when the behaviour changes**, not when the subscriber changes.

---

## Step 12: Configuring Bob

Bob's preference might look like this.

```
HIGH

↓

SequentialDeliveryStrategy

↓

SMS

↓

Email
```

Alice may also use the same strategy.

```
HIGH

↓

SequentialDeliveryStrategy

↓

Phone

↓

SMS

↓

Email
```

Notice that both Bob and Alice use the **same strategy class**.

Only the configured channels are different.

The behaviour remains

> Execute channels one after another.

This is an important distinction.

- Different subscribers → Different configuration.
- Different clients → Different configuration.
- Different channels → Different configuration.
- Different delivery algorithm → Different strategy class.

---

## Step 13: A client publishes a notification

Suppose Amazon publishes

```
Amazon

↓

HIGH

↓

"Payment Failed"
```

Who should handle this request?

Certainly not Amazon.

Amazon's responsibility ends after publishing the notification.

Some object inside our platform has to take over.

---

## Step 14: What should happen next?

Let's think through the business flow.

Step 1

Find everyone who subscribed to Amazon.

```
Amazon

↓

SubscriptionRegistry

↓

Bob

Alice

Charlie
```

---

Step 2

For each subscriber, retrieve their preferences.

For Bob

```
SubscriptionPreference

↓

HIGH

↓

SequentialDeliveryStrategy
```

---

Step 3

Execute the delivery strategy.

```
SequentialDeliveryStrategy

↓

SMS

↓

Email
```

---

Step 4

Each channel performs its own work.

```
SmsChannel

↓

Send SMS
```

```
EmailChannel

↓

Send Email
```

---

## Step 15: Who should own this entire workflow?

Let's examine every object.

### Client

Can it coordinate everything?

No.

Its responsibility is only

> Publish notification.

---

### SubscriptionRegistry

Can it coordinate?

No.

It only knows

> Who subscribed?

---

### SubscriptionPreference

Can it coordinate?

No.

It only knows

> How this subscriber wants notifications.

---

### DeliveryStrategy

Can it coordinate?

No.

It only knows

> How to execute the delivery plan.

---

### NotificationChannel

Can it coordinate?

No.

It only knows

> How to send through one channel.

---

Notice something.

No existing object owns this complete workflow.

Whenever this happens, we derive a new object.

---

## Step 16: NotificationService

The NotificationService becomes the coordinator.

Its responsibility is simply

> Orchestrate the notification workflow.

It performs the following steps.

```
Receive Notification

↓

Find Subscribers

↓

For each Subscriber

↓

Get Delivery Strategy

↓

Execute Delivery Strategy

↓

Notification Delivered
```

Notice what NotificationService **doesn't** know.

It doesn't know

- how Email works
- how SMS works
- how Phone works
- Bob's preferences
- who subscribed

It simply asks the right objects to perform their responsibilities.

---

## Step 17: The complete picture

```
Amazon

↓

NotificationService

↓

SubscriptionRegistry

↓

SubscriptionPreference

↓

DeliveryStrategy

↓

NotificationChannel

↓

Subscriber
```

Each object has one responsibility.

No object knows everything.

No object does everything.

Every object collaborates with the others to complete the business flow.
