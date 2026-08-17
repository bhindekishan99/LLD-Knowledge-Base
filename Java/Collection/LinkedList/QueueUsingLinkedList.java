package Java.Collection.LinkedList;

import java.util.LinkedList;
import java.util.Queue;

public class QueueUsingLinkedList {
    public static void main(String[] args) {

        // Create a queue using LinkedList
        Queue<String> queue = new LinkedList<>();

        // ----- Insert -----
        queue.add("Aman");       // Adds at rear; throws exception if it cannot add
        queue.offer("Riya");     // Adds at rear; returns false if it cannot add
        queue.offer("Vikram");

        System.out.println("Queue: " + queue);

        // ----- Read front element -----
        System.out.println("Front using peek(): " + queue.peek());
        // peek() returns null when queue is empty

        System.out.println("Front using element(): " + queue.element());
        // element() throws exception when queue is empty

        // ----- Remove front element -----
        String served = queue.poll();
        System.out.println("\nServed using poll(): " + served);
        // poll() returns null when queue is empty

        String removed = queue.remove();
        System.out.println("Removed using remove(): " + removed);
        // remove() throws exception when queue is empty

        System.out.println("Queue now: " + queue);

        // ----- Add more customers -----
        queue.offer("Neha");
        queue.offer("Karan");
        queue.offer("Riya");     // Queue allows duplicates

        // ----- Search -----
        System.out.println("\nContains Karan: " + queue.contains("Karan"));

        // ----- Iterate -----
        System.out.println("\nCustomers waiting:");
        for (String customer : queue) {
            System.out.println(customer);
        }

        // ----- Size and empty check -----
        System.out.println("\nNumber of customers: " + queue.size());
        System.out.println("Is queue empty? " + queue.isEmpty());

        // ----- Remove a specific value -----
        queue.remove("Riya"); // removes only the first matching "Riya"
        System.out.println("\nAfter removing first Riya: " + queue);

        // ----- Clear all elements -----
        queue.clear();
        System.out.println("After clear: " + queue);
        System.out.println("Is queue empty? " + queue.isEmpty());
    }
}