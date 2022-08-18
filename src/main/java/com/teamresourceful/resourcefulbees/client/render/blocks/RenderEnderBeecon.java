package com.teamresourceful.resourcefulbees.client.render.blocks;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.client.utils.RenderCuboid;
import com.teamresourceful.resourcefulbees.common.block.EnderBeecon;
import com.teamresourceful.resourcefulbees.common.blockentity.EnderBeeconBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RenderEnderBeecon implements BlockEntityRenderer<EnderBeeconBlockEntity> {

    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    public RenderEnderBeecon(BlockEntityRendererProvider.Context renderer) {}

    @Override
    public void render(EnderBeeconBlockEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidStack stack = tile.getTank().getFluid();
        long gameTime = tile.getLevel().getGameTime();

        if (!stack.isEmpty()) {

            BlockState state = tile.getBlockState();
            boolean showBeam = state.hasProperty(EnderBeecon.BEAM) && state.getValue(EnderBeecon.BEAM);

            // render tank
            float percentage = tile.getTank().getFluidAmount() / (float)tile.getTank().getCapacity();
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(stack.getFluid());
            int color = props.getTintColor(stack);
            ResourceLocation stillTexture = props.getStillTexture(stack);
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            AABB box = new AABB(0.26, 0.25, 0.26, 0.74, 0.25 + percentage * 0.375, 0.74);
            RenderCuboid.renderCube(box, stillTexture, matrix, builder, color, light, overlayLight);
            // render beam
            if (!showBeam) return;
            float red = (color >> 16 & 255) / 255f;
            float green = (color >> 8 & 255) / 255f;
            float blue = (color & 255) / 255f;
            float alpha = (color >> 24 & 255) / 255f;
            float[] afloats = {red, green, blue, alpha};
            BeaconRenderer.renderBeaconBeam(matrix, renderer, TEXTURE_BEACON_BEAM, partialTick, 1.0F, gameTime, 0, 1024, afloats, 0.2F, 0.25F);
        }
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull EnderBeeconBlockEntity tile) {
        return true;
    }
}
