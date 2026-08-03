package service;

import models.BalanceSheet;
import models.Group;
import models.User;
import repository.GroupRepository;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

public class DebtSimplificationService {

    private final GroupRepository repository;

    public DebtSimplificationService(GroupRepository repository) {
        this.repository = repository;
    }

    public void simplify(String groupId) {

        Group group = repository.findById(groupId);

        /*
            Sender (Net Loss)
            More negative comes first.
         */
        PriorityQueue<UserBalance> senderHeap =
                new PriorityQueue<>(Comparator.comparingDouble(UserBalance::getAmount));

        /*
            Receiver (Net Profit)
            More positive comes first.
         */
        PriorityQueue<UserBalance> receiverHeap =
                new PriorityQueue<>((a, b) ->
                        Double.compare(b.getAmount(), a.getAmount()));

        /*
            Step 1:
            Calculate net balance of every user.
         */
        for (User user : group.getMembers()) {

            BalanceSheet sheet =
                    group.getBalanceSheet(user);

            double net =
                    sheet.getTotalPaid() - sheet.getTotalExpense();

            sheet.clearBalances();

            if (net > 0) {
                receiverHeap.offer(new UserBalance(user, net));
            }
            else if (net < 0) {
                senderHeap.offer(new UserBalance(user, net));
            }
        }

        /*
            Step 2:
            Match Sender <-> Receiver
         */
        while (!senderHeap.isEmpty() &&
                !receiverHeap.isEmpty()) {

            UserBalance sender = senderHeap.poll();
            UserBalance receiver = receiverHeap.poll();

            double settlement =
                    Math.min(
                            -sender.getAmount(),
                            receiver.getAmount());

            BalanceSheet senderSheet =
                    group.getBalanceSheet(sender.getUser());

            BalanceSheet receiverSheet =
                    group.getBalanceSheet(receiver.getUser());

            senderSheet.addBalance(
                    receiver.getUser(),
                    -settlement);

            receiverSheet.addBalance(
                    sender.getUser(),
                    settlement);

            sender.setAmount(
                    sender.getAmount() + settlement);

            receiver.setAmount(
                    receiver.getAmount() - settlement);

            if (sender.getAmount() < 0) {
                senderHeap.offer(sender);
            }

            if (receiver.getAmount() > 0) {
                receiverHeap.offer(receiver);
            }
        }
    }
}