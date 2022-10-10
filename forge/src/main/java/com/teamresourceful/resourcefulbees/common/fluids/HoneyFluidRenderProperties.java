package com.teamresourceful.resourcefulbees.common.fluids;

import com.teamresourceful.resourcefulbees.api.honeydata.fluid.FluidRenderData;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.Nullable;

public class HoneyFluidRenderProperties implements IClientFluidTypeExtensions {

    private final FluidRenderData data;

    public HoneyFluidRenderProperties(FluidRenderData data) {
        this.data = data;
    }

    @Override
    public int getTintColor() {
        return data.color().getValue() | 0xff000000;
    }

    @Override
    public ResourceLocation getStillTexture() {
        return data.still();
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return data.flowing();
    }

    @Override
    public @Nullable ResourceLocation getOverlayTexture() {
        return data.flowing();
    }

    @Override
    public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc) {
        return null; //Bc it doesn't do shit
    }


}
