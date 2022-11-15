package com.teamresourceful.resourcefulbees.common.lib.tags;

import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.versions.forge.ForgeVersion;

public final class ModFluidTags {
    public static final TagKey<Fluid> HONEY = FluidTags.create(new ResourceLocation(ForgeVersion.MOD_ID, "honey"));

    private ModFluidTags() {
        throw new UtilityClassError();
    }
}
