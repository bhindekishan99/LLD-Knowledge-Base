package Java.Collection.Map;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;


//TreeMap uses a Red-Black Tree internally, so keys are always sorted and these operations are typically O(log n):

public class TreeMapPractice {
    public static void main(String[] args) {

        // Key = student ID, Value = marks
        // TreeMap automatically sorts by key.
        NavigableMap<Integer, Integer> studentMarks = new TreeMap<>();

        // ----- Add / update -----
        studentMarks.put(103, 78);
        studentMarks.put(101, 92);
        studentMarks.put(105, 85);
        studentMarks.put(102, 88);

        System.out.println("Sorted by student ID: " + studentMarks);

        studentMarks.put(102, 91); // Updates value for existing key
        System.out.println("After updating ID 102: " + studentMarks);

        // ----- Read / search -----
        System.out.println("\nMarks of ID 101: " + studentMarks.get(101));
        System.out.println("Marks of ID 999: " + studentMarks.get(999)); // null

        System.out.println("Marks of ID 999 with default: "
                + studentMarks.getOrDefault(999, 0));

        System.out.println("Does ID 105 exist? "
                + studentMarks.containsKey(105));

        System.out.println("Does mark 85 exist? "
                + studentMarks.containsValue(85));

        // ----- Remove -----
        Integer removedMarks = studentMarks.remove(103);
        System.out.println("\nRemoved marks for ID 103: " + removedMarks);
        System.out.println("After removal: " + studentMarks);

        // ----- Sorted-map navigation -----
        System.out.println("\nLowest student ID: " + studentMarks.firstKey());
        System.out.println("Highest student ID: " + studentMarks.lastKey());

        System.out.println("ID < 104: " + studentMarks.lowerKey(104));
        System.out.println("ID <= 104: " + studentMarks.floorKey(104));
        System.out.println("ID >= 104: " + studentMarks.ceilingKey(104));
        System.out.println("ID > 104: " + studentMarks.higherKey(104));

        // ----- Iterate in sorted key order -----
        System.out.println("\nStudents in sorted ID order:");
        for (Map.Entry<Integer, Integer> entry : studentMarks.entrySet()) {
            System.out.println(
                    "ID: " + entry.getKey()
                    + ", Marks: " + entry.getValue()
            );
        }

        // ----- Size, empty, clear -----
        System.out.println("\nTotal students: " + studentMarks.size());
        System.out.println("Is empty? " + studentMarks.isEmpty());

        studentMarks.clear();
        System.out.println("After clear: " + studentMarks);
    }
}
