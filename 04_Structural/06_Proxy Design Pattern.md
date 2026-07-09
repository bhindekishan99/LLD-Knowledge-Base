# Proxy Design Pattern

> **Category:** Structural Design Pattern

---

# Sources

## 🎥 YouTube

https://www.youtube.com/watch?v=4z2LRJ-QPuI

## 🌐 Blog

https://codewitharyan.com/tech-blogs/proxy-design-pattern

---

# How to Think Towards the Proxy Pattern

Forget the Proxy Pattern for a moment.

Imagine you're building a **Video Streaming Platform**.

Initially,

your application streams videos to users.

```text
User

↓

Video Service

↓

Video Starts Playing
```

Everything works perfectly.

---

## A New Requirement Arrives

Now the Product Owner says,

> **"Some videos are Premium. Only Premium users should be able to watch them."**

Our first thought is,

let's check the user type inside the video service.

```java
if(user.isPremium()) {
    streamVideo();
} else {
    denyAccess();
}
```

Simple enough.

---

## Another Requirement

A few days later,

another requirement arrives.

> **"The same video is requested thousands of times every day. Can we cache it?"**

Again,

our natural thought is

```java
if(videoExistsInCache()) {
    returnCachedVideo();
} else {
    streamVideo();
}
```

Still manageable.

---

## Requirements Keep Growing

Soon,

more requirements arrive.

```text
Request Limiting

↓

Logging

↓

Authentication

↓

Authorization

↓

Caching

↓

Bandwidth Monitoring
```

Every time,

we add more code inside

```text
VideoService
```

---

## Stop and Think

Ask yourself.

What is the real responsibility of

```text
VideoService
```

It should simply

```text
Stream Videos
```

That's its job.

But now it also does

```text
Access Control

↓

Caching

↓

Request Limiting

↓

Logging

↓

Authentication

↓

Monitoring
```

Its responsibility keeps growing.

---

## The Class Starts Becoming Ugly

Initially,

the service looked like

```java
playVideo();
```

Later,

it becomes

```java
checkPermission();

↓

authenticateUser();

↓

countRequests();

↓

checkRateLimit();

↓

checkCache();

↓

logRequest();

↓

streamVideo();

↓

updateCache();

↓

writeLogs();
```

Now the actual work

```text
Stream Video
```

is only a small part of the class.

Everything else is

extra responsibilities.

---

## Think Like Real Life

Imagine entering a corporate office.

You don't directly walk into the CEO's cabin.

Instead,

you first meet

```text
Security Guard
```

The security guard checks

```text
Identity

↓

Visitor Pass

↓

Appointment

↓

Permissions
```

Only after all checks pass,

you're allowed to enter.

Notice something.

The security guard

doesn't do the CEO's work.

The CEO still performs the business work.

The guard simply controls

who can reach the CEO.

---

## First Refactoring

Instead of putting

```text
Permission Checks

↓

Caching

↓

Request Limiting
```

inside

```text
VideoService
```

we move them outside.

Now,

every request first goes to

```text
Proxy
```

The proxy decides

```text
Should this request proceed?
```

If yes,

it forwards the request.

Otherwise,

it blocks it.

---

## The Beautiful Observation

The

```text
VideoService
```

doesn't know

whether the request came directly from the client

or through a proxy.

It simply streams the video.

The proxy becomes a gatekeeper.

```text
Client

↓

Proxy

↓

Video Service
```

---

## Final Design

Instead of making

```text
VideoService
```

responsible for

```text
Authentication

Authorization

Caching

Logging

Rate Limiting
```

we introduce another object.

```text
Client

↓

Proxy

↓

Real Video Service
```

The Proxy performs all additional work.

Only after everything is valid,

it delegates the request to the real service.

---

## Complete Thinking Process

```text
Need Video Streaming Platform

        ↓

Need Premium Access

        ↓

Need Caching

        ↓

Need Request Limiting

        ↓

Need Logging

        ↓

VideoService Keeps Growing

        ↓

Business Logic Mixed With Access Logic

        ↓

Need Separate Gatekeeper

        ↓

Forward Requests Only When Allowed

        ↓

Proxy Design Pattern
```

---

# How to Remember the Proxy Pattern

Imagine an apartment society.

Visitors don't directly enter the building.

They first meet the

```text
Security Guard
```

The guard checks

```text
Visitor Name

↓

Flat Number

↓

Permission

↓

Entry Register
```

If everything is valid,

the guard allows entry.

Otherwise,

the visitor is stopped.

Notice something.

The guard never performs the resident's work.

He simply controls access.

Exactly the same happens in the **Proxy Pattern**.

The proxy doesn't perform the business logic.

It only decides

whether the client should reach the real object.

