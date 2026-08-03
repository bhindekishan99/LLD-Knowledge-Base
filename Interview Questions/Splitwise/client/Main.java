package client;

import enums.SplitType;
import models.Group;
import models.User;
import repository.GroupRepository;
import repository.InMemoryGroupRepository;
import service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        // -----------------------------
        // Users
        // -----------------------------
        User shub = new User("1", "Shub");
        User bob = new User("2", "Bob");
        User tom = new User("3", "Tom");

        // -----------------------------
        // Repository
        // -----------------------------
        GroupRepository repository =
                new InMemoryGroupRepository();

        // -----------------------------
        // Services
        // -----------------------------
        BalanceSheetService balanceSheetService =
                new BalanceSheetService();

        ExpenseService expenseService =
                new ExpenseService(
                        repository,
                        balanceSheetService);

        DebtSimplificationService simplificationService =
                new DebtSimplificationService(repository);

        GroupService groupService =
                new GroupService(
                        repository,
                        expenseService,
                        simplificationService);

        // -----------------------------
        // Create Group
        // -----------------------------
        String groupId =
                groupService.createGroup(
                        "Goa Trip",
                        List.of(shub, bob, tom));

        // -----------------------------
        // Expense 1
        // Lunch Day 1
        // -----------------------------
        groupService.addExpense(
                groupId,
                "Lunch Day 1",
                100,
                shub,
                List.of(shub, bob),
                SplitType.EQUAL,
                new HashMap<>());

        // -----------------------------
        // Expense 2
        // Lunch Day 2
        // -----------------------------
        groupService.addExpense(
                groupId,
                "Lunch Day 2",
                100,
                bob,
                List.of(bob, tom),
                SplitType.EQUAL,
                new HashMap<>());

        // -----------------------------
        // Before Simplification
        // -----------------------------
        System.out.println("\n===== BEFORE SIMPLIFICATION =====\n");

        printBalanceSheet(repository.findById(groupId));

        // -----------------------------
        // Simplify
        // -----------------------------
        groupService.simplifyDebt(groupId);

        // -----------------------------
        // After Simplification
        // -----------------------------
        System.out.println("\n===== AFTER SIMPLIFICATION =====\n");

        printBalanceSheet(repository.findById(groupId));
    }

    private static void printBalanceSheet(Group group) {

        for (User user : group.getMembers()) {

            System.out.println("--------------------------------");

            System.out.println(user.getName());

            System.out.println("Paid : "
                    + group.getBalanceSheet(user).getTotalPaid());

            System.out.println("Expense : "
                    + group.getBalanceSheet(user).getTotalExpense());

            System.out.println("Balances :");

            for (Map.Entry<User, Double> entry :
                    group.getBalanceSheet(user)
                            .getBalances()
                            .entrySet()) {

                User other = entry.getKey();
                double amount = entry.getValue();

                if (amount > 0) {

                    System.out.println(
                            other.getName()
                                    + " owes "
                                    + user.getName()
                                    + " "
                                    + amount);

                } else {

                    System.out.println(
                            user.getName()
                                    + " owes "
                                    + other.getName()
                                    + " "
                                    + (-amount));
                }
            }

            System.out.println();
        }
    }
}