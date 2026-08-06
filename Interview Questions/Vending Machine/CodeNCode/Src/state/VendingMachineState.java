package state;

import vendingmachine.VendingMachine;
import enums.Coin;
import model.Item;

public interface VendingMachineState{
    void insertCoin(VendingMachine vendingMachine, Coin coin);
    void selecteItem(VendingMachine vendingMachine, Item item);
    void despenseItem(VendingMachine vendingMachine);
    void cancleOrder(VendingMachine vendingMachine);

}
