package Java.Collection.Sets;

import java.util.LinkedHashSet;
import java.util.Set;

//It keeps elements unique like HashSet, but preserves their insertion order.

import java.util.LinkedHashSet;
import java.util.Set;

public class LinkedHashSetPractice {
    public static void main(String[] args) {

        // 1. Create
        Set<String> cities = new LinkedHashSet<>();

        // 2. Add elements
        cities.add("Delhi");
        cities.add("Mumbai");
        cities.add("Pune");
        cities.add("Bengaluru");

        boolean wasAdded = cities.add("Delhi"); // duplicate ignored
        System.out.println("Was duplicate Delhi added? " + wasAdded);

        System.out.println("\nCities: " + cities);

        // 3. Search
        System.out.println("Contains Pune? " + cities.contains("Pune"));
        System.out.println("Contains Chennai? " + cities.contains("Chennai"));

        // 4. Remove
        boolean wasRemoved = cities.remove("Mumbai");
        System.out.println("\nWas Mumbai removed? " + wasRemoved);
        System.out.println("After removal: " + cities);

        // 5. Size
        System.out.println("\nNumber of cities: " + cities.size());

        // 6. Iterate (in insertion order)
        System.out.println("\nCities in insertion order:");
        for (String city : cities) {
            System.out.println(city);
        }

        // 7. Empty check
        System.out.println("\nIs set empty? " + cities.isEmpty());

        // 8. Clear every element
        cities.clear();

        System.out.println("After clear: " + cities);
        System.out.println("Is set empty now? " + cities.isEmpty());
    }
}