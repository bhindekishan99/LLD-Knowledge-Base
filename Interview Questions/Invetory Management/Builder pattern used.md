### Syntax in builder pattern: Recursive Generic (Self-Referential Generic)

**Syntax:**
```java
Builder<T extends Builder<T>>
```

- **First `T`** → Declares a generic type parameter.
- **Second `T`** → Passes the same type as the generic argument to `Builder`, creating a self-referential (recursive) generic.

**Why is it needed?**

Without it:

```java
class Builder {
    Builder quantity(int q) {
        return this;
    }
}
```

`quantity()` returns `Builder`, so after calling it you lose access to child-specific methods:

```java
new ElectronicsBuilder()
    .quantity(5)      // returns Builder
    .warranty(24);    // ❌ Compile-time error
```

**With `T extends Builder<T>`:**

The first `T` declares the generic type, and the second `T` passes the same type to `Builder` so parent methods return the **child builder** instead of `Builder`, enabling fluent method chaining.

Example:

```java
class ElectronicsBuilder
        extends Builder<ElectronicsBuilder> { }
```

Now:

```java
new ElectronicsBuilder()
    .quantity(5)      // returns ElectronicsBuilder
    .warranty(24)     // ✅ Works
    .build();
```
