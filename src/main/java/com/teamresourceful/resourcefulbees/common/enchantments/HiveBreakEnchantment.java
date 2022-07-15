package com.teamresourceful.resourcefulbees.common.enchantments;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModEnchantments;
import net.minecraft.Util;
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

import java.util.HashMap;
import java.util.Map;

public class HiveBreakEnchantment extends Enchantment {
    
    private static final Map<Block, Block> CONVERSIONS = Util.make(new HashMap<>(), map -> {
        map.put(Blocks.STRIPPED_ACACIA_LOG, ModBlocks.ACACIA_BEE_NEST.get());
        map.put(Blocks.STRIPPED_BIRCH_LOG, ModBlocks.BIRCH_BEE_NEST.get());
        map.put(Blocks.STRIPPED_JUNGLE_LOG, ModBlocks.JUNGLE_BEE_NEST.get());
        map.put(Blocks.STRIPPED_OAK_LOG, ModBlocks.OAK_BEE_NEST.get());
        map.put(Blocks.STRIPPED_SPRUCE_LOG, ModBlocks.SPRUCE_BEE_NEST.get());
        map.put(Blocks.STRIPPED_DARK_OAK_LOG, ModBlocks.DARK_OAK_BEE_NEST.get());
        map.put(Blocks.GRASS_BLOCK, ModBlocks.GRASS_BEE_NEST.get());
        map.put(Blocks.CRIMSON_NYLIUM, ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get());
        map.put(Blocks.CRIMSON_STEM, ModBlocks.CRIMSON_BEE_NEST.get());
        map.put(Blocks.WARPED_NYLIUM, ModBlocks.WARPED_NYLIUM_BEE_NEST.get());
        map.put(Blocks.WARPED_STEM, ModBlocks.WARPED_BEE_NEST.get());
        map.put(Blocks.RED_MUSHROOM_BLOCK, ModBlocks.RED_MUSHROOM_BEE_NEST.get());
        map.put(Blocks.BROWN_MUSHROOM_BLOCK, ModBlocks.BROWN_MUSHROOM_BEE_NEST.get());
        map.put(Blocks.PURPUR_BLOCK, ModBlocks.PURPUR_BEE_NEST.get());
        map.put(Blocks.NETHERRACK, ModBlocks.NETHER_BEE_NEST.get());
        map.put(Blocks.PRISMARINE, ModBlocks.PRISMARINE_BEE_NEST.get());
    });

    public HiveBreakEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.DIGGER, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public static void onBlockBreak(Level level, BlockState state, Player player, BlockPos pos) {
        if (!level.isClientSide() && level.getRandom().nextInt(10) == 0) {
            int value = EnchantmentHelper.getEnchantmentLevel(ModEnchantments.HIVE_BREAK.get(), player);
            if (value > 0 && level.getRandom().nextInt(10 / value) == 0) {
                Block nest = HiveBreakEnchantment.CONVERSIONS.get(state.getBlock());
                if (nest == null) return;
                BlockState nestState = nest.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection(level.getRandom()));
                level.setBlockAndUpdate(pos, nestState);
            }
        }
    }

}
