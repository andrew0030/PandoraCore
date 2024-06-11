package com.github.andrew0030.pandora_core.utils.collection;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.function.Consumer;

public class ReadOnlyListIterator<T> implements ListIterator<T> {
    private final ListIterator<T> backing;

    public ReadOnlyListIterator(ListIterator<T> backing) {
        this.backing = backing;
    }

    @Override
    public boolean hasPrevious() {
        return backing.hasPrevious();
    }

    @Override
    public T previous() {
        return backing.previous();
    }

    @Override
    public int nextIndex() {
        return backing.nextIndex();
    }

    @Override
    public int previousIndex() {
        return backing.previousIndex();
    }

    @Override
    public void set(T t) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void add(T t) {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public boolean hasNext() {
        return backing.hasNext();
    }

    @Override
    public T next() {
        return backing.next();
    }

    @Override
    public void remove() {
        throw new RuntimeException("Unsupported");
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        backing.forEachRemaining(action);
    }
}
