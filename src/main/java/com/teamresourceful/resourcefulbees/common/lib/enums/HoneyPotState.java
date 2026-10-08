package com.teamresourceful.resourcefulbees.common.lib.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum HoneyPotState implements StringRepresentable {
    OPEN,
    CLOSED;

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public HoneyPotState toggle() {
        return this == CLOSED ? OPEN : CLOSED;
    }
}
