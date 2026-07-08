# Composite Design Pattern

> **Category:** Structural Design Pattern

---

# Sources

## 🎥 YouTube

https://www.youtube.com/watch?v=78270FDPlZg

## 🌐 Blog

https://codewitharyan.com/tech-blogs/composite-design-pattern

---

# How to Think Towards the Composite Pattern

Forget the Composite Pattern for a moment.

Imagine you're building a **Smart Home System**.

The system allows users to control every smart device in their house.

Initially, the house contains only a few devices.

```text
Air Conditioner

Smart Light
```

Turning ON a device is straightforward.

```java
airConditioner.turnOn();

smartLight.turnOn();
```

Everything works perfectly.

---

## A New Requirement Arrives

The Product Owner now says,

> **"Let's organize devices into rooms."**

Now your smart home looks like this.

```text
Room 1

├── Air Conditioner

└── Smart Light
```

The user should now be able to turn ON an entire room.

Naturally, we write

```java
room.turnOn();
```

Internally,

the room turns ON every device it contains.

```text
Room

↓

Air Conditioner.turnOn()

↓

Smart Light.turnOn()
```

Still simple.

---

## The Hierarchy Keeps Growing

A few weeks later,

another requirement arrives.

> **"A floor contains multiple rooms."**

Now the hierarchy becomes

```text
Floor

├── Room 1

│   ├── Air Conditioner

│   └── Smart Light

│

└── Room 2

    ├── Air Conditioner

    └── Smart Light
```

The Product Owner now wants

```java
floor.turnOn();
```

which should automatically turn ON

every room

and

every device.

---

## More Requirements

Soon,

another requirement arrives.

> **"A house contains multiple floors."**

Now the hierarchy becomes

```text
House

├── Floor 1

│   ├── Room

│   │

│   ├── Devices

│

└── Floor 2

    ├── Room

    ├── Devices
```

Now the user simply wants

```java
house.turnOn();
```

without worrying about

- Floors
- Rooms
- Devices

---

## Our First Thought

As developers,

we naturally write

```java
house.turnOn() {

    floor1.turnOn();

    floor2.turnOn();

}
```

Then

```java
floor.turnOn() {

    room1.turnOn();

    room2.turnOn();

}
```

Then

```java
room.turnOn() {

    airConditioner.turnOn();

    smartLight.turnOn();

}
```

At first,

this seems reasonable.

---

## The Problem Starts Appearing

Tomorrow,

the Product Owner introduces

```text
Garage
```

which also contains devices.

Next month,

they introduce

```text
Garden
```

Later,

they introduce

```text
Zone
```

which contains multiple rooms.

Now ask yourself.

How many places must be modified?

Every class that manually understands

the hierarchy.

---

## Stop and Observe

Notice something interesting.

Whether it's

```text
Air Conditioner
```

or

```text
Room
```

or

```text
Floor
```

or

```text
House
```

the client wants to perform exactly the same operation.

```java
turnOn();

turnOff();
```

The client shouldn't care

whether it is controlling

- one device

or

- one hundred devices.

---

## Think Like Real Life

Imagine you're using **File Explorer**.

You can delete

a single file.

```text
Resume.pdf
```

using

```text
Delete
```

You can also delete

an entire folder.

```text
Documents
```

using

the exact same

```text
Delete
```

button.

You never think about

whether it's

- one file

or

- one hundred files.

The operating system handles everything.

This is exactly how Composite works.

---

## First Refactoring

Instead of treating

devices,

rooms,

floors,

and houses

differently,

let's give them all one common interface.

```java
turnOn();

turnOff();
```

Now,

everything becomes

a

```text
Smart Component
```

---

## One More Thought

A device performs the operation directly.

```text
Air Conditioner

↓

turnOn()
```

But what should a room do?

A room doesn't actually turn ON.

Instead,

it asks

every child

to turn ON.

```text
Room

↓

Device 1

↓

Device 2

↓

Device 3
```

Exactly the same thing happens for

```text
Floor
```

and

```text
House
```

Each composite simply delegates the operation to its children.

---

## Final Design

Every object in the hierarchy implements

```java
SmartComponent
```

Leaf Nodes

```text
Air Conditioner

Smart Light
```

perform the operation themselves.

Composite Nodes

```text
Room

Floor

House
```

simply forward the operation

to all their children.

Now the client always writes

```java
component.turnOn();
```

without caring whether

`component`

is

- a device

- a room

- a floor

- or an entire house.

---

