package com.example.lrucache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LRUCache<K, V> {
    private final int capacity;
    private final long ttlMillis;
    private final Map<K, Node<K, V>> cache;
    private Node<K, V> head;
    private Node<K, V> tail;

    private static class Node<K, V> {
        K key;
        V value;
        long expirationTime;
        Node<K, V> prev;
        Node<K, V> next;

        Node(K key, V value, long ttlMillis) {
            this.key = key;
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }

    public LRUCache(int capacity, long ttl, TimeUnit timeUnit) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.ttlMillis = timeUnit.toMillis(ttl);
        this.cache = new HashMap<>(capacity);
    }

    public synchronized V get(K key) {
        Node<K, V> node = cache.get(key);
        if (node == null) return null;
        
        if (node.isExpired()) {
            removeNode(node);
            cache.remove(key);
            return null;
        }
        
        moveToHead(node);
        return node.value;
    }

    public synchronized void put(K key, V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("Key and value cannot be null");
        }

        Node<K, V> node = cache.get(key);
        if (node != null) {
            node.value = value;
            node.expirationTime = System.currentTimeMillis() + ttlMillis;
            moveToHead(node);
            return;
        }

        if (cache.size() >= capacity) {
            evictExpiredEntries();
            if (cache.size() >= capacity) {
                evictLRU();
            }
        }

        Node<K, V> newNode = new Node<>(key, value, ttlMillis);
        cache.put(key, newNode);
        addToHead(newNode);
    }

    private void evictExpiredEntries() {
        Node<K, V> current = tail;
        while (current != null) {
            Node<K, V> prev = current.prev;
            if (current.isExpired()) {
                removeNode(current);
                cache.remove(current.key);
            }
            current = prev;
        }
    }

    private void evictLRU() {
        if (tail != null) {
            cache.remove(tail.key);
            removeNode(tail);
        }
    }

    private void addToHead(Node<K, V> node) {
        node.next = head;
        node.prev = null;
        
        if (head != null) {
            head.prev = node;
        }
        head = node;
        
        if (tail == null) {
            tail = head;
        }
    }

    private void removeNode(Node<K, V> node) {
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
    }

    private void moveToHead(Node<K, V> node) {
        removeNode(node);
        addToHead(node);
    }

    public synchronized int size() {
        return cache.size();
    }

    public synchronized void clear() {
        cache.clear();
        head = tail = null;
    }
}