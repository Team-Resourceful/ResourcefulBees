package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.versions.forge.ForgeVersion;

public final class ModBlockTags {
    public static final TagKey<Block> HONEYCOMB = createBlockTag(ForgeVersion.MOD_ID, "storage_blocks/honeycombs");
    public static final TagKey<Block> WAX = createBlockTag(ForgeVersion.MOD_ID, "storage_blocks/wax");
    public static final TagKey<Block> MUSHROOM = createBlockTag(ForgeVersion.MOD_ID, "mushrooms");

    public static final TagKey<Block> HEAT_SOURCES = createBlockTag(ForgeVersion.MOD_ID, "heat_sources");
    public static final TagKey<Block> NEST_PLACEABLE_ON = createBlockTag(ResourcefulBees.MOD_ID, "nest_placeable_on");

    private ModBlockTags() {
        throw new UtilityClassError();
    }

    private static TagKey<Block> createBlockTag(String mod, String path) {
        return BlockTags.create(new ResourceLocation(mod, path));
    }
}