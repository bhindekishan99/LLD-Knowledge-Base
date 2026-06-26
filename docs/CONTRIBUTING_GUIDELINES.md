# Repository Principles

This document defines the quality standards for every note in this repository.

The objective is not to create the largest Low-Level Design repository.

The objective is to create one of the most **conceptually accurate**, **easy to understand**, and **interview-friendly** Low-Level Design resources.

---

# Core Philosophy

> **Maximum understanding with minimum unnecessary reading.**

Every explanation should maximize learning while minimizing unnecessary text.

We value **clarity over quantity**.

---

# Writing Principles

## 1. Explain "Why" Before "How"

Before introducing any class, interface, or design pattern, explain:

* Why do we need it?
* What problem does it solve?
* What happens if we don't introduce it?

Only then introduce the implementation.

---

## 2. Start With the Simplest Solution

Never introduce a design pattern immediately.

Start with the most natural implementation.

Only introduce additional abstractions when the current design becomes difficult to maintain.

---

## 3. Teach Design Thinking

The goal is not to memorize design patterns.

The goal is to understand how a software engineer naturally discovers the pattern.

Readers should finish each topic thinking:

> "I understand why this pattern exists."

instead of

> "I memorized another pattern."

---

## 4. Be Technically Correct

Never force concepts.

* Only discuss design principles that are genuinely relevant.
* Never invent design smells.
* Never exaggerate problems.
* Every statement should be technically defendable.

Accuracy is more important than completeness.

---

## 5. Good Design Is Contextual

Do not describe the traditional approach as "bad."

Instead explain:

* Why it was a reasonable solution initially.
* Why new requirements exposed its limitations.
* Why the design needed to evolve.

Every design has context.

---

## 6. Every Paragraph Must Add Value

Before keeping any paragraph, ask:

> "If I remove this paragraph, does the reader lose understanding?"

If the answer is **No**, remove it.

Avoid filler.

---

## 7. Prioritize Understanding

Not every section deserves the same amount of detail.

### ⭐⭐⭐ High Priority

Spend the most effort here.

* Problem Statement
* Traditional Approach
* Why Traditional Approach Breaks
* How to Think Towards the Pattern
* Design Decisions
* Real-world Applications

### ⭐⭐ Medium Priority

* Components
* UML
* Client Flow
* Complete Code
* Advantages
* Disadvantages
* Interview Questions

### ⭐ Low Priority

Keep these concise.

* Definition
* Intent
* Keywords
* History

---

## 8. Separate Core Concepts From Additional Insights

Not every concept is equally important.

If a topic is useful but not essential, place it in an **Additional Insight** section instead of interrupting the main learning flow.

This keeps the notes concise while still preserving valuable knowledge.

---

## 9. Challenge Every Source

Do not blindly trust:

* YouTube videos
* Articles
* Books
* Existing notes

Every explanation should be reviewed critically.

If a better explanation exists, prefer it.

---

# Review Checklist

Before finalizing any note, verify the following.

* [ ] Is the explanation technically correct?
* [ ] Have we explained "why" before "how"?
* [ ] Does every paragraph add value?
* [ ] Can anything be removed without reducing understanding?
* [ ] Is the traditional approach presented fairly?
* [ ] Have we explained why it stops scaling?
* [ ] Does the reader naturally discover the pattern?
* [ ] Are important design decisions justified?
* [ ] Are unnecessary terms and jargon avoided?
* [ ] Would I confidently recommend this note to another engineer?

If any answer is **No**, improve the note before publishing it.
