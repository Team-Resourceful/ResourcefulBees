package com.teamresourceful.resourcefulbees.client.render.fluid;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.lib.ModConstants;
import com.teamresourceful.resourcefulbees.registry.ModBlocks;
import com.teamresourceful.resourcefulbees.registry.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

@OnlyIn(Dist.CLIENT)
public class FluidRender {

    private FluidRender() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    private static final ResourceLocation HONEY_UNDERWATER = new ResourceLocation(ResourcefulBees.MOD_ID + ":textures/block/honey/honey_underwater.png");

    public static void honeyOverlay(RenderBlockOverlayEvent event) {
        if (event.getPlayer().level.getBlockState(event.getBlockPos()).getBlock() == ModBlocks.HONEY_FLUID_BLOCK.get()) {
            Minecraft minecraftIn = Minecraft.getInstance();
            minecraftIn.getTextureManager().bind(HONEY_UNDERWATER);
            BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
            float f = event.getPlayer().getBrightness();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            float f7 = -event.getPlayer().yRot / 64.0F;
            float f8 = event.getPlayer().xRot / 64.0F;
            Matrix4f matrix4f = event.getMatrixStack().last().pose();
            bufferbuilder.begin(7, DefaultVertexFormat.POSITION_COLOR_TEX);
            bufferbuilder.vertex(matrix4f, -1.0F, -1.0F, -0.5F).color(f, f, f, 0.42F).uv(4.0F + f7, 4.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, 1.0F, -1.0F, -0.5F).color(f, f, f, 0.42F).uv(0.0F + f7, 4.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, 1.0F, 1.0F, -0.5F).color(f, f, f, 0.42F).uv(0.0F + f7, 0.0F + f8).endVertex();
            bufferbuilder.vertex(matrix4f, -1.0F, 1.0F, -0.5F).color(f, f, f, 0.42F).uv(4.0F + f7, 0.0F + f8).endVertex();
            bufferbuilder.end();
            BufferUploader.end(bufferbuilder);
            RenderSystem.disableBlend();
            event.setCanceled(true);
        }
    }

    public static void setHoneyRenderType() {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.HONEY_FLUID_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY_STILL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.HONEY_FLOWING.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.CATNIP_HONEY_FLOWING.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModFluids.CATNIP_HONEY_STILL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CATNIP_HONEY_BLOCK.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.CATNIP_HONEY_FLUID_BLOCK.get(), RenderType.translucent());
    }
}
