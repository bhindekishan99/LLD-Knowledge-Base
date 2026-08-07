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
+-------------+ +-------------+ +----------------+ +----------------+
| IdleState   | |HasCardState | |SelectTxnState  | |ShowBalanceState|
+-------------+ +-------------+ +----------------+ +----------------+
                                              |
                                              |
                                              ▼
                                    +----------------+
                                    | WithdrawState  |
                                    +----------------+
                                              |
                                              |
                                              ▼
                                  +---------------------------+
                                  | CashWithdrawProcessor     |
                                  +---------------------------+
                                  | +withdraw()               |
                                  +---------------------------+
                                              |
                                              |
                                              ▼
                                     +----------------+
                                     | CashInventory  |
                                     +----------------+

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

# Relationship Summary

| Class | Relationship |
|--------|--------------|
| ATM | Has one `ATMState` |
| ATM | Has one inserted `Card` (`currentCard`) |
| ATM | Has one `CashInventory` |
| Card | Has one `Account` |
| WithdrawState | Uses `CashWithdrawProcessor` |
| CashWithdrawProcessor | Uses `CashInventory` |
| CashInventory | Stores `Map<CashType, Integer>` |
| ATMState | Implemented by all concrete states |

---

# State Transition Diagram

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
                 \                /
                  \              /
                   \            /
                    ▼          ▼
                 ejectCard()
                       |
                       ▼
                 +-------------+
                 |  IdleState  |
                 +-------------+
```