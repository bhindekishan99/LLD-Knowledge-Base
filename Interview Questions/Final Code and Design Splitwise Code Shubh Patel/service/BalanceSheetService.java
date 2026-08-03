package service;

import models.*;

public class BalanceSheetService {

    public void updateBalanceSheet(Group group, Expense expense) {

        User paidBy = expense.getPaidBy();

        BalanceSheet payerBalanceSheet =
                group.getBalanceSheet(paidBy);

        // Update total paid
        payerBalanceSheet.addTotalPaid(expense.getAmount());

        for (Split split : expense.getSplits()) {

            User participant = split.getParticipant();
            double share = split.getAmount();

            // Update participant's total expense
            BalanceSheet participantBalanceSheet =
                    group.getBalanceSheet(participant);

            participantBalanceSheet.addTotalExpense(share);

            if (participant.equals(paidBy))
                continue;

            /*
                Payer should receive money
             */
            payerBalanceSheet.addBalance(participant, share);

            /*
                Participant has to pay payer
             */
            participantBalanceSheet.addBalance(paidBy, -share);
        }
    }
}