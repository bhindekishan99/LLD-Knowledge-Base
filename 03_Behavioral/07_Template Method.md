# Template Method Design Pattern

> **Category:** Behavioral Design Pattern

---

# Source

## 🎥 YouTube

https://www.youtube.com/watch?v=MjqEq-v3KFo&t=1s

## 🌐 Blog

https://codewitharyan.com/tech-blogs/template-method-design-pattern

---

# How to Think Towards the Template Method Pattern

Forget the Template Method Pattern for a moment.

Imagine you're asked to build a **Coffee Machine**.

Preparing coffee involves the following steps.

```text
Boil Water
      ↓
Brew Coffee
      ↓
Pour into Cup
      ↓
Add Sugar & Milk
```

As a developer, what would you naturally write?

Probably something like this.

```java
class Coffee {

    public void prepare() {

        boilWater();
        brewCoffee();
        pourInCup();
        addSugarAndMilk();

    }

}
```

This is exactly how most developers—including senior engineers—would start.

At this point, there is absolutely nothing wrong with this solution.

It is simple, readable and solves today's problem.

---

## Requirements Start Growing

Now the Product Owner comes back.

> "Great! Now implement Tea."

Again, you naturally create another class.

```java
class Tea {

    public void prepare() {

        boilWater();
        steepTeaBag();
        pourInCup();
        addLemon();

    }

}
```

Everything still looks perfectly fine.

---

## ⭐ Thumb Rule

At this point, don't immediately think about the Template Method Pattern.

Instead, ask yourself one simple question.

> **Is the overall algorithm always the same, while only a few individual steps change?**

### If **No**

Keep separate implementations.

Example:

```java
class Car {

    void start() {
        // Completely different implementation
    }

}

class Printer {

    void start() {
        // Completely different implementation
    }

}
```

There is no common algorithm.

Trying to force inheritance would only make the design more complicated.

---

### If **Yes**

Think about the Template Method Pattern.

Example:

Coffee

```text
Boil Water
      ↓
Brew Coffee
      ↓
Pour into Cup
      ↓
Add Sugar & Milk
```

Tea

```text
Boil Water
      ↓
Steep Tea
      ↓
Pour into Cup
      ↓
Add Lemon
```

Notice something.

Most of the algorithm is identical.

Only two steps are changing.

That is the signal.

---

## Observe Carefully

Forget Coffee and Tea for a moment.

Instead,

look at the overall algorithm.

```text
Prepare Beverage

      ↓

Boil Water

      ↓

Brew Beverage

      ↓

Pour into Cup

      ↓

Add Condiments
```

Ask yourself,

> **Should every subclass rewrite this entire algorithm?**

Probably not.

The algorithm itself never changes.

Only the implementation of a few steps changes.

---

## First Refactoring

Instead of letting every subclass duplicate the algorithm,

move the common algorithm into one parent class.

```java
prepareRecipe() {

    boilWater();

    brew();

    pourInCup();

    addCondiments();

}
```

Now only the changing steps remain abstract.

```java
abstract void brew();

abstract void addCondiments();
```

Each subclass only implements what is different.

Coffee knows how to brew coffee.

Tea knows how to steep tea.

The parent class no longer needs to know these details.

---

## Another Problem Appears

Suppose a developer accidentally writes

```java
@Override
void prepareRecipe() {

    brew();

    pourInCup();

}
```

Oops!

They skipped

```text
Boil Water
```

The algorithm is now broken.

The parent class should control the algorithm.

Subclasses should only customize the variable steps.

That's why the template method becomes

```java
final void prepareRecipe()
```

The `final` keyword protects the algorithm from being modified.

---

## One More Requirement

Now the customer says,

> "Sometimes I don't want any condiments."

We don't want subclasses to rewrite the entire algorithm again.

Instead,

we introduce an optional extension point.

```java
if (customerWantsCondiments()) {

    addCondiments();

}
```

The algorithm is still exactly the same.

We simply added an optional step.

This is called a **Hook Method**.

---

## Complete Thinking

```text
Need Coffee

        ↓

Need Tea

        ↓

Duplicate Code

        ↓

Observe the Algorithm

        ↓

Most Steps Are Identical

        ↓

Move Algorithm to Parent Class

        ↓

Move Changing Steps to Subclasses

        ↓

Protect Algorithm Using final

        ↓

Need Optional Steps

        ↓

Introduce Hook Method

        ↓

Template Method Pattern
```

---

# How to Remember the Template Method Pattern

Imagine making **Maggi**.

The recipe is always

