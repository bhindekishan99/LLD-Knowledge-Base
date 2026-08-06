package state;

import enums.Coin;
import model.Item;
import vendingmachine.VendingMachine;

public class IdelState implements VendingMachineState {
    
    private static final IdelState idelState = new IdelState();
    
    public static IdelState getInstance(){
        return idelState;
    }

    @Override
    public void insertCoin(VendingMachine vendingMachine, Coin coin){
        vendingMachine.addBalance(coin);
        vendingMachine.setState(HasCoinState.getInstance());
    }

    @Override
    public void selecteItem(VendingMachine vendingMachine, Item item){
        System.out.println("Please insert coins first.");
    }

    @Override
    public void despenseItem(VendingMachine vendingMachine){
         System.out.println("Please insert coins and select an item.");
    }

    @Override
    public void cancleOrder(VendingMachine vendingMachine){
        System.out.println("Nothing to cancel.");
    }
}
