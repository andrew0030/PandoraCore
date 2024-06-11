package com.github.andrew0030.pandora_core.utils.collection;

import java.util.Iterator;
import java.util.function.Consumer;

public class ReadOnlyIterator<T> implements Iterator<T> {
    private final Iterator<T> backing;

    public ReadOnlyIterator(Iterator<T> backing) {
        this.backing = backing;
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
