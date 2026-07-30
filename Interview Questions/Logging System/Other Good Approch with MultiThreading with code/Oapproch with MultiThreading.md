# LLD: Logging System — YouTube Approach
source: https://www.youtube.com/watch?v=hOzH7ecc8vg&t=360s
## 1. Requirements

Design a logging system that supports:

- Multiple log levels
  - `DEBUG`
  - `INFO`
  - `WARN`
  - `ERROR`
  - etc.
- Multiple output destinations
  - Console
  - File
- Multiple formatting styles
  - Text
  - JSON
- Different destinations for different log levels
- Extensibility for new levels, appenders, and formatters
- Thread-safe logging

---

# 2. Core Domain Object

A `LogMessage` represents one log event.

```text
LogMessage
├── LogLevel
├── message
└── timestamp
```

Example:

```text
ERROR
"Payment failed"
1723456789
```

---

# 3. Log Formatter — Strategy Pattern

Different destinations may require different log formats.

For example:

### Text

```text
[ERROR] Payment failed
```

### JSON

```json
{
    "level": "ERROR",
    "message": "Payment failed"
}
```

Create a common abstraction:

```java
interface LogFormatter {
    String format(LogMessage logMessage);
}
```

Implementations:

```text
LogFormatter
      ↑
 ┌────┴─────┐
 │          │
Text      JSON
Formatter Formatter
```

So a new formatter can be added without modifying existing formatters.

**Pattern:** Strategy Pattern.

---

# 4. Log Appender — Output Destination

Appender represents **where the log should be written**.

```java
interface LogAppender {
    void append(LogMessage logMessage);
}
```

Implementations:

```text
LogAppender
     ↑
 ┌───┴────┐
 │        │
Console  File
Appender Appender
```

Each appender can use its own formatter.

Example:

```text
ConsoleAppender
    ↓
TextFormatter


FileAppender
    ↓
JsonFormatter
```

Conceptually:

```java
class ConsoleAppender implements LogAppender {

    LogFormatter formatter;

    public void append(LogMessage logMessage) {

        String formattedLog =
                formatter.format(logMessage);

        System.out.println(formattedLog);
    }
}
```

---

# 5. Log Handlers — Chain of Responsibility

Instead of putting log-level conditions inside `Logger`:

```java
if (level == INFO) {
    ...
}
else if (level == WARN) {
    ...
}
else if (level == ERROR) {
    ...
}
```

create separate handlers.

```text
              LogHandler
                  ↑
       ┌──────────┼──────────┐
       │          │          │
 InfoHandler  WarnHandler ErrorHandler
```

Handlers are connected:

```text
InfoHandler
     ↓
WarnHandler
     ↓
ErrorHandler
```

When a log enters:

```text
Log
 ↓
InfoHandler
 ↓
Can I handle it?
 ↓ No
WarnHandler
 ↓
Can I handle it?
 ↓ No
ErrorHandler
 ↓
Process
```

Conceptually:

```java
abstract class LogHandler {

    protected LogHandler nextHandler;

    public void setNext(LogHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract void handle(LogMessage log);
}
```

**Pattern:** Chain of Responsibility.

---

# 6. Handler → Appenders — Observer Pattern

A particular log level may need to go to multiple destinations.

Example:

```text
DEBUG
  ↓
Console


ERROR
  ↓
Console
+
File
```

Therefore each handler maintains a collection of appenders.

```text
ErrorHandler
     │
     ├── ConsoleAppender
     │
     └── FileAppender
```

The handler acts as the **Subject**.

The appenders act as **Observers**.

When the handler receives a matching log:

```text
ErrorHandler
     ↓
Notify all registered appenders
     ↓
 ┌───┴────┐
 ▼        ▼
Console  File
```

Conceptually:

```java
class ErrorHandler extends LogHandler {

    List<LogAppender> appenders;

    void handle(LogMessage log) {

        if (canHandle(log)) {

            for (LogAppender appender : appenders) {
                appender.append(log);
            }

        } else if (nextHandler != null) {

            nextHandler.handle(log);
        }
    }
}
```

**Pattern:** Observer Pattern.

---

# 7. Logger — Singleton Pattern

Application code should have a simple logging API:

```java
logger.info("Order created");
logger.warn("Inventory running low");
logger.error("Payment failed");
```

The `Logger` creates the `LogMessage` and sends it to the first handler.

```text
Application
     ↓
   Logger
     ↓
Create LogMessage
     ↓
First LogHandler
```

The video models `Logger` as a Singleton so that the application uses a single shared logger instance.

```text
Application Components
      │
      │
 ┌────┼────┐
 ▼    ▼    ▼
 A    B    C
  \   |   /
      ▼
    Logger
  Singleton
```

**Pattern:** Singleton Pattern.

---

# 8. Configuration

Configuration is responsible for setting up:

