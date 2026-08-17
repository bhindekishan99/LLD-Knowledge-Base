package Java.Collection.Map;

import java.util.Map;
import java.util.TreeMap;

public class DescendingTreeMap {
    public static void main(String[] args) {

        Map<Integer, String> students = new TreeMap<>(
            (key1, key2) -> {

                if (key1 > key2) {
                    return -1; // key1 is bigger, so it comes first
                }

                if (key1 < key2) {
                    return 1;  // key2 is bigger, so it comes first
                }

                return 0; // both keys are equal
            }
        );

        students.put(101, "Riya");
        students.put(105, "Vikram");
        students.put(103, "Aman");

        System.out.println(students);
    }
}



