# ATM Class Diagram

```text
                              +--------------------+
                              |        ATM         |
                              +--------------------+
                              | - currentState     |
                              | - currentCard      |
                              | - cashInventory    |
                              +--------------------+
                              | +insertCard()      |
                              | +enterPin()        |
                              | +selectTransaction()|
                              | +withdrawCash()    |
                              | +showBalance()     |
                              | +ejectCard()       |
                              +--------------------+
                                      |
             -------------------------------------------------
             |                       |                       |
             | has                   | has                   | has
             ▼                       ▼                       ▼

      +-------------+        +----------------+      +----------------+
      | ATMState    |        |     Card       |      | CashInventory  |
      +-------------+        +----------------+      +----------------+
      |<<interface>>|        | - cardNumber   |      | - inventory    |
      +-------------+        | - pin          |      +----------------+
      | +insertCard()|       | - account      |      | +addCash()     |
      | +enterPin() |        +----------------+      | +removeCash()  |
      | +selectTxn()|        | +verifyPin()   |      | +getCount()    |
      | +withdraw() |        | +getAccount()  |      +----------------+
      | +showBalance()|
      | +ejectCard() |
      +-------------+
             ^
             |
     ---------------------------------------------------------------------------
     |             |                |                 |                 |
     |             |                |                 |                 |
     ▼             ▼                ▼                 ▼                 ▼
+-------------+ +-------------+ +----------------+ +----------------+ +-------------+
| IdleState   | |HasCardState | |SelectTxnState  | |ShowBalanceState| |WithdrawState|
+-------------+ +-------------+ +----------------+ +----------------+ +-------------+


                 Card
                  |
                  | has
                  ▼

            +----------------+
            |    Account     |
            +----------------+
            | - accountNo    |
            | - balance      |
            +----------------+
            | +withdraw()    |
            | +getBalance()  |
            +----------------+


CashInventory
      |
      | stores
      ▼

Map<CashType, Integer>

CashType
---------------------
TWO_THOUSAND
FIVE_HUNDRED
TWO_HUNDRED
ONE_HUNDRED
```

---

# Chain of Responsibility

`WithdrawState` starts the cash-dispenser chain.

```text
WithdrawState
      |
      | dispense(amount, cashToDispense)
      ▼
+------------------------+
| TwoThousandDispenser   |
+------------------------+
      |
      | remaining amount
      ▼
+------------------------+
| FiveHundredDispenser   |
+------------------------+
      |
      | remaining amount
      ▼
+------------------------+
| TwoHundredDispenser    |
+------------------------+
      |
      | remaining amount
      ▼
+------------------------+
| OneHundredDispenser    |
+------------------------+
      |
      ▼
     null
```

## CashDispenser

```text
                    +-------------------------+
                    |     CashDispenser        |
                    |       <<abstract>>       |
                    +-------------------------+
                    | - next                  |
                    +-------------------------+
                    | +setNext()              |
                    | +dispense()             |
                    | #processDenomination()  |
                    +-------------------------+
                               |
             -----------------------------------------
             |                  |                    |
             ▼                  ▼                    ▼
+--------------------+ +--------------------+ +--------------------+
|TwoThousandDispenser| |FiveHundredDispenser| |TwoHundredDispenser |
+--------------------+ +--------------------+ +--------------------+
                                                   |
                                                   ▼
                                      +----------------------+
                                      |OneHundredDispenser   |
                                      +----------------------+
```

Each dispenser:

1. Checks the available quantity of its denomination from `CashInventory`.
2. Calculates how many notes of that denomination can be used.
3. Adds that quantity to `cashToDispense`.
4. Returns the remaining amount.
5. Passes the remaining amount to `next`.

The chain **does not modify `CashInventory`** while calculating the withdrawal.

---

# Withdrawal Processing

```text
WithdrawState
      |
      | amount = ₹3700
      ▼
CashDispenser Chain
      |
      | calculate
      ▼
cashToDispense
      |
      +-----------------------+
      |                       |
   SUCCESS                 FAILURE
      |                       |
      ▼                       ▼
Remove cash             Do nothing
from ATM                to inventory
      |
      ▼
Deduct amount
from Account
      |
      ▼
Display cash
      |
      ▼
Eject card
```

---

# Example

For a withdrawal of ₹3,700:

```text
₹3700
  |
  ▼
TwoThousandDispenser
  → 1 × ₹2000
  → remaining = ₹1700
  |
  ▼
FiveHundredDispenser
  → 3 × ₹500
  → remaining = ₹200
  |
  ▼
TwoHundredDispenser
  → 1 × ₹200
  → remaining = ₹0
  |
  ▼
Success
```

The resulting map is:

```text
cashToDispense

TWO_THOUSAND → 1
FIVE_HUNDRED → 3
TWO_HUNDRED  → 1
```

Only after the chain returns `true` do we remove these quantities from `CashInventory`.

---

# State Transition

The following represents **runtime behavior**, not class inheritance:

```text
                    +-------------+
                    |  IdleState  |
                    +-------------+
                           |
                     insertCard()
                           |
                           ▼
                    +--------------+
                    | HasCardState |
                    +--------------+
                           |
                       enterPin()
                           |
                           ▼
              +------------------------+
              | SelectTransactionState |
              +------------------------+
                    /              \
                   /                \
          SHOW_BALANCE         CASH_WITHDRAW
                 |                    |
                 ▼                    ▼
       +----------------+     +----------------+
       |ShowBalanceState|     | WithdrawState |
       +----------------+     +----------------+
                                      |
                                withdrawCash()
                                      |
                                      ▼
                            CashDispenser Chain
                                      |
                                      ▼
                                 ejectCard()
                                      |
                                      ▼
                              +-------------+
                              |  IdleState  |
                              +-------------+
```

---

# Relationship Summary

| Class | Responsibility |
|---|---|
| `ATM` | Maintains current state, current card and cash inventory |
| `ATMState` | Defines state-specific ATM operations |
| `IdleState` | Handles card insertion |
| `HasCardState` | Handles PIN verification |
| `SelectTransactionState` | Handles transaction selection |
| `ShowBalanceState` | Displays account balance |
| `WithdrawState` | Handles withdrawal request and starts the dispenser chain |
| `Card` | Represents card and associated account |
| `Account` | Maintains balance and withdrawal operation |
| `CashInventory` | Maintains available cash using `Map<CashType, Integer>` |
| `CashDispenser` | Abstract Chain of Responsibility handler |
| `TwoThousandDispenser` | Processes ₹2000 denomination |
| `FiveHundredDispenser` | Processes ₹500 denomination |
| `TwoHundredDispenser` | Processes ₹200 denomination |
| `OneHundredDispenser` | Processes ₹100 denomination |

---

# Design Patterns

## State Pattern

Used for:

```text
ATMState
   |
   +-- IdleState
   +-- HasCardState
   +-- SelectTransactionState
   +-- ShowBalanceState
   +-- WithdrawState
```

The ATM delegates operations to its current state.

## Chain of Responsibility

Used for:

```text
CashDispenser
      ↓
₹2000
      ↓
₹500
      ↓
₹200
      ↓
₹100
```

Each handler processes one denomination and passes the remaining amount to the next handler.

The chain **calculates the cash combination first**. The ATM inventory is modified only after the complete withdrawal has been successfully calculated.
