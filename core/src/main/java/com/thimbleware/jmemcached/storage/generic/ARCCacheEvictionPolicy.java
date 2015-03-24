package com.thimbleware.jmemcached.storage.generic;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by ankurgupta on 3/21/15.
 */
public class ARCCacheEvictionPolicy<K, V> implements CacheEvictionPolicy<K, V>{
    
    private Deque<K> T1;
    private Deque<K> B1;
    private Deque<K> T2;
    private Deque<K> B2;
    private int p;
    private int maxCapacity;
    
    public ARCCacheEvictionPolicy(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.p = 0;
        this.T1 = new LinkedList<K>();
        this.T2 = new LinkedList<K>();
        this.B1 = new LinkedList<K>();
        this.B2 = new LinkedList<K>();
    }



    private void replace(K key, ConcurrentMap<K, V> data) {
        if (T1.size() > 0 && ((B2.contains(key) && T1.size() == p) || T1.size() > p)) {
            K removedKey = T1.removeFirst();
            B1.addLast(removedKey);
            data.remove(removedKey);
        }
        else {
            K removedKey = T2.removeFirst();
            B2.addLast(removedKey);
            data.remove(removedKey);
        }
    }
    
    @Override
    public boolean put(K key, ConcurrentMap<K, V> data) {
        if (B1.contains(key)) {
            p = Math.min(maxCapacity, p + Math.max(B2.size() / B1.size(), 1));
            replace(key, data);
            B1.remove(key);
            T2.addLast(key);
            return true;
        }
        else if (B2.contains(key)) {
            p = Math.max(0, p - Math.max(B1.size() / B2.size(), 1));
            replace(key, data);
            B2.remove(key);
            T2.addLast(key);
            return true;
        }
        else {
            int L1Size = T1.size() + B1.size();
            int L2Size = T2.size() + B2.size();
            
            if (L1Size == maxCapacity) {
                if (T1.size() < maxCapacity) {
                    B1.removeFirst();
                    replace(key, data);
                }
                else {
                    K removedKey = T1.removeFirst();
                    data.remove(removedKey);
                }
            }
            else if (L1Size < maxCapacity && L1Size + L2Size >= maxCapacity){
                if (L1Size + L2Size == 2*maxCapacity) {
                    B2.removeFirst();
                }
                replace(key, data);
            }
            T1.addLast(key);
            return true;
        }
    }

    @Override
    public boolean onAccess(K key, ConcurrentMap<K, V> data) {
        if (T1.contains(key)) {
            T1.remove(key);
            T2.addLast(key);
            return true;
        }
        else if (T2.contains(key)) {
            T2.remove(key);
            T2.addLast(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(K key, ConcurrentMap<K, V> data) {
        // No remove provision for this cache eviction policy.
        return false;
    }

    @Override
    public K evict(ConcurrentMap<K, V> data) {
        return null;
    }
}
