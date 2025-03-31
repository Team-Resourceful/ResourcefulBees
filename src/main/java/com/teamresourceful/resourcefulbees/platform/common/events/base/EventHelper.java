package com.teamresourceful.resourcefulbees.platform.common.events.base;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class EventHelper<T> {

    private final List<Consumer<T>> listeners = new ArrayList<>();

    public void addListener(Consumer<T> listener) {
        listeners.add(listener);
    }

    public void removeListener(Consumer<T> listener) {
        listeners.remove(listener);
    }

    @ApiStatus.Internal
    public void fire(T event) {
        listeners.forEach(listener -> listener.accept(event));
    }
}
