# Memento Design Pattern

> **Category:** Behavioral Design Pattern

---

# Sources

## 🎥 YouTube

https://www.youtube.com/watch?v=WxKrJFYPK-A&t=60s

## 🌐 Blog

https://codewitharyan.com/tech-blogs/memento-design-pattern

---

# How to Think Towards the Memento Pattern

Forget the Memento Pattern for a moment.

Imagine you're building a **Text Editor**.

A user opens the editor and starts typing.

Initially, the editor contains

```text
Hello
```

The user edits it.

```text
Hello

↓

Hello, World!
```

Then edits it again.

```text
Hello

↓

Hello, World!

↓

Hello, World! Welcome to Memento Pattern.
```

Everything works perfectly.

---

## A New Requirement Arrives

The Product Owner now says,

> **"We need an Undo feature."**

As developers, our first instinct is usually something simple.

Before changing the text,

let's save the current value.

```java
String backup = editor.getText();

editor.setText("Hello, World!");
```

Now if the user clicks Undo,

simply restore the backup.

```java
editor.setText(backup);
```

Problem solved.

Or is it?

---

## Another Requirement Arrives

Now the Product Owner says,

> **"Support multiple Undo operations."**

Immediately our code starts becoming complicated.

```text
Current Text

Backup 1

Backup 2

Backup 3

Backup 4
```

Soon the editor starts managing

- Current Text
- Previous Text
- Previous Previous Text
- Undo Logic
- Backup Logic

All inside one class.

---

## Let's Think Carefully

Ask yourself,

> **What is the real responsibility of a Text Editor?**

A Text Editor should

- edit text
- display text

Should it also manage

- backup history?
- undo stack?
- redo stack?
- snapshots?

Probably not.

These are completely different responsibilities.

---

## Another Observation

Notice something interesting.

What actually needs to be remembered?

Not the entire editor.

Only its **state**.

For example,

```text
Hello
```

or

```text
Hello, World!
```

or

```text
Hello, World! Welcome!
```

Every time the user saves,

we're simply taking a **snapshot**.

The editor itself doesn't change.

Only its state changes.

---

## Think Like Real Life

Suppose you're writing notes in a notebook.

After finishing an important section,

you take a photo.

```text
Notebook

↓

📸 Snapshot
```

You continue writing.

Later,

you make a mistake.

Instead of erasing everything,

you simply look at the previous photo and restore it.

Notice something.

The notebook never stores its own photos.

The photos are stored separately.

Exactly the same thing should happen in software.

---

## First Refactoring

Instead of storing backups inside

```text
TextEditor
```

create a separate object that represents a snapshot.

```text
Snapshot

↓

Stores

↓

Current Text
```

Now the editor only creates snapshots.

It doesn't manage them.

---

## One More Problem

Who should store all these snapshots?

The editor?

No.

Otherwise we're back to the same problem.

Instead,

introduce another object whose only responsibility is

```text
History
```

It simply stores snapshots.

```text
Snapshot 1

Snapshot 2

Snapshot 3

Snapshot 4
```

Now the responsibilities become clear.

---

## Final Design

The editor creates snapshots.

```text
TextEditor

↓

Creates

↓

Snapshot
```

The history manager stores them.

```text
History

↓

Stores

↓

Snapshots
```

Whenever Undo is needed,

the history manager returns the previous snapshot,

and the editor restores itself.

---

## Complete Thinking Process

```text
Need Undo Feature

        ↓

Store Previous Text

        ↓

Need Multiple Undo

        ↓

Backup Logic Starts Growing

        ↓

Editor Gets Multiple Responsibilities

        ↓

Separate State From Editor

        ↓

Represent State As Snapshot

        ↓

Store Snapshots Separately

        ↓

Restore Previous Snapshot

        ↓

Memento Pattern
```

---

# How to Remember the Memento Pattern

Imagine you're writing a document.

After every important milestone,

you click

```text
Save
```

You don't create another editor.

You simply create another **snapshot**.

```text
Version 1

↓

Version 2

↓

Version 3

↓

Version 4
```

