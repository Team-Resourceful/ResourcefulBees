package com.teamresourceful.resourcefulbees.common.lib.enums;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum HoneyPotState implements StringRepresentable {
    OPEN,
    BEECON,
    CLOSED;

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
