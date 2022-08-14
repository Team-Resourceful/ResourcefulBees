package com.teamresourceful.resourcefulbees.common.utils;

public enum TriState {
    TRUE,
    FALSE,
    UNSET;

    public TriState next() {
        return values()[(ordinal() + 1) % values().length];
    }

    public boolean isTrue() {
        return this == TRUE;
    }

    public boolean isFalse() {
        return this == FALSE;
    }

    public boolean isUnset() {
        return this == UNSET;
    }

    public boolean isSet() {
        return this != UNSET;
    }
}
