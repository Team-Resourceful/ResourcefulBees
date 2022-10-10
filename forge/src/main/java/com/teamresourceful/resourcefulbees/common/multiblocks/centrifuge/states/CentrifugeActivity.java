package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

public enum CentrifugeActivity implements StringRepresentable {
    ACTIVE((byte)1),
    INACTIVE((byte)0);

    public static final EnumProperty<CentrifugeActivity> PROPERTY = EnumProperty.create("centrifuge_activity", CentrifugeActivity.class);

    private final byte state;

    CentrifugeActivity(byte state) {
        this.state = state;
    }

    @Override
    public @NotNull String getSerializedName() {
        return toString().toLowerCase();
    }

    public byte getByte() {
        return this.state;
    }

    public static CentrifugeActivity fromByte(byte state) {
        return switch (state) {
            case 1 -> CentrifugeActivity.ACTIVE;
            case 0 -> CentrifugeActivity.INACTIVE;
            default -> throw new IndexOutOfBoundsException("Invalid index while deciphering centrifuge activity");
        };
    }
}
