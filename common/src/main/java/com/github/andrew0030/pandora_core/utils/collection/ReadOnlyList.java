package com.github.andrew0030.pandora_core.utils.collection;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ReadOnlyList<T> implements List<T> {
    private final List<T> backing;

    public ReadOnlyList(List<T> backend) {
        this.backing = backend;
    }

    @Override
    public int size() {
        return backing.size();
    }

    @Override
    public boolean isEmpty() {
        return backing.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backing.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return backing.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return backing.toArray();
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return backing.toArray(a);
    }

    @Override
    public boolean add(T t) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return backing.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void clear() {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public Stream<T> stream() {
        return backing.stream();
    }

    @Override
    public Stream<T> parallelStream() {
        return backing.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        backing.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return backing.spliterator();
    }

    @Override
    public <T1> T1[] toArray(IntFunction<T1[]> generator) {
        return backing.toArray(generator);
    }

    @Override
    public boolean removeIf(Predicate<? super T> filter) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public T get(int index) {
        return backing.get(index);
    }

    @Override
    public T set(int index, T element) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void add(int index, T element) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public T remove(int index) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public int indexOf(Object o) {
        return backing.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backing.lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return new ReadOnlyListIterator<>(backing.listIterator());
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return new ReadOnlyListIterator<>(backing.listIterator(index));
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new ReadOnlyList<>(backing.subList(fromIndex, toIndex));
    }

    @Override
    public void replaceAll(UnaryOperator<T> operator) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void sort(Comparator<? super T> c) {
        throw new RuntimeException("Unsupported");
    }
}
