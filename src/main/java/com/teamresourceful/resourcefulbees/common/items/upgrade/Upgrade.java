package com.teamresourceful.resourcefulbees.common.items.upgrade;

public interface Upgrade {
    UpgradeType getUpgradeType();

    default boolean isType(UpgradeType type) {
        return getUpgradeType().isType(type);
    }
}
