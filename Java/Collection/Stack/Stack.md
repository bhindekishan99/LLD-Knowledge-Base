# Stack Implementations in Java

## 1. Legacy `Stack` Class

```java
import java.util.Stack;

Stack<Integer> stack = new Stack<>();

stack.push(10);
stack.push(20);

System.out.println(stack.peek()); // 20
System.out.println(stack.pop());  // 20
```

## 2. `LinkedList` as a Stack

```java
import java.util.Deque;
import java.util.LinkedList;

Deque<Integer> stack = new LinkedList<>();

stack.push(10);
stack.push(20);

System.out.println(stack.peek()); // 20
System.out.println(stack.pop());  // 20
```

## 3. `ArrayDeque` as a Stack — Recommended

```java
import java.util.ArrayDeque;
import java.util.Deque;

Deque<Integer> stack = new ArrayDeque<>();

stack.push(10); // [10]
stack.push(20); // [20, 10]
stack.push(30); // [30, 20, 10]

System.out.println(stack.peek()); // 30
System.out.println(stack.pop());  // 30
System.out.println(stack);        // [20, 10]

System.out.println(stack.isEmpty()); // false
System.out.println(stack.size());    // 2
```

`ArrayDeque` is the modern default for stack usage in Java.

```text
push(value) → add value to the top
peek()      → view the top value
pop()       → remove and return the top value
```


# Most Efficient Stack Implementation in Java

For a normal stack in Java, use:

```java
Deque<Integer> stack = new ArrayDeque<>();
```

It is generally the most efficient choice.

| Implementation | Use it? | Why |
|---|---|---|
| `ArrayDeque` | Recommended | Fast, low memory overhead, modern API |
| `LinkedList` | Usually no | Each item needs an extra node/object and links |
| `Stack` | No for new code | Legacy class; unnecessarily synchronized |

`ArrayDeque` gives amortized `O(1)` time complexity for:

```java
push()
pop()
peek()
```

Use `LinkedList` only when you specifically need linked-list behavior.

Avoid `Stack` in new Java code.