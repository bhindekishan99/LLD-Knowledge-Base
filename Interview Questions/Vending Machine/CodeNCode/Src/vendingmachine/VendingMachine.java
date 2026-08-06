package vendingmachine;

import enums.Coin;
import model.Inventory;
import model.Item;
import state.IdelState;
import state.VendingMachineState;

public class VendingMachine {

    VendingMachine vendingMachine = null;
    Inventory inventory = new Inventory();
    int balance = 0;
    Item selectedItem;
    VendingMachineState currenState = new IdelState();
    private VendingMachine(){}

    public VendingMachine creatVendingMachine(){
        if(vendingMachine == null){
            vendingMachine = new VendingMachine();
        }
        return vendingMachine;
    }

    public Inventory getInventory(){
        return inventory;
    }
    public void setState(VendingMachineState vendingMachineState){
        this.currenState = vendingMachineState;
    }
    public void insertCoin(Coin coin){
        currenState.insertCoin(this, coin);
    }
    public void addBalance(Coin coin){
        balance += coin.getValue();
    }
    public int getBalance(){
        return balance;
    }
    public void setSelectedItem(Item selectedItem){
        this.selectedItem = selectedItem;
    }
    public Item getSelectedItem(){
        return this.selectedItem;
    }
    public boolean canSelectItem(Item item){
        return this.balance >= item.getPrice();
    }
    public void clearBalance(){
        balance = 0;
    }
    public void clearSelectedItem(){
        selectedItem = null;
    }
    public void deductItem(Item item){
        inventory.deductItem(item);
    }
    public boolean isItemAvailable(Item item){
        return this.getInventory().isItemAvailable(item);
    }
    
    
}
