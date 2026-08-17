package Java.Collection.LinkedList;

import java.util.LinkedList;

public class LinkedListCoreFunctions {
    public static void main(String[] args) {

        LinkedList<String> cities = new LinkedList<>();

        // ----- Insert -----
        cities.add("Delhi");                 // Add at end
        cities.add("Mumbai");
        cities.add("Pune");

        cities.addFirst("Chandigarh");       // Add at beginning
        cities.addLast("Bengaluru");         // Add at end
        cities.add(2, "Jaipur");             // Add at a specific index

        System.out.println("After insertions: " + cities);

        // ----- Read -----
        System.out.println("First city: " + cities.getFirst());
        System.out.println("Last city: " + cities.getLast());
        System.out.println("City at index 2: " + cities.get(2));

        // ----- Search -----
        System.out.println("Contains Pune: " + cities.contains("Pune")); // return boolean
        System.out.println("Index of Mumbai: " + cities.indexOf("Mumbai"));

        cities.add("Delhi"); // Add a duplicate for lastIndexOf example
        System.out.println("Last index of Delhi: " + cities.lastIndexOf("Delhi"));

        // ----- Update -----
        cities.set(2, "Ahmedabad");
        System.out.println("After update: " + cities);

        // ----- Remove -----
        cities.remove();                     // Remove first element
        cities.removeFirst();                // Remove first element
        cities.removeLast();                 // Remove last element
        cities.remove(1);                    // Remove at index
        cities.remove("Pune");               // Remove first matching value

        System.out.println("After removals: " + cities);

        // ----- Queue operations -----
        cities.offer("Kolkata");             // Add at end
        cities.offerFirst("Goa");            // Add at beginning
        cities.offerLast("Chennai");         // Add at end

        System.out.println("\nAs a Queue/Deque: " + cities);
        System.out.println("Peek first: " + cities.peek()); //does not remove
        System.out.println("Peek last: " + cities.peekLast()); //does not remove
        System.out.println("Poll first: " + cities.poll()); //removes the first
        System.out.println("Poll last: " + cities.pollLast()); //removes the last

        // ----- Iterate -----
        System.out.println("\nRemaining cities:");
        for (String city : cities) {
            System.out.println(city);
        }

        // ----- Other useful operations -----
        System.out.println("\nNumber of cities: " + cities.size());
        System.out.println("Is empty: " + cities.isEmpty());

        cities.clear();                      // Remove every element
        System.out.println("After clear: " + cities);
        System.out.println("Is empty: " + cities.isEmpty());
    }
}