package com.resourcefulbees.resourcefulbees.utils;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Consumer;

@SuppressWarnings("ALL")
public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    public NavigableMap<Double, E> getMap(){
        return map;
    }

    public double getAdjustedWeight(double weight) {
        return weight / total;
    }

    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (E e : map.values()) {
            action.accept(e);
        }
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
