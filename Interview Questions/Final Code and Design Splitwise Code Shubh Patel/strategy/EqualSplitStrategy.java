package strategy;

import models.Split;
import models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public List<Split> split(
            double amount,
            List<User> participants,
            Map<String, Object> metadata) {

        double share = amount / participants.size();

        List<Split> splits = new ArrayList<>();

        for (User user : participants) {
            splits.add(new Split(user, share));
        }

        return splits;
    }
}