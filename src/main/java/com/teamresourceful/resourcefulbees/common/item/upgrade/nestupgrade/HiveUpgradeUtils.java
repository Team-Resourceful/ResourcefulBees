package com.teamresourceful.resourcefulbees.common.item.upgrade.nestupgrade;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import net.minecraft.world.level.block.Block;

public class HiveUpgradeUtils {

    private HiveUpgradeUtils() {
        throw new IllegalAccessError(ModConstants.UTILITY_CLASS);
    }

    public static Block getT1UpgradeFor(Block block) {
        if (ModBlocks.ACACIA_BEE_NEST.get().equals(block)) return ModBlocks.T1_ACACIA_BEEHIVE.get();
        if (ModBlocks.BIRCH_BEE_NEST.get().equals(block)) return ModBlocks.T1_BIRCH_BEEHIVE.get();
        if (ModBlocks.BROWN_MUSHROOM_BEE_NEST.get().equals(block)) return ModBlocks.T1_BROWN_MUSHROOM_BEEHIVE.get();
        if (ModBlocks.CRIMSON_BEE_NEST.get().equals(block)) return ModBlocks.T1_CRIMSON_BEEHIVE.get();
        if (ModBlocks.CRIMSON_NYLIUM_BEE_NEST.get().equals(block)) return ModBlocks.T1_CRIMSON_NYLIUM_BEEHIVE.get();
        if (ModBlocks.DARK_OAK_BEE_NEST.get().equals(block)) return ModBlocks.T1_DARK_OAK_BEEHIVE.get();
        if (ModBlocks.GRASS_BEE_NEST.get().equals(block)) return ModBlocks.T1_GRASS_BEEHIVE.get();
        if (ModBlocks.JUNGLE_BEE_NEST.get().equals(block)) return ModBlocks.T1_JUNGLE_BEEHIVE.get();
        if (ModBlocks.NETHER_BEE_NEST.get().equals(block)) return ModBlocks.T1_NETHER_BEEHIVE.get();
        if (ModBlocks.OAK_BEE_NEST.get().equals(block)) return ModBlocks.T1_OAK_BEEHIVE.get();
        if (ModBlocks.PRISMARINE_BEE_NEST.get().equals(block)) return ModBlocks.T1_PRISMARINE_BEEHIVE.get();
        if (ModBlocks.PURPUR_BEE_NEST.get().equals(block)) return ModBlocks.T1_PURPUR_BEEHIVE.get();
        if (ModBlocks.RED_MUSHROOM_BEE_NEST.get().equals(block)) return ModBlocks.T1_RED_MUSHROOM_BEEHIVE.get();
        if (ModBlocks.SPRUCE_BEE_NEST.get().equals(block)) return ModBlocks.T1_SPRUCE_BEEHIVE.get();
        if (ModBlocks.WARPED_BEE_NEST.get().equals(block)) return ModBlocks.T1_WARPED_BEEHIVE.get();
        if (ModBlocks.WARPED_NYLIUM_BEE_NEST.get().equals(block)) return ModBlocks.T1_WARPED_NYLIUM_BEEHIVE.get();
        if (ModBlocks.WITHER_BEE_NEST.get().equals(block)) return ModBlocks.T1_WITHER_BEEHIVE.get();
        return null;
    }

