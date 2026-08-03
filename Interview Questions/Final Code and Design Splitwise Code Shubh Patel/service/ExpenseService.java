package service;

import enums.SplitType;
import models.*;
import repository.GroupRepository;
import strategy.SplitStrategy;
import strategy.SplitStrategyFactory;

import java.util.List;
import java.util.Map;

public class ExpenseService {

    private final GroupRepository repository;
    private final BalanceSheetService balanceSheetService;

    public ExpenseService(GroupRepository repository,
                          BalanceSheetService balanceSheetService) {

        this.repository = repository;
        this.balanceSheetService = balanceSheetService;
    }

    public void addExpense(
            String groupId,
            String description,
            double amount,
            User paidBy,
            List<User> participants,
            SplitType splitType,
            Map<String, Object> metadata) {

        Group group = repository.findById(groupId);

        SplitStrategy strategy =
                SplitStrategyFactory.getStrategy(splitType);

        List<Split> splits =
                strategy.split(amount, participants, metadata);

        Expense expense =
                new Expense(
                        description,
                        amount,
                        paidBy,
                        splits,
                        splitType);

        group.addExpense(expense);

        balanceSheetService.updateBalanceSheet(group, expense);
    }
}