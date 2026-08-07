package state;

import models.*;

import java.util.Map;

import enums.*;

public class WithdrawCashState implements ATMState {

    private static WithdrawCashState withdrawCashState = new WithdrawCashState();

    public static WithdrawCashState getInstance(){
        return withdrawCashState;
    }

    private WithdrawCashState(){}

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
        System.out.println("You choosed to display balance");
    }

    @Override
    public void withDrawMoney(ATMMachine ATM, int amount){
        //will ask user to enter amount, but for simplycity we are not taking from user here
        Map<CashType, Integer> cash = ATM.withDrawCash(amount);
        if(cash == null){
            System.out.println("No sufficient money or "+amount+" RS can not be withdrawed");
        }else{
            System.out.println("Withdrawed amount = "+cash);
        }
    }

    @Override
    public void cancle(ATMMachine ATM){
        ATM.setState(WithdrawCashState.getInstance());
    }
    
}
