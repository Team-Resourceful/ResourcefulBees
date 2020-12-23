package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.beedata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;

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
            return honeyBottleItem.honeyBottleData.isRainbow() ? RainbowColor.getRGB() : honeyBottleItem.getHoneyBucketColor();
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public int getHoneyBucketColor() {
        return honeyBottleData.getHoneyColorInt();
    }

    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
        if (this instanceof BucketItem)
            return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
        else
            return super.initCapabilities(stack, nbt);
    }
}
