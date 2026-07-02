# Chain of Responsibility Design Pattern

> **Category:** Behavioral Design Pattern

---

# Source

## 🎥 YouTube

https://www.youtube.com/watch?v=SYulyXp5zKs&t=1s

## 🌐 Blog

https://codewitharyan.com/tech-blogs/chain-of-responsibiliy-design-pattern

---

# How to Think Towards the Chain of Responsibility Pattern

Forget the Chain of Responsibility Pattern for a moment.

Imagine you're building a **Leave Approval System**. Employees submit leave requests, and different people in the organization are responsible for approving them based on the number of leave days.

Initially, the approval rules are very simple.

- **1–3 days** → Supervisor
- **4–7 days** → Manager
- **8–14 days** → Director

As a developer, what would you naturally write?

Probably something like this.

```java
if (leaveDays <= 3) {
    System.out.println("Supervisor Approved");
} else if (leaveDays <= 7) {
    System.out.println("Manager Approved");
} else if (leaveDays <= 14) {
    System.out.println("Director Approved");
} else {
    System.out.println("Leave Rejected");
}
```

This is exactly how most developers—including senior engineers—would start.

At this point, there is absolutely nothing wrong with this implementation. It is simple, easy to understand, and solves today's problem.

---

## Requirements Start Growing

As the application evolves, the approval process becomes more complex.

The Product Owner introduces new requirements.

- If the Director cannot approve, forward the request to **HR**.
- If HR cannot approve, forward it to the **Vice President**.
- Certain requests must also be reviewed by the **Security Team**.
- Future approvers may be added without changing the existing approval flow.

Naturally, the `if-else` chain keeps growing.

```java
if (...) {

} else if (...) {

} else if (...) {

} else if (...) {

} else if (...) {

}
```

Every new approver requires modifying the same method.

As the business grows, this method becomes harder to maintain.

---

## ⭐ Thumb Rule

At this point, don't immediately think about the Chain of Responsibility Pattern.

Instead, ask yourself one simple question.

> **Should this request travel through multiple handlers until someone handles it?**

### If **No**

Keep the existing implementation.

Example:

```java
if (marks >= 90) {
    grade = "A";
} else if (marks >= 75) {
    grade = "B";
} else {
    grade = "C";
}
```

Nothing is being forwarded.

The application simply selects one outcome.

Using the Chain of Responsibility Pattern here would only make the solution more complicated.

### If **Yes**

Think about the Chain of Responsibility Pattern.

Example:

```text
Leave Request
      │
      ▼
Supervisor
      │
Cannot Handle
      ▼
Manager
      │
Cannot Handle
      ▼
Director
      │
Handles Request
      ▼
Stop
```

Notice something.

The **request moves from one handler to another** until someone processes it.

The sender never decides who should handle the request.

That is the biggest clue that this pattern might be a good fit.

---

## Key Observation

Forget the code for a moment.

Instead, observe the workflow.

```text
Employee
    │
    ▼
Submit Leave Request
    │
    ▼
Supervisor
    │
    ▼
Manager
    │
    ▼
Director
```

Now ask yourself.

> **Does the employee know who will finally approve the leave?**

No.

The employee simply submits the request.

Whether it is approved by the Supervisor, Manager, Director, or HR is none of the employee's concern.

The sender should not be tightly coupled to the receiver.

This is the most important realization.

---

## First Refactoring

Instead of writing one giant method that knows every approver, let every approver become responsible only for itself.

For example, the Supervisor should only answer one question.

```text
Can I approve this request?

      │

 ┌────┴────┐
 │         │
Yes        No
 │         │
Approve   Forward
```

Likewise, every other approver follows exactly the same logic.

- Can I handle it?
- If yes, process it.
- Otherwise, pass it to the next approver.

Now every class has a single responsibility.

---

## Another Problem Appears

Now every approver can process requests.

But another question appears.

> **How does the Supervisor know who comes next?**

Should it know about the Director?

Should it know about HR?

Should it know about the CEO?

