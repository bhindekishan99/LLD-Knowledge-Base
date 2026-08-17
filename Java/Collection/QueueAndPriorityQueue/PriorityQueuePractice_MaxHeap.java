package Java.Collection.QueueAndPriorityQueue;

import java.util.PriorityQueue;
import java.util.Queue;

public class PriorityQueuePractice_MaxHeap {
    public static void main(String[] args) {

        // Largest number gets highest priority.
        Queue<Integer> maxHeap = new PriorityQueue<>(
                (a, b) -> Integer.compare(b, a)
        );

        // Insert
        maxHeap.offer(30);
        maxHeap.offer(10);
        maxHeap.offer(50);
        maxHeap.offer(20);

        // View largest element
        System.out.println("Highest priority: " + maxHeap.peek()); // 50

        // Remove in descending order
        System.out.println("Processing in max-heap order:");
        while (!maxHeap.isEmpty()) {
            System.out.println(maxHeap.poll());
        }
    }
}