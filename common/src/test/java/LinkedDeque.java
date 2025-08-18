import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LinkedDeque<T> implements Deque<T> {
    private int size = 0;

    class Element {
        Element prev, next;
        T value;
    }

    Element root = null;
    Element end = null;

    public LinkedDeque() {
    }

    @Override
    public void addFirst(T t) {
        Element newElem = new Element();
        newElem.value = t;
        newElem.next = root;
        root = newElem;
        size++;
        if (end == null) end = root;
        else root.next.prev = root;
    }

    @Override
    public void addLast(T t) {
        Element newElem = new Element();
        newElem.value = t;
        newElem.prev = end;
        end = newElem;
        size++;
        if (root == null) root = end;
        else end.prev.next = end;
    }

    @Override
    public boolean offerFirst(T t) {
        addFirst(t);
        return true;
    }

    @Override
    public boolean offerLast(T t) {
        addLast(t);
        return true;
    }

    // TODO: deliberately throw the correct exception
    @Override
    public T removeFirst() {
        T v = root.value;
        root = root.next;
        if (root != null)
            root.prev = null;
        size--;
        invalidate();
        return v;
    }

    // TODO: deliberately throw the correct exception
    @Override
    public T removeLast() {
        T v = end.value;
        end = end.prev;
        if (end != null)
            end.next = null;
        size--;
        invalidate();
        return v;
    }

    @Override
    public T pollFirst() {
        if (root == null) return null;
        return removeFirst();
    }

    @Override
    public T pollLast() {
        if (end == null) return null;
        return removeLast();
    }

    @Override
    public T getFirst() {
        if (root == null) throw new NoSuchElementException();
        return root.value;
    }

    @Override
    public T getLast() {
        if (end == null) throw new NoSuchElementException();
        return end.value;
    }

    @Override
    public T peekFirst() {
        if (root == null) return null;
        return root.value;
    }

    @Override
    public T peekLast() {
        if (end == null) return null;
        return end.value;
    }

    @Override
    public boolean removeFirstOccurrence(Object o) {
        if (root == null) return false;
        Element e = root;
        while (e != null) {
            if (Objects.equals(e.value, o)) {
                Element nex = e.next;
                if (e.prev != null) e.prev.next = nex;
                if (nex != null) nex.prev = e.prev;
                if (e == end) end = e.prev;
                if (e == root) root = e.next;
                size--;
                invalidate();
                return true;
            }
            e = e.next;
        }
        return false;
    }

    @Override
    public boolean removeLastOccurrence(Object o) {
        if (end == null) return false;
        Element e = end;
        while (e != null) {
            if (Objects.equals(e.value, o)) {
                Element nex = e.prev;
                if (e.next != null) e.next.prev = nex;
                if (nex != null) nex.next = e.next;
                if (e == end) end = e.prev;
                if (e == root) root = e.next;
                size--;
                invalidate();
                return true;
            }
            e = e.prev;
        }
        return false;
    }

    @Override
    public boolean add(T t) {
        return offerLast(t);
    }

    @Override
    public boolean offer(T t) {
        return offerLast(t);
    }

    @Override
    public T remove() {
        return removeFirst();
    }

    @Override
    public T poll() {
        return pollFirst();
    }

    @Override
    public T element() {
        return getFirst();
    }

    @Override
    public T peek() {
        return peekFirst();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new RuntimeException("NYI");
    }

    @Override
    public void push(T t) {
        addFirst(t);
    }

    @Override
    public T pop() {
        return removeFirst();
    }

    @Override
    public boolean remove(Object o) {
        return removeFirstOccurrence(o);
    }

    @Override
    public boolean contains(Object o) {
        if (root == null) return false;
        Element e = root;
        while (e != null) {
            if (Objects.equals(e.value, o))
                return true;
            e = e.next;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        throw new RuntimeException("NYI");
    }

    @NotNull
    @Override
    public Iterator<T> descendingIterator() {
        throw new RuntimeException("NYI");
    }

    @Override
    public boolean isEmpty() {
        return end == null;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        throw new RuntimeException("NYI");
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        throw new RuntimeException("NYI");
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new RuntimeException("NYI");
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new RuntimeException("NYI");
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new RuntimeException("NYI");
    }

    @Override
    public void clear() {
        root = null;
        end = null;
        size = 0;
    }

    protected final void invalidate() {
        if (size == 0) {
            root = null;
            end = null;
        }
    }
}