No.

It only needs to know **one thing**.

```java
Approver nextApprover;
```

The Supervisor only knows the Manager.

The Manager only knows the Director.

The Director only knows HR.

Every handler only knows its immediate successor.

This creates a chain.

---

## Final Refactoring

Notice something else.

Every approver performs exactly the same operation.

```java
processLeaveRequest()
```

Whenever multiple classes expose the same behavior, our brain naturally thinks,

> **"Can I create one common abstraction?"**

The answer is **Yes**.

That abstraction becomes

```java
Approver
```

Every concrete approver extends this common abstraction.

The sender now communicates only with the first handler.

The request automatically travels through the chain until someone handles it.

---

## Complete Thinking

```text
Need Leave Approval

        ↓

Write if-else

        ↓

Requirements Grow

        ↓

Observe the Workflow

        ↓

The Request Travels

        ↓

Each Handler Handles One Responsibility

        ↓

Need Reference to Next Handler

        ↓

Create the Chain

        ↓

Need Common Abstraction

        ↓

Create Abstract Handler

        ↓

Chain of Responsibility Pattern
```

---

# How to Remember the Chain of Responsibility Pattern

Imagine you call your Internet Service Provider because your internet is not working.

Your call first reaches **Level 1 Support**.

If they cannot solve the problem, they forward it to **Level 2 Support**.

If the issue is still unresolved, it goes to a **Senior Engineer**.

Finally, if required, it reaches the **Network Team**.

```text
Customer
      │
      ▼
Level 1 Support
      │
      ▼
Level 2 Support
      │
      ▼
Senior Engineer
      │
      ▼
Network Team
```

Notice something.

You never decide who should solve your problem.

You simply raise a request.

The request itself keeps moving until someone handles it.

That is exactly how the Chain of Responsibility Pattern works.

## Memory Trick

Remember this one sentence.

> **Pass the request until someone can handle it.**

Whenever you notice:

- Multiple possible handlers.
- The sender shouldn't know who will process the request.
- The request should automatically move from one handler to another.

Ask yourself,

> **"Can I create a chain of handlers instead of writing one huge if-else?"**

If the answer is **Yes**, you're probably looking at the **Chain of Responsibility Pattern**.

---

# State Pattern vs Chain of Responsibility Pattern

These two patterns are often confused because both involve multiple objects.

However, they solve completely different problems.

| State Pattern | Chain of Responsibility Pattern |
|---------------|---------------------------------|
| Changes an object's behavior based on its current state. | Passes a request through multiple handlers until one handles it. |
| It can form a loop, red->yellow->green, it continue doing it unlike chain of responsibility it does'not stop when request is handled. | it is in liner form. |
| Only one state is active at a time. | Multiple handlers get an opportunity to process the request. |

### Quick Thumb Rule

- **State Pattern** → Same object, different state, different behavior.
- **Chain of Responsibility Pattern** → Same request, different handlers, until someone processes it.

---

# Traditional Implementation

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## LeaveRequest.java

```java
// Represents a leave request submitted by an employee.
public class LeaveRequest {

    private final String employeeName;
    private final int leaveDays;

    public LeaveRequest(String employeeName, int leaveDays) {
        this.employeeName = employeeName;
        this.leaveDays = leaveDays;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public int getLeaveDays() {
        return leaveDays;
    }

}
```

---

## LeaveApprovalSystem.java

