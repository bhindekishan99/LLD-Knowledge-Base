# Flyweight Design Pattern

> **Category:** Structural Design Pattern

---

# Sources

## 🎥 YouTube

https://www.youtube.com/watch?v=tLw0A1rYDXg

## 🌐 Blog

https://codewitharyan.com/tech-blogs/flyweight-design-pattern

---

# How to Think Towards the Flyweight Pattern

Forget the Flyweight Pattern for a moment.

Imagine you're building a **Forest Simulation Game**.

Initially,

your game contains only a few trees.

```text
Oak Tree

Pine Tree

Mango Tree
```

Every tree stores all its information.

```text
Type

↓

Color

↓

Texture

↓

Height

↓

X Coordinate

↓

Y Coordinate
```

Everything works perfectly.

---

## A New Requirement Arrives

The Product Owner says,

> **"The forest should contain 100 trees."**

No problem.

We simply create

```text
100 Tree Objects
```

Each object stores

```text
Type

Color

Texture

Height

X Position

Y Position
```

Still manageable.

---

## Another Requirement

Now the Product Owner says,

> **"Increase the forest size to 10,000 trees."**

Again,

we create

```text
10,000 Tree Objects
```

Each tree stores exactly the same information.

For example,

```text
Oak Tree #1

Type = Oak

Color = Green

Texture = OakTexture.png

Height = 15m

X = 100

Y = 200
```

---

```text
Oak Tree #2

Type = Oak

Color = Green

Texture = OakTexture.png

Height = 15m

X = 350

Y = 420
```

---

```text
Oak Tree #3

Type = Oak

Color = Green

Texture = OakTexture.png

Height = 15m

X = 780

Y = 510
```

Notice something.

Almost everything is repeated.

The only thing changing is

```text
X Position

Y Position
```

Everything else is identical.

---

## Stop and Think

Ask yourself.

Do we really need to store

```text
Oak

Green

OakTexture.png

15m
```

inside every Oak tree?

Imagine

```text
5,000 Oak Trees
```

That means

```text
"Oak"

stored

5,000 times
```

Similarly,

```text
OakTexture.png

stored

5,000 times
```

This is unnecessary memory consumption.

---

## Requirements Keep Growing

Soon,

the game supports

```text
100,000 Trees
```

Then

```text
500,000 Trees
```

Then

```text
1 Million Trees
```

Now the application starts consuming huge amounts of memory.

Not because the forest is large,

but because

the same information is stored again and again.

---

## The Beautiful Observation

Every tree contains two types of data.

### Data that never changes

```text
Tree Type

↓

Color

↓

Texture

↓

Height
```

Every Oak tree shares this information.

---

### Data that changes

```text
X Position

↓

Y Position
```

Every tree has its own location.

---

These two types of data are completely different.

One is

```text
Shared
```

The other is

```text
Unique
```

Yet,

our current design stores both inside every object.

---

## First Refactoring

Instead of storing

```text
Oak

Green

Texture

Height
```

inside every Oak tree,

we store it only once.

Now,

every Oak tree simply points to

```text
Oak Tree Type
```

The tree itself stores only

```text
X Position

Y Position
```

---

## Final Design

Separate the data into

```text
Intrinsic State

(Shared)
```

and

```text
Extrinsic State

(Unique)
```

```text
Tree

↓

TreeType
```

Now,

thousands of trees can share

the same

```text
TreeType
```

object.

Instead of creating

```text
5000 Oak Objects
```

we create

```text
1 OakTreeType

↓

Shared by

↓

5000 Oak Trees
```

Memory usage drops dramatically.

---

## Complete Thinking Process

```text
Need Forest Game

        ↓

Need Thousands of Trees

        ↓

Each Tree Stores Same Information

        ↓

Large Memory Consumption

        ↓

Identify Shared Data

        ↓

Store Shared Data Only Once

        ↓

Each Object Stores Only Unique Data

        ↓

Share Common Objects

        ↓

Flyweight Design Pattern
```

---

# How to Remember the Flyweight Pattern

Imagine a classroom.

There are

```text
100 Students
```

Every student studies

the same textbook.

Would you print

```text
100 identical books
```

inside every student object?

Of course not.

Instead,

the school keeps

one textbook design,

and every student refers to it.

Each student only has

```text
Name

↓

Roll Number

↓

Marks
```

The textbook is shared.

Similarly,

every Oak tree shares

the same

```text
TreeType
```

object.

Only

```text
Position
```

is unique.

---

## Memory Trick

> **When thousands of objects contain the same data, don't duplicate it. Share it.**

Or remember it as

> **Share the common state, store only the unique state.**

---

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Tree.java

