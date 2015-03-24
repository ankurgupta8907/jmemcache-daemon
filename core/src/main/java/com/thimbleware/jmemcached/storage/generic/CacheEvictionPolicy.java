package com.thimbleware.jmemcached.storage.generic;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by ankurgupta on 3/21/15.
 */
public interface CacheEvictionPolicy <K, V> {
    
    public boolean put(K key, ConcurrentMap<K, V> data);
    
    public boolean onAccess(K key, ConcurrentMap<K, V> data);
    
    public boolean remove(K key, ConcurrentMap<K, V> data);
    
    public K evict(ConcurrentMap<K, V> data);
}
