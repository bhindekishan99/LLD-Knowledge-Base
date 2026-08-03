package service;

import enums.SplitType;
import exception.GroupNotFoundException;
import models.Group;
import models.User;
import repository.GroupRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GroupService {

    private final GroupRepository repository;
    private final ExpenseService expenseService;
    private final DebtSimplificationService simplificationService;

    public GroupService(GroupRepository repository,
                        ExpenseService expenseService,
                        DebtSimplificationService simplificationService) {

        this.repository = repository;
        this.expenseService = expenseService;
        this.simplificationService = simplificationService;
    }

    public String createGroup(String name,
                              List<User> members) {

        String id = UUID.randomUUID().toString();

        Group group = new Group(id, name, members);

        repository.save(group);

        return id;
    }

    public void addMember(String groupId,
                          User user) {

        Group group = repository.findById(groupId);

        if (group == null)
            throw new GroupNotFoundException(groupId);

        group.addMember(user);
    }

    public void addExpense(
            String groupId,
            String description,
            double amount,
            User paidBy,
            List<User> participants,
            SplitType splitType,
            Map<String, Object> metadata) {

        expenseService.addExpense(
                groupId,
                description,
                amount,
                paidBy,
                participants,
                splitType,
                metadata
        );
    }

    public void simplifyDebt(String groupId) {

        simplificationService.simplify(groupId);
    }
}