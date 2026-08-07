package state;

import models.*;

import java.util.Map;

import enums.*;

public class SelectTransactionState implements ATMState {

    private static SelectTransactionState selectTransactionState = new SelectTransactionState();

    public static SelectTransactionState getInstance(){
        return selectTransactionState;
    }

    private SelectTransactionState(){}

    @Override
    public void insertCard(ATMMachine ATM, Card card){
        System.out.println("Card is already inserted.");
    }

    @Override
    public void enterPin(ATMMachine ATM, String pin){
        System.out.println("Correct PIN is already enterd, please select transction to do.");
    }

    @Override
    public void selectOperation(ATMMachine ATM, TransactionType transactionType){
        if(transactionType == TransactionType.CASHWITHDRAW){
            ATM.setState(ShowBalanceState.getInstance());
        } else {
            // Map<CashType, Integer> depositedMoney = ATM.withDrawCash();
            ATM.setState(WithdrawCashState.getInstance());
        }
    }

    @Override
    public void showBalance(ATMMachine ATM){
        System.out.println("Please enter PIN first");
    }

    @Override
    public void withDrawMoney(ATMMachine ATM, int amount){
        System.out.println("Please enter PIN first");
    }

    @Override
    public void cancle(ATMMachine ATM){
        ATM.setState(SelectTransactionState.getInstance());
    }
    
}
