package com.teamresourceful.resourcefulbees.client.rendering.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.util.RenderCuboid;
import com.teamresourceful.resourcefulbees.common.blockentities.EnderBeeconBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.EnderBeeconBlock;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class EnderBeeconRenderer implements BlockEntityRenderer<EnderBeeconBlockEntity, EnderBeeconRenderer.RenderState> {

    public EnderBeeconRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public @NonNull RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(@NonNull EnderBeeconBlockEntity beecon, @NonNull RenderState renderState, float partialTicks, @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        renderState.reset();

        BlockEntityRenderState.extractBase(beecon, renderState, breakProgress);
        FluidStack fluid = beecon.clientFluid();
        renderState.hasFluid = !fluid.isEmpty();

        if (!renderState.hasFluid) {
            renderState.showBeam = false;
            return;
        }

        renderState.fluidHeight = fluid.amount() / 16_000F;

        BlockState blockState = beecon.getBlockState();

        renderState.showBeam = blockState.hasProperty(EnderBeeconBlock.BEAM) && blockState.getValue(EnderBeeconBlock.BEAM);
        renderState.gameTime = beecon.getLevel() != null
                ? beecon.getLevel().getGameTime() + partialTicks
                : 0.0F;

        if (fluid.getFluid() instanceof CustomHoneyFluid.Still customHoney) {
            renderState.fluidColor = customHoney.getHoneyFluidData().renderData().color().getOpaqueValue();
        }

        FluidModel fluidModel = Minecraft.getInstance()
                .getModelManager()
                .getFluidStateModelSet()
                .get(fluid.getFluid().defaultFluidState());

        try (TextureAtlasSprite sprite = fluidModel
                .stillMaterial()
                .sprite()) {
            renderState.fluidSprite = sprite;
        }
    }

    @Override
    public void submit(RenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        if (!state.hasFluid) {
            return;
        }

        submitFluid(state, poseStack, collector);

        if (state.showBeam) {
            submitBeam(state, poseStack, collector);
        }
    }

    private static void submitBeam(RenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        float rotation = (state.gameTime % 40L) * 2.25F;
        BeaconRenderer.submitBeaconBeam(poseStack, collector, BeaconRenderer.BEAM_LOCATION, 1.0F, rotation, 0, BeaconRenderer.MAX_RENDER_Y, state.fluidColor, 0.2F, 0.25F);
    }

    private static void submitFluid(RenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        if (state.fluidSprite == null) {
            return;
        }

        AABB box = new AABB(
                0.26,
                0.25,
                0.26,
                0.74,
                0.25 + state.fluidHeight * 0.375,
                0.74
        );

        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.translucentMovingBlock(),
                (pose, consumer) -> RenderCuboid.renderCube(
                        box,
                        state.fluidSprite,
                        pose,
                        consumer,
                        state.fluidColor,
                        state.lightCoords,
                        OverlayTexture.NO_OVERLAY
                )
        );
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public static class RenderState extends BlockEntityRenderState {

        public boolean hasFluid;
        public boolean showBeam;

        public float fluidHeight;

        public int fluidColor = 0xFFFFFFFF;

        public float gameTime;

        @Nullable
        public TextureAtlasSprite fluidSprite;

        protected void reset() {
            hasFluid = false;
            showBeam = false;
            fluidHeight = 0.0F;
            fluidColor = 0xFFFFFFFF;
            fluidSprite = null;
            gameTime = 0L;
        }
    }
}