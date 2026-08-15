# Cache — Supported Functionality

- **Get**
  - Fetch value from cache.
  - On cache miss, fetch value from DB and add it to cache.

- **Put**
  - Add a new key to the cache.
  - Update an existing key.

- **Cache Eviction**
  - Evict a key when the cache reaches its capacity.

- **LRU Eviction**
  - Evict the least recently used key.

- **FIFO Eviction**
  - Evict the oldest inserted key.

- **Write-Through**
  - Write data to both cache and DB.

- **Pluggable Policies**
  - Write policy can be changed independently.
  - Eviction policy can be changed independently.

- **Thread Safety**
  - Cache operations are synchronized.
  - Eviction metadata is also updated safely.
  - See in extensions.md
  - 1. Cache with TTL
  - 2. Cache with hit ration
  - 3. Striped locking (Sharding)
  
