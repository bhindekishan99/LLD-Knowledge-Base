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
        User kishan = new User("1", "Kishan");
        User alice = new User("2", "Alice");
        User bob = new User("3", "Bob");

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

        // =========================================================
        // GROUP EXPENSE
        // =========================================================

        String goaTripGroupId =
                groupService.createGroup(
                        "Goa Trip",
                        List.of(kishan, alice, bob));

        // Kishan paid ₹900
        groupService.addExpense(
                goaTripGroupId,
                "Dinner",
                900,
                kishan,
                List.of(kishan, alice, bob),
                SplitType.EQUAL,
                new HashMap<>());

        // Bob paid ₹600
        groupService.addExpense(
                goaTripGroupId,
                "Taxi",
                600,
                bob,
                List.of(kishan, alice, bob),
                SplitType.EQUAL,
                new HashMap<>());

        // =========================================================
        // NON-GROUP : Kishan & Alice
        // =========================================================

        String kishanAliceGroupId =
                groupService.createGroup(
                        "Non-Group : Kishan & Alice",
                        List.of(kishan, alice));

        groupService.addExpense(
                kishanAliceGroupId,
                "Dinner",
                600,
                kishan,
                List.of(kishan, alice),
                SplitType.EQUAL,
                new HashMap<>());

        // =========================================================
        // NON-GROUP : Kishan & Bob
        // =========================================================

        String kishanBobGroupId =
                groupService.createGroup(
                        "Non-Group : Kishan & Bob",
                        List.of(kishan, bob));

        groupService.addExpense(
                kishanBobGroupId,
                "Movie",
                400,
                bob,
                List.of(kishan, bob),
                SplitType.EQUAL,
                new HashMap<>());

        // =========================================================
        // BEFORE SIMPLIFICATION
        // =========================================================

        System.out.println("\n==============================");
        System.out.println("GOA TRIP");
        System.out.println("==============================");
        printBalanceSheet(repository.findById(goaTripGroupId));

        System.out.println("\n==============================");
        System.out.println("NON GROUP : KISHAN & ALICE");
        System.out.println("==============================");
        printBalanceSheet(repository.findById(kishanAliceGroupId));

        System.out.println("\n==============================");
        System.out.println("NON GROUP : KISHAN & BOB");
        System.out.println("==============================");
        printBalanceSheet(repository.findById(kishanBobGroupId));

        // =========================================================
        // SIMPLIFY GOA TRIP ONLY
        // =========================================================

        groupService.simplifyDebt(goaTripGroupId);

        // =========================================================
        // AFTER SIMPLIFICATION
        // =========================================================

        System.out.println("\n==============================");
        System.out.println("GOA TRIP AFTER SIMPLIFICATION");
        System.out.println("==============================");
        printBalanceSheet(repository.findById(goaTripGroupId));
    }

    private static void printBalanceSheet(Group group) {

        for (User user : group.getMembers()) {

            System.out.println("--------------------------------");
            System.out.println(user.getName());

            System.out.println("Paid    : "
                    + group.getBalanceSheet(user).getTotalPaid());

            System.out.println("Expense : "
                    + group.getBalanceSheet(user).getTotalExpense());

            System.out.println("Balances:");

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
                                    + " :Rs."
                                    + amount);

                } else {

                    System.out.println(
                            user.getName()
                                    + " owes "
                                    + other.getName()
                                    + " : Rs."
                                    + (-amount));
                }
            }

            System.out.println();
        }
    }
}
