package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import net.minecraft.nbt.CompoundTag;

import java.util.Locale;

public enum ProcessStage {
    IDLE,
    PROCESSING,
    FINALIZING,
    COMPLETED;

    public boolean isIdle() {
        return this == IDLE;
    }

    public boolean isProcessing() {
        return this == PROCESSING;
    }

    public boolean isFinalizing() {
        return this == FINALIZING;
    }

    public boolean isCompleted() {
        return this == COMPLETED;
    }

    public static ProcessStage deserialize(CompoundTag tag) {
        return valueOf(tag.getString(NBTConstants.NBT_PROCESS_STAGE).toUpperCase(Locale.ROOT));
    }
}
