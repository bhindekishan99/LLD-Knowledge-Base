package Java.Collection.QueueAndPriorityQueue;

import java.util.PriorityQueue;
import java.util.Queue;

//PriorityQueue removes elements by priority, not by insertion order. By default, the smallest value has the highest priority.

public class PriorityQueuePractice_MinHeap {
    public static void main(String[] args) {

        // Smallest number gets served first
        Queue<Integer> tasks = new PriorityQueue<>();

        // ----- Insert -----
        tasks.offer(30);
        tasks.offer(10);
        tasks.offer(50);
        tasks.offer(20);

        System.out.println("PriorityQueue: " + tasks);
        // Do not expect this display to be fully sorted.

        // ----- View highest-priority element -----
        System.out.println("\nNext task priority: " + tasks.peek()); // 10

        // ----- Remove in priority order -----
        System.out.println("\nProcessing tasks:");
        while (!tasks.isEmpty()) {
            System.out.println(tasks.poll());
        }
        // Output: 10, 20, 30, 50

        // ----- More core operations -----
        tasks.offer(40);
        tasks.offer(15);
        tasks.offer(25);

        System.out.println("\nContains priority 15? " + tasks.contains(15));
        System.out.println("Number of tasks: " + tasks.size());

        tasks.remove(25); // Removes the value 25; not necessarily from the front
        System.out.println("After removing 25: " + tasks);

        tasks.clear();
        System.out.println("Is queue empty? " + tasks.isEmpty());
    }
}
