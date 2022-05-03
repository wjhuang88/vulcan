package io.vulcan.api.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Pair<A, B> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final A first;
    private final B second;

    public Pair(A first, B second) {
        if (null == first || null == second) {
            throw new IllegalArgumentException("Pair cannot have null element.");
        }
        this.first = first;
        this.second = second;
    }

    public static <T> Pair<T, T> from(Collection<T> collection) {
        if (null == collection) {
            throw new IllegalArgumentException("Collection is null.");
        }
        if (collection.size() < 2) {
            throw new IllegalArgumentException("Collection must have at least 2 elements.");
        }
        final Iterator<T> iterator = collection.iterator();
        return new Pair<>(iterator.next(), iterator.next());
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    public List<A> toList() {
        if (first.getClass() != second.getClass()) {
            throw new IllegalStateException("Only Pair with same element types can be converted to list.");
        }
        List<A> list = new ArrayList<>();
        list.add(first);

        @SuppressWarnings("unchecked")
        A secondA = (A) second;
        list.add(secondA);

        return list;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
