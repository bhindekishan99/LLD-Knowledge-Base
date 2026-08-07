package models;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import enums.*;

public class Inventory {
    private Map<CashType, Integer> cashMap = new ConcurrentHashMap<>();

    public synchronized void addCash(CashType cashType, int quantity){
        // Fixed: Atomic operation ensures no updates are lost between threads
        cashMap.merge(cashType, quantity, Integer::sum);
        //also same as : cashMap.merge(cashType, quantity, (oldValue, newValue) -> oldValue + newValue);
    }

    public int getTotalBalance(){
        int totalBalance = 0;
        for(Map.Entry<CashType,Integer> entry: cashMap.entrySet() ){
            totalBalance += entry.getKey().getValue() * entry.getValue();
        }
        return totalBalance;
    }

    public synchronized Map<CashType, Integer> withDrawMoney(int amount){
        
        if(amount > getTotalBalance()){
            return null;
        }

        Map<CashType, Integer> withDrawMoneyMap = new HashMap<>();

        List<CashType> list = Arrays.asList(CashType.values());

        list.sort(Collections.reverseOrder());

        for(CashType cashType : list){

            int requriedQuantity = amount/cashType.getValue();
            int availableQuantity = cashMap.getOrDefault(cashType,0);

            int tempAmount = 0;
            if(requriedQuantity > 0){
                if(availableQuantity > 0){
                    int takenQauntity = Math.min(requriedQuantity,availableQuantity);
                    tempAmount = takenQauntity*cashType.getValue();
                    withDrawMoneyMap.put(cashType,takenQauntity);
                    amount -= tempAmount;
                }
            }
        }
        //if still some amout remains, E.G RS 7 can not be withdrawd
        if(amount > 0){
            return null;
        }
        for(Map.Entry<CashType,Integer> entry : withDrawMoneyMap.entrySet()){
            CashType cashType = entry.getKey();
            int quantity = entry.getValue();

            cashMap.put(cashType, cashMap.get(cashType) - quantity);

        }
        return withDrawMoneyMap;
    }
}
