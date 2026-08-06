package model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Inventory {
    private final Map<Item, Integer> stock = new ConcurrentHashMap<>();

    public synchronized void addItem(Item item){
        stock.put(item, stock.getOrDefault(item, 0)+1);
    }

    public boolean isItemAvailable(Item item){
        return stock.getOrDefault(item, 0) > 0;
    }

    public synchronized void deductItem(Item item){
        if (!isItemAvailable(item)){
             throw new IllegalArgumentException("Item is out of stock: " + item.getName());
        }
        stock.put(item, stock.get(item)-1);
    }

    public void displayItems(){
        stock.forEach((item,quantity) -> System.out.println("  " + item + "  |  Qty: " + quantity));
    }
}