```text
Boil Water

      ↓

Add Maggi

      ↓

Cook

      ↓

Serve
```

Different people customize only the last step.

One person adds

- Cheese

Another adds

- Vegetables

Another adds

- Butter

The recipe never changes.

Only the toppings change.

The recipe is the **Template Method**.

The toppings are the **Abstract Methods implemented by subclasses**.

## Memory Trick

Remember one sentence.

> **Same recipe. Different ingredients.**

Whenever you notice

- the overall algorithm is always the same,
- only a few individual steps change,

ask yourself,

> **"Can I keep the algorithm in the parent class and let subclasses implement only the changing steps?"**

If the answer is **Yes**,

you're probably looking at the **Template Method Pattern**.

---

# Traditional Implementation

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Coffee.java

```java
// Represents the process of preparing Coffee.
public class Coffee {

    // Defines all the steps required to prepare coffee.
    public void prepare() {

        boilWater();
        brewCoffee();
        pourInCup();
        addSugarAndMilk();

    }

    // Boils water before brewing.
    private void boilWater() {
        System.out.println("Boiling Water...");
    }

    // Brews coffee.
    private void brewCoffee() {
        System.out.println("Brewing Coffee...");
    }

    // Pours the beverage into the cup.
    private void pourInCup() {
        System.out.println("Pouring into Cup...");
    }

    // Adds sugar and milk.
    private void addSugarAndMilk() {
        System.out.println("Adding Sugar and Milk...");
    }

}
```

---

## Tea.java

```java
// Represents the process of preparing Tea.
public class Tea {

    // Defines all the steps required to prepare tea.
    public void prepare() {

        boilWater();
        steepTeaBag();
        pourInCup();
        addLemon();

    }

    // Boils water before steeping tea.
    private void boilWater() {
        System.out.println("Boiling Water...");
    }

    // Steeps the tea bag.
    private void steepTeaBag() {
        System.out.println("Steeping Tea Bag...");
    }

    // Pours the beverage into the cup.
    private void pourInCup() {
        System.out.println("Pouring into Cup...");
    }

    // Adds lemon.
    private void addLemon() {
        System.out.println("Adding Lemon...");
    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        Coffee coffee = new Coffee();
        Tea tea = new Tea();

        System.out.println("Preparing Coffee");
        coffee.prepare();

        System.out.println();

        System.out.println("Preparing Tea");
        tea.prepare();

    }

}
```

---

## Output

```text
Preparing Coffee
Boiling Water...
Brewing Coffee...
Pouring into Cup...
Adding Sugar and Milk...

Preparing Tea
Boiling Water...
Steeping Tea Bag...
Pouring into Cup...
Adding Lemon...
```

---

## Problems With This Approach

Although the program works correctly, notice the amount of duplicated code.

Both classes perform the following steps.

```text
Boil Water

↓

Pour into Cup
```

Only these steps are different.

```text
Coffee

↓

Brew Coffee

↓

Add Sugar & Milk
```

```text
Tea

↓

Steep Tea Bag

↓

Add Lemon
```

Now imagine another requirement arrives.

- Green Tea
- Black Coffee
- Cappuccino
- Hot Chocolate

Every new beverage will duplicate the same algorithm again.

As more beverages are introduced,

the duplicate code continues to grow.

This is where the **Template Method Pattern** becomes useful.

</details>

---

</details>

---

# Template Method Pattern Implementation

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Beverage.java

```java
// Defines the common algorithm for preparing a beverage.
public abstract class Beverage {

    // Template Method.
    // Defines the overall algorithm and prevents subclasses from changing it.
    public final void prepareRecipe() {

        boilWater();
        brew();
        pourInCup();
        addCondiments();

    }

    // Common step shared by all beverages.
    private void boilWater() {
        System.out.println("Boiling Water...");
    }

    // Common step shared by all beverages.
    private void pourInCup() {
        System.out.println("Pouring into Cup...");
    }

    // Steps that vary for each beverage.
    protected abstract void brew();

    protected abstract void addCondiments();

}
```

<details>
<summary>💡 Why do we need an Abstract Class?</summary>

A common question is:

> **Why not simply create an interface?**

Initially, it may look like this.

```java
interface Beverage {

    void prepareRecipe();

}
```

Now every beverage implements the entire algorithm.

```java
class Coffee implements Beverage {

    @Override
    public void prepareRecipe() {

        boilWater();
        brewCoffee();
        pourInCup();
        addSugarAndMilk();

    }

}
```