```java
// Handles leave approval using a long if-else chain.
public class LeaveApprovalSystem {

    // Approves leave based on the number of leave days.
    public void approveLeave(LeaveRequest request) {

        int leaveDays = request.getLeaveDays();

        if (leaveDays <= 3) {

            System.out.println(
                    "Supervisor approved " + leaveDays + " day(s) leave for "
                            + request.getEmployeeName());

        } else if (leaveDays <= 7) {

            System.out.println(
                    "Manager approved " + leaveDays + " day(s) leave for "
                            + request.getEmployeeName());

        } else if (leaveDays <= 14) {

            System.out.println(
                    "Director approved " + leaveDays + " day(s) leave for "
                            + request.getEmployeeName());

        } else {

            System.out.println(
                    "Leave request rejected for "
                            + request.getEmployeeName());

        }

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        LeaveApprovalSystem approvalSystem = new LeaveApprovalSystem();

        approvalSystem.approveLeave(
                new LeaveRequest("John", 2));

        approvalSystem.approveLeave(
                new LeaveRequest("Alice", 6));

        approvalSystem.approveLeave(
                new LeaveRequest("Bob", 10));

        approvalSystem.approveLeave(
                new LeaveRequest("David", 20));

    }

}
```

---

## Output

```text
Supervisor approved 2 day(s) leave for John
Manager approved 6 day(s) leave for Alice
Director approved 10 day(s) leave for Bob
Leave request rejected for David
```

---

## Problems With This Approach

Although this implementation works correctly, it has several design problems.

### 1. The class knows every approver

The `LeaveApprovalSystem` is tightly coupled with every approver.

```text
LeaveApprovalSystem
        │
        ├── Supervisor
        ├── Manager
        ├── Director
        └── Future Approvers...
```

Whenever a new approver is introduced, this class must be modified.

---

### 2. Violates the Open/Closed Principle (OCP)

Suppose the company introduces a new approver.

```text
CEO
```

Now the existing method must be modified.

```java
else if (leaveDays <= 30) {

    System.out.println("CEO Approved");

}
```

Instead of extending the application, we're modifying already tested code.

This violates the **Open/Closed Principle**.

---

### 3. Tight Coupling

The sender directly decides who should approve the request.

```text
Employee

        │

        ▼

LeaveApprovalSystem

        │

        ├── Supervisor
        ├── Manager
        ├── Director
        └── HR
```

If the approval hierarchy changes, this class must change too.

---

### 4. Hard to Maintain

As more approvers are introduced,

the `if-else` chain keeps growing.

```java
if (...) {

} else if (...) {

} else if (...) {

} else if (...) {

} else if (...) {

}
```

Reading and maintaining the code becomes increasingly difficult.

---

### 5. Difficult to Extend

Imagine tomorrow the company introduces:

- HR
- Vice President
- CEO
- International Approval Team

Every new approver requires modifying the same method again.

As the organization grows,

this design becomes harder to maintain.

This is exactly where the **Chain of Responsibility Pattern** provides a much cleaner solution.

</details>

---

# Chain of Responsibility Pattern Implementation

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Approver.java

```java
// Abstract handler in the chain.
public abstract class Approver {

    // Reference to the next handler in the chain.
    protected Approver nextApprover;

    // Sets the next handler in the chain.
    public void setNextApprover(Approver nextApprover) {
        this.nextApprover = nextApprover;
    }

    // Processes the leave request.
    public abstract void processLeaveRequest(LeaveRequest request);

}
```

<details>
<summary>💡 Why do we need an Abstract Class?</summary>

A common question is:

> **Why not simply create an interface?**

Initially, an interface may look like this.

```java
public interface Approver {

    void processLeaveRequest(LeaveRequest request);

}
```

Now every approver must implement its own forwarding logic.

```java
class Supervisor implements Approver {

    private Manager manager;

    @Override
    public void processLeaveRequest(LeaveRequest request) {

        if (request.getLeaveDays() <= 3) {

            System.out.println("Supervisor Approved");

        } else {

            manager.processLeaveRequest(request);

        }

    }

}
```

The same forwarding logic is repeated in every handler.

```java
if (nextApprover != null) {
    nextApprover.processLeaveRequest(request);
}
```

This duplication grows as more handlers are added.

---

An abstract class allows us to move the common state into one place.

```java
protected Approver nextApprover;
```

It also provides a common implementation for linking the chain.

```java
public void setNextApprover(Approver nextApprover) {
    this.nextApprover = nextApprover;
}
```

Now every concrete handler only focuses on one responsibility:

- Can I handle the request?
- If not, forward it.

