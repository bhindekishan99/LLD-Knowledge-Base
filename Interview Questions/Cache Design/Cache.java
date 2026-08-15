import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Cache LLD - Single File
 *
 * Based on the uploaded Cache implementation.
 *
 * Thread Affinity / KeyBasedExecutor is intentionally removed.
 *
 * Main ideas:
 * 1. CacheStorage      -> in-memory cache
 * 2. DBStorage         -> persistent storage
 * 3. WritePolicy       -> controls how writes happen
 * 4. EvictionAlgorithm -> controls which key is removed
 *
 * Strategy Pattern:
 *     WritePolicy       -> WriteThroughPolicy
 *     EvictionAlgorithm -> LRUEvictionAlgorithm
 *
 * To change the policy, pass a different implementation
 * to Cache's constructor. Cache itself does not change.
 */
public class CacheDemo {

    // =========================================================
    // STORAGE
    // =========================================================

    interface CacheStorage<K, V> {
        void put(K key, V value) throws Exception;

        V get(K key) throws Exception;

        void remove(K key) throws Exception;

        boolean containsKey(K key);

        int size();

        int getCapacity();
    }

    interface DBStorage<K, V> {
        void write(K key, V value) throws Exception;

        V read(K key) throws Exception;

        void delete(K key) throws Exception;
    }

    static class InMemoryCacheStorage<K, V>
            implements CacheStorage<K, V> {

        private final Map<K, V> cache =
                new ConcurrentHashMap<>();

        private final int capacity;

        InMemoryCacheStorage(int capacity) {
            if (capacity <= 0) {
                throw new IllegalArgumentException(
                        "Capacity must be greater than 0");
            }

            this.capacity = capacity;
        }

        @Override
        public void put(K key, V value) {
            cache.put(key, value);
        }

        @Override
        public V get(K key) throws Exception {

            V value = cache.get(key);

            if (value == null) {
                throw new Exception(
                        "Key not found in cache: " + key);
            }

            return value;
        }

        @Override
        public void remove(K key) throws Exception {

            if (!cache.containsKey(key)) {
                throw new Exception(
                        "Key not found in cache: " + key);
            }

            cache.remove(key);
        }

        @Override
        public boolean containsKey(K key) {
            return cache.containsKey(key);
        }

        @Override
        public int size() {
            return cache.size();
        }

        @Override
        public int getCapacity() {
            return capacity;
        }
    }

    static class SimpleDBStorage<K, V>
            implements DBStorage<K, V> {

        /*
         * Mock DB for interview/demo purposes.
         */
        private final Map<K, V> database =
                new ConcurrentHashMap<>();

        @Override
        public void write(K key, V value) {
            database.put(key, value);
        }

        @Override
        public V read(K key) throws Exception {

            V value = database.get(key);

            if (value == null) {
                throw new Exception(
                        "Key not found in DB: " + key);
            }

            return value;
        }

        @Override
        public void delete(K key) throws Exception {

            if (!database.containsKey(key)) {
                throw new Exception(
                        "Key not found in DB: " + key);
            }

            database.remove(key);
        }
    }

    // =========================================================
    // WRITE POLICY - STRATEGY
    // =========================================================

    interface WritePolicy<K, V> {

        void write(
                K key,
                V value,
                CacheStorage<K, V> cacheStorage,
                DBStorage<K, V> dbStorage)
                throws Exception;
    }

    /*
     * Write-Through:
     *
     * update()
     *    |
     *    +----> Cache
     *    |
     *    +----> DB
     *
     * Both writes are started concurrently and we wait
     * for both to finish.
     */
    static class WriteThroughPolicy<K, V>
            implements WritePolicy<K, V> {

        @Override
        public void write(
                K key,
                V value,
                CacheStorage<K, V> cacheStorage,
                DBStorage<K, V> dbStorage)
                throws Exception {

            CompletableFuture<Void> cacheFuture =
                    CompletableFuture.runAsync(() -> {

                        try {
                            cacheStorage.put(key, value);
                        } catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    });

            CompletableFuture<Void> dbFuture =
                    CompletableFuture.runAsync(() -> {

                        try {
                            dbStorage.write(key, value);
                        } catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    });

            CompletableFuture.allOf(
                    cacheFuture,
                    dbFuture
            ).join();
        }
    }

