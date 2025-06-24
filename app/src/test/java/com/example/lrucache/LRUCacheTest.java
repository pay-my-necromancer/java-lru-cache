package com.example.lrucache;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;

class LRUCacheTest {
    private LRUCache<String, Integer> cache;

    @BeforeEach
    void setUp() {
        cache = new LRUCache<>(3, 1, TimeUnit.SECONDS);
    }

    @Test
    void testBasicOperations() {
        cache.put("A", 1);
        assertEquals(1, cache.get("A"));
        assertNull(cache.get("B"));
    }

    @Test
    void testEvictionPolicy() {
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        cache.put("D", 4); // Should evict "A"
        
        assertNull(cache.get("A"));
        assertEquals(4, cache.get("D"));
    }

    @Test
    void testTTLExpiration() throws InterruptedException {
        cache.put("A", 1);
        Thread.sleep(1500); // More than TTL
        assertNull(cache.get("A"));
    }

    @Test
    void testAccessOrder() {
        cache.put("A", 1);
        cache.put("B", 2);
        cache.put("C", 3);
        
        // Access "A" to make it recently used
        cache.get("A");
        cache.put("D", 4); // Should evict "B" not "A"
        
        assertNotNull(cache.get("A"));
        assertNull(cache.get("B"));
    }

    @Test
    void testNullChecks() {
        assertThrows(IllegalArgumentException.class, () -> cache.put(null, 1));
        assertThrows(IllegalArgumentException.class, () -> cache.put("A", null));
    }

    @Test
    void testClear() {
        cache.put("A", 1);
        cache.clear();
        assertEquals(0, cache.size());
        assertNull(cache.get("A"));
    }
}