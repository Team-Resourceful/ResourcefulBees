package com.resourcefulbees.resourcefulbees.lib;

import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class TagConstants {

    private TagConstants() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static final ITag<Fluid> RESOURCEFUL_HONEY = FluidTags.createOptional(new ResourceLocation(ResourcefulBees.MOD_ID, "resourceful_honey"));

}
