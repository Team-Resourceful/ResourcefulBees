package com.teamresourceful.resourcefulbees.utils;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.teamresourceful.resourcefulbees.block.multiblocks.apiary.ApiaryBlock;
import com.teamresourceful.resourcefulbees.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.registry.ModBlocks;
import com.teamresourceful.resourcefulbees.tileentity.multiblocks.apiary.ApiaryTileEntity;
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
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.ArrayList;
import java.util.List;

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
        Level world = Minecraft.getInstance().level;
        PoseStack ms = event.getMatrixStack();

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(RenderType.translucent());

        if (apiaryPos != null && enabled) {
            if (world != null && world.getBlockState(apiaryPos).getBlock() instanceof ApiaryBlock) {
                if (world.getBlockEntity(apiaryPos) instanceof ApiaryTileEntity) {
                    ApiaryTileEntity apiaryTE = (ApiaryTileEntity) world.getBlockEntity(apiaryPos);
                    if (apiaryTE != null && apiaryTE.isPreviewed())
                        for (BlockPos pos : STRUCTURE_PREVIEW_POS) {
                            if (world.getBlockState(pos).equals(Blocks.AIR.defaultBlockState()))
                                renderBlockAt(ms, buffer, ModBlocks.PREVIEW_BLOCK.get().defaultBlockState(), pos);
                            else {
                                if (BeeInfoUtils.getBlockTag("resourcefulbees:valid_apiary") != null
                                        && !world.getBlockState(pos).is(BeeInfoUtils.getBlockTag("resourcefulbees:valid_apiary"))) {
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
        float r = (float) (color >> 16 & 255) / 255.0F;
        float g = (float) (color >> 8 & 255) / 255.0F;
        float b = (float) (color & 255) / 255.0F;
        brd.getModelRenderer().renderModel(ms.last(), buffer, state, model, r, g, b, 16777215, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);

        ms.popPose();
    }
}
