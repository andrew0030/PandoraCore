package com.github.andrew0030.pandora_core.utils.collection;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

// TODO: not sure if it's better to do this or just an array
//       if array is better, it should only be for memory reasons
public class CyclicStack<T> implements Iterable<T> {
    protected boolean start;
    protected boolean end;
    protected CyclicStack<T> next;
    public final T value;

    public CyclicStack(T value, boolean start) {
        this.value = value;
        this.start = start;
    }

    public void link(CyclicStack<T> next) {
        if (this.next != null) throw new RuntimeException("Cannot update cycle of cyclic stack.");
        if (next.start) this.end = true;
        this.next = next;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isEnd() {
        return end;
    }

    public CyclicStack<T> getNext() {
        return next;
    }

    @Override
    public Iterator<T> iterator() {
        return new CyclicIterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        Iterable.super.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return Iterable.super.spliterator();
    }

    class CyclicIterator implements Iterator<T> {
        boolean started = false;
        CyclicStack<T> curr = CyclicStack.this;

        @Override
        public boolean hasNext() {
            return !curr.end || !started;
        }

        @Override
        public T next() {
            if (!started) {
                started = true;
                return CyclicStack.this.value;
            }
            return (curr = curr.next).value;
        }
    }
}
