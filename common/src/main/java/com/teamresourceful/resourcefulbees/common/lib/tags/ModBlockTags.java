package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModBlockTags {
    public static final TagKey<Block> HONEYCOMB = createBlockTag("forge", "storage_blocks/honeycombs");
    public static final TagKey<Block> WAX = createBlockTag("forge", "storage_blocks/wax");
    public static final TagKey<Block> MUSHROOM = createBlockTag("forge", "mushrooms");

    public static final TagKey<Block> HEAT_SOURCES = createBlockTag("forge", "heat_sources");
    public static final TagKey<Block> NEST_PLACEABLE_ON = createBlockTag(ModConstants.MOD_ID, "nest_placeable_on");
    public static final TagKey<Block> GLASS = createBlockTag("rbees_common", "glass");
    public static final TagKey<Block> OBSIDIAN = createBlockTag("rbees_common", "obsidian");
    public static final TagKey<Block> GRAVEL = createBlockTag("rbees_common", "gravel");
    public static final TagKey<Block> COBBLESTONE = createBlockTag("rbees_common", "cobblestone");
    public static final TagKey<Block> ORES = createBlockTag("rbees_common", "ores");

    public static final TagKey<Block> CENTRIFUGE_PICKABLE = createBlockTag("rbees_centrifuge", "pickaxe_mineable");

    private ModBlockTags() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static TagKey<Block> createBlockTag(String mod, String path) {
        return TagKey.create(Registries.BLOCK, new ResourceLocation(mod, path));
    }
}
