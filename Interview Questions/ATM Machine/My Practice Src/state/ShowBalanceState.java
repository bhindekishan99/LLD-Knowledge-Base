package state;

import models.*;

import java.util.Map;

import enums.*;

public class ShowBalanceState implements ATMState {

    private static ShowBalanceState showBalanceState = new ShowBalanceState();

    public static ShowBalanceState getInstance(){
        return showBalanceState;
    }

    private ShowBalanceState(){}

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
        System.out.println("You already choosed to show balance");
    }

    @Override
    public void showBalance(ATMMachine ATM){
        //here we can implement strategy pattern based on user preference
        //like display balance of screen or print on paper
        System.out.println("Please enter PIN first");
    }

    @Override
    public void withDrawMoney(ATMMachine ATM, int amount){
        System.out.println("You choosed to display balance");
    }

    @Override
    public void cancle(ATMMachine ATM){
        ATM.setState(ShowBalanceState.getInstance());
    }
    
}