    /*
     * Example of another policy.
     *
     * This is only here to demonstrate how easily the strategy
     * can be replaced. The Cache class does not need to change.
     *
     * In a real system, Write-Around could write to DB first
     * and skip updating the cache.
     */
    static class WriteAroundPolicy<K, V>
            implements WritePolicy<K, V> {

        @Override
        public void write(
                K key,
                V value,
                CacheStorage<K, V> cacheStorage,
                DBStorage<K, V> dbStorage)
                throws Exception {

            dbStorage.write(key, value);
        }
    }

    // =========================================================
    // EVICTION POLICY - STRATEGY
    // =========================================================

    interface EvictionAlgorithm<K> {

        /*
         * Called whenever a key becomes recently used.
         */
        void keyAccessed(K key);

        /*
         * Removes a key from the eviction structure
         * and returns the key that should be evicted.
         */
        K evictKey();
    }

    // =========================================================
    // DOUBLY LINKED LIST
    // =========================================================

    static class DoublyLinkedListNode<K> {

        private final K value;

        private DoublyLinkedListNode<K> prev;
        private DoublyLinkedListNode<K> next;

        DoublyLinkedListNode(K value) {
            this.value = value;
        }

        K getValue() {
            return value;
        }
    }

    static class DoublyLinkedList<K> {

        private DoublyLinkedListNode<K> head;
        private DoublyLinkedListNode<K> tail;

        void addAtTail(
                DoublyLinkedListNode<K> node) {

            if (tail == null) {
                head = node;
                tail = node;
                return;
            }

            tail.next = node;
            node.prev = tail;
            tail = node;
        }

        void detach(
                DoublyLinkedListNode<K> node) {

            if (node == null) {
                return;
            }

            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }

            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }

