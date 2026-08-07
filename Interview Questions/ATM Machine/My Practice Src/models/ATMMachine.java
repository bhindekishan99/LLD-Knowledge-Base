package models;

import java.util.Map;

import enums.CashType;
import enums.TransactionType;
import state.*;

public class ATMMachine {
    
    private static volatile ATMMachine ATM;
    private ATMState currentState;
    private Inventory inventory;

    private ATMMachine() {
        currentState = IdleState.getInstance();
        inventory = new Inventory();
    }

    public synchronized Map<CashType, Integer> withDrawCash(int amount){
        return inventory.withDrawMoney(amount);
    }

    public static ATMMachine getInstance() {
        if (ATM == null) {
            synchronized (ATMMachine.class) {
                if (ATM == null) {
                    ATM = new ATMMachine();
                }
            }
        }
        return ATM;
    }

    public boolean verifyPIN(String pin){
        //we take it as blckbox, it will fetch
        //account details and verify pin from DB
        //so we just keep it simple return true
        return pin.equals(pin);
    }
    
    public void displayBalance(){
        System.out.println("Show Balance");
    }

    public void setState(ATMState atmState){
        this.currentState = atmState;
    }

    public void insertCard(Card card){
        currentState.insertCard(this,card);
    }

    public void enterPin(String pin){
        currentState.enterPin(this, pin);
    }
    public void selectOperation(TransactionType transactionType){
        currentState.selectOperation(this, transactionType);
    }
    public void showBalance(){
        currentState.showBalance(this);
    }
    public void withDrawMoney(int amount){
        currentState.withDrawMoney(this, amount);
    }
    public void cancle(){
        currentState.cancle(this);
    }
}
