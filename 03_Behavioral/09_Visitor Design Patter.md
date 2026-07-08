# Visitor Design Pattern

> **Category:** Behavioral Design Pattern

---

# Sources

## 🎥 YouTube

https://www.youtube.com/watch?v=MvwkgjHe5Ic

## 🌐 Blog

https://codewitharyan.com/tech-blogs/visitor-design-pattern

---

# How to Think Towards the Visitor Pattern

Forget the Visitor Pattern for a moment.

Imagine you're building a **Hospital Management System**.

The hospital treats different types of patients.

- Child Patient
- Adult Patient
- Senior Patient

Initially, the hospital only supports two operations.

- Diagnosis
- Billing

As a developer, what would you naturally write?

Probably something like this.

```java
ChildPatient child = new ChildPatient();

child.diagnosis();
child.billing();
```

Similarly,

```java
AdultPatient adult = new AdultPatient();

adult.diagnosis();
adult.billing();
```

and

```java
SeniorPatient senior = new SeniorPatient();

senior.diagnosis();
senior.billing();
```

This is exactly how most developers—including senior engineers—would start.

At this point, there is absolutely nothing wrong with this implementation.

It is simple, easy to understand, and solves today's problem.

---

## Requirements Start Growing

As the hospital management system evolves, the Product Owner introduces new requirements.

Every patient now needs additional operations.

- Diagnosis
- Billing
- Health Report
- Insurance Processing
- Prescription Generation
- Diet Recommendation
- Risk Assessment

Naturally, every patient class starts growing.

```java
class ChildPatient {

    diagnosis();

    billing();

    generateHealthReport();

    processInsurance();

    generatePrescription();

    recommendDiet();

    calculateRisk();

}
```

Exactly the same thing happens for

- AdultPatient
- SeniorPatient

Every time a **new operation** is introduced,

every existing patient class must also be modified.

---

## Stop and Observe

Notice something interesting.

What is actually changing?

Is the patient changing?

**No.**

The patients remain exactly the same.

```text
Child Patient

Adult Patient

Senior Patient
```

What keeps changing are the **operations**.

Today

- Diagnosis
- Billing

Tomorrow

- Health Report
- Insurance

Later

- Tax Calculation
- Medical Analytics
- AI Recommendation

The **objects remain stable**.

The **operations keep increasing**.

This is the first clue.

---

## Another Observation

Ask yourself one simple question.

> **Who should own these operations?**

Should every patient know how to

- Diagnose?
- Generate Bills?
- Generate Reports?
- Calculate Insurance?
- Predict Risk?

Probably not.

A patient should only represent a patient.

It shouldn't contain every future operation the hospital may introduce.

---

## First Refactoring

Instead of placing every operation inside every patient,

what if we move one operation into one class?

For example,

```text
Diagnosis

↓

DiagnosisService
```

Similarly,

```text
Billing

↓

BillingService
```

Now the patient classes become smaller.

But another question appears.

---

## Another Problem Appears

How will the DiagnosisService know

whether it received

- ChildPatient
- AdultPatient
- SeniorPatient

Will we write

```java
if (patient instanceof ChildPatient) {

}
else if (patient instanceof AdultPatient) {

}
else if (patient instanceof SeniorPatient) {

}
```

We're back to the same problem again.

Only the location of the if-else has changed.

---

## Think Again

Instead of asking

> **Which patient am I visiting?**

what if we let

the patient itself

tell the visitor

who it is?

Imagine this.

```text
Doctor visits Patient

        │

        ▼

Patient says

"I am a Child Patient."

        │

        ▼

Doctor performs Child Diagnosis
```

Similarly,

```text
Doctor visits Patient

        │

        ▼

Patient says

"I am a Senior Patient."

        │

        ▼

Doctor performs Senior Diagnosis
```

Now,

the doctor never performs

`instanceof` checks.

The patient decides

which operation should execute.

---

## Final Refactoring

Every patient simply accepts a visitor.

```java
patient.accept(visitor);
```

The visitor now performs the correct operation.

```text
Patient

      │

accept(visitor)

      │

      ▼

Visitor

      │

visit(ChildPatient)

visit(AdultPatient)

visit(SeniorPatient)
```

Now adding

```text
HealthReportVisitor
```

requires

**zero changes**

to

- ChildPatient
- AdultPatient
- SeniorPatient

The patient hierarchy remains completely stable.

Only a new visitor is added.

---

## Complete Thinking

```text
Need Hospital System

        ↓

Keep Operations Inside Patients

        ↓

Requirements Grow

        ↓

Operations Keep Increasing

        ↓

Patient Classes Become Large

        ↓

Move Operations Outside

        ↓

Need Operation For Different Patient Types

        ↓

Avoid instanceof

        ↓

Let Patients Accept Visitors

        ↓

Create Visitor Interface

        ↓

Visitor Pattern
```