- Handler chain
- Appenders for each handler
- Formatter used by each appender

Example configuration:

```text
INFO
 ↓
ConsoleAppender
 ↓
TextFormatter


WARN
 ↓
ConsoleAppender
 ↓
TextFormatter


ERROR
 ├── ConsoleAppender → TextFormatter
 │
 └── FileAppender → JSONFormatter
```

The configuration therefore builds the relationships between:

```text
Handler
   ↓
Appender
   ↓
Formatter
```

---

# 9. Complete Runtime Flow

Suppose application executes:

```java
logger.error("Payment failed");
```

Flow:

```text
Application
     ↓
Logger.error(...)
     ↓
Create LogMessage
     ↓
Start Handler Chain
     ↓
InfoHandler
     ↓
not handled
     ↓
WarnHandler
     ↓
not handled
     ↓
ErrorHandler
     ↓
handled
     ↓
Notify Appenders
     ↓
 ┌───────────────┐
 ▼               ▼
ConsoleAppender FileAppender
     │               │
     ▼               ▼
TextFormatter    JsonFormatter
     │               │
     ▼               ▼
Console            File
```

---

# 10. Multithreading

Multiple application threads can use the logger simultaneously.

```text
Thread 1
    ↓
logger.error("Payment failed")


Thread 2
    ↓
logger.info("Order created")
```

Individual log messages should not become corrupted/interleaved.

The video handles two important concurrency problems.

---

## 10.1 Concurrent Modification of Appenders

Each handler maintains a list of appenders.

Example:

```text
ErrorHandler
    │
    ├── ConsoleAppender
    └── FileAppender
```

Consider:

```text
Thread 1
    ↓
Iterating appenders

while

Thread 2
    ↓
Adds/removes an appender
```

Using a normal `ArrayList` can cause:

```text
ConcurrentModificationException
```
<details>
<summary><b>IMP Concurrency: Why CopyOnWriteArrayList instead of synchronized ArrayList?</b></summary>

### Problem

A `LogHandler` maintains multiple appenders:

```java
List<LogAppender> appenders;
```

For example:

```text
ErrorHandler
    ├── ConsoleAppender
    └── FileAppender
```

For every log, we iterate over the appenders:

```java
for (LogAppender appender : appenders) {
    appender.append(logMessage);
}
```

At runtime, multiple threads may access this list.

For example:

```text
Thread 1 → Iterating over appenders

Thread 2 → Removing/adding an appender
```

With a normal `ArrayList`, concurrent modification during iteration can cause problems such as `ConcurrentModificationException`.

---

### Option 1: ArrayList + synchronized

We can make an `ArrayList` thread-safe using synchronization:

```java
private final List<LogAppender> appenders = new ArrayList<>();

public synchronized void addAppender(LogAppender appender) {
    appenders.add(appender);
}

public synchronized void removeAppender(LogAppender appender) {
    appenders.remove(appender);
}

public synchronized void notifyAppenders(LogMessage log) {
    for (LogAppender appender : appenders) {
        appender.append(log);
    }
}
```

This is correct, but synchronization means only one thread can execute these synchronized operations on the same object at a time.

```text
Thread 1 → iterate appenders
              ↓
           holds lock

Thread 2 → WAIT
Thread 3 → WAIT
```

Logging happens frequently, so this can create unnecessary contention.

---

### Option 2: CopyOnWriteArrayList

```java
private final List<LogAppender> appenders =
        new CopyOnWriteArrayList<>();
```

`CopyOnWriteArrayList` follows:

```text
Copy On Write
```

Reads/iterations use the existing array.

When the list is modified, a new copy of the underlying array is created.

Example:

```text
Original:

Array A
[Console, File]
```

Thread 1 starts iterating:

```text
Thread 1
    ↓
Array A
[Console, File]
```

Thread 2 removes `FileAppender`.

Instead of modifying `Array A`, Java creates a new copy:

```text
Array A                    Array B

[Console, File]    →       [Console, File]
                               ↓
                          remove File
                               ↓
                           [Console]
```

Thread 1 continues safely using the old snapshot:

```text
Thread 1 → [Console, File]
```

New operations see:

```text
[Console]
```

Therefore iteration is not disrupted.

---

### Why is CopyOnWriteArrayList suitable for Logging?

Our workload is:

```text
Iterating appenders     → VERY FREQUENT
Adding/removing appender → VERY RARE
```

`CopyOnWriteArrayList` is a good fit for **read-heavy, write-rare** collections.

Its downside is that modification is expensive because the underlying array must be copied.

But appender configuration changes rarely compared to how frequently logs are generated.

---

### ArrayList + synchronized vs CopyOnWriteArrayList

| Approach | Thread Safe? | Trade-off |
|---|---|---|
| `ArrayList` | ❌ | Concurrent modification is unsafe |
| `ArrayList + synchronized` | ✅ | Frequent iteration may require locking |
| `CopyOnWriteArrayList` | ✅ | Fast/safe iteration, but modifications are expensive |

