# Java Executors and `ExecutorService`

## What Is an Executor?

**An executor manages threads for us.**

```text
We submit tasks.
The executor chooses a worker thread to run each task.
```

```java
executor.submit(() -> {
    System.out.println("Task is running");
});
```

`submit()` accepts a `Runnable` or `Callable`.

## `newFixedThreadPool(n)`

```java
ExecutorService executor = Executors.newFixedThreadPool(2);
```

- Creates exactly `2` reusable worker threads.
- Only `2` tasks run at the same time.
- Extra tasks wait in a queue.
- Best default choice for controlled parallel work.

```text
Task 1 → Worker 1
Task 2 → Worker 2
Task 3 → waits
Task 4 → waits
```

Use for API calls, file processing, database tasks, and CPU work.

## `newCachedThreadPool()`

```java
ExecutorService executor = Executors.newCachedThreadPool();
```

- Creates new threads when needed.
- Reuses idle threads.
- Idle threads are removed after about 60 seconds.
- Has no fixed maximum number of threads.

```text
Many long-running tasks
        ↓
May create too many threads
        ↓
Can slow down or crash the application
```

Use only for short tasks when you control how many tasks are submitted.

## `newSingleThreadExecutor()`

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
```

- Creates one worker thread.
- Tasks run one by one.
- Tasks run in submission order.

```text
Task 1 → runs
Task 2 → waits
Task 3 → waits
```

Use when order matters, for example logging or sequential file updates.

## `newScheduledThreadPool(n)`

```java
ScheduledExecutorService scheduler =
    Executors.newScheduledThreadPool(2);
```

Run a task once after a delay:

```java
scheduler.schedule(
    () -> System.out.println("Runs after 5 seconds"),
    5,
    TimeUnit.SECONDS
);
```

Run a task repeatedly:

```java
scheduler.scheduleAtFixedRate(
    () -> System.out.println("Runs every 10 seconds"),
    0,
    10,
    TimeUnit.SECONDS
);
```

Use for reminders, cleanup tasks, and periodic jobs.

# Important Methods

## `submit()`

Submits a task to the executor.

```java
executor.submit(() -> {
    System.out.println("Task is running");
});
```

The executor gives the task to an available worker thread.

## `shutdown()`

```java
executor.shutdown();
```

- Stops accepting new tasks.
- Already submitted tasks finish normally.
- Use this for normal shutdown.

## `shutdownNow()`

```java
executor.shutdownNow();
```

- Stops accepting new tasks.
- Removes tasks still waiting in the queue.
- Sends an interrupt request to tasks that are currently running.
- It does not forcefully kill a running thread.

```text
Task 1 → currently running → receives interrupt request
Task 2 → waiting → removed
Task 3 → waiting → removed
```

Use only for urgent shutdown, timeout, or application exit.

## `awaitTermination()`

```java
executor.shutdown();

executor.awaitTermination(
    1,
    TimeUnit.MINUTES
);
```

Makes the current thread wait until:

- All executor tasks finish, or
- The specified timeout is reached.

## Typical Shutdown Pattern

```java
executor.shutdown();

try {
    if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        executor.shutdownNow();
    }
} catch (InterruptedException exception) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}
```

# Quick Summary

| Executor                      |            Threads | Best Use                                 |
| ----------------------------- | -----------------: | ---------------------------------------- |
| `newFixedThreadPool(n)`     | Fixed`n` threads | Controlled parallel work                 |
| `newCachedThreadPool()`     |  Grows when needed | Short tasks with controlled task arrival |
| `newSingleThreadExecutor()` |         One thread | Sequential tasks where order matters     |
| `newScheduledThreadPool(n)` | Fixed`n` threads | Delayed and repeated tasks               |

## `execute()`

```java
executor.execute(() -> {
    System.out.println("Task is running");
});
```

`execute()` submits a `Runnable` task to the executor. it's kind of fire and forget

```text
execute() → Runs a task, but does not return anything.
```

Use it when you do not need a result from the task.

## `submit()`

```java
Future<Integer> future = executor.submit(() -> {
    return 10 + 20;
});

System.out.println(future.get()); // 30
```

`submit()` can accept:

- `Runnable`
- `Callable<T>`

It returns a `Future`.

```text
Future → Represents the result of a task that may finish later.
```

## `execute()` vs `submit()`

| Method        | Accepts                         | Returns    | Use When                                |
| ------------- | ------------------------------- | ---------- | --------------------------------------- |
| `execute()` | `Runnable`                    | Nothing    | You only want to run a task(fire and forget)             |
| `submit()`  | `Runnable` or `Callable<T>` | `Future` | You need a result, status, or exception |

Example:

```java
executor.execute(() -> {
    System.out.println("Send notification");
});

Future<String> future = executor.submit(() -> {
    return "User data loaded";
});

System.out.println(future.get());
```

Use `execute()` for simple background tasks.

Use `submit()` when the task returns a value or you need to check its completion with `Future`.
