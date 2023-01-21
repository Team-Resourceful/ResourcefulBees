package com.teamresourceful.resourcefulbees.common.items.upgrade;

public enum UpgradeType {
    BREEDER,
    NEST;

    public boolean isType(UpgradeType type) {
        return this == type;
    }
}
