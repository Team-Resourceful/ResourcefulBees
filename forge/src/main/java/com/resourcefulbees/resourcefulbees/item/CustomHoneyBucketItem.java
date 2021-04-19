package com.resourcefulbees.resourcefulbees.item;

import com.resourcefulbees.resourcefulbees.api.honeydata.HoneyBottleData;
import com.resourcefulbees.resourcefulbees.lib.BeeConstants;
import com.resourcefulbees.resourcefulbees.utils.color.RainbowColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
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
            return honeyBottleItem.honeyBottleData.isRainbow() ? RainbowColor.getRGB() : honeyBottleItem.getHoneyBucketColor();
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    public int getHoneyBucketColor() {
        return honeyBottleData.getHoneyColorInt();
    }

    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidBucketWrapper(stack);
    }
}
