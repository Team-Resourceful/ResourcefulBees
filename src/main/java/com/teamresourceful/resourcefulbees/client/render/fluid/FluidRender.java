package com.teamresourceful.resourcefulbees.client.render.fluid;

import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class FluidRender {

    private FluidRender() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setHoneyRenderType() {
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY_STILL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY_FLOWING.get(), RenderType.translucent());
    }
}
