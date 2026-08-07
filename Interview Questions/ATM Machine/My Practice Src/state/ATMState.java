package state;

import models.*;
import enums.*;

public interface ATMState {
    public void insertCard(ATMMachine ATM, Card card);
    public void enterPin(ATMMachine ATM, String pin);
    public void selectOperation(ATMMachine ATM, TransactionType transactionType);
    public void showBalance(ATMMachine ATM);
    public void withDrawMoney(ATMMachine ATM, int amount);
    public void cancle(ATMMachine ATM);
}
