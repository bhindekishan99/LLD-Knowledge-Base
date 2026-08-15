import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Cache LLD - Single File
 *
 * Key-Based Thread Affinity / KeyBasedExecutor is intentionally removed.
 *
 * Main components:
 * 1. CacheStorage       -> stores actual cache data
 * 2. DBStorage          -> represents persistent storage
 * 3. WritePolicy       -> decides how a write is performed
 * 4. EvictionAlgorithm -> maintains eviction metadata/order
 *
 * Strategy Pattern:
 *     WritePolicy       -> WriteThroughPolicy
 *     EvictionAlgorithm -> LRUEvictionAlgorithm / FIFOEvictionAlgorithm
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
     *     put()
     *       |
     *       +----> Cache
     *       |
     *       +----> DB
     *
     * Both writes happen concurrently and Cache waits
     * until both complete.
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
     * Add another WritePolicy implementation when needed.
     *
     * Example:
     *
     * static class AnotherWritePolicy<K, V>
     *         implements WritePolicy<K, V> {
     *
     *     @Override
     *     public void write(
     *             K key,
     *             V value,
     *             CacheStorage<K, V> cacheStorage,
     *             DBStorage<K, V> dbStorage)
     *             throws Exception {
     *
     *         // Different write behavior
     *     }
     * }
     *
     * Cache itself does not need to change.
     */

    // =========================================================
    // EVICTION POLICY - STRATEGY
    // =========================================================

    /*
     * The Cache controls the flow.
     *
     * EvictionAlgorithm only manages eviction metadata.
     *
     * existingKeyAccessed(key)
     *     -> existing key was accessed
     *
     * addNewKey(key)
     *     -> a new key entered the cache
     *
     * evictKey()
     *     -> cache is full; return a key to remove
     */
    interface EvictionAlgorithm<K> {

        void existingKeyAccessed(K key);

        void addNewKey(K key);

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
     * existingKeyAccessed() -> O(1)
     * addNewKey()   -> O(1)
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
         * Both HashMap and LinkedList represent the same
         * eviction metadata, so their updates must be atomic.
         */
        @Override
        public synchronized void existingKeyAccessed(K key) {

            /*
             * existingKeyAccessed() is called only for a key that
             * already exists in the cache.
             */
            DoublyLinkedListNode<K> node =
                    keyToNode.get(key);

            if (node == null) {
                return;
            }

            // Existing key becomes Most Recently Used.
            list.detach(node);
            list.addAtTail(node);
        }

        @Override
        public synchronized void addNewKey(K key) {

            /*
             * addNewKey() is called only when a completely
             * new key enters the cache.
             */
            DoublyLinkedListNode<K> node =
                    new DoublyLinkedListNode<>(key);

            list.addAtTail(node);
            keyToNode.put(key, node);
        }

        @Override
        public synchronized K evictKey() {

            // Head = Least Recently Used.
            DoublyLinkedListNode<K> node =
                    list.getHead();

            if (node == null) {
                return null;
            }

            K key = node.getValue();

            // Remove from eviction data structure.
            list.removeHead();
            keyToNode.remove(key);

            return key;
        }
    }

    // =========================================================
    // FIFO EVICTION STRATEGY
    // =========================================================

    /*
     * FIFO:
     *
     * existingKeyAccessed() does nothing because accessing an
     * existing key does not change FIFO order.
     *
     * addNewKey() adds the new key to the end.
     *
     * evictKey() removes the oldest key.
     */
    static class FIFOEvictionAlgorithm<K>
            implements EvictionAlgorithm<K> {

        private final Queue<K> queue =
                new ArrayDeque<>();

        @Override
        public synchronized void existingKeyAccessed(K key) {
            // FIFO order does not change on access.
        }

        @Override
        public synchronized void addNewKey(K key) {
            queue.offer(key);
        }

        @Override
        public synchronized K evictKey() {
            return queue.poll();
        }
    }

    // =========================================================
    // CACHE
    // =========================================================

    static class Cache<K, V> {

        private final CacheStorage<K, V> cacheStorage;
        private final DBStorage<K, V> dbStorage;

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
         * GET
         *
         *     CacheStorage.get() (if not present then we'll read from DB)
         *             +
         *     evictionPolicy.existingKeyAccessed()
         *
         * existingKeyAccessed() is only for an existing key.
         */
        public synchronized V get(K key) throws Exception {
            // 1. Try cache
            if (cacheStorage.containsKey(key)) {

                V value = cacheStorage.get(key);

                // Existing cache key was accessed
                evictionAlgorithm.existingKeyAccessed(key);

                return value;
            }

            // 2. Cache miss → try DB (dbStorage.read() thorws exeption if key is not present in DB)
            V value = dbStorage.read(key);

            // 3. Key exists in DB but not cache.
            //    Add it to cache.
            if (cacheStorage.size() >= cacheStorage.getCapacity()) {

                K evictedKey =
                        evictionAlgorithm.evictKey();

                cacheStorage.remove(evictedKey);
            }

            cacheStorage.put(key, value);

            evictionAlgorithm.addNewKey(key);

            return value;
        }

        /*
         * PUT
         *
         * Case 1: Key already exists
         *
         *     update value
         *          +
         *     existingKeyAccessed(key)
         *
         * Case 2: Key is new and cache has space
         *
         *     put into cache
         *          +
         *     addNewKey(key)
         *
         * Case 3: Key is new and cache is full
         *
         *     evictKey()
         *          ↓
         *     remove evicted key
         *          ↓
         *     put new key
         *          ↓
         *     addNewKey(newKey)
         *
         * The complete flow is synchronized so CacheStorage
         * and EvictionAlgorithm remain logically in sync.
         */
        public synchronized void put(
                K key,
                V value)
                throws Exception {

            // -------------------------------------------------
            // Case 1: Key already exists
            // -------------------------------------------------

            if (cacheStorage.containsKey(key)) {

                writePolicy.write(
                        key,
                        value,
                        cacheStorage,
                        dbStorage);

                evictionAlgorithm.existingKeyAccessed(key);

                return;
            }

            // -------------------------------------------------
            // Case 2 / 3: New key
            // -------------------------------------------------

            if (cacheStorage.size() >=
                    cacheStorage.getCapacity()) {

                // Cache is full -> ask policy which key to evict.
                K evictedKey =
                        evictionAlgorithm.evictKey();

                if (evictedKey != null) {
                    cacheStorage.remove(evictedKey);
                }
            }

            /*
             * Write the new key.
             *
             * With WriteThroughPolicy this updates both
             * cache and DB.
             */
            writePolicy.write(
                    key,
                    value,
                    cacheStorage,
                    dbStorage);

            // Tell eviction policy that a new key entered.
            evictionAlgorithm.addNewKey(key);
        }

        /*
         * Direct DB read.
         *
         * This is mainly useful for demonstrating the
         * Cache + DB design in an interview.
         */
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
         * Select the policies here.
         *
         * Cache does not need to change when we replace
         * either strategy.
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
         * LRU:
         *
         * A -> B -> C
         * ↑         ↑
         * LRU      MRU
         */

        // Access A -> A becomes MRU.
        cache.get("A");

        /*
         * LRU becomes:
         *
         * B -> C -> A
         * ↑         ↑
         * LRU      MRU
         */

        // -----------------------------------------------------
        // Add D
        // -----------------------------------------------------

        cache.put("D", "Durian");

        /*
         * Cache was full.
         *
         * B was LRU, so:
         *
         * 1. evictionPolicy.evictKey() -> B
         * 2. cacheStorage.remove(B)
         * 3. cacheStorage.put(D)
         * 4. evictionPolicy.addNewKey(D)
         *
         * Final cache:
         *
         * A, C, D
         */

        try {
            cache.get("B");
        } catch (Exception e) {
            System.out.println("B was evicted.");
        }

        System.out.println(
                "A = " + cache.get("A"));

        System.out.println(
                "C = " + cache.get("C"));

        System.out.println(
                "D = " + cache.get("D"));

        /*
         * To switch to FIFO:
         *
         * EvictionAlgorithm<String> evictionPolicy =
         *         new FIFOEvictionAlgorithm<>();
         *
         * Nothing inside Cache needs to change.
         */
    }
}
