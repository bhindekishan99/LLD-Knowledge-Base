package state;

import enums.Coin;
import model.Item;
import vendingmachine.VendingMachine;

public class ItemSelectedState implements VendingMachineState {
    
    private static ItemSelectedState itemSelectedState = new ItemSelectedState();

    public static ItemSelectedState getInstance(){
        return itemSelectedState;
    }

    @Override
    public void insertCoin(VendingMachine vendingMachine, Coin coin){
        System.out.println("Not allowed as you already selected item");
    }

    @Override
    public void selecteItem(VendingMachine vendingMachine, Item item){
        System.out.println("Not allowed as you already selected item");
    }

    @Override
    public void despenseItem(VendingMachine vendingMachine){
        vendingMachine.deductItem(vendingMachine.getSelectedItem());
        System.out.println("Item got despensed");
        int change = vendingMachine.getBalance() - vendingMachine.getSelectedItem().getPrice();
        if(change > 0){
            System.out.println("Please take a change = "+change);
        }
        vendingMachine.setState(IdelState.getInstance());
    }

    @Override
    public void cancleOrder(VendingMachine vendingMachine){
        int refund = vendingMachine.getBalance();
        vendingMachine.clearBalance();
        vendingMachine.clearSelectedItem();
        vendingMachine.setState(IdelState.getInstance());
        System.out.println("Please take your refund "+refund);
    }
}
