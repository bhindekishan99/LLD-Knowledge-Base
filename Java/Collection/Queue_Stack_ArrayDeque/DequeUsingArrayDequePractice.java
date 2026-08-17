package Java.Collection.Queue_Stack_ArrayDeque;

import java.util.ArrayDeque;
import java.util.Deque;

public class DequeUsingArrayDequePractice {
    public static void main(String[] args) {

        // Create a Deque using ArrayDeque
        Deque<String> deque = new ArrayDeque<>();

        // ----- Add elements -----
        deque.offerFirst("B"); // [B]
        deque.offerFirst("A"); // [A, B]
        deque.offerLast("C");  // [A, B, C]
        deque.offerLast("D");  // [A, B, C, D]

        System.out.println("Deque: " + deque);

        // ----- View elements without removing -----
        System.out.println("Front: " + deque.peekFirst()); // A
        System.out.println("Rear: " + deque.peekLast());   // D

        // ----- Remove from both ends -----
        System.out.println("Removed from front: " + deque.pollFirst()); // A
        System.out.println("Removed from rear: " + deque.pollLast());   // D

        System.out.println("Deque now: " + deque); // [B, C]

        // ----- Search and iterate -----
        deque.offerLast("E");

        System.out.println("\nContains C? " + deque.contains("C"));
        System.out.println("Size: " + deque.size());

        System.out.println("\nForward loop:");
        for (String value : deque) {
            System.out.println(value);
        }

        System.out.println("\nReverse loop:");
        for (var iterator = deque.descendingIterator(); iterator.hasNext();) {
            System.out.println(iterator.next());
        }

        // ----- Empty and clear -----
        System.out.println("\nIs empty? " + deque.isEmpty());

        deque.clear();
        System.out.println("After clear: " + deque);
    }
}
