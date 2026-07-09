# Bridge Design Pattern

> **Category:** Structural Design Pattern

---

# Sources

## 🎥 YouTube

https://www.youtube.com/watch?v=okr_avFJg8Y

## 🌐 Blog

https://codewitharyan.com/tech-blogs/bridge-design-pattern

---

# How to Think Towards the Bridge Pattern

Forget the Bridge Pattern for a moment.

Imagine you're building a **Drawing Application**.

Initially,

your application supports only two shapes.

```text
Circle

Rectangle
```

Whenever a user wants to draw a shape,

the application simply draws it on the screen.

Everything works perfectly.

---

## A New Requirement Arrives

Now the Product Owner says,

> "Our application should support different rendering methods."

For now,

we need

```text
Raster Rendering
```

A natural implementation would be

```text
Circle

↓

Raster Draw
```

```text
Rectangle

↓

Raster Draw
```

Everything still looks fine.

---

## Another Requirement

Later,

another rendering engine is added.

```text
Vector Rendering
```

Now every shape must support both rendering methods.

```text
Circle

↓

Raster Draw

Vector Draw
```

```text
Rectangle

↓

Raster Draw

Vector Draw
```

Still manageable.

---

## Requirements Keep Growing

Soon,

more rendering technologies arrive.

```text
3D Rendering

Wireframe Rendering
```

Now every shape must support

```text
Raster

Vector

3D

Wireframe
```

For two shapes,

this is still acceptable.

---

## But Then...

The application starts supporting more shapes.

```text
Circle

Rectangle

Triangle

Square

Hexagon
```

Every shape now needs

```text
Raster Rendering

Vector Rendering

3D Rendering

Wireframe Rendering
```

Let's count.

```text
5 Shapes

×

4 Rendering Methods

=

20 Combinations
```

Tomorrow,

if another rendering method is introduced,

```text
OpenGL Rendering
```

every single shape must be modified again.

---

## Stop and Think

Ask yourself.

What actually changes here?

There are **two completely different dimensions**.

### Dimension 1

The shape.

```text
Circle

Rectangle

Triangle

Square
```

### Dimension 2

The rendering technology.

```text
Raster

Vector

3D

Wireframe
```

These two things are completely independent.

A new shape doesn't affect rendering.

A new renderer doesn't affect shapes.

Yet,

our current design mixes both together.

---

## Why Is This a Problem?

Suppose tomorrow we add

```text
Pentagon
```

Now we must support

```text
Pentagon + Raster

Pentagon + Vector

Pentagon + 3D

Pentagon + Wireframe
```

Next,

another rendering method arrives.

```text
SVG Rendering
```

Again,

every existing shape changes.

Notice the pattern.

Whenever one side changes,

the other side also changes.

This is exactly what creates the maintenance problem.

---

## Think Like Real Life

Imagine Microsoft Word.

A document can be

```text
Normal Mode

↓

Dark Theme
```

or

```text
Normal Mode

↓

Light Theme
```

The document itself doesn't change.

Only the way it is displayed changes.

Similarly,

a shape remains

```text
Circle
```

Whether it is rendered using

```text
Raster

or

Vector

or

3D
```

it is still the same Circle.

The rendering technology is simply another independent concern.

---

## First Refactoring

Instead of making

```text
Circle
```

responsible for

```text
Raster

Vector

3D

Wireframe
```

we separate the rendering logic.

Now,

the Circle simply says

> "I know I need to draw myself."

But it doesn't know

**how** it will be drawn.

That responsibility is delegated.

---

## The Beautiful Observation

Shapes and rendering methods can evolve independently.

We should not combine them into one hierarchy.

Instead,

they should collaborate.

```text
Circle

──────────────►

Renderer
```

The Circle only knows

```text
Draw yourself using this renderer.
```

The renderer decides

```text
Raster

Vector

3D

Wireframe
```

---

## Final Design

Instead of coupling

```text
Shape

+

Rendering
```

inside the same class,

we separate them.

```text
Shape

↓

has a

↓

Renderer
```

Now,

changing a renderer

doesn't affect any shape.

Adding a new shape

doesn't affect any renderer.

Both can evolve independently.

---

## Complete Thinking Process

```text
Need Drawing Application

        ↓

Need Multiple Shapes

        ↓

Need Multiple Rendering Methods

        ↓

Every Shape Supports Every Renderer

        ↓

Combinations Keep Increasing

        ↓

Two Independent Dimensions

        ↓

Separate Shape from Renderer

        ↓

Connect Them Using Composition

        ↓

Bridge Design Pattern
```

---

# How to Remember the Bridge Pattern

Imagine buying a TV.

The TV can be

```text
Sony

Samsung

LG
```

The remote can be

```text
Basic Remote

Smart Remote

Voice Remote
```

The TV brand and the remote type are independent.

You don't manufacture

```text
SonyWithBasicRemote

SonyWithSmartRemote

SamsungWithBasicRemote

SamsungWithVoiceRemote
```

Instead,

every TV simply works with a remote.

```text
Sony TV

↓

Remote
```

