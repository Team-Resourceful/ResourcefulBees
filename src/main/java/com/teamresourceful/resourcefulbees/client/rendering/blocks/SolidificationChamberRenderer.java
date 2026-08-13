package com.teamresourceful.resourcefulbees.client.rendering.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.util.RenderCuboid;
import com.teamresourceful.resourcefulbees.common.blockentities.SolidificationChamberBlockEntity;
import com.teamresourceful.resourcefulbees.common.fluids.CustomHoneyFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class SolidificationChamberRenderer implements BlockEntityRenderer<
        SolidificationChamberBlockEntity,
        SolidificationChamberRenderer.RenderState
        > {

    private static final float TANK_CAPACITY = 64_000F;

    public SolidificationChamberRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NonNull RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(
            @NonNull SolidificationChamberBlockEntity chamber,
            @NonNull RenderState renderState,
            float partialTicks,
            @NonNull Vec3 cameraPosition,
            @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderState.extractBase(
                chamber,
                renderState,
                breakProgress
        );

        renderState.hasFluid = false;
        renderState.fluidHeight = 0.0F;
        renderState.fluidColor = 0xFFFFFFFF;
        renderState.fluidSprite = null;

        FluidStack fluid = chamber.fluidStack();

        if (fluid.isEmpty()) {
            return;
        }

        renderState.hasFluid = true;
        renderState.fluidHeight = fluid.amount() / TANK_CAPACITY;

        if (fluid.getFluid() instanceof CustomHoneyFluid.Still customHoney) {
            renderState.fluidColor = customHoney
                    .getHoneyFluidData()
                    .renderData()
                    .color()
                    .getOpaqueValue();
        }

        FluidModel fluidModel = Minecraft.getInstance()
                .getModelManager()
                .getFluidStateModelSet()
                .get(fluid.getFluid().defaultFluidState());

        renderState.fluidSprite = fluidModel
                .stillMaterial()
                .sprite();
    }

    @Override
    public void submit(
            RenderState state,
            @NonNull PoseStack poseStack,
            @NonNull SubmitNodeCollector collector,
            @NonNull CameraRenderState camera
    ) {
        if (!state.hasFluid || state.fluidSprite == null) {
            return;
        }

        AABB box = new AABB(
                0.188,
                0.3125,
                0.188,
                0.812,
                0.3125 + state.fluidHeight * 0.687,
                0.812
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

    public static class RenderState extends BlockEntityRenderState {

        public boolean hasFluid;
        public float fluidHeight;
        public int fluidColor = 0xFFFFFFFF;

        @Nullable
        public TextureAtlasSprite fluidSprite;
    }
}