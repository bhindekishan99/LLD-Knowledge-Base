package Java.Collection.Map;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


// Use LinkedHashMap when you want to preserve the order in which entries were inserted. Use TreeMap when you want entries sorted by key.

public class LinkedHashMapPractice{
    public static void main(String[] args) {

        // Key = student ID, Value = marks
        Map<Integer, Integer> insertionOrderMarks = new LinkedHashMap<>();

        // Insert entries in this specific order
        insertionOrderMarks.put(103, 78);
        insertionOrderMarks.put(101, 92);
        insertionOrderMarks.put(105, 85);
        insertionOrderMarks.put(102, 88);

        System.out.println("LinkedHashMap: insertion order");
        for (Map.Entry<Integer, Integer> entry : insertionOrderMarks.entrySet()) {
            System.out.println(
                "Student ID: " + entry.getKey()
                + ", Marks: " + entry.getValue()
            );
        }

        // TreeMap sorts entries automatically by key (student ID)
        Map<Integer, Integer> sortedMarks = new TreeMap<>();

        sortedMarks.put(103, 78);
        sortedMarks.put(101, 92);
        sortedMarks.put(105, 85);
        sortedMarks.put(102, 88);

        System.out.println("\nTreeMap: sorted by student ID");
        for (Map.Entry<Integer, Integer> entry : sortedMarks.entrySet()) {
            System.out.println(
                "Student ID: " + entry.getKey()
                + ", Marks: " + entry.getValue()
            );
        }

        // ----- Core map operations -----
        System.out.println("\nMarks of student 101: " + sortedMarks.get(101));
        System.out.println("Student 105 exists: " + sortedMarks.containsKey(105));

        sortedMarks.put(102, 91); // Updates marks for ID 102
        System.out.println("Updated marks of 102: " + sortedMarks.get(102));

        sortedMarks.remove(103);
        System.out.println("After removing 103: " + sortedMarks);

        System.out.println("Total students: " + sortedMarks.size());
        System.out.println("Is map empty? " + sortedMarks.isEmpty());
    }
}
