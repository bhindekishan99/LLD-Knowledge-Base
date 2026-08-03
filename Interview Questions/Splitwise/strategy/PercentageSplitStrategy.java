package strategy;

import exception.InvalidSplitException;
import models.Split;
import models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PercentageSplitStrategy implements SplitStrategy {

    @Override
    @SuppressWarnings("unchecked")
    public List<Split> split(
            double amount,
            List<User> participants,
            Map<String, Object> metadata) {

        Map<User, Double> percentages =
                (Map<User, Double>) metadata.get("PERCENTAGES");

        double totalPercentage = 0;

        for (double percentage : percentages.values()) {
            totalPercentage += percentage;
        }

        if (Math.abs(totalPercentage - 100.0) > 0.0001) {
            throw new InvalidSplitException(
                    "Total percentage should be 100");
        }

        List<Split> splits = new ArrayList<>();

        for (User user : participants) {

            double percentage = percentages.get(user);

            double share = amount * percentage / 100;

            splits.add(new Split(user, share));
        }

        return splits;
    }
}