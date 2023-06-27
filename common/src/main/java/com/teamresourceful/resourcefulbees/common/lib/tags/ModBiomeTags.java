package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomeTags {

    public static final TagKey<Biome> ALLOWS_GOLD_FLOWER = createBiomeTag(ModConstants.MOD_ID, "allows_gold_flower");

    private ModBiomeTags() throws UtilityClassException {
        throw new UtilityClassException();
    }

    private static TagKey<Biome> createBiomeTag(String mod, String path) {
        return TagKey.create(Registries.BIOME, new ResourceLocation(mod, path));
    }
}
