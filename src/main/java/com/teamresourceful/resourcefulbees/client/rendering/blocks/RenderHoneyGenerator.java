package com.teamresourceful.resourcefulbees.client.rendering.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

public class RenderHoneyGenerator implements BlockEntityRenderer<HoneyGeneratorBlockEntity, BlockEntityRenderState> {

    public RenderHoneyGenerator(BlockEntityRendererProvider.Context renderer) {}

/*    @Override
    public void render(HoneyGeneratorBlockEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        *//*FluidHolder holder = tile.getFluid();
        if (!holder.isEmpty()) {
            float percentage = tile.getFluid().getFluidAmount() / (float)tile.getFluidContainer().getTankCapacity(0);
            int color = ClientFluidHooks.getFluidColor(holder);
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            AABB box = new AABB(0.0625, 0.0625, 0.0625, 0.9375, 0.0625 + percentage * 0.875, 0.9375);
            RenderCuboid.renderCube(box, ClientFluidHooks.getFluidSprite(holder), matrix, builder, color, light, overlayLight);
        }*//*
    }*/

    @Override
    public BlockEntityRenderState createRenderState() {
        return null;
    }

    @Override
    public void submit(BlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {

    }
}
