# State Design Pattern

> **Category:** Behavioral Design Pattern

---

# Source

## 📄 PDF

State Design Pattern - CodeWithAryan

## 🎥 YouTube

https://www.youtube.com/watch?v=uQMiEP52YIg&t=3s

## 🌐 Blog

https://codewitharyan.com/tech-blogs/state-design-pattern

---

# How to Think Towards the State Pattern

Forget the State Pattern for a moment.

Imagine you're asked to build a **Traffic Light System**.

The traffic light has three colors:

- RED
- GREEN
- YELLOW

Whenever the timer expires, it should move to the next color.

```
RED
 ↓
GREEN
 ↓
YELLOW
 ↓
RED
```

As a developer, what would you naturally write?

Probably this.

```java
public class TrafficLight {

    private String color = "RED";

    public void next() {

        if (color.equals("RED")) {
            color = "GREEN";
        } else if (color.equals("GREEN")) {
            color = "YELLOW";
        } else if (color.equals("YELLOW")) {
            color = "RED";
        }

    }

}
```

This is exactly how most developers—including senior engineers—would start.

At this point, there is absolutely nothing wrong with this solution.

It is simple, readable and solves today's problem.

---

## Requirements Start Growing

Now the Product Owner comes back.

> Add **BLINKING** mode for night traffic.

You add another `else if`.

Later,

> Add **MAINTENANCE** mode.

Another `else if`.

Then,

- Emergency Mode
- Manual Police Control
- Timer Logic
- Analytics
- LED Display
- Logging

The method slowly becomes larger and larger.

---

## ⭐ Thumb Rule

At this point, many developers think:

> **"Whenever I see a long `if-else`, I should use the State Pattern."**

**No.**

A long `if-else` alone is **not** a reason to introduce the State Pattern.

Ask yourself one simple question.

> **Does each branch have its own behavior that will continue to grow?**

### If **No**

Keep the `if-else`.

Example:

```java
if (age < 18) {
    category = "Minor";
} else if (age < 60) {
    category = "Adult";
} else {
    category = "Senior";
}
```

Each branch simply returns a value.

There is no complex behavior.

Creating three separate classes would only make the code more complicated.

---

### If **Yes**

Think about the State Pattern.

Example:

```java
if (color.equals("RED")) {

    stopCars();

    startTimer();

    logTraffic();

    updateDisplay();

    checkEmergencyVehicles();

    color = "GREEN";

}
```

Notice something.

This branch is no longer just checking a condition.

It owns its own behavior.

As requirements continue to grow,

every branch slowly becomes its own mini-program.

That is the signal.

---

## Now Observe Carefully

Forget the syntax.

Look at what each branch is actually doing.

```
RED

↓

Print message

↓

Decide next state

↓

Perform RED-specific work
```

```
GREEN

↓

Print message

↓

Decide next state

↓

Perform GREEN-specific work
```

```
YELLOW

↓

Print message

↓

Decide next state

↓

Perform YELLOW-specific work
```

Every state has its own behavior.

Ask yourself,

> **Who really knows how RED behaves?**

TrafficLight?

No.

**RED itself knows.**

Who knows what happens after GREEN?

Again,

**GREEN itself knows.**

The behavior belongs to the state itself.

---

## First Refactoring

Instead of one class doing everything,

move each state's behavior into its own class.

```
TrafficLight

↓

RedState

GreenState

YellowState
```

Each class now becomes responsible only for itself.

```java
class RedState {

    void next() {

        // RED specific behavior

    }

}
```

Notice,

you still haven't implemented the State Pattern.

You're simply moving behavior to the object that owns it.

---

## New Problem

Now you have three classes.

Question:

How does the application know

which state is currently active?

Someone must remember

```
Current State = RED

↓

Current State = GREEN

↓

Current State = YELLOW
```

That responsibility belongs to another object.

This object becomes the **Context**.

```
TrafficLightContext
```

The Context doesn't know transition logic.

It simply remembers the current state.

---

## Another Question

Suppose the current state is RED.

User calls

```java
next();
```

Who should decide that the next state is GREEN?

Context?

No.

RED itself knows that.

So RED simply says

```java
context.setState(new GreenState());
```

Likewise,

GREEN knows the next state is YELLOW.

YELLOW knows the next state is RED.

Each state becomes responsible for deciding its own transition.

---

## Finally...

Every state exposes the same operations.

```java
next()

showSignal()
```

Whenever multiple classes expose identical behavior,

our brain naturally thinks,

> **"Let's create a common contract."**

That contract becomes

```java
TrafficLightState
```

Now the Context stores

```java
TrafficLightState currentState;
```

instead of

```java
RedState
```

or

```java
GreenState
```

Now Context simply delegates.

```java
currentState.next(this);
```

The Context no longer needs to know which concrete state is active.

---

## Complete Thinking

```
Need Traffic Light

        ↓

Write if-else

        ↓

Requirements grow

        ↓

Huge if-else

        ↓

Observe carefully

        ↓

Each branch owns its own behavior

        ↓

Move behavior into separate classes

        ↓

Need to remember current state

        ↓

Create Context

        ↓

Need common type

        ↓

Create State Interface

        ↓

State Pattern
```

---

# How to Remember the State Pattern

Imagine yourself.

Morning

```
Sleeping
```

↓

Office

```
Working
```

↓

Evening

```
Driving
```

↓

Night

```
Sleeping
```

You are still the same person.

Only your **state** changes.

Because your state changes,

your behavior also changes.

```
Sleeping

↓

canSleep()

cannotDrive()

cannotWork()
```

```
Working

↓

writeCode()

attendMeeting()

cannotSleep()
```

