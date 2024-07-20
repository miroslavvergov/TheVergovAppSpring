package com.project.thevergov.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * CacheStore: A generic caching utility class using Google's Guava library.
 * This class manages the lifecycle of cache entries, including insertion, retrieval, and eviction.
 *
 * @param <K> the type of keys maintained by this cache
 * @param <V> the type of mapped values
 */
@Slf4j
public class CacheStore<K, V> {

    // Cache instance to store key-value pairs
    private final Cache<K, V> cache;

    /**
     * Constructs a CacheStore instance with specified expiration duration.
     *
     * @param expiryDuration the duration after which an entry should expire
     * @param timeUnit the time unit for the expiration duration
     */
    public CacheStore(int expiryDuration, TimeUnit timeUnit) {
        // Initialize the cache with given expiry duration and concurrency level
        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(expiryDuration, timeUnit)
                .concurrencyLevel(Runtime.getRuntime().availableProcessors())
                .build();
    }

    /**
     * Retrieves a value from the cache.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this cache contains no mapping for the key
     */
    public V get(@NotNull K key) {
        log.info("Retrieving from Cache with key {}", key.toString());
        return cache.getIfPresent(key);
    }

    /**
     * Puts a key-value pair into the cache.
     *
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    public void put(@NotNull K key, @NotNull V value) {
        log.info("Storing record in Cache for key {}", key.toString());
        cache.put(key, value);
    }

    /**
     * Evicts a key-value pair from the cache.
     *
     * @param key the key whose mapping is to be removed from the cache
     */
    public void evict(@NotNull K key) {
        log.info("Removing from Cache with key {}", key.toString());
        cache.invalidate(key);
    }
}
