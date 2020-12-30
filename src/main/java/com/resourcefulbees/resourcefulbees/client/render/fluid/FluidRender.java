package com.resourcefulbees.resourcefulbees.client.render.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.resourcefulbees.resourcefulbees.ResourcefulBees;
import com.resourcefulbees.resourcefulbees.registry.ModBlocks;
import com.resourcefulbees.resourcefulbees.registry.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

@OnlyIn(Dist.CLIENT)
public class FluidRender {

    private static final ResourceLocation HONEY_UNDERWATER = new ResourceLocation(ResourcefulBees.MOD_ID + ":textures/block/honey/honey_underwater.png");

    public static void honeyOverlay(RenderBlockOverlayEvent event) {
        if (event.getPlayer().world.getBlockState(event.getBlockPos()).getBlock() == ModBlocks.HONEY_FLUID_BLOCK.get()) {
            Minecraft minecraftIn = Minecraft.getInstance();
            minecraftIn.getTextureManager().bindTexture(HONEY_UNDERWATER);
            BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
            float f = event.getPlayer().getBrightness();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float f7 = -event.getPlayer().rotationYaw / 64.0F;
            float f8 = event.getPlayer().rotationPitch / 64.0F;
            Matrix4f matrix4f = event.getMatrixStack().peek().getModel();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEXTURE);
            bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, 0.42F).texture(4.0F + f7, 4.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, 0.42F).texture(0.0F + f7, 4.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, 0.42F).texture(0.0F + f7, 0.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, 0.42F).texture(4.0F + f7, 0.0F + f8).endVertex();
            bufferbuilder.finishDrawing();
            WorldVertexBufferUploader.draw(bufferbuilder);
            RenderSystem.disableBlend();
            event.setCanceled(true);
        }
    }

    public static void setHoneyRenderType() {
        RenderTypeLookup.setRenderLayer(ModBlocks.HONEY_FLUID_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.HONEY_STILL.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.HONEY_FLOWING.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.CATNIP_HONEY_FLOWING.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModFluids.CATNIP_HONEY_STILL.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.CATNIP_HONEY_BLOCK.get(), RenderType.getTranslucent());
        RenderTypeLookup.setRenderLayer(ModBlocks.CATNIP_HONEY_FLUID_BLOCK.get(), RenderType.getTranslucent());
    }
}
