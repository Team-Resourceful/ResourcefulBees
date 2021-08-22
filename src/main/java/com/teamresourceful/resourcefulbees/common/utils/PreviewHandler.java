package com.teamresourceful.resourcefulbees.common.utils;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamresourceful.resourcefulbees.common.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.BlockAccessor;
import com.teamresourceful.resourcefulbees.common.registry.ModBlocks;
import com.teamresourceful.resourcefulbees.common.tileentity.multiblocks.apiary.ApiaryTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
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

    public static void setPreview(BlockPos apiary, MutableBoundingBox box, boolean enabled){
        STRUCTURE_PREVIEW_POS.clear();
        if (enabled) {
            PreviewHandler.apiaryPos = apiary;
            BlockPos.betweenClosedStream(box).forEach((blockPos -> {
                if (   (blockPos.getX() == box.x0 || blockPos.getX() == box.x1 ||
                        blockPos.getY() == box.y0 || blockPos.getY() == box.y1 ||
                        blockPos.getZ() == box.z0 || blockPos.getZ() == box.z1  ) && !apiary.equals(blockPos.immutable())) {

                    BlockPos savedPos = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                    STRUCTURE_PREVIEW_POS.add(savedPos);
                }
            }));
        }
        else apiaryPos = null;
        PreviewHandler.enabled = enabled;
    }

    public static void onWorldRenderLast(RenderWorldLastEvent event) {
        World world = Minecraft.getInstance().level;
        MatrixStack ms = event.getMatrixStack();

        IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder buffer = buffers.getBuffer(RenderType.translucent());

        if (apiaryPos != null && enabled) {
            if (world != null && world.getBlockState(apiaryPos).getBlock() instanceof ApiaryBlock) {
                if (world.getBlockEntity(apiaryPos) instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTE = (ApiaryTileEntity) world.getBlockEntity(apiaryPos);
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

    private static void renderBlockAt(MatrixStack ms, IVertexBuilder buffer, BlockState state, BlockPos pos) {
        double renderPosX = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition().x();
        double renderPosY = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition().y();
        double renderPosZ = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition().z();

        ms.pushPose();
        ms.translate(-renderPosX, -renderPosY, -renderPosZ);

        BlockRendererDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        ms.translate(pos.getX(), pos.getY(), pos.getZ());
        IBakedModel model = brd.getBlockModel(state);
        int color = Minecraft.getInstance().getBlockColors().getColor(state, null, null, 0);
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        brd.getModelRenderer().renderModel(ms.last(), buffer, state, model, r, g, b, 16777215, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);

        ms.popPose();
    }
}
