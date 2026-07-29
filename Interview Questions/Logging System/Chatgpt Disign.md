# LLD: Logging System

## 1. Problem: 
source: https://codewitharyan.com/tech-blogs/design-logging-system

Design a Logging System that supports:

- Log levels: `DEBUG`, `INFO`, `WARNING`, `ERROR`, `FATAL`
- Level/content-based filtering
- Different log formats
- Multiple destinations: Console, File, Network
- Context enrichment
- Chain of log handlers
- Runtime configuration
- Easy addition of new filters, formatters, handlers, and destinations

---

# 2. How to Think About the Problem

Do not directly jump to design patterns.

Follow:

```text
Requirement
    ↓
Example
    ↓
Runtime Flow
    ↓
Responsibilities
    ↓
Abstractions
    ↓
Design Patterns
```

---

# 3. Understand One Log

A log represents one application event.

```text
Log
├── timestamp
├── level
├── message
└── context
```

```java
enum LogLevel {
    DEBUG,
    INFO,
    WARNING,
    ERROR,
    FATAL
}
```

Context can contain additional metadata:

```text
filePath
threadName
requestId
serviceName
```

---

# 4. Understand the Runtime Flow

Example:

```text
ERROR: "Payment failed"
```

Configuration:

```text
INFO+  → Console
ERROR+ → File
FATAL  → Network
```

Flow:

```text
ERROR Log
    ↓
INFO+ Rule
    ↓ YES
Console
    ↓
ERROR+ Rule
    ↓ YES
File
    ↓
FATAL Rule
    ↓ NO
End
```

Therefore the ERROR log goes to:

```text
Console ✓
File    ✓
Network ✗
```

---

# 5. Filtering

Filtering does **not** mean storing logs and searching them later.

It means:

> Should the current log be processed by this handler?

Examples:

```text
Level >= ERROR

Message contains "DATABASE"

Service == "PaymentService"
```

Responsibility:

```java
interface LogFilter {
    boolean shouldProcess(Log log);
}
```

Possible implementations:

```text
LevelFilter
ContentFilter
ServiceFilter
```

---

# 6. Formatting

The same log can have different representations.

Console:

```text
[ERROR] Payment failed
```

File:

```text
12:30 | ERROR | PaymentService | Payment failed
```

Network:

```json
{
  "level": "ERROR",
  "message": "Payment failed"
}
```

Therefore formatting is a separate responsibility.

```java
interface LogFormatter {
    String format(Log log);
}
```

Implementations:

```text
BasicFormatter
DetailedFormatter
JsonFormatter
```

---

# 7. Destination

A destination decides **where/how the formatted log is written**.

```java
interface Destination {
    void send(String formattedLog);
}
```

Implementations:

```text
ConsoleDestination
FileDestination
NetworkDestination
```

---

# 8. LogHandler

One handler represents one configured logging rule.

Example:

```text
Handler
├── Filter      → Should I process?
├── Formatter   → How should it look?
└── Destination → Where should it go?
```

Conceptually:

```java
class LogHandler {

    LogFilter filter;
    LogFormatter formatter;
    Destination destination;

    LogHandler nextHandler;

    void handle(Log log) {

        if (filter.shouldProcess(log)) {

            String formattedLog =
                    formatter.format(log);

            destination.send(formattedLog);
        }

        if (nextHandler != null) {
            nextHandler.handle(log);
        }
    }
}
```

Handlers form a chain:

```text
Log
 ↓
Handler 1
 ↓
Handler 2
 ↓
Handler 3
 ↓
End
```

---

# 9. Context Enrichment

The logging system may automatically add metadata.

Example:

```text
Original Log
    ↓
Add threadName
Add serviceName
Add requestId
    ↓
Enriched Log
```

Responsibility:

```java
interface ContextEnricher {
    void enrich(ContextData context);
}
```

Possible implementations:

```text
ThreadContextEnricher
ServiceContextEnricher
RequestContextEnricher
```

---

# 10. LogService

`LogService` is the entry point used by the application.

Application simply calls:

```java
logService.log(
    LogLevel.ERROR,
    "Payment failed",
    context
);
```

`LogService`:

```text
Receive request
      ↓
Enrich Context
      ↓
Create Log
      ↓
Send to first LogHandler
```

It should NOT contain filtering, formatting, or destination-specific logic.

---

# 11. Final Design

```text
                    Application
                        │
                        ▼
                   LogService
                        │
                 ContextEnricher
                        │
                        ▼
                       Log
                        │
                        ▼
                   LogHandler
                  /     |      \
                 /      |       \
                ▼       ▼        ▼
          LogFilter Formatter Destination
                                   │
                         ┌─────────┼─────────┐
                         ▼         ▼         ▼
                      Console     File     Network

                        │
                        ▼
                   nextHandler
                        │
                        ▼
                   LogHandler
```

---

# 12. Main Responsibilities

| Component | Responsibility |
|---|---|
| `Log` | Represents one log event |
| `ContextData` | Stores log metadata |
| `ContextEnricher` | Adds additional context |
| `LogFilter` | Decides whether a log should be processed |
| `LogFormatter` | Converts a log into required representation |
| `Destination` | Outputs the formatted log |
| `LogHandler` | Coordinates Filter → Format → Destination |
| `LogService` | Entry point and starts handler chain |

---

# 13. Design Patterns Used

| Design Pattern | Where Used | Why |
|---|---|---|
| **Chain of Responsibility** | `LogHandler → nextHandler` | A log passes through multiple independent handlers |
| **Strategy** | `LogFormatter` | Formatting algorithm can change independently |
| **Strategy** | `LogFilter` | Different filtering rules can be plugged in |
| **Strategy / Polymorphism** | `Destination` | Console, File, Network can be switched/extended easily |
| **Strategy / Pipeline** | `ContextEnricher` | Different context enrichment behaviors can be added |
| **Facade-like Service** | `LogService` | Gives applications a simple entry point to the logging subsystem |

---

# 14. Key LLD Thinking

For unfamiliar problems, think in this order:

```text
1. WHAT?
   Understand requirements

2. FLOW?
   Run one concrete example

3. WHO?
   Identify responsibilities

4. HOW?
   Create abstractions/classes/interfaces

5. PATTERNS?
   Recognize suitable design patterns

6. CODE
```

### Important

Do not think:

```text
Requirement keyword
      ↓
Design Pattern
      ↓
Classes
```

Prefer:

```text
Example
   ↓
Flow
   ↓
Responsibility
   ↓
Abstraction
   ↓
Pattern
   ↓
Code
```

> **Design patterns should emerge from the responsibilities and relationships in the solution rather than being forced into the problem at the beginning.**
