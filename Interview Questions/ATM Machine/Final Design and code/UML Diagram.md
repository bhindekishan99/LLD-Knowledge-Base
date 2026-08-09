
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
     ----------------------------------------------------------
     |            |                |                 |
     |            |                |                 |
+-------------+ +-------------+ +----------------+ +----------------+ +----------------+
| IdleState   | |HasCardState | |SelectTxnState  | |ShowBalanceState| | WithdrawState  |
+-------------+ +-------------+ +----------------+ +----------------+ +----------------+
                                              |
                                              |
                                              | starts
                                              ▼
                                  +----------------------+
                                  |   CashDispenser      |
                                  |      <<abstract>>    |
                                  +----------------------+
                                  | - next               |
                                  +----------------------+
                                  | +setNext()           |
                                  | +dispense()          |
                                  +----------------------+
                                              |
                         ---------------------+---------------------
                         |                    |                    |
                         ▼                    ▼                    ▼
                +----------------+   +----------------+   +----------------+
                |TwoThousand     |   | FiveHundred    |   | TwoHundred     |
                |Dispenser       |   | Dispenser      |   | Dispenser      |
                +----------------+   +----------------+   +----------------+
                         |                    |                    |
                         |                    |                    |
                         +--------------------+--------------------+
                                              |
                                              ▼
                                  +----------------------+
                                  | OneHundredDispenser  |
                                  +----------------------+


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

```text
WithdrawState
      |
      | dispenseCash(amount)
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

Each dispenser:

1. Checks whether its denomination can be used.
2. Gets the available quantity from `CashInventory`.
3. Dispenses the maximum possible number of notes.
4. Passes the remaining amount to `next`.

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
Done
```

---

# State Transition

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
       |ShowBalanceState|     | WithdrawState  |
       +----------------+     +----------------+
                                      |
                                dispenseCash()
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

# Relationship Summary

| Class                      | Responsibility                                           |
| -------------------------- | -------------------------------------------------------- |
| `ATM`                    | Maintains current state, current card and cash inventory |
| `ATMState`               | Defines state-specific ATM operations                    |
| `IdleState`              | Handles card insertion                                   |
| `HasCardState`           | Handles PIN verification                                 |
| `SelectTransactionState` | Handles transaction selection                            |
| `ShowBalanceState`       | Displays account balance                                 |
| `WithdrawState`          | Handles withdrawal request                               |
| `Card`                   | Represents card and associated account                   |
| `Account`                | Maintains balance and withdrawal operation               |
| `CashInventory`          | Maintains available cash using`Map<CashType, Integer>` |
| `CashDispenser`          | Base handler for cash denomination                       |
| `TwoThousandDispenser`   | Handles ₹2000 notes                                     |
| `FiveHundredDispenser`   | Handles ₹500 notes                                      |
| `TwoHundredDispenser`    | Handles ₹200 notes                                      |
| `OneHundredDispenser`    | Handles ₹100 notes                                      |

## Design Patterns

### State Pattern

Used for:

```text
IdleState
HasCardState
SelectTransactionState
ShowBalanceState
WithdrawState
```

### Chain of Responsibility

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

The **only change from the previous design is that `CashWithdrawProcessor` is replaced by the `CashDispenser` chain**. This preserves the design we already derived while making cash dispensing a natural use of Chain of Responsibility.
