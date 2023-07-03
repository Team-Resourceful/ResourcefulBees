package com.teamresourceful.resourcefulbees.client.rendering.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.client.util.RenderCuboid;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class RenderHoneyGenerator implements BlockEntityRenderer<HoneyGeneratorBlockEntity> {

    public RenderHoneyGenerator(BlockEntityRendererProvider.Context renderer) {}

    @Override
    public void render(HoneyGeneratorBlockEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidHolder holder = tile.getFluid();
        if (!holder.isEmpty()) {
            float percentage = tile.getFluid().getFluidAmount() / (float)tile.getFluidContainer().getTankCapacity(0);
            int color = ClientFluidHooks.getFluidColor(holder);
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            AABB box = new AABB(0.0625, 0.0625, 0.0625, 0.9375, 0.0625 + percentage * 0.875, 0.9375);
            RenderCuboid.renderCube(box, ClientFluidHooks.getFluidSprite(holder), matrix, builder, color, light, overlayLight);
        }
    }
}