```text
Samsung TV

↓

Remote
```

The TV doesn't care which remote it receives.

Similarly,

the remote doesn't care which TV it controls.

Both evolve independently.

Exactly the same happens in the **Bridge Pattern**.

---

## Memory Trick

> **When two independent dimensions keep growing separately, don't combine them using inheritance. Connect them using composition.**

---

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Circle.java

```java
// Circle with Raster Rendering
public class RasterCircle {

    public void draw() {

        System.out.println("Drawing Circle");

        System.out.println("Rendering using Raster Engine");

    }

}
```

---

## Rectangle.java

```java
// Rectangle with Raster Rendering
public class RasterRectangle {

    public void draw() {

        System.out.println("Drawing Rectangle");

        System.out.println("Rendering using Raster Engine");

    }

}
```

---

## VectorCircle.java

```java
// Circle with Vector Rendering
public class VectorCircle {

    public void draw() {

        System.out.println("Drawing Circle");

        System.out.println("Rendering using Vector Engine");

    }

}
```

---

## VectorRectangle.java

```java
// Rectangle with Vector Rendering
public class VectorRectangle {

    public void draw() {

        System.out.println("Drawing Rectangle");

        System.out.println("Rendering using Vector Engine");

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        RasterCircle rasterCircle = new RasterCircle();

        rasterCircle.draw();

        System.out.println();

        VectorCircle vectorCircle = new VectorCircle();

        vectorCircle.draw();

        System.out.println();

        RasterRectangle rasterRectangle = new RasterRectangle();

        rasterRectangle.draw();

        System.out.println();

        VectorRectangle vectorRectangle = new VectorRectangle();

        vectorRectangle.draw();

    }

}
```

---

## Output

```text
Drawing Circle
Rendering using Raster Engine

Drawing Circle
Rendering using Vector Engine

Drawing Rectangle
Rendering using Raster Engine

Drawing Rectangle
Rendering using Vector Engine
```

---

## Problems With This Approach

Although this implementation works initially, it becomes difficult to maintain as both **Shapes** and **Rendering Methods** continue to grow.

---

### 1. Class Explosion

Initially, we have

```text
Circle

Rectangle
```

and

```text
Raster Rendering
```

Only two classes are required.

```text
RasterCircle

RasterRectangle
```

Later,

Vector Rendering is introduced.

Now we need

```text
RasterCircle

VectorCircle

RasterRectangle

VectorRectangle
```

The number of classes immediately doubles.

---

### 2. Every New Shape Creates Multiple Classes

Suppose tomorrow we introduce

```text
Triangle
```

Now we need

```text
RasterTriangle

VectorTriangle
```

If another renderer already exists,

even more classes are required.

One new shape creates multiple new classes.

---

### 3. Every New Renderer Creates Multiple Classes

Suppose another rendering engine is introduced.

```text
OpenGL Renderer
```

Now every existing shape requires another class.

```text
OpenGLCircle

OpenGLRectangle

OpenGLTriangle

...
```

One new renderer forces changes across the entire hierarchy.

---

### 4. Two Independent Dimensions Are Mixed Together

Notice something interesting.

A shape represents

```text
What to draw
```

A renderer represents

```text
How to draw
```

These are two completely independent responsibilities.

Yet,

our design combines both into a single class.

```text
RasterCircle
```

contains

```text
Shape

+

Rendering Method
```

This unnecessarily couples two independent concepts.

---

### 5. Difficult to Scale

Imagine supporting

```text
10 Shapes
```

and

```text
8 Rendering Engines
```

The number of required classes becomes

```text
10 × 8 = 80 Classes
```

Most of these classes differ only in

- Shape
- Rendering Technology

The project becomes harder to maintain as it grows.

---

### 6. Poor Maintainability

Suppose the rendering logic changes.

Every class using that renderer must be updated.

Similarly,

adding a new shape requires creating one class for every renderer.

Both hierarchies become tightly coupled.

---

## The Real Problem

Notice something important.

There are two independent dimensions.

```text
Shapes

↓

Circle

Rectangle

Triangle
```

and

```text
Rendering Methods

↓

Raster

Vector

OpenGL
```

These two dimensions evolve independently.

Adding a new shape shouldn't require modifying rendering classes.

Adding a new renderer shouldn't require creating new shape classes.

Instead of combining both using inheritance,

we need a way to separate them while still allowing them to work together.

This is exactly the problem the **Bridge Pattern** solves.

</details>

---

# Applying the Bridge Pattern

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Renderer.java (Implementor)

```java
// Implementor
// Defines the rendering interface.
public interface Renderer {

    void render(String shape);

}
```

<details>
<summary>💡 Why do we need a Renderer interface?</summary>

A common question is:

> **Why can't every shape directly implement Raster or Vector rendering?**

Initially, that works.

For example,

```java
class RasterCircle
```

or

```java
class VectorCircle
```

But as soon as another rendering engine arrives,

```text
OpenGL
```

every shape needs another class.

Similarly,

when another shape arrives,

every renderer needs another class.

The problem is that **rendering is an independent concern.**

A Circle doesn't care

