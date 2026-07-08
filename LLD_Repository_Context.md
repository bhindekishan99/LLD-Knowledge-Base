# LLD Knowledge Base - Project Context (Frozen)

## Project Goal

The goal of this repository is **not** to create textbook notes.

The goal is to build one of the best **Low Level Design (LLD)** repositories on GitHub that teaches **design thinking** rather than design pattern definitions.

The repository should help readers:

- Think like a software engineer.
- Understand why a pattern exists.
- Learn how to derive a pattern from a real problem.
- Implement production-quality code.
- Remember the pattern using one strong analogy.
- Understand how the design evolves when requirements change.

The repository should be useful for both interviews and real software development.

---

# Core Philosophy

> **Maximum understanding with minimum unnecessary reading.**

Every section should increase the reader's understanding.

If a section doesn't improve understanding, remove it.

---

# What We Don't Want

We don't want textbook notes.

Avoid sections like:

- Definition
- One-line Summary
- Advantages
- Disadvantages
- Components Table
- Recognition Pattern
- Real-world Examples
- Interview Questions
- Design Principles section
- Long theoretical discussions

Readers naturally understand these while learning the pattern.

---

# What We Actually Want

Every design pattern should teach one thing:

> **How do I naturally arrive at this pattern from a real software problem?**

The focus is on **thinking**, not memorization.

---

# Learning Flow

Every pattern should follow this flow.

## 1. Sources

Always mention the original learning resources.

Example:

```text
## Sources

🎥 YouTube

🌐 Blog
```

Never mention local PDFs.

---

## 2. How to Think Towards the Pattern ⭐⭐⭐⭐⭐

This is the most important section.

Do not introduce the pattern immediately.

Instead, teach the thinking process.

Typical flow:

Problem

↓

Traditional Thinking

↓

Requirements Grow

↓

Pain Appears

↓

Refactoring Thought Process

↓

Pattern Naturally Emerges

The reader should feel:

> "I would have invented this pattern myself."

---

## 3. How to Remember the Pattern ⭐⭐⭐⭐⭐

Every pattern should have exactly one memorable analogy.

Examples:

Visitor → Doctor visiting patients

Mediator → Auction House

Observer → YouTube Subscribers

Chain of Responsibility → Customer Support

Strategy → Google Maps Route Selection

One analogy should make the pattern unforgettable.

---

## 4. Traditional Solution

Keep this inside a collapsed section.

Include:

- UML
- Complete Code
- Client Code
- Output
- Problems

Show what every developer would naturally write.

Never criticize the traditional solution.

It solved today's problem.

---

## 5. Applying the Pattern

Keep this inside a collapsed section.

Include:

- UML
- Complete Implementation
- Client Code
- Output
- Execution Flow

Whenever introducing:

- Interface
- Abstract Class

Explain **Why** with a practical example.

Do not simply mention SOLID principles.

Teach the reasoning.

---

## 6. Handling New Requirements (Optional)

This is one of the most valuable sections.

After implementing the pattern,

introduce one realistic business requirement.

Example:

- Add WhatsApp Notification.
- Add Health Report.
- Add Fraud Detection.

Then explain:

- Should existing classes change?
- What new class is added?
- What remains unchanged?
- Why is the design flexible?

This demonstrates the real strength of the pattern.

Include this section only when it genuinely adds value.

Do not force it.

---

## 7. Quick Revision

End every pattern with a concise revision.

Include:

Intent

Memory Trick

Thumb Rule

Thinking Process

The reader should revise the entire pattern in under one minute.

---

# Source First Principle

The source decides:

> **What we teach.**

We decide:

> **How we teach it.**

Always:

- Study the complete source.
- Extract every valuable concept.
- Extract every important example.
- Extract every interview discussion.
- Extract every follow-up requirement.

Do not blindly copy the source.

Reorganize it into our learning flow.

---

# Source Example Rule

Always use the same example as the source.

Example:

Visitor

Hospital

Chain of Responsibility

Leave Approval

Mediator

Auction

Observer

YouTube

Avoid inventing different domains unless adding additional real-world examples becomes necessary.

---

# Writing Style

The repository should feel like it was written by a single author.

Maintain consistency across every markdown file.

Use:

- Short paragraphs.
- Simple English.
- Production-quality Java.
- Small meaningful comments.
- Consistent formatting.
- Clear UML.
- Clean code.

---

# Repository Goal

This repository should teach readers:

- How to think.
- How to design.
- How to refactor.
- How to recognize design problems.
- How to evolve software.

Not just how to memorize GoF patterns.

---

# Final Philosophy

A reader should finish a pattern and feel:

"I understand why this pattern exists."

instead of

"I memorized another design pattern."