```java
// Represents a tree in the forest.
public class Tree {

    private String type;
    private String color;
    private String texture;
    private double height;

    private int x;
    private int y;

    public Tree(String type,
                String color,
                String texture,
                double height,
                int x,
                int y) {

        this.type = type;
        this.color = color;
        this.texture = texture;
        this.height = height;

        this.x = x;
        this.y = y;

    }

    public void display() {

        System.out.println(
                type +
                " Tree at (" + x + ", " + y + ")" +
                " Color: " + color +
                ", Height: " + height +
                "m");

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        Tree tree1 = new Tree(
                "Oak",
                "Green",
                "OakTexture.png",
                15,
                10,
                20);

        Tree tree2 = new Tree(
                "Oak",
                "Green",
                "OakTexture.png",
                15,
                30,
                40);

        Tree tree3 = new Tree(
                "Oak",
                "Green",
                "OakTexture.png",
                15,
                50,
                60);

        tree1.display();

        tree2.display();

        tree3.display();

    }

}
```

---

## Output

```text
Oak Tree at (10, 20) Color: Green, Height: 15.0m

Oak Tree at (30, 40) Color: Green, Height: 15.0m

Oak Tree at (50, 60) Color: Green, Height: 15.0m
```

---

## Problems With This Approach

Initially,

everything looks fine.

We simply create one object for every tree.

---

### 1. Duplicate Data

Notice something.

Every Oak tree stores

```text
Type

↓

Oak
```

```text
Color

↓

Green
```

```text
Texture

↓

OakTexture.png
```

```text
Height

↓

15m
```

Again and again.

Only the position changes.

```text
X Coordinate

Y Coordinate
```

The remaining data is identical.

---

### 2. Huge Memory Consumption

Suppose the game contains

```text
100,000 Oak Trees
```

Now we store

```text
"Oak"
```

one hundred thousand times.

Similarly,

```text
Green
```

```text
OakTexture.png
```

```text
15m
```

are also stored

100,000 times.

This wastes a huge amount of memory.

---

### 3. Memory Usage Grows Rapidly

Imagine supporting

```text
1 Million Trees
```

Every object stores

```text
Tree Type

Color

Texture

Height

Position
```

Even though

only

```text
Position
```

is unique.

Most of the memory is occupied by duplicate data.

---

### 4. Difficult to Maintain

Suppose tomorrow,

the texture of every Oak tree changes.

Instead of

```text
OakTexture.png
```

we now use

```text
OakHDTexture.png
```

Since every tree stores its own texture,

all Oak tree objects must be updated.

The same problem occurs for

- Color
- Height
- Tree Type

---

### 5. Poor Scalability

As the forest grows,

memory usage grows almost linearly.

```text
10 Trees

↓

100 Trees

↓

10,000 Trees

↓

1,000,000 Trees
```

Most of the additional memory is spent storing

the same information repeatedly.

---

## The Real Problem

Notice something important.

Every Tree contains two different kinds of data.

### Shared Data

```text
Tree Type

↓

Color

↓

Texture

↓

Height
```

Every Oak tree shares these values.

---

### Unique Data

```text
X Position

↓

Y Position
```

Every tree has its own position.

Our current design stores

both kinds of data

inside every object.

This causes unnecessary duplication.

Instead,

we should store the shared information only once,

and let every tree reference it.

This is exactly the problem the **Flyweight Pattern** solves.

</details>

---

# Applying the Flyweight Pattern

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## TreeType.java (Flyweight)

```java
// Flyweight
// Stores the shared (intrinsic) state of a tree.
public class TreeType {

    private String type;
    private String color;
    private String texture;
    private double height;

    public TreeType(String type,
                    String color,
                    String texture,
                    double height) {

        this.type = type;
        this.color = color;
        this.texture = texture;
        this.height = height;

    }

    public void display(int x, int y) {

        System.out.println(
                type +
                " Tree at (" + x + ", " + y + ")" +
                " Color: " + color +
                ", Height: " + height + "m");

    }

}
```

<details>
<summary>💡 Why do we need TreeType?</summary>

This is the most important part of the Flyweight Pattern.

Initially,

every Tree stored

```text
Type

↓

Color

↓

Texture

↓

Height

↓

Position
```

Notice something.

For every Oak tree,

```text
Type

Color

Texture

Height
```

never change.

Only

```text
X Position

Y Position
```

are different.

So why should we store

```text
Oak

Green

OakTexture.png

15m
```

inside every object?

Instead,

we store this information only once inside

```java
TreeType
```

Now,

thousands of Oak trees can share the same object.

This saves a huge amount of memory.

</details>

---

## Tree.java (Context)

