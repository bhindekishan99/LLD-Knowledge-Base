package Java.Collection.Sets;

import java.util.Set;
import java.util.TreeSet;

//TreeSet stores unique elements in sorted order.

public class TreeSetPractice {
    public static void main(String[] args) {

        // 1. Create
        Set<Integer> scores = new TreeSet<>();

        // 2. Add elements — automatically sorted
        scores.add(85);
        scores.add(40);
        scores.add(95);
        scores.add(60);
        scores.add(85); // duplicate ignored

        System.out.println("Scores in sorted order: " + scores);

        // 3. Search
        System.out.println("\nContains 60? " + scores.contains(60));
        System.out.println("Contains 75? " + scores.contains(75));

        // 4. Remove
        boolean removed = scores.remove(40);
        System.out.println("\nWas 40 removed? " + removed);
        System.out.println("After removal: " + scores);

        // 5. Iterate — always sorted
        System.out.println("\nScores:");
        for (Integer score : scores) {
            System.out.println(score);
        }

        // 6. Size and empty check
        System.out.println("\nNumber of scores: " + scores.size());
        System.out.println("Is set empty? " + scores.isEmpty());

        // 7. Clear
        scores.clear();
        System.out.println("\nAfter clear: " + scores);
        System.out.println("Is empty now? " + scores.isEmpty());
    }
}
