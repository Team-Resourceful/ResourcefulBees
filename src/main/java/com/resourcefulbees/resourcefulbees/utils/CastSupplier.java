package com.resourcefulbees.resourcefulbees.utils;

import java.util.function.Supplier;

public class CastSupplier <C extends P, P> implements Supplier<P> {

    private final Supplier<C> from;

    public CastSupplier(Supplier<C> from) {
        this.from = from;
    }

    @Override
    public P get() {
        return from.get();
    }
}
