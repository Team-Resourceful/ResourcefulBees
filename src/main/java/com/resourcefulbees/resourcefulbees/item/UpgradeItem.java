package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.lib.NBTConstants;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.jetbrains.annotations.NotNull;

public class UpgradeItem extends Item {


    private CompoundNBT upgradeData = new CompoundNBT();

    public UpgradeItem(Item.Properties properties, CompoundNBT upgradeData) {
        super(properties);
        setUpgradeData(upgradeData);
    }

    public CompoundNBT getUpgradeData() {
        return upgradeData;
    }

    public void setUpgradeData(CompoundNBT upgradeData) {
        this.upgradeData = upgradeData;
    }

    public static boolean isUpgradeItem(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof UpgradeItem;
    }

    public static CompoundNBT getUpgradeData(ItemStack stack) {
        if (stack.getTagElement("UpgradeData") == null && isUpgradeItem(stack)) {
           return ((UpgradeItem) stack.getItem()).getUpgradeData();
        }
        return new CompoundNBT();
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

        CompoundNBT upgradeData = new CompoundNBT();

        public CompoundNBT build() {
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