```java
// Context
// Stores only the unique (extrinsic) state.
public class Tree {

    private int x;
    private int y;

    private TreeType treeType;

    public Tree(int x,
                int y,
                TreeType treeType) {

        this.x = x;
        this.y = y;
        this.treeType = treeType;

    }

    public void display() {

        treeType.display(x, y);

    }

}
```

<details>
<summary>💡 Why does Tree store only the position?</summary>

Notice something.

Every tree has a different

```text
X Position

↓

Y Position
```

These values cannot be shared.

Every tree occupies a different location in the forest.

That's why

```text
Position
```

remains inside

```java
Tree
```

while

```text
Type

Color

Texture

Height
```

are moved to

```java
TreeType
```

This separation is the heart of the Flyweight Pattern.

</details>

---

## TreeFactory.java (Flyweight Factory)

```java
import java.util.HashMap;
import java.util.Map;

// Flyweight Factory
// Creates and reuses TreeType objects.
public class TreeFactory {

    private static final Map<String, TreeType> treeTypes =
            new HashMap<>();

    public static TreeType getTreeType(String type,
                                       String color,
                                       String texture,
                                       double height) {

        String key = type + color + texture + height;

        return treeTypes.computeIfAbsent(
                key,
                k -> new TreeType(
                        type,
                        color,
                        texture,
                        height));

    }

}

```

<details>
<summary>💡 Why do we need a Factory?</summary>

Suppose we don't use a factory.

Every time we write

```java
new TreeType(...)
```

a new object is created.

Imagine

```text
5000 Oak Trees
```

Now we accidentally create

```text
5000 TreeType objects
```

again.

We lose all the memory savings.

The purpose of the factory is to ensure

```text
One Shared Object

↓

Many Tree Objects
```

Whenever a matching TreeType already exists,

the factory simply returns the existing object.

Otherwise,

it creates one.

This guarantees that shared data is stored only once.

</details>

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        TreeType oak =
                TreeFactory.getTreeType(
                        "Oak",
                        "Green",
                        "OakTexture.png",
                        15);

        Tree tree1 = new Tree(10, 20, oak);

        Tree tree2 = new Tree(30, 40, oak);

        Tree tree3 = new Tree(50, 60, oak);

        tree1.display();

        tree2.display();

        tree3.display();

    }

}
```

---

## Output

```text
Oak Tree at (10, 20) Color: Green, Height: 15.0m

Oak Tree at (30, 40) Color: Green, Height: 15.0m

Oak Tree at (50, 60) Color: Green, Height: 15.0m
```

---

## Execution Flow

### Example

```java
TreeType oak =
        TreeFactory.getTreeType(...);

Tree tree =
        new Tree(10, 20, oak);

tree.display();
```

Execution happens as follows.

```text
Client

        │

Requests

Oak TreeType

        │

        ▼

TreeFactory

        │

Already Exists?

        │

YES

        │

Returns Existing TreeType

        │

        ▼

Tree

stores

X Position

Y Position

Reference to TreeType

        │

display()

        │

        ▼

TreeType

Uses Shared Data

+

Tree Position

        ▼

Displays Tree
```

Notice something important.

The

```text
Tree
```

doesn't store

```text
Type

Color

Texture

Height
```

It only stores

```text
Position
```

Whenever it needs the remaining information,

it simply delegates to

```java
TreeType
```

Thousands of Tree objects can safely share the same TreeType object.

This is the core idea behind the **Flyweight Pattern**.

</details>

---

# Handling New Requirements

## New Requirement 1

The Product Owner says,

> **"Support Pine Trees."**

### Without the Flyweight Pattern

Every Pine tree stores

```text
Type

Color

Texture

Height
```

again.

Thousands of Pine trees duplicate the same data.

---

### With the Flyweight Pattern

Simply create one new Flyweight.

```java
TreeType pine =
        TreeFactory.getTreeType(
                "Pine",
                "Dark Green",
                "PineTexture.png",
                18);
```

Now,

every Pine tree shares this object.

```java
Tree tree =
        new Tree(120, 250, pine);
```

No existing classes change.

---

## New Requirement 2

Now the Product Owner says,

> **"The forest should support one million trees."**

### Without the Flyweight Pattern

Memory usage increases dramatically because every object stores

```text
Type

Color

Texture

Height
```

again and again.

---

### With the Flyweight Pattern

Memory usage grows mainly because of

```text
Position
```

since

```text
TreeType
```

objects are reused.

Whether there are

```text
100

↓

10,000

↓

1,000,000
```

Oak trees,

there is still only

```text
One Oak TreeType
```

shared by all of them.

This significantly reduces memory consumption and allows the application to scale efficiently.

</details>