    public static Block getT2UpgradeFor(Block block) {
        if (ModBlocks.T1_ACACIA_BEEHIVE.get().equals(block)) return ModBlocks.T2_ACACIA_BEEHIVE.get();
        if (ModBlocks.T1_ACACIA_BEEHIVE.get().equals(block)) return ModBlocks.T2_ACACIA_BEEHIVE.get();
        if (ModBlocks.T1_BROWN_MUSHROOM_BEEHIVE.get().equals(block)) return ModBlocks.T2_BROWN_MUSHROOM_BEEHIVE.get();
        if (ModBlocks.T1_CRIMSON_BEEHIVE.get().equals(block)) return ModBlocks.T2_CRIMSON_BEEHIVE.get();
        if (ModBlocks.T1_CRIMSON_BEEHIVE.get().equals(block)) return ModBlocks.T2_CRIMSON_BEEHIVE.get();
        if (ModBlocks.T1_DARK_OAK_BEEHIVE.get().equals(block)) return ModBlocks.T2_DARK_OAK_BEEHIVE.get();
        if (ModBlocks.T1_GRASS_BEEHIVE.get().equals(block)) return ModBlocks.T2_GRASS_BEEHIVE.get();
        if (ModBlocks.T1_JUNGLE_BEEHIVE.get().equals(block)) return ModBlocks.T2_JUNGLE_BEEHIVE.get();
        if (ModBlocks.T1_NETHER_BEEHIVE.get().equals(block)) return ModBlocks.T2_NETHER_BEEHIVE.get();
        if (ModBlocks.T1_OAK_BEEHIVE.get().equals(block)) return ModBlocks.T2_OAK_BEEHIVE.get();
        if (ModBlocks.T1_PRISMARINE_BEEHIVE.get().equals(block)) return ModBlocks.T2_PRISMARINE_BEEHIVE.get();
        if (ModBlocks.T1_PURPUR_BEEHIVE.get().equals(block)) return ModBlocks.T2_PURPUR_BEEHIVE.get();
        if (ModBlocks.T1_RED_MUSHROOM_BEEHIVE.get().equals(block)) return ModBlocks.T2_RED_MUSHROOM_BEEHIVE.get();
        if (ModBlocks.T1_SPRUCE_BEEHIVE.get().equals(block)) return ModBlocks.T2_SPRUCE_BEEHIVE.get();
        if (ModBlocks.T1_WARPED_BEEHIVE.get().equals(block)) return ModBlocks.T2_WARPED_BEEHIVE.get();
        if (ModBlocks.T1_WARPED_NYLIUM_BEEHIVE.get().equals(block)) return ModBlocks.T2_WARPED_NYLIUM_BEEHIVE.get();
        if (ModBlocks.T1_WITHER_BEEHIVE.get().equals(block)) return ModBlocks.T2_WITHER_BEEHIVE.get();
        return null;
    }

    public static Block getT3UpgradeFor(Block block) {
        if (ModBlocks.T2_ACACIA_BEEHIVE.get().equals(block)) return ModBlocks.T3_ACACIA_BEEHIVE.get();
        if (ModBlocks.T2_ACACIA_BEEHIVE.get().equals(block)) return ModBlocks.T3_ACACIA_BEEHIVE.get();
        if (ModBlocks.T2_BROWN_MUSHROOM_BEEHIVE.get().equals(block)) return ModBlocks.T3_BROWN_MUSHROOM_BEEHIVE.get();
        if (ModBlocks.T2_CRIMSON_BEEHIVE.get().equals(block)) return ModBlocks.T3_CRIMSON_BEEHIVE.get();
        if (ModBlocks.T2_CRIMSON_BEEHIVE.get().equals(block)) return ModBlocks.T3_CRIMSON_BEEHIVE.get();
        if (ModBlocks.T2_DARK_OAK_BEEHIVE.get().equals(block)) return ModBlocks.T3_DARK_OAK_BEEHIVE.get();
        if (ModBlocks.T2_GRASS_BEEHIVE.get().equals(block)) return ModBlocks.T3_GRASS_BEEHIVE.get();
        if (ModBlocks.T2_JUNGLE_BEEHIVE.get().equals(block)) return ModBlocks.T3_JUNGLE_BEEHIVE.get();
        if (ModBlocks.T2_NETHER_BEEHIVE.get().equals(block)) return ModBlocks.T3_NETHER_BEEHIVE.get();
        if (ModBlocks.T2_OAK_BEEHIVE.get().equals(block)) return ModBlocks.T3_OAK_BEEHIVE.get();
        if (ModBlocks.T2_PRISMARINE_BEEHIVE.get().equals(block)) return ModBlocks.T3_PRISMARINE_BEEHIVE.get();
        if (ModBlocks.T2_PURPUR_BEEHIVE.get().equals(block)) return ModBlocks.T3_PURPUR_BEEHIVE.get();
        if (ModBlocks.T2_RED_MUSHROOM_BEEHIVE.get().equals(block)) return ModBlocks.T3_RED_MUSHROOM_BEEHIVE.get();
        if (ModBlocks.T2_SPRUCE_BEEHIVE.get().equals(block)) return ModBlocks.T3_SPRUCE_BEEHIVE.get();
        if (ModBlocks.T2_WARPED_BEEHIVE.get().equals(block)) return ModBlocks.T3_WARPED_BEEHIVE.get();
        if (ModBlocks.T2_WARPED_NYLIUM_BEEHIVE.get().equals(block)) return ModBlocks.T3_WARPED_NYLIUM_BEEHIVE.get();
        if (ModBlocks.T2_WITHER_BEEHIVE.get().equals(block)) return ModBlocks.T3_WITHER_BEEHIVE.get();
        return null;
    }
}
