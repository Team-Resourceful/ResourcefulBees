package com.teamresourceful.resourcefulbees.client.rendering.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.client.util.RenderCuboid;
import com.teamresourceful.resourcefulbees.common.blockentities.HoneyGeneratorBlockEntity;
import com.teamresourceful.resourcefulbees.common.blocks.HoneyGeneratorBlock;
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
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class HoneyGenRenderer implements BlockEntityRenderer<HoneyGeneratorBlockEntity, HoneyGenRenderer.RenderState> {

    public HoneyGenRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public @NonNull RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(@NonNull HoneyGeneratorBlockEntity tile, @NonNull RenderState state, float partialTick, @NonNull Vec3 cameraPosition, @Nullable ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(tile, state, breakProgress);
        state.facing = tile.getBlockState().getValue(HoneyGeneratorBlock.FACING);


        TankData tankData = tile.tankData();
        FluidStack fluid = tankData.fluid();

        state.hasFluid = !fluid.isEmpty();

        if (!state.hasFluid) {
            state.fluidSprite = null;
            state.fluidHeight = 0;
            return;
        }

        int capacity = tankData.capacity();

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

        AABB box = createFluidBox(state.facing, state.fluidHeight);

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

    private static AABB createFluidBox(Direction facing, float fluidHeight) {
        // Glass tank bounds in the NORTH-facing model:
        // x: 10 -> 16
        // y:  5 -> 13
        // z:  0 -> 12
        //
        // Inset by half a model pixel to prevent z-fighting with the glass.
        double inset = 0.5 / 16.0;

        double minX = 10.0 / 16.0 + inset;
        double minY =  5.0 / 16.0 + inset;
        double minZ =  0.0 / 16.0 + inset;

        double maxX = 1.0 - inset;
        double maxY = 13.0 / 16.0 - inset;
        double maxZ = 12.0 / 16.0 - inset;

        double fluidY = minY + (maxY - minY) * fluidHeight;

        return switch (facing) {
            case NORTH -> new AABB(
                    minX, minY, minZ,
                    maxX, fluidY, maxZ
            );

            case EAST -> new AABB(
                    1.0 - maxZ, minY, minX,
                    1.0 - minZ, fluidY, maxX
            );

            case SOUTH -> new AABB(
                    1.0 - maxX, minY, 1.0 - maxZ,
                    1.0 - minX, fluidY, 1.0 - minZ
            );

            case WEST -> new AABB(
                    minZ, minY, 1.0 - maxX,
                    maxZ, fluidY, 1.0 - minX
            );

            default -> throw new IllegalStateException(
                    "Unexpected honey generator facing: " + facing
            );
        };
    }

    public static class RenderState extends BlockEntityRenderState {

        public boolean hasFluid;
        public float fluidHeight;

        public int fluidColor = 0xFFFFFFFF;
        public Direction facing = Direction.NORTH;

        @Nullable
        public TextureAtlasSprite fluidSprite;
    }
}