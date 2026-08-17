package Java.Collection.Sets;

import java.util.HashSet;
import java.util.Set;

//HashSet stores only unique values. It does not preserve insertion order.

public class HashSetPractice {
    public static void main(String[] args) {

        String sentence = "java is fun and java is powerful";

        // Split sentence into individual words
        String[] words = sentence.split(" ");

        // HashSet removes duplicates automatically
        Set<String> uniqueWords = new HashSet<>();

        // Insert words
        for (String word : words) {
            uniqueWords.add(word);
        }

        // Iterate unique words
        System.out.println("Unique words:");
        for (String word : uniqueWords) {
            System.out.println(word);
        }

        System.out.println("\nNumber of unique words: " + uniqueWords.size());
    
        uniqueWords.add("coding");        // true if added; false if already present
        uniqueWords.contains("java");     // true
        uniqueWords.remove("fun");        // removes it
        uniqueWords.size();               // number of unique values
        uniqueWords.isEmpty();            // true/false
        uniqueWords.clear();              // removes all values
    }
}
