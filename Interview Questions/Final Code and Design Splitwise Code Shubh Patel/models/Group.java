package models;

import java.util.*;

public class Group {

    private final String id;
    private final String name;

    private final List<User> members = new ArrayList<>();
    private final List<Expense> expenses = new ArrayList<>();

    /*
        Each user has his own balance sheet
     */
    private final Map<User, BalanceSheet> balanceSheets = new HashMap<>();

    public Group(String id, String name, List<User> members) {

        this.id = id;
        this.name = name;

        for (User user : members) {
            addMember(user);
        }
    }

    public void addMember(User user) {

        members.add(user);
        balanceSheets.put(user, new BalanceSheet());
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public BalanceSheet getBalanceSheet(User user) {
        return balanceSheets.get(user);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return members;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public Map<User, BalanceSheet> getBalanceSheets() {
        return balanceSheets;
    }
}