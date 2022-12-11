package com.teamresourceful.resourcefulbees.common.item;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.NBTConstants;
import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class MutatedPollenItem extends Item {
    public MutatedPollenItem(Properties arg) {
        super(arg);
        Color color = ConstantColors.aliceblue;
    }

    public static int getColor(ItemStack stack, int tintIndex) {
        return tintIndex == 0 ? ((MutatedPollenItem) stack.getItem()).getBasePollenColor(stack) : ((MutatedPollenItem) stack.getItem()).getTopPollenColor(stack);
    }

    private int getTopPollenColor(@NotNull ItemStack stack) {
        if (!stack.hasTag() || stack.getTag() == null) return BeeConstants.DEFAULT_POLLEN_TOP_COLOR.getValue();
        DataResult<Color> color = Color.decodeColor(new Dynamic<>(NbtOps.INSTANCE, stack.getTag().get(NBTConstants.POLLEN_TOP_COLOR)));
        return color.result().orElse(BeeConstants.DEFAULT_POLLEN_TOP_COLOR).getValue();
    }

    private int getBasePollenColor(@NotNull ItemStack stack) {
        if (!stack.hasTag() || stack.getTag() == null) return BeeConstants.DEFAULT_POLLEN_BASE_COLOR.getValue();
        DataResult<Color> color = Color.decodeColor(new Dynamic<>(NbtOps.INSTANCE, stack.getTag().get(NBTConstants.POLLEN_BASE_COLOR)));
        return color.result().orElse(BeeConstants.DEFAULT_POLLEN_BASE_COLOR).getValue();
    }
}
