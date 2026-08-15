# Cache System - Class Diagram

```mermaid
classDiagram

    %% =========================
    %% Storage Layer
    %% =========================

    class CacheStorage~K,V~ {
        <<interface>>
        +put(K key, V value) void
        +get(K key) V
        +delete(K key) void
    }

    class DBStorage~K,V~ {
        <<interface>>
        +write(K key, V value) void
        +read(K key) V
        +delete(K key) void
    }


    %% =========================
    %% Cache Storage Implementation
    %% =========================

    class InMemoryCacheStorage~K,V~ {
        -ConcurrentHashMap~K,V~ cache
        +put(K key, V value) void
        +get(K key) V
        +delete(K key) void
    }

    CacheStorage <|.. InMemoryCacheStorage


    %% =========================
    %% Write Policies
    %% =========================

    class WritePolicy~K,V~ {
        <<interface>>
        +write(K key, V value, CacheStorage cacheStorage, DBStorage dbStorage) void
    }

    class WriteThroughPolicy~K,V~ {
        +write(K key, V value, CacheStorage cacheStorage, DBStorage dbStorage) void
    }

    class WriteBackPolicy~K,V~ {
        +write(K key, V value, CacheStorage cacheStorage, DBStorage dbStorage) void
    }

    class WriteAroundPolicy~K,V~ {
        +write(K key, V value, CacheStorage cacheStorage, DBStorage dbStorage) void
    }

    WritePolicy <|.. WriteThroughPolicy
    WritePolicy <|.. WriteBackPolicy
    WritePolicy <|.. WriteAroundPolicy


    %% =========================
    %% Cache
    %% =========================

    class Cache~K,V~ {
        -CacheStorage~K,V~ cacheStorage
        -DBStorage~K,V~ dbStorage
        -WritePolicy~K,V~ writePolicy

        +get(K key) V
        +put(K key, V value) void
        +delete(K key) void
    }

    Cache --> CacheStorage : uses
    Cache --> DBStorage : uses
    Cache --> WritePolicy : uses


    %% =========================
    %% Relationships
    %% =========================

    WriteThroughPolicy --> CacheStorage : writes
    WriteThroughPolicy --> DBStorage : writes

    WriteBackPolicy --> CacheStorage : writes
    WriteBackPolicy --> DBStorage : async write

    WriteAroundPolicy --> DBStorage : writes
