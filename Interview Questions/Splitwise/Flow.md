# Splitwise LLD (Shubh Patel Approach)

source: https://youtu.be/2QvlBrhLLHc?si=o6mzmGwH7LPKME_i

> **Goal:** Understand the overall design and flow before diving into the code.

---

# 1. Requirements

## Functional Requirements

- Create a Group
- Add Members to a Group
- Add Expense
    - Group Expense
    - One-to-One Expense
- View Balance Sheet
- Simplify Debts
- Support multiple split types
    - Equal
    - Percentage
    - Unequal (Future)

---

# 2. High Level Flow

```text
                Client
                   │
                   ▼
             GroupService
          /       |        \
         /        |         \
        ▼         ▼          ▼
GroupRepository ExpenseService DebtSimplificationService
                    │
                    ▼
          SplitStrategyFactory
                    │
        ┌───────────┴────────────┐
        │                        │
        ▼                        ▼
 EqualSplitStrategy      PercentageSplitStrategy
        │                        │
        └───────────┬────────────┘
                    ▼
              List<Split>
                    │
                    ▼
               Expense Object
                    │
                    ▼
          Add Expense to Group
                    │
                    ▼
         BalanceSheetService
                    │
                    ▼
     Update every user's BalanceSheet
```

---

# 3. Models

## User

Represents a user.

```text
User
├── id
└── name
```

---

## Split

Represents one participant's share.

```text
Split
├── participant
└── amount
```

Example

```
Dinner ₹900

Kishan -> ₹300
Alice  -> ₹300
Bob    -> ₹300
```

becomes

```
[
Split(Kishan,300)
Split(Alice,300)
Split(Bob,300)
]
```

---

## Expense

Represents one expense.

```text
Expense
├── description
├── amount
├── paidBy
├── splitType
└── List<Split>
```

Notice:

Instead of

```
List<User>
```

Shubh stores

```
List<Split>
```

because Split stores both

- participant
- share

---

## BalanceSheet

Each user has one BalanceSheet **per group**.

```text
BalanceSheet
├── totalPaid
├── totalExpense
└── balances
      └── Map<User, Amount>
```

Meaning

```
+amount
I will receive money

-amount
I have to pay money
```

Example

```
Kishan

Paid = 900
Expense = 300

Alice -> +300
Bob   -> +300
```

---

## Group

```text
Group
├── id
├── name
├── members
├── expenses
└── balanceSheets
```

Each Group contains

- Members
- Expenses
- One BalanceSheet for every member

---

# 4. Repository Layer

Repository hides storage.

```text
GroupRepository

findById()

save()

delete()
```

Current implementation

```
InMemoryGroupRepository
```

stores

```
Map<GroupId, Group>
```

Future

```
SQLGroupRepository
MongoGroupRepository
```

can be added without changing services.

---

# 5. Service Layer

## GroupService

Entry point of the application.

Responsibilities

- Create Group
- Add Member
- Add Expense
- Simplify Debt

Delegates work to other services.

---

## ExpenseService

Responsible for

- Creating Expense
- Calling Split Strategy
- Updating BalanceSheet

Flow

```
Input
        ↓
Strategy
        ↓
List<Split>
        ↓
Expense
        ↓
Update BalanceSheet
```

---

## BalanceSheetService

Responsible for updating balances.

Updates

- totalPaid
- totalExpense
- balances

---

## DebtSimplificationService

Responsible for minimizing transactions.

Uses

Greedy Algorithm

Flow

```
Calculate Net Balance

↓

Separate

Receivers (+)

Senders (-)

↓

Priority Queues

↓

Match Highest Receiver
with Highest Sender

↓

Update Balance Sheets
```

---

# 6. Strategy Pattern

Different bill splitting algorithms.

```text
            SplitStrategy
                 ▲
                 │
      ┌──────────┴──────────┐
      │                     │
      ▼                     ▼
 EqualSplit          PercentageSplit
```

Each strategy returns

```
List<Split>
```

---

# 7. Factory Pattern

Creates Strategy object.

```text
SplitType

↓

Factory

↓

Correct Strategy
```

Example

```
EQUAL

↓

EqualSplitStrategy
```

---

# 8. Overall Expense Flow

```text
Client

↓

GroupService

↓

ExpenseService

↓

SplitStrategyFactory

↓

SplitStrategy

↓

List<Split>

↓

Expense

↓

Group.addExpense()

↓

BalanceSheetService

↓

Updated BalanceSheets
```

---

# 9. One-to-One Expense

Shubh treats one-to-one expense as a **default group**.

```
Non-Group Expenses
```

Therefore,

both

```
Group Expense
```

and

```
One-to-One Expense
```

follow exactly the same architecture.

---

# 10. Design Patterns Used

| Pattern | Used In | Why |
|----------|---------|-----|
| Strategy | SplitStrategy | Different split algorithms |
| Factory | SplitStrategyFactory | Runtime creation of strategy |
| Repository | GroupRepository | Hide storage implementation |
| Dependency Inversion | GroupRepository interface | Easily switch DB implementation |
| Service Layer | GroupService, ExpenseService, BalanceSheetService | Separate business logic |

---

# 11. Final Architecture

```text
                     Client
                        │
                        ▼
                  GroupService
             ┌─────────┼─────────┐
             │         │         │
             ▼         ▼         ▼
      Repository  ExpenseService DebtSimplificationService
                       │
                       ▼
             SplitStrategyFactory
                       │
             ┌─────────┴──────────┐
             ▼                    ▼
       EqualStrategy      PercentageStrategy
                       │
                       ▼
                 List<Split>
                       │
                       ▼
                    Expense
                       │
                       ▼
              BalanceSheetService
                       │
                       ▼
              User BalanceSheets
```

---

# Key Learning

- GroupService is the **entry point**.
- ExpenseService creates the Expense.
- Strategy calculates participant shares.
- Factory creates Strategy.
- BalanceSheetService updates balances.
- DebtSimplificationService minimizes transactions.
- Repository hides storage implementation.
- One-to-one expenses are treated as a **default group**.
