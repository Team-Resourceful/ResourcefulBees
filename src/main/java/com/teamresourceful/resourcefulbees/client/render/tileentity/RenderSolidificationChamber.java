package com.teamresourceful.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.common.blockentity.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.utils.RenderCuboid;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RenderSolidificationChamber implements BlockEntityRenderer<SolidificationChamberBlockEntity> {

    public RenderSolidificationChamber(BlockEntityRendererProvider.Context renderer) {}

    @Override
    public void render(SolidificationChamberBlockEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidStack stack = tile.getTank().getFluid();
        if (!stack.isEmpty()) {
            float percentage = tile.getTank().getFluidAmount() / (float)tile.getTank().getCapacity();
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(stack.getFluid());
            int color = props.getTintColor(stack);
            ResourceLocation stillTexture = props.getStillTexture(stack);
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            Vec3 start = new Vec3(0.188, 0.3125, 0.188);
            Vec3 end = new Vec3(0.812, 0.3125 + percentage * 0.687, 0.812);
            RenderCuboid.renderCube(start, end, stillTexture, matrix, builder, color, light, overlayLight);
        }
    }
}