```text
Client

↓

Proxy (Security Guard)

↓

Real Object
```

---

## Memory Trick

> **When an object needs a gatekeeper to control access before reaching the real object, think of the Proxy Pattern.**

---

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## VideoService.java

```java
// Responsible for streaming videos.
public class VideoService {

    public void playVideo(String user, String video) {

        System.out.println("Streaming \"" + video + "\" for " + user);

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        VideoService videoService = new VideoService();

        videoService.playVideo("John", "Java Design Patterns");

    }

}
```

---

## Output

```text
Streaming "Java Design Patterns" for John
```

---

## A New Requirement Arrives

The Product Owner says,

> **"Only Premium users should be allowed to watch Premium videos."**

Our first thought is,

let's add an access check.

```java
public void playVideo(String user, String video) {

    if(user.equals("Premium")) {

        System.out.println("Streaming \"" + video + "\"");

    }
    else {

        System.out.println("Access Denied");

    }

}
```

Problem solved.

At least for now.

---

## Another Requirement

Now another requirement arrives.

> **"Cache frequently watched videos."**

Again,

we modify the same class.

```java
checkCache();

↓

if available

↓

return cached video

↓

else

↓

stream video

↓

store in cache
```

---

## Another Requirement

The Product Owner now says,

> **"Log every request."**

Again,

the same class changes.

```java
logRequest();

↓

checkPermission();

↓

checkCache();

↓

streamVideo();
```

---

## Requirements Keep Growing

Soon,

the service starts handling

```text
Authentication

↓

Authorization

↓

Caching

↓

Logging

↓

Request Limiting

↓

Bandwidth Monitoring

↓

Streaming Video
```

---

## Client Code Also Starts Growing

Instead of simply writing

```java
videoService.playVideo(...);
```

the client gradually becomes responsible for many checks.

```java
authenticateUser();

↓

validateSubscription();

↓

checkRequestLimit();

↓

videoService.playVideo();
```

Different clients may even perform these checks differently,

leading to inconsistent behavior.

---

## Problems With This Approach

Although the implementation works initially,

it becomes difficult to maintain as more responsibilities are added.

---

### 1. Too Many Responsibilities

The VideoService should simply

```text
Stream Videos
```

Instead,

it now performs

- Authentication
- Authorization
- Caching
- Logging
- Rate Limiting
- Monitoring

The business logic becomes mixed with infrastructure logic.

---

### 2. Violates the Single Responsibility Principle

Every new requirement forces us to modify

```text
VideoService
```

The class now changes for multiple reasons.

---

### 3. Difficult to Reuse

Suppose tomorrow another service is introduced.

```text
MusicService
```

Now we again implement

- Authentication
- Logging
- Caching

The same code gets duplicated across multiple services.

---

### 4. Difficult to Maintain

Suppose the authentication process changes.

Now every service implementing authentication must be updated.

This increases maintenance effort.

---

### 5. Business Logic Becomes Hard to Read

Instead of

```java
streamVideo();
```

the method now looks like

```text
Authenticate

↓

Authorize

↓

Check Cache

↓

Log Request

↓

Validate Rate Limit

↓

Stream Video

↓

Update Cache

↓

Write Logs
```

The actual business logic becomes buried under infrastructure code.

---

### 6. Future Enhancements Become Difficult

Tomorrow the Product Owner says,

> **"Add Audit Logging for every request."**

Again,

the VideoService changes.

The class keeps growing,

even though its actual responsibility

is only

```text
Stream Videos
```

---

## The Real Problem

Notice something important.

The actual work is only

```text
Play Video
```

Everything else

```text
Authentication

Authorization

Caching

Logging

Monitoring

Rate Limiting
```

is performed **before or after** the real work.

These are not the responsibility of

```text
VideoService
```

Instead of putting all these responsibilities inside the real object,

we need another object that sits in front of it,

controls access,

performs additional work,

and only then forwards the request.

This is exactly the problem the **Proxy Pattern** solves.

</details>

---

# Applying the Proxy Pattern

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## VideoService.java (Subject)

```java
// Subject
// Defines the common interface for both the Real Service and the Proxy.
public interface VideoService {

    void playVideo(String user, String video);

}
```

<details>
<summary>💡 Why do we need a common interface?</summary>

A common question is:

> **Why can't the client directly use `RealVideoService` or `VideoServiceProxy`?**

The reason is flexibility.

The client should not know whether it is talking to

- the real service, or
- the proxy.

It should simply interact with

```java
VideoService
```

Today, the object may be

```java
new VideoServiceProxy();
```

Tomorrow, it could directly be

```java
new RealVideoService();
```

Since both implement the same interface,

the client code never changes.

