package io.vulcan.api.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Triple<A, B, C> implements Serializable {
    private static final long serialVersionUID = 2L;

    private final A first;
    private final B second;
    private final C third;

    public Triple(A first, B second, C third) {
        if (null == first || null == second || null == third) {
            throw new IllegalArgumentException("Triple cannot have null item.");
        }
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public static <T> Triple<T, T, T> from(Collection<T> collection) {
        if (null == collection) {
            throw new IllegalArgumentException("Collection is null.");
        }
        if (collection.size() < 3) {
            throw new IllegalArgumentException("Collection must have at least 3 items.");
        }
        final Iterator<T> iterator = collection.iterator();
        return new Triple<>(iterator.next(), iterator.next(), iterator.next());
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    public C getThird() {
        return third;
    }

    public List<A> toList() {
        if (first.getClass() != second.getClass() || first.getClass() != third.getClass()) {
            throw new IllegalStateException("Only Triple with same type items can be converted to list.");
        }
        List<A> list = new ArrayList<>();
        list.add(first);

        @SuppressWarnings("unchecked")
        A secondA = (A) second;
        list.add(secondA);

        @SuppressWarnings("unchecked")
        A thirdA = (A) third;
        list.add(thirdA);

        return list;
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ", " + third + ")";
    }
}
