package Java.Collection.Map;

import java.util.HashMap;
import java.util.Map;

public class HashMapPractice {
    public static void main(String[] args) {

        // Exercise: count frequency of each word
        String sentence = "java is fun and java is powerful";

        // Key   = word
        // Value = number of times the word appears
        Map<String, Integer> wordFrequency = new HashMap<>();

        for (String word : sentence.split(" ")) {

            // getOrDefault(word, 0):
            // If word exists, return its current count.
            // Otherwise, return 0.
            int currentCount = wordFrequency.getOrDefault(word, 0);

            wordFrequency.put(word, currentCount + 1);
        }

        System.out.println("Word frequencies: " + wordFrequency);

        // ----- get -----
        System.out.println("\nCount of 'java': " + wordFrequency.get("java"));
        // Returns null if key does not exist
        System.out.println("Count of 'python': " + wordFrequency.get("python"));

        // ----- getOrDefault -----
        System.out.println(
            "Count of 'python' (default 0): "
            + wordFrequency.getOrDefault("python", 0)
        );

        // ----- containsKey -----
        System.out.println("\nDoes 'fun' exist? " + wordFrequency.containsKey("fun"));

        // ----- put / update -----
        wordFrequency.put("java", 10); // Updates Java's existing value
        System.out.println("After updating java: " + wordFrequency);

        // ----- remove -----
        Integer removedCount = wordFrequency.remove("and");
        System.out.println("\nRemoved count for 'and': " + removedCount);
        System.out.println("After removal: " + wordFrequency);

        // ----- entrySet and loop -----
        System.out.println("\nAll word frequencies:");
        for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // ----- Other useful methods -----
        System.out.println("\nNumber of distinct words: " + wordFrequency.size());
        System.out.println("Is map empty? " + wordFrequency.isEmpty());

        wordFrequency.clear();
        System.out.println("After clear: " + wordFrequency);
    }
}