            node.prev = null;
            node.next = null;
        }

        DoublyLinkedListNode<K> getHead() {
            return head;
        }

        void removeHead() {

            if (head == null) {
                return;
            }

            if (head.next == null) {
                head = null;
                tail = null;
                return;
            }

            head = head.next;
            head.prev = null;
        }
    }

    // =========================================================
    // LRU EVICTION STRATEGY
    // =========================================================

    /*
     * LRU:
     *
     * Head -> Least Recently Used
     * Tail -> Most Recently Used
     *
     * HashMap<K, Node> + Doubly Linked List
     *
     * keyAccessed() -> O(1)
     * evictKey()    -> O(1)
     */
    static class LRUEvictionAlgorithm<K>
            implements EvictionAlgorithm<K> {

        private final DoublyLinkedList<K> list =
                new DoublyLinkedList<>();

        private final Map<K, DoublyLinkedListNode<K>>
                keyToNode =
                new HashMap<>();

        /*
         * LRU has two structures that must stay consistent:
         *
         *     HashMap + Doubly Linked List
         *
         * Therefore these operations are synchronized.
         */
        @Override
        public synchronized void keyAccessed(K key) {

            if (keyToNode.containsKey(key)) {

                DoublyLinkedListNode<K> node =
                        keyToNode.get(key);

                list.detach(node);
                list.addAtTail(node);

                return;
            }

            DoublyLinkedListNode<K> node =
                    new DoublyLinkedListNode<>(key);

            list.addAtTail(node);
            keyToNode.put(key, node);
        }

        @Override
        public synchronized K evictKey() {

            DoublyLinkedListNode<K> node =
                    list.getHead();

            if (node == null) {
                return null;
            }

            K key = node.getValue();

            list.removeHead();
            keyToNode.remove(key);

            return key;
        }
    }

    /*
     * Example alternative eviction strategy.
     *
     * FIFO:
     * remove the oldest inserted key.
     *
     * The implementation is intentionally simple for
     * demonstrating Strategy replacement.
     */
    static class FIFOEvictionAlgorithm<K>
            implements EvictionAlgorithm<K> {

        private final Queue<K> queue =
                new ArrayDeque<>();

        private final Set<K> present =
                new HashSet<>();

        @Override
        public synchronized void keyAccessed(K key) {

            if (present.add(key)) {
                queue.offer(key);
            }
        }

        @Override
        public synchronized K evictKey() {

            K key = queue.poll();

            if (key != null) {
                present.remove(key);
            }

            return key;
        }
    }

    // =========================================================
    // CACHE
    // =========================================================

    static class Cache<K, V> {

        private final CacheStorage<K, V> cacheStorage;
        private final DBStorage<K, V> dbStorage;

        /*
         * These are Strategy objects.
         *
         * Cache does not know the concrete implementation.
         */
        private final WritePolicy<K, V> writePolicy;
        private final EvictionAlgorithm<K> evictionAlgorithm;

        Cache(
                CacheStorage<K, V> cacheStorage,
                DBStorage<K, V> dbStorage,
                WritePolicy<K, V> writePolicy,
                EvictionAlgorithm<K> evictionAlgorithm) {

            this.cacheStorage = cacheStorage;
            this.dbStorage = dbStorage;
            this.writePolicy = writePolicy;
            this.evictionAlgorithm = evictionAlgorithm;
        }

        /*
         * Read operation.
         *
         * Synchronized because a cache read also changes
         * the LRU ordering.
         */
        public synchronized V get(K key)
                throws Exception {

            if (!cacheStorage.containsKey(key)) {
                throw new Exception(
                        "Key not found in cache: " + key);
            }

            V value = cacheStorage.get(key);

            // get() makes this key most recently used.
            evictionAlgorithm.keyAccessed(key);

            return value;
        }

        /*
         * Write operation.
         *
         * For this interview implementation, the complete
         * cache update is synchronized so that:
         *
         *     capacity check
         *          +
         *     eviction
         *          +
         *     write
         *          +
         *     LRU update
         *
         * remain consistent.
         *
         * We intentionally do NOT use KeyBasedExecutor.
         */
        public synchronized void put(
                K key,
                V value)
                throws Exception {

            boolean keyAlreadyExists =
                    cacheStorage.containsKey(key);

            if (!keyAlreadyExists &&
                    cacheStorage.size() >=
                            cacheStorage.getCapacity()) {

                K evictedKey =
                        evictionAlgorithm.evictKey();

                if (evictedKey != null) {
                    cacheStorage.remove(evictedKey);
                }
            }

            /*
             * The selected WritePolicy decides how the
             * cache and DB are updated.
             */
            writePolicy.write(
                    key,
                    value,
                    cacheStorage,
                    dbStorage);

            /*
             * Tell the eviction strategy that this key
             * was just used.
             */
            evictionAlgorithm.keyAccessed(key);
        }

        public V getFromDB(K key)
                throws Exception {

            return dbStorage.read(key);
        }
    }

    // =========================================================
    // CLIENT
    // =========================================================

    public static void main(String[] args)
            throws Exception {

        CacheStorage<String, String> cacheStorage =
                new InMemoryCacheStorage<>(3);

        DBStorage<String, String> dbStorage =
                new SimpleDBStorage<>();

        /*
         * Select policies here.
         *
         * Changing these objects does NOT require changing
         * the Cache class.
         */
        WritePolicy<String, String> writePolicy =
                new WriteThroughPolicy<>();

        EvictionAlgorithm<String> evictionPolicy =
                new LRUEvictionAlgorithm<>();

        Cache<String, String> cache =
                new Cache<>(
                        cacheStorage,
                        dbStorage,
                        writePolicy,
                        evictionPolicy);

        // -----------------------------------------------------
        // Add A, B, C
        // -----------------------------------------------------

        cache.put("A", "Apple");
        cache.put("B", "Banana");
        cache.put("C", "Cherry");

        /*
         * LRU order:
         *
         * A -> B -> C
         *
         * A = LRU
         * C = MRU
         */

        // Access A.
        cache.get("A");

        /*
         * LRU order becomes:
         *
         * B -> C -> A
         *
         * B = LRU
         * A = MRU
         */

        // -----------------------------------------------------
        // Add D
        // -----------------------------------------------------

        cache.put("D", "Durian");

        /*
         * Cache was full.
         *
         * B was least recently used.
         *
         * Therefore:
         *
         * B is evicted.
         *
         * Cache:
         * A, C, D
         */

        try {
            cache.get("B");
        } catch (Exception e) {
            System.out.println(
                    "B was evicted.");
        }

        System.out.println(
                "A = " + cache.get("A"));

        System.out.println(
                "C = " + cache.get("C"));

        System.out.println(
                "D = " + cache.get("D"));

        // -----------------------------------------------------
        // Change eviction strategy
        // -----------------------------------------------------

        /*
         * To use FIFO instead of LRU:
         *
         * EvictionAlgorithm<String> evictionPolicy =
         *         new FIFOEvictionAlgorithm<>();
         *
         * Cache implementation remains unchanged.
         */

        // -----------------------------------------------------
        // Change write strategy
        // -----------------------------------------------------

        /*
         * To use another write policy:
         *
         * WritePolicy<String, String> writePolicy =
         *         new WriteAroundPolicy<>();
         *
         * Again, Cache implementation remains unchanged.
         */
    }
}