```
Driving

↓

accelerate()

brake()

cannotWriteCode()
```

The object never changes.

Only its **current state** changes.

That is exactly what the State Pattern models.

## Memory Trick

Remember one sentence.

> **Same object. Different state. Different behavior.**

Whenever you see

```java
if(state == ...)
```

ask yourself,

> **Can each state become its own object?**

If the answer is **Yes**,

you're probably looking at the **State Pattern**.

---

# Traditional Implementation

<details>
<summary><strong>Click to view complete traditional implementation</strong></summary>

```java
// Represents a simple traffic light system.
public class TrafficLight {

    // Stores the current color.
    private String color;

    public TrafficLight() {
        color = "RED";
    }

    // Changes the traffic light to the next state.
    public void next() {

        if (color.equals("RED")) {

            System.out.println("RED -> Stop");

            color = "GREEN";

        } else if (color.equals("GREEN")) {

            System.out.println("GREEN -> Go");

            color = "YELLOW";

        } else if (color.equals("YELLOW")) {

            System.out.println("YELLOW -> Slow Down");

            color = "RED";

        }

    }

    public String getColor() {
        return color;
    }

}
```

### Client Code

```java
public class Main {

    public static void main(String[] args) {

        TrafficLight trafficLight = new TrafficLight();

        trafficLight.next();
        trafficLight.next();
        trafficLight.next();

    }

}
```

### Output

```text
RED -> Stop
GREEN -> Go
YELLOW -> Slow Down
```

</details>

---

# State Pattern Implementation

<details>
<summary><strong>Click to view complete State Pattern implementation</strong></summary>

## TrafficLightState.java

```java
// Represents the behavior of a traffic light state.
public interface TrafficLightState {

    // Performs the current state's behavior and transitions to the next state.
    void next(TrafficLightContext context);

    // Returns the name of the current state.
    String getStateName();

}
```

<details>
<summary>💡 Why do we need an interface?</summary>

Initially, we have only three states.

- RedState
- GreenState
- YellowState

Without an interface, the context would need to store a concrete class.

```java
private RedState currentState;
```

Immediately a problem appears.

After RED, the current state becomes GREEN.

Now the variable should become

```java
private GreenState currentState;
```

Then later,

```java
private YellowState currentState;
```

One variable cannot keep changing its type.

We need one common type that represents every possible state.

That's exactly why we introduce

```java
TrafficLightState
```

Now the context simply stores

```java
private TrafficLightState currentState;
```

Whether the current object is

- RedState
- GreenState
- YellowState
- BlinkingState
- MaintenanceState

the context doesn't care.

It only depends on the abstraction.

This is another example of the **Dependency Inversion Principle (DIP)**.

</details>

---

## TrafficLightContext.java

```java
// Represents the traffic light.
public class TrafficLightContext {

    // Stores the current active state.
    private TrafficLightState currentState;

    public TrafficLightContext() {
        currentState = new RedState();
    }

    // Updates the current active state.
    public void setState(TrafficLightState state) {
        currentState = state;
    }

    // Returns the current active state.
    public TrafficLightState getState() {
        return currentState;
    }

    // Delegates the request to the current active state.
    public void next() {
        currentState.next(this);
    }

}
```

---

## RedState.java

```java
// Represents the RED state.
public class RedState implements TrafficLightState {

    @Override
    public void next(TrafficLightContext context) {

        System.out.println("RED -> Stop");

        context.setState(new GreenState());

    }

    @Override
    public String getStateName() {
        return "RED";
    }

}
```

---

## GreenState.java

```java
// Represents the GREEN state.
public class GreenState implements TrafficLightState {

    @Override
    public void next(TrafficLightContext context) {

        System.out.println("GREEN -> Go");

        context.setState(new YellowState());

    }

    @Override
    public String getStateName() {
        return "GREEN";
    }

}
```

---

## YellowState.java

```java
// Represents the YELLOW state.
public class YellowState implements TrafficLightState {

    @Override
    public void next(TrafficLightContext context) {

        System.out.println("YELLOW -> Slow Down");

        context.setState(new RedState());

    }

    @Override
    public String getStateName() {
        return "YELLOW";
    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        TrafficLightContext trafficLight = new TrafficLightContext();

        trafficLight.next();
        trafficLight.next();
        trafficLight.next();
        trafficLight.next();
        trafficLight.next();

    }

}
```

---

## Output

```text
RED -> Stop
GREEN -> Go
YELLOW -> Slow Down
RED -> Stop
GREEN -> Go
```

</details>

---

# Key Takeaways

- A long `if-else` is **not** enough to justify the State Pattern.
- Use the State Pattern only when **each branch has its own behavior that continues to grow**.
- Move state-specific behavior into separate state classes.
- Let each state decide what the next state should be.
- The **Context** only stores the current state and delegates the request.
- The **State interface** allows the Context to work with any current or future state without modification.

---

# Quick Revision

### Intent

> Move state-specific behavior into separate objects so an object's behavior changes automatically when its state changes.

---

### Thumb Rule

```text
Long if-else

        │

        ▼

Does each branch have its own behavior
that will continue to grow?

        │
   ┌────┴────┐
   │         │
  No        Yes
   │         │
Keep      Think about
if-else   State Pattern
```

---

### Thinking Process

```text
if-else

      ↓

Requirements grow

      ↓

Each branch owns behavior

      ↓

Move behavior into State classes

      ↓

Need current active state

      ↓

Create Context

      ↓

Need common type

      ↓

Create State interface
```

---

### Memory Trick

> **Same object. Different state. Different behavior.**
