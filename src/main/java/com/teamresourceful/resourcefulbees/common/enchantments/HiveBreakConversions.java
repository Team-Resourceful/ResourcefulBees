package com.teamresourceful.resourcefulbees.common.enchantments;

import com.google.common.base.Suppliers;
import com.teamresourceful.resourcefulbees.common.lib.records.HiveType;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class HiveBreakConversions {

    private static final Supplier<Map<Block, Block>> CONVERSIONS =
            Suppliers.memoize(HiveBreakConversions::createConversions);

    private HiveBreakConversions() {
    }

    public static Block getConversion(Block block) {
        return CONVERSIONS.get().get(block);
    }

    private static Map<Block, Block> createConversions() {
        Map<Block, Block> conversions = new HashMap<>();

        HiveType.values().forEach(hiveType -> {
            Block nest = hiveType.tierOneNest();
            hiveType.hiveBreakBlocks().forEach(input -> conversions.put(input.get(), nest));
        });

        return Map.copyOf(conversions);
    }
}