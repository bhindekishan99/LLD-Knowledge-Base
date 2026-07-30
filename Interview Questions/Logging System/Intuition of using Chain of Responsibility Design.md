## Why Chain of Responsibility?

Initially, a simpler design can be:

```text
Logger
  ↓
Map<LogLevel, FormatStrategy>
  ↓
Formatter
  ↓
Map<LogLevel, List<AppenderStrategy>>
  ↓
Appender(s)
```

For example:

```text
ERROR → JSON Formatter → [Console, File]
INFO  → Text Formatter → [Console]
```

```java
Map<LogLevel, LogFormatter> formatterConfig;
Map<LogLevel, List<LogAppender>> appenderConfig;
```

This is perfectly valid if routing depends only on `LogLevel`.

## Then Why Use Chain of Responsibility? How LogHandler Evolves into Chain of Responsibility

Suppose routing no longer depends only on `LogLevel`.

Example:

```text
ERROR + message contains "Payment"
    → JSON Formatter
    → Console + File

ERROR + message contains "Database"
    → Detailed Formatter
    → File
```

A simple `Map<LogLevel, Configuration>` is no longer sufficient.

---

### Step 1: Extract Routing Logic from Logger

We should not put all decision-making inside `Logger`, like this

```java
List<LogHandler> handlers;

for (LogHandler handler : handlers) {
    if (handler.canHandle(log)) {
        handler.handle(log);
    }
}
```

Instead:

```text
Logger
   ↓
LogHandler
```

`LogHandler` decides:

```text
Should this log be processed?
How should it be formatted?
Where should it be sent?
```

But **having a `LogHandler` does NOT mean we are using Chain of Responsibility.**

---

### Step 2: LogHandler Starts Growing

Over time:

```java
if (ERROR && message.contains("Payment")) {
    ...
}
else if (ERROR && message.contains("Database")) {
    ...
}
else if (FATAL) {
    ...
}
else if (WARNING && message.contains("Memory")) {
    ...
}
```

Now `LogHandler` itself becomes too complex.

So split it into specialized handlers:

```text
PaymentLogHandler
DatabaseLogHandler
MemoryLogHandler
FatalLogHandler
```

Each handler knows only its own matching and processing logic.

---

### Step 3: Connect Handlers → Chain of Responsibility

Instead of `Logger` deciding which handler to call:

```text
Logger
   ↓
PaymentHandler
   ↓
DatabaseHandler
   ↓
MemoryHandler
   ↓
FatalHandler
```

`Logger` only knows the first handler:

```java
firstHandler.handle(log);
```

Each handler does:

```java
void handle(Log log) {

    if (canHandle(log)) {
        process(log);
        return;
    }

    if (nextHandler != null) {
        nextHandler.handle(log);
    }
}
```

Now this becomes **Chain of Responsibility**.

---

### Key Takeaway

```text
Complex routing in Logger
        ↓
Extract LogHandler
        ↓
LogHandler becomes complex
        ↓
Split into specialized handlers
        ↓
Connect handlers
        ↓
Chain of Responsibility
```

> **`LogHandler` is a responsibility/class. Chain of Responsibility describes how multiple handlers are connected and how a request moves between them.**

So we can have a `LogHandler` **without** using Chain of Responsibility.


### Key Takeaway

| Situation | Better Approach |
|---|---|
| Routing is simply `LogLevel → Configuration` | `Map` |
| Request must pass through multiple processors | Chain of Responsibility |
| Each handler has its own matching/filtering logic | Chain of Responsibility |

> **Don't use COR just because it is a Logging System. Use it when the request naturally needs to pass through a chain of independent handlers.**
