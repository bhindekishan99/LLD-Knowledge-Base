import java.util.*;

/*==========================================================
                        ENUMS
==========================================================*/

enum TransactionType {
    CASH_WITHDRAW,
    SHOW_BALANCE
}

enum CashType {

    TWO_THOUSAND(2000),
    FIVE_HUNDRED(500),
    TWO_HUNDRED(200),
    ONE_HUNDRED(100);

    private final int value;

    CashType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

/*==========================================================
                        ACCOUNT
==========================================================*/

class Account {

    private final String accountNumber;
    private double balance;

    public Account(String accountNumber,
                   double balance) {

        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public boolean withdraw(double amount) {

        if (balance < amount) {
            return false;
        }

        balance -= amount;
        return true;
    }
}

/*==========================================================
                          CARD
==========================================================*/

class Card {

    private final String cardNumber;
    private final String pin;
    private final Account account;

    public Card(String cardNumber,
                String pin,
                Account account) {

        this.cardNumber = cardNumber;
        this.pin = pin;
        this.account = account;
    }

    public boolean verifyPin(String enteredPin) {
        return pin.equals(enteredPin);
    }

    public Account getAccount() {
        return account;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}

/*==========================================================
                    CASH INVENTORY
==========================================================*/

class CashInventory {

    private final Map<CashType, Integer> inventory =
            new EnumMap<>(CashType.class);
    /*
       HashMap → random order ❌
       TreeMap → sorted order ✅
       EnumMap → enum declaration order ✅
    */

    public void addCash(CashType type,
                        int count) {

        inventory.put(
                type,
                inventory.getOrDefault(type, 0) + count);
    }

    public int getCount(CashType type) {
        return inventory.getOrDefault(type, 0);
    }

    public Map<CashType, Integer> getInventory() {
        return inventory;
    }

    public void removeCash(CashType type,
                           int count) {

        inventory.put(
                type,
                inventory.get(type) - count);
    }
}

/*==========================================================
                        ATM
==========================================================*/

class ATM {

    private ATMState currentState;

    private Card currentCard;

    private final CashInventory inventory =
            new CashInventory();

    public ATM() {
        currentState = new IdleState();
    }

    public ATMState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ATMState state) {
        this.currentState = state;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card card) {
        this.currentCard = card;
    }

    public CashInventory getInventory() {
        return inventory;
    }

    /* Delegate to current state */

    public void insertCard(Card card) {
        currentState.insertCard(this, card);
    }

    public void enterPin(String pin) {
        currentState.enterPin(this, pin);
    }

    public void selectTransaction(
            TransactionType type) {

        currentState.selectTransaction(
                this,
                type);
    }

    public void withdrawCash(int amount) {
        currentState.withdrawCash(this, amount);
    }

    public void showBalance() {
        currentState.showBalance(this);
    }

    public void ejectCard() {
        currentState.ejectCard(this);
    }
}

/*==========================================================
                    STATE INTERFACE
==========================================================*/

interface ATMState {

    default void insertCard(
            ATM atm,
            Card card) {

        System.out.println("Operation not allowed.");
    }

    default void enterPin(
            ATM atm,
            String pin) {

        System.out.println("Operation not allowed.");
    }

    default void selectTransaction(
            ATM atm,
            TransactionType transactionType) {

        System.out.println("Operation not allowed.");
    }

    default void withdrawCash(
            ATM atm,
            int amount) {

        System.out.println("Operation not allowed.");
    }

    default void showBalance(
            ATM atm) {

        System.out.println("Operation not allowed.");
    }

    default void ejectCard(
            ATM atm) {

        System.out.println("Operation not allowed.");
    }
}

/*==========================================================
                    IDLE STATE
==========================================================*/

class IdleState implements ATMState {

    @Override
    public void insertCard(
            ATM atm,
            Card card) {

        atm.setCurrentCard(card);
        atm.setCurrentState(new HasCardState());

        System.out.println("Card inserted.");
    }
}

/*==========================================================
                    HAS CARD STATE
==========================================================*/

class HasCardState implements ATMState {

    @Override
    public void enterPin(
            ATM atm,
            String pin) {

        if (!atm.getCurrentCard().verifyPin(pin)) {

            System.out.println("Invalid PIN");

            atm.ejectCard();
            return;
        }

        System.out.println("PIN verified.");

        atm.setCurrentState(
                new SelectTransactionState());
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println("Card ejected.");

        atm.setCurrentCard(null);
        atm.setCurrentState(new IdleState());
    }
}

/*==========================================================
            SELECT TRANSACTION STATE
==========================================================*/

class SelectTransactionState implements ATMState {

