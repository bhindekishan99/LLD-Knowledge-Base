package Java.Collection.ArrayList;

import java.util.ArrayList;

public class ArrayListPractice {
    public static void main(String[] args) {

        // 1. Create
        ArrayList<String> students = new ArrayList<>();

        // 2. Insert
        students.add("Aman");
        students.add("Riya");
        students.add("Vikram");
        students.add("Neha");

        System.out.println("After insertion: " + students);

        // 3. Read / search
        System.out.println("\nStudent at index 1: " + students.get(1));

        String searchName = "Vikram";
        if (students.contains(searchName)) {
            System.out.println(searchName + " is present.");
        } else {
            System.out.println(searchName + " is not present.");
        }

        // 4. Update
        students.set(2, "Arjun"); // Replaces Vikram at index 2
        System.out.println("\nAfter update: " + students);

        // 5. Remove
        students.remove("Neha");  // Removes by value, remove first value, do not remove all matching values
        // students.remove(0);    // Removes by index
        // students.removeIf(name -> name.equals("Aman")); // removes all data = Aman
        
        System.out.println("After removal: " + students);

        // 6. Iterate
        System.out.println("\nStudents:");
        for (String student : students) {
            System.out.println(student);
        }

        // 7. Mini exercise:
        // Count names that begin with the letter 'A'
        int count = 0;

        for (String student : students) {
            if (student.startsWith("A")) {
                count++;
            }
        }

        System.out.println("\nStudents whose name starts with A: " + count);
    }
}