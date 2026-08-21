package Java.Collection.QueueAndPriorityQueue;

import java.util.LinkedList;
import java.util.Queue;

/*
offer(value) // insert at rear
peek()       // view front
poll()       // remove front

These are safer than add(), element(), and remove() because they return false or null instead of throwing an exception when an operation cannot succeed.

Most efficient way to implement: using ArrayDeque
ArrayDeque: O(1) amortized time for operations (add, poll, peek). It has minimal memory overhead and is the best general-purpose choice.
LinkedList: O(1) constant time but comes with heavy JVM garbage collection overhead due to node object allocation
 */

public class QueuePractice {
    public static void main(String[] args) {

        // Queue follows FIFO: First In, First Out
        Queue<String> customers = new LinkedList<>();

        // ----- Insert at rear -----
        customers.offer("Aman");
        customers.offer("Riya");
        customers.offer("Vikram");

        System.out.println("Queue: " + customers);

        // ----- View front without removing -----
        System.out.println("Next customer: " + customers.peek());

        // ----- Remove from front -----
        String servedCustomer = customers.poll();
        System.out.println("Served customer: " + servedCustomer);
        System.out.println("Queue after serving: " + customers);

        // ----- More core methods -----
        customers.offer("Neha");
        customers.offer("Karan");

        System.out.println("\nContains Neha? " + customers.contains("Neha"));
        System.out.println("Number of customers: " + customers.size());
        System.out.println("Is queue empty? " + customers.isEmpty());

        // remove(value) removes the first matching occurrence
        customers.remove("Riya");
        System.out.println("After removing Riya: " + customers);

        // ----- Iterate from front to rear -----
        System.out.println("\nCustomers waiting:");
        for (String customer : customers) {
            System.out.println(customer);
        }

        // ----- Clear queue -----
        customers.clear();
        System.out.println("\nAfter clear: " + customers);
        System.out.println("Is queue empty now? " + customers.isEmpty());
    }
}
