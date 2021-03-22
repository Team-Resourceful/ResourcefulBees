package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class UpgradeItem extends Item {


    private CompoundTag upgradeData = new CompoundTag();

    public UpgradeItem(Properties properties, CompoundTag upgradeData) {
        super(properties);
        setUpgradeData(upgradeData);
    }

    public CompoundTag getUpgradeData() {
        return upgradeData;
    }

    public void setUpgradeData(CompoundTag upgradeData) {
        this.upgradeData = upgradeData;
    }

    public static boolean isUpgradeItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof UpgradeItem;
    }

    public static CompoundTag getUpgradeData(ItemStack stack) {
        if (stack.getTagElement("UpgradeData") == null && isUpgradeItem(stack)) {
           return ((UpgradeItem) stack.getItem()).getUpgradeData();
        }
        return new CompoundTag();
    }

    public static String getUpgradeType(@NotNull ItemStack stack) {
        return getUpgradeData(stack).getString(NBTConstants.NBT_UPGRADE_TYPE);
    }

    public static boolean hasUpgradeData(ItemStack stack) {
        return getUpgradeData(stack) != null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        CompoundTag upgradeData = new CompoundTag();

        public CompoundTag build() {
            return upgradeData.isEmpty() ? null : upgradeData;
        }

        public Builder upgradeType(String type) {
            upgradeData.putString(NBTConstants.NBT_UPGRADE_TYPE, type);
            return this;
        }

        public Builder upgradeModification(String type, float value) {
            upgradeData.putFloat(type, value);
            return this;
        }
    }
}
