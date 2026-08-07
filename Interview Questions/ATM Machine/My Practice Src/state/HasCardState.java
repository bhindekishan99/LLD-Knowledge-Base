package state;

import enums.TransactionType;
import models.*;

public class HasCardState implements ATMState {

    private static HasCardState hasCardState = new HasCardState();

    public static HasCardState getInstance(){
        return hasCardState;
    }

    private HasCardState(){}

    @Override
    public void insertCard(ATMMachine ATM, Card card){
        System.out.println("Card is already inserted.");
    }

    @Override
    public void enterPin(ATMMachine ATM, String pin){
    
        boolean isPinVerified = ATM.verifyPIN(pin);
        if(isPinVerified){
            ATM.setState(SelectTransactionState.getInstance());
        }else{
            System.out.println("Wrong pin enterd");
        }
    }

    @Override
    public void selectOperation(ATMMachine ATM, TransactionType transactionType){
        System.out.println("Please enter PIN first");
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
        ATM.setState(IdleState.getInstance());
    }
    
}