---

# How to Remember the Visitor Pattern

Imagine a hospital.

Patients never perform

- Diagnosis
- Billing
- Health Report

Instead,

specialists visit them.

```text
Child Patient

        ▲

        │

Doctor Visits

        │

Adult Patient

        ▲

        │

Doctor Visits

        │

Senior Patient
```

Tomorrow,

the hospital hires

another specialist.

```text
Health Report Specialist
```

Does any patient change?

No.

Only a new specialist starts visiting them.

Exactly the same idea applies to the Visitor Pattern.

The objects remain unchanged.

New operations arrive as new visitors.

## Memory Trick

Remember this one sentence.

> **Objects stay the same. Visitors bring new operations.**

---

# Traditional Solution

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Patient.java

```java
public interface Patient {

    void diagnosis();

    void billing();

}
```

---

## ChildPatient.java

```java
// Represents a child patient.
public class ChildPatient implements Patient {

    @Override
    public void diagnosis() {
        System.out.println("Diagnosing a child patient.");
    }

    @Override
    public void billing() {
        System.out.println("Calculating billing for a child patient.");
    }

}
```

---

## AdultPatient.java

```java
// Represents an adult patient.
public class AdultPatient implements Patient {

    @Override
    public void diagnosis() {
        System.out.println("Diagnosing an adult patient.");
    }

    @Override
    public void billing() {
        System.out.println("Calculating billing for an adult patient.");
    }

}
```

---

## SeniorPatient.java

```java
// Represents a senior patient.
public class SeniorPatient implements Patient {

    @Override
    public void diagnosis() {
        System.out.println("Diagnosing a senior patient.");
    }

    @Override
    public void billing() {
        System.out.println("Calculating billing for a senior patient.");
    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        Patient[] patients = {
                new ChildPatient(),
                new AdultPatient(),
                new SeniorPatient()
        };

        for (Patient patient : patients) {

            patient.diagnosis();
            patient.billing();

            System.out.println();

        }

    }

}
```

---

## Output

```text
Diagnosing a child patient.
Calculating billing for a child patient.

Diagnosing an adult patient.
Calculating billing for an adult patient.

Diagnosing a senior patient.
Calculating billing for a senior patient.
```

---

## Problems With This Approach

Although this implementation works correctly, it has several design problems.

### 1. Every Patient Class Keeps Growing

Initially, each patient only supports two operations.

```text
ChildPatient

Diagnosis()

Billing()
```

Later, the Product Owner asks for:

- Health Report
- Insurance Processing
- Prescription Generation
- Diet Recommendation
- Risk Assessment

Now every patient class becomes

```text
ChildPatient

Diagnosis()

Billing()

HealthReport()

Insurance()

Prescription()

DietRecommendation()

RiskAssessment()

...
```

Exactly the same changes must also be made in:

- AdultPatient
- SeniorPatient

As the application grows, every patient class becomes larger.

---

### 2. Violates the Open/Closed Principle (OCP)

Suppose tomorrow the hospital introduces a new operation.

```text
Generate Health Report
```

Now every existing patient class must be modified.

```java
public class ChildPatient {

    diagnosis();

    billing();

    generateHealthReport();

}
```

The same modification is required in:

- AdultPatient
- SeniorPatient

Instead of extending the application,

we're modifying already tested classes.

This violates the **Open/Closed Principle**.

---

### 3. Different Responsibilities Live Together

Ask yourself,

> **What is the responsibility of a patient?**

A patient should simply represent a patient.

Instead, it now contains logic for:

- Diagnosis
- Billing
- Insurance
- Reporting
- Prescription
- Analytics

These are different responsibilities.

As new hospital features arrive,

patient classes become responsible for more and more unrelated operations.

---

### 4. Hard to Maintain

Imagine the hospital now supports:

- 15 Patient Types
- 20 Operations

Adding just one new operation requires modifying

all 15 patient classes.

Maintenance effort grows with every new feature.

---

### 5. Difficult to Extend

Suppose management introduces

```text
AI Diagnosis
```

Should we modify:

- ChildPatient
- AdultPatient
- SeniorPatient

again?

Yes.

Every new operation forces changes across the entire patient hierarchy.

The object hierarchy is stable.

Only the operations are changing.

This is exactly where the **Visitor Pattern** provides a much cleaner solution.

</details>

---

# Visitor Pattern Implementation

<details>
<summary><strong>Click to view complete implementation</strong></summary>

## Patient.java

```java
// Represents an element that can accept different visitors.
public interface Patient {

    void accept(Visitor visitor);

}
```

<details>
<summary>💡 Why do we need an interface?</summary>

A common question is:

