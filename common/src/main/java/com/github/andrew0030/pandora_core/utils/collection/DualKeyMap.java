package com.github.andrew0030.pandora_core.utils.collection;

import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DualKeyMap<T, U, V> implements Map<Pair<T, U>, V> {
    final class Key {
        T l;
        U r;
        int hash;
        final Class<?> clz = Key.class;

        public Key(T l, U r) {
            this.l = l;
            this.r = r;
            hash = Objects.hash(l, r);
        }

        public Key set(T l, U r) {
            this.l = l;
            this.r = r;
            hash = Objects.hash(l, r);
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || (o.getClass() != clz)) return false;
            //noinspection unchecked
            Key key = (Key) o;
            return Objects.equals(l, key.l) && Objects.equals(r, key.r);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    Map<Key, V> backend;
    Key interm = new Key(null, null);

    public DualKeyMap(Map<Key, V> backend) {
        this.backend = backend;
    }

    @Override
    public int size() {
        return backend.size();
    }

    @Override
    public boolean isEmpty() {
        return backend.isEmpty();
    }

    @Override
    public boolean containsKey(Object keyA) {
        Pair<T, U> key = (Pair<T, U>) keyA;
        return backend.containsKey(interm.set(key.getFirst(), key.getSecond()));
    }

    public boolean containsKey(T keyL, U keyR) {
        return backend.containsKey(interm.set(keyL, keyR));
    }

    @Override
    public boolean containsValue(Object value) {
        return backend.containsValue(value);
    }

    @Override
    public V get(Object keyA) {
        Pair<T, U> key = (Pair<T, U>) keyA;
        return backend.get(interm.set(key.getFirst(), key.getSecond()));
    }

    public V get(T keyL, U keyR) {
        return backend.get(interm.set(keyL, keyR));
    }

    public V getOrDefault(T keyL, U keyR, V defaultV) {
        return backend.getOrDefault(interm.set(keyL, keyR), defaultV);
    }

    @Override
    public void clear() {
        backend.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DualKeyMap<?, ?, ?> map2D = (DualKeyMap<?, ?, ?>) o;
        return Objects.equals(backend, map2D.backend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(backend);
    }

    @Nullable
    @Override
    public V put(Pair<T, U> key, V value) {
        return backend.put(new Key(key.getFirst(), key.getSecond()), value);
    }

    @Nullable
    public V put(T keyL, U keyR, V value) {
        return backend.put(new Key(keyL, keyR), value);
    }

    @Override
    public V remove(Object keyA) {
        Pair<T, U> key = (Pair<T, U>) keyA;
        return backend.remove(interm.set(key.getFirst(), key.getSecond()));
    }

    public V removeKey(T keyL, U keyR) {
        return backend.remove(interm.set(keyL, keyR));
    }

    @Override
    public void putAll(@NotNull Map<? extends Pair<T, U>, ? extends V> m) {
//        backend.putAll(m);
        throw new RuntimeException("NYI");
    }

    @NotNull
    @Override
    public Set<Pair<T, U>> keySet() {
//        return backend.keySet();
        throw new RuntimeException("NYI");
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return backend.values();
    }

    @NotNull
    @Override
    public Set<Entry<Pair<T, U>, V>> entrySet() {
//        return backend.entrySet();
        throw new RuntimeException("NYI");
    }
}
