# Decorator Design Pattern

> **Category:** Structural Design Pattern

---

# Sources

## 🎥 YouTube

https://www.youtube.com/watch?v=dgww0AiHAY0

## 🌐 Blog

https://codewitharyan.com/tech-blogs/decorator-design-pattern

---

# How to Think Towards the Decorator Pattern

Forget the Decorator Pattern for a moment.

Imagine you're building a **Coffee Shop Ordering System**.

Initially, the shop sells only two types of coffee.

```text
Espresso

Cappuccino
```

The implementation is straightforward.

```java
Coffee espresso = new Espresso();

Coffee cappuccino = new Cappuccino();
```

Everything works perfectly.

---

## A New Requirement Arrives

Customers now want

```text
Milk
```

added to any coffee.

Our first thought is

```text
EspressoWithMilk

CappuccinoWithMilk
```

Simple enough.

---

## Another Requirement

Now customers also want

```text
Sugar
```

Again,

our natural thought is

```text
EspressoWithSugar

CappuccinoWithSugar
```

Still manageable.

---

## Requirements Keep Growing

Soon,

customers ask for

```text
Vanilla
```

Now we need

```text
EspressoWithVanilla

CappuccinoWithVanilla
```

Everything still looks okay.

---

## Now Comes the Real Problem

Customers don't order only

Milk

or

Sugar.

They order combinations.

For example,

```text
Espresso + Milk + Sugar
```

Now we create

```text
EspressoWithMilkSugar
```

Another customer wants

```text
Espresso + Milk + Vanilla
```

Now we create

```text
EspressoWithMilkVanilla
```

Another customer wants

```text
Espresso + Sugar + Vanilla
```

Now we create

```text
EspressoWithSugarVanilla
```

Another customer wants

```text
Espresso + Milk + Sugar + Vanilla
```

Now another class.

---

## Class Explosion Begins

Now think about

all coffee types.

```text
Espresso

Cappuccino

Latte

Mocha

Black Coffee
```

and

all customizations.

```text
Milk

Sugar

Vanilla

Honey

Cream

Chocolate
```

Suddenly,

your project starts looking like this.

```text
EspressoWithMilk

EspressoWithSugar

EspressoWithMilkSugar

EspressoWithVanilla

EspressoWithMilkVanilla

EspressoWithSugarVanilla

EspressoWithMilkSugarVanilla

...
```

The same thing repeats for

```text
Cappuccino

Latte

Mocha

Black Coffee
```

The number of classes grows exponentially.

---

## Stop and Think

Ask yourself.

What actually changes?

Does

```text
Espresso
```

change?

No.

The only thing changing is

the extra toppings.

```text
Milk

Sugar

Vanilla

Honey
```

These are simply

additional responsibilities

being added to the coffee.

The coffee itself remains the same.

---

## Think Like Real Life

Imagine ordering coffee at Starbucks.

You first order

```text
Espresso
```

The barista asks,

> "Would you like milk?"

You say,

```text
Yes
```

Then asks,

> "Would you like sugar?"

Again,

```text
Yes
```

Then,

> "Vanilla?"

Again,

```text
Yes
```

Notice something.

Nobody creates a brand-new coffee.

The barista simply keeps adding

one customization

on top of another.

```text
Espresso

↓

Milk

↓

Sugar

↓

Vanilla
```

Every customization wraps the previous coffee.

This is exactly how the Decorator Pattern works.

---

## First Refactoring

Instead of creating

```text
EspressoWithMilk

EspressoWithSugar

EspressoWithMilkSugar
```

we create

only

```text
Espresso
```

Then,

whenever needed,

we wrap it.

```text
Espresso

↓

Milk
```

Need sugar?

Wrap again.

```text
Espresso

↓

Milk

↓

Sugar
```

Need vanilla?

Wrap again.

```text
Espresso

↓

Milk

↓

Sugar

↓

Vanilla
```

No new subclasses.

---

## The Beautiful Observation

Every customization behaves exactly like coffee.

Milk can be treated as coffee.

Sugar can be treated as coffee.

Vanilla can be treated as coffee.

Why?

Because every customization wraps another coffee and also exposes the same interface.

This allows unlimited combinations.

---

## Final Design

Instead of

creating hundreds of subclasses,

we dynamically build coffee at runtime.

```text
Espresso

↓

MilkDecorator

↓

SugarDecorator

↓

VanillaDecorator
```

