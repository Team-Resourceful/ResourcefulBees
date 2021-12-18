package com.teamresourceful.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.common.blockentity.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.utils.CubeModel;
import com.teamresourceful.resourcefulbees.common.utils.RenderCuboid;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RenderHoneyGenerator implements BlockEntityRenderer<HoneyGeneratorBlockEntity> {

    public RenderHoneyGenerator(BlockEntityRendererProvider.Context renderer) {}

    @Override
    public void render(HoneyGeneratorBlockEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidStack stack = tile.getTank().getFluid();
        if (!stack.isEmpty()) {
            float percentage = tile.getTank().getFluidAmount() / (float)tile.getTank().getCapacity();
            int color = stack.getFluid().getAttributes().getColor();
            ResourceLocation stillTexture = stack.getFluid().getAttributes().getStillTexture();
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            Vector3f start = new Vector3f(0.0625f, 0.0625f, 0.0625f);
            Vector3f end = new Vector3f(0.9375f, 0.0625f + percentage * 0.875f, 0.9375f);
            CubeModel model = new CubeModel(start, end);
            model.setTextures(stillTexture);
            RenderCuboid.INSTANCE.renderCube(model, matrix, builder, color, light, overlayLight);
        }
    }
}