Whenever you make a mistake,

you don't rebuild the document.

You restore one of the previously saved versions.

Exactly the same thing happens in the Memento Pattern.

The object keeps working normally.

Whenever needed,

it creates a snapshot of its current state.

Someone else stores those snapshots.

Later,

the object restores itself from one of them.

---

## Memory Trick

Remember this one sentence.

> **The object creates snapshots of itself, while someone else safely stores those snapshots.**

---

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## TextEditor.java

```java
// Represents a simple text editor.
public class TextEditor {

    private String content;

    public TextEditor(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        TextEditor editor = new TextEditor("Hello");

        // User edits the document.
        String backup1 = editor.getContent();
        editor.setContent("Hello, World!");

        // User edits again.
        String backup2 = editor.getContent();
        editor.setContent("Hello, World! Welcome!");

        System.out.println("Current : " + editor.getContent());

        // Undo
        editor.setContent(backup2);

        System.out.println("Undo 1  : " + editor.getContent());

        // Undo Again
        editor.setContent(backup1);

        System.out.println("Undo 2  : " + editor.getContent());

    }

}
```

---

## Output

```text
Current : Hello, World! Welcome!
Undo 1  : Hello, World!
Undo 2  : Hello
```

---

## Problems With This Approach

Although this implementation works for a small example, it becomes difficult to maintain as new requirements arrive.

---

### 1. The Editor Manages Too Many Responsibilities

The `TextEditor` should only focus on editing text.

Instead, it slowly becomes responsible for:

- Managing backups
- Maintaining undo history
- Supporting redo
- Restoring previous states

The editor is no longer just editing text.

It is becoming a history manager as well.

---

### 2. Backup Logic Gets Scattered

Suppose every time the user edits the document, we write:

```java
String backup = editor.getContent();

editor.setContent(newContent);
```

Every client must remember to create a backup before modifying the editor.

If one developer forgets,

Undo will fail.

The backup logic is now duplicated throughout the application.

---

### 3. Supporting Multiple Undo Becomes Difficult

Initially, one backup variable is enough.

```java
String backup;
```

Soon we need

```java
backup1

backup2

backup3

backup4
```

Eventually we replace them with

```java
List<String>

or

Stack<String>
```

Now the client code becomes responsible for managing history.

This responsibility doesn't belong here.

---

### 4. No Separation Between Current State and History

The editor now has to think about two completely different things.

Current State

```text
Hello, World!
```

History

```text
Hello

↓

Hello, World!

↓

Hello, World! Welcome!
```

Current state and historical states should not be managed by the same object.

---

### 5. Future Features Become Hard

Imagine tomorrow the Product Owner asks for

- Unlimited Undo
- Redo
- Restore to Version 5
- Auto Save
- Checkpoint Restore

Where should all this logic go?

If we continue with the traditional approach,

the editor class will keep growing.

Soon it becomes difficult to understand, maintain, and test.

---

### The Real Problem

Notice something important.

The editor itself isn't complicated.

Only its **state** keeps changing.

Every time the user edits the document,

we simply want to remember

```text
Current State
```

instead of adding more responsibilities to the editor.

This is exactly the problem the **Memento Pattern** solves.

---
</details>

# Applying the Memento Pattern

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Memento.java

```java
// Stores a snapshot of the TextEditor's state.
public class Memento {

    private final String content;

    public Memento(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
```

<details>
<summary>💡 Why do we need a separate Memento class?</summary>

A common question is:

> **Why can't the `TextEditor` simply store all previous contents inside a `List<String>`?**

Initially, that seems like a perfectly reasonable solution.

```java
public class TextEditor {

    private String content;

    private List<String> history;

}
```

But now the editor has two responsibilities.

- Managing the current document.
- Managing the history of the document.

These are completely different concerns.

The editor should only care about its **current state**.

History management should belong somewhere else.

---

Think of a real text editor like Microsoft Word.

When you press **Ctrl + S**, the editor creates a snapshot of the document.

The snapshot is a separate object.