Each decorator adds one responsibility

without modifying the existing object.

---

## Complete Thinking Process

```text
Need Coffee Shop

        ↓

Base Coffee Types

        ↓

Need Milk

        ↓

Need Sugar

        ↓

Need Vanilla

        ↓

Need Every Combination

        ↓

Inheritance Explosion

        ↓

Need Dynamic Features

        ↓

Wrap Existing Object

        ↓

Decorator Pattern
```

---

# How to Remember the Decorator Pattern

Imagine ordering a pizza.

You first order

```text
Pizza
```

Then add

```text
Extra Cheese
```

Then

```text
Olives
```

Then

```text
Mushrooms
```

The restaurant doesn't create a new pizza type called

```text
PizzaWithExtraCheeseAndOlivesAndMushrooms
```

Instead,

they simply keep adding toppings.

```text
Pizza

↓

Extra Cheese

↓

Olives

↓

Mushrooms
```

Each topping wraps the previous pizza.

The base pizza never changes.

Exactly the same thing happens in the Decorator Pattern.

---

## Memory Trick

> **Decorator adds new responsibilities by wrapping an existing object, not by creating new subclasses.**

---

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Coffee.java

```java
// Base class for all coffee types.
public abstract class Coffee {

    public abstract String getDescription();

    public abstract double getCost();

}
```

---

## Espresso.java

```java
// Represents an Espresso coffee.
public class Espresso extends Coffee {

    @Override
    public String getDescription() {
        return "Espresso";
    }

    @Override
    public double getCost() {
        return 120.0;
    }

}
```

---

## Cappuccino.java

```java
// Represents a Cappuccino coffee.
public class Cappuccino extends Coffee {

    @Override
    public String getDescription() {
        return "Cappuccino";
    }

    @Override
    public double getCost() {
        return 150.0;
    }

}
```

---

## EspressoWithMilk.java

```java
// Espresso with Milk.
public class EspressoWithMilk extends Espresso {

    @Override
    public String getDescription() {
        return super.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return super.getCost() + 30;
    }

}
```

---

## EspressoWithSugar.java

```java
// Espresso with Sugar.
public class EspressoWithSugar extends Espresso {

    @Override
    public String getDescription() {
        return super.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return super.getCost() + 10;
    }

}
```

---

## EspressoWithMilkSugar.java

```java
// Espresso with Milk and Sugar.
public class EspressoWithMilkSugar extends Espresso {

    @Override
    public String getDescription() {
        return super.getDescription() + ", Milk, Sugar";
    }

    @Override
    public double getCost() {
        return super.getCost() + 30 + 10;
    }

}
```

---

## CappuccinoWithMilk.java

```java
// Cappuccino with Milk.
public class CappuccinoWithMilk extends Cappuccino {

    @Override
    public String getDescription() {
        return super.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return super.getCost() + 30;
    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        Coffee coffee1 = new Espresso();

        Coffee coffee2 = new EspressoWithMilk();

        Coffee coffee3 = new EspressoWithMilkSugar();

        Coffee coffee4 = new CappuccinoWithMilk();

        System.out.println(coffee1.getDescription()
                + " : ₹" + coffee1.getCost());

        System.out.println(coffee2.getDescription()
                + " : ₹" + coffee2.getCost());

        System.out.println(coffee3.getDescription()
                + " : ₹" + coffee3.getCost());

        System.out.println(coffee4.getDescription()
                + " : ₹" + coffee4.getCost());

    }

}
```

---

## Output

```text
Espresso : ₹120.0

Espresso, Milk : ₹150.0

Espresso, Milk, Sugar : ₹160.0

Cappuccino, Milk : ₹180.0
```

---

## Problems With This Approach

Although this implementation works initially, it becomes difficult to maintain as the number of coffee customizations grows.

---

### 1. Class Explosion

Initially, we have only two coffee types.

```text
Espresso

Cappuccino
```

Now customers request

```text
Milk
```

We create

```text
EspressoWithMilk

CappuccinoWithMilk
```

Later,

they request

```text
Sugar
```

Again,

new classes.

```text
EspressoWithSugar

CappuccinoWithSugar
```

Now customers want both.

```text
EspressoWithMilkSugar

CappuccinoWithMilkSugar
```

The number of subclasses keeps increasing.

---

### 2. Every New Combination Requires Another Class