## Complete Thinking Process

```text
Need Smart Home

        ↓

Add Devices

        ↓

Group Devices Into Rooms

        ↓

Group Rooms Into Floors

        ↓

Group Floors Into House

        ↓

Hierarchy Keeps Growing

        ↓

Client Should Not Care About Levels

        ↓

Need Common Interface

        ↓

Leaf Performs Action

        ↓

Composite Delegates To Children

        ↓

Composite Pattern
```

---

# How to Remember the Composite Pattern

Imagine your computer's file system.

```text
C:

├── Documents

│   ├── Resume.pdf

│   ├── Notes.docx

│

└── Pictures

    ├── Photo1.jpg

    └── Photo2.png
```

When you click

```text
Delete
```

you use exactly the same operation for

- a single file

and

- an entire folder.

The operating system automatically deletes everything inside the folder.

You don't need different buttons for

- Delete File
- Delete Folder
- Delete Drive

Everything is treated uniformly.

The Composite Pattern works in exactly the same way.

Individual objects and groups of objects expose the same interface, allowing the client to treat both identically.

## Memory Trick

> **The Composite Pattern lets you treat one object and a group of objects in exactly the same way.**

---

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## AirConditioner.java

```java
public class AirConditioner {

    public void turnOn() {
        System.out.println("Air Conditioner is now ON.");
    }

    public void turnOff() {
        System.out.println("Air Conditioner is now OFF.");
    }

}
```

---

## SmartLight.java

```java
public class SmartLight {

    public void turnOn() {
        System.out.println("Smart Light is now ON.");
    }

    public void turnOff() {
        System.out.println("Smart Light is now OFF.");
    }

}
```

---

## SmartHomeController.java

```java
public class SmartHomeController {

    public static void main(String[] args) {

        // Room 1
        AirConditioner room1AC = new AirConditioner();
        SmartLight room1Light = new SmartLight();

        // Room 2
        AirConditioner room2AC = new AirConditioner();
        SmartLight room2Light = new SmartLight();

        // Turn ON Room 1
        System.out.println("Turning ON devices in Room 1");

        room1AC.turnOn();
        room1Light.turnOn();

        // Turn ON Room 2
        System.out.println("\nTurning ON devices in Room 2");

        room2AC.turnOn();
        room2Light.turnOn();

        // Turn OFF Room 1
        System.out.println("\nTurning OFF devices in Room 1");

        room1AC.turnOff();
        room1Light.turnOff();

        // Turn OFF Room 2
        System.out.println("\nTurning OFF devices in Room 2");

        room2AC.turnOff();
        room2Light.turnOff();

    }

}
```

---

## Output

```text
Turning ON devices in Room 1

Air Conditioner is now ON.
Smart Light is now ON.

Turning ON devices in Room 2

Air Conditioner is now ON.
Smart Light is now ON.

Turning OFF devices in Room 1

Air Conditioner is now OFF.
Smart Light is now OFF.

Turning OFF devices in Room 2

Air Conditioner is now OFF.
Smart Light is now OFF.
```

---

## Problems With This Approach

Although this implementation works for a small smart home, it quickly becomes difficult to maintain as the hierarchy grows.

---

### 1. The Controller Manages the Entire Hierarchy

The controller manually knows

- every room
- every device
- every floor

For example,

```java
room1AC.turnOn();
room1Light.turnOn();

room2AC.turnOn();
room2Light.turnOn();
```

As the hierarchy grows,

the controller keeps growing.

It becomes responsible for traversing the entire smart home.

---

### 2. Logic Is Repeated Everywhere

Notice that the same logic appears repeatedly.

```java
device1.turnOn();

device2.turnOn();

device3.turnOn();
```

Later,

the same code is written for

- Room 1
- Room 2
- Floor 1
- Floor 2

The application becomes full of duplicate traversal logic.

---

### 3. Tight Coupling

The controller directly depends on

```text
AirConditioner

SmartLight

...
```

Tomorrow,

if a new device is introduced,

```text
CoffeeMachine
```

the controller must change.

Similarly,

if a new group appears,

```text
Garage
```

the controller again needs modification.

---

### 4. Difficult To Support Hierarchies

Suppose the Product Owner introduces

```text
House

↓

Floor

↓

Zone

↓

Room

↓

Devices
```

The controller now needs nested loops.

```java
for (Floor floor : house) {

    for (Room room : floor) {

        for (Device device : room) {

            device.turnOn();

        }

    }

}
```

As more hierarchy levels appear,

