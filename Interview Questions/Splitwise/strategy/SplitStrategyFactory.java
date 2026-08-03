package strategy;

import enums.SplitType;

public class SplitStrategyFactory {

    private SplitStrategyFactory() {
        // Utility class
    }

    public static SplitStrategy getStrategy(SplitType splitType) {

        switch (splitType) {

            case EQUAL:
                return new EqualSplitStrategy();

            case PERCENTAGE:
                return new PercentageSplitStrategy();

            // Add new strategies here
            // case UNEQUAL:
            //     return new UnequalSplitStrategy();

            default:
                throw new IllegalArgumentException(
                        "Unsupported Split Type : " + splitType);
        }
    }
}