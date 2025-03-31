package com.teamresourceful.resourcefulbees.client.rendering.blocks;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.client.util.RenderCuboid;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.ClientFluidHooks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class EnderBeeconRenderer implements BlockEntityRenderer<EnderBeeconBlockEntity> {

    public static final ResourceLocation TEXTURE_BEACON_BEAM = new ResourceLocation("textures/entity/beacon_beam.png");

    public EnderBeeconRenderer(BlockEntityRendererProvider.Context renderer) {}

    @Override
    public void render(EnderBeeconBlockEntity tile, float partialTick, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidHolder holder = tile.getFluid();
        long gameTime = tile.getLevel().getGameTime();

        if (!holder.isEmpty()) {

            BlockState state = tile.getBlockState();
            boolean showBeam = state.hasProperty(EnderBeeconBlock.BEAM) && state.getValue(EnderBeeconBlock.BEAM);

            // render tank
            float percentage = tile.getFluid().getFluidAmount() / (float)tile.getFluidContainer().getTankCapacity(0);
            int color = ClientFluidHooks.getFluidColor(holder);
            VertexConsumer builder = renderer.getBuffer(Sheets.translucentCullBlockSheet());
            AABB box = new AABB(0.26, 0.25, 0.26, 0.74, 0.25 + percentage * 0.375, 0.74);
            RenderCuboid.renderCube(box, ClientFluidHooks.getFluidSprite(holder), matrix, builder, color, light, overlayLight);
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
