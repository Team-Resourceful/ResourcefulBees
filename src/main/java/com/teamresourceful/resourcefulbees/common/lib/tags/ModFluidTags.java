package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefullib.common.exceptions.UtilityClassException;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public final class ModFluidTags {
    public static final TagKey<Fluid> HONEY = TagKey.create(Registries.FLUID, Identifier.fromNamespaceAndPath("c", "honey"));

    private ModFluidTags() throws UtilityClassException {
        throw new UtilityClassException();
    }
}