Later, if you want to restore an older version, the editor simply loads one of those snapshots.

It doesn't know how many snapshots exist or where they are stored.

Exactly the same idea is implemented here.

The **Memento** represents one immutable snapshot of the editor's state.

It contains only the information required to restore the editor later.

</details>

---

## TextEditor.java (Originator)

```java
// Creates and restores snapshots of its state.
public class TextEditor {

    private String content;

    public TextEditor(String content) {
        this.content = content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    // Creates a snapshot of the current state.
    public Memento save() {
        return new Memento(content);
    }

    // Restores the previous state.
    public void restore(Memento memento) {
        this.content = memento.getContent();
    }

}
```

---

## History.java (Caretaker)

```java
import java.util.Stack;

// Stores all snapshots created by the editor.
public class History {

    private final Stack<Memento> history = new Stack<>();

    public void save(Memento memento) {
        history.push(memento);
    }

    public Memento undo() {

        if (history.isEmpty()) {
            return null;
        }

        return history.pop();

    }

}
```

<details>
<summary>💡 Why do we need a Caretaker (History)?</summary>

Another common question is:

> **Why not let the `TextEditor` store all the Memento objects?**

Because then the editor would again become responsible for:

- Editing text
- Creating snapshots
- Storing snapshots
- Managing Undo
- Managing Redo

Its responsibilities would keep increasing.

Instead, we separate the concerns.

```text
TextEditor

↓

Creates Snapshot

↓

History

↓

Stores Snapshot
```

Now each class has one clear responsibility.

| Class | Responsibility |
|--------|----------------|
| TextEditor | Edit and restore text |
| Memento | Store a snapshot |
| History | Manage snapshots |

This makes the design much easier to understand, test, and extend.

</details>

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        TextEditor editor = new TextEditor("Hello");

        History history = new History();

        history.save(editor.save());

        editor.setContent("Hello, World!");

        history.save(editor.save());

        editor.setContent("Hello, World! Welcome!");

        System.out.println("Current : " + editor.getContent());

        editor.restore(history.undo());

        System.out.println("Undo 1  : " + editor.getContent());

        editor.restore(history.undo());

        System.out.println("Undo 2  : " + editor.getContent());

    }

}
```

---

## Output

```text
Current : Hello, World! Welcome!

Undo 1  : Hello, World!

Undo 2  : Hello
```

---

## Execution Flow

```text
User edits document

        │

        ▼

TextEditor creates a Memento

        │

        ▼

History stores the Memento

        │

        ▼

User edits again

        │

        ▼

Another Memento is created

        │

        ▼

History stores it

        │

        ▼

User clicks Undo

        │

        ▼

History returns the previous Memento

        │

        ▼

TextEditor restores its state

        │

        ▼

Previous document is restored
```

Notice something important.

The **History** never looks inside a `Memento`.

The **Memento** never knows who stores it.

The **TextEditor** never knows how many snapshots exist.

Each class has a single responsibility, making the design loosely coupled and easy to extend.

</details>
---

# Handling New Requirements (Optional)

## New Requirement

The Product Owner now says,

> "We already support Undo.
> Now we also need Redo."

Explain why the current design can support this with minimal changes.

Implement:

- RedoHistory
or
- Two Stacks (Undo + Redo)

Show:

- What changed?
- What remained unchanged?
- Why the TextEditor class was untouched.

---

# Quick Revision

## Intent

Capture an object's state without exposing its internal implementation so that it can be restored later.

---

## Memory Trick

> The object creates snapshots of itself.
> Someone else safely stores those snapshots.

---

## Thumb Rule

If you hear requirements like:

- Undo
- Restore Previous State
- Checkpoints
- Version History
- Snapshot
- Rollback

Think about the **Memento Pattern**.

---

## Thinking Process

Need Undo

↓

Need Multiple Undo

↓

Editor Starts Managing History

↓

Separate State

↓

Create Snapshot (Memento)

↓

Store Snapshot Separately (Caretaker)

↓

Restore Snapshot

↓

Memento Pattern
