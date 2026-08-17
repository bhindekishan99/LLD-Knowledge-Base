package Java.Collection.Sets;

import java.util.NavigableSet;
import java.util.TreeSet;

//To use TreeSet-specific navigation methods, declare the left side as TreeSet or preferably NavigableSet:
// Unlike HashSet, TreeSet usually does not allow null, because it needs to compare elements to keep them sorted.

public class NavigableSetPratice {

    public static void main(String[] args) {
        NavigableSet<Integer> scores = new TreeSet<>();

        scores.add(85);
        scores.add(40);
        scores.add(95);
        scores.add(60);

        System.out.println(scores.first()); // 40: smallest
        System.out.println(scores.last()); // 95: largest

        System.out.println(scores.lower(60)); // 40: strictly less than 60
        System.out.println(scores.floor(60)); // 60: less than or equal to 60
        System.out.println(scores.ceiling(61)); // 85: greater than or equal to 61
        System.out.println(scores.higher(60)); // 85: strictly greater than 60

    }

}
