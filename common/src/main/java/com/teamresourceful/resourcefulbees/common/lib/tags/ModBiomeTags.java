package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomeTags {

    public static final TagKey<Biome> ALLOWS_GOLD_FLOWER = createBiomeTag(ModConstants.MOD_ID, "allows_gold_flower");

    private ModBiomeTags() {
        throw new UtilityClassError();
    }

    private static TagKey<Biome> createBiomeTag(String mod, String path) {
        return TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(mod, path));
    }
}