- whether it is rendered using Raster,
- Vector,
- or OpenGL.

It only knows

> "I need someone to render me."

That's why we introduce

```java
Renderer
```

Now every rendering engine follows the same contract.

Shapes simply delegate the rendering work to it.

</details>

---

## RasterRenderer.java (Concrete Implementor)

```java
// Concrete Implementor
// Renders shapes using the Raster engine.
public class RasterRenderer implements Renderer {

    @Override
    public void render(String shape) {

        System.out.println("Rendering " + shape + " using Raster Renderer.");

    }

}
```

---

## VectorRenderer.java (Concrete Implementor)

```java
// Concrete Implementor
// Renders shapes using the Vector engine.
public class VectorRenderer implements Renderer {

    @Override
    public void render(String shape) {

        System.out.println("Rendering " + shape + " using Vector Renderer.");

    }

}
```

---

## Shape.java (Abstraction)

```java
// Abstraction
// Represents a shape that delegates rendering.
public abstract class Shape {

    protected Renderer renderer;

    public Shape(Renderer renderer) {
        this.renderer = renderer;
    }

    public abstract void draw();

}
```

<details>
<summary>💡 Why do we need an abstract Shape?</summary>

This is the heart of the Bridge Pattern.

Notice that

```text
Circle
```

is still a Shape,

whether it is rendered using

- Raster
- Vector
- OpenGL

The rendering method doesn't change

what the object is.

Instead,

a Shape simply stores a reference to

```java
Renderer
```

Whenever it needs to draw itself,

it delegates the work.

```java
renderer.render(...);
```

This creates a bridge between

```text
Shape

↓

Renderer
```

without tightly coupling them.

Now both hierarchies can evolve independently.

</details>

---

## Circle.java (Refined Abstraction)

```java
// Refined Abstraction
// Represents a Circle.
public class Circle extends Shape {

    public Circle(Renderer renderer) {
        super(renderer);
    }

    @Override
    public void draw() {

        renderer.render("Circle");

    }

}
```

---

## Rectangle.java (Refined Abstraction)

```java
// Refined Abstraction
// Represents a Rectangle.
public class Rectangle extends Shape {

    public Rectangle(Renderer renderer) {
        super(renderer);
    }

    @Override
    public void draw() {

        renderer.render("Rectangle");

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        Renderer rasterRenderer = new RasterRenderer();
        Renderer vectorRenderer = new VectorRenderer();

        Shape circle = new Circle(rasterRenderer);

        Shape rectangle = new Rectangle(vectorRenderer);

        Shape vectorCircle = new Circle(vectorRenderer);

        Shape rasterRectangle = new Rectangle(rasterRenderer);

        circle.draw();

        rectangle.draw();

        vectorCircle.draw();

        rasterRectangle.draw();

    }

}
```

---

## Output

```text
Rendering Circle using Raster Renderer.

Rendering Rectangle using Vector Renderer.

Rendering Circle using Vector Renderer.

Rendering Rectangle using Raster Renderer.
```

---

## Execution Flow

### Example

```java
Shape shape = new Circle(new RasterRenderer());

shape.draw();
```

Execution Flow

```text
Client

        │

new Circle(new RasterRenderer())

        │

        ▼

Circle

stores

RasterRenderer

        │

shape.draw()

        │

        ▼

Circle

        │

Delegates

        │

renderer.render("Circle")

        │

        ▼

RasterRenderer

        │

Performs Raster Rendering

        ▼

Circle Rendered
```

Notice something important.

The

```text
Circle
```

never performs the rendering itself.

Instead,

it delegates the responsibility to the

```java
Renderer
```

Similarly,

the renderer doesn't know

which shape it is rendering.

Both hierarchies remain independent while collaborating through composition.

This is the core idea behind the **Bridge Pattern**.

</details>

---

# Handling New Requirements

## New Requirement 1

The Product Owner says,

> **"We now need to support Triangle."**

### Without the Bridge Pattern

We create

```text
RasterTriangle

VectorTriangle

OpenGLTriangle

...
```

One new shape creates multiple new classes.

---

### With the Bridge Pattern

Simply create

```java
public class Triangle extends Shape {

    public Triangle(Renderer renderer) {
        super(renderer);
    }

    @Override
    public void draw() {

        renderer.render("Triangle");

    }

}
```

No renderer changes.

Existing renderers automatically support Triangle.

---

## New Requirement 2

Now the Product Owner says,

> **"Support OpenGL Rendering."**

### Without the Bridge Pattern

Every existing shape needs another class.

```text
OpenGLCircle

OpenGLRectangle

OpenGLTriangle

...
```

---

### With the Bridge Pattern

Simply create

```java
public class OpenGLRenderer implements Renderer {

    @Override
    public void render(String shape) {

        System.out.println(
                "Rendering " + shape + " using OpenGL Renderer.");

    }

}
```

Now every existing shape automatically supports OpenGL.

```java
Shape circle = new Circle(new OpenGLRenderer());

circle.draw();
```

No shape classes change.

This follows the **Open/Closed Principle** perfectly.

</details>
