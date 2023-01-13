package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModBlockTags {
    public static final TagKey<Block> HONEYCOMB = createBlockTag("forge", "storage_blocks/honeycombs");
    public static final TagKey<Block> WAX = createBlockTag("forge", "storage_blocks/wax");
    public static final TagKey<Block> MUSHROOM = createBlockTag("forge", "mushrooms");

    public static final TagKey<Block> HEAT_SOURCES = createBlockTag("forge", "heat_sources");
    public static final TagKey<Block> NEST_PLACEABLE_ON = createBlockTag(ModConstants.MOD_ID, "nest_placeable_on");

    private ModBlockTags() {
        throw new UtilityClassError();
    }

    private static TagKey<Block> createBlockTag(String mod, String path) {
        return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(mod, path));
    }
}
