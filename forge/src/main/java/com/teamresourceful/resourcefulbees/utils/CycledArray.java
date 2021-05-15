package com.teamresourceful.resourcefulbees.utils;

import java.util.Collection;

public class CycledArray<T> {

    final T[] list;
    int count;

    public CycledArray(T[] list) {
        this.list = list;
    }

    public CycledArray(Collection<T> list) {
        this.list = (T[]) list.toArray();
    }

    public T cycle(int amount) {
        count += amount;
        if (count >= list.length) count = 0;
        return list[count];
    }

    @SuppressWarnings("UnusedReturnValue")
    public T cycle() {
        return cycle(1);
    }

    public boolean isEmpty() {
        return list.length == 0;
    }

    public T get() {
        return list[count];
    }
}