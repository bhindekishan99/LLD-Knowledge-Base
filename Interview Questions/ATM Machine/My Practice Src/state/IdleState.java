package state;

import java.util.Map;

import enums.CashType;
import enums.TransactionType;
import models.*;


public class IdleState implements ATMState{

    private static IdleState idleState = new IdleState();

    public static IdleState getInstance(){
        return idleState;
    }

    private IdleState(){}

    @Override
    public void insertCard(ATMMachine ATM, Card card){
        ATM.setState(HasCardState.getInstance());
    }

    @Override
    public void enterPin(ATMMachine ATM, String pin){
        System.out.println("Please enter card first");
    }

    @Override
    public void selectOperation(ATMMachine ATM, TransactionType transactionType){
        System.out.println("Please enter card first");
    }

    @Override
    public void showBalance(ATMMachine ATM){
        System.out.println("Please enter card first");
    }

    @Override
    public void withDrawMoney(ATMMachine ATM, int amount){
        System.out.println("Please enter card first");
    }

    @Override
    public void cancle(ATMMachine ATM){
        //stays in same state.
    }
}
