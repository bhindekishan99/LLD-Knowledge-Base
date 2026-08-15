# Cache Class Diagram

```mermaid
classDiagram

    %% =========================
    %% CACHE
    %% =========================

    class Cache {
        -CacheStorage cacheStorage
        -DBStorage dbStorage
        -WritePolicy writePolicy
        -EvictionAlgorithm evictionAlgorithm

        +get(key) value
        +put(key, value) void
        +getFromDB(key) value
    }


    %% =========================
    %% CACHE STORAGE
    %% =========================

    class CacheStorage {
        <<interface>>

        +put(key, value) void
        +get(key) value
        +remove(key) void
        +containsKey(key) boolean
        +size() int
        +getCapacity() int
    }

    class InMemoryCacheStorage {
        -Map cache
        -int capacity

        +put(key, value) void
        +get(key) value
        +remove(key) void
        +containsKey(key) boolean
        +size() int
        +getCapacity() int
    }

    CacheStorage <|.. InMemoryCacheStorage


    %% =========================
    %% DATABASE STORAGE
    %% =========================

    class DBStorage {
        <<interface>>

        +write(key, value) void
        +read(key) value
        +delete(key) void
    }

    class SimpleDBStorage {
        -Map database

        +write(key, value) void
        +read(key) value
        +delete(key) void
    }

    DBStorage <|.. SimpleDBStorage


    %% =========================
    %% WRITE POLICY
    %% =========================

    class WritePolicy {
        <<interface>>

        +write(key, value, cacheStorage, dbStorage) void
    }

    class WriteThroughPolicy {
        +write(key, value, cacheStorage, dbStorage) void
    }

    WritePolicy <|.. WriteThroughPolicy


    %% =========================
    %% EVICTION POLICY
    %% =========================

    class EvictionAlgorithm {
        <<interface>>

        +existingKeyAccessed(key) void
        +addNewKey(key) void
        +evictKey() key
    }

    class LRUEvictionAlgorithm {
        -DoublyLinkedList list
        -Map keyToNode

        +existingKeyAccessed(key) void
        +addNewKey(key) void
        +evictKey() key
    }

    class FIFOEvictionAlgorithm {
        -Queue queue

        +existingKeyAccessed(key) void
        +addNewKey(key) void
        +evictKey() key
    }

    EvictionAlgorithm <|.. LRUEvictionAlgorithm
    EvictionAlgorithm <|.. FIFOEvictionAlgorithm


    %% =========================
    %% LRU DATA STRUCTURE
    %% =========================

    class DoublyLinkedList {
        -DoublyLinkedListNode head
        -DoublyLinkedListNode tail

        +addAtTail(node) void
        +detach(node) void
        +getHead() node
        +removeHead() void
    }

    class DoublyLinkedListNode {
        -value
        -prev
        -next

        +getValue() value
    }

    LRUEvictionAlgorithm --> DoublyLinkedList : uses
    LRUEvictionAlgorithm --> DoublyLinkedListNode : keyToNode
    DoublyLinkedList --> DoublyLinkedListNode : manages


    %% =========================
    %% CACHE RELATIONSHIPS
    %% =========================

    Cache --> CacheStorage : uses
    Cache --> DBStorage : uses
    Cache --> WritePolicy : uses
    Cache --> EvictionAlgorithm : uses
```

## Design Overview

```mermaid
flowchart TD

    Client --> Cache

    Cache --> CacheStorage
    Cache --> DBStorage
    Cache --> WritePolicy
    Cache --> EvictionAlgorithm

    CacheStorage --> InMemoryCacheStorage

    DBStorage --> SimpleDBStorage

    WritePolicy --> WriteThroughPolicy

    EvictionAlgorithm --> LRUEvictionAlgorithm
    EvictionAlgorithm --> FIFOEvictionAlgorithm

    LRUEvictionAlgorithm --> DoublyLinkedList
    LRUEvictionAlgorithm --> DoublyLinkedListNode
```

## Eviction Strategy

```text
                 EvictionAlgorithm
                  <<interface>>
                        |
              +---------+---------+
              |                   |
              ▼                   ▼
     LRUEvictionAlgorithm   FIFOEvictionAlgorithm
              |
              |
              ▼
      DoublyLinkedList
              |
              ▼
    DoublyLinkedListNode
```

### EvictionAlgorithm Responsibilities

```text
existingKeyAccessed(key)
    → Existing key was accessed

addNewKey(key)
    → New key entered the cache

evictKey()
    → Cache is full
    → Return key that should be removed
```

### LRU

```text
LRUEvictionAlgorithm
        |
        +-- HashMap<Key, Node>
        |
        +-- DoublyLinkedList
```

### FIFO

```text
FIFOEvictionAlgorithm
        |
        +-- Queue<Key>
```

## Strategy Pattern

```text
Cache
 |
 +---- WritePolicy
 |        |
 |        +---- WriteThroughPolicy
 |
 +---- EvictionAlgorithm
          |
          +---- LRUEvictionAlgorithm
          |
          +---- FIFOEvictionAlgorithm
```

`Cache` depends on the **interfaces**, so the eviction strategy can be changed without modifying `Cache`.