> **Why not directly create `ChildPatient`, `AdultPatient`, and `SeniorPatient` classes without introducing the `Patient` interface?**

Initially, that approach works perfectly.

```java
ChildPatient

AdultPatient

SeniorPatient
```

Now imagine the client wants to process all patients.

Without a common abstraction, the client would look like this.

```java
ChildPatient child = new ChildPatient();
AdultPatient adult = new AdultPatient();
SeniorPatient senior = new SeniorPatient();
```

Every place that works with patients now depends on concrete classes.

---

Instead, create one common abstraction.

```java
public interface Patient {

    void accept(Visitor visitor);

}
```

Now the client communicates only with

```java
Patient
```

instead of

```java
ChildPatient

AdultPatient

SeniorPatient
```

The client becomes independent of concrete patient types.

Tomorrow if a new patient type is introduced,

```text
VIPPatient
```

the existing client code doesn't change.

It simply works with

```java
Patient
```

This follows the **Dependency Inversion Principle (DIP)**.

> **High-level modules should depend on abstractions, not concrete implementations.**

</details>

---

## Visitor.java

```java
// Defines operations for every patient type.
public interface Visitor {

    void visit(ChildPatient childPatient);

    void visit(AdultPatient adultPatient);

    void visit(SeniorPatient seniorPatient);

}
```

<details>
<summary>💡 Why do we need another interface?</summary>

Notice something interesting.

We are not creating

```java
DiagnosisVisitor
```

directly.

Instead, we first define

```java
Visitor
```

Why?

Because every new operation should follow the same contract.

Today we have

```text
DiagnosisVisitor
```

Tomorrow we may introduce

```text
BillingVisitor

HealthReportVisitor

InsuranceVisitor

PrescriptionVisitor
```

Every visitor should know how to process every patient type.

The common contract guarantees this.

```java
visit(ChildPatient)

visit(AdultPatient)

visit(SeniorPatient)
```

Now every visitor behaves consistently.

The patient simply accepts a visitor without caring which operation it performs.

</details>

---

## ChildPatient.java

```java
// Represents a child patient.
public class ChildPatient implements Patient {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
```

---

## AdultPatient.java

```java
// Represents an adult patient.
public class AdultPatient implements Patient {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
```

---

## SeniorPatient.java

```java
// Represents a senior patient.
public class SeniorPatient implements Patient {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
```

---

## DiagnosisVisitor.java

```java
// Performs diagnosis for different patient types.
public class DiagnosisVisitor implements Visitor {

    @Override
    public void visit(ChildPatient childPatient) {
        System.out.println(
                "Diagnosing a child patient: Pediatric examination.");
    }

    @Override
    public void visit(AdultPatient adultPatient) {
        System.out.println(
                "Diagnosing an adult patient: Routine health check.");
    }

    @Override
    public void visit(SeniorPatient seniorPatient) {
        System.out.println(
                "Diagnosing a senior patient: Geriatric evaluation.");
    }

}
```

---

## BillingVisitor.java

```java
// Calculates billing for different patient types.
public class BillingVisitor implements Visitor {

    @Override
    public void visit(ChildPatient childPatient) {
        System.out.println(
                "Calculating billing for a child patient.");
    }

    @Override
    public void visit(AdultPatient adultPatient) {
        System.out.println(
                "Calculating billing for an adult patient.");
    }

    @Override
    public void visit(SeniorPatient seniorPatient) {
        System.out.println(
                "Calculating billing for a senior patient.");
    }

}
```

---

## Client Code

```java
public class Main {

    public static void main(String[] args) {

        Patient[] patients = {
                new ChildPatient(),
                new AdultPatient(),
                new SeniorPatient()
        };

        Visitor diagnosisVisitor = new DiagnosisVisitor();
        Visitor billingVisitor = new BillingVisitor();

        for (Patient patient : patients) {

            patient.accept(diagnosisVisitor);
            patient.accept(billingVisitor);

            System.out.println();

        }

    }

}
```

---

## Output

```text
Diagnosing a child patient: Pediatric examination.
Calculating billing for a child patient.

Diagnosing an adult patient: Routine health check.
Calculating billing for an adult patient.

Diagnosing a senior patient: Geriatric evaluation.
Calculating billing for a senior patient.
```

---

## Execution Flow

```text
Client

    │

Creates Visitor

    │

Creates Patient

    │

patient.accept(visitor)

    │

Patient delegates

    │

visitor.visit(this)

    │

Correct visit() method executes

    │

Operation completed
```

Notice something important.

The client never writes

```java
if (patient instanceof ChildPatient)
```

or

```java
if (patient instanceof AdultPatient)
```

The patient itself decides which `visit()` method should execute.

This technique is known as **Double Dispatch**, one of the core ideas behind the Visitor Pattern.

</details>
