package com.teamresourceful.resourcefulbees.common.enchantments;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class HiveBreakEnchantment extends Enchantment {

    public HiveBreakEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public static void onBlockBreak(Level level, BlockState state, Player player, BlockPos pos) {
        if (!level.isClientSide()) {
            if (level.getRandom().nextInt(10) == 0) {
                if (EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HIVE_BREAK.get(), player) > 0) {
                    Block nest = getReplacementBlock(state);
                    if (nest == null) return;
                    BlockState nestState = nest.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom()));
                    level.setBlockAndUpdate(pos, nestState);
                }
            }
        }
    }

    @Nullable
    public static Block getReplacementBlock(@Nullable BlockState block) {
        if (block == null) return null;
        if (block.is(Blocks.STRIPPED_ACACIA_LOG)) return ModBlocks.ACACIA_BEE_NEST.get();
        if (block.is(Blocks.STRIPPED_BIRCH_LOG)) return ModBlocks.BIRCH_BEE_NEST.get();
        if (block.is(Blocks.STRIPPED_JUNGLE_LOG)) return ModBlocks.JUNGLE_BEE_NEST.get();
        if (block.is(Blocks.STRIPPED_OAK_LOG)) return ModBlocks.OAK_BEE_NEST.get();
        if (block.is(Blocks.STRIPPED_SPRUCE_LOG)) return ModBlocks.SPRUCE_BEE_NEST.get();
        if (block.is(Blocks.STRIPPED_DARK_OAK_LOG)) return ModBlocks.DARK_OAK_BEE_NEST.get();
        if (block.is(Blocks.GRASS_BLOCK)) return ModBlocks.GRASS_BEE_NEST.get();
        if (block.is(Blocks.CRIMSON_NYLIUM)) return ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get();
        if (block.is(Blocks.CRIMSON_STEM)) return ModBlocks.CRIMSON_BEE_NEST.get();
        if (block.is(Blocks.WARPED_NYLIUM)) return ModBlocks.WARPED_NYLIUM_BEE_NEST.get();
        if (block.is(Blocks.WARPED_STEM)) return ModBlocks.WARPED_BEE_NEST.get();
        if (block.is(Blocks.RED_MUSHROOM_BLOCK)) return ModBlocks.RED_MUSHROOM_BEE_NEST.get();
        if (block.is(Blocks.BROWN_MUSHROOM_BLOCK)) return ModBlocks.BROWN_MUSHROOM_BEE_NEST.get();
        if (block.is(Blocks.PURPUR_BLOCK)) return ModBlocks.PURPUR_BEE_NEST.get();
        if (block.is(Blocks.NETHERRACK)) return ModBlocks.NETHER_BEE_NEST.get();
        if (block.is(Blocks.PRISMARINE)) return ModBlocks.PRISMARINE_BEE_NEST.get();
        return null;
    }

}