For this logging system:

```text
CopyOnWriteArrayList
        ✓
```

is a better fit because reads/iterations are much more frequent than modifications.

---

### Don't Confuse This With synchronized FileAppender

They solve two different concurrency problems:

```text
LogHandler
    │
    │ CopyOnWriteArrayList
    │
    │ Protects the collection of appenders
    ▼
Appenders
    │
    ▼
FileAppender
    │
    │ synchronized
    │
    │ Protects writing to shared file
    ▼
File
```

Remember:

```text
Shared collection being modified
        ↓
CopyOnWriteArrayList

Shared resource being written
        ↓
synchronized
```

</details>

### Solution

Use:

```java
CopyOnWriteArrayList<LogAppender>
```

instead of:

```java
ArrayList<LogAppender>
```

Conceptually:

```java
private final List<LogAppender> appenders =
        new CopyOnWriteArrayList<>();
```

This allows:

```text
Thread 1
    ↓
Safely iterate appenders

        simultaneously

Thread 2
    ↓
Add/remove appender
```

---

## 10.2 Multiple Threads Writing to Same File

Suppose:

```text
Thread 1 → writes "Hello"

Thread 2 → writes "World"
```

Without synchronization, writes could become interleaved/corrupted.

We want either:

```text
Hello
World
```

or:

```text
World
Hello
```

but not a mixed output.

### Solution

Synchronize the file write operation.

```java
public synchronized void append(
        LogMessage logMessage) {

    String formattedLog =
            formatter.format(logMessage);

    writer.write(formattedLog);

    writer.flush();
}
```

Flow:

```text
Thread 1
    ↓
Acquire FileAppender lock
    ↓
Write complete log
    ↓
Release lock

Thread 2
    ↓
Acquire lock
    ↓
Write complete log
    ↓
Release lock
```

Therefore only one thread writes to the shared file at a time.

---

# 11. Why Two Different Concurrency Solutions?

| Problem | Solution | Reason |
|---|---|---|
| Appender list modified while another thread iterates | `CopyOnWriteArrayList` | Safe concurrent iteration/modification |
| Multiple threads write to same file | `synchronized` | Prevents interleaved writes |

Quick way to remember:

```text
Shared Collection
      ↓
CopyOnWriteArrayList


Shared File/Resource
      ↓
synchronized
```

---

# 12. Final Architecture

```text
                         Logger
                       Singleton
                           │
                           ▼
                    LogMessage
                           │
                           ▼
                  LogHandler Chain
                Chain of Responsibility
                           │
              CopyOnWriteArrayList
                    of Appenders
                           │
                       Observer
                    ┌──────┴──────┐
                    ▼             ▼
            ConsoleAppender   FileAppender
                    │             │
                    │       synchronized
                    │          append()
                    │             │
                    └──────┬──────┘
                           ▼
                     LogFormatter
                       Strategy
                     ┌─────┴─────┐
                     ▼           ▼
                   Text         JSON
                Formatter     Formatter
```

---

# 13. Design Patterns Used

| Design Pattern | Where Used | Why |
|---|---|---|
| **Chain of Responsibility** | `LogHandler` chain | Pass log through handlers until appropriate handler processes it |
| **Observer** | Handler → Appenders | One handler can notify multiple destinations |
| **Strategy** | `LogFormatter` | Different formatting algorithms can be plugged in |
| **Strategy / Polymorphism** | `LogAppender` | Console/File destinations share a common abstraction |
| **Singleton** | `Logger` | Provides one globally shared logger instance |
| **Configuration** | Handler/Appender setup | Centralizes construction and runtime logging configuration |

---

# 14. Responsibilities

| Component | Responsibility |
|---|---|
| `LogMessage` | Represents one log event |
| `Logger` | Application-facing logging API |
| `LogHandler` | Handles a particular log level and forms the chain |
| `LogAppender` | Writes logs to a destination |
| `LogFormatter` | Formats the log before output |
| Configuration | Builds handler chain and attaches appenders |
| `CopyOnWriteArrayList` | Makes appender collection safe for concurrent modification |
| `synchronized` | Protects shared file writes |

---

# 15. Interview Summary

The complete flow can be remembered as:

```text
Application
     ↓
Logger
     ↓
LogMessage
     ↓
Handler Chain
     ↓
Matching Handler
     ↓
Notify Appenders
     ↓
Format
     ↓
Console / File
```

Patterns:

```text
Handler → Handler
    = Chain of Responsibility

Handler → Multiple Appenders
    = Observer

Appender → Formatter
    = Strategy

Global Logger
    = Singleton
```

Concurrency:

```text
Appender collection
    = CopyOnWriteArrayList

Shared file writing
    = synchronized
```
