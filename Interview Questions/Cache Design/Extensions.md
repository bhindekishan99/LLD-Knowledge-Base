# Cache — TTL, Hit Ratio & Concurrency

## 1. Cache with TTL Expiry

```text
Cache
 |
 +-- CacheStorage
 |      └── Map<Key, CacheData>
 |              |
 |              └── CacheData {
 |                      value
 |                      expirationTime
 |                  }
 |
 +-- ExpirationService
 |      └── removes expired entries
 |              |
 |              └── also removes key from EvictionPolicy
 |
 +-- EvictionPolicy
 |      └── used when cache is still full
 |
 +-- WritePolicy
        └── controls Cache + DB writes
```

### TTL Flow

```text
put(key, value)
      |
      ↓
expirationTime = currentTime + TTL
      |
      ↓
store CacheData in cache
```

### Get Flow

```text
get(key)
    |
    ↓
key exists?
    |
    +-- NO → get from DB
    |
    +-- YES
          |
          ↓
       expired?
        /    \
      YES     NO
       |       |
    remove   return value
       |
       ↓
   get from DB
       |
       ↓
   add to cache
```

## 2. Cache Hit Ratio

Maintain:

```java
private long hits;
private long misses;
```

### `get()`

```java
public synchronized V get(K key) {

    Node<K, V> node = map.get(key);

    if (node == null) {
        misses++;
        return null;
    }

    hits++;

    list.moveToFront(node);

    return node.value;
}
```

### Hit Ratio

```java
public synchronized double hitRatio() {

    long total = hits + misses;

    return total == 0
            ? 0.0
            : (double) hits / total;
}
```

## 3. Global Lock Bottleneck

If `get()` and `put()` are synchronized:

```java
public synchronized V get(...)
public synchronized void put(...)
```

only **one thread can execute a cache operation at a time**.

### Why can't we simply use a read lock? 

ReadLock: It is managed through the ReadWriteLock interface and implemented by the ReentrantReadWriteLock class in the java.util.concurrent.locks package.

#### Key Features:
```Text
Shared Access: Many reader threads can hold the read lock at the exact same time.

Mutual Exclusion with Writers: If a thread wants to write (acquire a write lock), it must wait until all active read locks are released.
Conversely, if a write lock is active, no threads can acquire a read lock.

Use Case: It optimizes performance in read-heavy systems where data changes infrequently but is read constantly.
```

### Code Example

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheSystem {
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private String sharedData = "Initial Data";

    public String readData() {
        // Acquire the read lock
        rwLock.readLock().lock(); 
        try {
            // Multiple threads can execute this block at the same time
            return sharedData; 
        } finally {
            // Always release the lock in the finally block
            rwLock.readLock().unlock(); 
        }
    }

    public void writeData(String newData) {
        // Acquire the exclusive write lock
        rwLock.writeLock().lock(); 
        try {
            this.sharedData = newData;
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}
```

Because LRU `get()` is not purely a read.

It also modifies the LRU list:

```text
A → B → C

get(A)

B → C → A
          ↑
         MRU
```

Therefore, `get()` also needs exclusive access to the LRU structure.

### Problem with global lock

```text
Thread 1 → user_1
Thread 2 → user_9999
```

Even though the keys are unrelated:

```text
Thread 1
    ↓
Global Lock
    ↓
working on user_1

Thread 2
    ↓
WAIT
```

### Result

- Correct values
- Poor scalability
- Threads wait for the global lock
- Adding more CPU cores does not improve throughput significantly

## 4. Striped Locking

It is a computer programming technique that divides a large data structure into smaller, independent partitions
Partition the cache into `N` independent shards.

Each shard has independent cache(like shards) it self

```text
                         Cache
                           |
                     hash(key) % N
                           |
            +--------------+--------------+
            |              |              |
            ↓              ↓              ↓
       +---------+    +---------+    +---------+
       | Shard 0 |    | Shard 1 |    | Shard 2 |
       |---------|    |---------|    |---------|
       | Map     |    | Map     |    | Map     |
       | LRU     |    | LRU     |    | LRU     |
       | Lock    |    | Lock    |    | Lock    |
       +---------+    +---------+    +---------+
```

### Key Routing

```text
Key
 ↓
hash(key) % N
 ↓
Shard
```

Keys mapped to different shards can operate in parallel:

```text
Thread 1 → Shard 0 → Lock 0
Thread 2 → Shard 2 → Lock 2
```

### Code

```java
public class StripedLRUCache<K, V> {

    private final LRUCache<K, V>[] shards;

    @SuppressWarnings("unchecked")
    public StripedLRUCache(
            int shardCount,
            int perShardCapacity) {

        shards = new LRUCache[shardCount];

        for (int i = 0; i < shardCount; i++) {
            shards[i] =
                    new LRUCache<>(perShardCapacity);
        }
    }

    private LRUCache<K, V> shardFor(K key) {

        int index =
                (key.hashCode() & 0x7fffffff)
                        % shards.length;

        return shards[index];
    }

    public V get(K key) {
        return shardFor(key).get(key);
    }

    public void put(K key, V value) {
        shardFor(key).put(key, value);
    }
}
```

### Trade-off

With one global LRU:

```text
One global LRU order
```

With striped locking:

```text
Shard 0 → own LRU
Shard 1 → own LRU
Shard 2 → own LRU
```

Therefore, eviction becomes **per-shard instead of globally LRU**.

This improves concurrency but only approximates global LRU ordering.

## Key Takeaway

```text
Global Lock
    ↓
Simple and provides Correctness
    ↓
Poor scalability

Striped Locking
    ↓
Multiple independent shards
    ↓
Different keys can execute in parallel with correctness
    ↓
Better scalability
    ↓
Eviction becomes per-shard
```
