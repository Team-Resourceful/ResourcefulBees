package com.teamresourceful.resourcefulbees.common.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BlockAccessor;
import com.teamresourceful.resourcefulbees.common.registry.minecraft.ModBlocks;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.ArrayList;
import java.util.List;


//TODO clean this class up badly.... -oreo
public class PreviewHandler {
    private static BlockPos apiaryPos = null;
    private static final List<BlockPos> STRUCTURE_PREVIEW_POS = new ArrayList<>();
    private static boolean enabled;

    private PreviewHandler() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void setPreview(BlockPos apiary, BoundingBox box, boolean enabled){
        STRUCTURE_PREVIEW_POS.clear();
        if (enabled) {
            PreviewHandler.apiaryPos = apiary;
            BlockPos.betweenClosedStream(box).forEach((blockPos -> {
                if (   (blockPos.getX() == box.minX() || blockPos.getX() == box.maxX() ||
                        blockPos.getY() == box.minY() || blockPos.getY() == box.maxY() ||
                        blockPos.getZ() == box.minZ() || blockPos.getZ() == box.maxZ()  ) && !apiary.equals(blockPos.immutable())) {

                    BlockPos savedPos = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    STRUCTURE_PREVIEW_POS.add(savedPos);
                }
            }));
        }
        else apiaryPos = null;
        PreviewHandler.enabled = enabled;
    }

    public static void onWorldRenderLast(RenderLevelLastEvent event) {
        Level world = Minecraft.getInstance().level;
        PoseStack ms = event.getPoseStack();

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(RenderType.translucent());

        if (apiaryPos != null && enabled) {
            if (world != null && world.getBlockState(apiaryPos).getBlock() instanceof ApiaryBlock) {
                if (world.getBlockEntity(apiaryPos) instanceof ApiaryTileEntity apiaryTE) {
                    if (apiaryTE != null && apiaryTE.isPreviewed())
                        for (BlockPos pos : STRUCTURE_PREVIEW_POS) {
                            if (world.getBlockState(pos).equals(Blocks.AIR.defaultBlockState()))
                                renderBlockAt(ms, buffer, ModBlocks.PREVIEW_BLOCK.get().defaultBlockState(), pos);
                            else {
                                if (((BlockAccessor) world.getBlockState(pos).getBlock()).getHasCollision()) {
                                    renderBlockAt(ms, buffer, ModBlocks.ERRORED_PREVIEW_BLOCK.get().defaultBlockState(), pos);
                                }
                            }
                        }
                }
            } else {
                STRUCTURE_PREVIEW_POS.clear();
                apiaryPos = null;
                PreviewHandler.enabled = false;
            }
        }

        buffers.endBatch(RenderType.translucent());
    }

    private static void renderBlockAt(PoseStack ms, VertexConsumer buffer, BlockState state, BlockPos pos) {
        double renderPosX = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition().x();
        double renderPosY = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition().y();
        double renderPosZ = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition().z();

        ms.pushPose();
        ms.translate(-renderPosX, -renderPosY, -renderPosZ);

        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        ms.translate(pos.getX(), pos.getY(), pos.getZ());
        BakedModel model = brd.getBlockModel(state);
        int color = Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        brd.getModelRenderer().renderModel(ms.last(), buffer, state, model, r, g, b, 16777215, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);

        ms.popPose();
    }
}
