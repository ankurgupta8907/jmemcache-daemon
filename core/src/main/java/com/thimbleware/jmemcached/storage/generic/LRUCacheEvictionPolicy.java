package com.thimbleware.jmemcached.storage.generic;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ankurgupta on 3/21/15.
 */
public class LRUCacheEvictionPolicy<K, V> implements CacheEvictionPolicy<K, V> {
    
    private Deque<K> keyList;
    private int capacity;
    
    public LRUCacheEvictionPolicy(int capacity) {
        keyList = new LinkedList<K>();
        this.capacity = capacity;
    }
    @Override
    public boolean put(K key, ConcurrentMap<K, V> data) {
        keyList.addLast(key);
        return true;
    }

    @Override
    public boolean onAccess(K key, ConcurrentMap<K, V> data) {
        keyList.remove(key);
        keyList.addLast(key);
        return true;
    }

    @Override
    public boolean remove(K key, ConcurrentMap<K, V> data) {
        keyList.remove(key);
        return true;
    }

    @Override
    public K evict(ConcurrentMap<K, V> data) {
        if (keyList.size() == this.capacity) {
            return keyList.removeFirst();
        }
        return null;
    }
}