The chain infrastructure is reused by every handler.

</details>

---

## Supervisor.java

```java
// Handles leave requests up to 3 days.
public class Supervisor extends Approver {

    @Override
    public void processLeaveRequest(LeaveRequest request) {

        if (request.getLeaveDays() <= 3) {

            System.out.println(
                    "Supervisor approved "
                            + request.getLeaveDays()
                            + " day(s) leave for "
                            + request.getEmployeeName());

        } else if (nextApprover != null) {

            nextApprover.processLeaveRequest(request);

        }

    }

}
```

---

## Manager.java

```java
// Handles leave requests up to 7 days.
public class Manager extends Approver {

    @Override
    public void processLeaveRequest(LeaveRequest request) {

        if (request.getLeaveDays() <= 7) {

            System.out.println(
                    "Manager approved "
                            + request.getLeaveDays()
                            + " day(s) leave for "
                            + request.getEmployeeName());

        } else if (nextApprover != null) {

            nextApprover.processLeaveRequest(request);

        }

    }

}
```

---

## Director.java

```java
// Handles leave requests up to 14 days.
public class Director extends Approver {

    @Override
    public void processLeaveRequest(LeaveRequest request) {

        if (request.getLeaveDays() <= 14) {

            System.out.println(
                    "Director approved "
                            + request.getLeaveDays()
                            + " day(s) leave for "
                            + request.getEmployeeName());

        } else if (nextApprover != null) {

            nextApprover.processLeaveRequest(request);

        } else {

            System.out.println(
                    "Leave request rejected for "
                            + request.getEmployeeName());

        }

    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        // Create handlers.
        Approver supervisor = new Supervisor();
        Approver manager = new Manager();
        Approver director = new Director();

        // Build the chain.
        supervisor.setNextApprover(manager);
        manager.setNextApprover(director);

        // Process requests.
        supervisor.processLeaveRequest(
                new LeaveRequest("John", 2));

        supervisor.processLeaveRequest(
                new LeaveRequest("Alice", 6));

        supervisor.processLeaveRequest(
                new LeaveRequest("Bob", 10));

        supervisor.processLeaveRequest(
                new LeaveRequest("David", 20));

    }

}
```

---

## Output

```text
Supervisor approved 2 day(s) leave for John
Manager approved 6 day(s) leave for Alice
Director approved 10 day(s) leave for Bob
Leave request rejected for David
```

---

## How the Request Flows

```text
Employee submits request
           │
           ▼
     Supervisor
           │
      Can Handle?
      │        │
     Yes      No
      │        │
 Approve      ▼
         Manager
              │
         Can Handle?
         │        │
        Yes      No
         │        │
    Approve      ▼
            Director
                 │
            Can Handle?
            │        │
           Yes      No
            │        │
       Approve   Reject
```

The important thing to notice is that **the employee never decides who will approve the leave**.

The employee simply submits the request to the first handler.

Each handler decides one of two things:

1. **Can I handle this request?**
2. **If not, should I forward it to the next handler?**

This keeps the sender completely decoupled from all concrete approvers.

</details>

---

# Quick Revision

## Intent

> **Avoid coupling the sender of a request to its receiver by giving multiple objects a chance to handle the request. Pass the request along the chain until one handler processes it.**

---

## Memory Trick

> **Pass the request until someone can handle it.**

---

## Thumb Rule

```text
Request

    │

    ▼

Should multiple objects get a chance
to process this request?

          │
     ┌────┴────┐
     │         │
    No        Yes
     │         │
Keep simple   Think about
logic         Chain of Responsibility
```

---

## Thinking Process

```text
Need Leave Approval
        ↓
Write if-else
        ↓
Requirements Grow
        ↓
Observe the Request Flow
        ↓
Each Approver Handles One Responsibility
        ↓
Need Reference to Next Handler
        ↓
Create the Chain
        ↓
Need Common Abstraction
        ↓
Create Abstract Handler
        ↓
Chain of Responsibility Pattern
```

</details>
