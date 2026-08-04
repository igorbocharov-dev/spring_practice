package com.practice.spring.storage;

import java.util.Collection;

public interface Storage<K, V> {

    Collection<V> values();

    V put(K key, V value);

    V get(K key);

    V replace(K key, V value);

    V remove(K key);

    int size();
}
