import java.util.EnumMap;
import java.util.Map;


/*==========================================================
                        ENUMS
==========================================================*/

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

enum TransactionType {
    SHOW_BALANCE,
    CASH_WITHDRAW
}


/*==========================================================
                        ACCOUNT
==========================================================*/

class Account {

    private final String accountNo;
    private double balance;

    public Account(
            String accountNo,
            double balance) {

        this.accountNo = accountNo;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public boolean withdraw(double amount) {

        if (amount > balance) {
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

    public Card(
            String cardNumber,
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
}


/*==========================================================
                    CASH INVENTORY
==========================================================*/

class CashInventory {

    private final Map<CashType, Integer> inventory =
            new EnumMap<>(CashType.class);

    public void addCash(
            CashType cashType,
            int quantity) {

        inventory.put(
                cashType,
                inventory.getOrDefault(cashType, 0)
                        + quantity);
    }

    public int getCount(CashType cashType) {

        return inventory.getOrDefault(
                cashType,
                0);
    }

    public void removeCash(
            CashType cashType,
            int quantity) {

        int currentQuantity =
                inventory.getOrDefault(
                        cashType,
                        0);

        if (currentQuantity < quantity) {
            throw new RuntimeException(
                    "Not enough cash in ATM.");
        }

        inventory.put(
                cashType,
                currentQuantity - quantity);
    }
}


/*==========================================================
                    ATM STATE INTERFACE
==========================================================*/

interface ATMState {

    void insertCard(
            ATM atm,
            Card card);

    void enterPin(
            ATM atm,
            String pin);

    void selectTransaction(
            ATM atm,
            TransactionType type);

    void withdraw(
            ATM atm,
            int amount);

    void showBalance(
            ATM atm);

    void ejectCard(
            ATM atm);
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

        atm.setState(
                new HasCardState());

        System.out.println(
                "Card inserted.");
    }

    @Override
    public void enterPin(
            ATM atm,
            String pin) {

        System.out.println(
                "Please insert card first.");
    }

    @Override
    public void selectTransaction(
            ATM atm,
            TransactionType type) {

        System.out.println(
                "Please insert card first.");
    }

    @Override
    public void withdraw(
            ATM atm,
            int amount) {

        System.out.println(
                "Please insert card first.");
    }

    @Override
    public void showBalance(
            ATM atm) {

        System.out.println(
                "Please insert card first.");
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println(
                "No card inserted.");
    }
}


/*==========================================================
                    HAS CARD STATE
==========================================================*/

class HasCardState implements ATMState {

    @Override
    public void insertCard(
            ATM atm,
            Card card) {

        System.out.println(
                "Card already inserted.");
    }

    @Override
    public void enterPin(
            ATM atm,
            String pin) {

        Card card =
                atm.getCurrentCard();

        if (!card.verifyPin(pin)) {

            System.out.println(
                    "Invalid PIN.");

            atm.ejectCard();
            return;
        }

        System.out.println(
                "PIN verified.");

        atm.setState(
                new SelectTransactionState());
    }

    @Override
    public void selectTransaction(
            ATM atm,
            TransactionType type) {

        System.out.println(
                "Please enter PIN first.");
    }

    @Override
    public void withdraw(
            ATM atm,
            int amount) {

        System.out.println(
                "Please enter PIN first.");
    }

    @Override
    public void showBalance(
            ATM atm) {

        System.out.println(
                "Please enter PIN first.");
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println(
                "Card ejected.");

        atm.setCurrentCard(null);

        atm.setState(
                new IdleState());
    }
}


/*==========================================================
                SELECT TRANSACTION STATE
==========================================================*/

class SelectTransactionState implements ATMState {

    @Override
    public void insertCard(
            ATM atm,
            Card card) {

        System.out.println(
                "Card already inserted.");
    }

    @Override
    public void enterPin(
            ATM atm,
            String pin) {

        System.out.println(
                "PIN already verified.");
    }

    @Override
    public void selectTransaction(
            ATM atm,
            TransactionType type) {

        if (type == TransactionType.SHOW_BALANCE) {

            atm.setState(
                    new ShowBalanceState());

            atm.showBalance();

        } else if (
                type == TransactionType.CASH_WITHDRAW) {

            atm.setState(
                    new WithdrawState());

            System.out.println(
                    "Please enter withdrawal amount.");
        }
    }

    @Override
    public void withdraw(
            ATM atm,
            int amount) {

        System.out.println(
                "Please select withdrawal first.");
    }

    @Override
    public void showBalance(
            ATM atm) {

        System.out.println(
                "Please select balance inquiry first.");
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println(
                "Card ejected.");

        atm.setCurrentCard(null);

        atm.setState(
                new IdleState());
    }
}


/*==========================================================
                    SHOW BALANCE STATE
==========================================================*/

class ShowBalanceState implements ATMState {

    @Override
    public void insertCard(
            ATM atm,
            Card card) {

        System.out.println(
                "Transaction already in progress.");
    }

    @Override
    public void enterPin(
            ATM atm,
            String pin) {

        System.out.println(
                "PIN already verified.");
    }

    @Override
    public void selectTransaction(
            ATM atm,
            TransactionType type) {

        System.out.println(
                "Transaction already selected.");
    }

    @Override
    public void withdraw(
            ATM atm,
            int amount) {

        System.out.println(
                "Please complete current transaction.");
    }

    @Override
    public void showBalance(
            ATM atm) {

        double balance =
                atm.getCurrentCard()
                        .getAccount()
                        .getBalance();

        System.out.println(
                "Current Balance : ₹" + balance);

        atm.ejectCard();
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println(
                "Card ejected.");

        atm.setCurrentCard(null);

        atm.setState(
                new IdleState());
    }
}


/*==========================================================
                    WITHDRAW STATE
==========================================================*/

class WithdrawState implements ATMState {

    @Override
    public void insertCard(
            ATM atm,
            Card card) {

        System.out.println(
                "Transaction already in progress.");
    }

    @Override
    public void enterPin(
            ATM atm,
            String pin) {

        System.out.println(
                "PIN already verified.");
    }

    @Override
    public void selectTransaction(
            ATM atm,
            TransactionType type) {

        System.out.println(
                "Transaction already selected.");
    }

    @Override
    public void withdraw(
            ATM atm,
            int amount) {

        Account account =
                atm.getCurrentCard()
                        .getAccount();

        // 1. Check account balance
        if (account.getBalance() < amount) {

            System.out.println(
                    "Insufficient account balance.");

            atm.ejectCard();
            return;
        }

        // 2. Calculate which notes can be dispensed
        Map<CashType, Integer> cashToDispense =
                new EnumMap<>(CashType.class);

        CashDispenser dispenser =
                CashDispenserChainBuilder
                        .buildChain();

        boolean success =
                dispenser.dispense(
                        atm,
                        amount,
                        cashToDispense);

        // 3. ATM cannot produce requested amount
        if (!success) {

            System.out.println(
                    "ATM cannot dispense requested amount.");

            atm.ejectCard();
            return;
        }

        // 4. Remove cash from ATM inventory
        for (Map.Entry<CashType, Integer> entry :
                cashToDispense.entrySet()) {

            atm.getCashInventory()
                    .removeCash(
                            entry.getKey(),
                            entry.getValue());
        }

        // 5. Deduct amount from user's account
        account.withdraw(amount);

        // 6. Display cash to user
        System.out.println(
                "\nCash to dispense:");

        for (Map.Entry<CashType, Integer> entry :
                cashToDispense.entrySet()) {

            System.out.println(
                    entry.getKey()
                            + " -> "
                            + entry.getValue()
                            + " note(s)");
        }

        System.out.println(
                "Cash withdrawal successful.");

        // 7. Finish transaction
        atm.ejectCard();
    }

    @Override
    public void showBalance(
            ATM atm) {

        System.out.println(
                "Please complete withdrawal.");
    }

    @Override
    public void ejectCard(
            ATM atm) {

        System.out.println(
                "Card ejected.");

        atm.setCurrentCard(null);

        atm.setState(
                new IdleState());
    }
}


/*==========================================================
              CHAIN OF RESPONSIBILITY
==========================================================*/

abstract class CashDispenser {

    protected CashDispenser next;

    public void setNext(
            CashDispenser next) {

        this.next = next;
    }

    public boolean dispense(
            ATM atm,
            int amount,
            Map<CashType, Integer> cashToDispense) {

        int remainingAmount =
                processDenomination(
                        atm,
                        amount,
                        cashToDispense);

        // This handler + previous handlers
        // were able to satisfy the complete amount.
        if (remainingAmount == 0) {
            return true;
        }

        // No more handlers.
        if (next == null) {
            return false;
        }

        // Pass remaining amount to next denomination.
        return next.dispense(
                atm,
                remainingAmount,
                cashToDispense);
    }

    protected abstract int processDenomination(
            ATM atm,
            int amount,
            Map<CashType, Integer> cashToDispense);
}


/*==========================================================
                ₹2000 DISPENSER
==========================================================*/

class TwoThousandDispenser
        extends CashDispenser {

    @Override
    protected int processDenomination(
            ATM atm,
            int amount,
            Map<CashType, Integer> cashToDispense) {

        int denomination = 2000;

        int requiredQuantity =
                amount / denomination;

        int availableQuantity =
                atm.getCashInventory()
                        .getCount(
                                CashType.TWO_THOUSAND);

        int quantityToDispense =
                Math.min(
                        requiredQuantity,
                        availableQuantity);

        if (quantityToDispense > 0) {

            cashToDispense.put(
                    CashType.TWO_THOUSAND,
                    quantityToDispense);
        }

        return amount -
                quantityToDispense * denomination;
    }
}


/*==========================================================
                ₹500 DISPENSER
==========================================================*/

class FiveHundredDispenser
        extends CashDispenser {

    @Override
    protected int processDenomination(
            ATM atm,
            int amount,
            Map<CashType, Integer> cashToDispense) {

        int denomination = 500;

        int requiredQuantity =
                amount / denomination;

        int availableQuantity =
                atm.getCashInventory()
                        .getCount(
                                CashType.FIVE_HUNDRED);

        int quantityToDispense =
                Math.min(
                        requiredQuantity,
                        availableQuantity);

        if (quantityToDispense > 0) {

            cashToDispense.put(
                    CashType.FIVE_HUNDRED,
                    quantityToDispense);
        }

        return amount -
                quantityToDispense * denomination;
    }
}


/*==========================================================
                ₹200 DISPENSER
==========================================================*/

class TwoHundredDispenser
        extends CashDispenser {

    @Override
    protected int processDenomination(
            ATM atm,
            int amount,
            Map<CashType, Integer> cashToDispense) {

        int denomination = 200;

        int requiredQuantity =
                amount / denomination;

        int availableQuantity =
                atm.getCashInventory()
                        .getCount(
                                CashType.TWO_HUNDRED);

        int quantityToDispense =
                Math.min(
                        requiredQuantity,
                        availableQuantity);

        if (quantityToDispense > 0) {

            cashToDispense.put(
                    CashType.TWO_HUNDRED,
                    quantityToDispense);
        }

        return amount -
                quantityToDispense * denomination;
    }
}


/*==========================================================
                ₹100 DISPENSER
==========================================================*/

class OneHundredDispenser
        extends CashDispenser {

    @Override
    protected int processDenomination(
            ATM atm,
            int amount,
            Map<CashType, Integer> cashToDispense) {

        int denomination = 100;

        int requiredQuantity =
                amount / denomination;

        int availableQuantity =
                atm.getCashInventory()
                        .getCount(
                                CashType.ONE_HUNDRED);

        int quantityToDispense =
                Math.min(
                        requiredQuantity,
                        availableQuantity);

        if (quantityToDispense > 0) {

            cashToDispense.put(
                    CashType.ONE_HUNDRED,
                    quantityToDispense);
        }

        return amount -
                quantityToDispense * denomination;
    }
}


/*==========================================================
            CASH DISPENSER CHAIN BUILDER
==========================================================*/

class CashDispenserChainBuilder {

    public static CashDispenser buildChain() {

        CashDispenser twoThousand =
                new TwoThousandDispenser();

        CashDispenser fiveHundred =
                new FiveHundredDispenser();

        CashDispenser twoHundred =
                new TwoHundredDispenser();

        CashDispenser oneHundred =
                new OneHundredDispenser();

        twoThousand.setNext(
                fiveHundred);

        fiveHundred.setNext(
                twoHundred);

        twoHundred.setNext(
                oneHundred);

        return twoThousand;
    }
}


/*==========================================================
                            ATM
==========================================================*/

class ATM {

    private ATMState currentState;

    private Card currentCard;

    private final CashInventory cashInventory;

    public ATM() {

        cashInventory =
                new CashInventory();

        currentState =
                new IdleState();
    }

    public void insertCard(Card card) {

        currentState.insertCard(
                this,
                card);
    }

    public void enterPin(String pin) {

        currentState.enterPin(
                this,
                pin);
    }

    public void selectTransaction(
            TransactionType type) {

        currentState.selectTransaction(
                this,
                type);
    }

    public void withdrawCash(int amount) {

        currentState.withdraw(
                this,
                amount);
    }

    public void showBalance() {

        currentState.showBalance(
                this);
    }

    public void ejectCard() {

        currentState.ejectCard(
                this);
    }

    public void setState(
            ATMState state) {

        this.currentState = state;
    }

    public void setCurrentCard(
            Card card) {

        this.currentCard = card;
    }

    public Card getCurrentCard() {

        return currentCard;
    }

    public CashInventory getCashInventory() {

        return cashInventory;
    }
}


/*==========================================================
                          CLIENT
==========================================================*/

public class Main {

    public static void main(String[] args) {

        ATM atm = new ATM();

        // --------------------------------------------
        // Load cash into ATM
        // --------------------------------------------

        atm.getCashInventory().addCash(
                CashType.TWO_THOUSAND,
                5);

        atm.getCashInventory().addCash(
                CashType.FIVE_HUNDRED,
                10);

        atm.getCashInventory().addCash(
                CashType.TWO_HUNDRED,
                10);

        atm.getCashInventory().addCash(
                CashType.ONE_HUNDRED,
                20);


        // --------------------------------------------
        // Create Account and Card
        // --------------------------------------------

        Account account =
                new Account(
                        "ACC001",
                        10000);

        Card card =
                new Card(
                        "CARD001",
                        "1234",
                        account);


        // --------------------------------------------
        // Show Balance
        // --------------------------------------------

        System.out.println(
                "\n===== SHOW BALANCE =====");

        atm.insertCard(card);

        atm.enterPin("1234");

        atm.selectTransaction(
                TransactionType.SHOW_BALANCE);


        // --------------------------------------------
        // Withdraw ₹3700
        // --------------------------------------------

        System.out.println(
                "\n===== WITHDRAW ₹3700 =====");

        atm.insertCard(card);

        atm.enterPin("1234");

        atm.selectTransaction(
                TransactionType.CASH_WITHDRAW);

        atm.withdrawCash(3700);


        // --------------------------------------------
        // Show Balance Again
        // --------------------------------------------

        System.out.println(
                "\n===== FINAL BALANCE =====");

        atm.insertCard(card);

        atm.enterPin("1234");

        atm.selectTransaction(
                TransactionType.SHOW_BALANCE);
    }
}