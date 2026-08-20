package com.teamresourceful.resourcefulbees.client.rendering.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.util.RenderCuboid;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.components.TankData;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class HoneyGenRenderer implements BlockEntityRenderer<HoneyGeneratorBlockEntity, HoneyGenRenderer.RenderState> {

    public HoneyGenRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public @NonNull RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(@NonNull HoneyGeneratorBlockEntity tile, @NonNull RenderState state, float partialTick, @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(tile, state, breakProgress);

        List<TankData> tankData = tile.tankData();
        FluidStack fluid = tankData.getFirst().fluid();

        state.hasFluid = !fluid.isEmpty();

        if (!state.hasFluid) {
            state.fluidSprite = null;
            state.fluidHeight = 0;
            return;
        }

        int capacity = tankData.getFirst().capacity();

        state.fluidHeight = capacity > 0
                ? Math.clamp(fluid.amount() / (float) capacity, 0.0F, 1.0F)
                : 0.0F;

        state.fluidColor = 0xFFFFFFFF;

        if (fluid.getFluid() instanceof CustomHoneyFluid.Still customHoney) {
            state.fluidColor = customHoney
                    .getHoneyFluidData()
                    .renderData()
                    .color()
                    .getOpaqueValue();
        }

        FluidModel fluidModel = Minecraft.getInstance()
                .getModelManager()
                .getFluidStateModelSet()
                .get(fluid.getFluid().defaultFluidState());

        state.fluidSprite = fluidModel
                .stillMaterial()
                .sprite();
    }

    @Override
    public void submit(RenderState state, @NonNull PoseStack poseStack, @NonNull SubmitNodeCollector collector, @NonNull CameraRenderState camera) {
        if (!state.hasFluid || state.fluidSprite == null) {
            return;
        }

        AABB box = new AABB(
                0.0625,
                0.0625,
                0.0625,

                0.9375,
                0.0625 + state.fluidHeight * 0.875,
                0.9375
        );

        RenderType renderType = RenderTypes.translucentMovingBlock();

        collector.submitCustomGeometry(
                poseStack,
                renderType,
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

    public static class RenderState extends BlockEntityRenderState {

        public boolean hasFluid;
        public float fluidHeight;

        public int fluidColor = 0xFFFFFFFF;

        @Nullable
        public TextureAtlasSprite fluidSprite;
    }
}