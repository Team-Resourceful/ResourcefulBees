package com.teamresourceful.resourcefulbees.common.lib.enums;

import com.mojang.serialization.Codec;

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

    public static final Codec<ProcessStage> CODEC = Codec.STRING.xmap(s -> ProcessStage.valueOf(s.toUpperCase(Locale.ROOT)), e -> e.toString().toLowerCase(Locale.ROOT));
}
