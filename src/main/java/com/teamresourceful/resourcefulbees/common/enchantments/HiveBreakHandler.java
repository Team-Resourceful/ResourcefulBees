package com.teamresourceful.resourcefulbees.common.enchantments;

import com.teamresourceful.resourcefulbees.common.registries.minecraft.ModEnchantments;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

;

public final class HiveBreakHandler {

    private HiveBreakHandler() {
    }

    public static void onBlockDrops(BlockDropsEvent event) {
        Level level = event.getLevel();

        if (level.isClientSide()) {
            return;
        }

        ItemStack tool = event.getTool();

        Holder<Enchantment> hiveBreak = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(ModEnchantments.HIVE_BREAK);

        int enchantmentLevel = tool.getEnchantmentLevel(hiveBreak);

        if (enchantmentLevel <= 0) {
            return;
        }

        if (level.getRandom().nextInt(100) >= enchantmentLevel) {
            return;
        }

        Block nest = HiveBreakConversions.getConversion(event.getState().getBlock());

        if (nest == null) {
            return;
        }
        BlockState nestState = nest.defaultBlockState();
        if (nestState.hasProperty(HorizontalDirectionalBlock.FACING)) {
            nestState = nestState.setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom()));
        }

        level.setBlockAndUpdate(event.getPos(), nestState);
    }
}