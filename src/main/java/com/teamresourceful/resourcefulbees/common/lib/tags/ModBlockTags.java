package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class ModBlockTags {
    public static final TagKey<Block> HONEYCOMB = createBlockTag("c", "storage_blocks/honeycombs");
    public static final TagKey<Block> WAX = createBlockTag("c", "storage_blocks/wax");
    public static final TagKey<Block> MUSHROOM = createBlockTag("c", "mushrooms");

    public static final TagKey<Block> HEAT_SOURCES = createBlockTag("c", "heat_sources");
    public static final TagKey<Block> NEST_PLACEABLE_ON = createBlockTag(ModConstants.MOD_ID, "nest_placeable_on");
    public static final TagKey<Block> GOLD_FLOWER_BONEMEALABLE = createBlockTag(ModConstants.MOD_ID, "gold_flower_bonemealable");

    public static final TagKey<Block> CENTRIFUGE_PICKABLE = createBlockTag("rbees_centrifuge", "pickaxe_mineable");

    private ModBlockTags() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static TagKey<Block> createBlockTag(String mod, String path) {
        return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(mod, path));
    }
}
