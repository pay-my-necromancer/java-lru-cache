package com.example.lrucache;

import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== LRU Cache with TTL Demo ===");
        
        // Создаем кеш на 3 элемента с TTL = 2 секунды
        LRUCache<String, String> cache = new LRUCache<>(3, 2, TimeUnit.SECONDS);
        
        // Добавляем элементы
        cache.put("1", "Apple");
        cache.put("2", "Banana");
        cache.put("3", "Cherry");
        
        System.out.println("\nInitial cache state:");
        printCache(cache);
        
        // Доступ к элементу делает его "недавно использованным"
        System.out.println("\nAccessing key '2': " + cache.get("2"));
        printCache(cache);
        
        // Добавляем новый элемент (вытеснит "1")
        System.out.println("\nAdding '4': Dragonfruit");
        cache.put("4", "Dragonfruit");
        printCache(cache);
        
        // Ждем истечения TTL
        System.out.println("\nWaiting for TTL expiration (3 seconds)...");
        Thread.sleep(3000);
        
        System.out.println("\nAfter TTL expiration:");
        printCache(cache);
        
        // Добавляем новые элементы
        System.out.println("\nAdding fresh items:");
        cache.put("5", "Elderberry");
        cache.put("6", "Fig");
        printCache(cache);
    }
    
    private static void printCache(LRUCache<String, String> cache) {
        System.out.println("Cache size: " + cache.size());
        for (int i = 1; i <= 6; i++) {
            String key = String.valueOf(i);
            System.out.printf("Key %s: %s%n", key, cache.get(key));
        }
    }
}