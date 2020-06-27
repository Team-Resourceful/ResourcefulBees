package com.dungeonderps.resourcefulbees.item;

import com.dungeonderps.resourcefulbees.registry.ItemGroupResourcefulBees;
import net.minecraft.item.Item;

public class UpgradeItem extends Item {

    public enum UpgradeType {
        CAPACITY,
        SPEED,
        COMPACT
    }

    private Enum<UpgradeType> upgradeType = null;
    private int upgradeTier = 0;

    public UpgradeItem(Enum<UpgradeType> upgradeType, int upgradeTier) {
        super(new Properties().setNoRepair().maxStackSize(1).group(ItemGroupResourcefulBees.RESOURCEFUL_BEES));
        this.upgradeType = upgradeType;
        this.upgradeTier = upgradeTier;
    }

    public Enum<UpgradeType> getUpgradeType() {
        return upgradeType;
    }

    public int getUpgradeTier() {
        return upgradeTier;
    }
}
