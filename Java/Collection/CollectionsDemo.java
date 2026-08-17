package Java.Collection;

import java.util.*;

public class CollectionsDemo {
    public static void main(String[] args) {

        // 1. ArrayList — ordered, duplicates allowed
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Java");
        arrayList.add("Python");
        arrayList.add("Java");

        for (String language : arrayList) {
            System.out.println(language);
        }

        // 2. LinkedList — ordered; efficient inserts/removals at ends
        List<Integer> linkedList = new LinkedList<>();
        linkedList.add(10);
        linkedList.add(20);
        linkedList.add(30);

        for (Integer number : linkedList) {
            System.out.println(number);
        }

        // 3. HashSet — unique values; no guaranteed order
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Apple");
        hashSet.add("Banana");
        hashSet.add("Apple"); // duplicate ignored

        for (String fruit : hashSet) {
            System.out.println(fruit);
        }

        // 4. LinkedHashSet — unique values; insertion order preserved
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("First");
        linkedHashSet.add("Second");
        linkedHashSet.add("Third");

        for (String value : linkedHashSet) {
            System.out.println(value);
        }

        // 5. TreeSet — unique values; automatically sorted
        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(30);
        treeSet.add(10);
        treeSet.add(20);

        for (Integer number : treeSet) {
            System.out.println(number); // 10, 20, 30
        }

        // 6. Queue using LinkedList — typically FIFO
        Queue<String> queue = new LinkedList<>();
        queue.offer("Customer 1");
        queue.offer("Customer 2");
        queue.offer("Customer 3");

        for (String customer : queue) {
            System.out.println(customer);
        }

        // 7. PriorityQueue — smallest element has highest priority by default
        Queue<Integer> priorityQueue = new PriorityQueue<>();
        priorityQueue.offer(30);
        priorityQueue.offer(10);
        priorityQueue.offer(20);

        for (Integer number : priorityQueue) {
            System.out.println(number);
        }
        // Note: looping over PriorityQueue does NOT guarantee sorted output.
        // Use poll() repeatedly if you need priority order.

        // 8. Deque using ArrayDeque — insert/remove from both ends
        Deque<String> deque = new ArrayDeque<>();
        deque.addFirst("Front");
        deque.addLast("Middle");
        deque.addLast("Back");

        for (String item : deque) {
            System.out.println(item);
        }

        // 9. HashMap — key-value pairs; no guaranteed order
        Map<Integer, String> hashMap = new HashMap<>();
        hashMap.put(1, "Aman");
        hashMap.put(2, "Riya");
        hashMap.put(3, "Vikram");

        for (Map.Entry<Integer, String> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // 10. LinkedHashMap — key-value pairs; insertion order preserved
        Map<Integer, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put(3, "Third");
        linkedHashMap.put(1, "First");
        linkedHashMap.put(2, "Second");

        for (Map.Entry<Integer, String> entry : linkedHashMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // 11. TreeMap — key-value pairs; keys automatically sorted
        Map<Integer, String> treeMap = new TreeMap<>();
        treeMap.put(30, "Thirty");
        treeMap.put(10, "Ten");
        treeMap.put(20, "Twenty");

        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    }
}