package com.thimbleware.jmemcached.storage.generic;

import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.SizedItem;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ankurgupta on 3/21/15.
 */
public final class GenericCacheEvictionHashMap<K, V extends SizedItem> implements Serializable, CacheStorage<K, V> {

    private ConcurrentMap<K, V> data;
    private CacheEvictionPolicy<K, V> evictionPolicy;
    private int capacity;
    private Lock lock;

    public GenericCacheEvictionHashMap(int maximumCapacity) {
        data = new ConcurrentHashMap<K, V>(maximumCapacity, 0.75f, 16);
        this.evictionPolicy = new LRUCacheEvictionPolicy<K, V>(maximumCapacity);
        this.capacity = maximumCapacity;
        this.lock = new ReentrantLock();
    }
    
    @Override
    public V get(Object keyObject) {
        if (data.containsKey(keyObject)) {
            K key = (K) keyObject;
            evictionPolicy.onAccess(key, data);
            return data.get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        this.lock.lock();
        K deletedKey = evictionPolicy.evict(data);
        if (deletedKey != null) {
            data.remove(deletedKey);
        }
        this.lock.unlock();

        this.lock.lock();
        evictionPolicy.put(key, data);
        data.put(key, value);
        this.lock.unlock();

        return value;
    }

    @Override
    public V remove(Object key) {
        this.lock.lock();
        V val = data.remove(key);
        evictionPolicy.remove((K) key, data);
        this.lock.unlock();
        return val;
    }
    
    @Override
    public long getMemoryCapacity() {
        return 0;
    }

    @Override
    public long getMemoryUsed() {
        return 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        return false;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return false;
    }

    @Override
    public V replace(K key, V value) {
        return null;
    }
}