    @Override
    public void selectTransaction(
            ATM atm,
            TransactionType transactionType) {

        switch (transactionType) {

            case CASH_WITHDRAW:

                atm.setCurrentState(
                        new WithdrawState());

                System.out.println(
                        "Please enter withdrawal amount.");

                break;

            case SHOW_BALANCE:

                atm.setCurrentState(
                        new ShowBalanceState());

                atm.showBalance();

                break;
        }
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println("Card ejected.");

        atm.setCurrentCard(null);
        atm.setCurrentState(new IdleState());
    }
}

/*==========================================================
                SHOW BALANCE STATE
==========================================================*/

class ShowBalanceState implements ATMState {

    @Override
    public void showBalance(
            ATM atm) {

        System.out.println(
                "Available Balance : "
                        + atm.getCurrentCard()
                             .getAccount()
                             .getBalance());

        atm.ejectCard();
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println("Card ejected.");

        atm.setCurrentCard(null);
        atm.setCurrentState(new IdleState());
    }
}

/*==========================================================
                WITHDRAW STATE
==========================================================*/

class WithdrawState implements ATMState {

    @Override
    public void withdrawCash(
            ATM atm,
            int amount) {

        Account account =
                atm.getCurrentCard()
                        .getAccount();

        if (account.getBalance() < amount) {

            System.out.println(
                    "Insufficient Account Balance");

            atm.ejectCard();
            return;
        }

        boolean success =
                CashWithdrawProcessor.withdraw(
                        atm.getInventory(),
                        amount);

        if (!success) {

            System.out.println(
                    "ATM doesn't have sufficient cash.");

            atm.ejectCard();
            return;
        }

        account.withdraw(amount);

        System.out.println(
                "Please collect your cash.");

        atm.ejectCard();
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println("Card ejected.");

        atm.setCurrentCard(null);
        atm.setCurrentState(new IdleState());
    }
}

/*==========================================================
                CASH WITHDRAW PROCESSOR
==========================================================*/

class CashWithdrawProcessor {

    /**
     * Greedy Algorithm
     *
     * Returns true if cash can be dispensed.
     */
    public static boolean withdraw(
            CashInventory inventory,
            int amount) {

        Map<CashType, Integer> cashToDispense =
                new LinkedHashMap<>();

        int remainingAmount = amount;

        // First calculate whether ATM can dispense
        for (CashType cashType : CashType.values()) {

            int noteValue = cashType.getValue();

            int availableNotes =
                    inventory.getCount(cashType);

            int requiredNotes =
                    remainingAmount / noteValue;

            int notesToUse =
                    Math.min(requiredNotes, availableNotes);

            if (notesToUse > 0) {

                cashToDispense.put(
                        cashType,
                        notesToUse);

                remainingAmount -=
                        notesToUse * noteValue;
            }
        }

        if (remainingAmount != 0) {
            return false;
        }

        // Remove cash from inventory
        for (Map.Entry<CashType, Integer> entry :
                cashToDispense.entrySet()) {

            inventory.removeCash(
                    entry.getKey(),
                    entry.getValue());
        }

        // Display dispensed cash
        System.out.println("\nDispensed Cash:");

        for (Map.Entry<CashType, Integer> entry :
                cashToDispense.entrySet()) {

            System.out.println(
                    entry.getKey()
                            + " -> "
                            + entry.getValue());
        }

        return true;
    }
}

/*==========================================================
                        CLIENT
==========================================================*/

public class Main {

    public static void main(String[] args) {

        // Create ATM
        ATM atm = new ATM();

        // Load cash into ATM
        atm.getInventory().addCash(
                CashType.TWO_THOUSAND,
                10);

        atm.getInventory().addCash(
                CashType.FIVE_HUNDRED,
                20);

        atm.getInventory().addCash(
                CashType.TWO_HUNDRED,
                30);

        atm.getInventory().addCash(
                CashType.ONE_HUNDRED,
                50);

        // Create Account
        Account account =
                new Account(
                        "ACC101",
                        15000);

        // Create Card
        Card card =
                new Card(
                        "123456789",
                        "1234",
                        account);

        // Flow

        atm.insertCard(card);

        atm.enterPin("1234");

        atm.selectTransaction(
                TransactionType.CASH_WITHDRAW);

        atm.withdrawCash(3700);

        /*
        // Balance Flow

        atm.insertCard(card);

        atm.enterPin("1234");

        atm.selectTransaction(
                TransactionType.SHOW_BALANCE);
        */
    }
}