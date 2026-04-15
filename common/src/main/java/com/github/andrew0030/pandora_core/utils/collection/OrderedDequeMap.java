package com.github.andrew0030.pandora_core.utils.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A {@code map} that maintains insertion/deque order, while providing {@code O(1)} key lookups.
 * Uses a {@link HashMap} for fast lookups and {@link ArrayDeque} for fast ends.
 */
public class OrderedDequeMap<K, V> {
    private final Map<K, V> map = new HashMap<>();
    private final Deque<K> deque = new ArrayDeque<>();

    // ##########################
    // Core Map Operations (O(1))
    // ##########################
    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     */
    @Nullable
    public V get(@NotNull K key) {
        return this.map.get(key);
    }

    /**
     * Returns {@code true} if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(@NotNull K key) {
        return this.map.containsKey(key);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return this.map.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public void clear() {
        this.map.clear();
        this.deque.clear();
    }

    // ######################################################
    // Deque Operations (O(1) ~ With rare cases being slower)
    // ######################################################
    /**
     * Inserts the specified element at the end of this map.
     * <p>If {@code key} exists, moves it to the end.
     * <p><strong>Complexity:</strong><ul>
     *     <li> {@code O(1)} if {@code key} is new</li>
     *     <li> {@code O(n)} if {@code key} exists</li>
     * </ul>
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws NullPointerException if the specified key is null
     */
    public void putLast(@NotNull K key, V value) {
        this.removeKeyFromOrder(key); // O(n) in deque, but keeps order consistent
        this.deque.addLast(key);
        this.map.put(key, value);
    }

    /**
     * Inserts the specified element at the front of this map.
     * <p>If {@code key} exists, moves it to the front.
     * <p><strong>Complexity:</strong><ul>
     *     <li> {@code O(1)} if {@code key} is new</li>
     *     <li> {@code O(n)} if {@code key} exists</li>
     * </ul>
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @throws NullPointerException if the specified key is null
     */
    public void putFirst(@NotNull K key, V value) {
        this.removeKeyFromOrder(key); // O(n) in deque, but keeps order consistent
        this.deque.addFirst(key);
        this.map.put(key, value);
    }

    /**
     * Retrieves the first {@code value} of the map, or
     * returns {@code null} if this map is empty.
     *
     * @return the first {@code value} of the map, or {@code null} if this map is empty.
     */
    @Nullable
    public V getFirst() {
        K key = this.deque.peekFirst();
        return key != null ? this.map.get(key) : null;
    }

    /**
     * Retrieves the last {@code value} of the map, or
     * returns {@code null} if this map is empty.
     *
     * @return the last {@code value} of the map, or {@code null} if this map is empty.
     */
    @Nullable
    public V getLast() {
        K key = this.deque.peekLast();
        return key != null ? this.map.get(key) : null;
    }

    /**
     * Retrieves and removes the first {@code value} of the map,
     * or returns {@code null} if this map is empty.
     *
     * @return the first {@code value} of the map, or {@code null} if this map is empty.
     */
    @Nullable
    public V pollFirst() {
        K key = this.deque.pollFirst();
        return key != null ? this.map.remove(key) : null;
    }

    /**
     * Retrieves and removes the last {@code value} of the map,
     * or returns {@code null} if this map is empty.
     *
     * @return the last {@code value} of the map, or {@code null} if this map is empty.
     */
    @Nullable
    public V pollLast() {
        K key = this.deque.pollLast();
        return key != null ? this.map.remove(key) : null;
    }

    // #########################################
    // Removal by Key (O(n) ~ Due to order sync)
    // #########################################
    /**
     * Removes the mapping for the specified {@code key} from this map if present.
     *
     * @param  key key whose mapping is to be removed from the map
     * @return the previous value associated with {@code key}, or
     *         {@code null} if there was no mapping for {@code key}.
     *         (A {@code null} return can also indicate that the map
     *         previously associated {@code null} with {@code key}.)
     */
    @Nullable
    public V remove(@NotNull K key) {
        this.removeKeyFromOrder(key);
        return this.map.remove(key);
    }

    private void removeKeyFromOrder(K key) {
        this.deque.remove(key);
    }

    // #################
    // Ordered Iteration
    // #################
    /**
     * Returns {@code values} in deque order.
     *
     * @return {@code values} in deque order.
     */
    @NotNull
    public Iterable<V> valuesInOrder() {
        return () -> new Iterator<>() {
            private final Iterator<K> keyIter = deque.iterator();
            @Override public boolean hasNext() { return keyIter.hasNext(); }
            @Override public V next() { return map.get(keyIter.next()); }
        };
    }

    /**
     * Returns {@code keys} in deque order.
     *
     * @return {@code keys} in deque order.
     */
    @NotNull
    public Iterable<K> keysInOrder() {
        return this.deque;
    }
}