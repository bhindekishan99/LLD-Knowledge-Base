package state;

import enums.Coin;
import model.Item;
import vendingmachine.VendingMachine;

public class HasCoinState implements VendingMachineState {

    private final static HasCoinState hasCoinState = new HasCoinState();
    
    public static HasCoinState getInstance(){
        return hasCoinState;
    }

    @Override
    public void insertCoin(VendingMachine vendingMachine, Coin coin){
        vendingMachine.addBalance(coin);
    }

    @Override
    public void selecteItem(VendingMachine vendingMachine, Item item){
        if(!vendingMachine.canSelectItem(item)){
            System.out.println("No sufficient balance");
            return;
        } 
        if(!vendingMachine.isItemAvailable(item)){
            System.out.println("Item is out of stock");
            return;
        }
        vendingMachine.setSelectedItem(item);
        vendingMachine.setState(ItemSelectedState.getInstance());
    }

    @Override
    public void despenseItem(VendingMachine vendingMachine){
        System.out.println("Please select an item first.");
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
