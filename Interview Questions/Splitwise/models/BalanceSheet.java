package models;

import java.util.HashMap;
import java.util.Map;

public class BalanceSheet {

    private double totalPaid;
    private double totalExpense;

    /*
        +ve -> I will receive money from this user
        -ve -> I have to pay this user
     */
    private final Map<User, Double> balances = new HashMap<>();

    public void addTotalPaid(double amount) {
        totalPaid += amount;
    }

    public void addTotalExpense(double amount) {
        totalExpense += amount;
    }

    public void addBalance(User user, double amount) {

        double newBalance = balances.getOrDefault(user, 0.0) + amount;

        if (Math.abs(newBalance) < 0.0001) {
            balances.remove(user);
        } else {
            balances.put(user, newBalance);
        }
    }

    public void clearBalances() {
        balances.clear();
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public Map<User, Double> getBalances() {
        return balances;
    }
}