Tomorrow customers ask for

```text
Vanilla
```

Now we need

```text
EspressoWithVanilla

CappuccinoWithVanilla

EspressoWithMilkVanilla

EspressoWithSugarVanilla

EspressoWithMilkSugarVanilla

...
```

The combinations grow exponentially.

Instead of adding one feature,

we keep creating many new classes.

---

### 3. Violates the Open/Closed Principle

Suppose we introduce a new topping.

```text
Honey
```

Now every coffee type needs multiple new subclasses.

```text
EspressoWithHoney

EspressoWithMilkHoney

EspressoWithSugarHoney

EspressoWithMilkSugarHoney

...
```

The existing design keeps expanding through inheritance.

Maintaining these subclasses becomes increasingly difficult.

---

### 4. Difficult to Reuse Features

Notice something interesting.

The logic for adding Milk is identical regardless of the coffee.

```text
Add Milk

↓

Increase Cost

↓

Update Description
```

Yet we rewrite this same logic in

```text
EspressoWithMilk

CappuccinoWithMilk

LatteWithMilk

MochaWithMilk
```

The same behavior is duplicated across many subclasses.

---

### 5. Compile-Time Customization

Suppose a customer orders

```text
Espresso

↓

Milk

↓

Sugar
```

The required class must already exist.

If tomorrow a customer orders

```text
Espresso

↓

Milk

↓

Honey

↓

Vanilla
```

and no corresponding class exists,

the system cannot create that combination.

Every possible combination must be anticipated during development.

---

### 6. Difficult to Scale

Imagine supporting

```text
10 Coffee Types
```

and

```text
8 Toppings
```

The number of subclasses becomes enormous.

Most of those classes differ only by

- Description
- Cost

This makes the design difficult to understand and maintain.

---

## The Real Problem

Notice something important.

The coffee itself never changes.

Only

```text
Milk

Sugar

Vanilla

Honey

Cream
```

keep getting added.

Instead of creating a new subclass for every possible combination,

we need a way to **add these responsibilities dynamically at runtime**.

This is exactly the problem the **Decorator Pattern** solves.
</details>
---

# Applying the Decorator Pattern

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Coffee.java (Component)

```java
// Component
// Defines the common interface for all coffee types and decorators.
public interface Coffee {

    String getDescription();

    double getCost();

}
```

<details>
<summary>💡 Why do we need a common interface?</summary>

A common question is:

> **Why can't we directly create `Espresso`, `Cappuccino`, and the decorators without introducing the `Coffee` interface?**

Initially, that works.

```java
Espresso espresso = new Espresso();
```

But now imagine we want to decorate it.

```text
Milk

↓

Espresso
```

Later,

```text
Sugar

↓

Milk

↓

Espresso
```

Now ask yourself,

what exactly is the object inside the decorator?

Sometimes it's

```text
Espresso
```

Sometimes it's

```text
MilkDecorator
```

Sometimes it's

```text
SugarDecorator
```

The decorator shouldn't care what it is wrapping.

It should simply know

> **"I'm wrapping a Coffee."**

That's why every object implements

```java
Coffee
```

Now the decorator can wrap

- Espresso
- Cappuccino
- Latte
- Another Decorator

without any changes.

</details>

---

## Espresso.java (Concrete Component)

```java
// Concrete Component
// Represents a plain Espresso.
public class Espresso implements Coffee {

    @Override
    public String getDescription() {
        return "Espresso";
    }

    @Override
    public double getCost() {
        return 120;
    }

}
```

---

## Cappuccino.java (Concrete Component)

```java
// Concrete Component
// Represents a plain Cappuccino.
public class Cappuccino implements Coffee {

    @Override
    public String getDescription() {
        return "Cappuccino";
    }

    @Override
    public double getCost() {
        return 150;
    }

}
```

---

## CoffeeDecorator.java (Decorator)

```java
// Decorator
// Base class for all coffee customizations.
public abstract class CoffeeDecorator implements Coffee {

    protected Coffee coffee;

    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

}
```

<details>
<summary>💡 Why do we need CoffeeDecorator?</summary>

This is the most important part of the Decorator Pattern.

A common question is:

> **Why don't we simply extend `Espresso`?**

For example,

```java
class MilkDecorator extends Espresso
```

This immediately creates a problem.

Now Milk works only for

```text
Espresso
```

What about

