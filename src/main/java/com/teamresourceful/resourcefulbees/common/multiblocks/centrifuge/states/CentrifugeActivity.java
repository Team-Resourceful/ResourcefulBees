package com.teamresourceful.resourcefulbees.common.multiblocks.centrifuge.states;

import net.minecraft.state.EnumProperty;
import net.minecraft.util.IStringSerializable;
import org.jetbrains.annotations.NotNull;

public enum CentrifugeActivity implements IStringSerializable {
    ACTIVE((byte)1),
    INACTIVE((byte)0);

    public static final EnumProperty<CentrifugeActivity> CENTRIFUGE_ACTIVITY_ENUM_PROPERTY = EnumProperty.create("centrifugeactivity", CentrifugeActivity.class);

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
        switch (state) {
            case 1:
                return CentrifugeActivity.ACTIVE;
            case 0:
                return CentrifugeActivity.INACTIVE;
            default:
                throw new IndexOutOfBoundsException("Invalid index while deciphering centrifuge activity");
        }
    }
}
