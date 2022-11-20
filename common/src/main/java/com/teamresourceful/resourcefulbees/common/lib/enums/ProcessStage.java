package com.teamresourceful.resourcefulbees.common.lib.enums;

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
}
