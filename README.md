# 🚀 LLD Knowledge Base

> **Master Low-Level Design by learning how to think like a software designer—not by memorizing design patterns.**

---

# 📖 About

Most Low-Level Design (LLD) resources explain **what** a design pattern is.

Some explain **how** to implement it.

Very few explain:

* **Why does this pattern exist?**
* **What problem does it solve?**
* **How would you discover this pattern if it had never been invented?**
* **Which design principles are violated before introducing the pattern?**

This repository is built around answering those questions.

The goal is to develop **design thinking**, enabling readers to derive good designs naturally instead of memorizing pattern names.

---

# 🎯 Goals

## Goal 1 — Crack Low-Level Design Interviews

This repository prepares you for LLD interviews by covering:

* Object-Oriented Programming
* SOLID Principles
* UML & Class Relationships
* Design Patterns
* Real-world Low-Level Design Problems
* Java Implementations
* Common Interview Questions
* Pattern Comparisons
* Recognition Techniques

---

## Goal 2 — Build Strong Design Thinking

The objective is not just to learn **what** a pattern is.

The objective is to understand:

* Why does this design problem occur?
* Why does the traditional solution fail?
* Which SOLID principles are violated?
* Which responsibilities should be extracted?
* What abstraction is missing?
* How do we naturally arrive at the correct design?
* What trade-offs does this design introduce?

The ultimate goal is to think like a software designer.

---

# 💡 Learning Philosophy

Every topic follows the same learning journey.

```text
Problem
    ↓
Traditional Solution
    ↓
Growing Requirements
    ↓
Design Smells
    ↓
Violated Design Principles
    ↓
Responsibility Analysis
    ↓
Discover the Missing Abstraction
    ↓
Design Pattern
    ↓
Implementation
    ↓
Trade-offs
    ↓
Real-world Applications
```

The journey is more important than the final code.

---

# 🧠 Repository Principles

Every note in this repository follows these principles.

### 1. Start with the Simplest Working Solution

Never introduce a design pattern immediately.

Start with the most natural implementation.

---

### 2. Feel the Pain First

Readers should understand **why** the current design becomes difficult to maintain.

A pattern should solve an experienced problem—not an imagined one.

---

### 3. Learn Through Evolution

Every design evolves naturally.

Instead of saying:

> "Use Mediator Pattern."

We ask:

> "Why are these classes tightly coupled?"

The pattern becomes the natural solution.

---

### 4. Connect Every Pattern with Design Principles

Every topic identifies:

* Design Smells
* SOLID Principle Violations
* High Coupling
* Low Cohesion
* Responsibility Misplacement

before introducing the solution.

---

### 5. Explain Every Important Design Decision

The repository explains decisions such as:

* Why is Builder a static nested class?
* Why is the constructor private?
* Why does Bidder know only the Mediator?
* Why should the Invoker not know the Receiver?

Every important design choice is justified.

---

### 6. Teach Trade-offs

Every design pattern includes:

* Advantages
* Disadvantages
* When NOT to use it

There is no perfect design pattern.

---

# 📚 Repository Structure

```text
LLD-Knowledge-Base/

├── 00_Foundation/
│
├── 01_Creational/
│
├── 02_Structural/
│
├── 03_Behavioral/
│
├── 04_LLD_Problems/
│
├── 05_Interview/
│
├── assets/
│
└── templates/
```

---

# 📄 Every Design Pattern Covers

Each design pattern follows a consistent structure.

1. Definition
2. Problem Statement
3. Traditional Approach
4. Problems in Traditional Approach
5. Thought Process (How to Discover the Pattern)
6. Design Smells & SOLID Violations
7. Components
8. UML Diagram
9. Client Flow
10. Complete Java Implementation
11. Important Interview Discussions
12. Memory Trick
13. Recognition Pattern
14. Advantages
15. Disadvantages
16. Pattern Comparison
17. Quick Revision
18. Practice Question
19. References

---

# 🏗 Every LLD Problem Covers

Every design problem follows a structured approach.

* Requirement Gathering
* Functional Requirements
* Non-Functional Requirements
* Identifying Entities
* Class Responsibilities
* Applying SOLID
* Applying Design Patterns
* UML Diagram
* Complete Java Solution
* Design Discussion
* Possible Extensions
* Interview Follow-up Questions

---

# 🎓 Who Is This Repository For?

* Students learning Object-Oriented Design
* Software Engineers preparing for interviews
* Java Developers
* Backend Engineers
* Anyone who wants to build strong software design fundamentals

---

# 📅 Progress

## Foundation

* [ ] OOP
* [ ] SOLID
* [ ] UML
* [ ] Class Relationships
* [ ] Coupling & Cohesion

## Creational Patterns

* [ ] Singleton
* [ ] Factory
* [ ] Abstract Factory
* [ ] Builder
* [ ] Prototype

## Structural Patterns

* [ ] Adapter
* [ ] Decorator
* [ ] Composite
* [ ] Facade
* [ ] Proxy
* [ ] Bridge
* [ ] Flyweight

## Behavioral Patterns

* [ ] Observer
* [ ] Command
* [ ] Mediator
* [ ] Iterator
* [ ] Strategy
* [ ] State
* [ ] Template Method
* [ ] Chain of Responsibility
* [ ] Visitor
* [ ] Memento

## LLD Problems

* [ ] Parking Lot
* [ ] Splitwise
* [ ] BookMyShow
* [ ] Elevator System
* [ ] Snake & Ladder
* [ ] Chess
* [ ] ATM
* [ ] Vending Machine
* [ ] Cricbuzz

---

# ⭐ Contributing

Suggestions, improvements, and constructive feedback are always welcome.

If you notice a better design approach, a clearer explanation, or a real-world example that improves conceptual understanding, feel free to contribute.

The goal is to make this repository one of the most conceptually complete resources for learning Low-Level Design.

---

> **"Don't memorize design patterns. Understand the problems that gave birth to them."**