the traversal logic becomes increasingly complicated.

---

### 5. Violates the Open/Closed Principle

Tomorrow,

we introduce

```text
Garden
```

or

```text
Garage
```

Both can contain devices.

The controller must again be modified.

Instead of extending the application,

we keep changing already-tested code.

---

### 6. The Client Shouldn't Care About The Hierarchy

Ask yourself,

does the Smart Home Controller really care whether it is controlling

- one Air Conditioner
- one Room
- one Floor
- one House

No.

It simply wants to execute

```java
turnOn();

turnOff();
```

The hierarchy should hide itself from the client.

---

## The Real Problem

Notice something important.

The problem is **not** turning devices ON.

The problem is traversing an ever-growing hierarchy.

The client shouldn't need to know

```text
House

↓

Floor

↓

Room

↓

Device
```

Instead,

it should simply write

```java
component.turnOn();
```

Whether the component represents

- one device

or

- an entire house

shouldn't matter.

This is exactly the problem the **Composite Pattern** solves.

</details>

---

# Applying the Composite Pattern

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## SmartComponent.java

```java
// Component
// Common interface for both individual devices and groups of devices.
public interface SmartComponent {

    void turnOn();

    void turnOff();

}
```

<details>
<summary>💡 Why do we need a common interface?</summary>

A common question is:

> **Why can't we directly work with `AirConditioner`, `SmartLight`, `Room`, and `Floor` separately?**

Initially, that works perfectly.

```java
AirConditioner ac = new AirConditioner();

ac.turnOn();
```

Similarly,

```java
Room room = new Room();

room.turnOn();
```

But now the client needs to know

- Is this a device?
- Is this a room?
- Is this a floor?
- Is this a house?

The client starts treating every object differently.

Instead, we introduce one common abstraction.

```java
public interface SmartComponent {

    void turnOn();

    void turnOff();

}
```

Now everything becomes a **SmartComponent**.

Whether it represents

- a single device
- a room
- a floor
- an entire house

the client always writes

```java
component.turnOn();
```

The client no longer cares about the hierarchy.

This is the biggest benefit of the Composite Pattern.

</details>

---

## AirConditioner.java (Leaf)

```java
// Leaf Node
// Represents an individual smart device.
public class AirConditioner implements SmartComponent {

    @Override
    public void turnOn() {
        System.out.println("Air Conditioner is now ON.");
    }

    @Override
    public void turnOff() {
        System.out.println("Air Conditioner is now OFF.");
    }

}
```

---

## SmartLight.java (Leaf)

```java
// Leaf Node
// Represents an individual smart device.
public class SmartLight implements SmartComponent {

    @Override
    public void turnOn() {
        System.out.println("Smart Light is now ON.");
    }

    @Override
    public void turnOff() {
        System.out.println("Smart Light is now OFF.");
    }

}
```

---

## Room.java (Composite)

```java
import java.util.ArrayList;
import java.util.List;

// Composite
// Represents a room containing multiple smart components.
public class Room implements SmartComponent {

    private final String name;

    private final List<SmartComponent> components = new ArrayList<>();

    public Room(String name) {
        this.name = name;
    }

    public void addComponent(SmartComponent component) {
        components.add(component);
    }

    @Override
    public void turnOn() {

        System.out.println("Turning ON " + name);

        for (SmartComponent component : components) {
            component.turnOn();
        }

    }

    @Override
    public void turnOff() {

        System.out.println("Turning OFF " + name);

        for (SmartComponent component : components) {
            component.turnOff();
        }

    }

}
```

<details>
<summary>💡 Why is Room also a SmartComponent?</summary>

At first glance,

a room is **not** a smart device.

So why does it implement

```java
SmartComponent
```

Because the client wants to use the same operation for both.

Without Composite,

the client would write

```java
if (object instanceof Room) {

    ...

}
else if (object instanceof AirConditioner) {

    ...

}
```

The client keeps checking object types.

Instead,

we let every object expose the same interface.

A room doesn't turn ON itself.

Instead,

it delegates the request to all its children.

```text
Room

↓

Air Conditioner

↓

Smart Light
```

This allows the client to treat

```text
Room
```

and

```text
Air Conditioner
```

exactly the same way.

</details>

---

## Floor.java (Composite)

