package com.teamresourceful.resourcefulbees.common.lib.enums;

import net.minecraft.util.IStringSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public enum HoneyPotState implements IStringSerializable {
    OPEN,
    BEECON,
    CLOSED;

    @Override
    public @NotNull String getSerializedName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
