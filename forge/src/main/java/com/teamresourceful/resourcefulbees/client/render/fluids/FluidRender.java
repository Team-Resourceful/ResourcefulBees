package com.teamresourceful.resourcefulbees.client.render.fluids;

import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModFluids;
import com.teamresourceful.resourcefulbees.common.lib.tools.UtilityClassError;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class FluidRender {

    private FluidRender() {
        throw new UtilityClassError();
    }

    public static void setHoneyRenderType() {
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY_STILL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY_FLOWING.get(), RenderType.translucent());
    }
}