```java
import java.util.ArrayList;
import java.util.List;

// Composite
// Represents a floor containing multiple rooms.
public class Floor implements SmartComponent {

    private final String name;

    private final List<SmartComponent> components = new ArrayList<>();

    public Floor(String name) {
        this.name = name;
    }

    public void addComponent(SmartComponent component) {
        components.add(component);
    }

    @Override
    public void turnOn() {

        System.out.println("Turning ON " + name);

        for (SmartComponent component : components) {
            component.turnOn();
        }

    }

    @Override
    public void turnOff() {

        System.out.println("Turning OFF " + name);

        for (SmartComponent component : components) {
            component.turnOff();
        }

    }

}
```

---

## House.java (Composite)

```java
import java.util.ArrayList;
import java.util.List;

// Composite
// Represents the complete smart home.
public class House implements SmartComponent {

    private final List<SmartComponent> components = new ArrayList<>();

    public void addComponent(SmartComponent component) {
        components.add(component);
    }

    @Override
    public void turnOn() {

        System.out.println("Turning ON House");

        for (SmartComponent component : components) {
            component.turnOn();
        }

    }

    @Override
    public void turnOff() {

        System.out.println("Turning OFF House");

        for (SmartComponent component : components) {
            component.turnOff();
        }

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        // -----------------------------
        // Create Individual Devices
        // -----------------------------

        SmartComponent livingRoomAC = new AirConditioner();
        SmartComponent livingRoomLight = new SmartLight();

        SmartComponent bedRoomAC = new AirConditioner();
        SmartComponent bedRoomLight = new SmartLight();

        // -----------------------------
        // Create Rooms
        // -----------------------------

        Room livingRoom = new Room("Living Room");

        livingRoom.addComponent(livingRoomAC);
        livingRoom.addComponent(livingRoomLight);

        Room bedRoom = new Room("Bed Room");

        bedRoom.addComponent(bedRoomAC);
        bedRoom.addComponent(bedRoomLight);

        // -----------------------------
        // Create Floor
        // -----------------------------

        Floor firstFloor = new Floor("First Floor");

        firstFloor.addComponent(livingRoom);
        firstFloor.addComponent(bedRoom);

        // -----------------------------
        // Create House
        // -----------------------------

        House house = new House();

        house.addComponent(firstFloor);

        // ====================================================
        // 1. Operate Individual Device (Leaf)
        // ====================================================

        System.out.println("======== Individual Device ========");

        livingRoomAC.turnOn();
        livingRoomAC.turnOff();

        // ====================================================
        // 2. Operate Room (Composite)
        // ====================================================

        System.out.println("\n======== Living Room ========");

        livingRoom.turnOn();
        livingRoom.turnOff();

        // ====================================================
        // 3. Operate Floor (Composite)
        // ====================================================

        System.out.println("\n======== First Floor ========");

        firstFloor.turnOn();
        firstFloor.turnOff();

        // ====================================================
        // 4. Operate Entire House (Root Composite)
        // ====================================================

        System.out.println("\n======== Complete House ========");

        house.turnOn();
        house.turnOff();

    }

}
```

---

## What This Demonstrates

Notice something interesting.

The exact same operations

```java
turnOn();

turnOff();
```

work for

```text
✓ Air Conditioner

✓ Room

✓ Floor

✓ House
```

The client never changes.

Only the object changes.

This is the biggest strength of the **Composite Pattern**.

The client doesn't care whether it is controlling

- a single object
- a group of objects
- or the entire object hierarchy.

All of them are treated uniformly because they implement the same interface:

```java
SmartComponent
```

This is exactly why the Composite Pattern exists.
```

---

## Output

```text
Turning ON House

Turning ON First Floor

Turning ON Living Room

Air Conditioner is now ON.
Smart Light is now ON.

Turning ON Bedroom

Air Conditioner is now ON.
Smart Light is now ON.

Turning OFF House

Turning OFF First Floor

Turning OFF Living Room

Air Conditioner is now OFF.
Smart Light is now OFF.

Turning OFF Bedroom

Air Conditioner is now OFF.
Smart Light is now OFF.
```

---

## Execution Flow

```text
Client

    │

house.turnOn()

    │

    ▼

House

    │

Delegates to Floors

    │

    ▼

Floor

    │

Delegates to Rooms

    │

    ▼

Room

    │

Delegates to Devices

    │

    ▼

Air Conditioner

Smart Light

    │

Actual Operation Executes
```

Notice something important.

The **client never traverses the hierarchy**.

It simply writes

```java
house.turnOn();
```

Each composite is responsible for forwarding the request to its own children.

This recursive delegation is the heart of the **Composite Pattern**.

</details>
