package Java.Collection.QueueAndPriorityQueue;

import java.util.PriorityQueue;
import java.util.Queue;

//

public class StudentHeapExample {

    static class Student {
        String name;
        int id;

        Student(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String toString() {
            return name + " (id=" + id + ")";
        }
    }

    public static void main(String[] args) {

        // Min-heap: smallest ID comes out first
        Queue<Student> minHeap = new PriorityQueue<>(
            (student1, student2) ->
                Integer.compare(student1.id, student2.id) //compare simply does: student1.id - student2.id
                // negative → student1 comes before student2
                // zero     → both have equal priority
                // positive → student2 comes before student1
        );

        // Max-heap: largest ID comes out first
        Queue<Student> maxHeap = new PriorityQueue<>(
            (student1, student2) ->
                Integer.compare(student2.id, student1.id)
        );

        Student aman = new Student("Aman", 103);
        Student riya = new Student("Riya", 101);
        Student vikram = new Student("Vikram", 105);

        minHeap.offer(aman);
        minHeap.offer(riya);
        minHeap.offer(vikram);

        maxHeap.offer(aman);
        maxHeap.offer(riya);
        maxHeap.offer(vikram);

        System.out.println("Min-heap order:");
        while (!minHeap.isEmpty()) {
            System.out.println(minHeap.poll());
        }

        System.out.println("\nMax-heap order:");
        while (!maxHeap.isEmpty()) {
            System.out.println(maxHeap.poll());
        }
    }
}