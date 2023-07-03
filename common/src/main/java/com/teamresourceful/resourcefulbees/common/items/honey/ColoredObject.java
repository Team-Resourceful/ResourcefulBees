package com.teamresourceful.resourcefulbees.common.items.honey;

import com.teamresourceful.resourcefulbees.common.lib.constants.BeeConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface ColoredObject {

    int getObjectColor(int index);

    static int getItemColor(ItemStack stack, int index) {
        if (stack.getItem() instanceof ColoredObject coloredObject) {
            return coloredObject.getObjectColor(index);
        }
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ColoredObject coloredBlock) {
            return coloredBlock.getObjectColor(index);
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }

    static int getBlockColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int index) {
        if (state.getBlock() instanceof ColoredObject coloredBlock) {
            return coloredBlock.getObjectColor(index);
        }
        return BeeConstants.DEFAULT_ITEM_COLOR;
    }
}
