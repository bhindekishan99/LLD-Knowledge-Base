import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

class BoundedBlockingQueue {

    private final int capacity;

    // Number of elements currently available to dequeue
    private final Semaphore full;

    // Number of empty spaces available for enqueue
    private final Semaphore empty;

    // LinkedList is NOT thread-safe by itself
    private final Queue<Integer> queue;

    // Lock used to protect LinkedList operations
    private final Object lock = new Object();

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;

        // Initially queue is empty
        full = new Semaphore(0);

        // Initially all positions are empty
        empty = new Semaphore(capacity);

        queue = new LinkedList<>();
    }

    // Add an element
    public void enqueue(int element) throws InterruptedException {

        // Wait until there is an empty slot
        empty.acquire();

        try {
            // LinkedList is not thread-safe,
            // so protect the modification.
            synchronized (lock) {
                queue.add(element);
            }

            // One more element is now available
            full.release();

        } catch (RuntimeException e) {
            // If adding fails, return the empty slot
            empty.release();
            throw e;
        }
    }

    // Remove an element
    public int dequeue() throws InterruptedException {

        // Wait until at least one element is available
        full.acquire();

        try {
            int result;

            // Protect LinkedList modification
            synchronized (lock) {
                result = queue.remove();
            }

            // One more empty slot is now available
            empty.release();

            return result;

        } catch (RuntimeException e) {
            // If removing fails, return the full permit
            full.release();
            throw e;
        }
    }

    // Get current queue size
    public int size() {

        synchronized (lock) {
            return queue.size();
        }
    }
}
