package strategy;

import models.Split;
import models.User;

import java.util.List;
import java.util.Map;

public interface SplitStrategy {

    List<Split> split(
            double amount,
            List<User> participants,
            Map<String, Object> metadata
    );
}