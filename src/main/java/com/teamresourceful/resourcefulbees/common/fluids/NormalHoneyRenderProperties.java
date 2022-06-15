package com.teamresourceful.resourcefulbees.common.fluids;

import com.teamresourceful.resourcefulbees.ResourcefulBees;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.IFluidTypeRenderProperties;
import org.jetbrains.annotations.Nullable;

public class NormalHoneyRenderProperties implements IFluidTypeRenderProperties {

    public static final ResourceLocation FLUID_STILL = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_still");
    public static final ResourceLocation FLUID_FLOWING = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_flow");
    public static final ResourceLocation FLUID_OVERLAY = new ResourceLocation(ResourcefulBees.MOD_ID, "block/honey/honey_overlay");

    public static final NormalHoneyRenderProperties INSTANCE = new NormalHoneyRenderProperties();

    private NormalHoneyRenderProperties() {
    }

    @Override
    public ResourceLocation getStillTexture() {
        return FLUID_STILL;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return FLUID_FLOWING;
    }

    @Override
    public @Nullable ResourceLocation getOverlayTexture() {
        return FLUID_OVERLAY;
    }

    @Override
    public @Nullable ResourceLocation getRenderOverlayTexture(Minecraft mc) {
        return getOverlayTexture();
    }
}
