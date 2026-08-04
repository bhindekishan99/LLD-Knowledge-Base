# Kishan's LLD Design Process

1. Read requirements.

2. Extract nouns → Entities.

3. Add basic attributes.

4. Pick one happy flow.

5. For every step in the flow:
   - What data changes?
   - Who owns this data?
   - Can the owner perform this work?
     - Yes → keep it there.
     - No → introduce a Service.
   - Does the behavior vary?
     - Yes → introduce a Pattern.

6. Complete one happy flow.

7. Start coding in dependency order.

# Example of Splitwise

# My LLD Design Thinking Process

## Step 1: Read Requirements

Don't think about classes or design patterns.

Understand the business problem first.

### Example (Splitwise)

```
1. Group and non-group expense
2. User can add expense
3. User can see how much to pay/take to/from whom
4. Simplify debts
```

---

## Step 2: Extract Nouns (Entities)

Every noun is a potential class.

Don't worry about whether it is perfect.

### Example

```
User
Group
Expense
Split
BalanceSheet
SplitType
```

---

## Step 3: Add Basic Attributes

Don't spend much time here.

Only write what you know from requirements.

You can always add more later.

### Example

```
User
- id
- name

Expense
- description
- amount
- paidBy
- splits
- splitType

Split
- participant
- amount

Group
- members
- expenses
- balanceSheets

BalanceSheet
- totalPaid
- totalExpense
- balances
```

---

# Step 4: Pick One Happy Flow

Instead of thinking about the whole system, choose one business flow.

For Splitwise,

```
User adds an expense.
```

Now derive the design from this flow.

---

## Step 5: Ask Questions During the Flow

### Step 5.1

User provides

```
description
amount
paidBy
participants
splitType
```

Question:

How do I create an Expense?

Observation:

Expense requires

```
List<Split>
```

---

### Step 5.2

Question:

Who creates List<Split>?

Requirement says

```
Equal Split

Percentage Split
```

Behavior varies.

Introduce

```
SplitStrategy
```

```
SplitStrategy
      ▲
      │
 ┌────┴─────┐
 │          │
Equal   Percentage
```

Now Expense can be created.

---

### Step 5.3

Question:

Where should this Expense be stored?

Answer:

```
Group
```

Group owns Expenses.

So

```
Group.addExpense(expense)
```

naturally appears.

---

### Step 5.4

Question:

After adding Expense,

what changes?

Requirement says

```
User should know

Pay

Receive
```

Need

```
BalanceSheet
```

---

### Step 5.5

Question:

Who should update BalanceSheet?

Possible answer

```
Group.updateBalanceSheet()
```

Question again:

Should Group perform this work?

No.

Updating balances is business logic.

Introduce

```
BalanceSheetService
```

```
BalanceSheetService

update(group, expense)
```

---

### Step 5.6

Question:

How do we simplify debts?

Possible answer

```
Group.simplify()
```

Question again:

Should Group perform this algorithm?

No.

Simplification is a separate responsibility.

Introduce

```
DebtSimplificationService
```

---

### Step 5.7

Question:

How do I find a Group?

Need

```
GroupRepository
```

---

### Step 5.8

Question:

Who orchestrates the complete flow?

Instead of

```
Client

↓

ExpenseService

↓

BalanceSheetService

↓

Repository

↓

DebtSimplificationService
```

Introduce

```
GroupService
```

which coordinates everything.

---

# Final Happy Flow

```
Client

↓

GroupService

↓

ExpenseService

↓

SplitStrategy

↓

Expense

↓

Group.addExpense()

↓

BalanceSheetService

↓

Updated BalanceSheets

↓

DebtSimplificationService (Optional)
```

---

# General Thinking Framework

For every step in the happy flow, ask:

### 1. What data changes?

This usually gives an Entity.

Example

```
Expense
BalanceSheet
```

---

### 2. Who owns this data?

Example

```
Expense

↓

Group
```

So Group stores Expenses.

---

### 3. Can the owner perform this work?

If YES

Keep it there.

If NO

Introduce a Service.

Example

```
Group

can update balances

BUT

should not.

↓

BalanceSheetService
```

---

### 4. Does the behavior vary?

If YES

Think about Design Patterns.

Example

```
Split Calculation

Equal

Percentage

↓

Strategy Pattern
```

---

# Final Design

```
Client
    │
    ▼
GroupService
    │
    ├──────────────┐
    ▼              ▼
ExpenseService   DebtSimplificationService
    │
    ▼
SplitStrategy
    │
    ▼
Expense
    │
    ▼
Group
    │
    ▼
BalanceSheetService
    │
    ▼
BalanceSheet
```

---

# My LLD Checklist

- ✅ Read requirements
- ✅ Extract nouns (Entities)
- ✅ Add basic attributes
- ✅ Pick one happy flow
- ✅ For every step ask:
    - What data changes?
    - Who owns the data?
    - Can the owner perform this work?
    - If not, introduce a Service.
    - Does behavior vary?
        - If yes, introduce a Design Pattern.
- ✅ Complete one happy flow.
- ✅ Only after the design is complete, start coding.
