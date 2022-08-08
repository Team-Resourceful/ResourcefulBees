package com.teamresourceful.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.client.utils.RenderCuboid;
import com.teamresourceful.resourcefulbees.common.blockentity.HoneyGeneratorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
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
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(stack.getFluid());
            int color = props.getTintColor(stack);
            ResourceLocation stillTexture = props.getStillTexture(stack);
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            AABB box = new AABB(0.0625, 0.0625, 0.0625, 0.9375, 0.0625 + percentage * 0.875, 0.9375);
            RenderCuboid.renderCube(box, stillTexture, matrix, builder, color, light, overlayLight);
        }
    }
}
