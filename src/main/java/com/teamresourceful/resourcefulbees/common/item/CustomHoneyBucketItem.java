package com.teamresourceful.resourcefulbees.common.item;

import com.teamresourceful.resourcefulbees.api.honeydata.HoneyBottleData;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CustomHoneyBucketItem extends BucketItem {

    private final HoneyBottleData honeyBottleData;

    public CustomHoneyBucketItem(Supplier<? extends Fluid> supplier, Properties builder, HoneyBottleData honeyBottleData) {
        super(supplier, builder);
        this.honeyBottleData = honeyBottleData;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) {
            CustomHoneyBucketItem honeyBottleItem = (CustomHoneyBucketItem) stack.getItem();
            return honeyBottleItem.getHoneyBucketColor();
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public int getHoneyBucketColor() {
        return honeyBottleData.getColor().getValue();
    }

    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundNBT nbt) {
        return new FluidBucketWrapper(stack);
    }
}