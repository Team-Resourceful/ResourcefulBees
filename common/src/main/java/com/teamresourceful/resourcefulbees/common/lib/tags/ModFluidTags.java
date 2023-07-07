package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import dev.architectury.injectables.targets.ArchitecturyTarget;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public final class ModFluidTags {
    public static final TagKey<Fluid> HONEY = TagKey.create(Registries.FLUID, new ResourceLocation(ArchitecturyTarget.getCurrentTarget(), "honey"));

    private ModFluidTags() {
        throw new UtilityClassError();
    }
}
