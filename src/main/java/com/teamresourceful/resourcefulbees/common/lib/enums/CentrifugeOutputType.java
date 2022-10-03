package com.teamresourceful.resourcefulbees.common.lib.enums;

public enum CentrifugeOutputType {
    ITEM,
    FLUID;

    public boolean isItem() {
        return this == ITEM;
    }

    public boolean isFluid() {
        return this == FLUID;
    }
}