```java
class Tea implements Beverage {

    @Override
    public void prepareRecipe() {

        boilWater();
        steepTeaBag();
        pourInCup();
        addLemon();

    }

}
```

We're back to the original problem.

Both classes duplicate

- Boil Water
- Pour into Cup

An interface only provides a contract.

It cannot reuse the common implementation.

---

An abstract class allows us to keep

- Common implementation
- Common algorithm
- Abstract customization points

inside one place.

```java
public abstract class Beverage {

    public final void prepareRecipe() {

        boilWater();

        brew();

        pourInCup();

        addCondiments();

    }

}
```

Now subclasses only implement what is different.

```java
brew();

addCondiments();
```

This eliminates duplication while still allowing customization.

---

### Why is `prepareRecipe()` marked as `final`?

The parent class defines the correct sequence.

```text
Boil Water

↓

Brew Beverage

↓

Pour into Cup

↓

Add Condiments
```

Suppose a subclass overrides it.

```java
@Override
public void prepareRecipe() {

    brew();

    addCondiments();

}
```

Oops!

The developer accidentally skipped

- Boiling Water
- Pouring into Cup

The algorithm is now broken.

Making the template method `final`

ensures that every beverage follows the same algorithm.

Subclasses can customize the steps,

but they cannot change the overall flow.

</details>

---

## Coffee.java

```java
// Represents Coffee preparation.
public class Coffee extends Beverage {

    @Override
    protected void brew() {
        System.out.println("Brewing Coffee...");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding Sugar and Milk...");
    }

}
```

---

## Tea.java

```java
// Represents Tea preparation.
public class Tea extends Beverage {

    @Override
    protected void brew() {
        System.out.println("Steeping Tea Bag...");
    }

    @Override
    protected void addCondiments() {
        System.out.println("Adding Lemon...");
    }

}
```

---

## Hook Method (Optional Step)

Sometimes the algorithm needs optional steps.

For example,

a customer may not want any condiments.

Instead of changing the algorithm,

we introduce a **Hook Method**.

```java
public abstract class Beverage {

    public final void prepareRecipe() {

        boilWater();

        brew();

        pourInCup();

        if (customerWantsCondiments()) {
            addCondiments();
        }

    }

    protected boolean customerWantsCondiments() {
        return true;
    }

    protected abstract void brew();

    protected abstract void addCondiments();

}
```

Now a subclass can simply override the hook.

```java
public class BlackCoffee extends Beverage {

    @Override
    protected void brew() {
        System.out.println("Brewing Black Coffee...");
    }

    @Override
    protected void addCondiments() {
        System.out.println("No Condiments");
    }

    @Override
    protected boolean customerWantsCondiments() {
        return false;
    }

}
```

Notice,

the algorithm never changed.

Only one decision point became customizable.

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        Beverage coffee = new Coffee();
        Beverage tea = new Tea();

        System.out.println("Preparing Coffee");
        coffee.prepareRecipe();

        System.out.println();

        System.out.println("Preparing Tea");
        tea.prepareRecipe();

    }

}
```

---

## Output

```text
Preparing Coffee
Boiling Water...
Brewing Coffee...
Pouring into Cup...
Adding Sugar and Milk...

Preparing Tea
Boiling Water...
Steeping Tea Bag...
Pouring into Cup...
Adding Lemon...
```

</details>

---

# Quick Revision

## Intent

> **Define the skeleton of an algorithm in a parent class while allowing subclasses to customize specific steps without changing the overall algorithm.**

---

## Memory Trick

> **Same recipe. Different ingredients.**

---

## Thumb Rule

```text
Multiple classes

        │

        ▼

Do they follow the same algorithm?

        │
   ┌────┴────┐
   │         │
  No        Yes
   │         │
Don't use   Think about
Template    Template Method
Method
```

---

## Thinking Process

```text
Need Coffee

        ↓

Need Tea

        ↓

Duplicate Code

        ↓

Observe Algorithm

        ↓

Most Steps Are Identical

        ↓

Move Algorithm to Parent Class

        ↓

Move Variable Steps to Subclasses

        ↓

Protect Algorithm Using final

        ↓

Need Optional Steps

        ↓

Introduce Hook Method

        ↓

Template Method Pattern
```

</details>

---

# Quick Revision

## Memory Trick

> **Same recipe. Different ingredients.**

## Thinking Process

```text
Duplicate Code

        ↓

Observe Algorithm

        ↓

Move Common Algorithm to Parent

        ↓

Move Variable Steps to Children

        ↓

Protect Algorithm

        ↓

Template Method Pattern
```