This is one of the biggest advantages of the Proxy Pattern.

</details>

---

## RealVideoService.java (Real Subject)

```java
// Real Subject
// Performs the actual business operation.
public class RealVideoService implements VideoService {

    @Override
    public void playVideo(String user, String video) {

        System.out.println("Streaming \"" + video + "\" for " + user);

    }

}
```

---

## VideoServiceProxy.java (Proxy)

```java
// Proxy
// Controls access to the RealVideoService.
public class VideoServiceProxy implements VideoService {

    private final RealVideoService realVideoService;

    public VideoServiceProxy() {

        realVideoService = new RealVideoService();

    }

    @Override
    public void playVideo(String user, String video) {

        System.out.println("Checking subscription...");

        if (!user.equalsIgnoreCase("Premium")) {

            System.out.println("Access Denied. Premium membership required.");

            return;

        }

        System.out.println("Access Granted.");

        realVideoService.playVideo(user, video);

    }

}
```

<details>
<summary>💡 Why do we need a Proxy?</summary>

This is the heart of the Proxy Pattern.

Initially,

the client directly called

```java
realVideoService.playVideo(...);
```

As new requirements arrived,

the service started handling

- Authentication
- Authorization
- Logging
- Caching
- Rate Limiting

These responsibilities don't belong to

```text
RealVideoService
```

Its only responsibility is

```text
Stream Videos
```

Instead,

we introduce another object.

```text
Client

↓

Proxy

↓

Real Video Service
```

The proxy performs all additional work.

Only if everything is valid,

it forwards the request to the real service.

The real object remains clean and focused only on business logic.

</details>

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        VideoService videoService = new VideoServiceProxy();

        System.out.println("===== Premium User =====");

        videoService.playVideo("Premium", "Design Patterns");

        System.out.println();

        System.out.println("===== Normal User =====");

        videoService.playVideo("John", "Design Patterns");

    }

}
```

---

## Output

```text
===== Premium User =====

Checking subscription...

Access Granted.

Streaming "Design Patterns" for Premium

===== Normal User =====

Checking subscription...

Access Denied. Premium membership required.
```

---

## Execution Flow

### Premium User

```text
Client

        │

playVideo()

        │

        ▼

VideoServiceProxy

        │

Check Subscription

        │

Premium User

        │

Forward Request

        │

        ▼

RealVideoService

        │

Streams Video

        ▼

Video Starts Playing
```

---

### Normal User

```text
Client

        │

playVideo()

        │

        ▼

VideoServiceProxy

        │

Check Subscription

        │

Not Premium

        │

Access Denied

        ▼

Request Stops Here
```

Notice something important.

The client never communicates directly with

```text
RealVideoService
```

It always communicates with

```java
VideoService
```

which, in this case, is implemented by

```java
VideoServiceProxy
```

The proxy decides whether the request should reach the real object.

If allowed,

the request is forwarded.

Otherwise,

the request is blocked.

The **RealVideoService** never knows

- who performed authentication,
- who checked permissions,
- or who denied access.

It simply performs its business operation whenever a valid request reaches it.

This is the core idea behind the **Proxy Pattern**.

</details>

---

# Handling New Requirements

## New Requirement 1

The Product Owner says,

> **"Cache frequently watched videos."**

### Without the Proxy Pattern

We modify

```text
RealVideoService
```

again.

```text
Check Cache

↓

Stream Video

↓

Store in Cache
```

The service keeps growing.

---

### With the Proxy Pattern

We simply enhance the proxy.

```java
@Override
public void playVideo(String user, String video) {

    if(cache.contains(video)) {

        System.out.println("Returning video from cache.");

        return;
    }

    realVideoService.playVideo(user, video);

    cache.add(video);

}
```

No changes are required in

```java
RealVideoService
```

---

## New Requirement 2

Now the Product Owner says,

> **"Log every request."**

Again,

only the proxy changes.

```java
System.out.println("User requested : " + video);

realVideoService.playVideo(user, video);
```

The real object remains untouched.

---

## New Requirement 3

Another requirement arrives.

> **"Limit each user to 100 requests per day."**

Again,

the proxy performs the check.

```java
if(requestCount >= 100) {

    System.out.println("Daily request limit exceeded.");

    return;

}

realVideoService.playVideo(user, video);
```

No modifications are required in the real service.

The proxy continues acting as the gatekeeper.

---

## The Biggest Advantage

Whenever a new requirement appears that needs to happen

- before the real work,
- after the real work,
- or instead of the real work,

we simply enhance the **Proxy**.

The **RealVideoService** remains focused on only one responsibility:

```text
Streaming Videos
```

This follows the **Open/Closed Principle** and keeps the business logic clean.

</details>
