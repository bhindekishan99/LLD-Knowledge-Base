package Java.Collection.LinkedList;

import java.util.Deque;
import java.util.LinkedList;

public class DequeUsingLinkedList {
    public static void main(String[] args) {

        // Create a Deque using LinkedList
        Deque<String> deque = new LinkedList<>();

        // ----- Insert from both ends -----
        deque.offerFirst("B");  // [B]
        deque.offerFirst("A");  // [A, B]
        deque.offerLast("C");   // [A, B, C]
        deque.offerLast("D");   // [A, B, C, D]

        System.out.println("Deque: " + deque);

        // ----- View elements from both ends -----
        System.out.println("First element: " + deque.peekFirst()); // A
        System.out.println("Last element: " + deque.peekLast());   // D

        // ----- Remove from both ends -----
        String firstRemoved = deque.pollFirst(); // removes A
        String lastRemoved = deque.pollLast();   // removes D

        System.out.println("\nRemoved from front: " + firstRemoved);
        System.out.println("Removed from rear: " + lastRemoved);
        System.out.println("Deque now: " + deque); // [B, C]

        // ----- Add regular elements -----
        deque.offerFirst("X");
        deque.offerLast("Y");

        // ----- Search and iterate -----
        System.out.println("\nContains C: " + deque.contains("C"));

        System.out.println("Forward iteration:");
        for (String value : deque) {
            System.out.println(value);
        }

        System.out.println("\nReverse iteration:");
        for (var iterator = deque.descendingIterator(); iterator.hasNext();) {
            System.out.println(iterator.next());
        }

        // ----- Size and clear -----
        System.out.println("\nSize: " + deque.size());

        deque.clear();
        System.out.println("Is empty: " + deque.isEmpty());
    }
}