```text
Cappuccino

Latte

Mocha
```

Should we create

```text
MilkForCappuccino

MilkForLatte

MilkForMocha
```

Again?

We're back to the inheritance explosion.

Instead,

Milk wraps

```java
Coffee
```

not

```java
Espresso
```

Now the same decorator works for

```text
Espresso

↓

Milk
```

```text
Cappuccino

↓

Milk
```

```text
Latte

↓

Milk
```

Even better,

it also works for another decorator.

```text
Espresso

↓

Milk

↓

Sugar

↓

Vanilla
```

This is only possible because the decorator wraps the abstraction

```java
Coffee
```

instead of a concrete class.

</details>

---

## MilkDecorator.java (Concrete Decorator)

```java
// Concrete Decorator
// Adds Milk to a coffee.
public class MilkDecorator extends CoffeeDecorator {

    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 30;
    }

}
```

---

## SugarDecorator.java (Concrete Decorator)

```java
// Concrete Decorator
// Adds Sugar to a coffee.
public class SugarDecorator extends CoffeeDecorator {

    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Sugar";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 10;
    }

}
```

---

## VanillaDecorator.java (Concrete Decorator)

```java
// Concrete Decorator
// Adds Vanilla to a coffee.
public class VanillaDecorator extends CoffeeDecorator {

    public VanillaDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Vanilla";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 40;
    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        // Plain Espresso
        Coffee espresso = new Espresso();

        System.out.println(
                espresso.getDescription() +
                " : ₹" + espresso.getCost());

        // Espresso + Milk
        Coffee espressoWithMilk =
                new MilkDecorator(new Espresso());

        System.out.println(
                espressoWithMilk.getDescription() +
                " : ₹" + espressoWithMilk.getCost());

        // Espresso + Milk + Sugar
        Coffee espressoWithMilkSugar =
                new SugarDecorator(
                        new MilkDecorator(
                                new Espresso()));

        System.out.println(
                espressoWithMilkSugar.getDescription() +
                " : ₹" + espressoWithMilkSugar.getCost());

        // Cappuccino + Vanilla + Sugar
        Coffee cappuccino =
                new SugarDecorator(
                        new VanillaDecorator(
                                new Cappuccino()));

        System.out.println(
                cappuccino.getDescription() +
                " : ₹" + cappuccino.getCost());

    }

}
```

---

## Output

```text
Espresso : ₹120

Espresso, Milk : ₹150

Espresso, Milk, Sugar : ₹160

Cappuccino, Vanilla, Sugar : ₹200
```

---

## Execution Flow

### Example

```java
Coffee coffee =
        new SugarDecorator(
                new MilkDecorator(
                        new Espresso()));
```

Execution happens from inside to outside.

```text
Create Espresso

        │

        ▼

Wrap with MilkDecorator

        │

        ▼

Wrap with SugarDecorator

        │

        ▼

Client calls

getCost()

        │

        ▼

SugarDecorator

↓

calls

MilkDecorator

↓

calls

Espresso

↓

returns 120

↓

Milk adds 30

↓

Sugar adds 10

↓

Final Cost = 160
```

The same happens for

```java
getDescription()
```

Each decorator delegates the call to the wrapped object,

adds its own behavior,

and returns the updated result.

Notice something important.

The original

```text
Espresso
```

object is never modified.

Instead,

each decorator wraps the previous object and adds one new responsibility.

This is the core idea behind the **Decorator Pattern**.

</details>

---

# Handling New Requirements

## New Requirement

The Product Owner now says,

> **"Customers now want Honey as an additional topping."**

### Without the Decorator Pattern

We would need classes like

```text
EspressoWithHoney

EspressoWithMilkHoney

EspressoWithSugarHoney

EspressoWithMilkSugarHoney

CappuccinoWithHoney

...
```

The number of subclasses keeps increasing.

---

### With the Decorator Pattern

Simply create one new decorator.

```java
public class HoneyDecorator extends CoffeeDecorator {

    public HoneyDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return coffee.getDescription() + ", Honey";
    }

    @Override
    public double getCost() {
        return coffee.getCost() + 25;
    }

}
```

Now every existing coffee automatically supports Honey.

```java
Coffee coffee =
        new HoneyDecorator(
                new MilkDecorator(
                        new Espresso()));
```

No existing classes change.

This follows the **Open/Closed Principle** perfectly.

</details>

</details>
